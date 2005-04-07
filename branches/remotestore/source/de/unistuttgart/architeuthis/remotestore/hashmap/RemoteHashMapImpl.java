/*
 * file:        RemoteHashMapImpl.java
 * created:     08.02.2005
 * last change: 06.04.2005 by Michael Wohlfart
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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.Transmitter;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashMap. Derzeit
 * sind nur wenige Methode von <CODE>HashMap</CODE> implementiert.<P>
 *
 * ToDo: Für putAll kann ein eigener Transmitter vorgesehen werden.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RemoteHashMapImpl implements RemoteHashMap {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
//    private static final long serialVersionUID = 3905239013777553205L;

    /**
     * Standard Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteHashMapImpl.class.getName());

    /**
     * Delegatee, also das Objekt, das die lokale Arbeit macht (an das alle
     * Aufrufe weitergeleitet werden).
     */
    private HashMap hashMap = new HashMap();

    /**
     * Das <CODE>RelayHashMap</CODE>, an das Veränderungen an diesem Objekt
     * weitergeleitet werden.
     */
    private RelayHashMap relayHashMap = null;

    /**
     * Der <CODE>Transmitter</CODE>, an den die Objekte der Methode
     * <CODE>put</CODE> übergeben werden.
     */
    private Transmitter putTransmitter = null;

    /**
     * Konstruktor ohne spezielle Wirkung.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected RemoteHashMapImpl() throws RemoteException {
        super();
    }

    /**
     * Anmelden einer <CODE>RelayHashMap</CODE>.
     *
     * @param remoteStore  Das anzumendende Speicherobjekt. Wenn der Wert
     *                     <CODE>null</CODE> ist, passiert nichts.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        if (remoteStore != null) {
            relayHashMap = (RelayHashMap) remoteStore;
            putTransmitter = new Transmitter(relayHashMap, new PutProcedure());
        }
    }

    /**
     * Abmelden einer <CODE>RelayHashMap</CODE>.
     *
     * @param remoteStore  Das abzumendende Speicherobjekt. Wenn der Wert
     *                     <CODE>null</CODE> ist oder ein anderer RemoteStore
     *                     als der registrierte, passiert nichts.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void unregisterRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        if ((remoteStore != null) && (remoteStore == relayHashMap)) {
            relayHashMap = null;
            putTransmitter.terminate();
            putTransmitter = null;
        }
    }

    /**
     * Speichert zu einen key-Objekt ein value-Objekt nur lokal, ohne das
     * Objekt-Paar an die RelayMap weiterzugeben.
     *
     * @param key    Das key-Objekt, unter dem das value-Objekt gespeichert
     *               wird.
     * @param value  Das value-Objekt, das zum key-Objekt gespeichert wird.
     *
     * @throws RemoteException RMI-Probleme
     */
    synchronized void putLocal(Object key, Object value)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called put, key: " + key + " for " + value);
        }

        // Den Delegatee updaten.
        hashMap.put(key, value);
    }

    /**
     * Speichert zu einen key-Objekt ein value-Objekt. Das Objekt-Paar wird
     * zur Speicherung an andere RemoteStores weitergegeben, wenn eine
     * <CODE>RelayMap</CODE> angemeldet wurde. Für den Anwendungsentwickler
     *  ist transparent, ob hier ein lokales Objekt distStore) angesprochen
     * wird oder dies ein RMI-Aufruf ist und das angesprochene Storage-Objekt
     * (centralStore) auf den Dispatcher liegt.
     *
     * @param key    Das key-Objekt, unter dem das value-Objekt gespeichert
     *               wird.
     * @param value  Das value-Objekt, das zum key-Objekt gespeichert wird.
     *
     * @throws RemoteException RMI-Probleme
     */
    public synchronized void put(Object key, Object value)
        throws RemoteException {

        // Erstmal lokal updaten.
        putLocal(key, value);

        // Dann das Objekt-Paar an den Transmitter zur Weiterleitung an den
        // RelayStore und damit an die anderen RemoteHashMaps übergeben.
        if (putTransmitter != null) {
            putTransmitter.enqueue(new MapEntry(key, value));
        }
    }

    /**
     * Liefert zu einem key-Objekt das lokal gespeicherte value-Objekt.
     *
     * @param key  Ein key-Objekt.
     *
     * @return  Das zugehörige value-Objekt oder <CODE>null</CODE>, wenn das
     *          key-Objekt nicht enthalten ist.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized Object get(Object key) throws RemoteException {

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called get, key: " + key);
        }

        return hashMap.get(key);
    }

    /**
     * Liefert die Anzahl der in der lokalen Map enthaltenen Objekt-Paare.
     *
     * @return  Die Anzahl der enthaltenen Objekt-Paare.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized int size() throws RemoteException {
        return hashMap.size();
    }
}

