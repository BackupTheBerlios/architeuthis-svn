/*
 * file:        RelayHashSetImpl.java
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

import java.util.logging.Level;
import java.util.logging.Logger;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;

import de.unistuttgart.architeuthis.remotestore.AbstractRemoteStore;

/**
 * Diese Klasse vermittelt zwischen Instanzen von Klassen, die
 * <CODE>RemoteHashSet</CODE> implementieren.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RelayHashSetImpl extends AbstractRemoteStore implements RelayHashSet {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
//    private static final long serialVersionUID = 3257847679689505078L;

    /**
     * Standard Logger Pattern.
     */
    private static final Logger LOGGER = Logger.getLogger(RelayHashSetImpl.class.getName());

    /**
     * Delegatee, also das Objekt, das die lokale Arbeit macht (an das alle
     * Aufrufe weitergeleitet werden).
     */
    private HashSet hashSet = new HashSet();

    /**
     * Konstruktor ohne spezielle Wirkung.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected RelayHashSetImpl() throws RemoteException {
        super();
    }

    /**
     * Anmeldung eines RemoteStores. Dabei wird dem anzumeldenden RemoteStore
     * der aktuelle Inhalt dieses Objekts hinzugef�gt.
     *
     * @param remoteStore  Ein neuer RemoteStore.
     *
     * @throws RemoteException  Bei einem Probleme mit einem RMI Zugriff.
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        RemoteHashSet remoteHashSet = (RemoteHashSet)remoteStore;
        if (!remoteHashSet.isEmpty()) {
            remoteHashSet.addAll(hashSet);
        }
        super.registerRemoteStore(remoteStore);
    }

    /**
     * Diese Methode wird von RemoteHashSets aufgerufen, denen ein neuen
     * Objekt zur Speicherung �bergeben wurde. Dieses Objekt wird an die
     * anderen RemoteHashSets weitergegeben.
     *
     * @param origin  Der RemoteStore, zu dem ein neues Objekt hinzugef�gt
     *                wurde.
     * @param object  Das neue Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addRemote(Object origin, Object object)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.info("called addRemote, origin:"
                        + origin
                        + " for "
                        + object);
        }

        // Erstmal den Delegatee updaten.
        hashSet.add(object);

        // Alle anderen RemoteHashSets benachrichtigen, wobei das RemoteHashSet
        // ausgelassen wird, von der der Update kommt.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            RemoteHashSet peer = (RemoteHashSet) iterator.next();

            // Nur updaten, wenn das nicht der Aufrufer ist.
            if (!peer.equals(origin)) {
                peer.add(object);
            }
        }
    }

    /**
     * Liefert eine Kopie des verwendeten <CODE>HashSet</CODE>.
     *
     * @return  Eine Kopie des <CODE>HashSet</CODE>.
     *
     * @throws RemoteException  Wenn bei der RMI Kommunikation ein
     *                          Fehler auftritt.
     */
    public synchronized HashSet getHashSet() throws RemoteException {
        return ((HashSet) hashSet.clone());
    }
}
