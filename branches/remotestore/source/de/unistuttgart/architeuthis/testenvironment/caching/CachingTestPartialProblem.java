/*
 * file:        CachingTestPartialProblem.java
 * created:     <???>
 * last change: 26.05.2004 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.testenvironment.caching;

import de.unistuttgart.architeuthis.testenvironment.PrimeNumbers;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Implementierung des <code>PartialProblem</code>-Interfaces, um ein neues
 * Problem f�r das <code>ProblemManager</code> bereitzustellen.
 *
 * @author Ralf Kible, Achim Linke
 */
public class CachingTestPartialProblem implements PartialProblem {

    /**
     * Untere Grenze, ab der PrimeNumbers gesucht werden
     */
    private long minWert = 0;

    /**
     * Obere Grenze, bis zu welcher PrimeNumbers gesucht werden
     */
    private long maxWert = 0;

    /**
     * Zu verwendender Standard-Konstruktor, der alle wichtigen Parameter
     * zuweist.
     *
     * @param min  Ab hier werden PrimeNumbers gesucht.
     * @param max  Bis hier werden PrimeNumbers gesucht.
     * @param num  Interne Verwaltungsnummer
     */
    public CachingTestPartialProblem(long min, long max) {
        DummyClass1 d1 = new DummyClass1();
        DummyClass2 d2 = new DummyClass2();
        DummyClass3 d3 = new DummyClass3();
        minWert = min;
        maxWert = max;
    }

    /**
     * Berechnet alle PrimeNumbers von <code>min</code> bis <code>max</code> und
     * liefert die L�sung in Form eines <code>PartialSolution</code> Objekts
     * zur�ck. Die Nummer des <code>PartialProblems</code> wird in die L�sung
     * �bernommen. Intern wird ausschlie�lich die Methode
     * <code>primzahlTeilbereich</code> der Klasse <code>PrimeNumbers</code>
     * benutzt.
     *
     * @return die berechnete Teill�sung.
     *
     * @throws ProblemComputeException  Falls ein Fehler bei der Berechnung
     *                                  auftritt
     */
    public PartialSolution compute() throws ProblemComputeException {
        return new CachingTestPartialSolution(
            PrimeNumbers.primzahlTeilbereich(minWert, maxWert));
    }
}
