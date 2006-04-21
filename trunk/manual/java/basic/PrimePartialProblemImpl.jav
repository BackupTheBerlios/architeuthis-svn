/*
 * file:        PrimePartialProblemImpl.java
 * created:     <???>
 * last change: 21.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.prime.basic;

import de.unistuttgart.architeuthis.testenvironment.PrimeNumbers;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.NonCommPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;

/**
 * Test zur Benutzung der abstrakten Klasse <code>AbstractOrderedProblem</code>.
 * Es werden alle PrimeNumbers zwischen anzugebenden Grenzen berechnet.
 *
 * @author Achim Linke, Dietmar Lippold
 */
public class PrimePartialProblemImpl implements NonCommPartialProblem {

    /**
     * Zahl, ab der nach Primzahlen gesucht werden soll.
     */
    private long minWert;

    /**
     * Zahl, bis zu der nach Primzahlen gesucht werden soll.
     */
    private long maxWert;

    /**
     * Liefert eine neue Instanz zu einem vorgegebenen Intervall. Die
     * Grenzen des Interalls sind Teil sind Teil von diesem.
     *
     * @param min  Die Zahl, ab der nach Primzahlen gesucht werden soll.
     * @param max  Die Zahl, bis zu der nach Primzahlen gesucht werden soll.
     */
    public PrimePartialProblemImpl(long min, long max) {
        minWert = min;
        maxWert = max;
    }

    /**
     * Führt die konkrete Berechnung der Primzahlen aus.
     *
     * @return  Container-Klasse, die die Liste der Primzahlen im vorgegebenen
     *          Bereich enthält.
     */
    public PartialSolution compute() throws ProblemComputeException {
        return new ContainerPartialSolution(
            PrimeNumbers.primzahlTeilbereich(minWert, maxWert));
    }
}

