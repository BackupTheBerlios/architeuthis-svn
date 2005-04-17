/*
 * file:        Operative.java
 * created:     <???>
 * last change: 17.04.2005 by Dietmar Lippold
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

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreGenException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;

/**
 * RMI Remote-Interface des Operative.
 *
 * @author Jürgen Heit
 */
public interface Operative extends Remote {

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
     * @param parProb      Vom Operative zu berechnendes Teilproblem
     * @param remoteStore  Zentraler RemoteStore oder <CODE>null</CODE>, falls
     *                     keiner verwendet wird
     * @param generator    Zum Erzeugen der RemoteStores benötigter
     *                     Generator oder <CODE>null</CODE>, falls keiner
     *                     verwendet wird.
     *
     * @throws RemoteException          Bei Kommunikationsproblemen über RMI.
     * @throws ProblemComputeException  Falls ein Berechnungsfehler auftritt.
     * @throws RemoteStoreGenException  Der lokale <CODE>RemoteStore</CODE>
     *                                  konnte nicht erzeugt werden.
     * @throws RemoteStoreException     Die gegenseitige Anmeldung von lokalem
     *                                  und zentralem <CODE>RemoteStore</CODE>
     *                                  war nicht möglich.
     */
    public void fetchPartialProblem(PartialProblem parProb,
                                    RemoteStore remoteStore,
                                    RemoteStoreGenerator generator)
        throws RemoteException, ProblemComputeException,
               RemoteStoreGenException, RemoteStoreException;

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

