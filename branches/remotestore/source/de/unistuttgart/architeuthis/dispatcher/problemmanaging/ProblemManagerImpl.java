/*
 * file:        ProblemManagerImpl.java
 * created:     29.06.2003
 * last change: 06.04.2005 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@t-online.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *              Michael Wohlfart,  michael.wohlfart@zsw-bw.de
 *
 *
 * This file is part of Architeuthis.
 *
 * Architeuthis is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Architeuthis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Architeuthis; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.dispatcher.problemmanaging;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.misc.util.BlockingBuffer;
import de.unistuttgart.architeuthis.dispatcher.computemanaging.ComputeManagerImpl;
import de.unistuttgart.architeuthis.dispatcher.statistic.SystemStatisticsCollector;
import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemTransmitter;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Der <code>ProblemManager</code> ermöglicht es mehere Probleme gleichzeitig
 * mit dem {@link ComputeManager} zu berechnen.
 *
 * @author Jürgen Heit, Andreas Heydlauff, Dietmar Lippold
 */
public class ProblemManagerImpl extends UnicastRemoteObject implements ProblemManager {

    /**
     * Die Größe des Puffers, in dem die erzeugten Teilprobleme aller Probleme
     * gespeichert werden.
     */
    private static final int BLOCKING_BUFFER_SIZE = 1;

    /**
     * Die maximale Anzahl der Versuche, eine Lösung an den Problem-Transmitter
     * zu senden.
     */
    private static final long SEND_SOLUTION_MAX_TRY = 3;

    /**
     * Wartezeit zwischen den Sende-Versuchen an einen Problem-Transmitter.
     */
    private static final long SEND_SOLUTION_TIMEOUT = 300;

    /**
     * Referenz auf den Compute-Manager.
     */
    private ComputeManagerImpl cmpManager = null;

    /**
     * Hashtabelle, in der jedem <code>ProblemWrapper</code> eines Problems der
     * zugehörige <code>ProblemTransmitter</code> zugeordnet wird.
     */
    private Map probWrapTransmitter =
        Collections.synchronizedMap(new HashMap());

    /**
     * Puffer für die Teilproblem-Wrapper aller Probleme.
     */
    private BlockingBuffer parProbWrapperBuffer;

    /**
     * Thread, der die Teilproblem-Wrapper aller Probleme einsammelt.
     */
    private PartialProblemCollector partialProblemCollectorThread;

    /**
     * Statistik über den Dispatcher
     */
    private SystemStatisticsCollector systemStatistic;

    /**
     * {@link java.util.logging.Logger} eingestellt auf
     * de.unistuttgart.architeuthis.dispatcher
     */
    private Logger log =
        Logger.getLogger("de.unistuttgart.architeuthis.dispatcher");

    /**
     * Gibt an, ob der Dienst dieses Objekts beendet wurde.
     */
    private boolean terminated = false;

    /**
     * Dieser Constructor wird vom {@link ComputeManagerImpl} aufgerufen.
     *
     * @param computeManager    Referenz auf aufrufenden ComputeManager.
     * @param sysStatCollector  Statistik Collector.
     *
     * @throws RemoteException  Falls es zu Fehlern bei der RMI-Kommunikation
     *                          kommt.
     */
    public ProblemManagerImpl(ComputeManagerImpl computeManager,
                              SystemStatisticsCollector sysStatCollector)
        throws RemoteException {

        cmpManager = computeManager;
        systemStatistic = sysStatCollector;
        parProbWrapperBuffer = new BlockingBuffer(BLOCKING_BUFFER_SIZE);
        partialProblemCollectorThread =
            new PartialProblemCollector(this, parProbWrapperBuffer);
    }

