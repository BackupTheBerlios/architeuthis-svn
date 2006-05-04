/*
 * filename:    OperativeImpl.java
 * created:     <???>
 * last change: 04.05.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.operative;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.unistuttgart.commandline.Option;
import de.unistuttgart.commandline.ParameterParser;
import de.unistuttgart.commandline.ParameterParserException;

import de.unistuttgart.architeuthis.misc.CacheFlushingRMIClSpi;
import de.unistuttgart.architeuthis.systeminterfaces.ComputeManager;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.systeminterfaces.Operative;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreException;
import de.unistuttgart.architeuthis.userinterfaces.RemoteStoreGenException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStoreGenerator;

/**
 * Implementierung die Operative Remote-Anwendung und nimmt die Kommunikation
 * mit dem Dispatcher vor. Die Klasse erzeugt Instanzen von
 * <code>OperativeComputing</code> für die Durchführung der Berechnung, wobei
 * ein erzeugtes Thread-Objekt wiederverwendet wird, wenn der Thread nicht
 * abgebrochen werden mußte.
 *
 * @author Jürgen Heit, Ralf Kible, Dietmar Lippold, Michael Wohlfart
 */
public class OperativeImpl extends UnicastRemoteObject implements Operative {

    /**
     * Logger für diese Klasse
     */
    private static final Logger LOGGER
        = Logger.getLogger(OperativeImpl.class.getName());

    /**
     * Generierte SerialVersionUID. Diese muss geändert werden, sobald
     * strukurelle Änderungen an dieser Klasse durchgeführt worden sind.
     */
    private static final long serialVersionUID = 3977296607899299896L;

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
     * Der dezentrale Speichers oder <CODE>null</CODE>, falls keiner verwendet
     * wird.
     */
    private RemoteStore distRemoteStore = null;

    /**
     * Der zentrale Speicher oder <CODE>null</CODE>, falls keiner verwendet
     * wird.
     */
    private RemoteStore centralRemoteStore = null;

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
     * @param computeManager  Der ComputeManagers in der RMI-Registry.
     *
     * @throws MalformedURLException  Die Angabe vom <code>compManager</code>
     *                                war kein zulässiger Name.
     * @throws RemoteException        Kommunikationsproblem über RMI.
     * @throws NotBoundException      Der Dispatcher war auf der Registry nicht
     *                                eingetragen.
     */
    private OperativeImpl(ComputeManager computeManager)
        throws MalformedURLException,
               RemoteException,
               NotBoundException {

        this.computeManager = computeManager;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
    }

    /**
     * Startet den Berechnungs-Thread, der im Hintergrund läuft. Zur
     * Berechnung eines tatsächlichen PartialProblmes muss noch die
     * fetchPartialProblemMethode aufgerufen werden.<P>
     *
     * Diese Methode ist protected für den Zugriff durch JUnit-Tests.
     */
    protected synchronized void startComputation() {

        // Hier wird ein shutdownHook gesetzt. Dieser wird aufgerufen, wenn
        // vom System ein term-Signal kommt (also beim Beenden oder bei
        // Strg+c). Dann wird kurz aufgeräumt.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });

        backgroundComputation = new OperativeComputing(this);
        LOGGER.log(Level.INFO, "Operative gestartet");
    }

