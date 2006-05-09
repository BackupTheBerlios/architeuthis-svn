/*
 * file:        RandomProblemImpl.java
 * created:
 * last change: 24.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.random;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Implementierung von Problem, die nur zu Testzwecken gedacht ist.
 * Es wird eine zufällige Anzahl von Teilproblemen generiert, und auf eine
 * zufällige Teillösung gewartet.
 *
 * @author Ralf Kible
 */
public class RandomProblemImpl implements Problem {

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
     * Schlange der ausgegebenen Teilprobleme
     */
    private LinkedList dispensedProblems = new LinkedList();

    private boolean ready = false;

    private int helpNumber = 1;

    private int ausgegebeneParProbs = 0;

    private int erhalteneParProbs = 0;

    private int sugPartProbNumber;

    private int solutionNumber = 0;

    private LinkedList partialProblems = new LinkedList();

    /**
     * Methode, die vom ProblemManager aufgerufen wird, um dem Problem eine neu
     * eingetroffene Lösung zu übermitteln.
     *
     * @param parSol   Vom ProblemManager übermittelte Teillösung.
     * @param parProb  Referenz auf das Teilproblem, zu dem die Teillösung
     *                 ermittelt wurde.
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        // Zuerst Lösung casten und in die Warteschlange einfügen.
        RandomPartialSolutionImpl p = (RandomPartialSolutionImpl) parSol;
        solutions.put(parProb, p.getNumber());
        erhalteneParProbs++;
        if (erhalteneParProbs == helpNumber) {
            ausgegebeneParProbs = 0;
            erhalteneParProbs = 0;
            helpNumber = (int) Math.round(sugPartProbNumber * Math.random());
            if (helpNumber == 0) {
                helpNumber = 1;
            }
        }

        System.out.println("Loesung von problem nr "
                           + p.getNumber() + " bekommen");

        // Durch die Liste laufen, solange die Lösungen in der richtigen
        // Reihenfolge vorliegen
        while ((!dispensedProblems.isEmpty())
               && (solutions.containsKey(dispensedProblems.getFirst()))) {

            // Das Problem, das gerade in dieser Reihenfolge bearbeitet werden
            // soll, wird aus der solutions-HashMap entnommen
            Integer problemProcessed =
                (Integer) solutions.get(dispensedProblems.getFirst());
            if (problemProcessed.intValue() == solutionNumber) {
                ready = true;
                System.out.println("ready auf true gesetzt");
            }
            dispensedProblems.removeFirst();
        }

    }

    /**
     * Liefert auf Anfrage vom ProblemManager ein Teilproblem zurück.
     * Beim ersten Aufruf werden außerdem die Teilprobleme generiert.
     *
     * @param suggestedPartProbNumber  Vom ProblemManager erbetene Anzahl
     *                                 bereitzuhaltender Teilprobleme
     */
    public PartialProblem getPartialProblem(int suggestedPartProbNumber) {

        // Erster Aufruf? Falls ja, dann Teilprobleme generieren.
        if (firstCall) {

            firstCall = false;
            sugPartProbNumber = suggestedPartProbNumber;
            helpNumber = (int) Math.round(sugPartProbNumber * Math.random());
            if (helpNumber == 0) {
                helpNumber = 1;
            }

            //max gibt die maximale Anzahl von zu generierenden Teilproblemen an
            //max liegt im Bereich von 1 bis 5*suggestedPartProbNumber + 1
            int max =
                (int) Math.round(Math.random() * 5 * suggestedPartProbNumber) + 1;
            System.out.println("generiere " + max + " Probleme");

            //Lösung vom Problem erreicht, sobald das Teilproblem solutionNumber
            //berechnet wurde
            solutionNumber = (int) Math.round(max * Math.random());
            if (solutionNumber == 0) {
                solutionNumber = 1;
            }
            System.out.println(
                "Suche Loesung von Teilproblem Nr." + solutionNumber);
            for (int i = 1; i <= max; i++) {
                System.out.println("Problem " + i + " generiert!");
                partialProblems.addLast(new RandomPartialProblemImpl(i));
            }
            System.out.println("Probleme generiert");
        }

        // Dieser Teil wird immer ausgeführt, er liefert ein Teilproblem
        // zurück.
        try {
            if (!partialProblems.isEmpty()) {
                if (ausgegebeneParProbs <= helpNumber) {
                    RandomPartialProblemImpl p =
                        (RandomPartialProblemImpl) partialProblems.getFirst();
                    System.out.println("Gebe Problem raus");
                    partialProblems.removeFirst();
                    ausgegebeneParProbs++;
                    dispensedProblems.addLast(p);
                    return p;
                } else {
                    //falls noch nicht genügend Teilprobleme zurückgekommen
                    //sind, gebe keine neuen Teilprobleme aus
                    return null;
                }
            }
            //falls es keine Teilprobleme mehr gibt
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
        if (ready) {
            return "Fertig!";
        }
        return null;
    }
}

