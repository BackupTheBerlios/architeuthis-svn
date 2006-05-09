/*
 * file:        ProblemTransmitter.java
 * created:     <???>
 * last change: 26.05.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;

/**
 * Remote-Interface zur Kommunikation des Dispatchers mit dem Problem-übermittler.
 *
 * @author Ralf Kible
 *
 */
public interface ProblemTransmitter extends Remote {

    /**
     * Wird vom ComputeManager aufgerufen, um die Gesamtlösung des fertig berechneten
     * Problems auf den Rechner des Benutzers zu übertragen.
     *
     * @param solution  Gesamtlösung des vom ProblemManager berechneten Problems
     *                  als <code>Serializable</code>
     * @param stat  Die abschließende Statistik des Problems, zu dem die Lösung
     *              zurückgegeben wird.
     * @throws RemoteException  bei RMI Verbindungsproblemen
     */
    public void fetchSolution(Serializable solution, ProblemStatistics stat)
        throws RemoteException;

    /**
     * Dient dazu, dass das ProblemManager dem Benutzer Nachrichten übermitteln
     * kann. Dies sind primär Fehlermeldungen, z.B. falls keine Operatives mehr
     * am ComputeManager angemeldet sind, so dass die Berechnung nicht mehr
     * vorangeht.
     *
     * @param messageID  int, das die Nachricht charakterisiert, wobei die
     *                   verschiedenen Nachrichten als Konstanten in
     *                   {@link ExceptionCodes} definiert sind.
     * @param message    Nachrichtenstring
     * @throws RemoteException  bei RMI Verbindungsproblemen
     */
    public void fetchMessage(int messageID, String message) throws RemoteException;

}
