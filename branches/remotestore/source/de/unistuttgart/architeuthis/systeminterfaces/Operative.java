/*
 * file:        Operative.java
 * created:     <???>
 * last change: 15.02.2005 by Michael Wohlfart
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *              Michael Wohlfart   michael.wohlfart@zsw-bw.de
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
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.systeminterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;

/**
 * RMI Remote-Interface des Operative
 *
 * @author Jürgen Heit
 */
public interface Operative
    extends Remote {

    /**
     * Dummy-Prozedur, deren Aufruf einen Fehler ergeben würde, falls die RMI-
     * Verbindung zusammenbricht.
     *
     * @return  <code>true</code> Falls erreichbar, sonst nicht näher
     *          spezifizierbar.
     *
     * @throws RemoteException  bei Kommunikationsproblemen über RMI
     */
    public boolean isReachable() throws RemoteException;

    /**
     * Wird vom ComputeManager aufgerufen um einem untätigen Operative ein
     * neues Teilproblem zuzuweisen.
     *
     * @param parProb  vom Operative zu berechnendes Teilproblem
     * 
     * @param remoteStore zentraler RemoteStore oder null, falls keiner
     *                    verwendet wird
     * 
     * @param gernerator zum Erzeugen des dezentralen RemoteStores benötigter
     *                   RemoteStoreGenerator oder null falls keiner verwendet wird
     *
     * @throws RemoteException  bei Kommunikationsproblemen über RMI
     * @throws ProblemComputeException  falls ein Berechnungsfehler auftritt
     */
    public void fetchPartialProblem(PartialProblem parProb,
            RemoteStore remStor,
            RemoteStoreGenerator generator) throws
        RemoteException, ProblemComputeException;

    /**
     * Wird vom ComputeManager aufgerufen, um die aktuelle Berechnung eines
     * Teilproblems abzubrechen. Dies sollte z.B. dann geschehen, wenn ein
     * Teilproblem bereits von einem anderen Operative berechnet wurde.
     *
     * @throws RemoteException bei Kommunikationsproblemen über RMI
     */
    public void stopComputation() throws RemoteException;


    /**
     * Wird vom ComputeManager aufgerufen, um den Operative zu beenden, falls
     * der ComputeManager beendet wird.
     *
     * @throws RemoteException  bei Kommunikationsproblemen über RMI
     */
    public void doExit() throws RemoteException;

}
