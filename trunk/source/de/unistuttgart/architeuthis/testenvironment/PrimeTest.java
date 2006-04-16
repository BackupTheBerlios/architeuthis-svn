/*
 * file:        PrimeTest.java
 * created:     08. Juni 2003
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

/**
 * Testet die Methoden der Klasse <code>PrimeNumbers</code>.
 *
 * @author  Dietmar Lippold
 */
public class PrimeTest {

    /**
     * Teste für mehrere Zahlen, ob es sich bei ihnen um eine Primzahl
     * handelt, errechnet alle Primzahlen in einem bestimmten Intervall und
     * errechnet die Primzahlen mit den Nummern aus einem bestimmten
     * Intervall.
     *
     * @param args  Die Standardargumente einer main-Methode.
     */
    public static void main(String[] args) {

        System.out.println("Ermittlung von PrimeNumbers");
        System.out.println();

        if (MyPrimeNumbers.istPrim(2)) {
            System.out.println("2 ist eine Primzahl");
        } else {
            System.out.println("2 ist keine Primzahl");
        }
        if (MyPrimeNumbers.istPrim(5)) {
            System.out.println("5 ist eine Primzahl");
        } else {
            System.out.println("5 ist keine Primzahl");
        }
        if (MyPrimeNumbers.istPrim(9)) {
            System.out.println("9 ist eine Primzahl");
        } else {
            System.out.println("9 ist keine Primzahl");
        }
        System.out.println();

        System.out.println("PrimeNumbers zwischen 1234567 und 1234765:");
        System.out.println(MyPrimeNumbers.primzahlTeilbereich(1234567, 1234765));
        System.out.println();

        System.out.println("Primzahl Nummer 1234567 bis 1234576:");
        System.out.println(MyPrimeNumbers.primzahlTeilfolge(1234567, 1234576));
    }
}

