/*
 * file:        LocalHashMap.java
 * created:     10.04.2005
 * last change: 12.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.remotestore.hashmap.interf;

import java.util.Map;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore zu
 * implementieren sind, der die Funktionalität einer <CODE>HashMap</CODE> hat
 * und Daten nur lokal speichert, ohne sie an andere Remote-Stores
 * weiterzugeben.
 *
 * @author Dietmar Lippold
 */
public interface LocalHashMap extends RemoteStore {

    /**
     * Speichert zu einen key-Objekt ein value-Objekt nur lokal, ohne das
     * Objekt-Paar an andere Remote-Stores weiterzugeben.
     *
     * @param key    Das key-Objekt, unter dem das value-Objekt gespeichert
     *               wird.
     * @param value  Das value-Objekt, das zum key-Objekt gespeichert wird.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void putLocal(Object key, Object value) throws RemoteException;

    /**
     * Speichert die Einträge der übergebenen Map lokal, ohne die Objekt-Paare
     * an die RelayMap weiterzugeben.
     *
     * @param map  Die Map, deren Einträge gespeichert werden.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void putAllLocal(Map map) throws RemoteException;
}