    /**
     * Liefert einn Teilproblem-Wrapper ({@link ParProbWrapper}) zurück, falls
     * es ein {@link Problem} in der Problem-Verwaltungs-Collection gibt,
     * welches noch Teilproblem-Wrapper hat.
     * Falls kein Problem aktuell ein Teilproblem verfügbar hat, wird
     * <code>null</code> zurückgeliefert. Wenn mindestens ein Problem ein
     * Teilproblem-Wrapper verfügbar hat, wird der Teilproblem-Wrapper von dem
     * Problem geliefert, von dem die geschätzte gesamte Berechnungszeit der
     * Teilprobleme, die vom <code>ProblemWrapper</code> ausgeliefert aber noch
     * nicht berechnet wurden, am geringsten ist. Wenn es mehrere Probleme gibt,
     * von denen derzeit kein Teilproblem ausgeliefert ist, deren geschätzte
     * Berechnungszeit also einheitlich Null ist, wird das Problem mit dem
     * geringsten Verhältnis von bisheriger Berechnungsdauer zu Existenzdauer
     * gewählt.
     *
     * @return  Teilproblem-Wrapper ({@link ParProbWrapper}) des Problems mit
     *          der geschätzten geringsten Berechnungszeit der in Bearbeitung
     *          befindlichen Teilprobleme, oder <code>null</code>, wenn kein
     *          Teilproblem-Wrapper verfügbar ist.
     */
    ParProbWrapper collectParProbWrapper() {
        ProblemWrapper nextProbWrap;
        ProblemWrapper minTimeProbWrap = null;
        ParProbWrapper parProbWrap;
        Iterator       probWrapIter;
        float          nextCompRatio = 0;
        float          minCompRatio = 0;
        long           nextTime = 0;
        long           minTime = 0;

        do {
            parProbWrap = null;
            synchronized (probWrapTransmitter) {
                probWrapIter = probWrapTransmitter.keySet().iterator();
                while (probWrapIter.hasNext()) {
                    nextProbWrap = (ProblemWrapper) probWrapIter.next();
                    if (nextProbWrap.newParProbWrapperAvailable()) {
                        nextTime = nextProbWrap.estimatedComputationTime();
                        if ((minTimeProbWrap == null) || (nextTime < minTime)) {
                            minTimeProbWrap = nextProbWrap;
                            minTime = nextTime;
                            if (minTime == 0) {
                                minCompRatio = minTimeProbWrap.computationRatio();
                            }
                        } else if (nextTime == 0) {
                            // Es ist auch minTime == 0
                            nextCompRatio = nextProbWrap.computationRatio();
                            if (nextCompRatio < minCompRatio) {
                                minTimeProbWrap = nextProbWrap;
                                minCompRatio = nextCompRatio;
                            }
                        }
                    }
                }
            }

            if (minTimeProbWrap != null) {
                synchronized (minTimeProbWrap) {
                    parProbWrap = minTimeProbWrap.getNewParProbWrapper();
                }
            }
        } while ((minTimeProbWrap != null) && (parProbWrap == null));
        return parProbWrap;
    }

    /**
     * Löscht die Werte <code>null</code> aus dem Teilproblem-Puffer.
     */
    void removeNullElements() {
        parProbWrapperBuffer.removeNullElements();
    }

    /**
     * Reaktiviert eventuell vorhandene passive Operatives.
     */
    void reactivatePassiveOperatives() {
        // Null-Objekte aus Teilproblem-Puffer entfernen
        parProbWrapperBuffer.removeNullElements();
        new Thread() {
            public void run() {
                log.info("Start vom Thread reactivatePassiveOperatives()");
                cmpManager.reactivatePassiveOperatives();
            }
        }
        .start();
    }

    /**
     * Bricht die Berechnungen zu einer Liste von Teilproblemen ab.
     *
     * @param parProbWrapper  eine neu erzeugte Liste der Wrapper der
     *                        abzubrechenden Teilprobleme.
     */
    private void abortAllPartialProblems(List parProbWrapper) {
        LinkedList computingParProbWrapper = new LinkedList();
        Iterator abortIter;

        // Die Partial-Problem-Wrapper aus dem Puffer löschen
        abortIter = parProbWrapper.iterator();
        while (abortIter.hasNext()) {
            ParProbWrapper parProbWrap = (ParProbWrapper) abortIter.next();
            if (!parProbWrapperBuffer.remove(parProbWrap)) {
                computingParProbWrapper.add(parProbWrap);
            }
        }

        // Die Berechnung der Teilprobleme vom ComputeManager abbrechen lassen
        abortIter = computingParProbWrapper.iterator();
        while (abortIter.hasNext()) {
            ParProbWrapper parProbWrap = (ParProbWrapper) abortIter.next();
            cmpManager.abortComputation(parProbWrap);
        }
    }

