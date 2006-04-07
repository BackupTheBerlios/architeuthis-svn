/*
 * file:        AddAllProcedure.java
 * created:     05.04.2005
 * last change: 07.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.remotestore.hashset.impl;

import java.util.Collection;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.TransmitProcedure;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.hashset.interf.RelayHashSet;

/**
 * Implementiert eine Methode, die beim RelayStore für eine Collection die
 * Methode <CODE>addAll</CODE> aufruft.
 *
 * @author Dietmar Lippold
 */
public class AddAllProcedure implements TransmitProcedure {

    /**
     * Übertragt die an den <CODE>Transmitter</CODE> übergebene Collection zum
     * angegebenen zentralen <CODE>RelayHashSet</CODE>, indem es dort die
     * Methode <CODE>addAll</CODE> aufruft.
     *
     * @param collObject  Die zu übertragende Collection.
     * @param relayStore  Der RelayStore, zu dem das Objekt übertragen werden
     *                    soll. Dabei handelt es sich um ein
     *                    <CODE>RelayHashSet</CODE>.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void transmit(Object collObject, RemoteStore relayStore)
        throws RemoteException {

        Collection collection = (Collection) collObject;
        ((RelayHashSet) relayStore).addAll(collection);
    }
}

