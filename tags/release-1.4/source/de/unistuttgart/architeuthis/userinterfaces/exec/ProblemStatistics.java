/*
 * file:        ProblemStatistics.java
 * created:     <???>
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

/**
 * Dieses Interface enthält Methoden zum Zugriff auf Statistik-Werte für
 * problemspezifische Statistiken.
 *
 * @author Andreas Heydlauff, Jürgen Heit, Dietmar Lippold
 */
public interface ProblemStatistics extends AbstractStatistics {

    /**
     * Liefert die Anzahl der Operatives, die ein Teilproblem berechnen, wobei
     * mehrfach vergebene Teilprobleme mehrfach zählen.
     *
     * @return  Die Anzahl der der Operatives, die ein Teilproblem berechnen.
     */
    public long getComputingOperatives();

    /**
     * Liefert die Anzahl der angeforderten Teilprobleme, d.h. die Anzahl der
     * Aufrufe der Methode <code>getPartialProblem(long)</code> vom konkreten
     * Problem.
     *
     * @return  Die Anzahl der angeforderten Teilprobleme.
     */
    public long getRequestedPartialProblems();

    /**
     * Liefert die Zeitdauer seit dem Empfang des Problems.
     *
     * @return  Zeit in Millisekunden.
     */
    public long getProblemAge();

}
