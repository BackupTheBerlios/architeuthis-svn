/*
 * file:        RelayHashMapImpl.java
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


package de.unistuttgart.architeuthis.remotestore.hashmap.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.AbstractRelayStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.hashmap.interf.LocalHashMap;
import de.unistuttgart.architeuthis.remotestore.hashmap.interf.RelayHashMap;

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
    private static final long serialVersionUID = 4034843940855473757L;

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
    public RelayHashMapImpl() throws RemoteException {
        super();
    }

    /**
     * Anmeldung einer <CODE>RemoteHashMap</CODE>. Dabei wird diesem der
     * der aktuelle Inhalt dieses Objekts hinzugef�gt.
     *
     * @param remoteStore  Eine neue <CODE>RemoteHashMap</CODE>
     *
     * @throws RemoteException  Bei einem Probleme mit einem RMI Zugriff.
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        LocalHashMap remoteHashMap = (LocalHashMap) remoteStore;

        // Die aktuellen Elemente dem zu registrierenden RemoteStore
        // hinzuf�gen.
        remoteHashMap.putAllLocal(hashMap);

        // Den zu registrierenden RemoteStore speichern.
        super.registerRemoteStore(remoteStore);
    }

    /**
     * Speichert zu einen key-Objekt ein value-Objekt. Das Objekt-Paar wird
     * zur Speicherung an alle RemoteStores, bis auf den, der das Objekt
     * �bergeben hat, weitergegeben.
     *
     * @param key          Das key-Objekt, unter dem das value-Objekt
     *                     gespeichert wird.
     * @param value        Das value-Objekt, das zum key-Objekt gespeichert
     *                     wird.
     * @param remoteStore  Der RemoteStore, von dem die �bergebenen Objekte
     *                     kommen und an den die Opjekte nicht weitergeleitet
     *                     werden sollen. Wenn der Wert <code>null</code> ist,
     *                     werden die Werte auch an den aufrufenden Operative
     *                     weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void put(Serializable key, Serializable value,
                                 LocalHashMap remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called put, key: " + key + " for " + value);
        }

        // Erstmal den Delegatee updaten.
        hashMap.put(key, value);

        // Das Objekt-Paar an alle RemoteHashMaps �bertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalHashMap peer = (LocalHashMap) iterator.next();
            if (!peer.equals(remoteStore)) {
                peer.putLocal(key, value);
            }
        }
    }

    /**
     * Speichert die Eintr�ge der �bergebenen Map, die serialisierbar sein
     * m�ssen. Die Map wird zur Speicherung an alle RemoteStores, bis auf den,
     * der das Objekt �bergeben hat, weitergegeben.
     *
     * @param map          Die Map, deren Eintr�ge gespeichert werden.
     * @param remoteStore  Der RemoteStore, von dem die �bergebene Map kommt
     *                     und an den sie nicht weitergeleitet werden soll.
     *                     Wenn der Wert <code>null</code> ist, wird die Map
     *                     auch an den aufrufenden Operative weitergeleitet.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void putAll(Map map, LocalHashMap remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called putAll, number of entries = " + map.size());
        }

        // Erstmal den Delegatee updaten.
        hashMap.putAll(map);

        // Die Map an alle RemoteHashMaps �bertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalHashMap peer = (LocalHashMap) iterator.next();
            if (!peer.equals(remoteStore)) {
                peer.putAllLocal(map);
            }
        }
    }
}

