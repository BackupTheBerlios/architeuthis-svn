/*
 * file:        GeneratePrimes.java
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


package de.unistuttgart.architeuthis.testenvironment.prime.basic;

import de.unistuttgart.architeuthis.user.ProblemComputation;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Berechnet Primzahlen aus einem Zahlen- oder Nummernbereich unter Benutzung
 * der Klasse <code>ProblemComputation</code>.
 *
 * @author  Ralf Kible, Dietmar Lippold
 */
public class GeneratePrimes {

    /**
     * Der Name des Dispatchers des ComputeSystems, das zur Berechnung
     * verwendet werden soll. Dieser muss angepasst werden!
     */
    private static final String DISPATCHER = "localhost";

    /**
     * Der URL (in der Regel auf einem WWW-Server), unter dem die Klassen
     * dieses Packages bereitgestellt werden, um für das ComputeSystem
     * verfügbar zu sein. Dies muss angepasst werden!
     */
    private static final String CLASS_URL = "http://localhost:1855/";

    /**
     * Errechnet Primzahlen aus einem Zahlenbereich, der durch
     * Kommandozeilenargumente festgelegt wird, entweder lokal oder remote
     * (auf dem Compute-System).
     *
     * @param args  Ein Array, das die Untergrenze und die Obergrenze des
     *              Intervalls des Zahlenbereichs sowie den Buchstaben l oder
     *              r enthält. Der Buchstabe gibt an, ob das Problem lokal
     *              oder remote berechnet werden soll.
     */
    public static void main(String[] args) {
        ProblemComputation  problemComputation = new ProblemComputation();
        SerializableProblem problem;
        long                startTime;

        System.out.println("Berechnet eine Folge von Primzahlen\n");

        if ((args.length == 3)
            && (args[2].equals("l") || args[2].equals("r"))) {

            try {
                problem = new PrimeRangeProblemImpl(Long.parseLong(args[0]),
                                                    Long.parseLong(args[1]));
                startTime = System.currentTimeMillis();

                if (args[2].equals("l")) {
                    System.out.println("Die Primzahlen aus dem Zahlenbereich"
                                       + " von " + args[0] + " bis "
                                       + args[1] + " lokal :");
                    System.out.println(
                        problemComputation.computeProblem(problem));
                } else {
                    System.out.println("Die Primzahlen aus dem Nummernbereich"
                                       + " von " + args[0] + " bis "
                                       + args[1] + " remote :");
                    System.out.println(
                        problemComputation.transmitProblem(problem, DISPATCHER,
                                                           CLASS_URL));
                }

                System.out.println("Dauer [ms]: " +
                                   (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        } else {
            System.out.println("Fehler! Es müssen zwei Zahlen und der Buchstabe"
                               + " l oder r als Kommandozeilen-Parameter"
                               + " übergeben werden");
        }
    }
}

