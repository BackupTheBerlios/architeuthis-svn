/*
 * file:        RemoteHashSetNewImpl.java
 * created:     12.04.2006
 * last change: 12.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.remotestore.hashsetnew;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.hashset.interf.LocalHashSet;
import de.unistuttgart.architeuthis.remotestore.hashset.impl.RemoteHashSetImpl;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashSet, wobei
 * zus�tzlich die Menge der vom RealyStore neu hinzugef�gten Objekte verwaltet
 * wird. Derzeit sind nur wenige Methode von <CODE>HashSet</CODE>
 * implementiert.
 *
 * @author Dietmar Lippold
 */
public class RemoteHashSetNewImpl extends RemoteHashSetImpl
    implements UserRemoteHashSetNew, LocalHashSet {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 6161099304610034986L;

    /**
     * Standard Logger Pattern.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteHashSetNewImpl.class.getName());

    /**
     * Die Menge der neu vom RealyStore hinzugef�gten Objekte.
     */
    private HashSet newElements = new HashSet();

    /**
     * Konstruktor, der festlegt, da� bei Verwendung eines RelayStore dessen
     * Methoden asynchron aufgerufen werden sollen.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public RemoteHashSetNewImpl() throws RemoteException {
        super();
    }

    /**
     * Konstruktor, mit dem festlegt werden kann, ob bei Verwendung eines
     * RelayStore dessen Methoden synchron aufgerufen werden sollen.
     *
     * @param synchronComm  Genau dann <CODE>true</CODE>, wenn bei Verwendung
     *                      eines RelayStore dessen Methoden synchron
     *                      aufgerufen werden sollen.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public RemoteHashSetNewImpl(boolean synchronComm) throws RemoteException {
        super(synchronComm);
    }

    /**
     * Speichert ein Objekt nur im lokalen HashSet, ohne es an das
     * RelayHashSet weiterzugeben. Das �bergebene Objekt wird au�erdem in der
     * Menge der neuen Objekte gespeichert.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addLocal(Object object) throws RemoteException {

        super.addLocal(object);

        // Die neuen Elemente erg�nzen.
        newElements.add(object);
    }

    /**
     * Speichert die Objekte der �bergebenen <CODE>Collection</CODE> nur
     * im lokalen HashSet, ohne sie an das <CODE>RelayHashSet</CODE>
     * weiterzugeben. Die �bergebenen Objekte werden au�erdem in der
     * Menge der neuen Objekte gespeichert.
     *
     * @param collection  Die Collection der zu speichernden Objekte.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void addAllLocal(Collection collection)
        throws RemoteException {

        super.addAllLocal(collection);

        // Die neuen Elemente erg�nzen.
        newElements.addAll(collection);
    }

    /**
     * Speichert ein Objekt im lokalen HashSet und sendet es an andere
     * RemoteHashSets weiter, wenn ein <CODE>RelayHashSet</CODE> angemeldet
     * wurde. Diese Methode wird vom Teilproblem aufgerufen. F�r den
     * Anwendungsentwickler ist es transparent, ob hier ein lokales Objekt
     * (distStore) angesprochen wird oder dies ein RMI-Aufruf ist und das
     * angesprochene Storage-Objekt (centralStore) auf den Dispatcher liegt.
     * <P>
     * Das �bergebene Objekt wird nicht in der Menge der neuen Objekte
     * gespeichert.
     *
     * @param object  Das zu speichernde Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public void add(Serializable object) throws RemoteException {

        super.addLocal(object);
        addRemote(object);
    }

    /**
     * Speichert die Objekte der <CODE>Collection</CODE> im lokalen HashSet
     * und sendet sie an andere RemoteHashSets weiter, wenn ein
     * <CODE>RelayHashSet</CODE> angemeldet wurde. Diese Methode wird vom
     * Teilproblem aufgerufen. F�r den Anwendungsentwickler ist es
     * transparent, ob hier ein lokales Objekt (distStore) angesprochen wird
     * oder dies ein RMI-Aufruf ist und das angesprochene Storage-Objekt
     * (centralStore) auf den Dispatcher liegt.<P>
     *
     * Die �bergebenen Objekte werden nicht in der Menge der neuen Objekte
     * gespeichert.
     *
     * @param collection  Die aufzunehmenden Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void addAll(Collection collection) throws RemoteException {

        super.addAllLocal(collection);
        addAllRemote(collection);
    }

    /**
     * Liefert die Anzahl der vom RelayStore seit dem letzten Aufruf von
     * <CODE>newElements</CODE> neu hinzugef�gten Objekte.
     *
     * @return  Die Anzahl der enthaltenen Objekte.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public synchronized int newElementNumber() throws RemoteException {
        return newElements.size();
    }

    /**
     * Liefert eine Menge der vom RelayStore seit dem letzten Aufruf dieser
     * Methode neu hinzugef�gten Objekte.
     *
     * @return  Eine Menge der vom RelayStore seit dem letzten Aufruf dieser
     *          Methode neu hinzugef�gten Objekte.
     *
     * @throws RemoteException  Wenn bei der RMI Kommunikation ein Fehler
     *                          aufgetreten ist.
     */
    public synchronized HashSet newElements() throws RemoteException {
        HashSet returnElements;

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Abruf der neuen Eemente");
        }

        returnElements = newElements;
        newElements = new HashSet();

        return returnElements;
    }
}

