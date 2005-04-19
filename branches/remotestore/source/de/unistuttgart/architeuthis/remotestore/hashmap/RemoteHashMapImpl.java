/*
 * file:        RemoteHashMapImpl.java
 * created:     08.02.2005
 * last change: 19.04.2005 by Dietmar Lippold
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

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.unistuttgart.architeuthis.remotestore.Transmitter;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashMap. Derzeit
 * sind nur wenige Methode von <CODE>HashMap</CODE> implementiert.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RemoteHashMapImpl extends UnicastRemoteObject
    implements UserRemoteHashMap, LocalRemoteHashMap {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = -4848335853202597813L;

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
     * Der <CODE>Transmitter</CODE>, an den die Argumente der Methode
     * <CODE>put</CODE> übergeben werden.
     */
    private Transmitter putTransmitter = null;

    /**
     * Der <CODE>Transmitter</CODE>, an den das Argument der Methode
     * <CODE>putAll</CODE> übergeben werden.
     */
    private Transmitter putAllTransmitter = null;

    /**
     * Gibt an, ob diese Instanz bei Verwendung eines RelayStore dessen
     * Methoden synchron aufrufen soll. Falls kein RelayStore verwendet
     * wird, ist der Wert dieses Attributs ohne Bedeutung.
     */
    private boolean synchronComm;

    /**
     * Konstruktor, der festlegt, daß bei Verwendung eines RelayStore dessen
     * Methoden asynchron aufgerufen werden sollen.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected RemoteHashMapImpl() throws RemoteException {
        synchronComm = false;
    }

    /**
     * Konstruktor, mit dem festlegt werden kann, ob bei Verwendung eines
     * RelayStore dessen Methoden synchron aufgerufen werden sollen.
     *
     * @param synchronComm  Genau dann <CODE>true</CODE>, wenn bei
     *                      Verwendung eines RelayStore dessen Methoden
     *                      synchron aufgerufen werden sollen.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected RemoteHashMapImpl(boolean synchronComm) throws RemoteException {
        this.synchronComm = synchronComm;
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
            if (!synchronComm) {
                putTransmitter = new Transmitter(relayHashMap, new PutProcedure());
                putAllTransmitter = new Transmitter(relayHashMap,
                                                    new PutAllProcedure());

            }
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
            if (!synchronComm) {
                putTransmitter.terminate();
                putTransmitter = null;
                putAllTransmitter.terminate();
                putAllTransmitter = null;
            }
        }
    }

    /**
     * Beendet dieses RemoteStore und meldet ihn insbesondere als RMI-Dienst
     * ab.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void terminate() throws RemoteException {

        boolean success = unexportObject(this, true);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("unexportObject Erfolg : " + success);
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
    public synchronized void putLocal(Object key, Object value)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called put, key: " + key + " for " + value);
        }

        // Den Delegatee updaten.
        hashMap.put(key, value);
    }

    /**
     * Speichert die Einträge der übergebenen Map lokal, ohne die Objekt-Paare
     * an die RelayMap weiterzugeben.
     *
     * @param map  Die Map, deren Einträge gespeichert werden.
     *
     * @throws RemoteException RMI-Probleme
     */
    public synchronized void putAllLocal(Map map)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called putAll, number of entries = " + map.size());
        }

        // Den Delegatee updaten.
        hashMap.putAll(map);
    }

    /**
     * Speichert zu einen key-Objekt ein value-Objekt. Das Objekt-Paar wird
     * zur Speicherung an andere RemoteStores weitergegeben, wenn eine
     * <CODE>RelayMap</CODE> angemeldet wurde. Für den Anwendungsentwickler
     * ist transparent, ob hier ein lokales Objekt (distStore) angesprochen
     * wird oder dies ein RMI-Aufruf ist und das angesprochene Storage-Objekt
     * (centralStore) auf den Dispatcher liegt.
     *
     * @param key    Das key-Objekt, unter dem das value-Objekt gespeichert
     *               wird.
     * @param value  Das value-Objekt, das zum key-Objekt gespeichert wird.
     *
     * @throws RemoteException RMI-Probleme
     */
    public void put(Serializable key, Serializable value) throws RemoteException {

        // Erstmal lokal updaten.
        putLocal(key, value);

        if (relayHashMap != null) {
            if (synchronComm) {
                // Das Objekt-Paar direkt an den RelayStore und damit an
                // die anderen RemoteHashMaps übergeben.
                relayHashMap.put(key, value);
            } else {
                // Das Objekt-Paar an den Transmitter zur Weiterleitung an
                // den RelayStore und damit an die anderen RemoteHashMaps
                // übergeben.
                putTransmitter.enqueue(new MapEntry(key, value));
            }
        }
    }

    /**
     * Speichert die Einträge der übergebenen Map, die serialisierbar sein
     * müssen. Die Map wird zur Speicherung an andere RemoteStores
     * weitergegeben, wenn eine <CODE>RelayMap</CODE> angemeldet wurde. Für
     * den Anwendungsentwickler ist transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     *
     * @param map  Die Map, deren Einträge gespeichert werden.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void putAll(Map map) throws RemoteException {

        // Erstmal lokal updaten.
        putAllLocal(map);

        if (relayHashMap != null) {
            if (synchronComm) {
                // Die Map direkt an den RelayStore und damit an die anderen
                // RemoteHashMaps übergeben.
                relayHashMap.putAll(map);
            } else {
                // Die Map an den Transmitter zur Weiterleitung an den
                // RelayStore und damit an die anderen RemoteHashMaps
                // übergeben.
                putAllTransmitter.enqueue(map);
            }
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
    public synchronized Serializable get(Serializable key) throws RemoteException {

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called get, key: " + key);
        }

        return ((Serializable) hashMap.get(key));
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

