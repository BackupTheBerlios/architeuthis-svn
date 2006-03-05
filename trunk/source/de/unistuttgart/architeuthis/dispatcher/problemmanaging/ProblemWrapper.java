/*
 * file:        ProblemWrapper.java
 * created:     29.06.2003
 * last change: 05.03.2006 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
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
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.misc.Numerator;
import de.unistuttgart.architeuthis.misc.util.BlockingBuffer;
import de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi;
import de.unistuttgart.architeuthis.dispatcher.statistic.SystemStatisticsCollector;
import de.unistuttgart.architeuthis.dispatcher.statistic.ProblemStatisticsCollector;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreGenException;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;

/**
 * Die Klasse kapselt ein Problem mit den schon erzeugten Teilproblemen und
 * den schon erhaltenen Teill�sungen.
 *
 * @author J�rgen Heit, Andreas Heydlauff, Dietmar Lippold
 */
class ProblemWrapper extends Thread {

    /**
     * Ein Z�hler f�r die Anzahl der erzeugten Instanzen diesert Klasse.
     */
    private static Numerator problemIdNumerator = new Numerator();

    /**
     * Eine Nummer, die dieses Objekt und das darin gespeicherte Problem
     * eindeutig kennzeichnet.
     */
    private long problemId;

    /**
     * Menge von <code>ParProbWrapper</code>-Objekten, die als Teilprobleme
     * vom zugeh�rigen Problem generiert und die ausgeliefert wurden und zu
     * denen noch keine Teill�sung geliefert wurde.
     */
    private Set deliveredParProbWrapper =
        Collections.synchronizedSet(new HashSet());

    /**
     * Buffer f�r <code>ParProbWrapper</code>-Objekte mit den vom Problem
     * erzeugten Teilproblemen.
     */
    private BlockingBuffer parProbWrapBuffer = new BlockingBuffer();

    /**
     * Buffer f�r Paare aus den vom Problem erzeugten Teilprobleme und den
     * zugeh�rigen Teill�sungen f�r das Problem.
     */
    private BlockingBuffer parProbSolBuffer = new BlockingBuffer();

    /**
     * Referenz auf das zugeordnete Problem.
     */
    private Problem problem;

    /**
     * Zentrales Speicherobjekt f�r dieses Problem.
     */
    private RemoteStore centralRemoteStore = null;

    /**
     * RemoteStoreGenerator f�r dieses Problem, mit dem der
     * <CODE>centralRemoteStore</CODE> erzeugt wurde.
     */
    private RemoteStoreGenerator remoteStoreGenerator = null;

    /**
     * Signalisiert dem Thread, sich zu beenden.
     */
    private volatile boolean terminated = false;

    /**
     * Gibt an, ob der ClassLoader zum Problem freigegeben werden kann.
     */
    private volatile boolean classLoaderUnblocked = false;

    /**
      * System-Statistik.
      */
    private SystemStatisticsCollector systemStatistic;

    /**
      * Problemspezifische Statistik.
      */
    private ProblemStatisticsCollector problemStatistic;

    /**
     * {@link java.util.logging.Logger} eingestellt auf
     * de.unistuttgart.architeuthis.dispatcher
     */
    private static final Logger LOGGER =
        Logger.getLogger(ProblemWrapper.class.getName());

    /**
     * Referenz auf den ProblemManager
     */
    private ProblemManagerImpl problemManager;

    /**
     * Wird vom {@link de.unistuttgart.architeuthis.interfaces.ProblemManager}
     * aufgerufen und setzt eine Referenz auf das zu verwaltende Problem sowie
     * die System-Statistik.
     *
     * @throws RemoteStoreGenException  Der zentrale <CODE>RemoteStore</CODE>
     *                                  konnte nicht erzeugt werden.
     *
     * @param probMan       Zentraler Problem-Manager.
     * @param prob          Zu verwaltendes Problem.
     * @param sysStatistic  Statistik des ComputeSystems.
     * @param generator     RemoteStore Generator.
     */
    ProblemWrapper(ProblemManagerImpl probMan,
                   Problem prob,
                   RemoteStoreGenerator generator,
                   SystemStatisticsCollector sysStatistic)
        throws RemoteStoreGenException {

        super();

        // Zuerst wird ein zentraler RemoteStore erzeugt
        try {
            if (generator != null) {
                centralRemoteStore = generator.generateCentralRemoteStore();
            }
            remoteStoreGenerator = generator;
        } catch (Throwable e) {
            LOGGER.info("Fehler bei der Erzeugung vom zentralen RemoteStore");
            throw new RemoteStoreGenException(e.getMessage(), e.getCause());
        }

        problem = prob;
        problemManager = probMan;
        systemStatistic = sysStatistic;
        problemStatistic = new ProblemStatisticsCollector(sysStatistic);
        problemId = problemIdNumerator.nextNumber();

        // URLs des ClassLoader des Problems registrieren
        URLClassLoader ucl = (URLClassLoader) problem.getClass().getClassLoader();
        CacheFlushingRMIClSpi.registerUrls(ucl.getURLs());

        // Thread starten
        start();
    }

