/*
 * file:        AbstractRemoteStore.java
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
package de.unistuttgart.architeuthis.remotestore;

import java.util.logging.Level;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.remotestore.hashmap.RemoteHashMapImpl;

/**
 * Diese Klasse implementiert die Register- und
 * Unregister-Funktionalität für einen RemoteStore.
 *
 * Beide Methoden werden in der Operative Komponente verwendet um
 * dezentrale Speicher am zentralen Speicher an- bzw. abzumelden.
 *
 *
 *
 * @author Michael Wohlfart
 *
 */
public abstract class AbstractRemoteStore extends UnicastRemoteObject
                                          implements RemoteStore {
    /**
     * Konstruktor
     *
     * @throws RemoteException RMI-Probleme
     */
    protected AbstractRemoteStore() throws RemoteException {
        super();
    }

    /**
     * Standard Logger Pattern
     */
    private static final Logger LOGGER = Logger
            .getLogger(RemoteHashMapImpl.class.getName());


    /**
     * Alle registrieren RemoteStores werden in einem HashSet
     * gespeichert. Konkrete Implementierungen können über
     * getRemoteStoreIterator einen Iterator erhalten und auf
     * die einzelnen Elemente diesen HashSet zugreifen.
     */
    private HashSet registeredStores = new HashSet();


    /**
     * Anmeldung eines RemoteStores
     *
     * @param remoteStore neuer RemoteStore
     *
     * @throws RemoteException Probleme mit RMI Zugriff
     */
    public synchronized void registerRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("registering RemoteStore: "
                    + remoteStore
                    + " in "
                    + this);
        }

        registeredStores.add(remoteStore);
    }


    /**
     * Abmeldung eines RemoteStores
     *
     * @param remoteStore RemoteStore der abgemeldet werden soll
     *
     * @throws RemoteException Zugriff ’ber RMI
     */
    public synchronized void unregisterRemoteStore(RemoteStore remoteStore)
        throws RemoteException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("unregistering RemoteStore: "
                    + remoteStore
                    + " in "
                    + this);
        }

        registeredStores.remove(remoteStore);
    }


    /**
     * Liefert eine Iteration über alle Remote Stores,
     * die aktuell registriert sind.
     *
     * @return Iterator der alle registieren RemoteStores enthält.
     */
    protected Iterator getRemoteStoreIterator() {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("getRemoteStoreIterator size: "
                    + registeredStores.size());
        }
        return registeredStores.iterator();
    }


}
