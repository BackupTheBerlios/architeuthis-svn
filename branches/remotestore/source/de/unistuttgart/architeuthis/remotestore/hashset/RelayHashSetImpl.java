/*
 * file:        RelayHashSetImpl.java
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
import java.util.Iterator;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.AbstractRelayStore;

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
//    private static final long serialVersionUID = 3257847679689505078L;

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
    protected RelayHashSetImpl() throws RemoteException {
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

        LocalRemoteHashSet remoteHashSet = (LocalRemoteHashSet) remoteStore;

        // Die aktuellen Elemente dem zu registrierenden RemoteStore
        // hinzufügen.
        Iterator iter = hashSet.iterator();
        while (iter.hasNext()) {
            remoteHashSet.addLocal(iter.next());
        }

        // Den zu registrierenden RemoteStore speichern.
        super.registerRemoteStore(remoteStore);
    }

    /**
     * Diese Methode wird von RemoteHashSets aufgerufen, denen ein neuen
     * Objekt zur Speicherung übergeben wurde. Dieses Objekt wird an alle
     * RemoteHashSets weitergegeben.
     *
     * @param object  Das neue Objekt.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    public synchronized void add(Object object) throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("called addRemote for : " + object);
        }

        // Erstmal den Delegatee updaten.
        hashSet.add(object);

        // Das Objekt an alle RemoteHashSets übertragen.
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            LocalRemoteHashSet peer = (LocalRemoteHashSet) iterator.next();
            peer.addLocal(object);
        }
    }

    /**
     * Liefert eine Kopie des verwendeten <CODE>HashSet</CODE>.
     *
     * @return  Eine Kopie des <CODE>HashSet</CODE>.
     *
     * @throws RemoteException  Wenn bei der RMI Kommunikation ein
     *                          Fehler auftritt.
     */
    public synchronized HashSet getHashSet() throws RemoteException {
        return ((HashSet) hashSet.clone());
    }
}