    /**
     * Liefert den zentralen RemoteStore zum gespeicherten Problem.
     *
     * @return  Den im Konstruktor erzeugten zentralen RemoteStore.
     */
    RemoteStore getCentralRemoteStore() {
        return centralRemoteStore;
    }

    /**
     * Liefert den RemoteStoreGenerator zum gespeicherten Problem.
     *
     * @return  Den f�r diese Problem verwendeten RemoteStoreGenerator.
     */
    RemoteStoreGenerator getRemoteStoreGenerator() {
        return remoteStoreGenerator;
    }

    /**
     * Liefert eine neue Liste mit den Wrappern aller in Bearbeitung
     * befindlichen und ausgelieferten Teilprobleme des Problems dieser
     * Instanz.
     *
     * @return  Alle ausgelieferten in Bearbeitung befindlichen Teilproblem.
     */
    List getAllDeliveredParProbWrapper() {
        List parProbWrapper = new LinkedList();

        synchronized (deliveredParProbWrapper) {
            parProbWrapper.addAll(deliveredParProbWrapper);
        }
        return parProbWrapper;
    }

    /**
     * Gibt an, ob von diesem Problem derzeit Teilproblem-Wrapper geliefert
     * werden k�nnen.
     *
     * @return  <code>true</code>, falls Teilproblem-Wrapper verf�gbar,
     *          <code>false</code sonst.
     */
    boolean newParProbWrapperAvailable() {
        return ((!parProbWrapBuffer.isEmpty()) && (!terminated));
    }

    /**
     * Liefert einen neuen Teilproblem-Wrapper aus dem Teilproblempuffer oder
     * den Wert <code>null</code>, wenn derzeit kein neuer Wrapper eines
     * Teilproblems verf�gbar ist. Eine Instanz von ParProbWrapper enth�lt als
     * erzeugendes Objekt dieses Objekt.
     *
     * @return  Wrapper eines Teilproblems {@link ParProbWrapper} oder
     *          <code>null</code>
     */
    ParProbWrapper getNewParProbWrapper() {
        ParProbWrapper parProbWrap = null;

        if (!terminated) {
            synchronized (parProbWrapBuffer) {
                if (!parProbWrapBuffer.isEmpty()) {
                    parProbWrap = (ParProbWrapper) parProbWrapBuffer.dequeue();
                    deliveredParProbWrapper.add(parProbWrap);
                }
            }
        }
        return parProbWrap;
    }

    /**
     * �bergibt eine berechnete Teill�sung und den Wrapper des zugeh�rigen
     * Teilproblems.
     *
     * @param parProbWrap  Der Wrapper des zur Teill�sung geh�renden
     *                     brechneten Teilproblem.
     * @param parSol       Die berechnete Teill�sung.
     */
    void collectPartialSolution(ParProbWrapper parProbWrap,
                                PartialSolution parSol) {
        PartialSolutionParProbWrapper parProbWrapSolPair;

        synchronized (parProbSolBuffer) {
            if (deliveredParProbWrapper.remove(parProbWrap)) {
                parProbWrapSolPair = new PartialSolutionParProbWrapper(parProbWrap, parSol);
                parProbSolBuffer.enqueue(parProbWrapSolPair);
            } else {
                LOGGER.severe("Teill�sung erhalten zu einem Teilproblem,"
                              + " das nicht in Berechnung war.");

            }
        }
    }

    /**
     * Liefert die statistischen Daten des {@link Problem}s zur�ck.
     *
     * @return  ProblemStatistik {@link ProblemStatistics}
     */
    ProblemStatistics getStatistics() {
        return problemStatistic.getSnapshot();
    }

    /**
     * Liefert die voraussichtliche Zeit bis alle Teilprobleme, die derzeit
     * noch in Berechnung sind, berechnet worden sind. Wenn sich kein
     * Teilproblem in Berechnung befindet, wird der Wert Null geliefert.
     *
     * @return  Die gesch�tzte Zeit, bis alle in Berechnung befindlichen
     *          Teilprobleme berechnet sind.
     */
    long estimatedComputationTime() {
        return problemStatistic.estimatedComputationTime(parProbWrapBuffer.size());
    }

