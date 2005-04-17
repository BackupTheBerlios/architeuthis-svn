/*
 * file:        HashStorePut.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 17.04.2005 von Dietmar Lippold
 * developers:  Michael Wohlfart michael.wohlfart@zsw-bw.de
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

import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.hashmap.UserRemoteHashMap;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.CommunicationPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Legt bei Ausfü+hrung der Methode <CODE>compute</CODE> ein value-Objekt
 * unter einem key-Objekt in einem <CODE>RemoteStore</CODE> ab.
 *
 * @author Michael Wohlfart
 */
public class HashStorePut implements CommunicationPartialProblem {

    /**
     * Key-Objekt, unter dem das value-Objekt im <CODE>RemoteStore</CODE>
     * abgelegt wird.
     */
    private String key;

    /**
     * Value-Object, das unter dem key-Objekt im <CODE>RemoteStore</CODE>
     * abgelegt wird.
     */
    private Object value;

    /**
     * Konstruktor.
     *
     * @param key    Das key-Objekt, zu dem das value-Objekt aus dem
     *               <CODE>RemoteStore</CODE> abgerufen werden soll.
     * @param value  Das value-Objekt, das unter dem key-Objekt im
     *               <CODE>RemoteStore</CODE> abgelegt wird.
     */
    public HashStorePut(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Legt das value-Objekt unter dem key-Objekt im übergebenen
     * <CODE>RemoteStore</CODE> ab.
     *
     * @param store  Der <CODE>RemoteStore</CODE>, in dem das value-Objekt
     *               unter dem key-Objekt abgelegt wird.
     *
     * @return  Der Wert <CODE>null</CODE>.
     *
     * @throws ProblemComputeException  Sollte nicht auftreten.
     * @throws RemoteException          Bei einem RMI Problem.
     */
    public PartialSolution compute(RemoteStore store)
        throws ProblemComputeException, RemoteException {

        if (store instanceof UserRemoteHashMap) {
            ((UserRemoteHashMap) store).put(key, value);
        } else {
            System.err.println("wrong remotestore parameter: " + store);
        }
        return null;
    }
}

