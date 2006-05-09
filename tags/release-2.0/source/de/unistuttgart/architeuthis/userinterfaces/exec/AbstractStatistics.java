/*
 * file:        AbstractStatistics.java
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


package de.unistuttgart.architeuthis.userinterfaces.exec;

import java.io.Serializable;

/**
 * Dieses Interface enthält Methoden zum Zugriff auf Statistik-Werte,
 * die sowohl bei der allgemeinen als auch bei der problemspezifischen
 * Statistik geführt werden.
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public interface AbstractStatistics extends Serializable {

    /**
     * Liefert die Anzahl der erzeugten Teilprobleme, d.h. die Anzahl der
     * Fälle, in denen die Methode <code>getPartialProblem(long)</code> vom
     * konkreten Problem bzw. von einem der Probleme nicht <code>null</code>
     * geliefert hat.
     *
     * @return  Anzahl der erzeugten Teilprobleme.
     */
    public long getCreatedPartialProblems();

    /**
     * Liefert die Anzahl der Teilproblem, die in Bearbeitung sind, zurück.
     * Das ist die Anzahl der Teilprobleme, die vom konkreten Problem bzw. von
     * einem der Probleme ausgegeben wurden und von denen dieses noch keine
     * Teillösung erhalten hat.
     *
     * @return  Anzahl der Teilprobleme in Bearbeitung
     */
    public long getProcessingPartialProblems();

    /**
     * Liefert die Anzahl der in Berechnung befindlichen Teilprobleme, d.h.
     * die Anzahl der unterschiedlichen Teilprobleme, die von mindestens einem
     * Operatives gerade berechnet werden.
     *
     * @return  Anzahl der Teilprobleme in Berechnung
     */
    public long getComputingPartialProblems();

    /**
     * Liefert die Anzahl der berechneten Teilprobleme, d.h. Anzahl der
     * Teilprobeme, zu denen ein Operative eine Teillösung geliefert hat.
     *
     * @return  Anzahl der berechneten Teilprobleme.
     */
    public long getComputedPartialProblems();

    /**
     * Liefert die Anzahl der fertig bearbeiteten Teilprobleme, d.h. Anzahl der
     * Aufrufe von <code>Problem.collectResult(PartialSolution, PartialProblem)</code>
     * für das konkrete Problem bzw. für eines der Probleme.
     *
     * @return  Anzahl der fertig bearbeiteten Teilprobleme
     */
    public long getProcessedPartialProblems();

    /**
     * Liefert die Anzahl der bei der Bearbeitung abgebrochenen Teilprobleme.
     *
     * @return  Anzahl der bei der Bearbeitung abgebrochenen Teilprobleme.
     */
    public long getAbortedPartialProblems();

    /**
     * Liefert die durchschnittliche Berechnungszeit aller fertig berechneten
     * Teilprobleme zurück. Gestartet wird vor dem Senden eines Teilproblems
     * und gestoppt nach dem Empfang der ersten Teillösung zu diesem
     * Teilproblem. Rechenzeit, die durch Mehrfachverteilung anfällt, wird
     * nicht berücksichtigt.
     *
     * @return  durchschnittliche Zeit in Millisekunden
     */
    public long getAverageComputationDuration();

    /**
     * Liefert die gesamte Berechnungszeit aller fertig berechneten Teilprobleme
     * zurück. Gestartet wird vor dem Senden eines Teilproblems und gestoppt
     * nach dem Empfang der ersten Teillösung zu diesem Teilproblem. Rechenzeit,
     * die durch Mehrfachverteilung anfällt, wird nicht berücksichtigt.
     *
     * @return  gesamte Zeit in Millisekunden
     */
    public long getTotalComputationDuration();
}
