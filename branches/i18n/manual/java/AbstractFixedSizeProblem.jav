/*
 * file:       AbstractFixedSizeProblem.java
 * last change: 11.02.2004 by Jürgen Heit
 * copyright 2003:  Jürgen Heit,       juergen.heit@gmx.de
 *                  Andreas Heydlauff, AndiHeydlauff@gmx.de
 *                  Achim Linke,       achim81@gmx.de
 *                  Ralf Kible,        ralf_kible@gmx.de
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

package de.unistuttgart.architeuthis.abstractproblems;

import java.io.Serializable;

import de.unistuttgart.architeuthis.userinterfaces.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.PartialSolution;

/**
 * Abstrakte Klasse zur Verwaltung einer festen Anzahl von Teilproblemen und
 * deren Teillösungen. Von einer konkreten Unterklasse erzeugte Teilprobleme
 * werden bei Bedarf an den ComputeManager gegeben. Sobald alle Teillösungen
 * eingegangen sind, werden diese in die ursprüngliche Reihenfolge
 * (entsprechend der Reihenfolge der Teilprobleme) gebracht und der konkreten
 * Unterklasse zur Erstellung einer Gesamtlösung übergeben.
 *
 * @author Andreas Heydlauff
 */
public abstract class AbstractFixedSizeProblem extends AbstractOrderedProblem {

    /**
     * Array, das alle Teilprobleme enthält.
     */
    private PartialProblem[] partialProblems = null;

    /**
     * Array, das die bisher erhaltenen Teillösungen enthält.
     */
    private PartialSolution[] partialSolutions = null;

    /**
     * Anzahl der verteilten Teilprobleme
     */
    private int distributed = 0;

    /**
     * Anzahl der eingesammelten Teilprobleme
     */
    private int collected = 0;

    /**
     * Beim ersten Aufruf wird <code>createParitalProblem</code> aufgerufen, um
     * alle Teilprobleme zu generieren. Bei allen Aufrufen werden Teilprobleme
     * ausgegeben, solange noch welche vorhanden sind.
     *
     * @param number  gewünschte Gesamtanzahl der zu generierenden
     *                Teilprobleme
     * @return  genau ein Teilproblem. Dies ist unabhängig von der Gesamtanzahl
     *          der generierten Teilprobleme. <code>null</code> falls
     *          kein Teilproblem mehr ausgegeben werden soll
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#getPartialProblem(long)
     */
    protected PartialProblem createPartialProblem(long number) {
        if (partialProblems == null) {
            partialProblems = createPartialProblems(number);
            partialSolutions = new PartialSolution[partialProblems.length];
            distributed = 0;
            collected = 0;
        }

        if (distributed < partialProblems.length) {
            distributed++;
            return partialProblems[distributed - 1];
        } else {
            return null;
        }
    }

    /**
     * Nimmt eine Teillösung entgegen, sammelt diese und ruft bei der letzten 
     * Teillösung <code>createSolution</code> auf, um die Gesamtlösung 
     * zurückzuliefern.
     *
     * @param parSol  Die nächste fertige Teillösung für das Problem.
     * @return  Gesamtlösung, falls diese bereits fertig ist, sonst
     *          <code>null</code>
     */
    protected Serializable receivePartialSolution(PartialSolution parSol) {
        partialSolutions[collected] = (PartialSolution) parSol;
        collected++;

        if (collected == partialProblems.length) {
            return createSolution(partialSolutions);
        } else {
            return null;
        }
    }

    /**
     * Stellt ein Array von Teilproblemen zur Verwaltung bereit.<p>
     * Diese Methode muss von einer konkreten Unterklasse implementiert werden.
     *
     * @param porblemsExpected  gewünschte Anzahl von Teilproblemen.
     *
     * @return  Array von Teilproblemen.
     */
    protected abstract PartialProblem[] createPartialProblems(long problemsExpected);


    /**
     * Erstellt eine Gesamtlösung aus allen Teillösungen.<p>
     * Diese Methode muss von einer konkreten Unterklasse implementiert werden.
     *
     * @param partialSolutions  alle eingegangenen Teillösungen.
     * @return  Gesamtlösung
     */
    protected abstract Serializable createSolution(PartialSolution[] partialSolutions);

}
