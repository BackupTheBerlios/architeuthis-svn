/*
 * file:        Numerator.java
 * last change: 01.05.2004 by Dietmar Lippold
 * developers:  Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
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

/**
 * Implementiert einen Zähler, der eine fortlaufende Folge eindeutiger Nummern
 * liefert.
 *
 * @author Dietmar Lippold
 */
public class Numerator {

    /**
     * Die zuletzt vergebene Nummer bzw. am Anfang der Wert -1.
     */
    private long number = -1;

    /**
     * Liefert die nächste eindeutige Nummer dieses Zählers. Die erste
     * gelieferte Nummer ist der Wert 0.
     *
     * @return  Die nächste eindeutige Nummer dieses Zählers.
     */
    public synchronized long nextNumber() {
        number++;
        return number;
    }
}
