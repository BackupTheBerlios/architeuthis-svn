/*
 * file:        ProblemStatisticsCollector.java
 * created:     17.08.2003
 * last change: 08.10.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
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


package de.unistuttgart.architeuthis.dispatcher.statistic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.unistuttgart.architeuthis.dispatcher.problemmanaging.ParProbWrapper;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;

/**
 * Diese Klasse modifiziert die Statistik-Werte des
 * {@link de.unistuttgart.architeuthis.dispatcher.statistic.ProblemStatisticsContainer}
 *
 * @see de.unistuttgart.architeuthis.systeminterfaces.SystemStatistics
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public class ProblemStatisticsCollector {

    /**
     * Statistik-Werte
     */
    private ProblemStatisticsContainer statisticsContainer =
        new ProblemStatisticsContainer();

    /**
     * Zeit, zu der der ComputeManager gestartet wurde.
     */
    private long birthday;

    /**
     * Speichert zu jedem Teilproblem-Wrapper-Objekt, das sich noch in
     * Berechnung befinden, den Start-Zeitpunkt des Teilproblems.
     */
    private HashMap startedPartProbCompTime = new HashMap();

    /**
     * Referenz auf den {@link SystemStatisticsCollector} oder der Wert
     * <code>null</code>, wenn keine System-Statistik aktualisiert werden soll.
     */
    private SystemStatisticsCollector systemStatistics;

    /**
     * Standard-Konstruktor. Setzt die Startzeit und den
     * {@link SystemStatisticsCollector}. Wenn anstatt diesem der Wert 
     * <code>null</code> übergeben wird, wird keine System-Statistik
     * aktualisiert.
     *
     * @param SystemStatistics  Referenz auf die allgemeine System-Statistik,
     *                          der ebenfalls Änderungen mitgeteilt werden.
     *                          Wenn der Wert <code>null</code>, wird keine
     *                          System-Statistik aktualisiert.
     */
    public ProblemStatisticsCollector(
            SystemStatisticsCollector systemStatisticsCollector) {
        birthday = System.currentTimeMillis();
        systemStatistics = systemStatisticsCollector;
    }

    /**
     * Vermerkt die Anforderung eines neuen Teilproblems.
     */
    public synchronized void notifyRequestedPartialProblem() {
        statisticsContainer.requestedPartialProblems++;
    }

    /**
     * Vermerkt die Erzeugung eines neuen Teilproblems.
     */
    public synchronized void notifyCreatedPartialProblem() {
        statisticsContainer.createdPartialProblems++;
        statisticsContainer.processingPartialProblems++;
        if (systemStatistics != null) {
            systemStatistics.notifyCreatedPartialProblem();
        }
    }

    /**
     * Beginnt mit der Zeitmessung der Berechnungsdauer für ein Teilproblem,
     * das bisher von keinem Operative berechnet wurde. Wenn sich zu jedem
     * Zeitpunkt maximal ein Teilproblem in Berechnung befindet, kann anstatt
     * des Teilproblems auch der Wert <code>null</code> übergeben werden.
     * Dieser muß dann natürlich auch den Methden <code>stopTimeMeasurement</code>
     * und <code>abortTimeMeasurement</code> übergeben werden.
     *
     * @param partProbWrap  Teilproblem-Wrapper-Objekt für das mit der
     *                      Zeitmessung begonnen wird.
     */
    public synchronized void startTimeMeasurement(ParProbWrapper partProbWrap) {
        if (!startedPartProbCompTime.containsKey(partProbWrap)) {
            startedPartProbCompTime.put(partProbWrap,
                                        new Long(System.currentTimeMillis()));
            statisticsContainer.computingPartialProblems++;
            statisticsContainer.computingOperatives++;
            if (systemStatistics != null) {
                systemStatistics.notifyBeginComputingPartialProblem();
            }
        }
    }

    /**
     * Beendet die Zeitmessung der Berechnungsdauer für ein Teilproblem, da
     * dessen Berechnung erfolgreich abgeschlossen wurde. Wenn die Zeitmessung
     * schon beendet war, wird nur die Anzahl der berechnenden Operatives
     * verringert.
     *
     * @param partProbWrap  Teilproblem-Wrapper-Objekt für das die Zeitmessung
     *                      gestoppt wird.
     */
    public synchronized void stopTimeMeasurement(ParProbWrapper partProbWrap) {
        Long startTime;
        long diffTime;

        startTime = (Long) startedPartProbCompTime.remove(partProbWrap);
        if (startTime == null) {
            decComputingOperatives();
        } else {
            diffTime = System.currentTimeMillis() - startTime.longValue();

            statisticsContainer.computingPartialProblems--;
            statisticsContainer.computingOperatives--;
            statisticsContainer.computedPartialProblems++;
            statisticsContainer.computationDuration += diffTime;
            if (systemStatistics != null) {
                systemStatistics.notifyEndComputingPartialProblem(diffTime);
            }
        }
    }

    /**
     * Beendet die Zeitmessung der Berechnungsdauer für ein Teilproblem, da
     * dessen Berechnung abgebrochen wurde. Die Berechnugszeit wird nicht
     * verwendet. Wenn die Zeitmessung schon beendet war, wird nur die Anzahl
     * der berechnenden Operatives verringert.
     *
     * @param partProbWrap  Teilproblem-Wrapper-Objekt für das die Zeitmessung
     *                      abgebrochen wird.
     */
    public synchronized void abortTimeMeasurement(ParProbWrapper partProbWrap) {
        Long startTime;

        startTime = (Long) startedPartProbCompTime.remove(partProbWrap);
        if (startTime == null) {
            decComputingOperatives();
        } else {
            statisticsContainer.computingPartialProblems--;
            statisticsContainer.computingOperatives--;
            if (systemStatistics != null) {
                systemStatistics.notifyAbortComputingPartialProblem();
            }
        }
    }

    /**
     * Erhöht die Anzahl der Operatives, die ein schon in Berechnung
     * befindliches Teilproblem berechnen.
     */
    public synchronized void incComputingOperatives() {
        statisticsContainer.computingOperatives++;
        if (systemStatistics != null) {
            systemStatistics.decFreeOperatives();
        }
    }

    /**
     * Verringert die Anzahl der der Operatives, die ein schon mehrfach in
     * Berechnung befindliches Teilproblem berechnen.
     */
    public synchronized void decComputingOperatives() {
        statisticsContainer.computingOperatives--;
        if (systemStatistics != null) {
            systemStatistics.incFreeOperatives();
        }
    }

    /**
     * Vermerkt die Fertigstellung der Bearbeitung eines Teilproblems.
     */
    public synchronized void notifyProcessedPartialProblem() {
        statisticsContainer.processingPartialProblems--;
        statisticsContainer.processedPartialProblems++;
        if (systemStatistics != null) {
            systemStatistics.notifyProcessedPartialProblem();
        }
    }

    /**
     * Vermerkt den Abbruch der Bearbeitung eines Teilproblems, zu dem das
     * konkrete Problem also keine Teillösung erhalten hat.
     */
    public synchronized void notifyAbortedPartialProblem() {
        statisticsContainer.processingPartialProblems--;
        statisticsContainer.abortedPartialProblems++;
        if (systemStatistics != null) {
            systemStatistics.notifyAbortedPartialProblem();
        }
    }

    /**
     * Vermerkt den Abbruch der Bearbeitung mehrerer Teilproblem, zu denen das
     * konkrete Problem also keine Teillösung erhalten hat.
     *
     * @param number  die Anzahl der abgebrochenen Teilprobleme.
     */
    public synchronized void notifyAbortedPartialProblems(int number) {
        statisticsContainer.processingPartialProblems -= number;
        statisticsContainer.abortedPartialProblems += number;
        if (systemStatistics != null) {
            systemStatistics.notifyAbortedPartialProblems(number);
        }
    }

    /**
     * Liefert das Maximum der Berechnungszeit der sich in Berechnung
     * befindlichen Teilprobleme. Wenn sich kein Teilproblem in Berechnung
     * befindet, wird der Wert Null geliefert.
     *
     * @return  Maximale Berechnungszeit in Millisekunden
     */
    private long maxTimeInComputation() {
        long maxTime = 0;
        long nowTime, startTime;

        nowTime = System.currentTimeMillis();
        Iterator parProbTimeIter = startedPartProbCompTime.values().iterator();
        while (parProbTimeIter.hasNext()) {
            startTime = ((Long) parProbTimeIter.next()).longValue();
            maxTime = Math.max(maxTime, nowTime - startTime);
        }
        return maxTime;
    }

    /**
     * Liefert die Summe der voraussichtlichen Restlaufzeiten der in Berechnung
     * befindlichen Teilprobleme. Die voraussichtliche Restlaufzeit eines
     * Teilproblems ist die Differenz von dessen aktueller Berechnungszeit zur
     * übergebenen durchschnittlichen Berechnungszeit, wobei das Minimum des
     * Wertes Null ist. Wenn sich kein Teilproblem in Berechnung befindet, wird
     * der Wert Null geliefert.
     *
     * @param avgTime  Durchschnittliche Berechnungszeit eines Teilproblems.
     * @return  Summe der voraussichtlichen Restlaufzeiten in Millisekunden
     */
    private long sumRestTimeInComputation(long avgTime) {
        long sumRestTime = 0;
        long nowTime, startTime, restTime;

        nowTime = System.currentTimeMillis();
        Iterator parProbTimeIter = startedPartProbCompTime.values().iterator();
        while (parProbTimeIter.hasNext()) {
            startTime = ((Long) parProbTimeIter.next()).longValue();
            restTime = avgTime - (nowTime - startTime);
            if (restTime > 0) {
                sumRestTime += restTime;
            }
        }
        return sumRestTime;
    }

    /**
     * Liefert die voraussichtliche gesamte Berechnungszeit aller Teilprobleme,
     * die von der Klasse <code>ProblemWrapper</code> ausgeliefert aber die
     * noch nicht berechnet wurden. Wenn sich kein Teilproblem in Bearbeitung
     * befindet, wird der Wert Null geliefert.
     *
     * @param parProbBeforeDeliv  Anzahl der Teilprobleme, die erzeugt aber
     *                            noch nicht ausgeliefert wurden.
     * @return  voraussichtliche gesamte Berechnungszeit der derzeit
     *          ausgelieferten Teilprobleme in Millisekunden
     */
    public synchronized long estimatedComputationTime(int parProbBeforeDeliv) {
        SystemStatistics sysStatData;
        long avgTime;
        long toCompute;

        if (statisticsContainer.processingPartialProblems == 0) {
            return 0;
        } else {
            // durchschnittliche Laufzeit ermitteln
            avgTime = statisticsContainer.getAverageComputationDuration();
            if (avgTime == 0) {
                if (systemStatistics != null) {
                    sysStatData = systemStatistics.getSnapshot();
                    avgTime = Math.max(maxTimeInComputation(),
                                       sysStatData.getAverageComputationDuration());
                } else {
                    avgTime = maxTimeInComputation();
                }
            }

            // Anzahl der noch zur Berechnung anstehenden Teilprobleme ermitteln
            toCompute = (statisticsContainer.createdPartialProblems
                         - statisticsContainer.computedPartialProblems
                         - parProbBeforeDeliv);
            return sumRestTimeInComputation(avgTime) + toCompute * avgTime;
        }
    }

    /**
     * Liefert einen Schnappshuß (eine Kopie) vom
     * {@link ProblemStatisticsContainer} zurück.
     *
     * @return  Statistik-Werte.
     */
    public synchronized ProblemStatistics getSnapshot() {
        ProblemStatisticsContainer statContainer;

        statContainer = (ProblemStatisticsContainer) statisticsContainer.clone();
        statContainer.problemAge = System.currentTimeMillis() - birthday;
        return statContainer;
    }
}
