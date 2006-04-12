/*
 * file:        UserRemoteHashSetNew.java
 * created:     12.04.2006
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


package de.unistuttgart.architeuthis.remotestore.hashsetnew;

import java.util.HashSet;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.hashset.UserRemoteHashSet;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore zu
 * implementieren sind, der die Funktionalität eines <CODE>HashSet</CODE> hat,
 * wobei zusätzlich die Menge der vom RealyStore neu hinzugefügten Objekte
 * verwaltet wird. Es wird von einem Teilproblem verwendet.
 *
 * @author Dietmar Lippold
 */
public interface UserRemoteHashSetNew extends UserRemoteHashSet, RemoteStore {

    /**
     * Liefert die Anzahl der vom RelayStore seit dem letzten Aufruf von
     * <CODE>newElements</CODE> neu hinzugefügten Objekte.
     *
     * @return  Die Anzahl der enthaltenen Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public int newElementNumber() throws RemoteException;

    /**
     * Liefert eine Menge der vom RelayStore seit dem letzten Aufruf dieser
     * Methode neu hinzugefügten Objekte.
     *
     * @return  Eine Menge der vom RelayStore seit dem letzten Aufruf dieser
     *          Methode neu hinzugefügten Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public HashSet newElements() throws RemoteException;
}

