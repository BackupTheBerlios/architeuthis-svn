/*
 * file:        ProblemComputation.java
 * created:     19.02.2004
 * last change: 16.06.2004 by Dietmar Lippold
 * developers:  J�rgen Heit, juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
 *
 *
 * This file is part of Architeuthis.
 *
 * Architeuthis is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * Architeuthis is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Architeuthis; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */

// FIXME: transmitProblem Methoden �berarbeiten

package de.unistuttgart.architeuthis.user;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.misc.Numerator;
import de.unistuttgart.architeuthis.dispatcher.statistic.ProblemStatisticsCollector;
import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.CommunicationPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.NonCommPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;

/**
 * Die Klasse ProblemComputation berechnet ein Problem.
 * Dazu kapselt Sie den ProblemTransmitter zum �bertragen des Problems auf ein
 * ComputeSystem und bietet die M�glichkeit zur lokalen Berechnung (Simulation
 * eines solchen ComputeSystems).
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public class ProblemComputation {

    /**
     * Die Anzahl der Probleme, die bisher zur lokalen Berechnung �bergeben
     * wurde.
     */
    private static Numerator localProblemNumerator = new Numerator();

    /**
     * Die Problem-Statistik des letzten berechneten Problems.
     */
    private volatile ProblemStatistics finalProbStat = null;

    /**
     * Gibt an, ob zus�tzliche Informationen ausgegeben werden sollen.
     */
    private boolean debugMode;

    /**
     * Ereugt eine neue Instanz.
     *
     * @param debug  gibt an, ob debug-Ausgaben erfolgen sollen.
     */
    public ProblemComputation(boolean debug) {
        debugMode = debug;
    }

    /**
     * Ereugt eine neue Instanz. Bei dieser werden keine zus�tzlichen
     * debug-Meldungen ausgegeben.
     */
    public ProblemComputation() {
        this(false);
    }

    /**
     * Berechnet ein <code>Problem</code> lokal. Der Methode
     * <code>getPartialProblem</code> der konkreten Instanz des Problems wird
     * dabei der angegebene Wert �bergeben. Die erzeugten Teilprobleme werden
     * jedoch rein sequentiell verarbeitet, wobei zu jedem Zeitpunkt maximal
     * ein Teilproblem erzeugt und noch nicht bearbeitet ist (sondern gerade
     * bearbeitet wird). Nach der Erzeugung einer Gesamtl�sung ist daher die
     * Anzahl der bearbeiteten Teilprobleme immer genau so gro� wie die Anzahl
     * der erzeugten und der angeforderten Teilprobleme.
     *
     * @param problem                Zu berechnendes Problem.
     * @param numberPartialProblems  Die vorgeschlagene Anzahl von
     *                               Teilproblemen, die vom konkreten Problem
     *                               erzeugt werden soll.
     *
     * @return  L�sung des Problems.
     *
     * @throws ProblemComputeException  Das Problem liefert keine Teilprobleme
     *                                  mehr aber auch keine Gesamtl�sung.
     */
    public Serializable computeProblem(SerializableProblem problem,
                                       long numberPartialProblems)
        throws ProblemComputeException {

        ProblemStatisticsCollector probStatCollector;
        PartialProblem partialProblem = null;
        PartialSolution partialSolution = null;
        Serializable solution = null;
        long problemId;

        problemId = localProblemNumerator.nextNumber();
        probStatCollector = new ProblemStatisticsCollector(null);
        do {
            probStatCollector.notifyRequestedPartialProblem();
            partialProblem = problem.getPartialProblem(numberPartialProblems);

            if (partialProblem != null) {
                probStatCollector.notifyCreatedPartialProblem();

                // Teill�sung berechnen
                probStatCollector.startTimeMeasurement(null);
                // FIXME:
                // Implementierung des RemoteStores f�r lokale Berechnung ???
                if (partialProblem instanceof NonCommPartialProblem) {
                    partialSolution = ((NonCommPartialProblem)partialProblem).compute();
                } else if (partialProblem instanceof CommunicationPartialProblem ) {
                    System.err.println(
                        "Verwendung von RemoteStores f�r die lokale Berechnung ist noch nicht Implementiert");
                } else {
                	// FIXME: bessere Fehlerbehandlung 
                	System.err.println("PartialProblem implementiert kein passendes Interface");
                	System.exit(1);             	
                }
                probStatCollector.stopTimeMeasurement(null);

                // Teill�sung dem Problem zur�ckgeben, damit es die
                // Gesamtl�sung zusammensetzen kann.
                problem.collectResult(partialSolution, partialProblem);
                probStatCollector.notifyProcessedPartialProblem();

                // Nach der Gesamtl�sung fragen
                solution = problem.getSolution();
            }
        } while ((partialProblem != null) && (solution == null));
        finalProbStat = probStatCollector.getSnapshot();

        if (solution == null) {
            throw new ProblemComputeException("Problem liefert keine"
                                              + " Teilprobleme und keine L�sung");
        }
        return solution;
    }

    /**
     * Berechnet ein <code>Problem</code> lokal, wobei der Methode
     * <code>getPartialProblem</code> der konkreten Instanz des Problems der
     * Wert 1 �bergeben wird.
     *
     * @param problem  Zu berechnendes Problem.
     *
     * @return  L�sung des Problems.
     *
     * @throws ProblemComputeException  Ausnahme, die bei der Berechnung eines
     *                                  Teilproblems auftreten kann.
     */
    public Serializable computeProblem(SerializableProblem problem)
        throws ProblemComputeException {

        return computeProblem(problem, 1);
    }

    /**
     * Berechnet ein <code>Problem</code> durch einen <code>Dispatcher</code>.
     * Die URLs, unter denen die Klassen liegen, m�ssen beim Aufruf der JVM
     * durch das Property <code>java.rmi.server.codebase</code> angeben sein.
     *
     * @param problem         Zu berechnendes Problem.
     * @param dispatcherHost  Adresse des ComputeSystems. Wenn kein Port
     *                        angegeben ist, wird der Standard-Port verwendet.
     * @param generator       RemoteStoreGenerator zur Erzeugung der verteilten
     *                        Speicher.
     *
     * @return  L�sung des Problems.
     *
     * @throws MalformedURLException    URL der Registry ist falsch.
     * @throws NotBoundException        Der Server war auf der Registry nicht
     *                                  eingetragen.
     * @throws RemoteException          Kommunikationsproblem �ber RMI.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(
        SerializableProblem problem,
        RemoteStoreGenerator generator,
        String dispatcherHost)
        throws
            MalformedURLException,
            RemoteException,
            NotBoundException,
            ProblemComputeException {

        // dies ist die zentrale transmitProblem-Methode
        // auf die alle anderen TransmitProblem Methoden zugreifen

        ProblemTransmitterImpl transmitter;
        Serializable solution = null;

        transmitter = new ProblemTransmitterImpl(dispatcherHost, debugMode);
        solution = transmitter.transmitProblem(problem, generator);
        finalProbStat = transmitter.getFinalProblemStat();

        return solution;
    }

    /**
     * Berechnet ein <code>Problem</code> durch einen <code>Dispatcher</code>.
     * Die URLs, unter denen die Klassen liegen, m�ssen beim Aufruf der JVM
     * durch das Property <code>java.rmi.server.codebase</code> angeben sein.
     *
     * @param problem         Zu berechnendes Problem.
     * @param dispatcherHost  Adresse des ComputeSystems. Wenn kein Port
     *                        angegeben ist, wird der Standard-Port verwendet.
     *
     * @return  L�sung des Problems.
     *
     * @throws MalformedURLException    URL der Registry ist falsch.
     * @throws NotBoundException        Der Server war auf der Registry nicht
     *                                  eingetragen.
     * @throws RemoteException          Kommunikationsproblem �ber RMI.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(
        SerializableProblem problem,
        String dispatcherHost)
        throws
            MalformedURLException,
            RemoteException,
            NotBoundException,
            ProblemComputeException {

        ProblemTransmitterImpl transmitter;
        Serializable solution = null;

        transmitter = new ProblemTransmitterImpl(dispatcherHost, debugMode);
        solution = transmitter.transmitProblem(problem);
        finalProbStat = transmitter.getFinalProblemStat();

        return solution;
    }


    /**
     * Berechnet ein <code>Problem</code> durch einen <code>Dispatcher</code>.
     * <code>codebases</code> geben die URLs an, von denen vom Problem
     * ben�tigte Klassen geladen werden k�nnen. Jede darin enthaltene URL wird
     * zu java.rmi.server.codebase hinzugef�gt, falls noch nicht vorhanden.
     *
     * @param problem         Zu berechnendes Problem.
     * @param dispatcherHost  Adresse des ComputeSystems. Wenn kein Port
     *                        angegeben ist, wird der Standard-Port verwendet.
     * @param codebases       URLs zu den vom Problem ben�tigten Klassen.
     * @param generator       RemoteStoreGenerator zur Erzeugung der verteilten
     *                        Speicher.
     *
     * @return  L�sung des Problems.
     *
     * @throws MalformedURLException    URL der Registry ist falsch.
     * @throws NotBoundException        Der Server war auf der Registry nicht
     *                                  eingetragen.
     * @throws RemoteException          Kommunikationsproblem �ber RMI.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(
        SerializableProblem problem,
        RemoteStoreGenerator generator,
        String dispatcherHost,
        URL[] codebases)
        throws
            MalformedURLException,
            RemoteException,
            NotBoundException,
            ProblemComputeException {

        addToCodebase(codebases);
        return transmitProblem(problem, generator, dispatcherHost);
    }
    /**
     * Berechnet ein <code>Problem</code> durch einen <code>Dispatcher</code>.
     * <code>codebases</code> geben die URLs an, von denen vom Problem
     * ben�tigte Klassen geladen werden k�nnen. Jede darin enthaltene URL wird
     * zu java.rmi.server.codebase hinzugef�gt, falls noch nicht vorhanden.
     *
     * @param problem         Zu berechnendes Problem.
     * @param dispatcherHost  Adresse des ComputeSystems. Wenn kein Port
     *                        angegeben ist, wird der Standard-Port verwendet.
     * @param codebases       URLs zu den vom Problem ben�tigten Klassen.
     *
     * @return  L�sung des Problems.
     *
     * @throws MalformedURLException    URL der Registry ist falsch.
     * @throws NotBoundException        Der Server war auf der Registry nicht
     *                                  eingetragen.
     * @throws RemoteException          Kommunikationsproblem �ber RMI.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(
        SerializableProblem problem,
        String dispatcherHost,
        URL[] codebases)
        throws
            MalformedURLException,
            RemoteException,
            NotBoundException,
            ProblemComputeException {

        addToCodebase(codebases);
        return transmitProblem(problem, dispatcherHost);
    }

    /**
     * Berechnet ein <code>Problem</code> durch einen <code>Dispatcher</code>.
     * Der URL, von dem vom Problem ben�tigte Klassen geladen werden, ist als
     * String zu �bergeben. Er wird zu java.rmi.server.codebase hinzugef�gt,
     * falls darin noch nicht vorhanden.
     *
     * @param problem         Zu berechnendes Problem.
     * @param dispatcherHost  Adresse des ComputeSystems. Wenn kein Port
     *                        angegeben ist, wird der Standard-Port verwendet.
     * @param codebase        URLs zu den vom Problem ben�tigten Klassen.
     * @param generator       RemoteStoreGenerator zur Erzeugung der verteilten
     *                        Speicher.
     *
     * @return  L�sung des Problems.
     *
     * @throws MalformedURLException    URL der Registry ist falsch.
     * @throws NotBoundException        Der Server war auf der Registry nicht
     *                                  eingetragen.
     * @throws RemoteException          Kommunikationsproblem �ber RMI.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(
        SerializableProblem problem,
        RemoteStoreGenerator generator,
        String dispatcherHost,
        String codebase)
        throws
            MalformedURLException,
            RemoteException,
            NotBoundException,
            ProblemComputeException {

        URL[] urls = new URL[1];
        urls[0] = new URL(codebase);

        return transmitProblem(problem, generator, dispatcherHost, urls);
    }

    /**
     * Berechnet ein <code>Problem</code> durch einen <code>Dispatcher</code>.
     * Der URL, von dem vom Problem ben�tigte Klassen geladen werden, ist als
     * String zu �bergeben. Er wird zu java.rmi.server.codebase hinzugef�gt,
     * falls darin noch nicht vorhanden.
     *
     * @param problem         Zu berechnendes Problem.
     * @param dispatcherHost  Adresse des ComputeSystems. Wenn kein Port
     *                        angegeben ist, wird der Standard-Port verwendet.
     * @param codebase        URLs zu den vom Problem ben�tigten Klassen.
     *
     * @return  L�sung des Problems.
     *
     * @throws MalformedURLException    URL der Registry ist falsch.
     * @throws NotBoundException        Der Server war auf der Registry nicht
     *                                  eingetragen.
     * @throws RemoteException          Kommunikationsproblem �ber RMI.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(
        SerializableProblem problem,
        String dispatcherHost,
        String codebase)
        throws
            MalformedURLException,
            RemoteException,
            NotBoundException,
            ProblemComputeException {

        URL[] urls = new URL[1];
        urls[0] = new URL(codebase);

        return transmitProblem(problem, null, dispatcherHost, urls);
    }
    
    /**
     * Gibt die abschlie�ende Statistik des letzten Berechneten Problems.
     *
     * @return  die Statistik des letzten berechneten Problems, oder
     *          <code>null</code>, noch kein Problem berechnet wurde.
     */
    public ProblemStatistics getFinalProblemStat() {
        return finalProbStat;
    }

    /**
     * F�gt die �bergebenen URLs zur Codebase der JVM hinzu, falls diese
     * darin noch nicht enthalten sind.
     *
     * @param codebases  URLs, die zur Codebase hinzugef�gt werden sollen.
     */
    private static void addToCodebase(URL[] codebases) {
        String systemCodebase;
        boolean systemCodebaseChanged = false;

        synchronized (localProblemNumerator) {
            systemCodebase = System.getProperty("java.rmi.server.codebase");
            if (systemCodebase == null) {
                systemCodebase = "";
            }

            for (int i = 0; i < codebases.length; i++) {
                if (systemCodebase.indexOf(codebases[i].toString()) == -1) {
                    if (systemCodebase.length() > 0) {
                        systemCodebase += " ";
                    }
                    systemCodebase += codebases[i].toString();
                    systemCodebaseChanged = true;
                }
            }

            if (systemCodebaseChanged) {
                System.setProperty("java.rmi.server.codebase", systemCodebase);
            }
        }
    }

}
