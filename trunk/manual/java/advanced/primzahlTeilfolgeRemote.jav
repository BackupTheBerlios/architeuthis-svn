/*
 * Auschnitt aus Klasse
 * de.unistuttgart.architeuthis.testenvironment.prime.advanced.PrimeNumbersParallel
 * Version: 24.04.2006
 */

    /**
     * Ermittelt alle Primzahlen aus einem angegebenen Nummern-Intervall, also
     * alle Primzahlen mit einer Nummer aus dem angegebenen Intervall. Die
     * übergebenen Intervallgrenzen gehören beide zum Intervall. Die erste
     * Primzahl, die Zwei, hat die Nummer Eins.
     *
     * @param minNummer  Der untere Wert des Nummern-Intervalls, zu dem die
     *                   Primzahlen ausgegeben werden. Wenn der Wert kleiner
     *                   als Eins ist, wird er als Eins interpretiert.
     * @param maxNummer  Der obere Wert des Nummern-Intervalls, zu dem die
     *                   Primzahlen ausgegeben werden.
     *
     * @return  Eine Liste, deren Elemente vom Typ <CODE>Long</CODE> die
     *          Primzahlen mit den Nummern aus dem angegebenen Intervall sind.
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

        pti = new ProblemTransmitterImpl(DISPATCHER);
        primParameters[0] = new Long(minNummer);
        primParameters[1] = new Long(maxNummer);
        solution = (ArrayList) pti.transmitProblem(new URL(CLASS_URL),
                                                   TEILFOLGEN_KLASSE,
                                                   primParameters);
        return solution;
    }

