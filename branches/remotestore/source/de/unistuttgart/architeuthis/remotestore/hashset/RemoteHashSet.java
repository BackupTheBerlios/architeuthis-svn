/*
 * file:        RemoteHashSet.java
 * created:     08.02.2005
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


package de.unistuttgart.architeuthis.remotestore.hashset;

import java.util.Collection;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore zu
 * implementieren sind, der die Funktionalität eines <CODE>HashSet</CODE> hat.
 * Er wird von einem Teilproblem verwendet.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface RemoteHashSet extends RemoteStore {

    /**
     * Nimmt ein Objekt in den lokalen Speicher auf und gibt dieses an andere
     + RemoteStores weiter, wenn welche vorhanden sind.
     *
     * @param object  Das aufzunehmende Objekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void add(Object object) throws RemoteException;

    /**
     * Nimmt die Objekte in den lokalen Speicher auf und gibt sie an andere
     + RemoteStores weiter, wenn welche vorhanden sind.
     *
     * @param objects  Die aufzunehmenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection objects) throws RemoteException;

    /**
     * Liefert die Anzahl der in dieser Menge enthaltenen Objekte.
     *
     * @return  Die Anzahl der enthaltenen Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public int size() throws RemoteException;
}

