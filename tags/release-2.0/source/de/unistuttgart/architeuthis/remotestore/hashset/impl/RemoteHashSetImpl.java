/*
 * file:        RemoteHashSetImpl.java
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.unistuttgart.architeuthis.remotestore.Transmitter;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.hashset.UserRemoteHashSet;
import de.unistuttgart.architeuthis.remotestore.hashset.interf.LocalHashSet;
import de.unistuttgart.architeuthis.remotestore.hashset.interf.RelayHashSet;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashSet. Derzeit
 * sind nur wenige Methode von <CODE>HashSet</CODE> implementiert.<P>
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RemoteHashSetImpl extends UnicastRemoteObject
    implements UserRemoteHashSet, LocalHashSet {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = -4903409536268811819L;

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
     * Dient zur Synchronisation des Zugriffs auf <CODE>relayHashSet</CODE>.
     */
    private Object relayStoreSyncObj = new Object();

    /**
     * Das <CODE>RelayHashSet</CODE>, an das Veränderungen an diesem Objekt
     * weitergeleitet werden.
     */
    private RelayHashSet relayHashSet = null;

    /**
     * Der <CODE>Transmitter</CODE> zur Übertragung der Objekte an den
     * RelayStore.
     */
    private Transmitter transmitter = null;

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
    public RemoteHashSetImpl() throws RemoteException {
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
    public RemoteHashSetImpl(boolean synchronComm) throws RemoteException {
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
    public void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        HashSetTransProc transmitProc;

        synchronized (relayStoreSyncObj) {
            if (remoteStore != null) {
                relayHashSet = (RelayHashSet) remoteStore;
                if (!synchronComm) {
                    transmitProc = new HashSetTransProc(this, relayHashSet);
                    transmitter = new Transmitter(transmitProc);
                }
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
    public void unregisterRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        synchronized (relayStoreSyncObj) {
            if ((remoteStore != null) && (remoteStore == relayHashSet)) {
                relayHashSet = null;
                if (!synchronComm) {
                    transmitter.terminate();
                    transmitter = null;
                }
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
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("unexportObject Erfolg : " + success);
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

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called addLocal for " + object);
        }

        // Den Delegatee updaten.
        hashSet.add(object);
    }

    /**
     * Speichert die Objekte der übergebenen <CODE>Collection</CODE> nur
     * im lokalen HashSet, ohne sie an das <CODE>RelayHashSet</CODE>
     * weiterzugeben.
     *
     * @param collection  Die Collection der zu speichernden Objekte.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addAllLocal(Collection collection)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called addAllLocal, number of elements = "
                          + collection.size());
        }

        // Den Delegatee updaten.
        hashSet.addAll(collection);
    }

    /**
     * Entfernt ein Objekt nur aus dem lokalen HashSet, ohne es an das
     * RelayHashSet weiterzugeben.
     *
     * @param object  Das zu entfernende Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void removeLocal(Object object) throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called removeLocal for " + object);
        }

        // Den Delegatee updaten.
        hashSet.remove(object);
    }

    /**
     * Entfernt die Objekte der übergebenen <CODE>Collection</CODE> nur aus
     * dem lokalen HashSet, ohne sie an das <CODE>RelayHashSet</CODE>
     * weiterzugeben.
     *
     * @param collection  Die Collection der zu entfernenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void removeAllLocal(Collection collection)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("called removeAllLocal, number of elements = "
                          + collection.size());
        }

        // Den Delegatee updaten.
        hashSet.removeAll(collection);
    }

    /**
     * Ermittelt, ob Daten, die über eine Methode vom Interface
     * <code>UserRemoteHashSet</code> übergeben wurden, lokal gespeichert
     * werden müssen.
     *
     * @return  <code>true</code> genau dann, wenn Daten vom zugehörigen
     *          Operative lokal gespeichert werden müssen, sonst
     *          <code>false</code>.
     */
    protected boolean localStoringNecessary() {

        synchronized (relayStoreSyncObj) {
            return ((relayHashSet == null) || !synchronComm);
        }
    }

    /**
     * Ruft für das übergebene Objekt die Methode add beim RelayStore auf,
     * falls dieser vorhanden ist. Das übergebene Objekt wird genau dann
     * wieder vom RelayStore an dieses Objekt übertragen, wenn die
     * Kommunikation synchron ist.
     *
     * @param object  Das zu übertragende Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected void addRemote(Serializable object) throws RemoteException {

        synchronized (relayStoreSyncObj) {
            if (relayHashSet != null) {
                if (synchronComm) {
                    // Das Objekt direkt an den RelayStore und damit an die
                    // anderen RemoteHashSets und indirekt an dieses
                    // RemoteHashSet übergeben.
                    relayHashSet.add(object, null);
                } else {
                    // Das Objekt an den Transmitter zur Weiterleitung an den
                    // RelayStore und damit an die anderen RemoteHashSets
                    // übergeben.
                    transmitter.enqueue(new AddObject(object));
                }
            }
        }
    }

    /**
     * Speichert das übergebene Objekt im lokalen HashSet und sendet es an
     * die anderen RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE>
     * angemeldet wurde.<p>
     *
     * Diese Methode wird vom Teilproblem aufgerufen. Für den
     * Anwendungsentwickler ist es transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public void add(Serializable object) throws RemoteException {

        if (localStoringNecessary()) {
            addLocal(object);
        }
        addRemote(object);
    }

    /**
     * Ruft für übergebene Collection die Methode addAll beim RelayStore auf,
     * falls dieser vorhanden ist. Die Collection wird genau dann wieder vom
     * RelayStore an dieses Objekt übertragen, wenn die Kommunikation synchron
     * ist.
     *
     * @param collection  Die zu übertragende Collection.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected void addAllRemote(Collection collection) throws RemoteException {

        synchronized (relayStoreSyncObj) {
            if (relayHashSet != null) {
                if (synchronComm) {
                    // Die Collection direkt an den RelayStore und damit an
                    // die anderen RemoteHashSets und indirekt an dieses
                    // RemoteHashSet übergeben.
                    relayHashSet.addAll(collection, null);
                } else {
                    // Die Collection an den Transmitter zur Weiterleitung an
                    // den RelayStore und damit an die anderen RemoteHashSets
                    // übergeben.
                    transmitter.enqueue(new AddAllObject(collection));
                }
            }
        }
    }

    /**
     * Speichert die Objekte der <CODE>Collection</CODE> im lokalen HashSet
     * und sendet sie an die anderen RemoteHashSets weiter, wenn ein
     * <CODE>RelayHashSet</CODE> angemeldet wurde.<p>
     *
     * Diese Methode wird vom Teilproblem aufgerufen. Für den
     * Anwendungsentwickler ist es transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     *
     * @param collection  Die aufzunehmenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection collection) throws RemoteException {

        if (localStoringNecessary()) {
            addAllLocal(collection);
        }
        addAllRemote(collection);
    }

    /**
     * Ruft für das übergebene Objekt die Methode remove beim RelayStore auf,
     * falls dieser vorhanden ist. Das übergebene Objekt wird genau dann
     * wieder vom RelayStore an dieses Objekt übertragen, wenn die
     * Kommunikation synchron ist.
     *
     * @param object  Das zu übertragende Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected void removeRemote(Serializable object) throws RemoteException {

        synchronized (relayStoreSyncObj) {
            if (relayHashSet != null) {
                if (synchronComm) {
                    // Das Objekt direkt an den RelayStore und damit an die
                    // anderen RemoteHashSets und indirekt an dieses
                    // RemoteHashSet übergeben.
                    relayHashSet.remove(object, null);
                } else {
                    // Das Objekt an den Transmitter zur Weiterleitung an den
                    // RelayStore und damit an die anderen RemoteHashSets
                    // übergeben.
                    transmitter.enqueue(new RemoveObject(object));
                }
            }
        }
    }

    /**
     * Entfernt das übergebene Objekt aus dem lokalen HashSet und sendet es an
     * die anderen RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE>
     * angemeldet wurde.<p>
     *
     * Diese Methode wird vom Teilproblem aufgerufen. Für den
     * Anwendungsentwickler ist es transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     *
     * @param object  Das zu entfernende Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public void remove(Serializable object) throws RemoteException {

        if (localStoringNecessary()) {
            removeLocal(object);
        }
        removeRemote(object);
    }

    /**
     * Ruft für übergebene Collection die Methode removeAll beim RelayStore
     * auf, falls dieser vorhanden ist. Die Collection wird genau dann wieder
     * vom RelayStore an dieses Objekt übertragen, wenn die Kommunikation
     * synchron ist.
     *
     * @param collection  Die zu übertragende Collection.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected void removeAllRemote(Collection collection) throws RemoteException {

        synchronized (relayStoreSyncObj) {
            if (relayHashSet != null) {
                if (synchronComm) {
                    // Die Collection direkt an den RelayStore und damit an
                    // die anderen RemoteHashSets und indirekt an dieses
                    // RemoteHashSet übergeben.
                    relayHashSet.removeAll(collection, null);
                } else {
                    // Die Collection an den Transmitter zur Weiterleitung an
                    // den RelayStore und damit an die anderen RemoteHashSets
                    // übergeben.
                    transmitter.enqueue(new RemoveAllObject(collection));
                }
            }
        }
    }

    /**
     * Entfernet die Objekte der <CODE>Collection</CODE> aus dem lokalen
     * HashSet und sendet sie an die anderen RemoteHashSets weiter, wenn ein
     * <CODE>RelayHashSet</CODE> angemeldet wurde.<p>
     *
     * Diese Methode wird vom Teilproblem aufgerufen. Für den
     * Anwendungsentwickler ist es transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     *
     * @param collection  Die zu entfernenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void removeAll(Collection collection) throws RemoteException {

        if (localStoringNecessary()) {
            removeAllLocal(collection);
        }
        removeAllRemote(collection);
    }

    /**
     * Ermittelt, ob das HashSet leer ist.
     *
     * @return  <code>true</code>, wenn das HashSet leer ist, sonst
     *          <code>false</code>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized boolean isEmpty() throws RemoteException {
        return hashSet.isEmpty();
    }

    /**
     * Ermittelt, das übergebene Objekt im HashSet enthalten ist.
     *
     * @return  <code>true</code>, wenn das übergebene Objekt im HashSet
     *          enthalten ist, <code>false</code>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized boolean contains(Serializable object)
        throws RemoteException {

        return hashSet.contains(object);
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

    /**
     * Liefert eine Kopie des verwendeten <CODE>HashSet</CODE>.
     *
     * @return  Eine Kopie des <CODE>HashSet</CODE>.
     *
     * @throws RemoteException  Wenn bei der RMI Kommunikation ein Fehler
     *                          aufgetreten ist.
     */
    public synchronized HashSet getHashSet() throws RemoteException {
        return ((HashSet) hashSet.clone());
    }
}

