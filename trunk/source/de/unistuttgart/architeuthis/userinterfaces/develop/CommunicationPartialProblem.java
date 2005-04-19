/*
 * file:        CommunicationPartialProblem.java
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
package de.unistuttgart.architeuthis.userinterfaces.develop;

import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;

/**
 * @author Michael Wohlfart
 *
 */
public interface CommunicationPartialProblem extends PartialProblem {
    /**
     * Startet die Berechnung des Teilproblems.
     *
     * @param store der Verteilte Speicher (RemoteStore)
     *
     * @return  berechnete Teillösung
     *
     * @throws ProblemComputeException  bei beliebigen Berechnungsproblemen
     * @throws RemoteException bei Problemen bei der Kommunikation mit dem RemoteStore
     */
    public PartialSolution compute(RemoteStore store) throws ProblemComputeException, RemoteException;

}
