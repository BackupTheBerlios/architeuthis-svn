/*
 * file:        Miscellaneous.java
 * last change: 11.02.2004 by Jürgen Heit
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


package de.unistuttgart.architeuthis.misc;

import java.io.*;
import java.net.URL;

/**
 * Hier sind alle Kleinigkeiten versammelt, die von diversen Klassen benötigt
 * werden.
 *
 * @author Ralf Kible
 *
 */
public class Miscellaneous {

    /**
     * Druckt Meldungen, wenn das erste Argument true ist. Speziell dazu
     * gedacht, um Debug-Meldungen zu drucken, in der Art:
     * printDebugMessage(debugFlag, "Fehlermeldung");
     *
     * @param message  Meldung, die ausgegeben wird.
     * @param flag  <code>true</code>, wenn die Meldung ausgegeben werden soll.
     */
    public static void printDebugMessage(boolean flag, String message) {
        if (flag) {
            System.out.println(message);
        }
    }

    /**
     * Fragt einen String vom Benutzer ab, und zwar so oft, bis dieser
     * fehlerfrei eingegeben wurde.
     *
     * @return String, den der Benutzer eingegeben hat
     */
    public static String readString() {
        boolean dirty = true;
        String read = null;
        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
        while (dirty) {
            try {
                read = b.readLine();
                dirty = false;
            } catch (IOException e) {
                System.out.println("Fehler bei Eingabe! Bitte nochmal: ");
            }
        }
        return read;
    }

    /**
     * Fragt einen URL vom Benutzer ab, und zwar so oft, bis dieser fehlerfrei
     * eingegeben wurde.
     *
     * @return URL, den der Benutzer eingegeben hat
     */
    public static URL readURL() {
        boolean dirty = true;
        URL read = null;
        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
        while (dirty) {
            try {
                read = new URL(b.readLine());
                dirty = false;
            } catch (IOException e) {
                System.out.println("Fehler bei Eingabe! Bitte nochmal: ");
            }
        }
        return read;
    }

    /**
     * Schreibt ein <code>Serializable</code> in eine Datei. Falls dabei Fehler
     * auftreten, wird nach einem neuen Dateinamen gefragt.
     *
     * @param writeObject  <code>Serializable</code>, das in Datei gespeichert wird
     * @param filename  Dateiname, in den gespeichert wird.
     */
    public static void writeSerializableToFile(Serializable writeObject,
             String filename) {
        try {
            if (filename == null) {
                System.out.println("Bitte Dateinamen fuer Loesung eingeben: ");
                filename = Miscellaneous.readString();
            }
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(writeObject);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            writeSerializableToFile(writeObject, null);
        } catch (IOException e) {
            writeSerializableToFile(writeObject, null);
        }
    }

}
