/*
 * file:        SystemStatisticsCollector.java
 * created:     17.08.2003
 * last change: 26.05.2004 by Dietmar Lippold
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

import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;

/**
 * Diese Klasse modifiziert die Statistik-Werte des
 * {@link de.
 *        unistuttgart.
 *        architeuthis.
 *        dispatcher.
 *        statistic.
 *        SystemStatisticsContainer}
 *
 * @see de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics
 *
 * @author Andreas Heydlauff, Jürgen Heit, Dietmar Lippold
 */
public class SystemStatisticsCollector {

    /**
     * Statistik-Werte.
     */
    private SystemStatisticsContainer statisticsContainer =
        new SystemStatisticsContainer();

    /**
     * Zeit, zu der der ComputeManager gestartet wurde.
     */
    private long birthday;

    /**
     * Standard-Konstruktor. Setzt die Startzeit.
     */
    public SystemStatisticsCollector() {
        birthday = System.currentTimeMillis();
    }

    /**
     * Vermerkt die Anmeldung eines Operatives am System.
     */
    public synchronized void notifyOperativesRegistration() {
        statisticsContainer.registeredOperatives++;
        statisticsContainer.freeOperatives++;
    }

    /**
     * Vermerkt die Abmeldung eines Operatives am System.
     */
    public synchronized void notifyOperativesUnregistration() {
        statisticsContainer.registeredOperatives--;
        statisticsContainer.freeOperatives--;
    }

    /**
     * Vermerkt die Entgegennahme eines Problems von einen Transmitter.
     */
    public synchronized void notifyProblemReceived() {
        statisticsContainer.receivedProblems++;
        statisticsContainer.currentProblems++;
    }

    /**
     * Vermerkt den Abbruch der Bearbeitung eines Problems, für das also keine
     * Gesamtlösung berechnet wurde.
     */
    public synchronized void notifyProblemAborted() {
        statisticsContainer.currentProblems--;
        statisticsContainer.abortedProblems++;
    }

    /**
     * Vermerkt die Übermittlung einer Gesamtlösung an den zugehörigen
     * Transmitter.
     */
    public synchronized void notifySolutionTransfered() {
        statisticsContainer.currentProblems--;
    }

    /**
     * Verringert die Anzahl der am System angemeldeten freien Operatives. Ein
     * angemeldeter Operative ist frei, wenn ihm kein Teilproblem zugewiesen
     * ist.
     */
    synchronized void decFreeOperatives() {
        statisticsContainer.freeOperatives--;
    }

    /**
     * Erhöht die Anzahl der am System angemeldeten freien Operatives. Ein
     * angemeldeter Operative ist frei, wenn ihm kein Teilproblem zugewiesen
     * ist.
     */
    synchronized void incFreeOperatives() {
        statisticsContainer.freeOperatives++;
    }

    /**
     * Vermerkt die Erzeugung eines neuen Teilproblems.
     */
    synchronized void notifyCreatedPartialProblem() {
        statisticsContainer.createdPartialProblems++;
        statisticsContainer.processingPartialProblems++;
    }

    /**
     * Vermerkt den Beginn der Berechnung eines Teilproblems.
     */
    synchronized void notifyBeginComputingPartialProblem() {
        statisticsContainer.computingPartialProblems++;
        statisticsContainer.freeOperatives--;
    }

    /**
     * Vermerkt den Abbruch der Berechnung eines Teilproblems.
     */
    synchronized void notifyAbortComputingPartialProblem() {
        statisticsContainer.computingPartialProblems--;
        statisticsContainer.freeOperatives++;
    }

    /**
     * Vermerkt das Ende der erfolgreichen Berechnung eines Teilproblems.
     *
     * @param duration  Zeitdauer der Berechnung in Millisekunden
     */
    synchronized void notifyEndComputingPartialProblem(long duration) {
        statisticsContainer.computingPartialProblems--;
        statisticsContainer.freeOperatives++;
        statisticsContainer.computedPartialProblems++;
        statisticsContainer.computationDuration += duration;
    }

    /**
     * Vermerkt die Fertigstellung der Bearbeitung eines Teilproblems.
     */
    synchronized void notifyProcessedPartialProblem() {
        statisticsContainer.processingPartialProblems--;
        statisticsContainer.processedPartialProblems++;
    }

    /**
     * Vermerkt den Abbruch der Bearbeitung eines Teilproblems, zum dem das
     * zugehörige konkrete Problem also keine Teillösung erhalten hat.
     */
    synchronized void notifyAbortedPartialProblem() {
        statisticsContainer.processingPartialProblems--;
        statisticsContainer.abortedPartialProblems++;
    }

    /**
     * Vermerkt den Abbruch der Bearbeitung mehrerer Teilprobleme, zum denen
     * das zugehörige konkrete Problem also keine Teillösung erhalten hat.
     *
     * @param number  die Anzahl der abgebrochenen Teilprobleme.
     */
    synchronized void notifyAbortedPartialProblems(long number) {
        statisticsContainer.processingPartialProblems -= number;
        statisticsContainer.abortedPartialProblems += number;
    }

    /**
     * Liefert einen Schnappshuß (eine Kopie) vom
     * {@link SystemStatisticsContainer} zurück.
     *
     * @return  Statistik-Werte.
     */
    public synchronized SystemStatistics getSnapshot() {
        SystemStatisticsContainer statContainer;

        statContainer = (SystemStatisticsContainer) statisticsContainer.clone();
        statContainer.dispatcherAge = System.currentTimeMillis() - birthday;
        return statContainer;
    }
}
