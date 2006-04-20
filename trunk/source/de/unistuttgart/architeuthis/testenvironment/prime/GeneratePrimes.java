/*
 * file:        GeneratePrimes.java
 * created:     <???>
 * last change: 20.04.2006 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
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


package de.unistuttgart.architeuthis.testenvironment.prime;

/**
 * Testet die Methoden der Klasse PrimeNumbersParallel.
 *
 * @author  Ralf Kible
 */
public class GeneratePrimes {

    /**
     * Errechnet die Primzahlteilfolge für zwei als Kommandozeilenargumente
     * übergebene Zahlen.
     *
     * @param args  Ein Array, das die Untergrenze und die Obergrenze des
     *              Intervalls enthält, aus dem die Nummern der zu
     *              ermittelnden Primzahlen stammen.
     */
    public static void main(String[] args) {
        long startTime;

        System.out.println("Berechnet eine Folge von PrimeNumbers\n");

        if (args.length == 2) {
            System.out.println("Folge von " + args[0] + " bis " + args[1]);
            try {
                System.out.println("Beginn...");
                startTime = System.currentTimeMillis();
                System.out.println(
                    PrimeNumbersParallel.primzahlTeilfolge(
                        (new Long(args[0])).intValue(),
                        (new Long(args[1])).intValue()));
                System.out.println("Fertig!");
                System.out.println("Dauer: " +
                                   (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        } else {
            System.out.println("Fehler! Es müssen zwei Zahlen als "
                               + "Kommandozeilen-Parameter übergeben werden");
        }
    }
}

