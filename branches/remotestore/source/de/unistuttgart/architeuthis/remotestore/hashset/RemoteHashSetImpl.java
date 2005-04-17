/*
 * file:        RemoteHashSetImpl.java
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


package de.unistuttgart.architeuthis.remotestore.hashset;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.Transmitter;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashSet. Derzeit
 * sind nur wenige Methode von <CODE>HashSet</CODE> implementiert.<P>
 *
 * ToDo: Für addAll kann ein eigener Transmitter vorgesehen werden.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RemoteHashSetImpl extends UnicastRemoteObject
    implements UserRemoteHashSet, LocalRemoteHashSet {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
//    private static final long serialVersionUID = 3257847679689505078L;

    /**
     * Standard Logger Pattern.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteHashSetImpl.class.getName());

    /**
     * Delegatee, also das Objekt, das die lokale Arbeit macht (an das alle
     * Aufrufe weitergeleitet werden).
     */
    private HashSet hashSet = new HashSet();

    /**
     * Das <CODE>RelayHashSet</CODE>, an das Veränderungen an diesem Objekt
     * weitergeleitet werden.
     */
    private RelayHashSet relayHashSet = null;

    /**
     * Der <CODE>Transmitter</CODE>, an den die Objekte der Methode
     * <CODE>add</CODE> übergeben werden.
     */
    private Transmitter addTransmitter = null;

    /**
     * Der <CODE>Transmitter</CODE>, an den die Objekte der Methode
     * <CODE>addAll</CODE> übergeben werden.
     */
    private Transmitter addAllTransmitter = null;

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
    protected RemoteHashSetImpl() throws RemoteException {
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
    protected RemoteHashSetImpl(boolean synchronComm) throws RemoteException {
        this.synchronComm = synchronComm;
    }

    /**
     * Anmelden eines <CODE>RelayHashSet</CODE>.
     *
     * @param remoteStore  Das anzumendende Speicherobjekt. Wenn der Wert
     *                     <CODE>null</CODE> ist, passiert nichts.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        if (remoteStore != null) {
            relayHashSet = (RelayHashSet) remoteStore;
            if (!synchronComm) {
                addTransmitter = new Transmitter(relayHashSet, new AddProcedure());
                addAllTransmitter = new Transmitter(relayHashSet,
                                                    new AddAllProcedure());
            }
        }
    }

    /**
     * Abmelden eines <CODE>RelayHashSet</CODE>.
     *
     * @param remoteStore  Das abzumendende Speicherobjekt. Wenn der Wert
     *                     <CODE>null</CODE> ist oder ein anderer RemoteStore
     *                     als der registrierte, passiert nichts.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void unregisterRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        if ((remoteStore != null) && (remoteStore == relayHashSet)) {
            relayHashSet = null;
            if (!synchronComm) {
                addTransmitter.terminate();
                addTransmitter = null;
                addAllTransmitter.terminate();
                addAllTransmitter = null;
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
     * Speichert ein Objekt nur im lokalen HashSet, ohne es an das
     * RelayHashSet weiterzugeben.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addLocal(Object object) throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called add for " + object);
        }

        // Den Delegatee updaten.
        hashSet.add(object);
    }

    /**
     * Speichert die Objekte der übergebenen <CODE>Collection</CODE> nur im
     * lokalen HashSet, ohne sie an das <CODE>RelayHashSet</CODE>
     * weiterzugeben.
     *
     * @param collection  Die Collection der zu speichernden Objekte.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addAllLocal(Collection collection)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called addAll, number of elements = "
                        + collection.size());
        }

        // Den Delegatee updaten.
        hashSet.addAll(collection);
    }

    /**
     * Speichert ein Objekt im lokalen HashSet und sendet es an andere
     * RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE> angemeldet
     * wurde. Diese Methode wird vom Teilproblem aufgerufen. Für den
     * Anwendungsentwickler ist es transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public void add(Object object) throws RemoteException {

        // Erstmal lokal updaten.
        addLocal(object);

        if (relayHashSet != null) {
            if (synchronComm) {
                // Das Objekt direkt an den RelayStore und damit an die
                // anderen RemoteHashSets übergeben.
                relayHashSet.add(object);
            } else {
                // Das Objekt an den Transmitter zur Weiterleitung an den
                // RelayStore und damit an die anderen RemoteHashSets
                // übergeben.
                addTransmitter.enqueue(object);
            }
        }
    }

    /**
     * Speichert die Objekte der <CODE>Collection</CODE> im lokalen HashSet
     * und sendet sie an andere RemoteHashSets weiter, wenn ein
     * <CODE>RelayHashSet</CODE> angemeldet wurde. Diese Methode wird vom
     * Teilproblem aufgerufen. Für den Anwendungsentwickler ist es
     * transparent, ob hier ein lokales Objekt (distStore) angesprochen wird
     * oder dies ein RMI-Aufruf ist und das angesprochene Storage-Objekt
     * (centralStore) auf den Dispatcher liegt.
     *
     * @param collection  Die aufzunehmenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection collection) throws RemoteException {

        // Erstmal lokal updaten.
        addAllLocal(collection);

        if (relayHashSet != null) {
            if (synchronComm) {
                // Die Collection direkt an den RelayStore und damit an die
                // anderen RemoteHashSets übergeben.
                relayHashSet.addAll(collection);
            } else {
                // Die Collection an den Transmitter zur Weiterleitung an den
                // RelayStore und damit an die anderen RemoteHashSets
                // übergeben.
                addAllTransmitter.enqueue(collection);
            }
        }
    }

    /**
     * Liefert die Anzahl der in dieser Menge enthaltenen Objekte.
     *
     * @return  Die Anzahl der enthaltenen Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized int size() throws RemoteException {
        return hashSet.size();
    }
}

