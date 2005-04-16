/*
 * file:        ProblemManager.java
 * created:     29.06.2003
 * last change: 16.04.2005 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *              Michael Wohlfart,  michael.wohlfart@zsw-bw.de
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

import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreGenException;
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
    String PORT_NO = ComputeManager.PORT_NO;  // public finals static sind hier unnötige Modifier

    /**
     * Binding des ProblemManager an der RMI-Registry.
     */
    String PROBLEMMANAGER_ID_STRING = "ProblemManager";

    /**
     * Teilt dem Compute-System die Position der Quelldateien für ein neues
     * Problem mit. Nötiger Quellcode wird von einem HTTP-Server geladen und
     * das {@link Problem} initialisiert.<p>
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * Lösung dem Problem zum Zusammenfügen übergeben.<br>
     *
     * @param transmitter        Problem-Übermittler, der das Problem sendet
     *                           und die Lösung empfangen soll.
     * @param url                Pfad zu den Quelldateien auf einem HTTP-Server.
     * @param className          {@link Problem}-spezifischer Name, das die
     *                           Berechnung startet.
     * @param problemParameters  Die formalen Parameter des vom Problem zu
     *                           startenden Konstruktors.
     * @param generator          Zum Erzeugen der RemoteStores benötigter
     *                           Generator oder <CODE>null</CODE>, falls keiner
     *                           verwendet wird.
     *
     * @throws RemoteException          Bei einem RMI-Verbindungsproblem.
     * @throws ClassNotFoundException   Falls die angegebene Klasse nicht
     *                                  auf dem HTTP-Server unter <code>url</code>
     *                                  gefunden wurde.
     * @throws ProblemComputeException  Bei einem Berechnungsfehler.
     * @throws RemoteStoreGenException  Der zentrale <CODE>RemoteStore</CODE>
     *                                  konnte nicht erzeugt werden.
     *
     * @see  Problem
     * @see  PartialProblem
     */
    public void loadProblem(ProblemTransmitter transmitter, URL url,
                            String className, Object[] problemParameters,
                            RemoteStoreGenerator generator)
        throws RemoteException, ClassNotFoundException,
               ProblemComputeException, RemoteStoreGenException;

    /**
     * Schickt dem Problem-Manager ein neues serialisierbares Problem und
     * ein RemoteStoreGenerator.<br>
     * Der RemoteStoreGenerator erzeugt evtl. benötigten verteilten
     * Speicher.<br>
     * Zur Berechnung werden vom Problem erzeugte {@link PartialProblem}
     * an Operatives verteilt und nach erfolgreicher Berechnung deren
     * Lösung dem Problem zum Zusammenfügen übergeben.<br>
     *
     * @param transmitter  Problem-übermittler, der das Problem sendet und die
     *                     Lösung empfangen soll
     * @param problem      serialisierbares Problem, das verteilt berechnet werden soll.
     * @param generator    RemoteStoreGenerator zum erzeugen des verteilten Speichers
     *
     * @throws RemoteException          Bei einem RMI-Verbindungsproblem.
     * @throws ProblemComputeException  Bei einem Berechnungsfehler.
     * @throws RemoteStoreGenException  Der zentrale <CODE>RemoteStore</CODE>
     *                                  konnte nicht erzeugt werden.
     *
     * @see  SerializableProblem
     * @see  PartialProblem
     */
    public void receiveProblem(ProblemTransmitter transmitter,
                               SerializableProblem problem,
                               RemoteStoreGenerator generator)
        throws RemoteException, ProblemComputeException, RemoteStoreGenException;

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

