/*
 * file:        CommandLineParser.java
 * last change: 11.02.2004 by J�rgen Heit
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


// TODO: evtl. sollte die Funktionalit�t zum Handling von Properties files nicht
//       im parser enthalten sein. Alternativ k�nnte der Parser ein properties 
//       file zur�ckliefern, in dem alle optionen eingetragen sind ?!

package de.unistuttgart.architeuthis.misc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * Hilft dabei, eine gr��ere Anzahl von Kommandozeilenparametern sinnvoll
 * zu verarbeiten.
 *
 * @author Ralf Kible
 *
 */
public class CommandLineParser {

    /**
     * HashMap, die alle momentan verwalteten Parameter enth�lt
     */
    private HashMap allParameters = new HashMap();

    /**
     * HahsMap, die Parameter enth�lt, die bisher noch nicht verwendet wurden
     */
    private HashMap unusedParameters = new HashMap();

    /**
     * Soll nicht verwendet werden.
     */
    private CommandLineParser() {
        super();
    }

    /**
     * Zu verwendender Konstruktor, der die Kommandozeilenargumente als
     * Array of String erh�lt. Der Gedanke ist, direkt die Argumente der
     * main-Methode hier zu �bergeben:
     * <pre>
     * public static void main(String[] args) {
     *     CommandlineParser parameter = new CommandLineParser(args);
     *     ...
     * </pre>
     *
     * @param args  Kommandozeilenparameter
     */
    public CommandLineParser(String[] args) {
        parseCommandlineParameters(args);
    }

    /**
     * Liest Kommandozeilenargumente der Form -(Buchstabe) [(Wert)] in
     * eine <code>HashMap</code> ein. Falls kein Wert angegeben wurde,
     * wird <code>null</code> als Wert eingetragen. Die Werte sollten
     * dann mit <code>getParameter()</code> oder <code>getSwitch()</code>
     * weiterverarbeitet werden.
     * Beispiel:
     * Test -t TEST -b -T test
     * Also steht in der HashMap:
     * -t -> TEST
     * -b -> <code>null</code>
     * -T -> test
     *
     * @param args  Array of Strings der Form: -(Buchstabe) (Wert)
     *              -(Buchstabe) (Wert) ...
     */
    private void parseCommandlineParameters(String[] args) {
        unusedParameters = new HashMap();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (i < args.length - 1) {
                    if (args[i + 1].startsWith("-")) {
                        unusedParameters.put(args[i], null);
                    } else {
                        unusedParameters.put(args[i], args[i + 1]);
                        i++;
                    }
                } else {
                    unusedParameters.put(args[i], null);
                }
            }
        }
        allParameters.putAll(unusedParameters);
    }

    /**
     * Gibt zur�ck, ob ein Parameter der Form -(Buchstabe) �bergeben wurde, und
     * markiert den Parameter als behandelt.
     *
     * @param key  zu suchender Parameter
     * @return  true, falls vorhanden, sonst false
     * @throws IOException  wird geworfen, falls dem Parameter ein Wert
     *                      �bergeben wurde (also: -t TEST anstatt -t)
     */
    public boolean getSwitch(String key)
        throws IOException {
        if (allParameters.containsKey(key)) {
            if (allParameters.get(key) == null) {
                unusedParameters.remove(key);
                return true;
            } else {
                throw new IOException(key);
            }
        }
        return false;
    }

    /**
     * Gibt den Wert zur�ck, der einem Parameter �bergeben wurde und markiert
     * den Parameter als behandelt.
     *
     * @param key  der zu suchende Parameter
     * @return  Wert des Parameters, niemals <code>null</code>
     * @throws IOException  Falls Parameter nicht vorhanden, oder
     *                      <code>null</code> als Wert hat
     */
    public String getParameter(String key)
        throws IOException {
        if (allParameters.containsKey(key)) {
            if (allParameters.get(key) != null) {
                unusedParameters.remove(key);
                return (String) allParameters.get(key);
            } else {
                throw new IOException(key);
            }
        }
        throw new IOException(key);
    }


    /**
     * Liefert zur�ck, falls noch nicht behandelte Parameter vorliegen.
     *
     * @return  <code>true</code>, falls unbehandelte Parameter vorliegen, sonst
     *          <code>false</code>
     */
    public boolean hasUnusedParameters() {
        return !unusedParameters.isEmpty();
    }

    /**
     * Gibt eine Stringdarstellung der unbehandelten Parameter zur�ck. Die Form
     * ist: {-(Parameter1)=(Wert1), -(Parameter2)=(Wert2) ...}
     *
     * @return  Stringdarstellung aller unbehandelten Parameter
     */
    public String getUnusedParameters() {
        return unusedParameters.toString();
    }

    /**
     * Erstellt <code>Properties</code> aus den Kommandozeilenparametern.
     * Dabei werden nur Parameter mit Schl�ssel und Wert aufgenommen. Der
     * Schl�sselindikator "-" wird dabei entfernt.
     *
     * @param defaults  <code>Properties</code>, die als Standardwerte
     *                  �bernommen werden.
     * @return aus <code>defaults</code> und den Programmarguementen
     *         zusammengesetzte <code>Properties</code>
     * @see java.util.Properties
     */
    public Properties properties(Properties defaults) {
        Properties returnValue = new Properties(defaults);

        Iterator paramIter = allParameters.entrySet().iterator();

        while (paramIter.hasNext()) {
            Entry entry = (Entry) paramIter.next();
            if (entry.getValue() != null) {
                returnValue.put(((String)entry.getKey()).substring(1), entry.getValue());
            }
        }

        return returnValue;
    }

}
