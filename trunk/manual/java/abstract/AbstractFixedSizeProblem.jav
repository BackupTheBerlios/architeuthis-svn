/*
 * file:        AbstractFixedSizeProblem.java
 * last change: 24.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.abstractproblems;

import java.io.Serializable;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Abstrakte Klasse zur Verwaltung einer festen Anzahl von Teilproblemen und
 * deren Teillösungen. Von einer konkreten Unterklasse erzeugte Teilprobleme
 * werden bei Bedarf an den ComputeManager gegeben. Sobald alle Teillösungen
 * eingegangen sind, werden diese in die ursprüngliche Reihenfolge
 * (entsprechend der Reihenfolge der Teilprobleme) gebracht und der konkreten
 * Unterklasse zur Erstellung einer Gesamtlösung übergeben.
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public abstract class AbstractFixedSizeProblem extends AbstractOrderedProblem {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 3509399230349106043L;

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
     * Liefert ein Array, in dem alle übergebenen Teilprobleme in der gleichen
     * Reihenfolge enthalten sind aber kein Wert <code>null</code> enthalten
     * ist.
     *
     * @param parProbArray  Ein Array mit Teilproblemen und eventuell dem Wert
     *                      <code>null</code>.
     *
     * @return  Array von Teilproblemen ohne den Wert <code>null</code>.
     */
    private PartialProblem[] withoutNull(PartialProblem[] parProbArray) {
        PartialProblem[] parProbs;
        int              notNullNumber;
        int              nextParProbIndex;

        notNullNumber = 0;
        for (int i = 0; i < parProbArray.length; i++) {
            if (parProbArray[i] != null) {
                notNullNumber++;
            }
        }

        if (notNullNumber == parProbArray.length) {
            return parProbArray;
        } else {
            parProbs = new PartialProblem[notNullNumber];
            nextParProbIndex = 0;
            for (int i = 0; i < parProbArray.length; i++) {
                if (parProbArray[i] != null) {
                    parProbs[nextParProbIndex] = parProbArray[i];
                    nextParProbIndex++;
                }
            }
            return parProbs;
        }
    }

    /**
     * Beim ersten Aufruf wird <code>createParitalProblem</code> aufgerufen,
     * um alle Teilprobleme zu generieren. Bei allen Aufrufen werden
     * Teilprobleme ausgegeben, solange noch welche vorhanden sind.
     *
     * @param number  Die vorgeschlagene Gesamtanzahl der zu generierenden
     *                Teilprobleme. Diese ist grösser oder gleich Eins.
     *
     * @return  Das nächste Teilproblem oder <code>null</code>, falls kein
     *          Teilproblem mehr geliefert werden kann.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#getPartialProblem(long)
     */
    protected PartialProblem createPartialProblem(int number) {

        if (partialProblems == null) {
            partialProblems = withoutNull(createPartialProblems(number));
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
     * Nimmt eine Teillösung entgegen, speichert diese und ruft bei der
     * letzten Teillösung <code>createSolution</code> auf, um die Gesamtlösung
     * zurückzuliefern.
     *
     * @param parSol  Die nächste fertige Teillösung für das Problem.
     *
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
     * Liefert ein Array von Teilproblemen. Die im Array enthaltenen Werte
     * <code>null</code> bleiben unberücksichtigt.
     *
     * @param problemsExpected  Die vorgeschlagene Anzahl von Teilproblemen.
     *                          Diese ist grösser oder gleich Eins.
     *
     * @return  Array von Teilproblemen.
     */
    protected abstract PartialProblem[] createPartialProblems(int problemsExpected);

    /**
     * Erstellt eine Gesamtlösung aus den übergebenen Teillösungen
     *
     * @param partialSolutions  Die Teillösungen zu allen Teilproblemen.
     *
     * @return  Die Gesamtlösung.
     */
    protected abstract Serializable createSolution(PartialSolution[] partialSolutions);
}

