/*
 * file:        RuntimeComparison.java
 * created:     <???>
 * last change: 06.03.2006 by Dietmar Lippold
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

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;

import de.unistuttgart.commandline.Option;
import de.unistuttgart.commandline.ParameterParser;
import de.unistuttgart.commandline.ParameterParserException;

import de.unistuttgart.architeuthis.misc.Miscellaneous;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;

/**
 * Das Programm dient zum Vergleich zwischen der Berechnung eines
 * {@link Problem}'s auf einem Computer und der Berechnung auf dem
 * Compute-System.
 *
 * @author Achim Linke, Jürgen Heit, Dietmar Lippold
 */
public class RuntimeComparison {

    /**
     * Logger für diese Klasse
     */
    private static final Logger LOGGER = Logger
            .getLogger(RuntimeComparison.class.getName());

    /**
     * Umrechnungsteiler von Millisekunden in Sekunden.
     */
    private static final long CHANGE_MILLISEC_TO_SEC = 1000;

    /**
     * Anhang für den Dateinamen des Ergebnisses der lokalen Berechnung.
     */
    private static final String LOCAL_SUPPLEMENT = "-local";

    /**
     * Anhang für den Dateinamen des Ergebnisses der remote Berechnung.
     */
    private static final String REMOTE_SUPPLEMENT = "-remote";

    /**
     * URL, unter der die folgenden Java-Dateien erreichbar sein müssen:
     * <ul>
     * <li>Implementierung des Interfaces {@link Problem}</li>
     * <li>Implementierung des Interfaces {@link PartialProblem}</li>
     * <li>Implementierung des Interfaces
     * {@link de.unistuttgart.architeuthis.systeminterfaces.PartialSolution}</li>
     * </ul>
     * Zum Beispiel:
     * http://www.informatik.uni-stuttgart.de/ifi/is/RMI-Test/Testenvironment/
     */
    private static URL url;

    /**
     * Der vollständige Name inklusive der packages der Java Datei, die das
     * Interface {@link Problem} implementiert.
     * Zum Beispiel:
     * de.unistuttgart.architeuthis.testenvironment.problem.ProblemImpl
     */
    private static String problemName;

    /**
     * Die Adresse oder der DNS-Name des Rechners, auf dem sich der
     * {@link de.unistuttgart.architeuthis.dispatcher.DispatcherImpl} befindet.
     * Zum Beispiel: isny.informatik.uni-stuttgart.de
     */
    private static String adressCompSys;

    /**
     * Die Statistik zum Problem nach dessen Berechnung.
     */
    private static ProblemStatistics finalProblemStat;

    /**
     * Berechnet das Problem lokal. Dafür wird ein Teilproblem generiert und
     * dieses berechnet.
     *
     * @return Serializable  Die Lösung des Problems.
     *
     * @throws ClassNotFoundException   Class-Datei konnte nicht vom Webserver
     *                                  geladen werden.
     * @throws InstantiationException   Fehler beim casten von Klasse ins
     *                                  Interface <code>Problem</code>
     * @throws IllegalAccessException   Fehler beim casten von Klasse ins
     *                                  Interface <code>Problem</code>
     * @throws ProblemComputeException  Fehler bei der Berechnung ist
     *                                  aufgetreten.
     */
    private static Serializable computeLocal()
        throws ClassNotFoundException,
               InstantiationException,
               IllegalAccessException,
               ProblemComputeException {

        Class pc = null;
        ProblemComputation problemComputation;
        SerializableProblem problem = null;
        Serializable solution;

        try {
            pc = RMIClassLoader.loadClass(url, problemName);
            problem = (SerializableProblem) pc.newInstance();
        } catch (MalformedURLException mURLEx) {
            throw new ClassNotFoundException(mURLEx.getMessage());
        }

        problemComputation = new ProblemComputation();
        solution = problemComputation.computeProblem(problem);
        finalProblemStat = problemComputation.getFinalProblemStat();
        return solution;
    }

