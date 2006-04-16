/*
 * file:        PrimeRangeProblemImpl.java
 * created:
 * last change: 16.04.2006 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
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
 */


package de.unistuttgart.architeuthis.testenvironment.fullprime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Mit dieser Klasse können entsprechend dem Interface <code>Problem</code>
 * PrimeNumbers innerhalb eines Zahlenbereichs verteilt berechnet werden.
 *
 * @author Ralf Kible, Achim Linke
 */
public class PrimeRangeProblemImpl implements Problem {

    /**
     * Speichert, wieviele Probleme insgesamt generiert wurden
     */
    private long probsGenerated = 1;

    /**
     * Speichert, wieviele Probleme bisher verarbeitet wurden.
     */
    private long probsProcessed = 0;

    /**
     * Die kleinste Zahl, ab der PrimeNumbers gesucht werden.
     */
    private long minNumber = 200000;

    /**
     * Die größte Zahl, bis zu der PrimeNumbers gesucht werden.
     */
    private long maxNumber = 201000;

    /**
     * Liste der noch nicht verarbeiteten Teilprobleme.
     */
    private LinkedList partialProblems = new LinkedList();

    /**
     * Liste der eingetroffenen, aber noch nicht verarbeiteten Lösungen.
     */
    private HashMap solutions = new HashMap();

    /**
     * Liste, die die bisher eingetroffenen PrimeNumbers hält.
     */
    private ArrayList finalSolution = new ArrayList();

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

    /**
     * Konstruktor ohne Argumente, der wenig Sinn macht.
     */
    public PrimeRangeProblemImpl () {
    }

    /**
     * Konstruktor, der dem Problem die richtigen Grenzen für die
     * Primzahl-Bestimmung zuweist.
     *
     * @param minWert  Kleinste Zahl, ab der PrimeNumbers gesucht werden.
     * @param maxWert  Größte Zahl, bis zu der PrimeNumbers gesucht werden.
     */
    public PrimeRangeProblemImpl(Long minWert, Long maxWert) {
        minNumber = minWert.longValue();
        maxNumber = maxWert.longValue();
    }

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
        PrimePartialSolutionImpl p = (PrimePartialSolutionImpl) parSol;
        solutions.put(parProb, p.getSolution());

        // Durch die Liste laufen, solange die Lösungen in der richtigen
        // Reihenfolge vorliegen
        while ((!dispensedProblems.isEmpty())
            && (solutions.containsKey(dispensedProblems.getFirst()))) {
            // Das Problem, das gerade in dieser Reihenfolge bearbeitet werden
            // soll, wird aus der solutions-HashMap entnommen
            ArrayList partialSolutionList = (ArrayList)
                    solutions.get(dispensedProblems.getFirst());
            // Die darin enthaltenen PrimeNumbers werden an die Lösung angefügt.
            finalSolution.addAll(partialSolutionList);
            // Die Anzahl der verarbeiteten Probleme wird hochgesetzt.
            probsProcessed++;
            // Nächste Teillösung suchen
            dispensedProblems.removeFirst();
        }

    }

    /**
     * Liefert auf Anfrage vom ProblemManager ein Teilproblem zurück.
     * Beim ersten Aufruf werden außerdem die Teilprobleme generiert.
     *
     * @param suggestedPartProbs  Vom ProblemManager erbetene Anzahl
     *                            bereitzuhaltender Teilprobleme.
     *
     * @return  Neues Teilproblem zur Berechnung.
     */
    public PartialProblem getPartialProblem(long suggestedPartProbs) {

        // Erster Aufruf? Falls ja, dann Teilprobleme generieren.
        if (firstCall) {
            // Festlegen der Bereiche für die Teilprobleme
            long schrittweite = (maxNumber - minNumber) / suggestedPartProbs;
            // Beginn des aktuellen Teilbereichs
            long current = minNumber;
            // TeilProblem Identifikationsnummer
            long probNumber = 1;

            firstCall = false;

            while (current + schrittweite < maxNumber) {
                partialProblems.addLast(
                    new PrimePartialProblemImpl(current,
                            current + schrittweite));
                current += schrittweite;
                probNumber++;
            }
            // In der Schleife wurde nur gleich große Teilbereiche erzeugt.
            // Evtl. ist der letzte Teilbereich kleiner:
            partialProblems.addLast(
                new PrimePartialProblemImpl(current,
                        maxNumber));
            // Nun noch die Anzahl der gesamt generierten Teilprobleme sichern
            probsGenerated = partialProblems.size();
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
     * @return  Die Gesamtlösung.
     */
    public Serializable getSolution() {
        // Wenn bereits alle generierten Probleme verarbeitet wurden:
        if (probsGenerated == probsProcessed) {
            return finalSolution;
        }
        return null;
    }
}

