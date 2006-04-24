/*
 * file:        PrimeRangeProblemImpl.java
 * created:     <???>
 * last change: 24.04.2006 by Dietmar Lippold
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
import java.util.List;
import java.util.ArrayList;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.AbstractFixedSizeProblem;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;
import de.unistuttgart.architeuthis.testenvironment.prime.PrimePartialProblemImpl;

/**
 * Es werden alle Primzahlen zwischen anzugebenden Grenzen berechnet, indem
 * das Intervall der Quadratwurzeln des vorgegebenen Gesamtintervall in gleich
 * große Teilintervalle zerlegt wird die deren Quadrate die Teilprobleme
 * definieren. Gegenüber einer gleichmäßigen Zerlegung des Gesamtintervalls
 * ist der Rechenaufwand für die Teilprobleme bei dieser Vorgehensweise
 * (abgesehen von sehr kleinen Gesamtintervallen) gleicher, was zu einer
 * effizienteren Berechnung des Gesamtproblems führt.
 *
 * @author Ralf Kible, Achim Linke, Dietmar Lippold
 */
public class PrimeRangeProblemImpl extends AbstractFixedSizeProblem {

    /**
     * Die Zahl, ab der nach Primzahlen gesucht wird.
     */
    private long minValue = 200000;

    /**
     * Die Zahl, bis zu der nach Primzahlen gesucht wird.
     */
    private long maxValue = 201000;

    /**
     * Konstruktor ohne Argumente, der nur benutzt wird, um die Klasse mit den
     * vorgegebenen Werten von <code>minValue</code> und <code>maxValue</code>
     * zu testen.
     */
    public PrimeRangeProblemImpl () {
    }

    /**
     * Zu verwendender Konstruktor, der sofort die Grenzen zur Berechnung der
     * Primzahlen setzt.
     *
     * @param min  Kleinste Zahl, ab der Primzahlen gesucht werden.
     * @param max  Größte Zahl, bis zu der Primzahlen gesucht werden.
     */
    public PrimeRangeProblemImpl(Long min, Long max) {
        minValue = min.longValue();
        maxValue = max.longValue();
    }

    /**
     * Liefert ein Array mit allen Teilproblemen.
     *
     * @param suggestedParProbs  Vorgeschlagene Anzahl von Teilproblemen.
     *
     * @return  Array von Teilproblemen.
     */
    protected PartialProblem[] createPartialProblems(int suggestedParProbs) {
        PartialProblem[] parProbs = new PartialProblem[suggestedParProbs];
        double           parItvSqrtSize, upperBoundSqrt;
        long             lowerBound, upperBound;
        int              pIndex;

        // Das Array der Teilprobleme mit null initialisieren, falls das
        // Gesamtintervall kleiner als die Anzahl der Teilprobleme ist.
        for (int i = 0; i < suggestedParProbs; i++) {
            parProbs[i] = null;
        }

        // Die Differenz der Wurzeln der Grenzen der Teilintervalle ermitteln.
        parItvSqrtSize = ((Math.sqrt(maxValue) - Math.sqrt(minValue))
                          / suggestedParProbs);

        // Das erste Teilproblem erzeugen.
        upperBoundSqrt = Math.sqrt(minValue) + parItvSqrtSize;
        upperBound = Math.min(maxValue,
                              (long) Math.ceil(Math.pow(upperBoundSqrt, 2)));
        parProbs[0] = new PrimePartialProblemImpl(minValue, upperBound);

        // Nummer und Anfangswert des nächstes Teilintervalls initialisieren.
        pIndex = 1;
        lowerBound = upperBound + 1;
        upperBoundSqrt = Math.max(Math.sqrt(lowerBound),
                                  upperBoundSqrt + parItvSqrtSize);
        upperBound = (long) Math.ceil(Math.pow(upperBoundSqrt, 2));

        // Die weiteren Teilintervalle und Teilprobleme erzeugen.
        while ((upperBound < maxValue) && (pIndex < parProbs.length - 1)) {
            parProbs[pIndex] = new PrimePartialProblemImpl(lowerBound, upperBound);
            pIndex++;
            lowerBound = upperBound + 1;
            upperBoundSqrt = Math.max(Math.sqrt(lowerBound),
                                      upperBoundSqrt + parItvSqrtSize);
            upperBound = (long) Math.floor(Math.pow(upperBoundSqrt, 2));
        }

        // Dem letzten Teilproblem das Restintervall zuweisen, falls es noch
        // ein solches gibt.
        if (maxValue >= lowerBound) {
            parProbs[pIndex] = new PrimePartialProblemImpl(lowerBound, maxValue);
        }

        return parProbs;
    }

    /**
     * Erstellt eine Gesamtlösung aus den übergebenen Teillösungen. Sowohl die
     * Teillösungen wie die Gesamtlösung sind Listen von Primzahlen.
     *
     * @param partialSolutions  Alle eingegangenen Teillösungen.
     *
     * @return  Die Gesamtlösung.
     */
    protected Serializable createSolution(PartialSolution[] partialSolutions) {
        ArrayList                finalSolution;
        ContainerPartialSolution parSol;

        finalSolution = new ArrayList();
        for (int i = 0; i < partialSolutions.length; i++) {
            parSol = (ContainerPartialSolution) partialSolutions[i];
            finalSolution.addAll((List) parSol.getPartialSolution());
        }
        return finalSolution;
    }
}

