/*
 * file:        DispatcherImpl.java
 * created:     18.12.20003
 * last change: 10.04.2006 by Dietmar Lippold
 * developer:   J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.dispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;

import de.unistuttgart.commandline.Option;
import de.unistuttgart.commandline.ParameterParser;
import de.unistuttgart.commandline.ParameterParserException;

import de.unistuttgart.architeuthis.dispatcher.computemanaging.ComputeManagerImpl;
import de.unistuttgart.architeuthis.systeminterfaces.ComputeManager;

/**
 * Der Dispatcher startet den ComputeMananger und ProblemManager.
 *
 * @author J�rgen Heit, Andreas Heydlauff, Dietmar Lippold, Michael Wohlfart
 */
public final class DispatcherImpl {

    /**
     * Privater Konstruktor, wird nicht verwendet. Dieser wird von checkstyle
     * empfohlen um die (versehentliche) Verwendung eines default Konstruktors
     * zu verhindern.
     */
    private DispatcherImpl() {
        // wird nie verwendet
    }

    /**
     * Standardname der Konfigurationsdatei.
     */
    private static final String DEFAULT_CONFIGNAME = "compserv.conf";

    /**
     * Minimale Zeitspanne zwischen zwei Operative-Erreichbarkeits
     * �berpr�fungen (in ms).
     */
    private static final long DEFAULT_INTERVAL_OPERATIVEMONITORING = 10000;

    /**
     * Maximale Anzahl der Kommunikationsversuche f�r einen Operative.
     */
    private static final int DEFAULT_REMOTE_OPERATIVE_MAXTRY = 3;

    /**
     * Aktiviert das Logging zum Level FINEST.
     */
    private static void activateFinestLogging() {
        Handler[]      handlers;
        ConsoleHandler consoleHandler;
        Logger         rootLogger, archiLogger;

        rootLogger = Logger.getLogger("");
        archiLogger = Logger.getLogger("de.unistuttgart.architeuthis");

        // log-Level f�r alle Logger von Architeuthis setzen
        archiLogger.setLevel(Level.FINEST);

        // der DefaultHandler h�ngt am root-Logger
        handlers = rootLogger.getHandlers();

        // einen ConsoleHandler finden:
        consoleHandler = null;
        for (int i = 0; i < handlers.length; i++) {
            if (handlers[i] instanceof ConsoleHandler) {
                consoleHandler = (ConsoleHandler) handlers[i];
            }
        }

        // Wenn kein ConsoleHandler am root-Logger vorhanden ist, einen
        // anh�ngen.
        if (consoleHandler == null) {
            consoleHandler = new java.util.logging.ConsoleHandler();
            Logger.getLogger("").addHandler(consoleHandler);
        }

        consoleHandler.setLevel(Level.FINEST);
    }

    /**
     * �bernimmt die Initialisierung des {@link ComputeManager}. Beim Starten
     * der {@link ComputeManagerImpl} werden die Kommandozeilenparameter
     * ausgelesen und die Konfigurationsdatei eingelesen. Die Angaben, die in
     * der Konfigurationsdatei enthalten sein k�nnen, sind im Handbuch des
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ProblemManager} im
     * Abschnitt des {@link ComputeManager} aufgelistet.
     *
     * @param args  Array der Kommandozeilen-Parameter-Strings
     */
    public static void main(String[] args) {
        // Defaultwerte setzen

        // default config filename
        String configname = DEFAULT_CONFIGNAME;
        // max. Kommunikationsversuche (f�r ComputeManagerImpl Konstruktor)
        long remoteOperativeMaxtries = DEFAULT_REMOTE_OPERATIVE_MAXTRY;
        // Timout Zeitspanne f�r Kommunikation mit Operative
        // (f�r ComputeManagerImpl Konstruktor)
        long millisOperativeMonitoringInterval = DEFAULT_INTERVAL_OPERATIVEMONITORING;
        // default port
        int port = Integer.parseInt(ComputeManager.PORT_NO);
        // parameter f�r ComputeManagerImpl Konstruktor. Beitzt keinen
        // default-Wert.
        boolean additionalThreads;

        ParameterParser parser = new ParameterParser();

        Option helpSwitch = new Option("help");
        helpSwitch.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);
        parser.addOption(helpSwitch);

