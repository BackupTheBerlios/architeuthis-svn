/*
 * file:        PutAllProcedure.java
 * created:     17.04.2005
 * last change: 11.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.remotestore.hashmap.impl;

import java.util.Map;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.TransmitProcedure;
import de.unistuttgart.architeuthis.remotestore.hashmap.interf.RelayHashMap;
import de.unistuttgart.architeuthis.remotestore.hashmap.interf.LocalRemoteHashMap;

/**
 * Implementiert eine Methode, die beim RelayStore für eine Map die Methode
 * <CODE>putAll</CODE> aufruft.
 *
 * @author Dietmar Lippold
 */
public class PutAllProcedure implements TransmitProcedure {

    /**
     * Lokaler RemotStore, von dem die zu übertragenden Daten stammen.
     */
    private LocalRemoteHashMap localStore;

    /**
     * RelayStore, an den die Daten übertragen werden sollen.
     */
    private RelayHashMap relayStore;

    /**
     * Erzeugt eine Instanz.
     *
     * @param localStore  Der lokale RemotStore, von dem die zu übertragenden
     *                    Objekte stammen.
     * @param relayStore  Der RelayStore, an den die Objekte übertragen
     *                    werden.
     */
    public PutAllProcedure(LocalRemoteHashMap localStore,
                           RelayHashMap relayStore) {

        this.localStore = localStore;
        this.relayStore = relayStore;
    }

    /**
     * Übertragt die übergebenen Map zur zentralen <CODE>RelayHashMap</CODE>,
     * die dem Konstruktor übergeben wurde.
     *
     * @param mapObject  Die zu übertragende Map.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void transmit(Object mapObject) throws RemoteException {

        Map map = (Map) mapObject;
        relayStore.putAll(map, localStore);
    }
}

