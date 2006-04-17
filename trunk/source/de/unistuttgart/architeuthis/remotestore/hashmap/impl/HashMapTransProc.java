/*
 * file:        HashMapTransProc.java
 * created:     17.04.2006
 * last change: 17.04.2006 by Dietmar Lippold
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

import de.unistuttgart.architeuthis.remotestore.TransmitObject;
import de.unistuttgart.architeuthis.remotestore.TransmitProcedure;
import de.unistuttgart.architeuthis.remotestore.hashmap.interf.LocalHashMap;
import de.unistuttgart.architeuthis.remotestore.hashmap.interf.RelayHashMap;

/**
 * Implementiert eine Methode, die beim RelayStore für ein Objekt-Paar die
 * Methode <CODE>put</CODE> aufruft.
 *
 * @author Dietmar Lippold
 */
public class HashMapTransProc implements TransmitProcedure {

    /**
     * Lokaler RemotStore, von dem die zu übertragenden Daten stammen.
     */
    private LocalHashMap localStore;

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
    public HashMapTransProc(LocalHashMap localStore, RelayHashMap relayStore) {

        this.localStore = localStore;
        this.relayStore = relayStore;
    }

    /**
     * Übertragt das im übergebenen Objekt enthaltene Objekt zum zentralen
     * <CODE>RelayHashMap</CODE>, die im Konstruktor angegeben wurde.
     *
     * @param transObject  Das Objekt, das das zu übertragende Objekt enthält.
     *
     * @throws RemoteException           Bei einem RMI Problem.
     * @throws IllegalArgumentException  Wenn das übergebene Objekt keinen
     *                                   zulässigen Typs besitzt.
     */
    public void transmit(TransmitObject transObject) throws RemoteException {

        if (transObject instanceof PutObject) {

            MapEntry mapEntry = (MapEntry) transObject.storedObject();
            relayStore.put(mapEntry.getKey(), mapEntry.getValue(), localStore);

        } else if (transObject instanceof PutAllObject) {

            relayStore.putAll((Map) transObject.storedObject(), localStore);

        } else {

            throw new IllegalArgumentException("Besitzt keinen zulässiger Typ: "
                                               + transObject);

        }
    }
}

