/*
 * file:        OrderedPrimeParProb.java
 * created:     <???>
 * last change: 11.02.2004 by Jürgen Heit
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

package de.unistuttgart.architeuthis.testenvironment.prime.example;

import de.unistuttgart.architeuthis.systeminterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.testenvironment.*;
import de.unistuttgart.architeuthis.userinterfaces.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;

/**
 * Test zur Benutzung der abstrakten Klasse <code>AbstractOrderedProblem</code>.
 * Es werden alle PrimeNumbers zwischen anzugebenden Grenzen berechnet.
 *
 * @author Achim Linke
 */
public class OrderedPrimeParProb implements PartialProblem {

    /**
     * Zahl, ab der nach PrimeNumbers gesucht wird.
     */
    private long minWert = 1;

    /**
     * Zahl, bis zu der nach PrimeNumbers gesucht wird.
     */
    private long maxWert = 2;

    /**
     * Konstruktor, der die Grenzen zur Primzahlsuche zuweist.
     * Die Grenzen werden ebenfalls untersucht.
     *
     * @param min  Die Zahl, ab der nach PrimeNumbers gesucht wird.
     * @param max  Die Zahl, bis zu der nach PrimeNumbers gesucht wird.
     */
    public OrderedPrimeParProb(long min, long max) {
        minWert = min;
        maxWert = max;
    }


    /**
     * Führt die konkrete Berechnung der PrimeNumbers aus.
     *
     * @return  Container-Klasse, die die Liste der PrimeNumbers im Bereich
     *          enthält.
     */
    public PartialSolution compute() throws ProblemComputeException {
        return new ContainerPartialSolution(
            PrimeNumbers.primzahlTeilbereich(minWert, maxWert));
    }
}
