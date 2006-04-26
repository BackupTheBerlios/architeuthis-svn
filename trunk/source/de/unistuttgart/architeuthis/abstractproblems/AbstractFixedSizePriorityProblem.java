/*
 * file:        AbstractFixedSizePriorityProblem.java
 * last change: 26.04.2006 von Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.abstractproblems;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Abstrakte Klasse zur Verwaltung von Teilproblemen und Teill�sungen.<p>
 * Von einer konkreten Unterklasse erzeugte Teilprobleme werden bei Bedarf
 * ihrer Priorit�t ensprechend an den ComputeManager gegeben. Sobald alle
 * Teill�sungen eingegangen sind, werden diese in der Reihenfolge ihrer
 * entsprechenden Teilprobleme der konkreten Unterklasse zur Erstellung
 * einer Gesamtl�sung �bergeben.<p>
 * Die verwendeten Teilprobleme m�ssen von
 * {@link de.unistuttgart.architeuthis.abstractproblems.PriorityPartialProblem}
 * erben.
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public abstract class AbstractFixedSizePriorityProblem implements SerializableProblem {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 7950521572006847810L;

    /**
     *  Unsortiertes Array der vom Problem generierten Teilprobleme.
     */
    private PartialProblem[] givenPartialProblems;

    /**
     * Nach Priorit�t sortierte Teilprobleme, die eins nach dem anderen bei
     * Bedarf ausgegeben werden.
     */
    private Iterator handoutPartialProblems = null;

    /**
     * Eingegangene Teill�sungen.
     */
    private HashMap partialSolutions = new HashMap();

    /**
     * Liefert ein Array, in dem alle �bergebenen Teilprobleme in der gleichen
     * Reihenfolge enthalten sind aber kein Wert <code>null</code> enthalten
     * ist.
     *
     * @param parProbArray  Ein Array mit Teilproblemen und eventuell dem Wert
     *                      <code>null</code>.
     *
     * @return  Array von Teilproblemen ohne den Wert <code>null</code>.
     */
    private PriorityPartialProblem[] withoutNull(PriorityPartialProblem[] parProbArray) {
        PriorityPartialProblem[] parProbs;
        int                      notNullNumber;
        int                      nextParProbIndex;

        notNullNumber = 0;
        for (int i = 0; i < parProbArray.length; i++) {
            if (parProbArray[i] != null) {
                notNullNumber++;
            }
        }

        if (notNullNumber == parProbArray.length) {
            return parProbArray;
        } else {
            parProbs = new PriorityPartialProblem[notNullNumber];
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
     * @param parProbsSuggested  Die vorgeschlagene Gesamtanzahl der zu
     *                           generierenden Teilprobleme. Diese ist gr�sser
     *                           oder gleich Eins.
     *
     * @return  Das n�chste Teilproblem oder <code>null</code>, falls kein
     *          Teilproblem mehr geliefert werden kann.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#getPartialProblem(long)
     */
    public PartialProblem getPartialProblem(int parProbsSuggested) {

        if (handoutPartialProblems == null) {
            givenPartialProblems = withoutNull(createPartialProblems(parProbsSuggested));
            handoutPartialProblems =
                (new TreeSet(Arrays.asList(givenPartialProblems))).iterator();
        }

        if (handoutPartialProblems.hasNext()) {
            return (PartialProblem) handoutPartialProblems.next();
        } else {
            return null;
        }
    }

    /**
     * Diese Methode wird vom ComputeManager aufgerufen um dem Problem eine
     * Teill�sung zu �bermitteln. Diese Teill�sung wird gesammelt, bis alle
     * Teilprobleme berechnet wurden, um die dann L�sung zu erstellen.
     *
     * @param parSol   Teill�sung zur �bergabe an das Problem-Objekt.
     * @param parProb  Referenz auf das Teilproblem, zu dem die Teill�sung
     *                 ermittelt wurde.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#collectPartialSolution(PartialSolution)
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {
        partialSolutions.put(parProb, parSol);
    }

    /**
     * Gibt die L�sung zur�ck, wenn alle Teilprobleme berechnet wurden. Sobald
     * alle Teill�sungen eingegangen sind, wird <code>createSolution</code>
     * aufgerufen, um eine Gesamtl�sung zu erzeugen.
     *
     * @return  Gesamtl�sung, die an den Problem-�bermittler geschickt wird.
     *          <code>null</code> falls die Gesamtl�sung noch nicht
     *          erstellt werden kann.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#getSolution()
     */
    public Serializable getSolution() {

        if (partialSolutions.size() >= givenPartialProblems.length) {

            PartialSolution[] sortedSolutions =
                new PartialSolution[givenPartialProblems.length];
            for (int i = 0; i < givenPartialProblems.length; i++) {
                sortedSolutions[i] =
                    (PartialSolution) partialSolutions.get(
                        givenPartialProblems[i]);
            }

            return createSolution(sortedSolutions);
        } else {
            return null;
        }
    }

    /**
     * Liefert ein Array von Teilproblemen. Die im Array enthaltenen Werte
     * <code>null</code> bleiben unber�cksichtigt.
     *
     * @param parProbsSuggested  Die vorgeschlagene Anzahl von Teilproblemen.
     *                           Diese ist gr�sser oder gleich Eins.
     *
     * @return  Array von Teilproblemen.
     */
    protected abstract PriorityPartialProblem[] createPartialProblems(int parProbsSuggested);

    /**
     * Erstellt eine Gesamtl�sung aus den �bergebenen Teill�sungen
     *
     * @param partialSolutions  Die Teill�sungen zu allen Teilproblemen.
     *
     * @return  Die Gesamtl�sung.
     */
    protected abstract Serializable createSolution(PartialSolution[] partialSolutions);
}

