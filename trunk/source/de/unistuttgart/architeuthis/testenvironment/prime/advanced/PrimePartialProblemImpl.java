/*
 * file:        PrimePartialProblemImpl.java
 * created:     <???>
 * last change: 22.04.2006 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
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
 */


package de.unistuttgart.architeuthis.testenvironment.prime.advanced;

import de.unistuttgart.architeuthis.testenvironment.PrimeNumbers;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.NonCommPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;

/**
 * Implementierung des <code>PartialProblem</code>-Interfaces, um ein neues
 * Problem für das <code>ProblemManager</code> bereitzustellen.
 *
 * @author Ralf Kible, Achim Linke
 */
public class PrimePartialProblemImpl implements NonCommPartialProblem {

    /**
     * Untere Grenze, ab der Primzahlen gesucht werden
     */
    private long minWert;

    /**
     * Obere Grenze, bis zu welcher Primzahlen gesucht werden
     */
    private long maxWert;

    /**
     * Erzeugt zum übergebenen Intervall eine neue Instanz.
     *
     * @param min  Ab hier werden Primzahlen gesucht.
     * @param max  Bis hier werden Primzahlen gesucht.
     */
    public PrimePartialProblemImpl(long min, long max) {
        minWert = min;
        maxWert = max;
    }

    /**
     * Berechnet alle Primzahlen aus dem Intervall, das dem Konstruktor
     * übergeben wurde, und liefert die Lösung in Form eines
     * <code>ContainerPartialSolution</code> Objekts zurück. Intern wird
     * ausschließlich die Methode <code>primzahlTeilbereich</code> der Klasse
     * <code>PrimeNumbers</code> benutzt.
     *
     * @return  Die berechnete Teillösung.
     *
     * @throws ProblemComputeException  Falls ein Fehler bei der Berechnung
     *                                  auftritt.
     */
    public PartialSolution compute() throws ProblemComputeException {
        return new ContainerPartialSolution(
            PrimeNumbers.primzahlTeilbereich(minWert, maxWert));
    }
}

