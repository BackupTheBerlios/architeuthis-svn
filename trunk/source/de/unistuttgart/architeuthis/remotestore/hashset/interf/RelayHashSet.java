/*
 * file:        RelayHashSet.java
 * created:     08.02.2005
 * last change: 17.04.2006 by Dietmar Lippold
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

import java.io.Serializable;
import java.util.Collection;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore mit
 * Relay-Funktion bei Verwaltung eines <CODE>HashSet</CODE> zu implementieren
 * sind.
 *
 * @see  java.util.HashSet
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public interface RelayHashSet extends RemoteStore {

    /**
     * Speichert das übergebene Objekt und gibt es an alle RemoteStores, bis
     * auf den, der das Objekt übergeben hat, weiter.
     *
     * @param object       Das zu speichernde Objekt.
     * @param remoteStore  Der RemoteStore, von dem das übergebene Objekt
     *                     kommt und an den das Opjekt nicht weitergeleitet
     *                     werden soll. Wenn der Wert <code>null</code> ist,
     *                     wird der Wert auch an den aufrufenden Operative
     *                     weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void add(Serializable object,
                    LocalHashSet remoteStore) throws RemoteException;

    /**
     * Speichert die Objekte der übergebenen <CODE>Collection</CODE>, die
     * serialisierbar sein müssen, und gibt die <CODE>Collection</CODE> an
     * alle RemoteStores, bis auf den, der die <CODE>Collection</CODE>
     * übergeben hat, weiter.
     *
     * @param collection   Die Collection der zu speichernden Objekte.
     * @param remoteStore  Der RemoteStore, von dem die übergebene Collection
     *                     kommt und an den sie nicht weitergeleitet werden
     *                     soll. Wenn der Wert <code>null</code> ist, wird die
     *                     Collection auch an den aufrufenden Operative
     *                     weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection collection,
                       LocalHashSet remoteStore) throws RemoteException;
}

