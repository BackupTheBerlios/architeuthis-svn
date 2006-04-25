/*
 * file:        OperativeMonitoringUnit.java
 * created:     12.08.2003
 * last change: 25.04.2006 von Dietmar Lippold
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

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.systeminterfaces.Operative;

/**
 * Überprüft in regelmäßigen Zeitabständen die Erreichbar von
 * {@link de.unistuttgart.architeuthis.interfaces.Operative}s, die sich beim
 * {@link de.unistuttgart.architeuthis.interfaces.ComputeManager} angemeldet haben.
 *
 * @author Jürgen Heit, Dietmar Lippold
 */
class OperativeMonitoringUnit extends Thread {

    /**
     * Referenz auf den Compute-Manager, der für die Verwaltung der
     * Operatives zuständig ist.
     */
    private ComputeManagerImpl computeManager;

    /**
     * Für jeden Operative als Key ist als Value die Anzahl der bisherigen
     * fehlgeschlagenen Versuche, den Operative zu erreichen, gespeichert.
     */
    private Hashtable operativesTries = new Hashtable();

    /**
     * Sobald <code>threadTerminated</code> auf <code>true</code> gesetzt wird,
     * beendet sich der Operative-Monitoring-Thread.
     */
    private volatile boolean threadTerminated;

    /**
     * Maximale Anzahl der Versuche einem Operative zu erreichen.
     */
    private long operativeReachableMaxTries;

    /**
     * Minimale Zeitdauer in Millisekunden nach der der
     * {@link Operative}-Erreichbarkeitstest erneut durchgeführt wird.
     */
    private long minOperativeMonitoringMillis;

    /**
     * {@link java.util.logging.Logger} eingestellt auf
     * de.unistuttgart.architeuthis.dispatcher
     */
    private static final Logger LOGGER
        = Logger.getLogger(OperativeMonitoringUnit.class.getName());


    /**
     * Initialisert den Operative-Erreichbarkeitstest.
     *
     * @param compMan           Referenz auf ComputeManager. Bei ihm melden
     *                          sich die Operatives an.
     * @param reachMaxTries     Maximale Anzahl der Versuche einem Operative zu
     *                          erreichen.
     * @param monitoringMillis  Minimale Zeitdauer in Millisekunden nach der der
     *                          {@link Operative}-Erreichbarkeitstest erneut
     *                          durchgeführt wird.
     */
    OperativeMonitoringUnit(
        ComputeManagerImpl compMan,
        long reachMaxTries,
        long monitoringMillis) {

        threadTerminated = false;
        operativeReachableMaxTries = reachMaxTries;
        minOperativeMonitoringMillis = monitoringMillis;
        computeManager = compMan;
    }

    /**
     * Fügt einen {@link Operative} zur
     * Operative-Erreichbarkeitsüberwachung hinzu. Die Erreichbarkeit des
     * Operatives wird erst beim nächsten Erreichbarkeitstest überprüft.
     * Dieser wird nach frühestens
     * {@link OperativeMonitoringUnit#minOperativeMonitoringMillis}
     * durchgeführt.
     *
     * @param operative  {@link Operative} der in die
     *                   Erreichbarkeitsüberwachung miteinbezogen werden soll.
     */
    void startMonitoring(Operative operative) {
        synchronized (operativesTries) {
            operativesTries.put(operative, new Long(0));
            LOGGER.fine("Operative zur Überwachung hinzugefügt.");
        }
    }

    /**
     * Entfernt einen {@link Operative} sofort aus der
     * Erreichbarkeitsüberwachung. Wird der {@link Operative} gerade auf
     * Erreichbarkeit überprüft, so wird dieser Test nicht abgebrochen.
     * <p>
     * Diese Methode sollte nur vom
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ComputeManager}
     * aufgerufen werden.
     * Der Prozess zur Überwachung der Erreichbarkeit von Operatives macht
     * dem ComputeManager nur Vorschläge welcher Operative entfernt werden
     * sollte.
     *
     * @param operative  {@link Operative}, der von der
     *                   Erreichbarkeitsüberwachung ausgeschlossen werden soll.
     */
    void stopMonitoring(Operative operative) {
        synchronized (operativesTries) {
            operativesTries.remove(operative);
            LOGGER.fine("Operative aus Überwachung entfernt.");
        }
    }


