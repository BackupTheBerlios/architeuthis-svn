/*
 * file:        GeneratePrimes.java
 * created:     <???>
 * last change: 28.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment;

/**
 * Berechnet Primzahlen aus einem Zahlen- oder Nummernbereich unter Benutzung
 * der Klasse <code>PrimeNumbersParallel</code>.
 *
 * @author  Ralf Kible, Dietmar Lippold
 */
public class GeneratePrimes {

    /**
     * Errechnet Primzahlen aus einem Zahlen- oder Nummernbereich, der durch
     * Kommandozeilenargumente festgelegt wird.
     *
     * @param args  Ein Array, das die Untergrenze und die Obergrenze des
     *              Intervalls eines Zahlen- oder Nummernbereichs sowie den
     *              Buchstaben z oder n enthält. Der Buchstabe gibt die Art
     *              des Bereichs an.
     */
    public static void main(String[] args) {
        long startTime;

        System.out.println("Berechnet eine Folge von Primzahlen\n");

        if ((args.length == 3)
            && (args[2].equals("z") || args[2].equals("n"))) {

            try {
                startTime = System.currentTimeMillis();

                if (args[2].equals("z")) {
                    System.out.println("Die Primzahlen aus dem Zahlenbereich"
                                       + " von " + args[0] + " bis "
                                       + args[1] + " :");
                    System.out.println(
                        PrimeNumbers.primzahlTeilbereich(
                            Long.parseLong(args[0]),
                            Long.parseLong(args[1])));
                } else {
                    System.out.println("Die Primzahlen aus dem Nummernbereich"
                                       + " von " + args[0] + " bis "
                                       + args[1] + " :");
                    System.out.println(
                        PrimeNumbers.primzahlTeilfolge(
                            Long.parseLong(args[0]),
                            Long.parseLong(args[1])));
                }

                System.out.println("Dauer [ms]: " +
                                   (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                System.err.println(e);
            }
            System.out.println();
        } else {
            System.out.println("Fehler! Es müssen zwei Zahlen und der Buchstabe"
                               + " z oder n als Kommandozeilen-Parameter"
                               + " übergeben werden");
        }
    }
}