    /**
     * Meldet, wenn sowohl ein zentraler wie ein dezentraler RemoteStore
     * vorhanden ist, zuerst den zentralen beim dezentralen und dann den
     * dezentralen beim zentralen ab.
     *
     * @throws RemoteException       Bei einem RMI Problem.
     * @throws RemoteStoreException  Bei der Abmeldung oder Beendigung des
     *                               zentralen oder dezentralen RemoteStore.
     */
    private void unregisterRemoteStore()
        throws RemoteException, RemoteStoreException {

        try {
            if ((centralRemoteStore != null) && (distRemoteStore != null)
                    && (distRemoteStore != centralRemoteStore)) {
                distRemoteStore.unregisterRemoteStore(centralRemoteStore);
                centralRemoteStore.unregisterRemoteStore(distRemoteStore);
                distRemoteStore.terminate();
            } else if ((centralRemoteStore == null) && (distRemoteStore != null)) {
                distRemoteStore.terminate();
            }
        } catch (RemoteException e) {
            throw e;
        } catch (ThreadDeath e) {
            // Dieser Error darf nicht abgefangen werden.
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING,
                       "Fehler bei der Abmeldung oder Beendigung eines RemoteStore");
            throw new RemoteStoreException(e.getMessage(), e.getCause());
        } finally {
            centralRemoteStore = null;
            distRemoteStore = null;
        }
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
     * @throws RemoteException  Bei RMI-Verbindungsproblemen.
     */
    public synchronized void doExit() throws RemoteException {

        LOGGER.log(Level.INFO, "Operative wird beendet");

        if (backgroundComputation != null) {
            // OperativeComputing-Thread beenden
            LOGGER.log(Level.FINE, "Berechnung wird gestoppt");

            backgroundComputation.stop();
            backgroundComputation = null;

            // RemoteStore abmelden
            try {
                unregisterRemoteStore();
            } catch (RemoteStoreException e) {
                LOGGER.log(Level.WARNING,
                           "RemoteStoreException beim Beenden aufgetreten."
                           + " Wird nicht mehr gemeldet.");
            }

            // Vom RMI-Server abmelden
            unexportObject(this, true);
        }
        computeManager = null;

        LOGGER.log(Level.INFO, "Operative beendet!");
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
                System.err.println("Abbruch durch Operative-Administrator");
                LOGGER.log(Level.FINE, "Versuche, Verbindung zu trennen.");
                computeManager.unregisterOperative(this);
                computeManager = null;
            }
        } catch (RemoteException e1) {
            //Vermutlich nicht angemeldet gewesen
            LOGGER.log(Level.WARNING, "Fehler beim Abmelden vom Dispatcher.");
        }

        // Die Hintergrundberechnung beenden und vom RMI-Server abmelden
        try {
            doExit();
        } catch (RemoteException e2) {
            // RemoteException kann nicht auftreten.
            LOGGER.log(Level.WARNING,
                       "RemoteException beim Abmelden vom Dispatcher.");
        }
    }

    /**
     * Dummy-Methode, die vom <code>ComputeManager</code> aufgerufen wird, um
     * zu testen, ob der Operative noch reagiert. Falls der Operative nicht mehr
     * erreichbar ist, wird auf dem Server eine <code>RemoteException</code>
     * ausgelößt.
     *
     * @throws RemoteException  Bei RMI-Verbindungsproblemen.
     *
     * @return <code>true</code>, falls der Operative erreichbar ist, sonst
     *         ergibt sich ein Verbindungsfehler.
     */
    public boolean isReachable() throws RemoteException {
        return true;
    }

    /**
     * Wird vom <code>ComputeManager</code> aufgerufen, um dem Operative ein
     * neues Teilproblem zur Berechnung zuzuweisen. Wenn <CODE>generator</CODE>
     * nicht <CODE>null</CODE> ist, wird versucht, einen dezentralen
     * RemoteStore zu erzeugen, um diesen dem Teilproblem zu übergeben. Wenn
     * kein dezentraler RemoteStore erzeugt werden kann oder <CODE>generator</CODE>
     * <CODE>null</CODE> ist, wird den Teilproblem der Wert <CODE>null</CODE>
     * übergeben. Wenn sowohl ein zentraler wie ein dezentraler RemoteStore
     * vorhanden ist, wird zuerst der zentrale beim dezentralen und dann der
     * dezentrale beim zentralen registiert.
     *
     * @param parProb       Neues Teilproblem für den Operative.
     * @param centralStore  Der zentrale RemoteStore.
     * @param generator     Der Generator eines lokalen RemoteStore.
     *
     * @throws RemoteException           Bei RMI-Verbindungsproblemen.
     * @throws ProblemComputeException   Wenn bereits ein Teilproblem berechnet
     *                                   wird.
     * @throws RemoteStoreGenException   Der lokale <CODE>RemoteStore</CODE>
     *                                   konnte nicht erzeugt werden.
     * @throws RemoteStoreException      Die gegenseitige Anmeldung von lokalem
     *                                   und zentralem <CODE>RemoteStore</CODE>
     *                                   war nicht möglich.
     * @throws IllegalArgumentException  Der Wert von <CODE>generator</CODE>
     *                                   ist <CODE>null</CODE>, der Wert von
     *                                   <CODE>centralStore</CODE> ist aber
     *                                   ungleich <CODE>null</CODE>.
     */
    public void fetchPartialProblem(PartialProblem parProb,
                                    RemoteStore centralStore,
                                    RemoteStoreGenerator generator)
        throws RemoteException,
               ProblemComputeException,
               RemoteStoreGenException, // Unterklasse von ProblemComputeException
               RemoteStoreException { // Unterklasse von ProblemComputeException

        if ((generator == null) && (centralStore != null)) {
            throw new IllegalArgumentException("generator ist gleich null,"
                                               + " centralStore aber ungleich null");
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE,
                       "OperativeImpl hat Aufgabe vom ComputeManager empfangen. "
                       + "centralStore: " + centralStore
                       + ", generator: " + generator);
        }

        // Zentralen RemoteStore merken, um den distRemoteStore dort später
        // abzumelden
        centralRemoteStore = centralStore;

        if (generator != null) {
            // Falls der Generator vorhanden ist, einen dezentralen RemoteStore
            // erzeugen.
            try {
                distRemoteStore = generator.generateDistRemoteStore();
            } catch (ThreadDeath e) {
                // Dieser Error darf nicht abgefangen werden.
                throw e;
            } catch (Throwable e) {
                LOGGER.log(Level.SEVERE, "Fehler bei der Erzeugung vom RemoteStore");
                throw new RemoteStoreGenException(e.getMessage(), e.getCause());
            }

            // Registriert die RemoteStores gegenseitig. Falls der Generator
            // keinen dezentralen RemoteStore geliefert hat, wird der zentrale
            // RemoteStore verwendet.
            if ((distRemoteStore != null) && (centralRemoteStore != null)) {
                try {
                    distRemoteStore.registerRemoteStore(centralRemoteStore);
                    centralRemoteStore.registerRemoteStore(distRemoteStore);
                } catch (ThreadDeath e) {
                    // Dieser Error darf nicht abgefangen werden.
                    throw e;
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE,
                               "Fehler bei der Anmeldung oder Abmeldung"
                               + " eines RemoteStore");
                    throw new RemoteStoreException(e.getMessage(), e.getCause());
                }
            } else if (distRemoteStore == null) {
                distRemoteStore = centralRemoteStore;
            }
        }

        backgroundComputation.fetchPartialProblem(parProb, distRemoteStore);
    }

    /**
     * Liefert einen Text, der eine Beschreibung der Ausnahme und einen
     * Stack-Trace enthält.
     *
     * @return  Einen Text, der eine Beschreibung der Ausnahme und einen
     *          Stack-Trace enthält.
     */
    String exceptionMessage(Throwable throwable) {
        StackTraceElement[] traceElements;
        StringBuffer        message;

        message = new StringBuffer();
        message.append(throwable.toString() + "\n");
        message.append("Auf dem Operative >>>\n");

        traceElements = throwable.getStackTrace();
        for (int e = 0; e < traceElements.length; e++) {
            message.append("    at " + traceElements[e].toString() + "\n");
        }

        message.append("<<<");

        return message.toString();
    }

    /**
     * Übermittelt dem Dispatcher eine Fehlermeldung und meldet den
     * distRemoteStore ab. Wenn dies nicht möglich ist, wird der Operative
     * beendet.
     *
     * @param exceptionCode     Wert, der die Ausnahme charakterisisert.
     * @param exceptionMessage  Fehlermeldung, die die Ausnahme näher
     *                          beschreibt. 
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
            computeManager = null;
            shutdown();
        }

        // RemoteStore abmelden wenn möglich
        try {
            unregisterRemoteStore();
        } catch (RemoteStoreException e) {
            LOGGER.log(Level.WARNING, "RemoteStoreException wird nicht mehr gemeldet");
        } catch (RemoteException e) {
            LOGGER.log(Level.WARNING, "RemoteException wird nicht mehr gemeldet");
        }
    }

    /**
     * Gibt eine berechnete Teillösung dem ComputeManager zurück. Damit wird
     * dem ComputeManager implizit mittgeteilt, dass der Operative nun ein
     * neues Teilproblem übernehmen kann.
     *
     * @param parSol  Teillösung, die dem ComputeManager übermittelt werden
     *                soll.
     */
    synchronized void returnPartialSolution(PartialSolution parSol) {
        String  exceptionMessage = null;
        long    versuch;
        int     exceptionCode = -1;
        boolean transmitted = false;

        // Den oder die ClassLoader löschen
        CacheFlushingRMIClSpi.flushClassLoaders();

        // RemoteStore abmelden
        try {
            unregisterRemoteStore();
            LOGGER.log(Level.FINE, "RemoteStore abgemeldet");
            versuch = 1;
        } catch (Exception e) {
            exceptionCode = ExceptionCodes.REMOTE_STORE_EXCEPTION;
            exceptionMessage = exceptionMessage(e);
            LOGGER.log(Level.WARNING,
                       "Abmeldung von RemoteStore fehlgeschlagen: " + e);
            versuch = CONNECT_RETRIES + 1;
        }

        // Mehrmals versuchen, die Teillösung zu senden
        while ((versuch <= CONNECT_RETRIES) && (!transmitted)) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           "Versuche, RemoteStore abzumelden und Teilergebnis"
                           + " zurückzugeben, Versuch: "
                           + "(" + versuch + "/" + CONNECT_RETRIES + ")");
            }
            try {
                computeManager.collectPartialSolution(parSol, this);
                LOGGER.log(Level.FINE, "Teilergebnis zurückgegeben");
                transmitted = true;
            } catch (RemoteException e) {
                exceptionCode = ExceptionCodes.PARTIALSOLUTION_SEND_EXCEPTION;
                exceptionMessage = exceptionMessage(e);
                try {
                    LOGGER.log(Level.WARNING,
                               "Teilergebnis konnte nicht zurückgegeben werden");
                    Thread.sleep(SEND_TIMEOUT);
                } catch (InterruptedException e1) {
                    // uninteressant
                }
            }
            versuch++;
        }

        if (!transmitted) {
            reportException(exceptionCode, exceptionMessage);
        }
    }

    /**
     * Bricht die momentan ausgeführte Hintergrundberechnung ab und schafft
     * die Voraussetzung für den Start einer neuen Hintergrundberechnung.
     * Falls gerade keine Hintergrundberechnung durchgeführt wird, so
     * geschieht nichts.
     *
     * @throws RemoteException  Bei RMI-Verbindungsproblemen.
     */
    public synchronized void stopComputation() throws RemoteException {
        LOGGER.log(Level.FINE, "Berechnung wird gestoppt");
        if (backgroundComputation.isComputing()) {
            backgroundComputation.stop();
            backgroundComputation = null;
//            Der nachfolgende Aufruf des GC führt beim Sun JDK 1.4 bis
//            Version 1.4.2_04-b05 häufiger zum Absturz.
//            System.gc();
            backgroundComputation = new OperativeComputing(this);
        }
    }

    /**
     * Startet den Operative. Als Kommandozeilenargument muss die Adresse
     * und das Verzeichnis der RMI-Registry angegeben werden, an der der
     * <code>ComputeManager</code> angemeldet ist. Als weiteres
     * Kommandozeilenargument kann der Debug-Modus mittels
     * <code>--debug</code> oder <code>-d</code> angeschaltet werden.
     *
     * @param args  Die obligatorischen Kommandozeilenargumente.
     */
    public static void main(String[] args) {
        ParameterParser parser = new ParameterParser();
        StringBuffer    binding = new StringBuffer();
        boolean         debugging = false;

        Option debug1 = new Option("d");
        debug1.setPrefix("-");
        debug1.setOptional(true);
        debug1.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);

        Option debug2 = new Option("debug");
        debug2.setPrefix("--");
        debug2.setOptional(true);
        debug2.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);

        parser.setFreeParameterPosition(ParameterParser.START);
        parser.setFreeParameterDescription("registry:port");
        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        parser.addOption(debug1);
        parser.addOption(debug2);

        try {
            parser.parseAll(args);

            // ist debuging aktiviert?
            if (parser.isEnabled(debug1) || parser.isEnabled(debug2)) {
                debugging = true;

                // log-Level für alle Logger dieses Packages setzen
                Logger logger = Logger.getLogger("de.unistuttgart.architeuthis.operative");
                logger.setLevel(Level.FINEST);

                // der DefaultHandler hängt am root-Logger
                Handler[] handlers = Logger.getLogger("").getHandlers();

                // einen ConsoleHandler finden:
                ConsoleHandler consoleHandler = null;
                for (int i=0; i < handlers.length; i++) {
                    if (handlers[i] instanceof ConsoleHandler) {
                        consoleHandler = (ConsoleHandler) handlers[i];
                    }
                }

                // kein ConsoleHandler am root-Logger ?!
                //  wir hängen selbst einen an:
                if (consoleHandler == null) {
                    consoleHandler = new java.util.logging.ConsoleHandler();
                    Logger.getLogger("").addHandler(consoleHandler);
                }

                consoleHandler.setLevel(Level.FINEST);
            }

            binding.append(parser.getFreeParameter());

            // ist ein Port angegeben ?
            if (binding.indexOf(":") == -1) {
                // Es wurde kein Port angegeben: default Port verwenden.
                binding.append(":");
                binding.append(ComputeManager.PORT_NO);
            }

            // noch id String anhängen und Slashes dazubasteln.
            binding.insert(0, "//");
            binding.append("/");
            binding.append(ComputeManager.COMPUTEMANAGER_ID_STRING);

            if (LOGGER.isLoggable(Level.CONFIG)) {
                LOGGER.log(Level.CONFIG, "Parameter für Operative: "
                        + "debugging: " + debugging
                        + ", ComputeManager: " + binding);
            }

            ComputeManager computeManager =
                (ComputeManager) java.rmi.Naming.lookup(binding.toString());

            // Aufruf des privaten Konstruktors mit ComputeManager.
            OperativeImpl operative = new OperativeImpl(computeManager);

            // erst wenn das funktioniert hat, computing starten
            operative.startComputation();

            // Wenn das funktioniert hat, dann anmelden
            LOGGER.log(Level.CONFIG, "Melde Operative an!");

            computeManager.registerOperative(operative);

            LOGGER.log(Level.CONFIG, "Operative gestartet!");

        } catch (ParameterParserException ex) {
            // usage ausgeben:
            System.err.println(parser);
            // Stacktrace (nicht unbedingt notwendig)
            ex.printStackTrace();
        } catch (java.rmi.StubNotFoundException e) {
            LOGGER.severe("Fehler! Die Stubs wurden vermutlich nicht generiert!");
            System.exit(1);
        } catch (Exception e) {
            LOGGER.severe("Fehler! Verbindung mit ComputeManager konnte"
                    + " nicht hergestellt werden:"
                    + e.getMessage()
                    + " Eventuell ist die Adresse falsch eingegeben worden: "
                    + args[0]);
            System.exit(1);
        }
    }
}

