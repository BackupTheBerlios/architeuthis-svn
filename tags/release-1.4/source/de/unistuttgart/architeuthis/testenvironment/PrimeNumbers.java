/*
 * file:        PrimeNumbers.java
 * created:     09. Juni 2003
 * last change: 11.02.2004 by J�rgen Heit
 * developers:  Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
 * 
 * 
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */

package de.unistuttgart.architeuthis.testenvironment;

import java.util.ArrayList;

/**
 * Beinhaltet Methoden zur Test und zur Errechnung von PrimeNumbers.
 *
 * @author  Dietmar Lippold
 */
public class PrimeNumbers {

    /**
     * Ermittelt f�r die �bergegebe Zahl, ob diese eine Primzahl ist.
     *
     * @param zahl  die zu �berpr�fende Zahl
     * @return      <CODE>true</CODE>, wenn die �bergebene Zahl eine Primzahl
     *              ist, anderenfalls <CODE>false</CODE>.
     */
    public static boolean istPrim(long zahl) {

        if (zahl < 2) {
            return false;
        } else if ((zahl == 2) || (zahl == 3)) {
            return true;
        } else if (zahl % 2 == 0) {
            return false;
        }

        for (long teiler = 3; teiler * teiler <= zahl; teiler += 2) {
            if (zahl % teiler == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ermittelt alle PrimeNumbers aus einem angegebenen Intervall. Die
     * �bergebenen Intervallgrenzen geh�ren beide zum Intervall.
     *
     * @param minWert  der untere Wert des Intervalls, aus dem die PrimeNumbers
     *                 ausgegeben werden
     * @param maxWert  der obere Wert des Intervalls, aus dem die PrimeNumbers
     *                 ausgegeben werden
     * @return         Eine Liste, deren Elemente vom Typ <CODE>Integer</CODE>
     *                 die PrimeNumbers aus dem angegebenen Intervall sind.
     */
    public static ArrayList primzahlTeilbereich(long minWert, long maxWert) {
        ArrayList teilbereich = new ArrayList();
        long startWert = minWert;

        if (startWert <= 2) {
            teilbereich.add(new Long(2));
            startWert = 3;
        }

        // Wenn der Startwert gerade ist, als Startwert n�chsten ungeraden
        // Wert nehmen
        if (startWert % 2 == 0) {
            startWert++;
        }

        for (long testZahl = startWert; testZahl <= maxWert; testZahl += 2) {
            if (istPrim(testZahl)) {
                teilbereich.add(new Long(testZahl));
            }
        }

        return teilbereich;
    }

    /**
     * Ermittelt alle PrimeNumbers aus einem angegebenen Nummern-Intervall,
     * also alle PrimeNumbers mit einer Nummer aus dem angegebenen Intervall.
     * Die �bergebenen Intervallgrenzen geh�ren beide zum Intervall. Die
     * erste Primzahl, die Zwei, hat die Nummer Eins.
     *
     * @param minNummer  der untere Wert des Nummern-Intervalls, zu dem die
     *                   PrimeNumbers ausgegeben werden. Wenn der Wert kleiner
     *                   als Eins ist, wird er als Eins interpretiert.
     * @param maxNummer  der obere Wert des Nummern-Intervalls, zu dem die
     *                   PrimeNumbers ausgegeben werden
     * @return           Eine Liste, deren Elemente vom Typ <CODE>Integer</CODE>
     *                   die PrimeNumbers mit den Nummern aus dem angegebenen
     *                   Intervall sind.
     */
    public static ArrayList primzahlTeilfolge(long minNummer, long maxNummer) {
        ArrayList teilfolge = new ArrayList();
        long testZahl; // Zahl, die als n�chstes zu testen ist
        long testNummer; // die Nummer der n�chsten Primzahl

        if (minNummer <= 1) {
            teilfolge.add(new Long(2));
        }
        testZahl = 3;
        testNummer = 2;
        while (testNummer <= maxNummer) {
            // n�chste Primzahl ermitteln
            while (!istPrim(testZahl)) {
                testZahl += 2;
            }
            // Primzahl aufnehmen, wenn sie gr��er als minNummer ist
            if (testNummer >= minNummer) {
                teilfolge.add(new Long(testZahl));
            }
            testZahl += 2;
            testNummer++;
        }
        return teilfolge;
    }
}
