/* Auschnitt aus Primzahlen.java
 * Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */

    /**
     * Ermittelt alle Primzahlen aus einem angegebenen Nummern-Intervall,
     * also alle Primzahlen mit einer Nummer aus dem angegebenen Intervall.
     * Die übergebenen Intervallgrenzen gehören beide zum Intervall. Die
     * erste Primzahl, die Zwei, hat die Nummer Eins.
     *
     * @param minNummer  der untere Wert des Nummern-Intervalls, zu dem die
     *                   Primzahlen ausgegeben werden. Wenn der Wert kleiner
     *                   als Eins ist, wird er als Eins interpretiert.
     * @param maxNummer  der obere Wert des Nummern-Intervalls, zu dem die
     *                   Primzahlen ausgegeben werden
     * @return           Eine Liste, deren Elemente vom Typ <CODE>Integer</CODE>
     *                   die Primzahlen mit den Nummern aus dem angegebenen
     *                   Intervall sind.
     */
    public static ArrayList primzahlTeilfolge(int minNummer, int maxNummer) {
        ArrayList teilfolge = new ArrayList();
        int       testZahl;    // Zahl, die als nächstes zu testen ist
        int       testNummer;  // die Nummer der nächsten Primzahl

        if (minNummer <= 1) {
            teilfolge.add(new Integer(2));
        }
        testZahl = 3;
        testNummer = 2;
        while (testNummer <= maxNummer) {
            // nächste Primzahl ermitteln
            while (! istPrim(testZahl)) {
                testZahl += 2;
            }
            // Primzahl aufnehmen, wenn sie größer als minNummer ist
            if (testNummer >= minNummer) {
                teilfolge.add(new Integer(testZahl));
            }
            testZahl += 2;
            testNummer++;
        }
        return teilfolge;
    }

