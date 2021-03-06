/*
 * filename:    OperativeImpl.java
 * created:     <???>
 * last change: 26.09.2004 by Dietmar Lippold
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

/**
 * Implementierung die Operative Remote-Anwendung und nimmt die Kommunikation
 * mit dem Dispatcher vor. Die Klasse erzeugt Instanzen von
 * <code>OperativeComputing</code> f�r die Durchf�hrung der Berechnung, wobei
 * ein erzeugtes Thread-Objekt wiederverwendet wird, wenn der Thread nicht
 * abgebrochen werden mu�te.
 *
 * @author J�rgen Heit, Ralf Kible, Dietmar Lippold
 */
public class OperativeImpl extends UnicastRemoteObject implements Operative {

    /**
     * Ist die Anzahl der Versuche, Verbindung mit dem ComputeManager
     * herzustellen, um eine L�sung zur�ckzugeben.
     */
    private static final long CONNECT_RETRIES = 3;

    /**
    * Gibt die Anzahl der Millisekunden an, die gewartet wird, falls aufgrund
    * einer {@link RemoteException} nochmals versucht werden muss eine
    * Teill�sung zu �bermitteln.
    */
    private static final long SEND_TIMEOUT = 3000; // Millisekunden;

    /**
     * Dieser <code>Thread</code> berechnet die L�sung des Teilproblems im
     * Hintergrund.
     */
    private OperativeComputing backgroundComputation = null;

    /**
     * Der <code>ComputeManager</code>, mit dem der Operative gerade verbunden
     * ist.
     */
    private ComputeManager computeManager = null;

    /**
     * Dieses Flag schaltet zus�tzliche Debug-Meldungen ein.
     */
    private boolean debugMode = true;

    /**
     * Dieser Konstruktor sollte nicht benutzt werden, muss aber wegen
     * der Ableitung von <code>UnicastRemoteObject</code> �berschrieben
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
     * @param bindingCompManager  String, der die Adresse und das Verzeichnis
     *                            des Eintrags des ComputeManagers in der
     *                            RMI-Registry beinhaltet.
     * @param debug               Schaltet zus�tzliche debug-Meldungen ein,
     *                            falls <code>true</code>
     * @throws MalformedURLException  Die Angabe vom <code>compManager</code>
     *                                war kein zul�ssiger Name.
     * @throws AccessException        Fehler beim Zugriff auf die Registry.
     *                                Vermutlich wurde eine falsche policy-Datei
     *                                benutzt.
     * @throws RemoteException        Kommunikationsproblem �ber RMI.
     * @throws NotBoundException      Der Dispatcher war auf der Registry nicht
     *                                eingetragen.
     */
    private OperativeImpl(String bindingCompManager, boolean debug)
        throws MalformedURLException,
               AccessException,
               RemoteException,
               NotBoundException {

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        debugMode = debug;

        // Den Thread zur Berechnung der Teilprobleme erzeugen
        backgroundComputation = new OperativeComputing(this, debugMode);

        // Hier wird ein shutdownHook gesetzt. Dieser wird aufgerufen, wenn vom
        // System ein term-Signal kommt (also beim Beenden oder bei Strg+c).
        // Dann wird kurz aufger�umt.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });

        // Schlage den ComputeManager in der Registry nach
        Miscellaneous.printDebugMessage(
            debugMode,
            "Debug: Versuche, Verbindung herzustellen");
        computeManager =
            (ComputeManager) java.rmi.Naming.lookup(bindingCompManager);

        // Wenn das funktioniert hat, dann anmelden
        Miscellaneous.printDebugMessage(
            debugMode,
            "Debug: Melde Operative an!");
        computeManager.registerOperative(this);

        Miscellaneous.printDebugMessage(debugMode, "Debug: Gestartet!");
    }

    /**
     * Diese Methode beendet den Operative lokal, das hei�t die
     * Hintergrundberechnung wird gestoppt und er wird vom lokalen RMI-Server
     * abgemeldet. Es wird keine Verbindung zum ComputeManager mehr
     * hergestellt. Der Operative mu� dort also auf andere Weise aus der
     * Verwaltung entfernt werden.<P>
     *
     * Die Methode kann au�erdem vom ComputeManager via RMI aufgerufen werden,
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
     * Diese Methode beendet den Operative vollst�ndig, das hei�t er wird vom
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
     * ausgel��t.
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
     * @param parProb  Neues Teilproblem f�r den Operative.
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
     * �brmittelt dem Dispatcher eine Fehlermeldung. Wenn dies nicht m�glich
     * ist, wird der Operative beendet.
     *
     * @param exceptionCode     Integerwert, der die Ausnahme charakterisisert.
     * @param exceptionMessage  Fehlermeldung, die die Ausnahme n�her beschreibt.
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
     * Gibt eine berechnete Teill�sung dem ComputeManager zur�ck. Damit wird
     * dem ComputeManager implizit mittgeteilt, dass der Operative nun ein
     * neues Teilproblem �bernehmen kann.
     *
     * @param parSol  Teill�sung, die dem ComputeManager �bermittelt werden
     *                soll
     */
    synchronized void returnPartialSolution(PartialSolution parSol) {
        long versuche = CONNECT_RETRIES;
        boolean transmitted = false;
        String exceptionMessage = null;

        // Den oder die ClassLoader l�schen
        CacheFlushingRMIClSpi.flushClassLoaders();

        // Mehrmals versuchen, die Teill�sung zu senden
        while ((versuche > 0) && (!transmitted)) {
            Miscellaneous.printDebugMessage(
                debugMode,
                "Debug: Versuche, Teilergebnis zur�ckzugeben");
            try {
                computeManager.returnPartialSolution(parSol, this);
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Teilergebnis zur�ckgegeben");
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
     * Bricht die momentan ausgef�hrte Hintergrundberechnung ab und schafft
     * die Voraussetzung f�r den Start einer neuen Hintergrundberechnung.
     * Falls gerade keine Hintergrundberechnung durchgef�hrt wird, so
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
//            Der nachfolgende Aufruf des GC f�hrt beim Sun JDK 1.4 bis
//            Version 1.4.2_04-b05 h�ufiger zum Absturz.
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
        boolean debug = false;
        String compManager = null;

        if ((args.length < 1) | (args.length > 2)) {
            System.out.println(
                "Fehler! Aufruf mit:\n\n "
                    + "OperativeImpl"
                    + " <registry>:<port> [-d / --debug]\n\n"
                    + "wobei ersteres die Adresse der Registry ist, an der der"
                    + "ComputeManager angemeldet ist, zweiteres ist optional"
                    + "und schaltet die Debug-Meldungen an");
            System.exit(1);
        }
        if (args.length == 2) {
            if ((args[1].equalsIgnoreCase("--debug"))
                || (args[1].equalsIgnoreCase("-d"))) {
                debug = true;
            }
        }

        compManager = args[0];
        // Wenn beim ProblemManager kein Port angegeben ist, den Standard-Port
        // verwenden
        if (compManager.indexOf(":") == -1) {
            compManager += ":" + ComputeManager.PORT_NO;
        }

        String binding = "//" + compManager + "/"
                         + ComputeManager.COMPUTEMANAGER_ID_STRING;

        try {
            new OperativeImpl(binding, debug);
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
