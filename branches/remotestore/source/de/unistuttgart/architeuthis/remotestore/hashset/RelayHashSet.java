/*
 * file:        RelayHashSet.java
 * created:     08.02.2005
 * last change: 01.04.2005 by Dietmar Lippold
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

import java.rmi.RemoteException;
import java.util.HashSet;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore mit
 * Relay-Funktion bei Verwaltung eines <CODE>HashSet</CODE> zu implementieren
 * sind.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface RelayHashSet extends RemoteStore {

    /**
     * Speichert das übergebene Objekt und sendet es an alle anderen
     * RemoteStores außer an den, von dem der Aufruf kommt.
     *
     * @param origin  RemoteStore, von dem der Aufruf kommt.
     * @param object  Neues Objekt für den Speicher.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addRemote(RemoteStore origin, Object object) throws RemoteException;

    /**
     * Liefert eine Kopie des gspeicherten <CODE>HashSet</CODE>.
     *
     * @return  Den Speicherinhalt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public HashSet getHashSet() throws RemoteException;
}

