/*
 * file:        ProblemManager.java
 * created:     29.06.2003
 * last change: 26.05.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
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

import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;

/**
 * Ermöglicht die Kommunikation zwischen Problem-übermittler und ComputeManager.
 * <p>
 * Das Compute-System kann zur Berechnunng eines neuen Problems angewiesen
 * werden. Dabei muss der nötige Quellcode lokalisiert werden.<br>
 * Nach erfolgreicher Berechnung wird das Ergebnis an den Problem-übermittler
 * zurückgegeben. <br>
 * Darüber hinaus ist es möglich, Statistiken über das Compute-System und dessen
 * Probleme abzufragen.
 *
 * @author Andreas Heydlauff, Jürgen Heit, Dietmar Lippold
 */
public interface ProblemManager extends Remote {

    /**
     * Port, unter der der ProblemManager hört.
     */
    public static final String PORT_NO = ComputeManager.PORT_NO;

    /**
     * Binding des ProblemManager an der RMI-Registry.
     */
    public static final String PROBLEMMANAGER_ID_STRING = "ProblemManager";

    /**
     * Teilt dem Compute-System die Position der Quelldateien für ein neues
     * Problem mit. Nötiger Quellcode wird von einem HTTP-Server geladen und
     * das {@link Problem} initialisiert.<p>
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * Lösung dem Problem zum Zusammenfügen übergeben.<br>
     *
     * @param transmitter  Problem-übermittler, der das Problem sendet und die
     *                     Lösung empfangen soll
     * @param url          Pfad zu den Quelldateien auf einem HTTP-Server
     * @param className    {@link Problem}-spezifischer Name, das die Berechnung
     *                     startet
     * @param problemParameters  Die formalen Parameter des vom Problem zu
     *                           startenden Konstruktors.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     * @throws ClassNotFoundException  falls die angegebene Klasse nicht
     *                                 auf dem HTTP-Server unter <code>url</code>
     *                                 gefunden wurde.
     * @throws ProblemComputeException  bei Berechnungsfehler.
     *
     * @see  Problem
     * @see  PartialProblem
     */
    public void loadProblem(ProblemTransmitter transmitter, URL url,
                            String className, Object[] problemParameters)
        throws RemoteException, ClassNotFoundException, ProblemComputeException;

    /**
     * Schickt dem Problem-Manager ein neues serialisierbares Problem.
     * Nötiger Quellcode wird durch RMI von einem HTTP-Server geladen.<p>
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * Lösung dem Problem zum Zusammenfügen übergeben.<br>
     *
     * @param transmitter  Problem-übermittler, der das Problem sendet und die
     *                     Lösung empfangen soll
     * @param problem      serialisierbares Problem, das verteilt berechnet werden soll.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     * @throws ProblemComputeException  bei Berechnungsfehler.
     *
     * @see  SerializableProblem
     * @see  PartialProblem
     */
    public void receiveProblem(ProblemTransmitter transmitter,
                               SerializableProblem problem)
        throws RemoteException, ProblemComputeException;

    /**
     * Liefert problemespezifische Statistik-Werte zurück.
     *
     * @param transmitter  Problem-Übermittler, der das Problem übertragen hat.
     *
     * @return  {@link ProblemStatistics} zu einem Problem.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     *
     * @see ProblemStatistics
     */
    public ProblemStatistics getProblemStatistics(ProblemTransmitter transmitter)
        throws RemoteException;

    /**
     * Liefert die allgemeine Statistik des Compute-Systems zurück.
     *
     * @return  {@link SystemStatistics} über das Compute-System
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen.
     *
     * @see SystemStatistics
     */
    public SystemStatistics getSystemStatistics()
        throws RemoteException;

    /**
     * Bricht die Berechnung eines Problems durch dessen Benutzer ab.
     *
     * @param transmitter  Problem-Übermittler, der das Problem übertragen hat.
     *
     * @throws RemoteException bei RMI-Verbingungsproblemen.
     */
    public void abortProblemByUser(ProblemTransmitter transmitter)
        throws RemoteException;
}
