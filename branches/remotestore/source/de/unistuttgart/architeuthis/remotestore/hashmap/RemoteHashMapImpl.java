/*
 * file:        RemoteHashMapImpl.java
 * created:     08.02.2005
 * last change: 05.04.2005 by Michael Wohlfart
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

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.remotestore.AbstractRelayStore;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashMap
 *
 * Für die Synchronisierung der RemoteStores sind 2 Ansätze denkbar:
 *
 * <UL>
 * <LI>"Push Ansatz": Die Daten werden beim Abspeichern an alle anderen
 *   RemoteStores weitergegeben.</LI>
 * <LI>"Get Ansatz": Die Daten werden bei der Anfrage durch den Anwender
 *   auf allen anderen RemoteStores gesucht.</LI>
 * </UL>
 *
 * Welcher Ansatz besser ist hängt von der Anwendung ab d.h. wie häufig
 * und in welcher Form (lesen/schreiben) auf die Daten im RemoteStore
 * zugegriffen wird.
 * In dieser Implementierung wird der "Push Ansatz" verwendet.<BR>
 * <BR>
 * FIXME:<BR>
 * Bei der Neuanmeldung eines RemoteStores müssen zunächst alle
 * Daten aus dem zentralen RemoteStore übernommen werden.
 *
 * 
 * @author Michael Wohlfart
 *
 */
public class RemoteHashMapImpl extends AbstractRelayStore implements RemoteHashMap {

    /**
     * generierte <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3905239013777553205L;

    /**
     * Standard Logger
     */
    private static final Logger LOGGER = Logger
            .getLogger(RemoteHashMapImpl.class.getName());


    /**
     * Der Delegatee ist das Objekt, das die eigentliche Arbeit macht
     * (an das alle get()/put()-Aufrufe weitergeleitet werden)
     */
    private HashMap hashMap = new HashMap();



    /**
     * @throws RemoteException RMI-Probleme
     */
    protected RemoteHashMapImpl() throws RemoteException {
        super();
    }

    /**
     * Diese Methode wird vom Anwendungsentwickler verwendet, um Objekte
     * zu einem Hash-Key abzulegen.
     * Für den Anwendungsentwickler ist transparent, ob hier ein lokales
     * Objekt (distStore) angesprochen wird, oder dies ein RMI-Aufruf ist
     * und das angesprochene Storage-Objekt (centralStore) auf den Dispatcher
     * liegt.
     *
     * @param key Hash-Key
     *
     * @param object das zu speichernde Objekt
     *
     * @throws RemoteException RMI-Probleme
     */
    public synchronized void put(Object key, Object object)
        throws RemoteException {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called put, key: "
                + key
                + " for "
                + object);
        }
        putRemote(this, key, object);
    }

    /**
     * Diese Methode wird vom Anwendungsentwickler verwendet, f’r den
     * Anwendungsentwickler ist transparent, ob hier ein lokales Objekt
     * angesprochen wird, oder dies ein RMI-Aufruf ist und das angesprochene
     * Storage-Objekt auf den Dispatcher liegt
     *
     * @param key hashkey
     *
     * @return das gesuchte Objekt
     *
     * @throws RemoteException RMI-Probleme
     */
    public synchronized Object get(Object key) throws RemoteException {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called get, key: "
                        + key);
        }
        // In der aktuellen Implementierung werden die Daten beim Abspeichern
        // an alle anderen RemoteStores propagiert, so dass die Daten
        // (mehr oder weniger) aktuell sein müssten.
        // In einer alternativen Implementierung könnte hier eine Anfrage
        // an den zentralen RemoteStore verwendet werden
        return getLocal(key);
    }

    /**
     * Diese Methode wird von der Plattform zur synchronisation der einzelnen
     * Storage Objekte untereinander verwendet.
     *
     * @param origin the origin of a change to the data
     *
     * @param key hashmap key
     *
     * @param object hashmap data object
     *
     * @throws RemoteException RMI-Probleme
     *
     */
    public synchronized void putRemote(Object origin, Object key, Object object)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called put, origin:"
                      + origin
                      + " key: "
                      + key
                      + " for "
                      + object);
        }
        // erstmal den Delegatee updaten:
        hashMap.put(key, object);

        // alle anderen Speicher benachrichtigen, wobei der Speicher
        // ausgelassen wird, von welchen der Update kommt
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            Object peer = iterator.next();
            // nur updaten wenn das nicht der Ursprung ist
            if (!peer.equals(origin)) {
                ((RemoteHashMap) peer) .putRemote(origin, key, object);
            }
        }

    }

    /**
     * Abfrage an den lokalen RemoteStore.
     *
     * @param key Hash-Key
     *
     * @throws RemoteException RMI Probleme
     *
     * @return das gewünschte Objekt
     */
    public synchronized Object getLocal(Object key)
        throws RemoteException  {
        // an den Delegatee weitergeben
        return hashMap.get(key);
    }

}
