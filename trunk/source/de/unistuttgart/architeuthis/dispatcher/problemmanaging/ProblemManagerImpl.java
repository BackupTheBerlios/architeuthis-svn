/*
 * file:        ProblemManagerImpl.java
 * created:     29.06.2003
 * last change: 25.04.2006 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
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
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemTransmitter;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreGenException;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;

/**
 * Der <code>ProblemManager</code> erm�glicht es mehere Probleme gleichzeitig
 * mit dem {@link ComputeManager} zu berechnen.
 *
 * @author J�rgen Heit, Andreas Heydlauff, Dietmar Lippold
 */
public class ProblemManagerImpl extends UnicastRemoteObject implements ProblemManager {

    /**
     * Die Gr��e des Puffers, in dem die erzeugten Teilprobleme aller Probleme
     * gespeichert werden.
     */
    private static final int BLOCKING_BUFFER_SIZE = 1;

    /**
     * Die maximale Anzahl der Versuche, eine L�sung an den Problem-Transmitter
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
     * zugeh�rige <code>ProblemTransmitter</code> zugeordnet wird.
     */
    private Map probWrapTransmitter =
        Collections.synchronizedMap(new HashMap());

    /**
     * Puffer f�r die Teilproblem-Wrapper aller Probleme.
     */
    private BlockingBuffer parProbWrapperBuffer;

    /**
     * Thread, der die Teilproblem-Wrapper aller Probleme einsammelt.
     */
    private PartialProblemCollector partialProblemCollectorThread;

    /**
     * Statistik �ber den Dispatcher
     */
    private SystemStatisticsCollector systemStatistic;

    /**
     * {@link java.util.logging.Logger} eingestellt auf
     * de.unistuttgart.architeuthis.dispatcher
     */
    private static final Logger LOGGER =
        Logger.getLogger(ProblemManagerImpl.class.getName());

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
     * Liefert einn Teilproblem-Wrapper ({@link ParProbWrapper}) zur�ck, falls
     * es ein {@link Problem} in der Problem-Verwaltungs-Collection gibt,
     * welches noch Teilproblem-Wrapper hat.
     * Falls kein Problem aktuell ein Teilproblem verf�gbar hat, wird
     * <code>null</code> zur�ckgeliefert. Wenn mindestens ein Problem ein
     * Teilproblem-Wrapper verf�gbar hat, wird der Teilproblem-Wrapper von dem
     * Problem geliefert, von dem die gesch�tzte gesamte Berechnungszeit der
     * Teilprobleme, die vom <code>ProblemWrapper</code> ausgeliefert aber noch
     * nicht berechnet wurden, am geringsten ist. Wenn es mehrere Probleme gibt,
     * von denen derzeit kein Teilproblem ausgeliefert ist, deren gesch�tzte
     * Berechnungszeit also einheitlich Null ist, wird das Problem mit dem
     * geringsten Verh�ltnis von bisheriger Berechnungsdauer zu Existenzdauer
     * gew�hlt.
     *
     * @return  Teilproblem-Wrapper ({@link ParProbWrapper}) des Problems mit
     *          der gesch�tzten geringsten Berechnungszeit der in Bearbeitung
     *          befindlichen Teilprobleme, oder <code>null</code>, wenn kein
     *          Teilproblem-Wrapper verf�gbar ist.
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
     * L�scht die Werte <code>null</code> aus dem Teilproblem-Puffer.
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
                LOGGER.fine("Start vom Thread reactivatePassiveOperatives()");
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

