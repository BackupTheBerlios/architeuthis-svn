/*
 * file:        AbstractRemoteStore.java
 * created:     08.02.2005
 * last change: 05.04.2005 by Dietmar Lippold
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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Diese Klasse implementiert die allgemeine Funktionalität eines RelayStore,
 * d.h. die Methoden zur Register- und Unregister-Funktionalität und die
 * Verwaltung der registrierten RemoteStores.<P>
 *
 * Beide Register- und Unregister-Methoden werden in einem Operative
 * verwendet, um dezentrale Speicher am zentralen Speicher an- und abzumelden.
 *
 * @author Michael Wohlfart
 */
public abstract class AbstractRelayStore extends UnicastRemoteObject
                                         implements RemoteStore {

    /**
     * Konstruktor.
     *
     * @throws RemoteException  Bei einem RMI-Problem.
     */
    protected AbstractRelayStore() throws RemoteException {
        super();
    }

    /**
     * Standard Logger Pattern.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractRelayStore.class.getName());

    /**
     * Alle registrieren RemoteStores werden in einem <CODE>HashSet</CODE>
     * gespeichert. Konkrete Implementierungen können über
     * <CODE>getRemoteStoreIterator</CODE> einen <CODE>Iterator</CODE> erhalten
     * und auf die einzelnen Elemente diesen <CODE>HashSet</CODE> zugreifen.
     */
    private HashSet registeredStores = new HashSet();

    /**
     * Anmeldung eines RemoteStores.
     *
     * @param remoteStore  Ein neuer RemoteStore.
     *
     * @throws RemoteException  Bei einem Probleme mit einem RMI Zugriff.
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
     * Abmeldung eines RemoteStores.
     *
     * @param remoteStore  Ein RemoteStore, der abgemeldet werden soll.
     *
     * @throws RemoteException  Bei einem Probleme mit einem RMI Zugriff.
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
     * Liefert einen Iterator über alle Remote Stores, die aktuell
     * registriert sind. Die Methode darf nur in einer abgeleiteten Klasse
     * aufgerufen werden und die aufrufende Methode muß auf <CODE>this</CODE>
     * synchronisiert sein.
     *
     * @return  Ein Iterator über alle registieren RemoteStores.
     */
    protected Iterator getRemoteStoreIterator() {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("getRemoteStoreIterator size: "
                          + registeredStores.size());
        }
        return registeredStores.iterator();
    }
}

