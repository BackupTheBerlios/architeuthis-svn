/*
 * file:        RelayHashMapImpl.java
 * created:     08.02.2005
 * last change: 17.04.2005 by Dietmar Lippold
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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.AbstractRelayStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Diese Klasse vermittelt zwischen Instanzen von Klassen, die
 * <CODE>RemoteHashMap</CODE> implementieren.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RelayHashMapImpl extends AbstractRelayStore implements RelayHashMap {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
//    private static final long serialVersionUID = 3905239013777553205L;

    /**
     * Standard Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RelayHashMapImpl.class.getName());

    /**
     * Delegatee, also das Objekt, das die lokale Arbeit macht (an das alle
     * Aufrufe weitergeleitet werden).
     */
    private HashMap hashMap = new HashMap();

    /**
     * Konstruktor ohne spezielle Wirkung.
     *
     * @throws RemoteException RMI-Probleme
     */
    protected RelayHashMapImpl() throws RemoteException {
        super();
    }

    /**
     * Anmeldung einer <CODE>RemoteHashMap</CODE>. Dabei wird diesem der
     * der aktuelle Inhalt dieses Objekts hinzugefügt.
     *
     * @param remoteStore  Eine neue <CODE>RemoteHashMap</CODE>
     *
     * @throws RemoteException  Bei einem Probleme mit einem RMI Zugriff.
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        LocalRemoteHashMap remoteHashMap = (LocalRemoteHashMap) remoteStore;

        // Die aktuellen Elemente dem zu registrierenden RemoteStore
        // hinzufügen.
        remoteHashMap.putAllLocal(hashMap);

        // Den zu registrierenden RemoteStore speichern.
        super.registerRemoteStore(remoteStore);
    }

    /**
     * Speichert zu einen key-Objekt ein value-Objekt. Das Objekt-Paar wird
     * zur Speicherung an alle RemoteStores weitergegeben.
     *
     * @param key    Das key-Objekt.
     * @param value  Das value-Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void put(Object key, Object value)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called put, key: " + key + " for " + value);
        }

        // Erstmal den Delegatee updaten.
        hashMap.put(key, value);

        // Das Objekt-Paar an alle RemoteHashMaps übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalRemoteHashMap peer = (LocalRemoteHashMap) iterator.next();
            peer.putLocal(key, value);
        }
    }

    /**
     * Speichert die Einträge der übergebenen Map. Die Map wird zur
     * Speicherung an alle RemoteStores weitergegeben.
     *
     * @param map  Die Map, deren Einträge gespeichert werden.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void putAll(Map map) throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called putAll, number of entries = " + map.size());
        }

        // Erstmal den Delegatee updaten.
        hashMap.putAll(map);

        // Die Map an alle RemoteHashMaps übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalRemoteHashMap peer = (LocalRemoteHashMap) iterator.next();
            peer.putAllLocal(map);
        }
    }

    /**
     * Liefert eine Kopie des verwendeten <CODE>HashMap</CODE>.
     *
     * @return  Eine Kopie des <CODE>HashMap</CODE>.
     *
     * @throws RemoteException  Wenn bei der RMI Kommunikation ein
     *                          Fehler auftritt.
     */
    public synchronized HashMap getHashMap() throws RemoteException {
        return ((HashMap) hashMap.clone());
    }
}

