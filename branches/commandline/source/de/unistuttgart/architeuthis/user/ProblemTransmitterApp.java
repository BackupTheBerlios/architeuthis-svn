/*
 * file:        ProblemTransmitterApp.java
 * created:     08.08.2003
 * last change: 18.01.2005 by Michael Wohlfart
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

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.AccessException;

import de.unistuttgart.architeuthis.misc.Miscellaneous;
import de.unistuttgart.architeuthis.misc.commandline.Option;
import de.unistuttgart.architeuthis.misc.commandline.ParameterParser;
import de.unistuttgart.architeuthis.misc.commandline.ParameterParserException;
import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Diese Klasse ist ein ausführbares Programm, welches dem Benutzer ermöglichen
 * soll, ein <code>Problem</code> an das <code>ProblemManager</code> zu schicken,
 * sowie eine Statistik seines Problems und eine allgemeine Statistik des
 * ComputeSystems in einer grafischen Ausgabe zu erhalten.
 *
 * @author Achim Linke, Dietmar Lippold
 */
public class ProblemTransmitterApp {

    /**
     * Transmitter, mit dem das Problem an den ProblemManager gesendet wird.
     */
    private static ProblemTransmitterImpl transmitter = null;

    /**
     * URL des äußersten Package der abrufbaren Problemklasssen.
     */
    private static URL packageURL = null;

    /**
     * Zentrale Class-Datei, die gestartet werden soll.
     */
    private static String classname = null;

    /**
     * Debug-Modus gibt zusätzliche Informationen aus.
     */
    private static boolean debugMode = false;

