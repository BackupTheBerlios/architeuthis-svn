/*
 * file:        OrderedPrimeRangeProblem.java
 * created:     <???>
 * last change: 11.02.2004 by Jürgen Heit
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

package de.unistuttgart.architeuthis.testenvironment.prime.example;

import java.io.Serializable;
import java.util.ArrayList;

import de.unistuttgart.architeuthis.userinterfaces.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.PartialSolution;
import de.unistuttgart.architeuthis.abstractproblems.AbstractOrderedProblem;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;

/**
 * Test zur Benutzung der abstrakten Klasse <code>AbstractOrderedProblem</code>.
 * Es werden alle PrimeNumbers zwischen anzugebenden Grenzen berechnet, indem
 * Teilintervalle mittels einer quadratischen Verteilungsfunktion berechnet
 * werden, welche als Teilprobleme an die Operatives geschickt werden.
 *
 * @author Achim Linke
 */
public class OrderedPrimeRangeProblem extends AbstractOrderedProblem {

    /**
     * Die Zahl, ab der nach PrimeNumbers gesucht wird.
     */
    private long minValue = 1;

    /**
     * Die Zahl, bis zu der nach PrimeNumbers gesucht wird.
     */
    private long maxValue = 10;

    /**
     * Speichert alle PrimeNumbers im angegebenen Bereich.
     */
    private ArrayList finalSolution = new ArrayList();

    /**
     * Speichert, wieviele Teillösungen noch verbleiben, bis die Gesamtlösung
     * fertig ist.
     */
    private long solutionsRemaining = 0;

    /**
     * Flag zur Bestimmung der Koeffizienten, da beim ersten Mal die Anzahl
     * der gewünschten Teilprobleme vom ProblemManager mitgeteilt wird.
     */
    private boolean firstStart = true;

    /**
     * Koeffizient für die quadratische Verteilungsfunktion.
     */
    private float coeffA = 0;

    /**
     * Koeffizient für die quadratische Verteilungsfunktion.
     */
    private float coeffB = 0;

    /**
     * Aus diesem Wert werden in der Verteilungsfunktion die nächsten Grenzen
     * des Intervalls berechnet.
     */
    private long distributeValue = 0;

    /**
     * Der Anfangswert des Intervalls vom letzten rausgegebenem Teilproblem.
     */
    private long lastStartValue = 0;

    /**
     * Der Endwert des Intervalls vom letzten rausgegebenem Teilproblem.
     */
    private long lastEndValue = 0;

    /**
     * Zu verwendender Konstruktor, der sofort die Grenzen zur Berechnung der
     * PrimeNumbers setzt.
     *
     * @param min  Die Untergrenze zur Suche von PrimeNumbers.
     * @param max  Die Obergrenze zur Suche von PrimeNumbers.
     */
    public OrderedPrimeRangeProblem (long min, long max) {
        minValue = min;
        maxValue = max;
    }

    /**
     * Generiert ein neues Teilproblem zur Berechnung auf einem Operative.
     *
     * @param problemsExpected  Anzahl zu generierender Teilprobleme - wird
     *                          nur beim ersten Start ausgewertet.
     *
     * @return  Ein Teilproblem zur Berechnung.
     */
    protected PartialProblem createPartialProblem(long problemsExpected) {
        // Falls zum ersten Mal aufgerufen:
        if (firstStart) {
            firstStart = false;
            // Koeffizienten der Verteilungsfunktion bestimmen
            // siehe distributionFunction
            coeffA = (float) (-1.0 / ((float) problemsExpected * problemsExpected));
            coeffB = (float) (2.0 / (float) problemsExpected);
        }
        // Es wurden schon alle Zahlen verteilt, dann null zurückgeben.
        if (lastEndValue == maxValue) {
            return null;
        }

        // Sonst: mittels while-Schleife die nächste Intervall-Grenze bestimmen
        long start = 0;
        while (start <= lastStartValue) {
            start = (long) Math.floor(distributionFunction(distributeValue));
            distributeValue++;
        }
        // Das Ende des Intervalls bestimmen
        long end = (long) Math.floor(distributionFunction(distributeValue) - 1);
        // Falls das Ende nicht größer ist (kann bei kleinen Intervallen und
        // vielen Operatives auftreten), dann end neu setzen.
        if (start > end) {
            end = start;
        }
        // Werte für die nächsten überprüfungen sichern
        lastStartValue = start;
        lastEndValue =  end;
        // Anzahl der noch zu erwartenden Probleme hochzählen
        solutionsRemaining++;
        return new OrderedPrimeParProb(start, end);
    }

    /**
     * Berechnet die Verteilungsfunktion, gemäß welcher die Intervallgrenzen
     * bestimmt werden. Dabei handelt es sich um eine quadratische Funktion
     * f:x->a*x^2 + b*x, deren Koeffizienten a, b so bestimmt sind, dass gilt:
     * f(0) = minValue, f(maxProb) = maxValue und
     * f'(maxProb) = 0 (dabei sei maxProb die Zahl, die beim ersten Start für
     * <code>problemsExpected</code> in <code>createPartialProblem</code>
     * übergeben wurde).
     *
     * @param x  Der Wert, für den die Funktion berechnet wird.
     * @return  Der berechnete Funktionswert.
     */
    protected float distributionFunction(float x) {
        return (coeffA * x * x + coeffB * x) * (maxValue + 1 - minValue) + minValue;
    }

    /**
     * Erhält die nächste Teillösung und gibt die Gesamtlösung zurück, falls
     * diese schon fertig ist, sonst <code>null</code>.
     *
     * @param parSol  Eine Teillösung
     *
     * @return  Die Gesamtlösung
     */
    protected Serializable receivePartialSolution(PartialSolution parSol) {
        finalSolution.addAll((ArrayList)
            ((ContainerPartialSolution) parSol).getSolution());
        solutionsRemaining--;
        if ((solutionsRemaining == 0) && (lastEndValue == maxValue)) {
            return finalSolution;
        }
        return null;
    }

}
