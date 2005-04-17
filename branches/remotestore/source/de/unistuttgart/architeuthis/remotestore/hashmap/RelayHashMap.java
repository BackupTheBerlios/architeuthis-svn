/*
 * file:        RelayHashMap.java
 * created:     08.02.2005
 * last change: 17.04.2005 by Dietmar Lippold
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

import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die f�r einen RemoteStore mit
 * Relay-Funktion bei Verwaltung einer <CODE>HashMap</CODE> zu implementieren
 * sind.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface RelayHashMap extends RemoteStore {

    /**
     * Speichert zu einen key-Objekt ein value-Objekt. Das Objekt-Paar wird
     * zur Speicherung an alle RemoteStores weitergegeben.
     *
     * @param key    Das key-Objekt, unter dem das value-Objekt gespeichert
     *               wird.
     * @param value  Das value-Objekt, das zum key-Objekt gespeichert wird.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void put(Object key, Object value) throws RemoteException;

    /**
     * Speichert die Eintr�ge der �bergebenen Map. Die Map wird zur
     * Speicherung an alle RemoteStores weitergegeben.
     *
     * @param map  Die Map, deren Eintr�ge gespeichert werden.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void putAll(Map map) throws RemoteException;

    /**
     * Liefert eine Kopie der gespeicherten <CODE>HashMap</CODE>.
     *
     * @return  Eine Kopie der gespeicherten <CODE>HashMap</CODE>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public HashMap getHashMap() throws RemoteException;
}

