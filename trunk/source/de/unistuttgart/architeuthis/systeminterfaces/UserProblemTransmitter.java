/*
 * file:        UserProblemTransmitter.java
 * created:     08.08.2003
 * last change: 26.05.2004 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.systeminterfaces;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;

/**
 * Dieses Interface ist die Schnittstelle zwischen Benutzer und ProblemManager.
 * Es dient zur �bermittlung der Daten eines Problems an ein ProblemManager.
 * Au�erdem ist es dem Benutzer m�glich eine Abschlussstatistik zu bekommen.
 *
 * @author Achim Linke, Andreas Heydlauff, Dietmar Lippold
 */
public interface UserProblemTransmitter {

    /**
     * �bermittelt die Daten eines Problems an ein ProblemManager.
     * Konkret wird dem ProblemManager ein Webserver, auf dem die Klassendateien
     * liegen, und eine Startklasse �bermittelt, so dass der
     * <code>ComputeManager</code> ein Objekt dieser Klasse instanzieren kann.
     * Die Fehlerbehandlung muss vom aufrufenden Programm �bernommen werden.
     *
     * @param webserver   URL, von dem die Class-Dateien geladen werden sollen.
     *                    Dabei ist das Root-Verzeichnis des Programms anzugeben,
     *                    worunter sich die gesamte Verzeichnisstruktur befindet,
     *                    die durch die Pakete vorgegeben ist.
     * @param classname   Zentrale Class-Datei, die gestartet werden soll.
     *                    Hier muss der komplette Name mit Paketen angegeben
     *                    werden.
     * @param problemParameters  Array mit den Parametern f�r den
     *                           Problem-Konstruktor.
     *
     * @return die L�sung f�r das �bergebene Problem.
     *
     * @throws MalformedURLException  URL der Registry oder des Webservers sind
     *                                falsch.
     * @throws NotBoundException  Verzeichnis wurde auf Registry nicht gefunden
     * @throws RemoteException  bei Kommunikationsproblemen �ber RMI
     * @throws ClassNotFoundException  Class-Datei konnte nicht vom Webserver
     *                                 geladen werden.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(URL webserver,  String classname,
                                        Object[] problemParameters)
        throws ClassNotFoundException, ProblemComputeException,
            MalformedURLException, RemoteException, NotBoundException;

    /**
     * �bermittelt ein Problem an ein ProblemManager.
     * Die Fehlerbehandlung muss vom aufrufenden Programm �bernommen werden.
     *
     * @param problem  Zentrale Problemklasse, die �bertragen wird.
     *
     * @return die L�sung f�r das �bergebene Problem.
     *
     * @throws MalformedURLException  URL der Registry ist falsch.
     * @throws NotBoundException  Verzeichnis wurde auf Registry nicht gefunden
     * @throws RemoteException  bei Kommunikationsproblemen �ber RMI
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public Serializable transmitProblem(SerializableProblem problem)
    throws RemoteException, NotBoundException, MalformedURLException, ProblemComputeException;

    /**
     * Bricht die Berechnung eines Problems ab.
     */
    public void abortProblem() throws RemoteException;

    /**
     * Liefert einen Schnappschu� der aktuelle System-Statistik.
     *
     * @return  <code>SystemStatistics</code>-Objekt, das die aktuellen
     *          Daten enth�lt.
     *
     * @throws RemoteException  Bei Verbindungsproblemen mit RMI.
     */
    public SystemStatistics getSystemStatistic() throws RemoteException;

    /**
     * Liefert einen Schnappschu� der aktuelle Problem-Statistik.
     *
     * @return  <code>ProblemStatistics</code>-Objekt, das die aktuellen
     *          Daten enth�lt, oder <code>null</code>, falls sich derzeit kein
     *          Problem, das von diesem Objekt �bergeben wurde, auf dem
     *          Compute-Server befindet.
     *
     * @throws RemoteException  Bei Verbindungsproblemen mit RMI.
     */
    public ProblemStatistics getProblemStatistic() throws RemoteException;

    /**
     * Gibt die abschlie�ende Statistik des von diesem ProblemTransmitter
     * �bermittelten Problems zur�ck.
     *
     * @return  die letzte Statistik des berechneten Problems, oder
     *          <code>null</code>, falls noch kein Problem berechnet wurde
     *          oder gerade eines berechnet wird.
     */
    public ProblemStatistics getFinalProblemStat();
}
