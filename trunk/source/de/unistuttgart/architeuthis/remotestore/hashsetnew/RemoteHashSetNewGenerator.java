/*
 * file:        RemoteHashSetNewGenerator.java
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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.remotestore.hashset.impl.RelayHashSetImpl;

/**
 * Klasse, die RemoteStores mit der Funktionalität eines <CODE>HashSet</CODE>
 * erzeugt, wobei zusätzlich die Menge der vom RealyStore neu hinzugefügten
 * Objekte verwaltet wird. Über den Konstruktor kann angegeben werden, ob nur
 * ein zentraler oder zusätzlich mehrere dezentrale RemoteStores verwendet
 * werden sollen und ob im zweiten Fall die Methoden des zentralen synchron
 * oder asynchron aufgerufen werden sollen.
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
     * Gibt an, ob ausschließlich ein zentraler Speicher verwendet werden
     * soll.
     */
    private boolean isCentralOnly;

    /**
     * Gibt an, ob die verteilten RemoteStores bei Verwendung eines RelayStore
     * dessen Methoden synchron aufrufen sollen. Falls kein RelayStore
     * verwendet wird, ist der Wert dieses Attributs ohne Bedeutung.
     */
    private boolean synchronComm;

    /**
     * Konstruktor, bei dem nur ein zentraler und kein verteilter RemoteStore
     * von der erzeugten Instanz erzeugt wird.
     */
    public RemoteHashSetNewGenerator() {
        this.isCentralOnly = true;
    }

    /**
     * Konstruktor, bei dem sowohl ein zentraler wie verteilte RemoteStores
     * von der erzeugten Instanz erzeugt wird. Als Parameter ist anzugeben,
     * ob die Aufrufe der verteilten beim zentralen RemoteStore synchron
     * erfolgen sollen.
     *
     * @param synchronComm  <CODE>true</CODE>, wenn die Aufrufe der verteilten
     *                      beim zentralen RemoteStore synchron erfolgen
     *                      sollen, anderenfalls <CODE>false</CODE>.
     */
    public RemoteHashSetNewGenerator(boolean synchronComm) {
        this.isCentralOnly = false;
        this.synchronComm = synchronComm;
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
            if (isCentralOnly) {
                return (new RemoteHashSetNewImpl());
            } else {
                return (new RelayHashSetImpl());
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert den dezentralen RemoteStore oder <CODE>null</CODE>, falls nur
     * ein zentraler Speicher verwendet werden soll.
     *
     * @return  Den dezentralen RemoteStore oder <CODE>null</CODE>.
     */
    public RemoteStore generateDistRemoteStore() {

        if (isCentralOnly) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Dezentraler RemoteStore wird nicht verwendet.");
            }
            return null;
        } else {
            try {
                if (LOGGER.isLoggable(Level.FINE)) {
                    if (synchronComm) {
                        LOGGER.fine("Erzeuge dezentralen synchronen RemoteStore.");
                    } else {
                        LOGGER.fine("Erzeuge dezentralen asynchronen RemoteStore.");
                    }
                }
                return (new RemoteHashSetNewImpl(synchronComm));
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}

