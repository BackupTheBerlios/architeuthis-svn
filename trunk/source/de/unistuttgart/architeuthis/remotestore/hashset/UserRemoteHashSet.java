/*
 * file:        UserRemoteHashSet.java
 * created:     08.02.2005
 * last change: 25.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.remotestore.hashset;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Collection;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore zu
 * implementieren sind, der die Funktionalität eines <CODE>HashSet</CODE> hat.
 * Es wird von einem Teilproblem verwendet.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface UserRemoteHashSet extends RemoteStore {

    /**
     * Speichert das übergebene Objekt im lokalen HashSet und sendet es an
     * andere RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE>
     * angemeldet wurde.
     *
     * @param object  Das aufzunehmende Objekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void add(Serializable object) throws RemoteException;

    /**
     * Speichert das übergebene Objekte der <CODE>Collection</CODE>, die
     * serialisierbar sein müssen, im lokalen HashSet und sendet sie an die
     * anderen RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE>
     * angemeldet wurde.
     *
     * @param collection  Die aufzunehmenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection collection) throws RemoteException;

    /**
     * Entfernt das übergebene Objekt im lokalen HashSet und sendet es an andere
     * RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE> angemeldet
     * wurde.
     *
     * @param object  Das zu entfernende Objekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void remove(Serializable object) throws RemoteException;

    /**
     * Entfernt die Objekte der <CODE>Collection</CODE>, die serialisierbar
     * sein müssen, aus dem lokalen HashSet und sendet sie an die anderen
     * RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE> angemeldet
     * wurde.
     *
     * @param collection  Die zu entfernenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void removeAll(Collection collection) throws RemoteException;

    /**
     * Ermittelt, ob das HashSet leer ist.
     *
     * @return  <code>true</code>, wenn das HashSet leer ist, sonst
     *          <code>false</code>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public boolean isEmpty() throws RemoteException;

    /**
     * Ermittelt, das übergebene Objekt im HashSet enthalten ist.
     *
     * @return  <code>true</code>, wenn das übergebene Objekt im HashSet
     *          enthalten ist, <code>false</code>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public boolean contains(Serializable object) throws RemoteException;

    /**
     * Liefert die Anzahl der in dieser Menge enthaltenen Objekte.
     *
     * @return  Die Anzahl der enthaltenen Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public int size() throws RemoteException;

    /**
     * Liefert eine Kopie des gespeicherten <CODE>HashSet</CODE>.
     *
     * @return  Den Speicherinhalt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public HashSet getHashSet() throws RemoteException;
}

