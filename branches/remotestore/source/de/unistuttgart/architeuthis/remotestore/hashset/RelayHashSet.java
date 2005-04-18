/*
 * file:        RelayHashSet.java
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


package de.unistuttgart.architeuthis.remotestore.hashset;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die f�r einen RemoteStore mit
 * Relay-Funktion bei Verwaltung eines <CODE>HashSet</CODE> zu implementieren
 * sind.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface RelayHashSet extends RemoteStore {

    /**
     * Speichert das �bergebene Objekt und gibt es an alle RemoteStores
     * weiter.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void add(Serializable object) throws RemoteException;

    /**
     * Speichert die Objekte der �bergebenen <CODE>Collection</CODE>, die
     * serialisierbar sein m�ssen, und gibt die <CODE>Collection</CODE> an
     * alle RemoteStores weiter.
     *
     * @param collection  Die Collection der zu speichernden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection collection) throws RemoteException;

    /**
     * Liefert eine Kopie des gespeicherten <CODE>HashSet</CODE>.
     *
     * @return  Den Speicherinhalt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public HashSet getHashSet() throws RemoteException;
}

