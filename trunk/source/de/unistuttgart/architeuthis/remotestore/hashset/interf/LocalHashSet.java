/*
 * file:        LocalHashSet.java
 * created:     10.02.2005
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


package de.unistuttgart.architeuthis.remotestore.hashset.interf;

import java.util.Collection;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore zu
 * implementieren sind, der die Funktionalität eines <CODE>HashSet</CODE> hat.
 * Er wird von einem Teilproblem verwendet.
 *
 * ToDo: Methode addAllLocal ergänzen.
 *
 * @author Dietmar Lippold
 */
public interface LocalHashSet extends RemoteStore {

    /**
     * Speichert ein Objekt nur im lokalen HashSet, ohne es an das
     * <CODE>RelayHashSet</CODE> weiterzugeben.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addLocal(Object object) throws RemoteException;

    /**
     * Speichert die Objekte der übergebenen <CODE>Collection</CODE> nur im
     * lokalen HashSet, ohne sie an das <CODE>RelayHashSet</CODE>
     * weiterzugeben.
     *
     * @param collection  Die Collection der zu speichernden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAllLocal(Collection collection) throws RemoteException;
}

