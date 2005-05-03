/*
 * file:        ComputeManagerImpl.java
 * created:     <Erstellungsdatum>
 * last change: 17.04.2005 by Dietmar Lippold
 * developer:   Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
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


package de.unistuttgart.architeuthis.dispatcher.computemanaging;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de
    .unistuttgart
    .architeuthis
    .dispatcher
    .problemmanaging
    .ProblemManagerImpl;
import de
    .unistuttgart
    .architeuthis
    .dispatcher
    .statistic
    .SystemStatisticsCollector;
import de
    .unistuttgart
    .architeuthis
    .dispatcher
    .statistic
    .ProblemStatisticsCollector;
import de.unistuttgart.architeuthis.dispatcher.problemmanaging.ParProbWrapper;
import de.unistuttgart.architeuthis.systeminterfaces.ComputeManager;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.systeminterfaces.Operative;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreGenException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;

/**
 * Implementierung des ComputeManager. Dies ist eine ausführbare Anwendung, die
 * folgende Aufgabe erfüllen soll:
 * <ul>
 *      <li> Verteilung der Teilprobleme eines (Gesamt-)Problems an die
 *           Operative.</li>
 *      <li> Entgegennahme der berechneten Lösungen der Teilprobleme (auch
 *           Teillösung genannt) von den Operatives. </li>
 *      <li> Übergabe der Teillösungen an den ProblemManager.</li>
 *      <li> Ermittlung und Bereitsstellung allgemeiner statistischer Daten
 *           für den Endbenutzer. </li>
 * </ul>
 *
 * @author Jürgen Heit, Andreas Heydlauff, Dietmar Lippold
 */