    /**
     * Übergibt dem
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ProblemTransmitter} das
     * <code>Problem</code> und berechnet es auf dem <code>ProblemManager</code>
     * parallel. Dabei wird dem
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ProblemTransmitter} die
     * Adresse des Webservers auf dem das Problem liegt, der Name des Problems und
     * die Adresse des <code>ComputeSystems</code> übergeben.
     *
     * @return Serializable  Die Lösung des Problems.
     *
     * @throws MalformedURLException    URL der Registry oder des Webservers ist
     *                                  falsch.
     * @throws RemoteException          Kommunikationsprobleme über RMI.
     * @throws NotBoundException        Verzeichnis wurde auf Registry nicht
     *                                  gefunden.
     * @throws ClassNotFoundException   Class-Datei konnte nicht vom Webserver
     *                                  geladen werden.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     */
    private static Serializable computeRemote()
        throws ClassNotFoundException,
               InstantiationException,
               IllegalAccessException,
               MalformedURLException,
               RemoteException,
               NotBoundException,
               ProblemComputeException {

        URL[] urls = new URL[1];
        Class pc = null;
        SerializableProblem problem = null;
        ProblemComputation problemComputation;
        Serializable solution;

        try {
            pc = RMIClassLoader.loadClass(url, problemName);
            problem = (SerializableProblem) pc.newInstance();
        } catch (MalformedURLException mUrlEx) {
            throw new ClassNotFoundException(mUrlEx.getMessage());
        }

        problemComputation = new ProblemComputation();
        urls[0] = url;
        solution =  problemComputation.transmitProblem(problem, adressCompSys,
                                                       urls);
        finalProblemStat = problemComputation.getFinalProblemStat();
        return solution;

//        ProblemTransmitterImpl transmit =
//            new ProblemTransmitterImpl(adressCompSys, debugMode);
//        solution = transmit.transmitProblem(url, problemName, null);
//        finalProblemStat = transmit.getFinalProblemStat();
//        return solution;
    }

    /**
     * Gibt die Parameter aus, die zum starten benötigt werden.
     */
    private static void usage() {
        System.out.println(
                "RuntimeComparison\n"
                + "Benutzung:\n"
                + "RuntimeComparison -u <package url> -r <computesystem> "
                + "-c <klassenname> -f <dateiname> [-d]\n"
                + "Die beoetigten Parameter sind:\n"
                + "-u packageURL  Der URL des äußersten Pakets der Klassen"
                + " des Problems\n"
                + "-r ProblemManager  Der Name des Computesystems\n"
                + "-c Class  Der Name der Problem-Klasse "
                + "(mit Paketbezeichnungen)\n"
                + "Optional:\n"
                + "-f Filename  Der Anfang eines Dateinamen, unter dem die"
                + " Lösung gespeichert werden soll\n"
                + "-d schaltet den Debug-Modus ein\n"
                + "\nBeispiel:\n"
                + "RuntimeComparison -u http://localhost/"
                + " -r <dispatcher-rechner>:<portnummer> "
                + "-c meinproblem.Problem -f Loesung.txt");
        System.exit(1);
    }

    /**
     * @throws
     * Nachdem die Parameter vom Starten auf Korrektheit verglichen wurden, wird
     * das implementierte Problem lokal und parallel berechnet.
     *
     * @param args  die Parameter die den Webserver, das Computesystem, den
     *              Klassennamen der Implementation des Interfaces
     *              <code>Problem</code>, sowie den Dateinamen für die Lösung
     *              enthalten
     *
     * @throws NotBoundException        Verzeichnis wurde auf Registry nicht
     *                                  gefunden.
     * @throws IOException              Fehler bei Ein- oder Ausgabe.
     * @throws RemoteException          Kommunikationsprobleme über RMI.
     * @throws ClassNotFoundException   Class-Datei konnte nicht vom Webserver
     *                                  geladen werden.
     * @throws InstantiationException   Fehler beim casten von Klasse ins
     *                                  Interface <code>Problem</code>.
     * @throws IllegalAccessException   Fehler beim casten von Klasse ins
     *                                  Interface <code>Problem</code>.
     * @throws ProblemComputeException  Fehler bei der Berechnung auf dem
     *                                  Compute-System ist aufgetreten.
     * @throws IOException              Lösung konnte nicht geschrieben werden.
     */
    public static void main(String[] args)
        throws NotBoundException,
               IOException,
               RemoteException,
               ClassNotFoundException,
               InstantiationException,
               IllegalAccessException,
               ProblemComputeException {

        ParameterParser parser = new ParameterParser();

        Option urlOption = new Option("u");
        urlOption.setDescription("webserver");
        urlOption.setOptional(false);
        parser.addOption(urlOption);

        Option remoteComputeOption = new Option("r");
        remoteComputeOption.setDescription("adressCompSys");
        remoteComputeOption.setOptional(false);
        parser.addOption(remoteComputeOption);

        Option problemNameOption = new Option("c");
        problemNameOption.setDescription("problemName");
        problemNameOption.setOptional(false);
        parser.addOption(problemNameOption);

        Option fileNameOption = new Option("f");
        fileNameOption.setDescription("filename");
        fileNameOption.setOptional(true);
        parser.addOption(fileNameOption);

        Option debugSwitch = new Option("d");
        parser.addOption(debugSwitch);

        Exception exception = null;
        Serializable solutionLocal = null;
        Serializable solutionRemote = null;
        String filename = null;
        String filenameLocal = null;
        String filenameRemote = null;
        double timeLocal;
        double timeRemote;

        try {
            parser.parseAll(args);

            // Falls der Benutzer keinen eigenen SecurityManager verwendet, muss
            // einer gesetzt werden, da die zu übertragenden Objekte mittels eines
            // RMIClassLoader geladen werden.
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new RMISecurityManager());
            }

