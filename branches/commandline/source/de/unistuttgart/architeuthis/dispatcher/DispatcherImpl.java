/*
 * file:        DispatcherImpl.java
 * created:     18.12.20003
 * last change: 26.09.2004 by Dietmar Lippold
 * developer:   Jürgen Heit,       juergen.heit@gmx.de
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


package de.unistuttgart.architeuthis.dispatcher;

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
 * @author Jürgen Heit, Andreas Heydlauff, Dietmar Lippold
 */
public final class DispatcherImpl {

    /**
     * Standardname der Konfigurationsdatei.
     */
    private static final String DEFAULT_CONFIGNAME = "cmpserv.conf";

    /**
     * Minimale Zeitspanne zwischen zwei Operative-Erreichbarkeits
     * Überprüfungen
     */
    private static final long DEFAULT_INTERVAL_OPERATIVEMONITORING = 10000;

    /**
     * Maximale Anzahl der Kommunikationsversuche für einen Operative.
     */
    private static final int REMOTE_OPERATIVE_MAXTRY = 3;

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
        //CommandLineParser cmdln = new CommandLineParser(args);
        String configname = DEFAULT_CONFIGNAME;
        boolean additionalThreads = false;

        ParameterParser parser = new ParameterParser();


        Option threadSwitch = new Option("t");
        parser.addOption(threadSwitch);

        Option configOption = new Option("c");
        configOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
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

        // verwenden wir ein properties file ?
        if (parser.isEnabled(configOption)) {
            configname = parser.getParameter(configOption);
        }

        try {
            // properties laden
            Properties props = new Properties();
            HashMap in = new HashMap(props);
            parser.parseProperties(in);

            parser.parseAll(args);

            additionalThreads = parser.isEnabled(threadSwitch);

            int port = Integer.parseInt(ComputeManager.PORT_NO);
            if (parser.isEnabled(portOption)) {
                port = parser.getParameterAsInt(portOption);
            }

            long millisOperativeMonitoringInterval = DEFAULT_INTERVAL_OPERATIVEMONITORING;
            if (parser.isEnabled(deadtimeOption)) {
                millisOperativeMonitoringInterval
                    = parser.getParameterAsLong(deadtimeOption);
            }

            long remoteOperativeMaxtries = REMOTE_OPERATIVE_MAXTRY;
            if (parser.isEnabled(deadtriesOption)) {
                millisOperativeMonitoringInterval
                    = parser.getParameterAsLong(deadtriesOption);
            }


            try {
                new ComputeManagerImpl(
                        port,
                        millisOperativeMonitoringInterval,
                        remoteOperativeMaxtries,
                        additionalThreads);
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
            }
        } catch (ParameterParserException e1) {
            System.err.println(parser);
            e1.printStackTrace();
        }

    }
}
