/*
 * file:       AbstractFixedSizeProblem.java
 * last change: 11.02.2004 by J�rgen Heit
 * copyright 2003:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */

package de.unistuttgart.architeuthis.abstractproblems;

import java.io.Serializable;

import de.unistuttgart.architeuthis.userinterfaces.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.PartialSolution;

/**
 * Abstrakte Klasse zur Verwaltung einer festen Anzahl von Teilproblemen und
 * deren Teill�sungen. Von einer konkreten Unterklasse erzeugte Teilprobleme
 * werden bei Bedarf an den ComputeManager gegeben. Sobald alle Teill�sungen
 * eingegangen sind, werden diese in die urspr�ngliche Reihenfolge
 * (entsprechend der Reihenfolge der Teilprobleme) gebracht und der konkreten
 * Unterklasse zur Erstellung einer Gesamtl�sung �bergeben.
 *
 * @author Andreas Heydlauff
 */
public abstract class AbstractFixedSizeProblem extends AbstractOrderedProblem {

    /**
     * Array, das alle Teilprobleme enth�lt.
     */
    private PartialProblem[] partialProblems = null;

    /**
     * Array, das die bisher erhaltenen Teill�sungen enth�lt.
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
     * @param number  gew�nschte Gesamtanzahl der zu generierenden
     *                Teilprobleme
     * @return  genau ein Teilproblem. Dies ist unabh�ngig von der Gesamtanzahl
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
     * Nimmt eine Teill�sung entgegen, sammelt diese und ruft bei der letzten 
     * Teill�sung <code>createSolution</code> auf, um die Gesamtl�sung 
     * zur�ckzuliefern.
     *
     * @param parSol  Die n�chste fertige Teill�sung f�r das Problem.
     * @return  Gesamtl�sung, falls diese bereits fertig ist, sonst
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
     * @param porblemsExpected  gew�nschte Anzahl von Teilproblemen.
     *
     * @return  Array von Teilproblemen.
     */
    protected abstract PartialProblem[] createPartialProblems(long problemsExpected);


    /**
     * Erstellt eine Gesamtl�sung aus allen Teill�sungen.<p>
     * Diese Methode muss von einer konkreten Unterklasse implementiert werden.
     *
     * @param partialSolutions  alle eingegangenen Teill�sungen.
     * @return  Gesamtl�sung
     */
    protected abstract Serializable createSolution(PartialSolution[] partialSolutions);

}
