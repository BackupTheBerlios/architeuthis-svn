/*
 * file:        DispatcherImpl.java
 * created:     18.12.20003
 * last change: 16.02.2005 by Dietmar Lippold
 * developer:   Jürgen Heit,       juergen.heit@gmx.de
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

import de.unistuttgart.architeuthis.dispatcher.computemanaging.ComputeManagerImpl;
import de.unistuttgart.architeuthis.systeminterfaces.ComputeManager;
import de.unistuttgart.architeuthis.misc.commandline.Option;
import de.unistuttgart.architeuthis.misc.commandline.ParameterParser;
import de.unistuttgart.architeuthis.misc.commandline.ParameterParserException;

/**
 * Der Dispatcher startet den ComputeMananger und ProblemManager.
 *
 * @author Jürgen Heit, Andreas Heydlauff, Dietmar Lippold, Michael Wohlfart
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
     * Überprüfungen (in ms).
     */
    private static final long DEFAULT_INTERVAL_OPERATIVEMONITORING = 10000;

    /**
     * Maximale Anzahl der Kommunikationsversuche für einen Operative.
     */
    private static final int DEFAULT_REMOTE_OPERATIVE_MAXTRY = 3;

    /**
     * Übernimmt die Initialisierung des {@link ComputeManager}. Beim Starten
     * der {@link ComputeManagerImpl} werden die Kommandozeilenparameter
     * ausgelesen und die Konfigurationsdatei eingelesen. Die Angaben, die in
     * der Konfigurationsdatei enthalten sein können, sind im Handbuch des
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ProblemManager} im
     * Abschnitt des {@link ComputeManager} aufgelistet.
     *
     * @param args  Array der Kommandozeilen-Parameter-Strings
     */
    public static void main(String[] args) {
        // Defaultwerte setzen

        // default config filename
        String configname = DEFAULT_CONFIGNAME;
        // max. Kommunikationsversuche (für ComputeManagerImpl Konstruktor)
        long remoteOperativeMaxtries = DEFAULT_REMOTE_OPERATIVE_MAXTRY;
        // Timout Zeitspanne für Kommunikation mit Operative
        // (für ComputeManagerImpl Konstruktor)
        long millisOperativeMonitoringInterval = DEFAULT_INTERVAL_OPERATIVEMONITORING;
        // default port
        int port = Integer.parseInt(ComputeManager.PORT_NO);
        // parameter für ComputeManagerImpl Konstruktor. Beitzt keinen
        // default-Wert.
        boolean additionalThreads;

        ParameterParser parser = new ParameterParser();

        Option helpSwitch = new Option("help");
        parser.addOption(helpSwitch);

        Option threadSwitch = new Option("t");
        parser.addOption(threadSwitch);

        Option configOption = new Option("c");
        configOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        configOption.setName("configfile");
        parser.addOption(configOption);

        Option portOption = new Option("port");
        portOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        portOption.setName("port");
        parser.addOption(portOption);

        Option deadtriesOption = new Option("deadtries");
        deadtriesOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        deadtriesOption.setName("deadtries");
        parser.addOption(deadtriesOption);

        Option deadtimeOption = new Option("deadtime");
        deadtimeOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        deadtimeOption.setName("deadtime");
        parser.addOption(deadtimeOption);

        parser.setComandline(args);

        try {
            // Option von help ermitteln
            parser.parseOption(helpSwitch);
            if (parser.isEnabled(helpSwitch)) {
                System.out.println(parser.toString());
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
                    // ... und im Kommandozeilenparser setzen.
                    HashMap propsHashMap = new HashMap(props);
                    parser.parseProperties(propsHashMap);
                }

                // properties mit den Kommandozeilenparametern überschreiben
                parser.parseAll(args);

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
            System.err.println("Ungültige URL für RMI-Registry : "
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

