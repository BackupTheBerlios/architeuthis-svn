/*
 * file:        PrimeNumbersParallel.java
 * created:     <???>
 * last change: 26.05.2004 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.prime;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.systeminterfaces.UserProblemTransmitter;
import de.unistuttgart.architeuthis.user.ProblemTransmitterImpl;

/**
 * Beinhaltet Methoden zur verteilten Errechnung von PrimeNumbers.
 * Nach außen soll sich die Klasse genau so verhalten wie
 * {@link de.unistuttgart.architeuthis.testenvironment.PrimeNumbers}.
 *
 * @author Ralf Kible
 */
public class PrimeNumbersParallel {

    /**
     * Der Name des Dispatchers des ComputeSystems, das zur Berechnung
     * verwendet werden soll. Dies muss angepasst werden!
     */
    private static final String DISPATCHER = "ischia";

    /**
     * Der URL (in der Regel auf einem WWW-Server), unter dem die Klassen
     * dieses Packages bereitgestellt werden, um für das ComputeSystem
     * verfügbar zu sein. Dies muss angepasst werden!
     */
    private static final String CLASS_URL = "http://www/ifi/is/RMI-Test/";

    /**
     * Die Klasse, die die verteilte Berechnung einer Teilfolge übernimmt.
     * Falls dieses Package verwendet wird, muss dies nicht geändert werden.
     */
    private static final String TEILFOLGEN_KLASSE =
        "de.unistuttgart.architeuthis.testenvironment.prime.PrimeSequenceProblemImpl";

    /**
     * Die Klasse, die die verteilte Berechnung eines Teilbereichs übernimmt.
     * Falls dieses Package verwendet wird, muss dies nicht geändert werden.
     */
    private static final String TEILBEREICHS_KLASSE =
        "de.unistuttgart.architeuthis.testenvironment.prime.PrimeRangeProblemImpl";

    /**
     * Ermittelt alle PrimeNumbers aus einem angegebenen Intervall. Die
     * übergebenen Intervallgrenzen gehören beide zum Intervall. Die Berechnung
     * wird verteilt auf dem unter COMPUTE_SYSTEM angegebenen ComputeSystem
     * ausgeführt. Die Klassen dieses Packages müssen unter CLASS_URL
     * bereitgestellt werden.
     *
     * @param minWert  der untere Wert des Intervalls, aus dem die PrimeNumbers
     *                 ausgegeben werden
     * @param maxWert  der obere Wert des Intervalls, aus dem die PrimeNumbers
     *                 ausgegeben werden
     * @return         Eine Liste, deren Elemente vom Typ <CODE>Integer</CODE>
     *                 die PrimeNumbers aus dem angegebenen Intervall sind.
     *
     * @throws MalformedURLException    Falls der DISPATCHER ungültig ist.
     * @throws RemoteException          Falls Fehler bei der Verbindung mit dem
     *                                  DISPATCHER auftreten.
     * @throws ClassNotFoundException   Falls die unter TEILBEREICHS_KLASSE
     *                                  angegebene Klasse nicht gefunden wurde
     * @throws ProblemComputeException  Falls ein Fehler bei der Berechnung auf
     *                                  dem ComputeSystem auftritt.
     * @throws NotBoundException        Falls das ComputeSystem nicht unter der
     *                                  Adresse DISPATCHER gefungen wurde.
     */
    public static ArrayList primzahlTeilbereich(long minWert, long maxWert)
        throws
            MalformedURLException,
            RemoteException,
            ClassNotFoundException,
            ProblemComputeException,
            NotBoundException {

        UserProblemTransmitter pti;
        Object[] primParameters = new Object[2];
        ArrayList solution = null;

        pti = new ProblemTransmitterImpl(DISPATCHER, false);
        primParameters[0] = new Long(minWert);
        primParameters[1] = new Long(maxWert);
        solution = (ArrayList) pti.transmitProblem(new URL(CLASS_URL),
                                                   TEILBEREICHS_KLASSE,
                                                   primParameters);
        return solution;
    }

    /**
     * Ermittelt alle PrimeNumbers aus einem angegebenen Nummern-Intervall,
     * also alle PrimeNumbers mit einer Nummer aus dem angegebenen Intervall.
     * Die übergebenen Intervallgrenzen gehören beide zum Intervall. Die
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
     *
     * @throws MalformedURLException    Falls der DISPATCHER ungültig ist.
     * @throws RemoteException          Falls Fehler bei der Verbindung mit dem
     *                                  DISPATCHER auftreten.
     * @throws ClassNotFoundException   Falls die unter TEILFOLGEN_KLASSE
     *                                  angegebene Klasse nicht gefunden wurde
     * @throws ProblemComputeException  Falls ein Fehler bei der Berechnung auf
     *                                  dem ComputeSystem auftritt.
     * @throws NotBoundException        Falls das ComputeSystem nicht unter der
     *                                  Adresse DISPATCHER gefungen wurde.
     */
    public static ArrayList primzahlTeilfolge(long minNummer, long maxNummer)
        throws
            MalformedURLException,
            RemoteException,
            ClassNotFoundException,
            ProblemComputeException,
            NotBoundException {

        UserProblemTransmitter pti;
        Object[] primParameters = new Object[2];
        ArrayList solution = null;

        pti = new ProblemTransmitterImpl(DISPATCHER, false);
        primParameters[0] = new Long(minNummer);
        primParameters[1] = new Long(maxNummer);
        solution = (ArrayList) pti.transmitProblem(new URL(CLASS_URL),
                                                   TEILFOLGEN_KLASSE,
                                                   primParameters);
        return solution;
    }
}