    /**
     * Liefert das Verh�ltnis der gesamten Berechnungszeit aller fertig
     * berechneten Teilprobleme des Problems zur Dauer der Existenz des
     * Problems. Wenn das Probem erst 0 ms existiert, wird der Wert Null
     * geliefert.
     *
     * @return  Das Verh�ltnis der gesamten Berechnungszeit aller fertig
     *          berechneten Teilprobleme des Problems zur Dauer der Existenz
     *          des Problems.
     */
    float computationRatio() {
        ProblemStatistics snapshot = problemStatistic.getSnapshot();
        long probAge = snapshot.getProblemAge();

        if (probAge == 0) {
            return 0;
        } else {
            return ((float) snapshot.getTotalComputationDuration() / probAge);
        }
    }

    /**
     * Signalisiert dem Thread, sich zu beenden und unterbricht ihn in
     * Wartezust�nden.
     */
    void terminate() {
        terminated = true;

        // Eine m�gliche Blockierung vom Puffer parProbSolBuffer aufheben
        parProbSolBuffer.enqueue(null);
    }

    /**
     * Gibt den ClassLoader zum Problem frei, da dieser mit Sicherheit nicht
     * mehr ben�tigt wird.
     */
    void unblockClassLoader() {
        classLoaderUnblocked = true;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Fragt das Problem nach der Gesamtl�sung. Falls diese existiert, wird sie
     * an den Problem-�bermittler gesendet, ausstehende Teilprobleme werden
     * abgebrochen und es wird die Methode <CODE>terminate()</CODE> aufgerufen.
     */
    private void askForSolution() {
        Serializable solution = null;

        try {
            solution = problem.getSolution();
        } catch (RuntimeException e) {
            problemManager.reportException(
                this,
                null,
                ExceptionCodes.SOLUTION_CREATE_EXCEPTION,
                e.getMessage());
            solution = null;
            terminate();
        }
        if (solution != null) {
            // L�sung an den Benutzer senden und Statistik updaten
            problemManager.sendSolution(this, solution);

            // Ausstehende Teilprobleme abbrechen und l�schen
            problemManager.removeProblem(this);

            terminate();
        }
    }

    /**
     * Liefert die maximale Anzahl von Teilproblemen, die ausgegeben werden
     * d�rfen, von denen noch keine Teill�sung geliefert wurde.
     *
     * @param sysStat  Die Systemstatistik.
     *
     * @return  die maximal zul�ssige Anzahl von ausstehenden Teilproblemen
     */
    private long parProbsOutLimit(SystemStatistics sysStat) {
        return (sysStat.getRegisteredOperatives() + 3);
    }

    /**
     * Liefert die vorgeschagene Anzahl von zu erzeugenden Teilproblemen, die
     * dem konkreten Problem beim Abruf eines Teilproblems angegeben wird.
     * Sie errechnet sich aus der Anzahl der momentan angemeldeten Operatives
     * und der Anzahl der momentan zu bearbeitenden Probleme. Die Anzahl ist
     * gr��er oder gleich Eins und kleiner oder gleich der Anzahl momentan
     * angemeldeter Operatives.
     *
     * @param sysStat  Die Systemstatistik.
     *
     * @return  die vorgeschagene Anzahl von zu erzeugenden Teilproblemen
     */
    private long suggestedParProbNumber(SystemStatistics sysStat) {
        float operativesPerProb = ((float) sysStat.getRegisteredOperatives())
                                  / sysStat.getCurrentProblems();

        if (operativesPerProb <= 1.0f) {
            return 1;
        } else {
            return Math.round(operativesPerProb);
        }
    }

    /**
     * Versucht in einer Schleife bis zur Terminierung ein Teilproblem in den
     * Buffer zu holen und aus dem Buffer eine Teill�sung dem
     * Problem zur�ckzugeben. Es wird sicher gestellt, da� nur eine bestimmte
     * maximale Anzahl von Teilproblemen aussteht. Bevor der Thread sich
     * beendet, aktualisiert er die Statistik zur Anzahl der abgebrochenen
     * Teilprobleme.
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        PartialSolutionParProbWrapper parProbWrapSolPair = null;
        PartialProblem parProb = null;
        ParProbWrapper parPropWrapper = null;
        SystemStatistics sysStat;
        long parProbsOut = 0;  // Anzahl ausstehender Teilprobleme
        long suggestedParProbNumber;

        while (!terminated) {
            sysStat = systemStatistic.getSnapshot();

            // Teilproblem abfragen, falls noch nicht zuviele ausgegeben wurden
            if (parProbsOut < parProbsOutLimit(sysStat)) {
                try {
                    suggestedParProbNumber = suggestedParProbNumber(sysStat);
                    parProb = problem.getPartialProblem(suggestedParProbNumber);
                    problemStatistic.notifyRequestedPartialProblem();

                    if (parProb == null) {
                        parPropWrapper = null;
                    } else {
                        problemStatistic.notifyCreatedPartialProblem();
                        parPropWrapper = new ParProbWrapper(parProb,
                                                            this,
                                                            problemStatistic);
                    }
                } catch (RuntimeException e) {
                    problemManager.reportException(
                        this,
                        null,
                        ExceptionCodes.PARTIALPROBLEM_CREATE_EXCEPTION,
                        e.toString());
                    parPropWrapper = null;
                    terminate();
                    break;
                } catch (Error e) {
                    // F�ngt insbesondere einen NoClassDefFoundError ab.
                    problemManager.reportException(
                        this,
                        null,
                        ExceptionCodes.PARTIALPROBLEM_CREATE_EXCEPTION,
                        e.toString());
                    parPropWrapper = null;
                    terminate();
                    break;
                }
                if (parPropWrapper != null) {
                    // Teilproblem-Wrapper zum Buffer hinzuf�gen
                    parProbWrapBuffer.enqueue(parPropWrapper);
                    parProbsOut++;

                    // Wenn die Schlange der Teilprobleme bisher leer war,
                    // eventuell vorhandene null Objekte in der Schlange aller
                    // Teilprobleme l�schen
                    if (parProbWrapBuffer.size() <= 1) {
                        problemManager.removeNullElements();
                    }
                } else {
                    askForSolution();
                    if ((!terminated) && (parProbsOut == 0)) {
                        problemManager.reportException(
                            this,
                            null,
                            ExceptionCodes.PROBLEM_INCORRECT_ERROR,
                            "Problem liefert keine Teilprobleme und keine L�sung");
                        terminate();
                    }
                }
            }

            if ((!terminated)
                && ((!parProbSolBuffer.isEmpty())
                    || (parPropWrapper == null)
                    || (parProbsOut >= parProbsOutLimit(sysStat)))) {
                // Teill�sung aus dem Buffer dem Problem holen; falls keine
                // Teill�sung enthalten ist, wartet der Aufruf
                parProbWrapSolPair = (PartialSolutionParProbWrapper) parProbSolBuffer.dequeue();

                // Sicherstellen, dass es sich nicht um den Wert null handelt,
                // der von der Methode terminate hinzugef�gt wurde.
                if (parProbWrapSolPair != null) {
                    parPropWrapper = parProbWrapSolPair.getParProbWrapper();
                    problemStatistic.notifyProcessedPartialProblem();
                    try {
                        problem.collectResult(
                            parProbWrapSolPair.getPartialSolution(),
                            parPropWrapper.getPartialProblem());

                    } catch (Throwable e) {
                        LOGGER.info("Fehler bei der Verarbeitung einer Teill�sung");
                        problemManager.reportException(
                            this,
                            parPropWrapper,
                            ExceptionCodes.PARTIALSOLUTION_COLLECT_EXCEPTION,
                            e.toString());
                        terminate();
                        break;
                    }
                    parProbsOut--;

                    askForSolution();
                }
            }
        }

        // Zum Schluss noch die Statistik zur Anzahl der nicht bearbeiteten
        // Teilprobleme aktualisieren.
        synchronized (parProbWrapBuffer) {
            synchronized (parProbSolBuffer) {
                synchronized (deliveredParProbWrapper) {
                    problemStatistic.notifyAbortedPartialProblems(
                        parProbWrapBuffer.size());
                    parProbWrapBuffer.clear();

                    problemStatistic.notifyAbortedPartialProblems(
                        deliveredParProbWrapper.size());
                    deliveredParProbWrapper.clear();

                    // Vor der Verwendung von parProbSolBuffer den in der
                    // Methode terminate eingef�gten Wert null l�schen
                    parProbSolBuffer.removeNullElements();

                    problemStatistic.notifyAbortedPartialProblems(
                        parProbSolBuffer.size());
                    parProbSolBuffer.clear();
                }
            }
        }

        // warten, bis der ClassLoader nicht mehr gebraucht wird
        synchronized (this) {
            while (!classLoaderUnblocked) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        // zentralen RemoteStore beenden
        if (centralRemoteStore != null) {
            try {
                centralRemoteStore.terminate();
            } catch (Throwable e) {
                LOGGER.info("Fehler beim Beenden vom zentralen RemoteStore");
                problemManager.reportException(
                    this,
                    parPropWrapper,
                    ExceptionCodes.REMOTE_STORE_EXCEPTION,
                    e.toString());
            }
        }

        // URLs des ClassLoader des Problems abmelden
        URLClassLoader ucl = (URLClassLoader) problem.getClass().getClassLoader();
        CacheFlushingRMIClSpi.unregisterUrls(ucl.getURLs());
    }

    /**
     * Liefert einen Text, der das enthaltene Problem eindeutig kennzeichnet.
     *
     * @return  Einen Text, der das enthaltene Problem eindeutig kennzeichnet.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Problem " + problemId;
    }
}

