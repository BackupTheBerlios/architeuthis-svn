/*
 * file:        PrimeSequenceProblemImpl.java
 * created:     <???>
 * last change: 26.04.2006 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
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


package de.unistuttgart.architeuthis.testenvironment.prime.advanced;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.AbstractOrderedProblem;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;

/**
 * Klasse dient der Ermittlung von Primzahlen aus einen Bereichs von Nummern
 * der Primzahlen.
 *
 * @author Ralf Kible, Achim Linke, Dietmar Lippold
 */
public class PrimeSequenceProblemImpl extends AbstractOrderedProblem {

    /**
     * Liste der noch nicht ausgegebenen Teilprobleme.
     */
    private LinkedList partialProblems = new LinkedList();

    /**
     * Liste, die alle Primzahlen mit einer größeren Nummer als
     * <code>minNumber</code> enthält.
     */
    private ArrayList finalSolution = new ArrayList();

    /**
     * Die Nummer der kleinste Primzahl, die gesucht wird.
     */
    private long minNumber = 2000000;

    /**
     * Die Nummer der größten Primzahl, die gesucht wird.
     */
    private long maxNumber = 2000000;

    /**
     * Speichert die Anzahl der bisher ermittelten Primzahlen, so dass nicht
     * alle Primzahlen gespeichert werden müssen.
     */
    private long currentlyFound = 0;

    /**
     * Gibt an, ob die Methode <code>createPartialProblem()</code> zum ersten
     * Mal aufgerufen wurde. Falls das der Fall ist, werden zuerst
     * Teilprobleme generiert.
     */
    private boolean firstCall = true;

    /**
     * Konstruktor ohne Argumente, der nur benutzt wird, um die Klasse mit den
     * vorgegebenen Werten von <code>minValue</code> und <code>maxValue</code>
     * zu testen.
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
    public PrimeSequenceProblemImpl(Long minWert, Long maxWert) {

        minNumber = minWert.longValue();
        maxNumber = maxWert.longValue();
    }

    /**
     * Liefert ein neues Teilproblem oder <code>null</code>, wenn es kein
     * Teilproblem mehr gibt. Beim ersten Aufruf werden außerdem die
     * Teilprobleme generiert.
     *
     * @param parProbsSuggested  Die vom ProblemManager vorgeschlagene Anzahl
     *                           bereitzuhaltender Teilprobleme.
     *
     * @return  Neues Teilproblem zur Berechnung.
     */
    protected PartialProblem createPartialProblem(int parProbsSuggested) {
        PrimeRangeProblemImpl primeRange;
        PartialProblem[]      createdParProbs;
        PartialProblem        nextParProb;
        long                  maxValue;

        // Falls erster Aufruf, dann Teilprobleme generieren.
        if (firstCall) {
            firstCall = false;

            // Den Wert maxValue errechnen, bis zu dem mindestens maxNumber
            // Primzahlen vorhanden sind. Abschätzung nach Rosser und
            // Schoenfeld, "Approximate formulas for some prime numbers".
            if (maxNumber < 15) {
                maxValue = 48;
            } else if (maxNumber < 7022) {
                maxValue = (long) Math.ceil(maxNumber
                                            * (Math.log(maxNumber)
                                               + Math.log(Math.log(maxNumber))
                                               - 0.5));
            } else {
                maxValue = (long) Math.ceil(maxNumber
                                            * (Math.log(maxNumber)
                                               + Math.log(Math.log(maxNumber))
                                               - 0.9385));
            }

            // Teilprobleme erzeugen und in eine Liste aufnehmen.
            primeRange = new PrimeRangeProblemImpl(2, maxValue);
            createdParProbs = primeRange.createPartialProblems(2 * parProbsSuggested);
            partialProblems = new LinkedList(Arrays.asList(createdParProbs));
        }

        // Nächstes Teilproblem aus der Liste liefern.
        if (partialProblems.isEmpty()) {
            return null;
        } else {
            nextParProb = (PartialProblem) partialProblems.getFirst();
            partialProblems.removeFirst();
            return nextParProb;
        }
    }

    /**
     * Nimmt die nächste Teillösung entgegen und liefert die Gesamtlösung,
     * wenn diese schon existert oder sonst den Wert <code>null</code>.
     *
     * @param parSol  Die nächste fertige Teillösung für das Problem.
     *
     * @return  Die Gesamtlösung, falls diese bereits fertig ist, sonst
     *          <code>null</code>.
     */
    protected Serializable receivePartialSolution(PartialSolution parSol) {
        ContainerPartialSolution parSolContainer;
        ArrayList                partialSolutionList;
        int                      newPrimesNumber;

        // Die Liste der nächsten Primzahlen ermitteln.
        parSolContainer = (ContainerPartialSolution) parSol;
        partialSolutionList = (ArrayList) parSolContainer.getPartialSolution();

        newPrimesNumber = partialSolutionList.size();

        if (newPrimesNumber + currentlyFound < minNumber) {

            // Auch mit der angefügten Lösung wird minNumber nicht
            // überschritten, also nur die Anzahl speichern.
            currentlyFound += newPrimesNumber;

        } else {

            // Dieser Teil ist für die Lösung interessant und wird der 
            // Gesamtlösung hinzugefügt.

            // Die noch nicht benötigten Elemente entfernen.
            if (minNumber - currentlyFound > 1) {
                int numberToAdd = (int) (minNumber - currentlyFound - 1);
                partialSolutionList =
                    new ArrayList(
                        partialSolutionList.subList(numberToAdd,
                                                    newPrimesNumber - 1));
            }

            // Rest einfügen in Gesamtlösung.
            finalSolution.addAll(partialSolutionList);

            // Die Anzahl der ermittelten Primzahlen erhöhen.
            currentlyFound += newPrimesNumber;
        }

        // Falls die Ergebnisliste bereits so viele Elemente enthält, wie
        // gewünscht sind, dann diesen Bereich extrahieren und zurückgegeben.
        if (finalSolution.size() >= (maxNumber - minNumber + 1)) {
            int numberToAdd = (int) (maxNumber - minNumber + 1);
            return new ArrayList(finalSolution.subList(0, numberToAdd));
        } else {
            return null;
        }
    }
}