            try {
                url = new URL(parser.getParameter(urlOption));
            } catch (MalformedURLException e) {
                System.out.println("\n\nWebserver-Adresse fehlerhaft!\n\n");
                usage();
            }

            adressCompSys = parser.getParameter(remoteComputeOption);

            // Wenn beim ProblemManager kein Port angegeben ist, den
            // Standard-Port verwenden
            if (adressCompSys.indexOf(":") == -1) {
                adressCompSys += ":" + ProblemManager.PORT_NO;
            }

            problemName = parser.getParameter(problemNameOption);

            // optionaler Parameter
            if (parser.isEnabled(fileNameOption)) {
                filename = parser.getParameter(fileNameOption);
                filenameLocal = filename + LOCAL_SUPPLEMENT;
                filenameRemote = filename + REMOTE_SUPPLEMENT;
            }

            if (parser.isEnabled(debugSwitch)) {
                // log-Level für alle Logger dieses packages:
                Logger logger = Logger.getLogger("de.unistuttgart.architeuthis.user");
                logger.setLevel(Level.FINEST);

                // der DefaultHandler hängt (normalerweise) am root-Logger
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

            System.out.println("Berechnung lokal:");
            timeLocal = System.currentTimeMillis();
            try {
                solutionLocal = computeLocal();
                timeLocal = System.currentTimeMillis() - timeLocal;
                System.out.println("\nErgebnis:");
                if (filenameLocal == null) {
                    System.out.println(solutionLocal.toString());
                } else {
                    Miscellaneous.writeSerializableToFile(solutionLocal,
                            filenameLocal);
                    System.out.println("Geschrieben in Datei " + filenameLocal);
                }
                System.out.println("Benötigte Zeit: "
                        + (timeLocal / CHANGE_MILLISEC_TO_SEC)
                        + " Sekunden");
                System.out.println("\nStatistik:\n" + finalProblemStat);
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE,
                        "Klasse wurde nicht gefunden");
                exception = e;
            } catch (InstantiationException e) {
                LOGGER.log(Level.SEVERE,
                        "Objekt kann nicht instanziert werden");
                exception = e;
            } catch (IllegalAccessException e) {
                LOGGER.log(Level.SEVERE,
                        "Kein Zugriff auf computelocal erlaubt");
                exception = e;
            } catch (ProblemComputeException e) {
                LOGGER.log(Level.SEVERE,
                        "Fehler bei der Berechnung / unerlaubte Rechnung");
                exception = e;
            }

            if (exception == null) {
                System.out.println("\nBerechnung remote:");
                timeRemote = System.currentTimeMillis();
                try {
                    solutionRemote = computeRemote();
                    timeRemote = System.currentTimeMillis() - timeRemote;
                    System.out.println("\nErgebnis:");
                    if (filenameRemote == null) {
                        System.out.println(solutionRemote.toString());
                    } else {
                        Miscellaneous.writeSerializableToFile(solutionRemote,
                                filenameRemote);
                        System.out.println("Geschrieben in Datei "
                                + filenameRemote);
                    }
                    System.out.println("Benötigte Zeit: "
                            + (timeRemote / CHANGE_MILLISEC_TO_SEC)
                            + " Sekunden");
                    System.out.println("\nStatistik:\n" + finalProblemStat);
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE,
                            "Klasse wurde nicht gefunden");
                    exception = e;
                } catch (InstantiationException e) {
                    LOGGER.log(Level.SEVERE,
                            "Objekt kann nicht instanziert werden");
                    exception = e;
                } catch (IllegalAccessException e) {
                    LOGGER.log(Level.SEVERE,
                            "Kein Zugriff auf computelocal erlaubt");
                    exception = e;
                } catch (MalformedURLException e) {
                    LOGGER.log(Level.SEVERE,
                            "URL-Addresse fehlerhaft");
                    exception = e;
                } catch (RemoteException e) {
                    LOGGER.log(Level.SEVERE,
                            "Fehler beim Verbindungsaufbau mit RMI");
                    exception = e;
                } catch (NotBoundException e) {
                    LOGGER.log(Level.SEVERE,
                            "Fehler beim lookup in der Registry (RMI)");
                    exception = e;
                } catch (ProblemComputeException e) {
                    LOGGER.log(Level.SEVERE,
                            "Fehler bei der Berechnung / unerlaubte Rechnung");
                    exception = e;
                }
            }

            if (exception != null) {
                LOGGER.throwing(RuntimeComparison.class.getName(), "main", exception);
            }
        } catch (ParameterParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}

