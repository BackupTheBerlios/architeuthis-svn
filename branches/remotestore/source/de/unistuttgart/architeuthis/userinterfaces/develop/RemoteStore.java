/*
 * file:        RemoteStore.java
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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface für ein Speicherobjekt, das den verteilten Speicher für
 * Architeurthis implementiert. Neben den hier vorgegebenen Methoden für die
 * Synchronisation der einzelnen Speicherobjekte untereinander müssen in
 * einer konkreten Implementierung noch weitere Methoden für den Zugriff auf
 * den Speicher implementiert werden.
 *
 * @author  Michael Wohlfart, Dietmar Lippold
 */
public interface RemoteStore extends Remote  {

    /**
     * Anmelden eines Speicherobjekts.
     *
     * @param remoteStore  Das anzumendende Speicherobjekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void registerRemoteStore(RemoteStore remoteStore) throws RemoteException;

    /**
     * Abmelden eines Speicherobjekts.
     *
     * @param remoteStore  Das abzumendende Speicherobjekt.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void unregisterRemoteStore(RemoteStore remoteStore) throws RemoteException;

    /**
     * Beendet dieses RemoteStore und meldet ihn insbesondere als RMI-Dienst
     * ab.
     *
     * @throws RemoteException  Bei einem RMI Problem.
     */
    public void terminate() throws RemoteException;
}

