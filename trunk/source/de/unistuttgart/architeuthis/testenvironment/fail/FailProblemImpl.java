/*
 * file:        FailProblemImpl.java
 * last change: 06.04.2006 von Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.testenvironment.fail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Ein fehlerhaftes Problem. Es liefert bei der Abfrage, ob eine Loesung schon
 * existiert, immer <code>null</code>.
 *
 * @author Achim Linke
 */
public class FailProblemImpl implements Problem {

    /**
     * Die Anzahl der erzeugten Teilprobleme.
     */
    private long solutionNumber = 0;

    /**
     * Liste der ausgegebenen Teilprobleme.
     */
    private LinkedList partialProblems = new LinkedList();

    /**
     * Liste der eingetroffenen, aber noch nicht verarbeiteten Lösungen.
     */
    private HashMap solutions = new HashMap();

    /**
    * Speichert, ob die Methode <code>getProblem()</code> zum ersten Mal
    * aufgerufen wurde. Falls das der Fall ist, werden zuerst Teilprobleme
    * generiert.
    */
    private boolean firstCall = true;

    /**
     * Schlange der ausgegebenen Teilprobleme.
     */
    private LinkedList dispensedProblems = new LinkedList();

    /**
     * Gibt an, ob schon zu allen Teilproblemen Teillösungen übergeben wurden.
     */
    private boolean ready = false;

    /**
     * Methode, die vom ProblemManager aufgerufen wird, um dem Problem eine
     * neu eingetroffene Lösung zu übermitteln.
     *
     * @param parSol   Vom ProblemManager übermittelte Teillösung.
     * @param parProb  Referenz auf das Teilproblem, zu dem die Teillösung
     *                 ermittelt wurde.
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        // Zuerst Lösung casten und in die Warteschlange einfügen.
        FailPartialSolutionImpl p = (FailPartialSolutionImpl) parSol;
        solutions.put(parProb, p.getNumber());

        System.out.println("Loesung von problem nr "
                           + p.getNumber() + " bekommen");

        // Durch die Liste laufen, solange die Lösungen in der richtigen
        // Reihenfolge vorliegen.
        while ((!dispensedProblems.isEmpty())
            && (solutions.containsKey(dispensedProblems.getFirst()))) {
            // Das Problem, das gerade in dieser Reihenfolge bearbeitet werden
            // soll, wird aus der solutions-HashMap entnommen
            Long problemProcessed =
                (Long) solutions.get(dispensedProblems.getFirst());
            if (problemProcessed.longValue() == solutionNumber) {
                ready = true;
                System.out.println("ready auf true gesetzt");
            }
            dispensedProblems.removeFirst();
        }
    }

    /**
     * Liefert auf Anfrage vom ProblemManager ein Teilproblem zurück. Beim
     * ersten Aufruf werden außerdem die Teilprobleme generiert.
     *
     * @param suggestedPartProbs  Vom ProblemManager erbetene Anzahl
     *                            bereitzuhaltender Teilprobleme.
     */
    public PartialProblem getPartialProblem(long suggestedPartProbs) {

        // Erster Aufruf? Falls ja, dann Teilprobleme generieren.
        if (firstCall) {
            firstCall = false;
            long max = 6 * suggestedPartProbs ;
            System.out.println("Generiere maximal" + max + " Probleme");
            solutionNumber = (long) Math.round(max * Math.random());
            if (solutionNumber == 0) {
                solutionNumber = 1;
            }

            for (long i = 1; i <= solutionNumber; i++) {
                System.out.println("Problem " + i + " generiert!");
                partialProblems.addLast(new FailPartialProblemImpl(i));
            }
            System.out.println("Probleme generiert");
        }
        // Dieser Teil wird immer ausgeführt, er liefert ein Teilproblem zurück
        try {
            if (!partialProblems.isEmpty()) {
                FailPartialProblemImpl p =
                    (FailPartialProblemImpl) partialProblems.getFirst();
                System.out.println("Gebe Problem raus.");
                partialProblems.removeFirst();
                dispensedProblems.addLast(p);
                return p;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert die Gesamtlösung des Problems zurück, oder <code>null</code>,
     * falls diese noch nicht bekannt ist.
     */
    public Serializable getSolution() {
        return null;
    }
}

