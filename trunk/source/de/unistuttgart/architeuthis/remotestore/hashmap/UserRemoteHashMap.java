/*
 * file:        UserRemoteHashMap.java
 * created:     08.02.2005
 * last change: 18.04.2005 by Dietmar Lippold
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

import java.io.Serializable;
import java.util.Map;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die f�r einen RemoteStore zu
 * implementieren sind, der die Funktionalit�t einer <CODE>HashMap</CODE> hat.
 * Es wird von einem Teilproblem verwendet.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface UserRemoteHashMap extends RemoteStore {

    /**
     * Speichert zu einen key-Objekt ein value-Objekt. Das Objekt-Paar wird
     * zur Speicherung an andere RemoteStores weitergegeben, wenn eine
     * <CODE>RelayMap</CODE> angemeldet wurde.
     *
     * @param key    Das key-Objekt, unter dem das value-Objekt gespeichert
     *               wird.
     * @param value  Das value-Objekt, das zum key-Objekt gespeichert wird.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void put(Serializable key, Serializable value) throws RemoteException;

    /**
     * Speichert die Eintr�ge der �bergebenen Map, die serialisierbar sein
     * m�ssen. Die Map wird zur Speicherung an andere RemoteStores
     * weitergegeben, wenn eine <CODE>RelayMap</CODE> angemeldet wurde.
     *
     * @param map  Die Map, deren Eintr�ge gespeichert werden.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void putAll(Map map) throws RemoteException;

    /**
     * Liefert zu einem key-Objekt das lokal gespeicherte value-Objekt.
     *
     * @param key  Das Key-Objekt, zu dem das zugeh�rige value-Objekt
     *             geliefert wird.
     *
     * @throws RemoteException  Bei einm RMI Problem.
     *
     * @return  Das zugeh�rige Objekt.
     */
    public Serializable get(Serializable key) throws RemoteException;

    /**
     * Liefert die Anzahl der in dieser loaklen Map enthaltenen Objekt-Paare.
     *
     * @return  Die Anzahl der enthaltenen Objekt-Paare.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public int size() throws RemoteException;
}

