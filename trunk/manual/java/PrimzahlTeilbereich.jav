/*
 * Auschnitt aus Klasse
 * de.unistuttgart.architeuthis.testenvironment.PrimeNumbers.
 * Version: 16.04.2006
 */

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
    public static ArrayList primzahlTeilbereich(int minWert, int maxWert) {
        ArrayList  teilbereich = new ArrayList();
        int        startWert = minWert;

        if (startWert <= 2) {
            teilbereich.add(new Integer(2));
            startWert = 3;
        }

        // Wenn der Startwert gerade ist, als Startwert nächsten ungeraden
        // Wert nehmen
        if (startWert % 2 == 0) {
            startWert++;
        }

        for (int testZahl = startWert; testZahl <= maxWert; testZahl += 2) {
            if (istPrim(testZahl)) {
                teilbereich.add(new Integer(testZahl));
            }
        }

        return teilbereich;
    }

