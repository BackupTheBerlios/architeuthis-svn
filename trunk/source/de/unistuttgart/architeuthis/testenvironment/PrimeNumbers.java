/*
 * file:        PrimeNumbers.java
 * created:     09. Juni 2003
 * last change: 16.04.2006 by Dietmar Lippold
 * developers:  Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
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
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.testenvironment;

import java.util.ArrayList;

/**
 * Beinhaltet Methoden zur Test und zur Errechnung von Primzahlen.
 *
 * @author  Dietmar Lippold
 */
public class PrimeNumbers {

    /**
     * Ermittelt für die übergegebe Zahl, ob diese eine Primzahl ist.
     *
     * @param zahl  Die zu überprüfende Zahl.
     *
     * @return  <CODE>true</CODE>, wenn die übergebene Zahl eine Primzahl ist,
     *          anderenfalls <CODE>false</CODE>.
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
     * Ermittelt alle Primzahlen aus einem angegebenen Intervall. Die
     * übergebenen Intervallgrenzen gehören beide zum Intervall.
     *
     * @param minWert  Der untere Wert des Intervalls, aus dem die Primzahlen
     *                 ausgegeben werden
     * @param maxWert  Der obere Wert des Intervalls, aus dem die Primzahlen
     *                 ausgegeben werden.
     *
     * @return  Eine Liste, deren Elemente vom Typ <CODE>Long</CODE> die
     *          Primzahlen aus dem angegebenen Intervall sind.
     */
    public static ArrayList primzahlTeilbereich(long minWert, long maxWert) {
        ArrayList teilbereich = new ArrayList();
        long      startWert   = minWert;

        if (startWert <= 2) {
            teilbereich.add(new Long(2));
            startWert = 3;
        }

        // Wenn der Startwert gerade ist, als Startwert nächsten ungeraden
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
     * Ermittelt alle Primzahlen aus einem angegebenen Nummern-Intervall, also
     * alle Primzahlen mit einer Nummer aus dem angegebenen Intervall. Die
     * übergebenen Intervallgrenzen gehören beide zum Intervall. Die erste
     * Primzahl, die Zwei, hat die Nummer Eins.
     *
     * @param minNummer  Der untere Wert des Nummern-Intervalls, zu dem die
     *                   PrimeNumbers ausgegeben werden. Wenn der Wert kleiner
     *                   als Eins ist, wird er als Eins interpretiert.
     * @param maxNummer  Der obere Wert des Nummern-Intervalls, zu dem die
     *                   PrimeNumbers ausgegeben werden.
     *
     * @return  Eine Liste, deren Elemente vom Typ <CODE>Long</CODE> die
     *          Primzahlen mit den Nummern aus dem angegebenen Intervall sind.
     */
    public static ArrayList primzahlTeilfolge(long minNummer, long maxNummer) {
        ArrayList teilfolge   = new ArrayList();
        long      testZahl;    // Zahl, die als nächstes zu testen ist
        long      testNummer;  // die Nummer der nächsten Primzahl

        if (minNummer <= 1) {
            teilfolge.add(new Long(2));
        }

        testZahl = 3;
        testNummer = 2;
        while (testNummer <= maxNummer) {

            // nächste Primzahl ermitteln
            while (!istPrim(testZahl)) {
                testZahl += 2;
            }

            // Primzahl aufnehmen, wenn sie größer als minNummer ist
            if (testNummer >= minNummer) {
                teilfolge.add(new Long(testZahl));
            }

            testZahl += 2;
            testNummer++;
        }

        return teilfolge;
    }
}

