/*
 * file:        RandomPartialProblemImpl.java
 * last change: 24.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.random;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.NonCommPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Teilproblem, das eine zuf�llige Zeit wartet. Nur zu Testzwecken.
 * @author Ralf Kible
 */
public class RandomPartialProblemImpl implements NonCommPartialProblem {

    /**
     * Maximale Zeit in Millisekunden, die ein Teilproblem braucht.
     */
    private static final long MAX_MILLISEC_TO_WAIT = 15000;

    /**
     * Umrechnungsteiler f�r Sekunden.
     */
    private static final long CHANGE_TO_SEC = 1000;

    /**
     * Die Nummer des <code>PartialProblem</code> Objekts, die zur internen
     * Verwaltung wichtig ist.
     */
    private int number;

    /**
     * Zu verwendender Standard-Konstruktor, der alle wichtigen Parameter
     * zuweist.
     *
     * @param num  Interne Verwaltungsnummer.
     */
    public RandomPartialProblemImpl(int num) {
        number = num;
    }

    /**
     * Wartet eine zuf�llige Zeit.
     *
     * @return  Dummy-L�sung.
     *
     * @throws ProblemComputeException  bei unerwarteten "Berechnungsproblemen"
     */
    public PartialSolution compute() throws ProblemComputeException {

        try {
            long zeit = (long) Math.round(Math.random() * MAX_MILLISEC_TO_WAIT);
            System.out.println(
                "Teilproblem"
                    + number
                    + " wartet "
                    + zeit / CHANGE_TO_SEC
                    + " sekunden");
            Thread.sleep(zeit);

        } catch (InterruptedException e) { }

        return new RandomPartialSolutionImpl(number);
    }
}

