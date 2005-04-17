/*
 * file:        HashStoreGet.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 17.04.2005 von Dietmar Lippold
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


package de.unistuttgart.architeuthis.testenvironment.hashstore;

import java.io.Serializable;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.hashmap.UserRemoteHashMap;
import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.CommunicationPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Ruft ein value-Objekt zu einem key-Objekt, das dem Konstruktor übergeben
 * wurde, bei Ausführung der Methode <CODE>compute</CODE> aus einem
 * <CODE>RemoteStore</CODE> ab und liefert es als Teillösung.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class HashStoreGet implements CommunicationPartialProblem {

    /**
     * Key-Objekt, zu dem das value-Objekt aus dem <CODE>RemoteStore</CODE>
     * geliefert wird.
     */
    private String key;

    /**
     * Konstruktor.
     *
     * @param key  Das key-Objekt, zu dem das value-Objekt aus dem
     *             <CODE>RemoteStore</CODE> abgerufen werden soll.
     */
    public HashStoreGet(String key) {
        this.key = key;
    }

    /**
     * Liefert als Teillösung das value-Objekt, das zum key-Objekt im
     * übergebenen <CODE>RemoteStore</CODE> gespeichert ist.
     *
     * @param store  Der <CODE>RemoteStore</CODE>, aus dem das value-Objekt
     *               abgerufen wird.
     *
     * @return  Das abgerufene value-Objekt.
     *
     * @throws ProblemComputeException  Sollte nicht auftreten.
     * @throws RemoteException          Bei einem RMI Problem.
     */
    public PartialSolution compute(RemoteStore store)
        throws ProblemComputeException, RemoteException {

        Serializable partialSolution = null;

        if (store instanceof UserRemoteHashMap) {
            partialSolution = (Serializable) ((UserRemoteHashMap) store).get(key);
        } else {
            System.err.println("wrong remotestore parameter: " + store);
        }
        return new ContainerPartialSolution(partialSolution);
    }
}