    /**
     * Entfernt ein Problem, das abgebrochen wurde oder zu dem eine
     * Gesamtlösung ermittelt wurde, aus der Verwaltung.
     *
     * @param probWrapper  ProblemWrapper vom zu entfernenden Problem.
     *
     * @return  Den <code>ProblemTransmitter</code>, der das Problem, das
     *          entfernt wurde, übertragen hat.
     */
    ProblemTransmitter removeProblem(ProblemWrapper probWrapper) {
        ProblemTransmitter removedTransmitter = null;
        List parProbWrapper = null;

        synchronized (probWrapper) {
            // ProblemWrapper aus der Liste der verwalteten Probleme entfernen
            removedTransmitter =
                (ProblemTransmitter) probWrapTransmitter.remove(probWrapper);

            if (removedTransmitter != null) {
                // Die Liste der Wrapper der abzubrechenden Teilprobleme holen
                parProbWrapper = probWrapper.getAllDeliveredParProbWrapper();

                // Die Beendigung vom Thread vom ProblemWrapper veranlassen
                probWrapper.terminate();
            }
        }

        if (removedTransmitter != null) {
            log.info("Breche für " + probWrapper.toString()
                     + " alle Teilprobleme ab");

            // Alle noch in Berechnung befindlichen Teilprobleme abbrechen.
            abortAllPartialProblems(parProbWrapper);

            // Nach Abbruch aller Teilprobleme den ClassLoader des Problems
            // freigeben
            probWrapper.unblockClassLoader();
        }

        return removedTransmitter;
    }

    /**
     * Bricht die Bearbeitung aller Probleme ab.
     */
    private synchronized void abortAllProblems() {
        LinkedList problemWrappers;
        ProblemWrapper probWrap;

        problemWrappers = new LinkedList(probWrapTransmitter.keySet());
        Iterator probWrapIter = problemWrappers.iterator();
        while (probWrapIter.hasNext()) {
            probWrap = (ProblemWrapper) probWrapIter.next();
            if (removeProblem(probWrap) != null) {
                systemStatistic.notifyProblemAborted();
            }
        }
    }

    /**
     * Liefert den nächsten Teilproblem-Wrapper ({@link ParProbWrapper}) aus
     * dem Puffer der zur Berechnung stehenden Teilprobleme.
     *
     * @return  Wrapper für das Teilproblem.
     */
    public ParProbWrapper getParProbWrapper() {
        ParProbWrapper partProbWrap =
            (ParProbWrapper) parProbWrapperBuffer.dequeue();
        return partProbWrap;
    }

    /**
     * Gibt die Teillösung an das Problem weiter.
     *
     * @param partSol       zurückzuliefernde Teillösung
     *                      ({@link PartialSolution})
     * @param partProbWrap  Wrapper des zur Teillösung gehörenden Teilproblems
     *                      ({@link ParProbWrapper})
     */
    public void collectPartialSolution(PartialSolution partSol,
                                       ParProbWrapper partProbWrap) {

        ProblemWrapper probWrap;

        probWrap = partProbWrap.getCreatingWrapper();
        // Testen, ob Problem noch existiert
        if (probWrap != null) {
            synchronized (probWrap) {
                if (probWrapTransmitter.containsKey(probWrap)) {
                    // Lösung an den ProblemWraper zurückgeben
                    probWrap.collectPartialSolution(partProbWrap, partSol);
                }
            }
        }
    }

    /**
     * Sendet eine Nachricht an einen Problem-übermittler.
     *
     * @param transmitter   Empfänger der Nachricht.
     * @param messageID     Code der Nachricht.
     * @param message       Nachrichtentext.
     */
    private void sendMessage(ProblemTransmitter transmitter,
                             int messageID,
                             String message) {

        try {
            log.info(
                    "Sende Nachricht \""
                    + String.valueOf(message)
                    + "\" an Problem-Übermittler");
            transmitter.fetchMessage(messageID, message);
        } catch (RemoteException e) {
            log.severe(
                    "<E> Nachricht konnte nicht an den Problem-Uebermittler "
                    + "geschickt werden");
        }
    }

