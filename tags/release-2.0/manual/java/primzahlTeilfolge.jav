/*
 * Auschnitt aus Klasse
 * de.unistuttgart.architeuthis.testenvironment.PrimeNumbers.
 * Version: 20.04.2006
 */

    /**
     * Ermittelt alle Primzahlen aus einem angegebenen Nummern-Intervall, also
     * alle Primzahlen mit einer Nummer aus dem angegebenen Intervall. Die
     * übergebenen Intervallgrenzen gehören beide zum Intervall. Die erste
     * Primzahl, die Zwei, hat die Nummer Eins.
     *
     * @param minNummer  Der untere Wert des Nummern-Intervalls, zu dem die
     *                   Primzahlen geliefert werden. Wenn der Wert kleiner
     *                   als Eins ist, wird er als Eins interpretiert.
     * @param maxNummer  Der obere Wert des Nummern-Intervalls, zu dem die
     *                   Primzahlen geliefert werden.
     *
     * @return  Eine aufsteigend geordnete Liste, deren Elemente vom Typ
     *          <CODE>Long</CODE> die Primzahlen mit den Nummern aus dem
     *          angegebenen Intervall sind.
     */
    public static ArrayList primzahlTeilfolge(int minNummer, int maxNummer) {
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

            // Primzahl aufnehmen, wenn sie größer als minNummer ist.
            if (testNummer >= minNummer) {
                teilfolge.add(new Long(testZahl));
            }

            testZahl += 2;
            testNummer++;
        }

        return teilfolge;
    }

