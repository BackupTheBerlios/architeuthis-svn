/*
 * file:            MyInteger.java
 * created:         12. November 2003
 * Letzte Änderung: 11.02.2004 by Jürgen Heit
 * developers:      Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
 * 
 * 
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */
                                                                                
package de.unistuttgart.architeuthis.testenvironment;

import java.io.Serializable;
                                                                                
                                                                                
/**
 * Realisiert eine vereinfachte Version der Klasse <CODE>Integer</CODE>.
 *
 * @author  Dietmar Lippold
 */
public class MyInteger implements Serializable {

    /**
     * Der gspeicherte Wert des Objekts.
     */
    private long wert;

    /**
     * Erzeugt eine neue Instanz.
     *
     * @param wert  Der Wert der neuen Instanz.
     */
    public MyInteger(long wert) {
        this.wert = wert;
    }

    /**
     * Liefert eine String, der den Wert des Objekts beschreibt.
     */
    public String toString() {
        return Long.toString(wert);
    }
}