    /**
     * Liefert die Lösung zum zu berechnenden Problem. Dazu wird bei einem
     * serialisierbaren Problem das Problem geladen und zum Dispatcher
     * übertragen. Bei einem nicht-serialisierbaren Problem wird der URL und
     * Name der Problem-Klasse zum Dispatcher übertragen.
     *
     * @param serializableProb  gibt an, ob das Problem serialisierbar ist
     *                          und entsprechend übertragen werden soll.
     * @return  die berechnete Lösung.
     */
    private static Serializable computedSolution(boolean serializableProb) {
        Class problemClass = null;
        SerializableProblem problem = null;

        if (serializableProb) {
            try {
                problemClass =
                    ClassLoader.getSystemClassLoader().loadClass(classname);
                problem = (SerializableProblem) problemClass.newInstance();
            } catch (ClassNotFoundException e) {
                System.out.println(
                        "Die zentrale Problemklasse wurde nicht gefunden:"
                        + classname);
                if (debugMode) {
                    e.printStackTrace();
                }
            } catch (InstantiationException e) {
                System.out.println(
                        "Von der angegebenen Problemklasse konnte keine Instanz"
                        + " erzeugt werden!");
                if (debugMode) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                System.out.println(
                    "Der Zugriff auf die Klasse war nicht möglich.\n");
                if (debugMode) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (serializableProb) {
                return transmitter.transmitProblem(problem);
            } else {
                return transmitter.transmitProblem(packageURL, classname, null);
            }
        } catch (ClassNotFoundException e) {
            System.out.println(
                    "Die zentrale Problemklasse wurde nicht gefunden:" + classname);
            if (debugMode) {
                e.printStackTrace();
            }
        } catch (ProblemComputeException e) {
            System.out.println(
                    "Fehler bei Berechnung des Problems: " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            System.out.println(
                    "Fehler bei der Verbindung mit dem Dispatcher.");
            if (debugMode) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Druckt die Benutzung des ProblemUebermittlers auf die Kommandozeile.
     *
     * @param status  Errorcode für die Shell, dabei 0=fehlerfrei
     */
    private static void usage(int status) {
        System.out.println(
                "\n\nProblemUebermittler\n\n"
                + "Benutzung:\n"
                + "ProblemTransmitterImpl -u <package url> -r <computesystem>"
                + " -c <klassenname> -f <dateiname> [-d] [-n] [-p]\n\n"
                + "Die beoetigten Parameter sind:\n"
                + "-u packageURL  Der URL des äußersten Pakets der Klassen"
                + " des Problems\n"
                + "-r ProblemManager  Der Rechner des Computesystems\n"
                + "-c Class  Der Name der Problem-Klasse (mit "
                + "Paketbezeichnungen)\n"
                + "-f Filename  Der Dateiname, unter dem die Lösung "
                + "gespeichert wird\n"
                + "Optional:\n"
                + "-s überträgt das Problem als serialisierbares Problem\n"
                + "-d schaltet den Debug-Modus ein\n"
                + "-n schaltet den graphischen Modus ab\n"
                + "-p schaltet die Allgemeine Statistik ab\n"
                + "\nBeispiel:\n"
                + "ProblemTransmitterApp -u http://localhost/"
                + " -r abteilungsserver:"
                + ProblemManager.PORT_NO
                + " -c meinproblem.Problem -f"
                + " Loesung.txt");
        System.exit(status);
    }

    /**
     * Hiermit kann der <code>ProblemTransmitter</code> auch standalone
     * aufgerufen werden, wobei alle Parameter aus der Kommandozeile
     * entnommen werden.
     *
     * @param args  Kommandozeilenparameter, siehe <code>usage()</code>.
     */
    public static void main(String[] args) {
        ProblemGUIStatisticsReader problemStatisticsReader = null;
        SystemGUIStatisticsReader systemStatisticReader = null;
        ProblemStatistics finalStat = null;
        Serializable solution;
        String problemManagerHost = null;
        String filename = null;
        boolean serializable = false;
        boolean graphicalMode = true;
        boolean onlyProblemGraphical = false;


        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }


        ParameterParser parser = new ParameterParser();

        // switches
        Option serializableSwitch = new Option("s");
        parser.addOption(serializableSwitch);

        Option debugSwitch = new Option("d");
        parser.addOption(debugSwitch);

        Option noGraphicalModeSwitch = new Option("n");
        parser.addOption(noGraphicalModeSwitch);

        Option problemGraphicalSwitch = new Option("p");
        parser.addOption(problemGraphicalSwitch);

        // parameters
        Option urlOption = new Option("u");
        urlOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        urlOption.setOptional(false);
        urlOption.setName("packageURL");
        parser.addOption(urlOption);

        Option classnameOption = new Option("c");
        classnameOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        classnameOption.setOptional(false);
        classnameOption.setName("classname");
        parser.addOption(classnameOption);

        Option filenameOption = new Option("f");
        filenameOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        filenameOption.setOptional(false);
        filenameOption.setName("filename");
        parser.addOption(filenameOption);

        Option problemManagerOption = new Option("r");
        problemManagerOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        problemManagerOption.setOptional(false);
        problemManagerOption.setName("problemManager");
        parser.addOption(problemManagerOption);

        
        try {
            parser.parseAll(args);

            serializable = parser.isEnabled(serializableSwitch);
            debugMode = parser.isEnabled(debugSwitch);
            graphicalMode = !parser.isEnabled(noGraphicalModeSwitch);
            onlyProblemGraphical = parser.isEnabled(problemGraphicalSwitch);

            packageURL = new URL(parser.getParameter(urlOption));
            classname = parser.getParameter(classnameOption);
            filename = parser.getParameter(filenameOption);
            problemManagerHost = parser.getParameter(problemManagerOption);


            // Status ausgeben
            Miscellaneous.printDebugMessage(
                    debugMode,
                    "\n\nStelle nun Verbindung her:\n"
                    + "URL: "
                    + packageURL
                    + "\n"
                    + "Klassenname: "
                    + classname
                    + "\n"
                    + "Computesystem: "
                    + problemManagerHost
                    + "\n"
                    + "Loesung: "
                    + filename
                    + "\n\n"
                    + "Debug-Modus ist AN\n\n");

            if (serializable) {
                // Die folgende Zeile muß gleich am Anfang stehen
                System.setProperty("java.rmi.server.codebase", packageURL.toString());
            }

            try {
                // Erzeugung des Problem-Transmitters
                transmitter = new ProblemTransmitterImpl(problemManagerHost,
                        debugMode);

                // Die graphischen Statistik-Ausgaben starten
                if (graphicalMode) {
                    problemStatisticsReader =
                        new ProblemGUIStatisticsReader(transmitter, problemManagerHost,
                                classname);
                    if (!onlyProblemGraphical) {
                        systemStatisticReader =
                            new SystemGUIStatisticsReader(problemManagerHost);
                    }
                }
            } catch (MalformedURLException e) {
                System.out.println("Eingegebene URL fehlerhaft!");
                if (debugMode) {
                    e.printStackTrace();
                }
                System.exit(1);
            } catch (AccessException e) {
                System.out.println(
                        "Der Zugriff auf den Problem-Manager war nicht erlaubt.\n"
                        + "Vermutlich falsche policy-Datei angegeben.");
                if (debugMode) {
                    e.printStackTrace();
                }
                System.exit(1);
            } catch (RemoteException e) {
                System.out.println(
                "Fehler bei der Verbindung mit dem Dispatcher.");
                if (debugMode) {
                    e.printStackTrace();
                }
                System.exit(1);
            } catch (NotBoundException e) {
                System.out.println(
                "Dieser angegebene problemManagerHost existiert nicht.");
                if (debugMode) {
                    e.printStackTrace();
                }
                System.exit(1);
            }

            if (!graphicalMode) {
                System.out.println("Beginne mit Übertragung des Problems");
            }
            solution = computedSolution(serializable);
            if (solution == null) {
                System.err.println(
                "Fehler! Die zurückgegebene Lösung ist null");
                System.exit(1);
            }

            Miscellaneous.printDebugMessage(
                    debugMode,
            "Schreibe Lösung in Datei");
            Miscellaneous.writeSerializableToFile(solution, filename);
            System.out.println("\nLösung erhalten und geschrieben!");
            System.out.println("Berechnung beeendet!\n");

            // Die letzte Problem-Statistik ausgeben
            finalStat = transmitter.getFinalProblemStat();
            if (finalStat != null) {
                if (graphicalMode) {
                    // außerdem Problem-Statistik-Ausgabe anhalten
                    problemStatisticsReader.stopAndShowFinalStat(finalStat);
                } else {
                    System.out.println("Abschließende Statistik des Problems:\n");
                    System.out.println(finalStat);
                }
            }


        } catch (MalformedURLException ex) {
            System.out.println("Fehler bei: -u");
            usage(1);
        } catch (IOException ex) {
            System.out.println("Fehler bei: " + ex.getMessage());
            usage(1);
        } catch (ParameterParserException ex) {
            System.out.println("Fehler in der Kommandozeile: " + ex.getMessage());
            usage(1);
        }

    }
}
