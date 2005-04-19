/*
 * file:        ProblemTransmitterImpl.java
 * created:     08.07.2003
 * last change: 15.02.2005 by Michael Wohlfart
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


package de.unistuttgart.architeuthis.user;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.AccessException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemTransmitter;
import de.unistuttgart.architeuthis.systeminterfaces.UserProblemTransmitter;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;
import de.unistuttgart.architeuthis.misc.Miscellaneous;

/**
 * Benutzerschnittstelle zur Benutzung des Compute-Systems.
 * Mit diesem Programm werden Probleme (oder besser: die Position der
 * Problemklassen auf einem Webserver) zum ComputeManager übertragen. Eine
 * Instanz dieser Klasse kann zu jedem Zeitpunkt beim Compute-System maximal
 * ein Problem in Berechnung haben.
 *
 * @author Ralf Kible, Andreas Heydlauff, Dietmar Lippold
 */
public class ProblemTransmitterImpl extends UnicastRemoteObject
    implements ProblemTransmitter, UserProblemTransmitter {

    /**
     * generierte <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 4121128130447750709L;

    /**
     * Der ProblemManager, an den das letzte Problem zur Berechnung übermittelt
     * wurde.
     */
    private volatile ProblemManager problemManager = null;

    /**
     * Die Lösung des übermittelten Problems.
     */
    private Serializable solution = null;

    /**
     * Die Problem-Statistik des letzten berechneten Problems.
     */
    private volatile ProblemStatistics finalStat = null;

    /**
     * Wenn das Problem aufgrund einer Exception abgebrochen wurde, ist dies
     * die Fehlermeldung, ansonsten der Wert <code>null</code>.
     */
    private String errorMessage = null;

    /**
     * Gibt an, ob sich derzeit ein übergebenes Problem in Bearbeitung
     * befindet.
     */
    private volatile boolean processing = false;

    /**
     * Gibt an, ob zusätzliche Debug-Meldungen ausgegeben werden sollen.
     */
    private boolean debugMode = false;

    /**
     * Standard-Konstruktor, der wegen der Ableitung von
     * <code>UnicastRemoteObject</code> überschrieben werden muss.
     *
     * @throws RemoteException  Kommunikationsproblem über RMI.
     */
    public ProblemTransmitterImpl() throws RemoteException {
        super();
    }

    /**
     * Konstruktor, im dem eine Verbindung zum übergebenen Dispatcher
     * hergestellt wird.
     *
     * @param dispatcherHost  Name des Rechners des Dispatcher (auf dem auch
     *                        die Registry läuft). Wenn kein Port angegeben
     *                        ist, wird der Standard-Port verwendet.
     * @param debugMode       gibt an, ob debug-Ausgaben erfolgen sollen.
     *
     * @throws MalformedURLException  Die Angabe vom <code>dispatcher</code>
     *                                war kein zulässiger Name.
     * @throws RemoteException        Kommunikationsproblem über RMI.
     * @throws AccessException        Fehler beim Zugriff auf die Registry.
     *                                Vermutlich wurde eine falsche policy-Datei
     *                                benutzt.
     * @throws NotBoundException      Der Dispatcher war auf der Registry nicht
     *                                eingetragen.
     */
    public ProblemTransmitterImpl(String dispatcherHost, boolean debugMode)
        throws
            MalformedURLException,
            AccessException,  // ist Unterklasse von RemoteException
            RemoteException,
            NotBoundException {

        this.debugMode = debugMode;

        // Hier wird ein shutdownHook gesetzt. Dieser wird aufgerufen, wenn das
        // Objekt entfernt wird, z.B. weil das verwendende Programm beendet
        // wird.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    abortProblem();
                } catch (RemoteException ex) {
                }
            }
        });

        // Für RMI-Verbindungen muss ein Security-Manager aktiv sein:
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // Der Export des Objekts erfolgt bei jedem Aufruf von transmitProblem
        unexportObject(this, true);

        String binding = "//" + dispatcherHost;

        // Wenn im dispatcherHost kein Port angegeben ist, den Standard-Port
        // verwenden
        if (dispatcherHost.indexOf(":") == -1) {
            binding += ":" + ProblemManager.PORT_NO;
        } else if (dispatcherHost.indexOf(":") == dispatcherHost.length() - 1) {
            binding += ProblemManager.PORT_NO;
        }

        binding += "/" + ProblemManager.PROBLEMMANAGER_ID_STRING;

        problemManager = (ProblemManager) java.rmi.Naming.lookup(binding);
    }

    /**
     * Bricht die Berechnung des aktuellen Problems auf dem ComputeServer ab.
     * Wenn aktuell kein Problem berechnet wird, geschieht nichts.
     *
     * @throws RemoteException  Kommunikationsproblem über RMI.
     */
    public void abortProblem() throws RemoteException {
        if ((problemManager != null) && processing) {
            Miscellaneous.printDebugMessage(
                debugMode,
                "Debug: Breche Problem ab");
            problemManager.abortProblemByUser(this);
        }
    }

    /**
     * Übermittelt die Daten eines Problems an ein ProblemManager. Konkret
     * wird dem ProblemManager ein Class-URL, unter dem die Klassendateien
     * liegen, und eine Startklasse übermittelt, so dass der
     * <code>ComputeManager</code> ein Objekt dieser Klasse instanzieren kann.
     * Ist bereits ein Problem dieses Objekts in Berechnung, wartet der Aufruf,
     * bis das Problem fertig berechnet ist.<p>
     *
     * Die Fehlerbehandlung muss vom aufrufenden Programm übernommen werden.
     *
     * @param packageUrl  URL, von dem die Class-Dateien geladen werden sollen.
     *                    Dabei ist das Root-Verzeichnis des Programms
     *                    anzugeben, worunter sich die gesamte
     *                    Verzeichnisstruktur befindet, die durch die Pakete
     *                    vorgegeben ist.
     * @param classname   Zentrale Class-Datei, die gestartet werden soll.
     *                    Hier muss der komplette Name mit Paketen angegeben
     *                    werden.
     * @param problemParameters  Array mit allen Parametern, die einem
     *                           Konstruktor des Problems übergeben werden
     *                           sollen. Anstatt einem Array der Länge Null
     *                           kann auch der Wert <code>null</code> übergeben
     *                           werden.
     *
     * @return  Die Lösung des an den ComputeManager übermittelten Problems.
     *
     * @throws ClassNotFoundException   Class-Datei konnte nicht vom Webserver
     *                                  geladen werden.
     * @throws RemoteException          Kommunikationsproblem über RMI.
     * @throws ProblemComputeException  Ein Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public synchronized Serializable transmitProblem(
            URL packageUrl,
            String classname,
            Object[] problemParameters)
        throws
            ClassNotFoundException,
            RemoteException,
            ProblemComputeException {

        while (processing) {
            // Es befindet sich noch ein anderes Problem in Bearbeitung
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Es kann ein neues Problem übertragen werden.
        processing = true;
        solution = null;
        finalStat = null;
        errorMessage = null;

        // RMI-Aufrufe entgegennehmen
        exportObject(this);

        try {
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Versuche, Problem zu ProblemManager zu schicken");
            if  (problemParameters == null) {
                problemManager.loadProblem(this, packageUrl,
                        classname, new Object[0], null);
            } else {
                problemManager.loadProblem(this, packageUrl,
                        classname, problemParameters, null);
            }

            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Warte auf Lösung....");
            while ((solution == null) && (errorMessage == null)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        } finally {
            unexportObject(this, true);
            processing = false;

            // Eventuell wartende Aufrufe dieser Methode aktivieren.
            notifyAll();
        }

        if (solution != null) {
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Lösung empfangen, wird zurückgegeben");
        }

        if (errorMessage != null) {
            String errorMessageCopy = errorMessage;
            throw new ProblemComputeException(errorMessageCopy);
        }

        return solution;
    }

    /**
     * Übermittelt ein serialisierbares Problem an ein ProblemManager.
     *
     * Die Fehlerbehandlung muss vom aufrufenden Programm übernommen werden.
     *
     * @param problem  Zentrale Problemklasse, die übertragen wird.
     *
     * @return  Die Lösung für das übergebene Problem.
     *
     * @throws RemoteException          Kommunikationsproblem über RMI.
     * @throws ProblemComputeException  Ein Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public synchronized Serializable transmitProblem(
            SerializableProblem problem)
        throws
            RemoteException,
            ProblemComputeException {
        return transmitProblem(problem, null);
    }


    /**
     * Übermittelt ein serialisierbares Problem und einen RemoteSToreGenerator
     * an ein ProblemManager.
     * Ist bereits ein Problem dieses Objekts in Berechnung, wartet der Aufruf,
     * bis das Problem fertig berechnet ist.<p>
     *
     * Die Fehlerbehandlung muss vom aufrufenden Programm übernommen werden.
     *
     * @param problem  Zentrale Problemklasse, die übertragen wird.
     *
     * @param generator RemoteStoreGenerator der verwendet werden soll 
     *                  oder null falls kein RemoteStoreGenerator benötigt wird
     *
     * @return  Die Lösung für das übergebene Problem.
     *
     * @throws RemoteException          Kommunikationsproblem über RMI.
     * @throws ProblemComputeException  Ein Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    public synchronized Serializable transmitProblem(
            SerializableProblem problem,
            RemoteStoreGenerator generator)
        throws
            RemoteException,
            ProblemComputeException {

        while (processing) {
            // Es befindet sich noch ein anderes Problem in Bearbeitung
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Es kann ein neues Problem übertragen werden.
        processing = true;
        solution = null;
        finalStat = null;
        errorMessage = null;

        // RMI-Aufrufe entgegennehmen
        exportObject(this);

        try {
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Versuche, Problem zu ProblemManager zu schicken");
            problemManager.receiveProblem(this, problem, generator);

            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Warte auf Lösung....");
            while ((solution == null) && (errorMessage == null)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        } finally {
            unexportObject(this, true);
            processing = false;

            // Eventuell wartende Aufrufe dieser Methode aktivieren.
            notifyAll();
        }

        if (solution != null) {
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Lösung empfangen, wird zurückgegeben");
        }

        if (errorMessage != null) {
            String errorMessageCopy = errorMessage;
            throw new ProblemComputeException(errorMessageCopy);
        }

        return solution;
    }

    /**
     * Bearbeitet die Nachrichten, die vom ComputeManager übermittelt werden.
     *
     * @param messageID  int, das die Nachricht charakterisiert, wobei die
     *                   verschiedenen Nachrichten als Konstanten in
     *                   {@link ExceptionCodes} definiert sind.
     * @param message    Nachrichtenstring
     *
     * @throws RemoteException  Kommunikationsproblem über RMI.
     */
    public synchronized void fetchMessage(int messageID, String message)
        throws RemoteException {

        switch (messageID) {
        case ExceptionCodes.PARTIALPROBLEM_CREATE_EXCEPTION :
            errorMessage = "Beim Erzeugen eines Teilproblems trat"
                + " eine Ausnahme auf";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.PARTIALSOLUTION_COLLECT_EXCEPTION :
            errorMessage = "Beim Entgegennehmen einer Teillösung trat"
                + " eine Ausnahme auf";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.SOLUTION_CREATE_EXCEPTION :
            errorMessage = "Beim Erzeugen der Lösung trat eine"
                + " Ausnahme auf";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.PARTIALPROBLEM_SEND_EXCEPTION :
            errorMessage = "Ein Teilproblem konnte nicht gesendet"
                + " werden";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.PARTIALSOLUTION_SEND_EXCEPTION :
            errorMessage = "Eine Teillösung konnte nicht gesendet"
                + " werden";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.SOLUTION_SEND_EXCEPTION :
            errorMessage = "Die Gesamtlösung konnte nicht gesendet"
                + " werden";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.PARTIALPROBLEM_COMPUTE_EXCEPTION :
            errorMessage = "Teilproblem meldet eine Ausnahme bei der"
                + " Berechnung";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.PARTIALPROBLEM_ERROR :
            errorMessage = "Bei der Berechnung eines Teilproblems trat"
                + " ein unerwarteteter Fehler auf";
            if (message != null) {
                errorMessage += ": " + message;
            } else {
                errorMessage += ".";
            }
            notifyAll();
            break;
        case ExceptionCodes.PROBLEM_INCORRECT_ERROR :
            errorMessage = "Problem liefert einerseits keine neuen"
                + " Teilprobleme und andererseits keine"
                + " Gesamtlösung.";
            notifyAll();
            break;
        case ExceptionCodes.DISPATCHER_SHUTDOWN :
            errorMessage = "Der Dispatcher wurde beendet!";
            notifyAll();
            break;
        case ExceptionCodes.USER_ABORT_PROBLEM :
            errorMessage = "Abbruch durch den Benutzer.";
            notifyAll();
            break;
        case ExceptionCodes.NO_OPERATIVES_REGISTERED :
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Keine Operatives mehr angemeldet!");
            break;
        case ExceptionCodes.NEW_OPERATIVES_REGISTERED :
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "Neuer Operative hat sich angemeldet!");
            break;
        default :
            System.err.println("Unbekannte Nachricht vom Dispatcher bekommen");
            if (message != null) {
                System.err.println(": " + message);
            } else {
                System.err.println(".");
            }
            break;
        }
    }

    /**
     * Diese Remote-Methode wird vom ProblemManager aufgerufen, um die Lösung
     * an den Problemübermittler zu schicken, damit sie für den Benutzer
     * verfügbar ist.
     *
     * @param sol   Lösung des Gesamtproblems
     * @param stat  Die letzte Statistik des Problems
     *
     * @throws RemoteException  Kommunikationsproblem über RMI.
     */
    public synchronized void fetchSolution(Serializable sol,
            ProblemStatistics stat)
        throws RemoteException {

        solution = sol;
        finalStat = stat;
        notifyAll();
        Miscellaneous.printDebugMessage(
                debugMode,
                "Debug: Loesung wurde vom Dispatcher uebermittelt!");
    }

    /**
     * Liefert einen Schnappschuß der aktuelle System-Statistik.
     *
     * @return  <code>SystemStatistics</code>-Objekt, das die aktuellen
     *          Daten enthält.
     *
     * @throws RemoteException  Bei Verbindungsproblemen mit RMI.
     */
    public synchronized SystemStatistics getSystemStatistic()
        throws RemoteException {

        return problemManager.getSystemStatistics();
    }

    /**
     * Liefert einen Schnappschuß der aktuelle Problem-Statistik.
     *
     * @return  <code>ProblemStatistics</code>-Objekt, das die aktuellen
     *          Daten enthält, oder <code>null</code>, falls sich derzeit kein
     *          Problem, das von diesem Objekt übergeben wurde, auf dem
     *          Compute-Server befindet.
     *
     * @throws RemoteException  Bei Verbindungsproblemen mit RMI.
     */
    public synchronized ProblemStatistics getProblemStatistic()
        throws RemoteException {

        if (!processing) {
            return null;
        } else {
            return problemManager.getProblemStatistics(this);
        }
    }

    /**
     * Gibt die abschließende Statistik des von diesem ProblemTransmitter
     * übermittelten Problems zurück.
     *
     * @return  die letzte Statistik des berechneten Problems, oder
     *          <code>null</code>, falls noch kein Problem berechnet wurde
     *          oder gerade eines berechnet wird.
     */
    public ProblemStatistics getFinalProblemStat() {
        return finalStat;
    }
}
