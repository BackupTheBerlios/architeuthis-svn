/*
 * filename:    OperativeImpl.java
 * created:     <???>
 * last change: 18.01.2005 by Michael Wohlfart
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *              Michael Wohlfart,  michael.wohlfart@zsw-bw.de
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


package de.unistuttgart.architeuthis.operative;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import de.unistuttgart.architeuthis.systeminterfaces.ComputeManager;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.systeminterfaces.Operative;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.misc.Miscellaneous;
import de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi;
import de.unistuttgart.architeuthis.misc.commandline.Option;
import de.unistuttgart.architeuthis.misc.commandline.ParameterParser;
import de.unistuttgart.architeuthis.misc.commandline.ParameterParserException;

/**
 * Implementierung die Operative Remote-Anwendung und nimmt die Kommunikation
 * mit dem Dispatcher vor. Die Klasse erzeugt Instanzen von
 * <code>OperativeComputing</code> für die Durchführung der Berechnung, wobei
 * ein erzeugtes Thread-Objekt wiederverwendet wird, wenn der Thread nicht
 * abgebrochen werden mußte.
 *
 * @author Jürgen Heit, Ralf Kible, Dietmar Lippold
 */
public class OperativeImpl extends UnicastRemoteObject implements Operative {

    /**
     * generierte SerialVersionUID, muss geändert werden, sobald
     * strukurelle Änderungen an dieser KLasse durchgeführt worden sind
     */
    private static final long serialVersionUID = 3257569516132447543L;

    /**
     * Ist die Anzahl der Versuche, Verbindung mit dem ComputeManager
     * herzustellen, um eine Lösung zurückzugeben.
     */
    private static final long CONNECT_RETRIES = 3;

    /**
    * Gibt die Anzahl der Millisekunden an, die gewartet wird, falls aufgrund
    * einer {@link RemoteException} nochmals versucht werden muss eine
    * Teillösung zu übermitteln.
    */
    private static final long SEND_TIMEOUT = 3000; // Millisekunden;

    /**
     * Dieser <code>Thread</code> berechnet die Lösung des Teilproblems im
     * Hintergrund.
     */
    private OperativeComputing backgroundComputation = null;

    /**
     * Der <code>ComputeManager</code>, mit dem der Operative gerade verbunden
     * ist.
     */
    private ComputeManager computeManager = null;

    /**
     * Dieses Flag schaltet zusätzliche Debug-Meldungen ein.
     */
    private boolean debugMode = true;