public final class ComputeManagerImpl
    extends UnicastRemoteObject
    implements ComputeManager {

    /**
     * Anzahl der Millisekunden nach der eine fehlgeschlagene übermittlung von
     * "total solution" wiederholt wird.
     */
    private static final long REMOTE_FAIL_WAIT_TIMEOUT = 200;

    // Informationen über den Dispatcher
    /**
     * RMI Registry Binding des Compute-Managers.
     */
    private String bindingOperativeMan;

    /**
     * RMI Registry Binding des Problem-Managers.
     */
    private String bindingProbMan;

    // Verwaltung der Operatives
    /**
     * Liste der aktiven Operative, d.h. Operative, die ein
     * Teilproblem berechnen.
     */
    private List infosActiveStateOperatives =
        Collections.synchronizedList(new LinkedList());

    /**
     * Liste der passiven Operative, d.h. Operative, die kein
     * Teilproblem berechnen und für die Berechnung neuer Teilprobleme zur
     * Verfügung stehen.
     */
    private List infosPassiveStateOperatives =
        Collections.synchronizedList(new LinkedList());

    /**
     * Zu jedem Operative bzw. zu jeder zugehörigen Instanz der Klasse
     * <CODE>InfoOperative</CODE> ist ein Objekt gespeichert, das gelockt
     * wird, wenn dem Operative ein Teilproblem zur Berechnung gesandt wird.
     */
    private Map sendingParProbLocker =
        Collections.synchronizedMap(new HashMap());

    // Referenzen zu anderen Komponenten

    /**
     * Referenz auf den Problem-Manager.
     */
    private ProblemManagerImpl problemManager;

    /**
     * Referenz auf die InfoParProbWrapperQueue.
     */
    private InfoParProbWrapperQueue partProbQueue;

    /**
     * Referenz auf die Operative-Monitoring-Unit.
     */
    private OperativeMonitoringUnit operativeMonitoring;

    /**
     * Statistik über den Dispatcher
     */
    private SystemStatisticsCollector systemStatistics;

    /**
     * {@link java.util.logging.Logger} eingestellt auf
     * de.unistuttgart.architeuthis.dispatcher
     */
    private Logger LOGGER = Logger.getLogger(ComputeManagerImpl.class.getName());

    /**
     * Maximale Anzahl von Versuchen, einen Operative zu erreichen, bis dieser
     * als unerreichbar angesehen wird.
     */
    private long remoteOperativeMaxTries;

    /**
     * Gibt an, ob für bestimmte Aufrufe von Methoden eines Operatives ein
     * neuer Thread erzeugt werden sollen.
     */
    private boolean createNewThreads = false;

    /**
     * Gibt an, ob der Dienst dieses Objekts beendet wurde.
     */
    private boolean terminated = false;

    /**
     * Meldet <code>ComputeManager</code> und {@link ProblemManagerImpl} an einer
     * neuen RMI-Registry auf Port <code>port</code> an. Ferner wird ein Thread
     * gestartet, der die Verfügbarkeit von angemeldeten Operative überwacht.
     *
     * @param port  Portnummer der zu startenden RMI-Registry
     * @param additionalThreads gibt an, ob neue Threads für den
     *                          Aufruf des Operative verwendet werden sollen
     * @param operativeMaxTries maximale Anzahl der Versuche mit dem Operative
     *                          zu kommunizieren
     * @param operativeMonitoringInterval Zeitinterval in ms in dem der Operative
     *                                    überwacht wird
     *
     *
     * @throws UnknownHostException  Falls die IP-Adresse von localhost nicht
     *                               ermittelt werden konnte.
     * @throws MalformedURLException Falls der URL für die Anmeldung bei der
     *                               RMI-Registry nicht gültig war.
     * @throws AlreadyBoundException Falls der Port mit der übergebenen Nummer
     *                               schon benutzt wird.
     * @throws RemoteException       Falls die RMI-Registry nicht gestartet
     *                               oder keine Verbindung zu ihr aufgenommen
     *                               werden konnte.
     */
    public ComputeManagerImpl(
        int port,
        long operativeMonitoringInterval,
        long operativeMaxTries,
        boolean additionalThreads)
        throws UnknownHostException, RemoteException,
               MalformedURLException, AlreadyBoundException {

        InetAddress ipAddress;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        systemStatistics = new SystemStatisticsCollector();
        problemManager = new ProblemManagerImpl(this, systemStatistics);
        partProbQueue = new InfoParProbWrapperQueue();

        remoteOperativeMaxTries = operativeMaxTries;
        createNewThreads = additionalThreads;

        // Nachsehen, wie die Maschine heißt, auf der der ComputeManager läuft.
        ipAddress = InetAddress.getLocalHost();
        LocateRegistry.createRegistry(port);

		LOGGER.config("Dispatcher auf: " + ipAddress);
        bindingOperativeMan = "//"
                              + ipAddress.getHostName()
                              + ":"
                              + String.valueOf(port)
                              + "/"
                              + COMPUTEMANAGER_ID_STRING;
        bindingProbMan = "//"
                         + ipAddress.getHostName()
                         + ":"
                         + String.valueOf(port)
                         + "/"
                         + ProblemManager.PROBLEMMANAGER_ID_STRING;

        // ComputeManager und ProblemManager bei der RMI-Registry anmelden.
        Naming.bind(bindingOperativeMan, this);
        Naming.bind(bindingProbMan, problemManager);

        // Thread zum Erkennen nicht mehr erreichbarer Operatives starten.
        operativeMonitoring =
            new OperativeMonitoringUnit(
                this,
                remoteOperativeMaxTries,
                operativeMonitoringInterval,
                LOGGER);

        // Hier wird ein shutdownHook gesetzt. Dieser wird aufgerufen, wenn vom
        // System ein term-Signal kommt (also beim Beenden oder bei Strg+c).
        // Dann wird kurz aufgeräumt.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });

        LOGGER.info("Dispatcher gestartet.");
    }

    /**
     * Entfernt einen {@link Operative} aus der Liste der Operatives des
     * Teilproblems, das er berechnet. Wenn der Operative der letzte ist, der
     * das Teilproblem berechnet, wird das Teilproblem als abgerochen vermerkt.
     * Wenn der Operative kein Teilproblem berechnet, passiert nichts.
     *
     * @param operativeInfoObj  Info-Objekt des Operatives, der entfernt werden
     *                          soll.
     */
    private void removeOperativeFromParProb(InfoOperative operativeInfoObj) {
        InfoParProbWrapper parProbInfoObj;
        ParProbWrapper parProbWrap;

        synchronized (operativeInfoObj) {
            parProbInfoObj = operativeInfoObj.getInfoParProbWrapper();
            if (parProbInfoObj != null) {
                synchronized (parProbInfoObj) {
                    parProbInfoObj.removeOperativeInfo(operativeInfoObj);
                    parProbWrap = parProbInfoObj.getParProbWrapper();

                    if (parProbInfoObj.getOperativeInfos().isEmpty()) {
                        // Es war der letzte Operative, der dieses
                        // Teilproblem berechnet hat
                        partProbQueue.setAborted(parProbInfoObj);
                        parProbWrap.getProblemStatisticCollector()
                                   .abortTimeMeasurement(parProbWrap);
                    } else {
                        // Es wurde ein mehrfach verteiltes Teilproblem
                        // abgebrochen
                        parProbWrap.getProblemStatisticCollector()
                                   .decComputingOperatives();
                    }
                }
            }
        }
    }

    /**
     * Entfernt einen {@link Operative} aus den Listen, in denen er verwaltet
     * wird. Die Verbindungen zwischen dem Operative und dem Teilproblem, das
     * er möglicher Weise berechnet, werden nicht verändert.
     *
     * @param operativeInfoObj  Info-Objekt des Operatives, der entfernt werden
     *                          soll.
     */
    private void removeOperativeFromLists(InfoOperative operativeInfoObj) {
        boolean contained;

        synchronized (operativeInfoObj) {

            // ermitteln, ob der Operative schon von einem anderen Thread
            // entfernt wurde.
            synchronized (infosActiveStateOperatives) {
                synchronized (infosPassiveStateOperatives) {
                    contained =
                        (infosActiveStateOperatives.remove(operativeInfoObj)
                         || infosPassiveStateOperatives.remove(operativeInfoObj));
                    if (contained
                            && infosActiveStateOperatives.isEmpty()
                            && infosPassiveStateOperatives.isEmpty()) {
                        problemManager.reportException(
                            null,
                            null,
                            ExceptionCodes.NO_OPERATIVES_REGISTERED,
                            "Letzter Operative wird abgemeldet");
                    }
                }
            }

            // Falls der Operative noch nicht entfernt worden war, muß er
            // jetzt aus der Überwachung entfernt und die Statistik
            // aktualisiert werden.
            if (contained) {
                // locking-Objekt entfernen
                sendingParProbLocker.remove(operativeInfoObj);

                // aus OperativeMonitoringUnit austragen
                operativeMonitoring.stopMonitoring(operativeInfoObj.getOperative());

                // Statistik aktualisieren und LOGGER-Ausgabe
                systemStatistics.notifyOperativesUnregistration();
                LOGGER.info(operativeInfoObj.toString() + " wurde abgemeldet");
            }
        }
    }

    /**
     * Entfernt einen {@link Operative}, der nicht mehr erreichbar ist, aus
     * der Verwaltung.
     *
     * @param operativeInfoObj  Info-Objekt des Operatives, der nicht mehr
     *                          erreichbar ist.
     */
    private void removeDeadOperative(InfoOperative operativeInfoObj) {
        boolean contained;

        synchronized (operativeInfoObj) {
            LOGGER.info(
                operativeInfoObj.toString()
                    + " "
                    + ((RemoteObject) operativeInfoObj.getOperative())
                        .getRef()
                        .remoteToString()
                    + " antwortet nicht -> er wird entfernt");

            removeOperativeFromLists(operativeInfoObj);
        }
    }

    /**
     * Stoppt die Berechnung auf einem bestimmten {@link Operative} und löscht
     * die Verbindung zum Teilproblem im übergebenen Operative-Info-Objekt.
     *
     * @param operativeInfo Info-Objekt eines Operatives
     */
    private void stopComputationOnOperative(InfoOperative operativeInfo) {
        if (operativeInfo.getInfoParProbWrapper() != null) {
            LOGGER.info("Stoppe Berechnung auf " + operativeInfo.toString());
            synchronized (operativeInfo) {
                try {
// Für GC-Fehler auf Operative nachfolgende Zeile auskommentieren
                    LOGGER.fine("Versuche Berechnung auf "
                             + operativeInfo + " abzubrechen");
                    operativeInfo.getOperative().stopComputation();
                    LOGGER.fine("Berechnung auf " + operativeInfo
                             + " erfolgreich abgebrochen");
                } catch (RemoteException e1) {
                    LOGGER.warning(
                        "Konnte Berechnung auf "
                            + operativeInfo.toString()
                            + " nicht abbrechen");
                    // falls Operative beim Stoppen nicht erreichbar war,
                    // entferne ihn, ohne lang zu "fackeln".
                    removeDeadOperative(operativeInfo);
                }
                operativeInfo.setInfoParProbWrapper(null);
            }
        }
    }

    /**
     * Versucht, falls nötig mehrfach, einem {@link Operative} ein
     * Teilproblem ({@link PartialProblem}) zur Berechnung zu übergeben. Falls
     * das nicht möglich war, wird ermittelt, ob die Ursache dafür beim
     * Teilproblem oder beim Operative lag und je nachdem wird entweder das
     * Teilproblem oder der Operative entfernt.
     *
     * @param partProbInfoObj   InfoObjekt des Teilproblems.
     * @param operativeInfoObj  InfoObjekt des Operatives.
     *
     * @return  <code>true</code>, wenn das Teilproblem dem Operative
     *          übergeben werden konnte, anderenfalls <code>false</code>.
     */
    private boolean sendPartialProblemToOperative(InfoParProbWrapper partProbInfoObj,
                                                  InfoOperative operativeInfoObj) {
        RemoteStoreGenerator generator;
        RemoteStore centralRemoteStore;
        String exceptionMessage = null;
        int exceptionCode = -1;
        boolean transmitted = false;

        synchronized (sendingParProbLocker.get(operativeInfoObj)) {
            synchronized (operativeInfoObj) {
                synchronized (partProbInfoObj) {

                    long tries = 0;
                    ParProbWrapper parProbWrap = partProbInfoObj.getParProbWrapper();
                    Operative operative = operativeInfoObj.getOperative();
                    // Teilproblem versuchen zu senden
                    while (!transmitted && (tries < remoteOperativeMaxTries)) {
                        tries++;
                        try {
                            LOGGER.fine("Versuche Teilproblem an "
                                     + operativeInfoObj + " zu senden");

                            centralRemoteStore = parProbWrap.getCentralRemoteStore();
                            LOGGER.fine("RemoteStore erhalten!");

                            generator = parProbWrap.getRemoteStoreGenerator();
                            LOGGER.fine("RemoteStoreGenerator erhalten!");

                            operative.fetchPartialProblem(parProbWrap.getPartialProblem(),
                                    centralRemoteStore, generator);
                            LOGGER.fine("Teilproblem erfolgreich an "
                                     + operativeInfoObj + " gesendet");
                            transmitted = true;
                        } catch (RemoteException e) {
                            LOGGER.fine("Senden des Teilproblems an "
                                     + operativeInfoObj + " fehlgeschlagen");
                            exceptionCode = ExceptionCodes.PARTIALPROBLEM_SEND_EXCEPTION;
                            exceptionMessage = e.toString();
                            try {
                                Thread.sleep(REMOTE_FAIL_WAIT_TIMEOUT);
                            } catch (InterruptedException e1) {
                                // Unterbrechung sollte hier nicht stören
                            }
                            continue;
                        } catch (RemoteStoreGenException e) {
                            LOGGER.info("Erzeugung von RemoteStore fehlgeschlagen");
                            exceptionCode = ExceptionCodes.REMOTE_STORE_GEN_EXCEPTION;
                            exceptionMessage = e.toString();
                        } catch (RemoteStoreException e) {
                            LOGGER.info("Anmeldung von RemoteStore fehlgeschlagen");
                            exceptionCode = ExceptionCodes.REMOTE_STORE_EXCEPTION;
                            exceptionMessage = e.toString();
                        } catch (ProblemComputeException e) {
                            LOGGER.severe("<E> "
                                       + operativeInfoObj.toString()
                                       + " ist bereits beschäftigt.");
                            exceptionCode = ExceptionCodes.PARTIALPROBLEM_SEND_EXCEPTION;
                            exceptionMessage = e.toString();
                        } catch (RuntimeException e) {
                            LOGGER.fine("Senden des Teilproblems an "
                                     + operativeInfoObj + " fehlgeschlagen");
                            exceptionCode = ExceptionCodes.PARTIALPROBLEM_SEND_EXCEPTION;
                            exceptionMessage = e.toString();
                            tries = remoteOperativeMaxTries;
                        }
                    }

                    if (transmitted) {
                        // Referenzen setzen falls Teilproblem erfolgreich an
                        // Operative übermittelt wurde.

                        InfoParProbWrapper pp =
                            operativeInfoObj.getInfoParProbWrapper();
                        if (pp != null) {
                            LOGGER.severe(
                                "<E> "
                                    + operativeInfoObj.toString()
                                    + " "
                                    + partProbInfoObj.toString()
                                    + " ist ein anderes");
                            LOGGER.severe("nämlich " + pp.toString());
                        }
                        operativeInfoObj.setInfoParProbWrapper(partProbInfoObj);

                        if (partProbInfoObj.getOperativeInfos()
                                           .contains(operativeInfoObj)) {
                            LOGGER.severe(
                                "<E> "
                                    + partProbInfoObj.toString()
                                    + " "
                                    + operativeInfoObj.toString()
                                    + " schon drin");
                        }
                        partProbInfoObj.addOperativeInfo(operativeInfoObj);
                    } else {
                        // Ermittlen, ob die fehlgeschlagene Übertragung vom
                        // Teilproblem oder am Operative verursacht wurde und je
                        // nachdem das Teilproblem oder den Operative entfernen
                        try {
                            if (operativeInfoObj.getOperative().isReachable()) {
                                // Die Ursache lag beim Teilproblem
                                problemManager.reportException(
                                    null,
                                    partProbInfoObj.getParProbWrapper(),
                                    exceptionCode,
                                    exceptionMessage);
                            } else {
                                // Die Ursache lag beim Operative
                                removeDeadOperative(operativeInfoObj);
                            }
                        } catch (RemoteException e) {
                            // Die Ursache lag beim Operative
                            removeDeadOperative(operativeInfoObj);
                        }
                    }
                }
            }
        }
        return transmitted;
    }

    /**
     * Versucht, einem unbeschäftigten aber aktiver {@link Operative} ein
     * Teilproblem ({@link PartialProblem}) zuzuweisen. Dazu wird zuerst
     * versucht, ihm ein abgebrochenes Teilproblem, dann ein neues Teilproblem
     * und schließlich ein in Berechnung befindliches Teilproblem zuzuweisen.
     * Wenn keines dieser Teilprobleme existiert, wird der Operative passiv
     * gesetzt.
     *
     * @param operativeInfoObj  unbeschäftigten aber aktiver {@link Operative},
     *                          der ein Teilproblem erhalten soll.
     */
    private void distributePartialProblem(InfoOperative operativeInfoObj) {
        InfoParProbWrapper partProbInfo = null;
        ParProbWrapper parProbWrap;
        boolean succeeded;

        synchronized (operativeInfoObj) {
            // Überprüfen, ob der Operative in der Zwischenzeit nicht entfernt
            // wurde
            if (infosActiveStateOperatives.contains(operativeInfoObj)) {
                LOGGER.info("distributePartialProblem für "
                         + operativeInfoObj.toString());
                do {
                    succeeded = false;

                    // Versuche dem Operative ein abgebrochenes Teilproblem
                    // zuzuweisen
                    partProbInfo = partProbQueue.getPreferedPartialProblemInfo();
                    if (partProbInfo != null) {
                        synchronized (partProbInfo) {
                            // Prüfen, ob das Teilproblem in der Zwischenzeit
                            // nicht von einem anderen Operative berechnet
                            // wird oder sogar schon fertig berechnet wurde
                            if ((partProbInfo.getOperativeInfos().size() == 0)
                                && (partProbQueue
                                        .isEnqueuedPartProbInfo(partProbInfo))) {
                                succeeded =
                                    sendPartialProblemToOperative(
                                        partProbInfo,
                                        operativeInfoObj);
                                if (succeeded) {
                                    parProbWrap = partProbInfo.getParProbWrapper();
                                    parProbWrap.getProblemStatisticCollector()
                                               .startTimeMeasurement(parProbWrap);
                                    LOGGER.info(
                                        "abgebrochenes "
                                            + partProbInfo.toString()
                                            + " an "
                                            + operativeInfoObj.toString()
                                            + " wieder vergeben.");
                                } else {
                                    // Sicherstellen, dass das Teilproblem
                                    // nicht wegen Fehler entfernt wurde
                                    if (partProbQueue
                                        .isEnqueuedPartProbInfo(partProbInfo)) {
                                        partProbQueue.setAborted(partProbInfo);
                                    }
                                }
                            }
                        }
                    } else {
                        // Versuche dem Operative ein neues Teilproblem zuzuweisen
                        synchronized (partProbQueue) {
                            parProbWrap = problemManager.getParProbWrapper();
                            if (parProbWrap != null) {
                                partProbInfo = new InfoParProbWrapper(parProbWrap);
                                partProbQueue.enqueuePartProbInfo(partProbInfo);
                            }
                        }
                        if (partProbInfo != null) {
                            synchronized (partProbInfo) {
                                // Prüfen, ob das Teilproblem in der Zwischenzeit
                                // nicht von einem anderen Operative berechnet
                                // wird oder sogar schon fertig berechnet wurde
                                if ((partProbInfo.getOperativeInfos().size() == 0)
                                    && (partProbQueue
                                            .isEnqueuedPartProbInfo(partProbInfo))) {
                                    succeeded =
                                        sendPartialProblemToOperative(
                                            partProbInfo,
                                            operativeInfoObj);
                                    if (succeeded) {
                                        parProbWrap.getProblemStatisticCollector()
                                                   .startTimeMeasurement(parProbWrap);
                                        LOGGER.info(
                                            "neues "
                                                + partProbInfo.toString()
                                                + " an "
                                                + operativeInfoObj.toString()
                                                + " vergeben");
                                    } else {
                                        // Sicherstellen, dass das Teilproblem
                                        // nicht wegen Fehler entfernt wurde
                                        if (partProbQueue
                                            .isEnqueuedPartProbInfo(partProbInfo)) {
                                            partProbQueue.setAborted(partProbInfo);
                                        }
                                    }
                                }
                            }
                        } else {
                            // Versuche dem Operative ein schon in Berechnung
                            // befindliches Teilproblem zuzuweisen
                            // (Teilproblem-Mehrfachvergabe)
                            partProbInfo = partProbQueue.rotatePartProbInfo();
                            if (partProbInfo != null) {
                                synchronized (partProbInfo) {
                                    // Prüfen, ob das Teilproblem in der
                                    // Zwischenzeit nicht von einem anderen
                                    // Operative fertig berechnet wurde
                                    if (partProbQueue
                                        .isEnqueuedPartProbInfo(partProbInfo)) {
                                        succeeded =
                                            sendPartialProblemToOperative(
                                                partProbInfo,
                                                operativeInfoObj);
                                        if (succeeded) {
                                            parProbWrap = partProbInfo.getParProbWrapper();
                                            parProbWrap.getProblemStatisticCollector()
                                                       .incComputingOperatives();
                                            LOGGER.info(
                                                "altes "
                                                    + partProbInfo.toString()
                                                    + " an "
                                                    + operativeInfoObj.toString()
                                                    + " mehrfachvergeben.");
                                        } else {
                                            // Sicherstellen, dass das Teilproblem
                                            // nicht wegen Fehler entfernt wurde
                                            if (partProbQueue
                                                .isEnqueuedPartProbInfo(partProbInfo)) {
                                                partProbQueue.undoRotation(partProbInfo);
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Dereit ist kein Teilproblem zur Berechnung
                                // vorhanden. Operative passiv setzen.
                                synchronized (infosActiveStateOperatives) {
                                    synchronized (infosPassiveStateOperatives) {
                                        infosActiveStateOperatives.remove(
                                            operativeInfoObj);
                                        infosPassiveStateOperatives.add(
                                            operativeInfoObj);
                                        operativeInfoObj.setActive(false);
                                        LOGGER.info(
                                            operativeInfoObj.toString()
                                                + " ist jetzt passiv");
                                    }
                                }
                            }
                        }
                    }
                } while ((!succeeded) && (partProbInfo != null)
                         && infosActiveStateOperatives.contains(operativeInfoObj));
            }
        }
    }

    /**
     * Versucht, jedem {@link Operative} der übergebenen <code>Collection</code>
     * unbeschäftigter aber aktiver Operatives ein Teilproblem
     * ({@link ParProbWrapper}) zuzuweisen. Operatives, bei denen das nicht
     * möglich war, werden passiv gesetzt.
     *
     * @param operatives  Collection unbeschäftigter aber aktiver Operatives
     * @see  #distributePartialProblem(InfoOperative)
     */
    private void distributePartialProblem(Collection operatives) {
        Object[] operativeArray;
        InfoOperative operativeInfoObj;

        synchronized (operatives) {
            operativeArray = operatives.toArray();
        }
        for (int i = 0; i < operativeArray.length; i++) {
            operativeInfoObj = (InfoOperative) operativeArray[i];
            if (createNewThreads) {
                final InfoOperative opInfo = operativeInfoObj;
                new Thread() {
                    private InfoOperative infoOperative;
                    public void run() {
                        distributePartialProblem(opInfo);
                    }
                }
                .start();
            } else {
                distributePartialProblem(operativeInfoObj);
            }
        }
    }

    /**
     * Wird vom ProblemManager aufgerufen um den ComputeManager zu reaktivieren
     * falls zuvor nichts mehr berechnet wurde. Ist der ComputeManager
     * beschäfttig, so bleibt der Aufruf dieser Methode ohne Wirkung.
     */
    public synchronized void reactivatePassiveOperatives() {
        List activeOperatives = new LinkedList();
        List passiveOperatives = new LinkedList();
        boolean removed;

        synchronized (infosActiveStateOperatives) {
            synchronized (infosPassiveStateOperatives) {
                activeOperatives.addAll(infosActiveStateOperatives);
                passiveOperatives.addAll(infosPassiveStateOperatives);
            }
        }

        // reaktiviere alle passiven Operatives
        Iterator passiveOpIter = passiveOperatives.iterator();
        while (passiveOpIter.hasNext()) {
            InfoOperative infoOp = (InfoOperative) passiveOpIter.next();
            synchronized (infoOp) {
                infoOp.setActive(true);
                synchronized (infosActiveStateOperatives) {
                    synchronized (infosPassiveStateOperatives) {
                        removed = infosPassiveStateOperatives.remove(infoOp);
                        if (removed) {
                            // der Operative ist zwischenzeitlich nicht
                            // abgemeldet worden
                            infosActiveStateOperatives.add(infoOp);
                        }
                    }
                }
            }
        }
        distributePartialProblem(passiveOperatives);
        passiveOperatives.clear();

        // überprüfe welche Operatives in der Zwischenzeit inaktiv
        // geworden sind.
        Iterator activeOpIter = activeOperatives.iterator();
        while (activeOpIter.hasNext()) {
            InfoOperative infoOp = (InfoOperative) activeOpIter.next();
            synchronized (infoOp) {
                if (!infoOp.isActive()) {
                    infoOp.setActive(true);
                    synchronized (infosActiveStateOperatives) {
                        synchronized (infosPassiveStateOperatives) {
                            removed = infosPassiveStateOperatives.remove(infoOp);
                            if (removed) {
                                // der Operative ist zwischenzeitlich nicht
                                // abgemeldet worden
                                infosActiveStateOperatives.add(infoOp);
                                passiveOperatives.add(infoOp);
                            }
                        }
                    }
                }
            }
        }
        distributePartialProblem(passiveOperatives);

        // starte Operative-Überwachung, falls noch nicht aktiv
        if (!operativeMonitoring.isAlive()) {
            operativeMonitoring.start();
        }
    }

    /**
     * Liefert zum übergebenen <code>Operative</code> das zugehörige
     * Operative-Info-Objekt. Ist der Operative nicht registiert oder ist der
     * Übergabeparameter <code>null</code>, so wird <code>null</code>
     * zurückgeliefert.
     *
     * @param operative  {@link Operative} zu dem das Operative-Info-Objekt
     *                   geliefert werden soll.
     * @return  zum <code>operative</code> gehöriges Info-Objekt
     */
    private InfoOperative findOperativeInfo(Operative operative) {
        boolean operativeFound = false;
        InfoOperative operativeInfoObj = null;
        Iterator operativeIter;

        synchronized (infosActiveStateOperatives) {
            synchronized (infosPassiveStateOperatives) {
                if (infosActiveStateOperatives.isEmpty()
                        && infosPassiveStateOperatives.isEmpty()) {
                    return null;
                } else {
                    // suche zunächst in Collection der aktiven Operatives...
                    operativeIter = infosActiveStateOperatives.iterator();
                    while (operativeIter.hasNext() && !operativeFound) {
                        operativeInfoObj = (InfoOperative) operativeIter.next();
                        if (operative.equals(operativeInfoObj.getOperative())) {
                            operativeFound = true;
                        }
                    }
                }
                if (!operativeFound) {
                    // ... falls nicht gefunden, suche in Collection der
                    // der passiven Operatives
                    operativeIter = infosPassiveStateOperatives.iterator();
                    while (operativeIter.hasNext() && !operativeFound) {
                        operativeInfoObj = (InfoOperative) operativeIter.next();
                        if (operative
                            .equals(operativeInfoObj.getOperative())) {
                            operativeFound = true;
                        }
                    }
                }
            }
        }
        if (operativeFound) {
            return operativeInfoObj;
        } else {
            LOGGER.fine("Operative-Info-Objekt nicht gefunden.");
            return null;
        }
    }

    /**
     * Überprüft genau einmal, ob ein Operative erreichbar ist. Falls nicht, so
     * wird er aus der Verwaltung entfernt. Falls er erreichbar ist, so wird er
     * nicht aus der Verwaltung entfernt.
     *
     * @param operative  zu überprüfender {@link Operative}
     */
    void verifyOperativeReachability(Operative operative) {
        InfoOperative operativeInfoObj = findOperativeInfo(operative);
        if (operativeInfoObj != null) {
            try {
                // gebe Operative noch eine Chance zu anworten
                operative.isReachable();
            } catch (RemoteException e) {
                removeOperativeFromParProb(operativeInfoObj);
                removeDeadOperative(operativeInfoObj);
                LOGGER.warning(operativeInfoObj + " ist nicht erreichbar"
                                             + " und wurde entfernt.");
            }
        }
    }

    /**
     * Registrierung eines Operative am ComputeManager.
     * Diese Methode ruft ein {@link Operative} auf, der sich beim
     * {@link ComputeManager} anmelden möchte.
     *
     * @param  operative Referenz auf den Operative
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     */
    public synchronized void registerOperative(Operative operative)
        throws RemoteException {

        if (!terminated) {
            // Verhindern, dass nicht ereichbare Operative sich anmelden können
            boolean reachable = false;
            try {
                reachable = operative.isReachable();
            } catch (RemoteException re) {
                reachable = false;
                LOGGER.warning("Operative ist bei Anmeldung nicht erreichbar");
            }

            if (reachable) {
                // ermitteln, ob bisher keine Operatives mehr angemeldet waren
                synchronized (infosActiveStateOperatives) {
                    synchronized (infosPassiveStateOperatives) {
                        if (infosActiveStateOperatives.isEmpty()
                                && infosPassiveStateOperatives.isEmpty()) {
                            problemManager.reportException(
                                null,
                                null,
                                ExceptionCodes.NEW_OPERATIVES_REGISTERED,
                                "Neuer Operative hat sich angemeldet");
                        }
                    }
                }

                // neues Objekt wird am Ende der Liste eingefügt
                InfoOperative operativeInfoObj = new InfoOperative(operative);
                infosActiveStateOperatives.add(operativeInfoObj);
                sendingParProbLocker.put(operativeInfoObj, new Object());

                // Statistik aktualisieren und LOGGER-Ausgabe
                systemStatistics.notifyOperativesRegistration();
                LOGGER.fine(operativeInfoObj.toString()
                         + " erfolgreich angemeldet.");

                if (!operativeMonitoring.isAlive()) {
                    operativeMonitoring.start();
                }

                // in Operative-Monitoring-Unit eintragen
                operativeMonitoring.startMonitoring(operative);

                // ein Teilproblem zuweisen
                distributePartialProblem(operativeInfoObj);
            }
        }
    }

    /**
     * Entfernt den Operative des übergebenen Info-Objekts komplett aus der
     * Verwaltung. So wird er in jedem Fall aus den Listen aller Operatives
     * entfernt. Berechnet er ein Teilproblem, wird er außerdem aus der Liste
     * der dieses Teilproblem berechnenden Operatives entfernt. Der Operative
     * selbst muß auf andere Weise beendet werden.
     *
     * @param  operativeInfoObj  Das Info-Objekt des Operatives, der abgemeldet
     *                           werden soll.
     */
    private void unregisterOperative(InfoOperative operativeInfoObj) {
        synchronized (operativeInfoObj) {
            // Wenn der Operative beschäftigt ist, ihn aus der Verwaltung des
            // berechneten Teilproblems entfernen
            removeOperativeFromParProb(operativeInfoObj);

            // Den Operative jetzt aus den Listen aller Operatives entfernen
            removeOperativeFromLists(operativeInfoObj);
        }
    }

    /**
     * Entfernt den übergebenen Operative komplett aus der Verwaltung. So wird
     * er in jedem Fall aus den Listen aller Operatives entfernt. Berechnet er
     * ein Teilproblem, wird er außerdem aus der Liste der dieses Teilproblem
     * berechnenden Operatives entfernt. Der Operative selbst muß auf andere
     * Weise beendet werden.
     *
     * @param  operative  Referenz auf Operative
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     */
    public void unregisterOperative(Operative operative)
        throws RemoteException {

        // Zugehöriges Operative-Info-Object ermitteln
        InfoOperative operativeInfoObj = findOperativeInfo(operative);
        if (operativeInfoObj == null) {
            LOGGER.severe(
                "<E> Unbekannter Operative hat versucht sich abzumelden.");
            throw new RemoteException(
                "Unbekannter Operative hat versucht sich abzumelden.");
        }
        LOGGER.fine(operativeInfoObj.toString() + " meldet sich ab.");
        unregisterOperative(operativeInfoObj);
    }

    /**
     * Teilt dem {@link ProblemManager} mit, dass der <code>ComputeManager</code>
     * beendet wird. Der {@link ProblemManager} wird hierdurch aufgefordert,
     * alle Probleme ({@link Problem}) zu entfernen und den jeweiligen
     * Problemübermittlern ({@link ProblemTransmitter}) eine entsprechende
     * Nachricht zu schicken. Außerdem werden alle Berechnungen der Operative
     * ({@link Operative}) gestoppt.
     */
    private synchronized void shutdown() {
        terminated = true;

        // Operative-Überwachung beenden
        LOGGER.info("Stoppe die Ueberwachung der Operatives...");
        operativeMonitoring.terminate();

        // Berechnungen auf allen Operatives beenden
        LOGGER.info("Melde die angemeldeten Operatives ab ...");
        List operativeList = new LinkedList();
        synchronized (infosActiveStateOperatives) {
            synchronized (infosPassiveStateOperatives) {
                operativeList.addAll(infosActiveStateOperatives);
                operativeList.addAll(infosPassiveStateOperatives);
            }
        }
        Iterator opInfoIt = operativeList.iterator();
        InfoOperative opInfo;
        while (opInfoIt.hasNext()) {
            // Den Operative beenden
            opInfo = (InfoOperative) opInfoIt.next();
            try {
                opInfo.getOperative().doExit();
                LOGGER.info(opInfo.toString() + " wurde beendet.");
            } catch (RemoteException e1) {
                LOGGER.warning(opInfo.toString() + " konnte nicht beendet werden.");
            }

            // Den Operative in jedem Fall aus der Verwaltung entfernen
            unregisterOperative(opInfo);
        }

        // Problem-Übermittler benachrichtigen
        problemManager.reportException(
            null,
            null,
            ExceptionCodes.DISPATCHER_SHUTDOWN,
            "Administratives Beenden.");

        // Aus der RMI-Registry austragen
        try {
            Naming.unbind(bindingOperativeMan);
            Naming.unbind(bindingProbMan);
        } catch (Exception e) {
            LOGGER.warning(
                "Austragen der Dienste aus der "
                    + "RMI-Registry fehlgeschlagen.");
        }
        LOGGER.info("Dispatcher beendet.");
    }

    /**
     * Bricht die Berechnung eines eventuell mehrfach verteilten Teilproblems
     * auf allen {@link Operative}s ab, die dieses Teilproblem berechnen, ab.
     * Beim ersten Operative kann außerdem die Zeitmessung für das Teilproblem
     * abgebrochen werden. Den Operatives werden neue Teilprobleme zugewiesen.
     *
     * @param partProbInfo     abzubrechendes Teilproblem
     * @param abortCompletely  gibt an, dass die Zeitmessung für das angegebene
     *                         Teilproblem noch abgebrochen werden muss und
     *                         veranlasst, dass die Methode erst nach dem
     *                         Abbruch aller Teilprobleme beendet wird, also
     *                         keine Threads erzeugt werden.
     */
    private void abortPartialProblem(InfoParProbWrapper partProbInfo,
                                     boolean abortCompletely) {
        ParProbWrapper parProbWrap;
        ProblemStatisticsCollector probStatCollector;
        InfoOperative opInfo;
        List abortOperativesCopy;

        if (partProbInfo != null) {
            synchronized (partProbInfo) {
                LOGGER.info(
                    "breche Berechnungen für "
                        + partProbInfo.toString()
                        + " ab");

                abortOperativesCopy = partProbInfo.getOperativeInfos();
            }

            parProbWrap = partProbInfo.getParProbWrapper();
            probStatCollector = parProbWrap.getProblemStatisticCollector();

            Iterator operativeIter = abortOperativesCopy.iterator();
            while (operativeIter.hasNext()) {
                opInfo = (InfoOperative) operativeIter.next();
                synchronized (opInfo) {
                    probStatCollector.abortTimeMeasurement(parProbWrap);

                    // Berechnung auf Operative abbechen und ihm neues
                    // Teilproblem zuweisen
                    if (createNewThreads && (!abortCompletely)) {
                        final InfoOperative finalOpInfo = opInfo;
                        new Thread() {
                            public void run() {
                                stopComputationOnOperative(finalOpInfo);
                                distributePartialProblem(finalOpInfo);
                            }
                        }
                        .start();
                    } else {
                        stopComputationOnOperative(opInfo);
                        distributePartialProblem(opInfo);
                    }

                    // Verbindung von Teilproblem zu Operative lösen
                    partProbInfo.removeOperativeInfo(opInfo);
                }
            }

        } else {
            LOGGER.severe("<E> Abzubrechendes Teilproblem ist \"null-Objekt\"");
        }
    }

    /**
     * Bricht die Berechnung eines eventuell mehrfach verteilten Teilproblems
     * auf allen {@link Operative}s ab, die dieses Teilproblem berechnen, ab.
     * Diesen Operatives werden neue Teilprobleme zugewiesen. Außerdem wird die
     * Zeitmessung für das Teilproblem abgebrochen.
     *
     * @param partProbWrap  abzubrechender Teilproblem-Wrapper
     */
    public void abortComputation(ParProbWrapper partProbWrap) {
        InfoParProbWrapper partProbInfoObj;

        partProbInfoObj = partProbQueue.removeParProbWrapper(partProbWrap);
        // partProbInfoObj ist nur dann ungleich null, wenn der Operative
        // nicht schon eine Teillösung oder Exception liefert.
        // Da die Methoden beim Operative synchronisiert sind, darf die
        // Berechnung für einen Operative, der eine Teillösung oder Exception
        // liefert, nicht abgebrochen werden.
        if (partProbInfoObj != null) {
            abortPartialProblem(partProbInfoObj, true);
        }
    }

    /**
     * Diese Remote-Methode wird von einem Operative aufgerufen um dem
     * ComputeManager eine berechnete Teillösung zu übermitteln.
     *
     * @param  parSol     Teillösungsobjekt
     * @param  operative  Referenz auf den Operative der die Berechnung
     *                    durchgeführt hat
     * @throws RemoteException  RMI RemoteException wird bei Netzproblemen
     *                          geworfen.
     */
    public void returnPartialSolution(PartialSolution parSol,
                                      Operative operative)
        throws RemoteException {

        InfoOperative operativeInfoObj = findOperativeInfo(operative);
        InfoParProbWrapper partProbInfoObj = null;
        ParProbWrapper parProbWrap;
        boolean inList = false;

        // nur Lösung annehmen, wenn Operative registriert ist;
        // von Operatives, die nicht erreichbar waren und aus der
        // Verwaltung entfernt wurden wird keine Teillösung angenommen
        if (operativeInfoObj != null) {
            // Testen, ob das vom Operative berechnete Teilproblem in der
            // Liste der aktuell berechneten Teilprobleme enthalten ist.

            // Das Teilproblem kann erst ermittelt werden, wenn das Senden
            // des Teilproblems an den Operative abgeschlossen ist.
            synchronized (sendingParProbLocker.get(operativeInfoObj)) {
                partProbInfoObj = operativeInfoObj.getInfoParProbWrapper();
            }

            if (partProbInfoObj != null) {
                LOGGER.info(
                    operativeInfoObj.toString()
                        + " liefert Teillösung zu "
                        + partProbInfoObj.toString());
            }

            inList = partProbQueue.removePartProbInfo(partProbInfoObj);
            // inList ist nur true für den ersten Thread, der eine Teillösung
            // eines eventuell mehrfach verteilten Teilproblems verarbeitet.
            // Da die Methoden beim Operative synchronisiert sind, darf die
            // Berechnung für einen Operative, der eine Teillösung oder
            // Exception liefert, nicht abgebrochen werden.
            if (inList) {
                synchronized (operativeInfoObj) {
                    synchronized (partProbInfoObj) {
                        // Statistik aktualisieren
                        parProbWrap = partProbInfoObj.getParProbWrapper();
                        parProbWrap.getProblemStatisticCollector()
                                   .stopTimeMeasurement(parProbWrap);

                        // vermerke, dass der Operative, der die Teillösung
                        // zurückliefert, das Teilproblem nicht mehr berechnet
                        partProbInfoObj.removeOperativeInfo(operativeInfoObj);
                        operativeInfoObj.setInfoParProbWrapper(null);

                        // breche das Teilproblem auf anderen Operatives, die
                        // es noch berechnet haben, ab
                        abortPartialProblem(partProbInfoObj, false);

                        problemManager.collectPartialSolution(
                            parSol,
                            partProbInfoObj.getParProbWrapper());

                        // weise dem Operative, der die Teillösung
                        // zurückliefert, ein neues Teilproblem zu
                        distributePartialProblem(operativeInfoObj);
                    }
                }
            }
        }
    }

    /**
     * Ausnahmen bei der Arbeit von Operatives werden über diese Methode an
     * den Compute-Manager gemeldet.
     *
     * @param operative         Referenz auf den Operative, auf dem der Fehler
     *                          auftrat.
     * @param exceptionCode     Integerwert, der die Ausnahme charakterisisert.
     * @param exceptionMessage  Fehlermeldung um die Ausnahme näher zu beschreiben.
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     */
    public void reportException(Operative operative,
                                int exceptionCode,
                                String exceptionMessage)
        throws RemoteException {

        InfoOperative operativeInfoObj = findOperativeInfo(operative);
        InfoParProbWrapper partProbInfoObj = null;
        ParProbWrapper parProbWrap;
        boolean inList;

        // nur Exception annehmen, wenn Operative registriert ist;
        // von Operatives, die nicht erreichbar waren und aus der
        // Verwaltung entfernt wurden wird keine Exception angenommen
        if (operativeInfoObj != null) {
            // Testen, ob das vom Operative berechnete Teilproblem in der
            // Liste der aktuell berechneten Teilprobleme enthalten ist.

            // Das Teilproblem kann erst ermittelt werden, wenn das Senden
            // des Teilproblems an den Operative abgeschlossen ist.
            synchronized (sendingParProbLocker.get(operativeInfoObj)) {
                partProbInfoObj = operativeInfoObj.getInfoParProbWrapper();
            }

            if (partProbInfoObj != null) {
                LOGGER.severe(
                    operativeInfoObj.toString()
                        + " meldet Fehler bei "
                        + partProbInfoObj.toString());
            }

            inList = partProbQueue.removePartProbInfo(partProbInfoObj);
            // inList ist nur true für den ersten Thread, der eine Exception
            // eines eventuell mehrfach verteilten Teilproblems verarbeitet.
            // Da die Methoden beim Operative synchronisiert sind, darf die
            // Berechnung für einen Operative, der eine Teillösung oder
            // Exception liefert, nicht abgebrochen werden.
            if (inList) {
                synchronized (operativeInfoObj) {
                    synchronized (partProbInfoObj) {
                        // Statistik aktualisieren
                        parProbWrap = partProbInfoObj.getParProbWrapper();
                        parProbWrap.getProblemStatisticCollector()
                                   .abortTimeMeasurement(parProbWrap);

                        // vermerke, dass der Operative, der die Exception
                        // gemeldet hat, das Teilproblem nicht mehr berechnet
                        partProbInfoObj.removeOperativeInfo(operativeInfoObj);
                        operativeInfoObj.setInfoParProbWrapper(null);

                        // breche das Teilproblem auf anderen Operatives, die
                        // es noch berechnen, ab
                        abortPartialProblem(partProbInfoObj, false);

                        problemManager.reportException(
                            null,
                            partProbInfoObj.getParProbWrapper(),
                            exceptionCode,
                            exceptionMessage);

                        // weise dem Operative, der die Exception gemeldet
                        // hat, ein neues Teilproblem zu
                        distributePartialProblem(operativeInfoObj);
                    }
                }
            }
        }
    }
}
