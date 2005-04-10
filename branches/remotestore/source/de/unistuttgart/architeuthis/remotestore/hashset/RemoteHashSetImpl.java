/*
 * file:        RemoteHashSetImpl.java
 * created:     08.02.2005
 * last change: 10.04.2005 by Dietmar Lippold
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
import java.util.HashSet;
import java.util.Collection;
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
public class RemoteHashSetImpl extends UnicastRemoteObject implements RemoteHashSet {

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
     * Konstruktor ohne spezielle Wirkung.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected RemoteHashSetImpl() throws RemoteException {
        super();
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
            addTransmitter = new Transmitter(relayHashSet, new AddProcedure());
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
            addTransmitter.terminate();
            addTransmitter = null;
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
    synchronized void addLocal(Object object) throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called add for " + object);
        }

        // Den Delegatee updaten.
        hashSet.add(object);
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
    public synchronized void add(Object object) throws RemoteException {

        // Erstmal lokal updaten.
        addLocal(object);

        // Dann das Objekt an den Transmitter zur Weiterleitung an den
        // RelayStore und damit an die anderen RemoteHashSets übergeben.
        if (addTransmitter != null) {
            addTransmitter.enqueue(object);
        }
    }

    /**
     * Speichert Objekte im lokalen HashSet und sendet sie an andere
     * RemoteHashSets weiter. Diese Methode wird vom Teilproblem aufgerufen.
     *
     * @param objects  Die aufzunehmenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized void addAll(Collection objects) throws RemoteException {

        Iterator iter = objects.iterator();
        while (iter.hasNext()) {
            add(iter.next());
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

