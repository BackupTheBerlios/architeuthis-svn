/*
 * file:        PrimeTest.java
 * created:     08. Juni 2003
 * last change: 11.02.2004 by Jürgen Heit
 * developers:  Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
 * 
 * 
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */

package de.unistuttgart.architeuthis.testenvironment;


/**
 * Testet die Methoden der Klasse PrimeNumbers.
 *
 * @author  Dietmar Lippold
 */
public class MyPrimeTest {

    /**
     * Teste für mehrere Zahlen, ob es sich bei ihnen um eine Primzahl handelt,
     * errechnet alle PrimeNumbers in einem bestimmten Intervall und errechnet
     * die PrimeNumbers mit den Nummern aus einem bestimmten Intervall.
     *
     * @param args  die Standardargumente einer main-Methode
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

