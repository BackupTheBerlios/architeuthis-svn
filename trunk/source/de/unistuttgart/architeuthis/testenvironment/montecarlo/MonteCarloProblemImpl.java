/*
 * file:        MonteCarloProblemImpl.java
 * created:     24.05.2003
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


package de.unistuttgart.architeuthis.testenvironment.montecarlo;

import java.io.Serializable;

import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Verwaltet das Verteilen und Zusammenf�gen der Teilprobleme, sowie die
 * Gesamtl�sung.
 *
 * @author Achim Linke
 */
public class MonteCarloProblemImpl implements Problem {

    /**
     * Hier werden die einzelnen Teill�sungen aufaddiert.
     */
    private Double zwischenErgebnis = new Double(0);

    /**
     * Ein flag, um zu wissen, ob <code>getPartialProblem</code> das erste Mal
     * aufgerufen wurde.
     */
    private boolean erstesmal = true;

    /**
     * Die Anzahl der zu erzeugenden Teilprobleme.
     */
    private long anzahlPartialProbs;

    /**
     * Die Anzahl der Berechungen, um dann den Mittelwert f�r Pi zu berechnen.
     */
    private final long anzahlVersucheGesamt = 10000000;

    /**
     * Die Anzahl der Teilprobleme, die noch in Berechnung sind, sowie die
     * berechneten Teilprobleme.
     */
    private long counterausgabe = 0;

    /**
     * Die Anzahl der Teilprobleme, die berechnet sind.
     */
    private long countererhalten = 0;

    /**
     * Addiert die Teill�sungen auf und z�hlt einen Z�hler hoch f�r die
     * erhaltenen Teill�sungen.
     *
     * @param parSol   Einzelne Teill�sung die aufaddiert wird zu den
     *                 bisherigen Teill�sungen.
     * @param parProb  Referenz auf das Teilproblem, zu dem die Teill�sung
     *                 ermittelt wurde.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#collectPartialSolution(de.unistuttgart.architeuthis.systeminterfaces.PartialSolution)
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        PartialSolutionImpl ps = (PartialSolutionImpl) parSol;
        zwischenErgebnis = new Double(zwischenErgebnis.doubleValue()
                                      + ps.getPartialSolution());
        countererhalten += 1;
    }

    /**
     * Gibt die Teilprobleme raus. Beim ersten Aufruf wird die Anzahl der
     * Teilprobleme festgelegt.
     *
     * @param number  Ist nur beim ersten Aufruf von Bedeutung. Dies ist die
     *                gew�nschte Anzahl der zu erzeugenden Teilprobleme.
     *
     * @return  das zu berechnende Teilproblem.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#getPartialProblem(long)
     */
    public PartialProblem getPartialProblem(long number) {

        // Abfrage, ob die Methode das erstemal aufgerufen wurde
        if (erstesmal) {
            erstesmal = false;
            //Amzahl der Teilprobleme festlegen
            if (number == 0) {
                anzahlPartialProbs = 1;
            } else {
                anzahlPartialProbs = number;
            }
            //neues Teilproblem erzeugen und Z�hler auf 1 setzen
            counterausgabe = 1;
            return new PartialProblemImpl(
                anzahlVersucheGesamt / anzahlPartialProbs);
        } else if (counterausgabe < anzahlPartialProbs) {
            //neues Teilproblem erzeugen und Z�hler hochz�hlen
            counterausgabe++;
            return new PartialProblemImpl(
                anzahlVersucheGesamt / anzahlPartialProbs);
        } else {
            // alle Teilprobleme ausgegeben
            return null;
        }
    }

    /**
     * �berpr�ft, ob alle Teilprobleme erhalten wurden und gibt gegebenfalls
     * die Gesamtl�sung zur�ck. Ist diese noch nicht vorhanden, wird
     * <code>null</code> zur�ckgegeben.
     *
     * @return die Gesamtl�sung
     * @see de.unistuttgart.architeuthis.systeminterfaces.Problem#getSolution()
     */
    public Serializable getSolution() {
        if (countererhalten == anzahlPartialProbs) {
            //wenn alle ausgegeben Teilprobleme berechnet wurden
            return (Serializable) new Double(
                zwischenErgebnis.doubleValue() / anzahlPartialProbs);
        } else {
            //L�sung noch nicht vorhanden
            return null;
        }
    }
}

