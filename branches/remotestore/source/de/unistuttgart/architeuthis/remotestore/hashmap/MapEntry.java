/*
 * file:        MapEntry.java
 * created:     05.04.2005
 * last change: 05.04.2005 by Dietmar Lippold
 * developers:  Michael Wohlfart, michael.wohlfart@zsw-bw.de
 *              Dietmar Lippold,  dietmar.lippold@informatik.uni-stuttgart.de
 *
 * This software was developed at the Institute for Intelligent Systems at the
 * University of Stuttgart (http://www.iis.uni-stuttgart.de/) under leadership
 * of Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de).
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


package de.unistuttgart.architeuthis.remotestore.hashmap;

/**
 * Implementiert ein Objekt-Paar aus einem key-Objekt und einem value-Objekt.
 *
 * @author Dietmar Lippold
 */
public class MapEntry {

    /**
     * Das enthaltene key-Objekt.
     */
    private Object keyObject;

    /**
     * Das enthaltene value-Objekt.
     */
    private Object valueObject;

    /**
     * Erzeugt eine neue Instanz aus einem key-Objekt und einem value-Objekt.
     *
     * @param objectpair  Das key-Objekt.
     * @param relayStore  Das value-Objekt.
     */
    public MapEntry(Object keyObject, Object valueObject) {
        this.keyObject = keyObject;
        this.valueObject = valueObject;
    }

    /**
     * Liefert das enthaltene key-Objekt.
     *
     * @return  Das enthaltene key-Objekt.
     */
    public Object getKey() {
        return keyObject;
    }

    /**
     * Liefert das enthaltene value-Objekt.
     *
     * @return  Das enthaltene value-Objekt.
     */
    public Object getValue() {
        return valueObject;
    }
}

