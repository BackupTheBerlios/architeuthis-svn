/*
 * file:        RemoteHashMapGenerator.java
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


package de.unistuttgart.architeuthis.remotestore.hashmap;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;

/**
 * Klasse zur Erzeugung HashMap �hnlicher RemoteStores f�r Architeuthis.
 * �ber ein Flag im Konstruktor kann gesteuert werden, ob ein
 * zentraler oder mehrere dezentrale Speicher verwendet werden.
 *
 * @author Michael Wohlfart
 *
 */
public class RemoteHashMapGenerator implements RemoteStoreGenerator {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
//    private static final long serialVersionUID = 3545517322140727097L;

    /**
     * Standard Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteHashMapGenerator.class.getName());

    /**
     * Defaulm�ssig wird lediglich zentraler Speicher verwendet.
     */
    private boolean isCentralOnly = true;

    /**
     * Einfacher Konstruktor f�r die Defaulteinstellungen.
     */
    public RemoteHashMapGenerator() {
    }

    /**
     * Konstruktor, bei dem anzugeben ist, ob ein verteilter Speicher
     * verwendet werden soll.
     *
     * @param isCentralOnly  <CODE>true</CODE>, wenn nur ein zentraler Speicher
     *                       verwendet werden soll, anderenfalls, wenn
     *                       dezentrale Speicher verwendet werden sollen,
     *                       <CODE>false</CODE>.
     */
    public RemoteHashMapGenerator(boolean isCentralOnly) {
        this.isCentralOnly = isCentralOnly;
    }

    /**
     * Liefert den zentralen RemoteStore.
     *
     * @return  Den zentralen RemoteStore.
     */
    public RemoteStore generateCentralRemoteStore() {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Erzeuge zentralen RemoteStore.");
        }

        try {
            if (isCentralOnly) {
                return new RemoteHashMapImpl();
            } else {
                return new RelayHashMapImpl();
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert den dezentralen RemoteStore oder <CODE>null</CODE>, falls
     * nur ein zentraler Speicher verwendet werden soll.
     *
     * @return  Den dezentralen RemoteStore oder <CODE>null</CODE>.
     */
    public RemoteStore generateDistRemoteStore() {
        if (isCentralOnly) {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Dezentraler RemoteStore wird nicht verwendet.");
            }
            return null;
        } else {
            try {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info("Erzeuge dezentralen RemoteStore.");
                }
                return new RemoteHashMapImpl();
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}

