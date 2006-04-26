/*
 * file:        PrimeSequenceProblemImpl.java
 * created:     <???>
 * last change: 26.04.2006 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
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
     * Liste, die alle Primzahlen mit einer gr��eren Nummer als
     * <code>minNumber</code> enth�lt.
     */
    private ArrayList finalSolution = new ArrayList();

    /**
     * Die Nummer der kleinste Primzahl, die gesucht wird.
     */
    private long minNumber = 2000000;

    /**
     * Die Nummer der gr��ten Primzahl, die gesucht wird.
     */
    private long maxNumber = 2000000;

    /**
     * Speichert die Anzahl der bisher ermittelten Primzahlen, so dass nicht
     * alle Primzahlen gespeichert werden m�ssen.
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
     * Konstruktor, der dem Problem die richtigen Grenzen f�r die
     * Primzahl-Bestimmung zuweist.
     *
     * @param minWert  Die Nummer der kleinsten Primzahl, die gesucht wird.
     * @param maxWert  Die Nummer der gr��ten Primzahl, die gesucht wird.
     */
    public PrimeSequenceProblemImpl(Long minWert, Long maxWert) {

        minNumber = minWert.longValue();
        maxNumber = maxWert.longValue();
    }

    /**
     * Liefert ein neues Teilproblem oder <code>null</code>, wenn es kein
     * Teilproblem mehr gibt. Beim ersten Aufruf werden au�erdem die
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
            // Primzahlen vorhanden sind. Absch�tzung nach Rosser und
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

        // N�chstes Teilproblem aus der Liste liefern.
        if (partialProblems.isEmpty()) {
            return null;
        } else {
            nextParProb = (PartialProblem) partialProblems.getFirst();
            partialProblems.removeFirst();
            return nextParProb;
        }
    }

    /**
     * Nimmt die n�chste Teill�sung entgegen und liefert die Gesamtl�sung,
     * wenn diese schon existert oder sonst den Wert <code>null</code>.
     *
     * @param parSol  Die n�chste fertige Teill�sung f�r das Problem.
     *
     * @return  Die Gesamtl�sung, falls diese bereits fertig ist, sonst
     *          <code>null</code>.
     */
    protected Serializable receivePartialSolution(PartialSolution parSol) {
        ContainerPartialSolution parSolContainer;
        ArrayList                partialSolutionList;
        int                      newPrimesNumber;

        // Die Liste der n�chsten Primzahlen ermitteln.
        parSolContainer = (ContainerPartialSolution) parSol;
        partialSolutionList = (ArrayList) parSolContainer.getPartialSolution();

        newPrimesNumber = partialSolutionList.size();

        if (newPrimesNumber + currentlyFound < minNumber) {

            // Auch mit der angef�gten L�sung wird minNumber nicht
            // �berschritten, also nur die Anzahl speichern.
            currentlyFound += newPrimesNumber;

        } else {

            // Dieser Teil ist f�r die L�sung interessant und wird der 
            // Gesamtl�sung hinzugef�gt.

            // Die noch nicht ben�tigten Elemente entfernen.
            if (minNumber - currentlyFound > 1) {
                int numberToAdd = (int) (minNumber - currentlyFound - 1);
                partialSolutionList =
                    new ArrayList(
                        partialSolutionList.subList(numberToAdd,
                                                    newPrimesNumber - 1));
            }

            // Rest einf�gen in Gesamtl�sung.
            finalSolution.addAll(partialSolutionList);

            // Die Anzahl der ermittelten Primzahlen erh�hen.
            currentlyFound += newPrimesNumber;
        }

        // Falls die Ergebnisliste bereits so viele Elemente enth�lt, wie
        // gew�nscht sind, dann diesen Bereich extrahieren und zur�ckgegeben.
        if (finalSolution.size() >= (maxNumber - minNumber + 1)) {
            int numberToAdd = (int) (maxNumber - minNumber + 1);
            return new ArrayList(finalSolution.subList(0, numberToAdd));
        } else {
            return null;
        }
    }
}

