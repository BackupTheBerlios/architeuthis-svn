/*
 * file:        RemoteHashSetNewGenerator.java
 * created:     12.04.2006
 * last change: 08.05.2006 by Dietmar Lippold
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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.remotestore.hashset.impl.RelayHashSetImpl;

/**
 * Klasse, die RemoteStores mit der Funktionalität eines <CODE>HashSet</CODE>
 * erzeugt, wobei zusätzlich die Menge der vom zentralen RemoteStore
 * (RealyStore) neu hinzugefügten Objekte verwaltet wird. Es werden immer ein
 * zentraler und zusätzlich mehrere dezentrale RemoteStores verwendet und die
 * Methoden des zentralen RealyStore werden asynchron aufgerufen.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class RemoteHashSetNewGenerator implements RemoteStoreGenerator {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = -5328463158784054092L;

    /**
     * Standard Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteHashSetNewGenerator.class.getName());

    /**
     * Erzeugt eine neue Instanz.
     */
    public RemoteHashSetNewGenerator() {
        super();
    }

    /**
     * Liefert den zentralen RemoteStore.
     *
     * @return  Den zentralen RemoteStore.
     */
    public RemoteStore generateCentralRemoteStore() {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Erzeuge zentralen RemoteStore.");
        }

        try {
            return (new RelayHashSetImpl());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert den dezentralen RemoteStore.
     *
     * @return  Den dezentralen RemoteStore.
     */
    public RemoteStore generateDistRemoteStore() {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Erzeuge dezentralen asynchronen RemoteStore.");
        }

        try {
            return (new RemoteHashSetNewImpl());
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