        Option debugSwitch = new Option("d");
        debugSwitch.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);
        parser.addOption(debugSwitch);

        Option threadSwitch = new Option("t");
        threadSwitch.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);
        parser.addOption(threadSwitch);

        Option configOption = new Option("c");
        configOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        configOption.setDescription("configfile");
        parser.addOption(configOption);

        Option portOption = new Option("port");
        portOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        portOption.setDescription("port");
        parser.addOption(portOption);

        Option deadtriesOption = new Option("deadtries");
        deadtriesOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        deadtriesOption.setDescription("deadtries");
        parser.addOption(deadtriesOption);

        Option deadtimeOption = new Option("deadtime");
        deadtimeOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        deadtimeOption.setDescription("deadtime");
        parser.addOption(deadtimeOption);

        parser.setComandline(args);

        try {
            // Option von help ermitteln
            parser.parseOption(helpSwitch);
            if (parser.isEnabled(helpSwitch)) {
                System.out.println("Benutzung:");
                System.out.println("DispatcherImpl " + parser.toString());
            } else {
                // Option von config-Datei ermitteln
                parser.parseOption(configOption);
                if (parser.isEnabled(configOption)) {
                    // Es wird ein vorgegebenes properties file verwendet
                    configname = parser.getParameter(configOption);
                } else if (!(new File(configname)).canRead()) {
                    // Es wurde kein properties file vorgegeben und ein default
                    // properties file ist nicht vorhanden.
                    configname = "";
                }

                // Daten vom properties file laden wenn vorhanden.
                if (configname != "") {
                    // properties laden ...
                    Properties props = new Properties();
                    props.load(new FileInputStream(configname));

                    // ... und im Kommandozeilenparser zusammen mit den
                    // Kommandozeilen-Parametern parsen.
                    HashMap propsHashMap = new HashMap(props);
                    parser.parseAll(args, propsHashMap);
                } else {
                    // Kommandozeilenparametern ohne properties-Datei parsen.
                    parser.parseAll(args);
                }

                if (parser.isEnabled(debugSwitch)) {
                    // f�r debugging finest Logging aktivieren.
                    activateFinestLogging();
                }

                additionalThreads = parser.isEnabled(threadSwitch);

                if (parser.isEnabled(portOption)) {
                    port = parser.getParameterAsInt(portOption);
                }

                if (parser.isEnabled(deadtimeOption)) {
                    millisOperativeMonitoringInterval
                        = parser.getParameterAsLong(deadtimeOption);
                }

                if (parser.isEnabled(deadtriesOption)) {
                    remoteOperativeMaxtries
                        = parser.getParameterAsLong(deadtriesOption);
                }

                new ComputeManagerImpl(
                        port,
                        millisOperativeMonitoringInterval,
                        remoteOperativeMaxtries,
                        additionalThreads);
            }
        // Exceptions vom ComputeManagerImpl Konstruktor:
        } catch (UnknownHostException e) {
            System.err.println("IP-Adresse vom localhost konnte nicht"
                    + "ermittelt werden");
        } catch (MalformedURLException e) {
            System.err.println("Ung�ltige URL f�r RMI-Registry : "
                    + e.toString());
        } catch (AlreadyBoundException e) {
            System.err.println("Port " + port + " wird schon benutzt.");
        } catch (RemoteException e) {
            System.err.println(
                    "Fehler beim Starten des Dispatchers!\n"
                    + "Stellen Sie sicher, dass Port "
                    + String.valueOf(port)
                    + " nicht benutzt wird.");

        // Kommandozeilen Expections:
        } catch (ParameterParserException e) {
            System.err.println(parser);
            e.printStackTrace();

        // sonstige Probleme:
        } catch (FileNotFoundException e) {
            System.err.println("Konfigurationsfile konnte nicht gefunden werden: " + configname);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO Exception");
            e.printStackTrace();
        }

    }
}

