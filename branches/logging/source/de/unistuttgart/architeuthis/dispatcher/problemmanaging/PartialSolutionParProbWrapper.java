/*
 * file:        PartialSolutionParProbWrapper.java
 * created:     04.04.2004
 * last change: 08.10.2004 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.dispatcher.problemmanaging;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Die Klasse stellt eine Verbindung zwischen einer Teill�sung und einem
 * Wrapper des zugeh�rigen Teilproblems dar.
 *
 * @author Andreas Heydlauff, J�rgen Heit, Dietmar Lippold
 */
class PartialSolutionParProbWrapper {

    /**
     * verwalteter Wrapper vom Teilproblem
     */
    private ParProbWrapper parProbWrap;

    /**
     * verwaltete Teill�sung
     */
    private PartialSolution parSol;

    /**
     * Konstruktor, der die Verbindung zwischen Teill�sung und Wrapper eines
     * Teilproblems herstellt.
     *
     * @param parProbWrapper ein Wrapper f�r das Teilproblem
     * @param partialSolution die Teill�sung
     *
     */
    public PartialSolutionParProbWrapper(
        ParProbWrapper parProbWrapper,
        PartialSolution partialSolution) {

        parProbWrap = parProbWrapper;
        parSol = partialSolution;
    }

    /**
     * Gibt den Wrapper zum Teilproblem der Verbindung zur�ck.
     *
     * @return  das Teilproblem der Verbindung
     */
    public ParProbWrapper getParProbWrapper() {
        return parProbWrap;
    }

    /**
     * Gibt die Teill�sung der Verbindung zur�ck.
     *
     * @return  die Teill�sung der Verbindung.
     */
    public PartialSolution getPartialSolution() {
        return parSol;
    }

}
