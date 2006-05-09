/*
 * file:        RelayHashSetImpl.java
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


package de.unistuttgart.architeuthis.remotestore.hashset.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.AbstractRelayStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.hashset.interf.LocalHashSet;
import de.unistuttgart.architeuthis.remotestore.hashset.interf.RelayHashSet;

/**
 * Diese Klasse vermittelt zwischen Instanzen von Klassen, die
 * <CODE>RemoteHashSet</CODE> implementieren.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RelayHashSetImpl extends AbstractRelayStore implements RelayHashSet {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = -5621617412932718922L;

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
    public RelayHashSetImpl() throws RemoteException {
        super();
    }

    /**
     * Anmeldung eines <CODE>RemoteHashSet</CODE>. Dabei wird diesem der
     * aktuelle Inhalt dieses Objekts hinzugefügt.
     *
     * @param remoteStore  Ein neues <CODE>RemoteHashSet</CODE>
     *
     * @throws RemoteException  Bei einem Probleme mit einem RMI Zugriff.
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        LocalHashSet remoteHashSet = (LocalHashSet) remoteStore;

        // Die aktuellen Elemente dem zu registrierenden RemoteStore
        // hinzufügen.
        remoteHashSet.addAllLocal(hashSet);

        // Den zu registrierenden RemoteStore speichern.
        super.registerRemoteStore(remoteStore);
    }

    /**
     * Speichert das übergebene Objekt und gibt es an alle RemoteStores, bis
     * auf den, der das Objekt übergeben hat, zur Speicherung weiter.
     *
     * @param object       Das zu speichernde Objekt.
     * @param remoteStore  Der RemoteStore, von dem das übergebene Objekt
     *                     kommt und an den das Opjekt nicht weitergeleitet
     *                     werden soll. Wenn der Wert <code>null</code> ist,
     *                     wird der Wert auch an den aufrufenden Operative
     *                     weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void add(Serializable object, LocalHashSet remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called add for : " + object);
        }

        // Erstmal den Delegatee updaten.
        hashSet.add(object);

        // Das Objekt an alle RemoteHashSets übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalHashSet peer = (LocalHashSet) iterator.next();
            if (!peer.equals(remoteStore)) {
                peer.addLocal(object);
            }
        }
    }

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
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addAll(Collection collection,
                                    LocalHashSet remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called addAll, number of elements = "
                          + collection.size());
        }

        // Erstmal den Delegatee updaten.
        hashSet.addAll(collection);

        // Das Objekt an alle RemoteHashSets übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalHashSet peer = (LocalHashSet) iterator.next();
            if (!peer.equals(remoteStore)) {
                peer.addAllLocal(collection);
            }
        }
    }

    /**
     * Entfernt das übergebene Objekt und gibt es an alle RemoteStores, bis
     * auf den, der das Objekt übergeben hat, zur Speicherung weiter.
     *
     * @param object       Das zu entfernende Objekt.
     * @param remoteStore  Der RemoteStore, von dem das übergebene Objekt
     *                     kommt und an den das Opjekt nicht weitergeleitet
     *                     werden soll. Wenn der Wert <code>null</code> ist,
     *                     wird der Wert auch an den aufrufenden Operative
     *                     weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void remove(Serializable object, LocalHashSet remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called remove for : " + object);
        }

        // Erstmal den Delegatee updaten.
        hashSet.remove(object);

        // Das Objekt an alle RemoteHashSets übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalHashSet peer = (LocalHashSet) iterator.next();
            if (!peer.equals(remoteStore)) {
                peer.removeLocal(object);
            }
        }
    }

    /**
     * Entfernt die Objekte der übergebenen <CODE>Collection</CODE>, die
     * serialisierbar sein müssen, und gibt die <CODE>Collection</CODE> an
     * alle RemoteStores, bis auf den, der die <CODE>Collection</CODE>
     * übergeben hat, weiter.
     *
     * @param collection   Die Collection der zu entfernenden Objekte.
     * @param remoteStore  Der RemoteStore, von dem die übergebene Collection
     *                     kommt und an den sie nicht weitergeleitet werden
     *                     soll. Wenn der Wert <code>null</code> ist, wird die
     *                     Collection auch an den aufrufenden Operative
     *                     weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void removeAll(Collection collection,
                                       LocalHashSet remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called removeAll, number of elements = "
                          + collection.size());
        }

        // Erstmal den Delegatee updaten.
        hashSet.removeAll(collection);

        // Das Objekt an alle RemoteHashSets übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalHashSet peer = (LocalHashSet) iterator.next();
            if (!peer.equals(remoteStore)) {
                peer.removeAllLocal(collection);
            }
        }
    }
}

