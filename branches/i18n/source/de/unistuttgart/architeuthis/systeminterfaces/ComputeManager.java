/*
 * file:        ComputeManager.java
 * last change: 26.05.2004,  von by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.systeminterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * RMI Remote-Interface, das vom Compute-Manager implementiert wird.
 *
 * @author J�rgen Heit
 */
public interface ComputeManager extends Remote {

    /**
     * Port-Nummer, auf der der ComputeManager h�rt.
     */
    public static final String PORT_NO = "1854";

    /**
     * Binding-Name bei der RMI-Registry (also der Name, unter der der
     * ComputeManager erreichbar ist).
     */
    public static final String COMPUTEMANAGER_ID_STRING = "ComputeManager";

    /**
     * Registrierung eines Operative am ComputeManager.
     *
     * @param  operative Referenz auf Operative
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     */
    public abstract void registerOperative(Operative operative)
        throws RemoteException;
    /**
     * Meldet einen Operative vom ComputeManager ab.
     *
     * @param  operative Referenz auf Operative
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     */
    public abstract void unregisterOperative(Operative operative)
        throws RemoteException;

    /**
     * Diese Remote-Methode wird von einem Operative aufgerufen um dem
     * ComputeManager eine berechnete Teill�sung zu �bermitteln.
     *
     * @param  parSol     Teill�sungsobjekt
     * @param  operative  Referenz auf den Operative der die Berechnung
     *                    durchgef�hrt hat
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     */
    public abstract void returnPartialSolution(
        PartialSolution parSol,
        Operative operative)
        throws RemoteException;
    
    /**
     * Ausnahmen werden �ber diese Methode an den Compute-Manager gemeldet.
     * 
     * @param operative         Referenz auf den Operative, auf dem der Fehler
     *                          auftrat.
     * @param exceptionCode     Integerwert, der die Ausnahme charakterisisert.
     * @param exceptionMessage  Fehlermeldung um die Ausnahme n�her zu beschreiben.
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     */
    public void reportException(
        Operative operative,
        int exceptionCode,
        String exceptionMessage)
        throws RemoteException;
}