        // Die Partial-Problem-Wrapper aus dem Puffer l�schen
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
     * Gesamtl�sung ermittelt wurde, aus der Verwaltung.
     *
     * @param probWrapper  ProblemWrapper vom zu entfernenden Problem.
     *
     * @return  Den <code>ProblemTransmitter</code>, der das Problem, das
     *          entfernt wurde, �bertragen hat.
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
            LOGGER.finest("Breche f�r " + probWrapper.toString()
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
     * Liefert den n�chsten Teilproblem-Wrapper ({@link ParProbWrapper}) aus
     * dem Puffer der zur Berechnung stehenden Teilprobleme.
     *
     * @return  Wrapper f�r das Teilproblem.
     */
    public ParProbWrapper getParProbWrapper() {
        ParProbWrapper partProbWrap =
            (ParProbWrapper) parProbWrapperBuffer.dequeue();
        return partProbWrap;
    }

    /**
     * Gibt die Teill�sung an das Problem weiter.
     *
     * @param partSol       zur�ckzuliefernde Teill�sung
     *                      ({@link PartialSolution})
     * @param partProbWrap  Wrapper des zur Teill�sung geh�renden Teilproblems
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
                    // L�sung an den ProblemWraper zur�ckgeben
                    probWrap.collectPartialSolution(partProbWrap, partSol);
                }
            }
        }
    }

    /**
     * Sendet eine Nachricht an einen Problem-�bermittler.
     *
     * @param transmitter   Empf�nger der Nachricht.
     * @param messageID     Code der Nachricht.
     * @param message       Nachrichtentext.
     */
    private void sendMessage(ProblemTransmitter transmitter,
                             int messageID,
                             String message) {

        try {
            LOGGER.fine("Sende Nachricht \""
                        + String.valueOf(message)
                        + "\" an Problem-�bermittler");
            transmitter.fetchMessage(messageID, message);
        } catch (RemoteException e) {
            LOGGER.severe("Nachricht konnte nicht an den Problem-Uebermittler"
                          + " geschickt werden");
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
     * Ausnahmen werden �ber diese Methode an den Problem-Manager gemeldet.
     *
     * @param problemWrapper    Der problemWrapper, zu dem die Exception
     *                          aufgetreten ist.
     * @param parProbWrapper    Der parProbWrapper, zu dem die Exception
     *                          aufgetreten ist.
     * @param exceptionCode     Integerwert, der die Ausnahme charakterisisert
     * @param exceptionMessage  Fehlermeldung um die Ausnahme n�her zu
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
            LOGGER.warning("Exception PARTIALPROBLEM_CREATE_EXCEPTION zu "
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
            LOGGER.warning("Exception PARTIALSOLUTION_COLLECT_EXCEPTION zu "
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
            LOGGER.warning("Exception SOLUTION_CREATE_EXCEPTION zu "
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
            LOGGER.warning("Exception PARTIALPROBLEM_SEND_EXCEPTION zu "
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
            LOGGER.warning("Exception PARTIALSOLUTION_SEND_EXCEPTION zu "
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
            LOGGER.warning("Exception SOLUTION_SEND_EXCEPTION zu "
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
            LOGGER.warning("Exception PARTIALPROBLEM_COMPUTE_EXCEPTION zu "
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
        case ExceptionCodes.REMOTE_STORE_GEN_EXCEPTION :
            LOGGER.warning("Exception REMOTE_STORE_GEN_EXCEPTION zu "
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
        case ExceptionCodes.REMOTE_STORE_EXCEPTION :
            LOGGER.warning("Exception REMOTE_STORE_EXCEPTION zu "
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
            LOGGER.warning("Exception PARTIALPROBLEM_ERROR zu "
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
            LOGGER.warning("Exception PROBLEM_INCORRECT_ERROR zu "
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
            LOGGER.warning("Exception USER_ABORT_PROBLEM zu "
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
                "Keine Operatives verf�gbar.");
            break;
        case ExceptionCodes.NEW_OPERATIVES_REGISTERED :
            sendMessageToAll(
                ExceptionCodes.NEW_OPERATIVES_REGISTERED,
                "Neuer Operative hat sich angemeldet.");
            break;
        default :
            LOGGER.severe(
                    "Unbekannte Fehlermeldung: "
                    + exceptionCode
                    + " "
                    + exceptionMessage);
        break;
        }
    }

    /**
     * Sendet die Gesamtl�sung an den Problem-Transmitter und aktualisiert die
     * Statistik bei erfolgreicher �bertragung.
     *
     * @param problemWrapper  Der ProblemWrapper, zu dessen Problem die
     *                        L�sung verschickt werden soll.
     * @param solution        Die zu verschickende Gesamtl�sung.
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
                    // Gesamtl�sung �bermitteln
                    transmitter.fetchSolution(
                            solution,
                            problemWrapper.getStatistics());
                    sent = true;
                    LOGGER.config("L�sung erfolgreich an Benutzer gesendet.");
                } catch (RemoteException e) {
                    exceptionMessage = e.toString();
                    LOGGER.warning(
                            "Problem-�bermittler nicht ereichbar f�r"
                            + " L�sungsr�ckgabe.");
                } catch (RuntimeException e) {
                    exceptionMessage = e.toString();
                    LOGGER.info("Senden der L�sung fehlgeschlagen");
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
            LOGGER.info("L�sung konnte nicht zur�ckgeliefert werden.");
        }
    }

    /**
     * Liefert den ProblemWrapper des angegebenen
     * <code>ProblemTransmitter</code>.
     *
     * @param transmitter  ProblemTransmitter, zu dem der ProblemWrapper
     *                     ermittelt werden soll.
     *
     * @return  ProblemWrapper des Problem-�bermittlers, der das Teilproblem
     *          �bermittelt hat, oder <code>null</code>, wenn dieser nicht
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
     * @param transmitter  Problem-�bermittler, der das Problem �bertragen hat.
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
     * Ermittelt, ob genug Resssourcen f�r die Annahme eines neuen Problems
     * vorhanden sind. Derzeit ist dies genau dann der Fall, wenn mindestens
     * ein Operative angemeldet ist.
     *
     * @return  <code>true</code>, wenn genug Resssourcen f�r die Annahme
     *          eines neuen Problems vorhanden sind, anderenfalls
     *          <code>false</code>.
     */
    private boolean computationResourcesLeft() {
        return (systemStatistic.getSnapshot().getRegisteredOperatives() > 0);
    }

    /**
     * Teilt dem ProblemManager die Position der Quelldateien f�r ein neues
     * Problem mit. Die n�tigten Klassen werden von einem HTTP-Server geladen und
     * das {@link Problem} wird initialisiert.<p>
     *
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}s
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * L�sung dem Problem zum Zusammenf�gen �bergeben.<br>
     * Wurden alle PartialProbleme berechnet, gibt <code>newProblem</code> die
     * Gesamtl�sung an den Problem-�bermittler zur�ck.
     *
     * @param transmitter        Problem-�bermittler, der das Problem sendet
     *                           und die L�sung empfangen soll.
     * @param url                Pfad zu den Quelldateien auf einem
     *                           HTTP-Server.
     * @param className          {@link Problem}-spezifischer Name, das die
     *                           Berechnung startet.
     * @param problemParameters  Die formalen Parameter des vom Problem zu
     *                           startenden Konstruktors.
     * @param generator          Der RemoteStore-Generator.
     *
     * @throws RemoteException          Falls ein Netzproblemen aufgetreten ist
     *                                  oder der Dispatcher gerade beendet wird.
     * @throws ClassNotFoundException   Tritt auf, wenn der Zugriff auf die
     *                                  Problemklasse nicht funktioniert,
     *                                  m�glicherweise wegen einer falschen URL.
     * @throws ProblemComputeException  Wenn nicht genung Compute-System-Resourcen
     *                                  vorhanden sind.
     * @throws RemoteStoreGenException  Der zentrale <CODE>RemoteStore</CODE>
     *                                  konnte nicht erzeugt werden.
     *
     * @see  Problem
     * @see  PartialProblem
     */
    public synchronized void loadProblem(ProblemTransmitter transmitter,
                                         URL url,
                                         String className,
                                         Object[] problemParameters,
                                         RemoteStoreGenerator generator)
        throws RemoteException, ClassNotFoundException,
               ProblemComputeException, RemoteStoreGenException {

        if (terminated) {
            throw new RemoteException("Dispatcher ist beim Shutdown");
        }

        if (transmitter == null) {
            throw new ProblemComputeException(
            "Kein g�ltiger Problem-�bermittler angegeben.");
        } else {
            if (probWrapTransmitter.containsValue(transmitter)) {
                throw new ProblemComputeException(
                "Problem-�bermittler wird bereits verwendet.");
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
                LOGGER.fine(probWrapper.toString() + " erzeugt");
                probWrapTransmitter.put(probWrapper, transmitter);

                // Null-Objekte aus Teilproblem-Puffer entfernen, da das neue
                // Problems m�glicherweise ein Teilproblem erzeugt hat, die
                // Abfrage durch collectPartialProblem aber vieleicht vor dem
                // Hinzuf�gen von probWrapper zu probWrapTransmitter erfolgte.
                parProbWrapperBuffer.removeNullElements();
            } catch (ClassNotFoundException e) {
                LOGGER.warning("Zugriff auf Problem-Klasse nicht m�glich");
                throw e;
            } catch (Exception e) {
                LOGGER.severe("Unerwartete Ausnahme");
                e.printStackTrace();
                throw new ProblemComputeException("Exception: "
                                                  + e.toString());
            } catch (Error e) {
                // F�ngt insbesondere einen NoClassDefFoundError ab.
                LOGGER.severe("Unerwarteter Error");
                e.printStackTrace();
                throw new ProblemComputeException("Error: " + e.toString());
            }
        } else {
            throw new ProblemComputeException("Keine Resourcen f�r neues"
                                              + " Problem mehr frei.");
        }
    }

    /**
     * Verarbeitet ein neues serialisierbares Problem.
     * N�tiger Quellcode wird durch RMI von einem HTTP-Server geladen.<p>
     *
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * L�sung dem Problem zum Zusammenf�gen �bergeben.<br>
     *
     * @param transmitter  Problem-�bermittler, der das Problem sendet und die
     *                     L�sung empfangen soll
     * @param problem      Serialisierbares Problem, das verteilt berechnet
     *                     werden soll.
     * @param generator    Der verwendete RemoteStoreGenerator oder
     *                     <CODE>null</CODE>, falls kein RemoteStoreGenerator
     *                     verwendet wird.
     *
     * @throws RemoteException          Bei einem RMI-Verbindungsproblem.
     * @throws ProblemComputeException  Bei einem Berechnungsfehler.
     * @throws RemoteStoreGenException  Der zentrale <CODE>RemoteStore</CODE>
     *                                  konnte nicht erzeugt werden.
     *
     * @see  SerializableProblem
     * @see  PartialProblem
     */
    public synchronized void receiveProblem(ProblemTransmitter transmitter,
                                            SerializableProblem problem,
                                            RemoteStoreGenerator generator)
        throws RemoteException, ProblemComputeException, RemoteStoreGenException {

        if (terminated) {
            throw new RemoteException("Dispatcher ist beim Shutdown");
        }

        if (transmitter == null) {
            throw new ProblemComputeException(
            "Kein g�ltiger Problem-�bermittler angegeben.");
        } else {
            if (probWrapTransmitter.containsValue(transmitter)) {
                throw new ProblemComputeException("Problem-�bermittler wird"
                                                  + " bereits verwendet.");
            }
        }

        if (computationResourcesLeft()) {
            systemStatistic.notifyProblemReceived();

            ProblemWrapper probWrapper =
                new ProblemWrapper(this, problem, generator, systemStatistic);
            LOGGER.fine(probWrapper.toString() + " erzeugt");
            probWrapTransmitter.put(probWrapper, transmitter);

            // Null-Objekte aus Teilproblem-Puffer entfernen, da das neue
            // Problems m�glicher Weise ein Teilproblem erzeugt hat, die
            // Abfrage durch collectPartialProblem aber vieleicht vor dem
            // Hinzuf�gen von probWrapper zu probWrapTransmitter erfolgte.
            parProbWrapperBuffer.removeNullElements();
        } else {
            throw new ProblemComputeException("Keine Resourcen f�r neues"
                                              + " Problem mehr frei.");
        }
    }

    /**
     * Stellt die problemspezifische Statistik bereit.
     *
     * @param transmitter  Problem-Transmitter, der das Problem �bermittelt hat,
     *                     zu dem die problemspezifische Statistik abgefragt
     *                     werden soll.
     *
     * @return problemsezifische Statistik �ber den Zustand eines Problems.
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
     * Liefert einen Schnappschu� der allgemeinen Compute-System-Statistik.
     *
     * @return  Allgemeine Statistik �ber den Zustand des Compute-Systems.
     *
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     */
    public SystemStatistics getSystemStatistics() throws RemoteException {
        return systemStatistic.getSnapshot();
    }
}