    /**
     * Beendet den Prozess, der alle {@link Operative}s in regelmäßigen
     * Zeitabständen auf Erreichbarkeit überprüft.
     *
     * @see java.lang.Thread#destroy()
     */
    public void terminate() {
        LOGGER.fine("Operative Überwachungsprozess wird gestoppt.");
        threadTerminated = true;
        synchronized (this) {
            this.notifyAll();
        }
        try {
            this.join();
            LOGGER.fine("Operative-Überwachung gestoppt.");
        } catch (InterruptedException e2) {
            LOGGER.fine("Benutzerabbruch.");
        }
    }

    /**
     * Startet den Prozess zur Überwachung der Erreichbarkeit von
     * {@link Operative}s.
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        LOGGER.info("Operative Überwachungsprozess gestartet.");
        while (!threadTerminated) {
            LOGGER.finest("Überprüfe Operatives auf Erreichbarkeit");
            Enumeration operatives = (Enumeration) operativesTries.keys();
            long startTime = System.currentTimeMillis();
            while (operatives.hasMoreElements()) {
                Operative operative = (Operative) operatives.nextElement();
                if (!threadTerminated) {
                    try {
                        operative.isReachable();
                        // wenn der Operative noch nicht entfernt wurde,
                        // dann resete seinen Verbindungsfehlerzähler, da
                        // Operative erreichbar.
                        synchronized (operativesTries) {
                            if (operativesTries.containsKey(operative)) {
                                operativesTries.put(operative, new Long(0));
                            }
                        }
                    } catch (RemoteException e) {
                        // falls der Operative nicht erreichbar ist setze seinen
                        // Fehlerzähler hoch, sofern er nicht schon entfernt
                        // wurde
                        long oldErrNo = 0;
                        boolean inList = false;
                        synchronized (operativesTries) {
                            inList = operativesTries.containsKey(operative);
                            if (inList) {
                                Long aux = (Long) operativesTries.get(operative);
                                oldErrNo = aux.intValue();
                                oldErrNo++;
                                if (oldErrNo > operativeReachableMaxTries) {
                                    // solange der ComputeManager den
                                    // Operative nicht aus der Verwaltung
                                    // entfernt gehen wir davon aus, dass alles
                                    // o.k. ist. (s.u.)
                                    operativesTries.put(operative, new Long(0));
                                } else {
                                    operativesTries.put(operative, new Long(oldErrNo));
                                    LOGGER.config(
                                        "Verbindungsfehler zu Operative. "
                                            + "Erhöhe seinen "
                                            + "Verbindungsfehlerzähler");
                                }
                            }
                        }
                        // falls der Operative schon zu oft nicht erreichbar
                        // war, so überprüft der ComputeManager ob der
                        // Operative aus der Verwaltung entfernt werden muss.
                        if (inList && (oldErrNo > operativeReachableMaxTries)) {
                            LOGGER.info("Verbindungsfehler zu Operative. "
                                        + "Schlage vor ihn zu entfernen.");
                            computeManager.verifyOperativeReachability(operative);
                        }
                    }
                }
            }
            long durationMillis = System.currentTimeMillis() - startTime;
            if (durationMillis < minOperativeMonitoringMillis) {
                synchronized (this) {
                    try {
                        this.wait(minOperativeMonitoringMillis - durationMillis);
                    } catch (InterruptedException e) {
                        LOGGER.warning(
                            "Operative-Überwachung konnte nicht warten.");
                    }
                }
            }
        }
    }
}

