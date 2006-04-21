/*
 * file:        PrimeRangeProblemImpl.java
 * created:     <???>
 * last change: 21.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.prime.example;

import java.io.Serializable;
import java.util.ArrayList;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.AbstractFixedSizeProblem;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;

/**
 * Es werden alle Primzahlen zwischen anzugebenden Grenzen berechnet, indem
 * Teilintervalle mittels einer quadratischen Verteilungsfunktion berechnet
 * werden, welche als Teilprobleme an die Operatives geschickt werden.
 *
 * @author Achim Linke, Dietmar Lippold
 */
public class PrimeRangeProblemImpl extends AbstractFixedSizeProblem {

    /**
     * Die Zahl, ab der nach Primzahlen gesucht wird.
     */
    private long minValue;

    /**
     * Die Zahl, bis zu der nach Primzahlen gesucht wird.
     */
    private long maxValue;

    /**
     * Zu verwendender Konstruktor, der sofort die Grenzen zur Berechnung der
     * Primzahlen setzt.
     *
     * @param min  Kleinste Zahl, ab der Primzahlen gesucht werden.
     * @param max  Größte Zahl, bis zu der Primzahlen gesucht werden.
     */
    public PrimeRangeProblemImpl(long min, long max) {
        minValue = min;
        maxValue = max;
    }

    /**
     * Liefert ein Array mit allen Teilproblemen.
     *
     * @param suggestedParProbs  Vorgeschlagene Anzahl von Teilproblemen.
     *
     * @return  Array von Teilproblemen.
     */
    protected PartialProblem[] createPartialProblems(long suggestedParProbs) {
        PartialProblem[] parProbs = new PartialProblem[suggestedParProbs];
        int              nextValue;
        int              pIndex;

        // Das Array der Teilprobleme mit null initialisieren, falls das
        // Gesamtintervall kleiner als die Anzahl der Teilprobleme ist.
        for (int i = 0; i < suggestedParProbs; i++) {
            parProbs[i] = null;
        }

        // Die Größe der Teilintervalle ermitteln. Der Quotient von Größe des
        // Gesamtintervalls und vorgeschlagener Anzahl der Teilprobleme wird
        // auf den nächsten ganzen Wert aufgerundet.
        parItvSize = ((maxValue - minValue + suggestedParProbs - 1)
                      / suggestedParProbs);

        // Nummer und Anfangswert des nächstes Teilintervalls initialisieren.
        pIndex = 0;
        nextValue = minValue;

        while (nextValue + parItvSize < maxValue) {
            parProbs[pIndex] = new PrimePartialProblemImpl(nextValue,
                                                           nextValue + parItvSize);
            pIndex++;
            nextVal += parItvSize;
        }

        // Dem letzten Teilproblem das Restintervall zuweisen.
        parProbs[pIndex] = new PrimePartialProblemImpl(nextValue, maxValue);

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