    /**
     * Sendet eine Nachricht an alle ProblemTransmitter.
     *
     * @param messageID  Code der Nachricht.
     * @param message    Nachrichtentext.
     */
    private synchronized void sendMessageToAll(int messageID, String message) {
        ProblemWrapper probWrap;

        synchronized (probWrapTransmitter) {
            Iterator probWrapIter = probWrapTransmitter.keySet().iterator();
            while (probWrapIter.hasNext()) {
                probWrap = (ProblemWrapper) probWrapIter.next();
                sendMessage(
                        (ProblemTransmitter) probWrapTransmitter.get(probWrap),
                        messageID,
                        message);
            }
        }
    }

    /**
     * Ausnahmen werden über diese Methode an den Problem-Manager gemeldet.
     *
     * @param problemWrapper    Der problemWrapper, zu dem die Exception
     *                          aufgetreten ist.
     * @param parProbWrapper    Der parProbWrapper, zu dem die Exception
     *                          aufgetreten ist.
     * @param exceptionCode     Integerwert, der die Ausnahme charakterisisert
     * @param exceptionMessage  Fehlermeldung um die Ausnahme näher zu
     *                          beschreiben.
     */
    public void reportException(ProblemWrapper problemWrapper,
                                ParProbWrapper parProbWrapper,
                                int exceptionCode,
                                String exceptionMessage) {

        ProblemWrapper probWrap = null;
        ProblemTransmitter transmitter = null;

        if (problemWrapper != null) {
            probWrap = problemWrapper;
        } else {
            if (parProbWrapper != null) {
                probWrap = parProbWrapper.getCreatingWrapper();
            }
        }

        switch (exceptionCode) {
        case ExceptionCodes.PARTIALPROBLEM_CREATE_EXCEPTION :
            log.severe("Exception PARTIALPROBLEM_CREATE_EXCEPTION zu "
                    + parProbWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.PARTIALSOLUTION_COLLECT_EXCEPTION :
            log.severe("Exception PARTIALSOLUTION_COLLECT_EXCEPTION zu "
                    + parProbWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.SOLUTION_CREATE_EXCEPTION :
            log.severe("Exception SOLUTION_CREATE_EXCEPTION zu "
                    + problemWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.PARTIALPROBLEM_SEND_EXCEPTION :
            log.severe("Exception PARTIALPROBLEM_SEND_EXCEPTION zu "
                    + parProbWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.PARTIALSOLUTION_SEND_EXCEPTION :
            log.severe("Exception PARTIALSOLUTION_SEND_EXCEPTION zu "
                    + parProbWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.SOLUTION_SEND_EXCEPTION :
            log.severe("Exception SOLUTION_SEND_EXCEPTION zu "
                    + problemWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.PARTIALPROBLEM_COMPUTE_EXCEPTION :
            log.severe("Exception PARTIALPROBLEM_COMPUTE_EXCEPTION zu "
                    + parProbWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.PARTIALPROBLEM_ERROR :
            log.severe("Exception PARTIALPROBLEM_ERROR zu "
                    + parProbWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.PROBLEM_INCORRECT_ERROR :
            log.severe("Exception PROBLEM_INCORRECT_ERROR zu "
                    + problemWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.USER_ABORT_PROBLEM :
            log.severe("Exception USER_ABORT_PROBLEM zu "
                    + problemWrapper + " : " + exceptionMessage);
            if (probWrap != null) {
                transmitter = removeProblem(probWrap);
                if (transmitter != null) {
                    systemStatistic.notifyProblemAborted();
                    sendMessage(transmitter,
                            exceptionCode,
                            exceptionMessage);
                }
            }
            break;
        case ExceptionCodes.DISPATCHER_SHUTDOWN :
            synchronized (this) {
                terminated = true;
            }
            sendMessageToAll(
                ExceptionCodes.DISPATCHER_SHUTDOWN,
                "Administratives Beenden.");
            abortAllProblems();
            partialProblemCollectorThread.terminate();
            break;
        case ExceptionCodes.NO_OPERATIVES_REGISTERED :
            sendMessageToAll(
                ExceptionCodes.NO_OPERATIVES_REGISTERED,
                "Keine Operatives verfügbar.");
            break;
        case ExceptionCodes.NEW_OPERATIVES_REGISTERED :
            sendMessageToAll(
                    ExceptionCodes.NEW_OPERATIVES_REGISTERED,
            "Neuer Operative hat sich angemeldet.");
            break;
        default :
            log.warning(
                    "Unbekannte Fehlermeldung: "
                    + exceptionCode
                    + " "
                    + exceptionMessage);
        break;
        }
    }

    /**
     * Sendet die Gesamtlösung an den Problem-Transmitter und aktualisiert die
     * Statistik bei erfolgreicher Übertragung.
     *
     * @param problemWrapper  Der ProblemWrapper, zu dessen Problem die
     *                        Lösung verschickt werden soll.
     * @param solution        Die zu verschickende Gesamtlösung.
     */
    void sendSolution(ProblemWrapper problemWrapper, Serializable solution) {
        ProblemTransmitter transmitter;
        String exceptionMessage = null;
        long tries = 0;
        boolean sent = false;

        synchronized (problemWrapper) {
            transmitter = (ProblemTransmitter) probWrapTransmitter.get(problemWrapper);

            // mehrere Versuche unternehmen
            while ((!sent) && (tries < SEND_SOLUTION_MAX_TRY)) {
                tries++;
                try {
                    // Gesamtlösung übermitteln
                    transmitter.fetchSolution(
                            solution,
                            problemWrapper.getStatistics());
                    sent = true;
                    log.info("Lösung erfolgreich an Benutzer gesendet.");
                } catch (RemoteException e) {
                    exceptionMessage = e.toString();
                    log.severe(
                            "<E> Problem-Übermittler nicht ereichbar für"
                            + " Lösungsrückgabe.");
                } catch (RuntimeException e) {
                    exceptionMessage = e.toString();
                    log.fine("Senden der Lösung fehlgeschlagen");
                    tries = SEND_SOLUTION_MAX_TRY;
                }

                if ((!sent) && (tries < SEND_SOLUTION_MAX_TRY)) {
                    try {
                        Thread.sleep(SEND_SOLUTION_TIMEOUT);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        if (sent) {
            systemStatistic.notifySolutionTransfered();
        } else {
            reportException(
                    problemWrapper,
                    null,
                    ExceptionCodes.SOLUTION_SEND_EXCEPTION,
                    exceptionMessage);
            log.warning("Lösung konnte nicht zurückgeliefert werden.");
        }
    }

    /**
     * Liefert den ProblemWrapper des angegebenen
     * <code>ProblemTransmitter</code>.
     *
     * @param transmitter  ProblemTransmitter, zu dem der ProblemWrapper
     *                     ermittelt werden soll.
     *
     * @return  ProblemWrapper des Problem-Übermittlers, der das Teilproblem
     *          übermittelt hat, oder <code>null</code>, wenn dieser nicht
     *          gespeichert ist.
     */
    private ProblemWrapper problemWrapper(ProblemTransmitter transmitter) {
        synchronized (probWrapTransmitter) {
            Iterator probWrapIter = probWrapTransmitter.keySet().iterator();
            ProblemWrapper probWrap;
            while (probWrapIter.hasNext()) {
                probWrap = (ProblemWrapper) probWrapIter.next();
                if (probWrapTransmitter.get(probWrap).equals(transmitter)) {
                    return probWrap;
                }
            }
        }
        return null;
    }

    /**
     * Bricht die Berechnung eines Problems durch dessen Benutzer ab.
     *
     * @param transmitter  Problem-Übermittler, der das Problem übertragen hat.
     *
     * @throws RemoteException bei RMI-Verbingungsproblemen.
     */
    public void abortProblemByUser(ProblemTransmitter transmitter)
        throws RemoteException {

        if (transmitter == null) {
            return;
        } else {
            ProblemWrapper probWrap = problemWrapper(transmitter);
            if (probWrap != null) {
                reportException(
                    probWrap,
                    null,
                    ExceptionCodes.USER_ABORT_PROBLEM,
                    "Abbruch durch Benutzer");
            }
        }
    }

    /**
     * Ermittelt, ob genug Resssourcen für die Annahme eines neuen Problems
     * vorhanden sind. Derzeit ist dies genau dann der Fall, wenn mindestens
     * ein Operative angemeldet ist.
     *
     * @return  <code>true</code>, wenn genug Resssourcen für die Annahme
     *          eines neuen Problems vorhanden sind, anderenfalls
     *          <code>false</code>.
     */
    private boolean computationResourcesLeft() {
        return (systemStatistic.getSnapshot().getRegisteredOperatives() > 0);
    }

    /**
     * Teilt dem ProblemManager die Position der Quelldateien für ein neues
     * Problem mit. Die nötigten Klassen werden von einem HTTP-Server geladen und
     * das {@link Problem} wird initialisiert.<p>
     *
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}s
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * Lösung dem Problem zum Zusammenfügen übergeben.<br>
     * Wurden alle PartialProbleme berechnet, gibt <code>newProblem</code> die
     * Gesamtlösung an den Problem-Übermittler zurück.
     *
     * @param transmitter  Problem-Übermittler, der das Problem sendet und die
     *                     Lösung empfangen soll
     * @param url          Pfad zu den Quelldateien auf einem HTTP-Server
     * @param className    {@link Problem}-spezifischer Name, das die Berechnung
     *                     startet
     * @param problemParameters  Die formalen Parameter des vom Problem zu
     *                           startenden Konstruktors.
     * @param generator der remoteStore Generator
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     *
     * @see  Problem
     * @see  PartialProblem
     * @throws RemoteException          Falls ein Netzproblemen aufgetreten ist
     *                                  oder der Dispatcher gerade beendet wird
     * @throws ClassNotFoundException   Tritt auf, wenn der Zugriff auf die
     *                                  Problemklasse nicht funktioniert,
     *                                  Möglicherweise wegen einer falschen URL.
     * @throws ProblemComputeException  Wenn nicht genung Compute-System-Resourcen
     *                                  vorhanden sind.
     */
    public synchronized void loadProblem(ProblemTransmitter transmitter,
                                         URL url,
                                         String className,
                                         Object[] problemParameters,
                                         RemoteStoreGenerator generator)
        throws RemoteException, ClassNotFoundException, ProblemComputeException {

        if (terminated) {
            throw new RemoteException("Dispatcher ist beim Shutdown");
        }

        if (transmitter == null) {
            throw new ProblemComputeException(
            "Kein gültiger Problem-Übermittler angegeben.");
        } else {
            if (probWrapTransmitter.containsValue(transmitter)) {
                throw new ProblemComputeException(
                "Problem-Übermittler wird bereits verwendet.");
            }
        }

        if (computationResourcesLeft()) {
            // Klasse vom HTTP-Server holen und instanziieren
            Class problemClass;
            try {
                Class[] problemParameterClasses = null;
                if (problemParameters != null) {
                    problemParameterClasses =
                        new Class[problemParameters.length];
                    for (int i = 0; i < problemParameters.length; i++) {
                        problemParameterClasses[i] =
                            problemParameters[i].getClass();
                    }
                }
                problemClass = RMIClassLoader.loadClass(url, className);
                Constructor c =
                    problemClass.getConstructor(problemParameterClasses);
                Problem problem = (Problem) c.newInstance(problemParameters);

                systemStatistic.notifyProblemReceived();

                ProblemWrapper probWrapper =
                    new ProblemWrapper(this, problem, generator, systemStatistic);
                log.info(probWrapper.toString() + " erzeugt");
                probWrapTransmitter.put(probWrapper, transmitter);

                // Null-Objekte aus Teilproblem-Puffer entfernen, da das neue
                // Problems möglicherweise ein Teilproblem erzeugt hat, die
                // Abfrage durch collectPartialProblem aber vieleicht vor dem
                // Hinzufügen von probWrapper zu probWrapTransmitter erfolgte.
                parProbWrapperBuffer.removeNullElements();
            } catch (ClassNotFoundException e) {
                log.warning("<E> Zugriff auf Problem-Klasse nicht möglich");
                throw e;
            } catch (Exception e) {
                log.severe("<E> unerwartete Ausnahme");
                e.printStackTrace();
                throw new ProblemComputeException("Exception: "
                        + e.toString());
            } catch (Error e) {
                // Fängt insbesondere einen NoClassDefFoundError ab.
                log.severe("<E> unerwarteter Error");
                e.printStackTrace();
                throw new ProblemComputeException("Error: " + e.toString());
            }
        } else {
            throw new ProblemComputeException(
            "Keine Resourcen für neues Problem mehr frei.");
        }
    }

    /**
     * Verarbeitet ein neues serialisierbares Problem.
     * Nötiger Quellcode wird durch RMI von einem HTTP-Server geladen.<p>
     *
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * Lösung dem Problem zum Zusammenfügen übergeben.<br>
     *
     * @param transmitter  Problem-übermittler, der das Problem sendet und die
     *                     Lösung empfangen soll
     * @param problem      serialisierbares Problem, das verteilt berechnet
     *                     werden soll.
     * @param generator    Der verwendete RemoteStoreGenerator oder null, falls
     *                     kein RemoteStoreGenerator verwendet wird.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     * @throws ProblemComputeException  bei Berechnungsfehler.
     *
     * @see  SerializableProblem
     * @see  PartialProblem
     */
    public synchronized void receiveProblem(ProblemTransmitter transmitter,
                                            SerializableProblem problem,
                                            RemoteStoreGenerator generator)
        throws RemoteException, ProblemComputeException {

        if (terminated) {
            throw new RemoteException("Dispatcher ist beim Shutdown");
        }

        if (transmitter == null) {
            throw new ProblemComputeException(
            "Kein gültiger Problem-Übermittler angegeben.");
        } else {
            if (probWrapTransmitter.containsValue(transmitter)) {
                throw new ProblemComputeException(
                "Problem-Übermittler wird bereits verwendet.");
            }
        }

        if (computationResourcesLeft()) {
            systemStatistic.notifyProblemReceived();

            ProblemWrapper probWrapper =
                new ProblemWrapper(this, problem, generator, systemStatistic);
            log.info(probWrapper.toString() + " erzeugt");
            probWrapTransmitter.put(probWrapper, transmitter);

            // Null-Objekte aus Teilproblem-Puffer entfernen, da das neue
            // Problems möglicher Weise ein Teilproblem erzeugt hat, die
            // Abfrage durch collectPartialProblem aber vieleicht vor dem
            // Hinzufügen von probWrapper zu probWrapTransmitter erfolgte.
            parProbWrapperBuffer.removeNullElements();
        } else {
            throw new ProblemComputeException("Keine Resourcen für neues"
                    + " Problem mehr frei.");
        }
    }

    /**
     * Stellt die problemspezifische Statistik bereit.
     *
     * @param transmitter  Problem-Transmitter, der das Problem übermittelt hat,
     *                     zu dem die problemspezifische Statistik abgefragt
     *                     werden soll.
     *
     * @return problemsezifische Statistik über den Zustand eines Problems.
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     */
    public ProblemStatistics getProblemStatistics(ProblemTransmitter transmitter)
        throws RemoteException {

        if (transmitter == null) {
            return null;
        } else {
            synchronized (probWrapTransmitter) {
                ProblemWrapper probWrap = problemWrapper(transmitter);
                if (probWrap != null) {
                    return probWrap.getStatistics();
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Liefert einen Schnappschuß der allgemeinen Compute-System-Statistik.
     *
     * @return  Allgemeine Statistik über den Zustand des Compute-Systems.
     *
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     */
    public SystemStatistics getSystemStatistics() throws RemoteException {
        return systemStatistic.getSnapshot();
    }
}

