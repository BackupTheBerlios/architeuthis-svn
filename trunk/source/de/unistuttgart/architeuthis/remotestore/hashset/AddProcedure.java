/*
 * file:        AddProcedure.java
 * created:     05.04.2005
 * last change: 18.04.2005 by Dietmar Lippold
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

import java.io.Serializable;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.TransmitProcedure;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Implementiert eine Methode, die beim RelayStore f�r ein Objekt die Methode
 * <CODE>add</CODE> aufruft.
 *
 * @author Dietmar Lippold
 */
public class AddProcedure implements TransmitProcedure {

    /**
     * �bertragt das an den <CODE>Transmitter</CODE> �bergebene Objekt zum
     * angegebenen zentralen <CODE>RelayHashSet</CODE>, indem es dort die
     * Methode <CODE>add</CODE> aufruft.
     *
     * @param object      Das zu �bertragende Objekt.
     * @param relayStore  Der RelayStore, zu dem das Objekt �bertragen werden
     *                    soll. Dabei handelt es sich um ein
     *                    <CODE>RelayHashSet</CODE>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void transmit(Object object, RemoteStore relayStore)
        throws RemoteException {

        ((RelayHashSet) relayStore).add((Serializable) object);
    }
}
