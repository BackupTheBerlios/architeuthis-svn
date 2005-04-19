/*
 * file:        RemoteHashSetImpl.java
 * created:     08.02.2005
 * last change: 08.02.2005 by Michael Wohlfart
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

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;

import de.unistuttgart.architeuthis.remotestore.AbstractRemoteStore;

/**
 * Diese Klasse implementiert das RemoteStore Interface als HashSet
 *
 * @author Michael Wohlfart
 *
 * FIXME: RMI-Stubs müssen im build.xml compiliert werden
 */
public class RemoteHashSetImpl extends AbstractRemoteStore implements RemoteHashSet {

    /**
     * generierte <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3257847679689505078L;

    /**
     * Standard Logger Pattern
     */
    private static final Logger LOGGER = Logger
        .getLogger(RemoteHashSetImpl.class.getName());


    /**
     * Delegatee, das Objekt, das die eigentliche Arbeit macht
     * (an das alle get()/put()-Aufrufe weitergeleitet werden)
     */
    private HashSet hashSet = new HashSet();



    /**
     * @throws RemoteException RMI-Probleme
     */
    protected RemoteHashSetImpl() throws RemoteException {
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
     * @param object das zu speichernde Objekt
     *
     * @throws RemoteException RMI-Probleme
     */
    public synchronized void add(Object object)
        throws RemoteException {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called put,  for "
                    + object);
        }
        addRemote(this, object);
    }

    /**
     * Diese Methode wird von der Plattform zur synchronisation der einzelnen
     * Storage Objekte untereinander verwendet.
     *
     * @param origin the origin of a change to the data
     *
     * @param object hashmap data object
     *
     * @throws RemoteException RMI-Probleme
     *
     */
    public synchronized void addRemote(Object origin, Object object)
        throws RemoteException {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("called put, origin:"
                    + origin
                    + " for "
                    + object);
        }
        // erstmal den Delegatee updaten:
        hashSet.add(object);

        // alle anderen Speicher benachrichtigen, wobei der Speicher
        // ausgelassen wird, von welchen der Update kommt
        Iterator iterator = getRemoteStoreIterator();
        while (iterator.hasNext()) {
            Object peer = iterator.next();
            // nur updaten wenn das nicht der Ursprung ist
            if (!peer.equals(origin)) {
                ((RemoteHashSet) peer) .addRemote(origin, object);
            }
        }

    }


    /**
     * Liefert clone des verwendeten HashSet
     *
     * @return Clone des HashSet
     *
     * @throws RemoteException wenn bei der RMI Kommunikation ein
     * Fehler auftritt
     */
    public synchronized HashSet getHashSet() throws RemoteException {
        return ((HashSet) hashSet.clone());
    }



}
