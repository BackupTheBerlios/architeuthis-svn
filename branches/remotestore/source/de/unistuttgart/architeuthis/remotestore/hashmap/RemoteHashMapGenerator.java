/*
 * file:        RemoteHashMapGenerator.java
 * created:     08.02.2005
 * last change: 19.04.2005 by Dietmar Lippold
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

import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;

/**
 * Klasse, die RemoteStores mit der Funktionalit‰t einer <CODE>HashMap</CODE>
 * erzeugt. ‹ber den Konstruktor kann angegeben werden, ob nur ein zentraler
 * oder zus‰tzlich mehrere dezentrale RemoteStores verwendet werden sollen und
 * ob im zweiten Fall die Methoden des zentralen synchron oder asynchron
 * aufgerufen werden sollen.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 *
 */
public class RemoteHashMapGenerator implements RemoteStoreGenerator {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = -4022863673565035332L;

    /**
     * Standard Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RemoteHashMapGenerator.class.getName());

    /**
     * Gibt an, ob ausschlieﬂlich ein zentraler Speicher verwendet werden
     * soll.
     */
    private boolean isCentralOnly;

    /**
     * Gibt an, ob die verteilten RemoteStores bei Verwendung eines
     * RelayStore dessen Methoden synchron aufrufen sollen. Falls kein
     * RelayStore verwendet wird, ist der Wert dieses Attributs ohne
     * Bedeutung.
     */
    private boolean synchronComm;

    /**
     * Konstruktor, bei dem nur ein zentraler und kein verteilter
     * RemoteStore von der erzeugten Instanz erzeugt wird.
     */
    public RemoteHashMapGenerator() {
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
    public RemoteHashMapGenerator(boolean synchronComm) {
        this.isCentralOnly = false;
        this.synchronComm = synchronComm;
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
                return (new RemoteHashMapImpl());
            } else {
                return (new RelayHashMapImpl());
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
                    if (synchronComm) {
                        LOGGER.info("Erzeuge dezentralen synchronen RemoteStore.");
                    } else {
                        LOGGER.info("Erzeuge dezentralen asynchronen RemoteStore.");
                    }
                }
                return (new RemoteHashMapImpl(synchronComm));
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}

