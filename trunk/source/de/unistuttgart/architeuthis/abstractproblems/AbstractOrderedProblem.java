/*
 * file:        AbstractOrderedProblem.java
 * last change: 23.04.2006 by Dietmar Lippold
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
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Abstrakte Klasse, die die Teillösungen mittels {@link receivePartialSolution}
 * in der gleichen Reihenfolge übergibt, in der die zugehörigen Teilprobleme
 * von {@link createPartialProblem} geliefert wurden.
 *
 * @author Achim Linke, Ralf Kible, Dietmar Lippold
 */
public abstract class AbstractOrderedProblem implements SerializableProblem {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 645314578534296394L;

    /**
     * Gesamtlösung des Problems.
     */
    private Serializable finalSolution = null;

    /**
     * Liste der eingetroffenen, aber noch nicht verarbeiteten Teillösungen.
     */
    private HashMap partialSolutions = new HashMap();

    /**
     * Schlange der ausgegebenen Teilprobleme.
     */
    private LinkedList dispensedPartialProblems = new LinkedList();

    /**
     * Ruft <code>createPartialProblem</code> der implementierenden
     * Unterklasse auf und speichert es, bis die zugehörige Teillösung
     * geliefert wird.
     *
     * @param number  Die vorgeschlagene Anzahl zu generierender Teilprobleme.
     *
     * @return  Von der implementierenden Unterklasse erzeugtes Teilproblem.
     */
    public PartialProblem getPartialProblem(long number) {

        PartialProblem prob = createPartialProblem(number);

        if (prob == null) {
            return null;
        }

        // Der Schlange hinten anfügen.
        dispensedPartialProblems.addLast(prob);

        return prob;
    }

    /**
     * Übergibt eine neu eingetroffene Teillösung.
     *
     * @param parSol   Vom ComputeManager übermittelte Teillösung.
     * @param parProb  Referenz auf das Teilproblem, das bearbeitet wurde.
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        // Die Teillösung in die Hashmap einfügen.
        partialSolutions.put(parProb, parSol);

        // Die nächsten Teillösungen, falls diese vorhanden sind, an die
        // Unterklasse übergeben.
        while ((!dispensedPartialProblems.isEmpty())
               && (partialSolutions.containsKey(dispensedPartialProblems.getFirst()))
               && (finalSolution == null)) {

            // Die gesuchte Teillösung aus der Hashmap holen.
            PartialSolution userParSol =
                (PartialSolution) partialSolutions.get(dispensedPartialProblems.getFirst());

            // Teillösung an das Problem schicken.
            // finalSolution bleibt null, wenn die Gesamtlösung noch nicht
            // vorhanden ist.
            finalSolution = receivePartialSolution(userParSol);

            // Die verschickte Teillösung kann nun aus der Hashmap und der
            // Liste entfernt werden.
            partialSolutions.remove(dispensedPartialProblems.removeFirst());
        }
    }

    /**
     * Liefert die Gesamtlösung an den ComputeManager zurück oder
     * <code>null</code>, wenn diese noch nicht existiert.
     *
     * @return  Die Gesamtlösung.
     */
    public Serializable getSolution() {
        return finalSolution;
    }

    /**
     * Liefert das nächste Teilproblem oder <code>null</code>, wenn keines
     * mehr existiert.
     *
     * @param problemsExpected  Die vorgeschlagene Anzahl von Teilproblemen.
     *
     * @return  Ein Teilproblem.
     */
    protected abstract PartialProblem createPartialProblem(long problemsExpected);

    /**
     * Nimmt eine Teillösung entgegen und liefert die Gesamtlösung oder
     * <code>null</code>, falls diese noch nicht fertig ist.
     *
     * @param parSol  Die nächste fertige Teillösung für das Problem.
     *
     * @return  Gesamtlösung, falls diese bereits fertig ist, sonst
     *          <code>null</code>.
     */
    protected abstract Serializable receivePartialSolution(PartialSolution parSol);
}

