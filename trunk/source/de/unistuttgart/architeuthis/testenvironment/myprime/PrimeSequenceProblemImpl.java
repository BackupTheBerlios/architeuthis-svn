/*
 * file:        PrimeSequenceProblemImpl.java
 * created:
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


package de.unistuttgart.architeuthis.testenvironment.myprime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Mit dieser Klasse k�nnen entsprechend dem Interface <code>Problem</code>
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
     * Die gr��te Primzahl, die gesucht wird.
     */
    private int maxNumber = 2000000;

    /**
     * Liste der noch nicht verarbeiteten Teilprobleme.
     */
    private LinkedList partialProblems = new LinkedList();

    /**
     * Liste der eingetroffenen, aber noch nicht verarbeiteten L�sungen.
     */
    private HashMap solutions = new HashMap();

    /**
     * Liste, die alle PrimeNumbers mit einer gr��eren Nummer als
     * <code>minNumber</code> enth�lt.
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
     * PrimeNumbers in einem Array gespeichert werden m�ssen.
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
     * Konstruktor, der dem Problem die richtigen Grenzen f�r die
     * Primzahl-Bestimmung zuweist.
     *
     * @param minWert  Die Nummer der kleinsten Primzahl, die gesucht wird.
     * @param maxWert  Die Nummer der gr��ten Primzahl, die gesucht wird.
     */
    public PrimeSequenceProblemImpl(Integer minWert, Integer maxWert) {
        minNumber = minWert.intValue();
        maxNumber = maxWert.intValue();
    }

    /**
     * Methode, die vom ProblemManager aufgerufen wird, um dem Problem eine neu
     * eingetroffene L�sung zu �bermitteln.
     *
     * @param parSol   Vom ProblemManager �bermittelte Teill�sung.
     * @param parProb  Referenz auf das Teilproblem, das gel�st wurde
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        // Zuerst L�sung casten und in die Warteschlange einf�gen.
        PrimePartialSolutionImpl p = (PrimePartialSolutionImpl) parSol;
        solutions.put(parProb, p.getSolution());

        // Durch die Liste laufen, solange die L�sungen in der richtigen
        // Reihenfolge vorliegen
        while ((!dispensedProblems.isEmpty())
               && (solutions.containsKey(dispensedProblems.getFirst()))) {

            ArrayList partialSolutionList =
                (ArrayList) solutions.get(dispensedProblems.getFirst());

            if (partialSolutionList.size() + currentlyFound < minNumber) {
                // Auch mit der angef�gten L�sung wird minNumber nicht
                // �berschritten, also nur die Anzahl speichern
                currentlyFound = currentlyFound + partialSolutionList.size();
                solutions.remove(dispensedProblems.removeFirst());

            } else {
                // Sonst: Dieser Teil ist f�r die L�sung interessant und
                // wird in der Gesamtl�sung gesichert
                int temp = partialSolutionList.size();
                // Die noch nicht ben�tigten Elemente entfernen
                if (minNumber - currentlyFound > 1) {
                    partialSolutionList =
                        new ArrayList(
                            partialSolutionList.subList(
                                minNumber - currentlyFound - 1,
                                partialSolutionList.size() - 1));
                }
                solutions.remove(dispensedProblems.removeFirst());
                // Rest einf�gen in Gesamtl�sung
                finalSolution.addAll(partialSolutionList);
                // currentlyfound darf erst hier gesetzt werden, sonst ist es
                // in der for-Schleife falsch.
                currentlyFound = currentlyFound + temp;
            }
        }

    }

    /**
     * Liefert auf Anfrage vom ProblemManager ein Teilproblem zur�ck.
     * Beim ersten Aufruf werden au�erdem die Teilprobleme generiert.
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
            // die gew�nschte Primzahl gesucht wird
            double max = 10;
            // schrittweite ist die Menge der Zahlen, die in einem Teilproblem
            // durchsucht werden.
            long schrittweite = 0;

            firstCall = false;

            // Zuerst den Wert max errechnen, bis zu dem mindestens maxNumber
            // PrimeNumbers vorhanden sind, Absch�tzung nach Rosser und
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

            // Dann die �quidistante Schrittweite f�r die einzelnen
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

        // Dieser Teil wird immer ausgef�hrt, er liefert ein Teilproblem zur�ck
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
     * Liefert die Gesamtl�sung des Problems zur�ck, oder <code>null</code>,
     * falls diese noch nicht bekannt ist.
     *
     * @return Die Gesamtl�sung.
     */
    public Serializable getSolution() {
        // Falls die Ergenisliste bereits so viele Elemente enth�lt, wie
        // gew�nscht sind, dann wird dieser Bereich ausgeschitten und
        // zur�ckgegeben.
        if (finalSolution.size() >= (maxNumber - minNumber + 1)) {
            // ArrayList result = new
            // ArrayList(finalsolution.subList(0, maxNumber -minNumber +1));
            return new ArrayList(
                finalSolution.subList(0, maxNumber - minNumber + 1));
        }
        return null;
    }
}

