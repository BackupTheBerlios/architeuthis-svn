/*
 * file:        CommunicationPartialProblem.java
 * created:     08.02.2005
 * last change: 17.04.2005 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.userinterfaces.develop;

import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;

/**
 * Interface muß von einem Teilproblem implementiert werden, das mit anderen
 * Teilproblemen Daten austauschen will.
 *
 * @author Michael Wohlfart
 */
public interface CommunicationPartialProblem extends PartialProblem {

    /**
     * Startet die Berechnung des Teilproblems.<P>
     *
     * Wenn ein <CODE>RemoteStoreGenerator</CODE> mit dem Problem zusammen
     * übergeben wurde, wird dessen Methode <CODE>generateDistRemoteStore</CODE>
     * vom Operative aufgerufen. Wenn diese ein Objekt liefert, wird dieses
     * als Parameter <CODE>store</CODE> beim Aufruf dieser Methode übergeben.
     * Wenn <CODE>generateDistRemoteStore</CODE> den Wert <CODE>null</CODE>
     * geliefert hat, wird der Wert der Methode <CODE>generateCentralRemoteStore</CODE>
     * die auf dem Dispatcher aufgerufen wurde, übergeben. Es wird der Wert
     * <CODE>null</CODE> übergeben, wenn beide Methoden den Wert <CODE>null</CODE>
     * geliefert haben oder wenn kein <CODE>RemoteStoreGenerator</CODE> mit
     * dem Problem zusammen übergeben wurde.
     *
     * @param store  Der Verteilte Speicher (RemoteStore).
     *
     * @return  Die berechnete Teillösung.
     *
     * @throws ProblemComputeException  Bei beliebigen Fehlern bei der
     *                                  Berechnung.
     * @throws RemoteException          Bei Problemen bei der Kommunikation
     *                                  mit dem RemoteStore.
     */
    public PartialSolution compute(RemoteStore store) throws ProblemComputeException,
                                                             RemoteException;
}