    /**
     * Dieser Konstruktor sollte nicht benutzt werden, muss aber wegen
     * der Ableitung von <code>UnicastRemoteObject</code> überschrieben
     * werden.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen
     */
    private OperativeImpl() throws RemoteException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }

    /**
     * Zu benutzender Konstruktor, der sofort Verbindung mit dem angegebenen
     * <code>ComputeManager</code> herstellt und den Debug-Modus setzt. Auch
     * dieser Konstruktor ist private, da er nicht aus anderen Klassen heraus
     * aufgerufen werden muss.
     *
     * @param computeManager      Der ComputeManagers in der
     *                            RMI-Registry.
     * @param debug               Schaltet zusätzliche debug-Meldungen ein,
     *                            falls <code>true</code>
     * @throws MalformedURLException  Die Angabe vom <code>compManager</code>
     *                                war kein zulässiger Name.
     * @throws RemoteException        Kommunikationsproblem über RMI.
     * @throws NotBoundException      Der Dispatcher war auf der Registry nicht
     *                                eingetragen.
     */
    private OperativeImpl(ComputeManager computeManager, boolean debug)
        throws MalformedURLException,
               RemoteException,
               NotBoundException {

        this.computeManager = computeManager;


        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        debugMode = debug;
    }


    /**
     * Startet den Berechnungs-Thread, der im Hintergrund läuft. Zur
     * Berechnung eines tatsächlichen PartialProblmes muss noch die
     * fetchPartialProblemMethode aufgerufen werden.<BR>
     * <BR>
     * Diese Methode ist protected für den Zugriff durch JUnit-Tests
     *
     */
    protected synchronized void startComputation() {

        // Hier wird ein shutdownHook gesetzt. Dieser wird aufgerufen, wenn vom
        // System ein term-Signal kommt (also beim Beenden oder bei Strg+c).
        // Dann wird kurz aufgeräumt.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });


        backgroundComputation = new OperativeComputing(this, this.debugMode);

    }

    /**
     * Diese Methode beendet den Operative lokal, das heißt die
     * Hintergrundberechnung wird gestoppt und er wird vom lokalen RMI-Server
     * abgemeldet. Es wird keine Verbindung zum ComputeManager mehr
     * hergestellt. Der Operative muß dort also auf andere Weise aus der
     * Verwaltung entfernt werden.<P>
     *
     * Die Methode kann außerdem vom ComputeManager via RMI aufgerufen werden,
     * um den Operative zu beenden, falls der ComputeManager beendet wird.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen
     */
    public synchronized void doExit() throws RemoteException {
        if (backgroundComputation != null) {
            // OperativeComputing-Thread beenden
            Miscellaneous.printDebugMessage(
                debugMode,
                "Debug: Berechnung wird gestoppt");
            backgroundComputation.stop();
            backgroundComputation = null;

            // Vom RMI-Server abmelden
            unexportObject(this, true);
        }
        computeManager = null;

        Miscellaneous.printDebugMessage(debugMode, "Debug: Beendet!");
    }

    /**
     * Diese Methode beendet den Operative vollständig, das heißt er wird vom
     * ComputeManager abgemeldet (falls er angemeldet war), die
     * Hintergrundberechnung wird gestoppt und er wird vom lokalen RMI-Server
     * abgemeldet.
     */
    private synchronized void shutdown() {
        // Vom Dispatcher abmelden
        try {
            if (computeManager != null) {
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Versuche, Verbindung zu trennen.");
                computeManager.unregisterOperative(this);
                computeManager = null;
            }
        } catch (RemoteException e1) {
            //Vermutlich nicht angemeldet gewesen
        }

        // Die Hintergrundberechnung beenden und vom RMI-Server abmelden
        try {
            doExit();
        } catch (RemoteException e2) {
            // RemoteException kann nicht auftreten.
        }
    }

    /**
     * Dummy-Methode, die vom <code>ComputeManager</code> aufgerufen wird, um
     * zu testen, ob der Operative noch reagiert. Falls der Operative nicht mehr
     * erreichbar ist, wird auf dem Server eine <code>RemoteException</code>
     * ausgelößt.
     *
     * @throws RemoteException bei RMI-Verbindungsproblemen
     *
     * @return <code>true</code>, falls der Operative erreichbar ist, sonst
     *         ergibt sich ein Verbindungsfehler.
     */
    public boolean isReachable() throws RemoteException {
        return true;
    }

    /**
     * Wird vom <code>ComputeManager</code> aufgerufen, um dem Operative ein
     * neues Teilproblem zur Berechnung zuzuweisen.
     *
     * @param parProb  Neues Teilproblem für den Operative.
     *
     * @throws RemoteException          bei RMI-Verbindungsproblemen
     * @throws ProblemComputeException  wenn bereits ein Teilproblem berechnet
     *                                  wird
     */
    public void fetchPartialProblem(PartialProblem parProb)
        throws RemoteException, ProblemComputeException {

        Miscellaneous.printDebugMessage(
            debugMode,
            "\nDebug: OperativeImpl hat Aufgabe vom ComputeManager empfangen.");
        backgroundComputation.fetchPartialProblem(parProb);
    }

    /**
     * Übrmittelt dem Dispatcher eine Fehlermeldung. Wenn dies nicht möglich
     * ist, wird der Operative beendet.
     *
     * @param exceptionCode     Integerwert, der die Ausnahme charakterisisert.
     * @param exceptionMessage  Fehlermeldung, die die Ausnahme näher beschreibt.
     */
    synchronized void reportException(int exceptionCode,
                                      String exceptionMessage) {
        try {
            computeManager.reportException(
                this,
                exceptionCode,
                exceptionMessage);
        } catch (RemoteException e) {
            // Dispatcher ist nicht erreichbar, Operative beenden
            shutdown();
        }
    }

    /**
     * Gibt eine berechnete Teillösung dem ComputeManager zurück. Damit wird
     * dem ComputeManager implizit mittgeteilt, dass der Operative nun ein
     * neues Teilproblem übernehmen kann.
     *
     * @param parSol  Teillösung, die dem ComputeManager übermittelt werden
     *                soll
     */
    synchronized void returnPartialSolution(PartialSolution parSol) {
        long versuche = CONNECT_RETRIES;
        boolean transmitted = false;
        String exceptionMessage = null;

        // Den oder die ClassLoader löschen
        CacheFlushingRMIClSpi.flushClassLoaders();

        // Mehrmals versuchen, die Teillösung zu senden
        while ((versuche > 0) && (!transmitted)) {
            Miscellaneous.printDebugMessage(
                debugMode,
                "Debug: Versuche, Teilergebnis zurückzugeben");
            try {
                computeManager.returnPartialSolution(parSol, this);
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Teilergebnis zurückgegeben");
                transmitted = true;
            } catch (RemoteException e) {
                exceptionMessage = e.getMessage();
                try {
                    Miscellaneous.printDebugMessage(
                        debugMode,
                        "Debug: Versuch fehlgeschlagen");
                    Thread.sleep(SEND_TIMEOUT);
                } catch (InterruptedException e1) {
                    // uninteressant
                }
            }
            versuche--;
        }
        if (!transmitted) {
            reportException(ExceptionCodes.PARTIALSOLUTION_SEND_EXCEPTION,
                            exceptionMessage);
        }
    }

    /**
     * Bricht die momentan ausgeführte Hintergrundberechnung ab und schafft
     * die Voraussetzung für den Start einer neuen Hintergrundberechnung.
     * Falls gerade keine Hintergrundberechnung durchgeführt wird, so
     * geschieht nichts.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen
     */
    public synchronized void stopComputation() throws RemoteException {
        Miscellaneous.printDebugMessage(
            debugMode,
            "Debug: Berechnung wird gestoppt");
        if (backgroundComputation.isComputing()) {
            backgroundComputation.stop();
            backgroundComputation = null;
//            Der nachfolgende Aufruf des GC führt beim Sun JDK 1.4 bis
//            Version 1.4.2_04-b05 häufiger zum Absturz.
//            System.gc();
            backgroundComputation = new OperativeComputing(this, debugMode);
        }
    }

    /**
     * Startet den Operative. Als Kommandozeilenargument muss die Adresse
     * und das Verzeichnis der RMI-Registry angegeben werden, an der der
     * <code>ComputeManager</code> angemeldet ist. Als weiteres
     * Kommandozeilenargument kann der Debug-Modus mittels
     * <code>--debug</code> angeschaltet werden.
     *
     * @param args  Die obligatorischen Kommandozeilenargumente
     */
    public static void main(String[] args) {


        ParameterParser parser = new ParameterParser();

        // debug option
        Option debug1 = new Option("d");
        debug1.setPrefix("-");
        debug1.setOptional(true);
        debug1.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);

        Option debug2 = new Option("debug");
        debug2.setPrefix("--");
        debug2.setOptional(true);
        debug2.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);

        parser.setFreeParameterPosition(ParameterParser.START);
        parser.setFreeParameterName("registry:port");
        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        parser.addOption(debug1);
        parser.addOption(debug2);

        StringBuffer binding = new StringBuffer();
        boolean debug = false;

        try {
            parser.parseAll(args);


            // ist debuging aktiviert?
            debug = (parser.isEnabled(debug1) || parser.isEnabled(debug2));

            binding.append(parser.getFreeParameter());

            // ist ein Port angegeben ?
            if (binding.indexOf(":") == -1) {
                // falls nicht: default Port verwenden:
                binding.append(":");
                binding.append(ComputeManager.PORT_NO);
            }

            // noch id String anhängen und Slashes dazubasteln:
            binding.insert(0, "//");
            binding.append("/");
            binding.append(ComputeManager.COMPUTEMANAGER_ID_STRING);


            ComputeManager computeManager =
                (ComputeManager) java.rmi.Naming.lookup(binding.toString());

            // Aufruf des privaten Konstruktors mit ComputeManager:
            OperativeImpl operative = new OperativeImpl(computeManager, debug);



            // erst wenn das funktioniert hat, computing starten
            operative.startComputation();

            // Wenn das funktioniert hat, dann anmelden
            Miscellaneous.printDebugMessage(
                debug,
                "Debug: Melde Operative an!");

            computeManager.registerOperative(operative);

            Miscellaneous.printDebugMessage(debug, "Debug: Gestartet!");


        } catch (ParameterParserException ex) {
            // usage ausgeben:
            System.err.println(parser);
            // Stacktrace (nicht unbedingt notwendig)
            ex.printStackTrace();

        } catch (java.rmi.StubNotFoundException e) {
            System.out.println(
                "\n\n Fehler! Die Stubs wurden vermutlich "
                    + "nicht generiert!\n");
            System.exit(1);
        } catch (Exception e) {
            System.out.println(
                "Fehler! Verbindung mit ComputeManager konnte "
                    + "nicht hergestellt werden:\n"
                    + e.getMessage()
                    + "\n"
                    + "Eventuell ist die Adresse falsch eingegeben worden:\n"
                    + args[0]);
            if (debug) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
