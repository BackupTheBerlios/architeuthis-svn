/*
 * file:        PrimeSequenceProblemImpl.java
 * created:
 * last change: 06.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.myprime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Mit dieser Klasse können entsprechend dem Interface <code>Problem</code>
 * Bereiche von PrimeNumbers verteilt berechnet werden.
 *
 * @author Ralf Kible, Achim Linke
 */
public class PrimeSequenceProblemImpl implements Problem {

    /**
     * Die kleinste Primzahl, die gesucht wird.
     */
    private int minNumber = 2000000;

    /**
     * Die größte Primzahl, die gesucht wird.
     */
    private int maxNumber = 2000000;

    /**
     * Liste der noch nicht verarbeiteten Teilprobleme.
     */
    private LinkedList partialProblems = new LinkedList();

    /**
     * Liste der eingetroffenen, aber noch nicht verarbeiteten Lösungen.
     */
    private HashMap solutions = new HashMap();

    /**
     * Liste, die alle PrimeNumbers mit einer größeren Nummer als
     * <code>minNumber</code> enthält.
     */
    private ArrayList finalSolution = new ArrayList();

    /**
     * Speichert, ob die Methode <code>getProblem()</code> zum ersten Mal
     * aufgerufen wurde. Falls das der Fall ist, werden zuerst Teilprobleme
     * generiert.
     */
    private boolean firstCall = true;

    /**
     * Speichert die Anzahl der in einer Reiher gefundenen PrimeNumbers, falls
     * diese kleiner als <code>minNumber</code> ist, so dass nicht alle
     * PrimeNumbers in einem Array gespeichert werden müssen.
     */
    private int currentlyFound = 0;

    /**
     * Schlange der ausgegebenen Teilprobleme
     */
    private LinkedList dispensedProblems = new LinkedList();

    /**
     * Konstruktor ohne Argumente, der wenig Sinn macht.
     */
    public PrimeSequenceProblemImpl() {

    }

    /**
     * Konstruktor, der dem Problem die richtigen Grenzen für die
     * Primzahl-Bestimmung zuweist.
     *
     * @param minWert  Die Nummer der kleinsten Primzahl, die gesucht wird.
     * @param maxWert  Die Nummer der größten Primzahl, die gesucht wird.
     */
    public PrimeSequenceProblemImpl(Integer minWert, Integer maxWert) {
        minNumber = minWert.intValue();
        maxNumber = maxWert.intValue();
    }

    /**
     * Methode, die vom ProblemManager aufgerufen wird, um dem Problem eine neu
     * eingetroffene Lösung zu übermitteln.
     *
     * @param parSol   Vom ProblemManager übermittelte Teillösung.
     * @param parProb  Referenz auf das Teilproblem, das gelöst wurde
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        // Zuerst Lösung casten und in die Warteschlange einfügen.
        PrimePartialSolutionImpl p = (PrimePartialSolutionImpl) parSol;
        solutions.put(parProb, p.getSolution());

        // Durch die Liste laufen, solange die Lösungen in der richtigen
        // Reihenfolge vorliegen
        while ((!dispensedProblems.isEmpty())
               && (solutions.containsKey(dispensedProblems.getFirst()))) {

            ArrayList partialSolutionList =
                (ArrayList) solutions.get(dispensedProblems.getFirst());

            if (partialSolutionList.size() + currentlyFound < minNumber) {
                // Auch mit der angefügten Lösung wird minNumber nicht
                // überschritten, also nur die Anzahl speichern
                currentlyFound = currentlyFound + partialSolutionList.size();
                solutions.remove(dispensedProblems.removeFirst());

            } else {
                // Sonst: Dieser Teil ist für die Lösung interessant und
                // wird in der Gesamtlösung gesichert
                int temp = partialSolutionList.size();
                // Die noch nicht benötigten Elemente entfernen
                if (minNumber - currentlyFound > 1) {
                    partialSolutionList =
                        new ArrayList(
                            partialSolutionList.subList(
                                minNumber - currentlyFound - 1,
                                partialSolutionList.size() - 1));
                }
                solutions.remove(dispensedProblems.removeFirst());
                // Rest einfügen in Gesamtlösung
                finalSolution.addAll(partialSolutionList);
                // currentlyfound darf erst hier gesetzt werden, sonst ist es
                // in der for-Schleife falsch.
                currentlyFound = currentlyFound + temp;
            }
        }

    }

    /**
     * Liefert auf Anfrage vom ProblemManager ein Teilproblem zurück.
     * Beim ersten Aufruf werden außerdem die Teilprobleme generiert.
     *
     * @param number  Vom ProblemManager erbetene Anzahl bereitzuhaltender
     *                Teilprobleme.
     *
     * @return  Neues Teilproblem zur Berechnung.
     */
    public PartialProblem getPartialProblem(long number) {

        // Erster Aufruf? Falls ja, dann Teilprobleme generieren.
        if (firstCall) {
            // in max wird gespeichert, bis zu welcher konkreten Zahl
            // die gewünschte Primzahl gesucht wird
            double max = 10;
            // schrittweite ist die Menge der Zahlen, die in einem Teilproblem
            // durchsucht werden.
            long schrittweite = 0;

            firstCall = false;

            // Zuerst den Wert max errechnen, bis zu dem mindestens maxNumber
            // PrimeNumbers vorhanden sind, Abschätzung nach Rosser und
            // Schoenfeld, "Approximate formulas for some prime numbers"
            if (maxNumber < 15) {
                max = 48;
            } else if (maxNumber < 7022) {
                max = Math.ceil(maxNumber * (Math.log(maxNumber)
                                + Math.log(Math.log(maxNumber)) - 0.5));
            } else {
                max = Math.ceil(maxNumber * (Math.log(maxNumber)
                                + Math.log(Math.log(maxNumber)) - 0.9385));
            }

            // Dann die äquidistante Schrittweite für die einzelnen
            // Teilprobleme berechnen.
            schrittweite = (long) (max / (2 * number));

            // Anhand der Schrittweite die Teilprobleme generieren.
            for (long i = 1; i <= 2 * number; i++) {
                partialProblems.addLast(
                    new PrimePartialProblemImpl(
                        (i - 1) * schrittweite + 1,
                        i * schrittweite));
            }

        }

        // Dieser Teil wird immer ausgeführt, er liefert ein Teilproblem zurück
        try {
            PartialProblem p = (PartialProblem) partialProblems.getFirst();
            partialProblems.removeFirst();
            dispensedProblems.addLast(p);
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Liefert die Gesamtlösung des Problems zurück, oder <code>null</code>,
     * falls diese noch nicht bekannt ist.
     *
     * @return Die Gesamtlösung.
     */
    public Serializable getSolution() {
        // Falls die Ergenisliste bereits so viele Elemente enthält, wie
        // gewünscht sind, dann wird dieser Bereich ausgeschitten und
        // zurückgegeben.
        if (finalSolution.size() >= (maxNumber - minNumber + 1)) {
            // ArrayList result = new
            // ArrayList(finalsolution.subList(0, maxNumber -minNumber +1));
            return new ArrayList(
                finalSolution.subList(0, maxNumber - minNumber + 1));
        }
        return null;
    }
}

