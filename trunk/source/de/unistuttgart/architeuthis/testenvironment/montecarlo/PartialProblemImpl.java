/*
 * file:        PartialProblemImpl.java
 * created:     24.05.2003
 * last change: 26.05.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
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


package de.unistuttgart.architeuthis.testenvironment.montecarlo;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.NonCommPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Die Klasse, die die Teilprobleme berechnet und die Teillösungen erzeugt.
 *
 * @author Achim Linke
 */
public class PartialProblemImpl implements NonCommPartialProblem {

    /**
     * Die Anzahl der Berechnungen
     */
    private long trial;

    /**
     * Konstruktor, der die Anzahl der Berchnungen übergeben bekommt und diesen
     * Wert speichert.
     *
     * @param trials  <code>integer</code>, der die Anzahl der Berechnungen
     *                enthält
     */
    PartialProblemImpl(long trials) {
        trial = trials;
    }

    /**
     * Erzeugt zwei Zufallszahlen und berechnet mit diesen nach dem
     * Monte-Carlo Verfahren die Zahl Pi.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.PartialProblem#compute()
     * @throws ProblemComputeException Falls unvorhergesehene Rechenprobleme
     *                                 auftreten (Teilung durch Null).
     * @return Die berechnete Zahl Pi.
     */
    public PartialSolution compute() throws ProblemComputeException {
        double x, y;
        long hit = 0;

        for (long i = 1; i <= trial; i++) {
            // zufälligen Punkt im Qudrat finden
            x = Math.random();
            y = Math.random();
            // Anzahl Treffer zählen
            if (x * x + y * y <= 1.0) {
                hit = hit + 1;
            }
        }
        // Lösungsobjekt, mit dem berechneten Wert für Pi erzeugen
        PartialSolutionImpl piBerechnet =
            new PartialSolutionImpl(4.0 * hit / trial);
        return piBerechnet;
    }
}
