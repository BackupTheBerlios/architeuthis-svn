/*
 * file:        AbstractOrderedProblem.java
 * last change: 06.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.abstractproblems;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Abstrakte Klasse, die die Teill�sungen mittels {@link receivePartialSolution}
 *  in der gleichen Reihenfolge �bergibt, in der die zugeh�rigen Teilprobleme
 * von {@link createPartialProblem} geliefert wurden.
 *
 * @author Achim Linke, Ralf Kible, Dietmar Lippold
 */
public abstract class AbstractOrderedProblem implements SerializableProblem {

    /**
     * Gesamtl�sung des Problems.
     */
    private Serializable finalSolution = null;

    /**
     * Liste der eingetroffenen, aber noch nicht verarbeiteten Teill�sungen.
     */
    private HashMap partialSolutions = new HashMap();

    /**
     * Schlange der ausgegebenen Teilprobleme
     */
    private LinkedList dispensedPartialProblems = new LinkedList();

    /**
     * Ruft <code>createPartialProblem</code> der implementierenden Unterklasse
     * auf und beh�lt sich eine Referenz auf das erstellte Teilproblem.
     *
     * @param number  Vom ComputeManager vorgeschlagene Anzahl zu generierender
     *                Teilprobleme.
     *
     * @return  Von der implementierenden Unterklasse erzeugtes Teilproblem.
     */
    public PartialProblem getPartialProblem(long number) {

        PartialProblem prob = createPartialProblem(number);

        if (prob == null) {
            return null;
        }

        // der Schlange hinten anf�gen
        dispensedPartialProblems.addLast(prob);

        return prob;
    }

    /**
     * Methode, die vom ComputeManager aufgerufen wird, um dem Problem eine neu
     * eingetroffene L�sung zu �bermitteln.
     *
     * @param parSol   Vom ComputeManager �bermittelte Teill�sung.
     * @param parProb  Referenz auf das Teilproblem, das bearbeitet wurde.
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        //Die Teill�sung in die Hashmap einf�gen.
        partialSolutions.put(parProb, parSol);

        //solange die gesuchte Teill�sung vorhanden ist
        while ((!dispensedPartialProblems.isEmpty())
            && (partialSolutions.containsKey(dispensedPartialProblems.getFirst()))
            && (finalSolution == null)) {
            //Die gesuchte Teill�sung aus der Hashmap holen.
            PartialSolution userParSol =
                (PartialSolution) partialSolutions.get(dispensedPartialProblems.getFirst());

            //Teill�sung an das Problem schicken.
            //finalSolution bleibt null, wenn die Gesamtl�sung noch nicht
            //vorhanden ist
            finalSolution = receivePartialSolution(userParSol);

            //Die verschickte Teill�sung kann nun aus der Hashmap und der Liste
            //entfernt werden
            partialSolutions.remove(dispensedPartialProblems.removeFirst());
        }
    }

    /**
     * Liefert die Gesamtl�sung an den ComputeManager zur�ck oder
     * <code>null</code>, wenn diese noch nicht existiert.
     *
     * @return  Die Gesamtl�sung.
     */
    public Serializable getSolution() {
        return finalSolution;
    }

    /**
     * Stellt ein Teilproblem zur Verwaltung bereit.<p>
     * Diese Methode muss von einer konkreten Unterklasse implementiert werden.
     *
     * @param problemsExpected  gew�nschte Anzahl von Teilproblemen.
     * @return  ein Teilproblem
     */
    protected abstract PartialProblem createPartialProblem(long problemsExpected);

    /**
     * Nimmt eine Teill�sung entgegen und liefert die Gesamtl�sung oder
     * <code>null</code>, falls diese noch nicht fertig ist.<p>
     * Diese Methode muss von einer konkreten Unterklasse implementiert werden.
     *
     * @param parSol  Die n�chste fertige Teill�sung f�r das Problem.
     * @return  Gesamtl�sung, falls diese bereits fertig ist, sonst
     *          <code>null</code>
     */
    protected abstract Serializable receivePartialSolution(PartialSolution parSol);

}
