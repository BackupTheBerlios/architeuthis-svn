/*
 * file:        SystemTextStatisticsReader.java
 * last change: 16.06.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
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


package de.unistuttgart.architeuthis.user;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.security.AccessControlException;

import de.unistuttgart.architeuthis.systeminterfaces.ProblemManager;
import de.unistuttgart.architeuthis.userinterfaces.exec.SystemStatistics;

/**
 * Klasse, die eine System-Statistik von einem ProblemManager abfragt und
 * über die Standard-Ausgabe ausgibt.
 *
 * @author Ralf Kible, Andreas Heydlauff, Dietmar Lippold
 */
public class SystemTextStatisticsReader {

    /**
     * Erwartet die Adresse des ComputeSystems als Kommandozeilen-Argument und
     * gibt die allgemeine Statistik des ComputeSystems auf der Konsole aus.
     *
     * @param args  Die Kommandozeilenparameter: Erwartet als einziges Argument
     *              die Adresse des ComputeSystems
     */
    public static void main(String[] args) {
        ProblemTransmitterImpl problemTransmitter;
        SystemStatistics sysStat;
        String compSys = null;

        if (args.length != 1) {
            System.err.println(
                "Fehler: Als Kommandozeilenargument muss die Adresse des\n"
                    + "Computesystems eingegeben werden. Beispiel:\n"
                    + "java SystemTextStatisticsReader meinSystem.de:"
                    + ProblemManager.PORT_NO);
            System.exit(1);
        } else {
            compSys = args[0];
        }

        try {
            problemTransmitter = new ProblemTransmitterImpl(compSys, false);
            sysStat = problemTransmitter.getSystemStatistic();
            System.out.println("\n\n" + "Statistik des ComputeSystems auf "
                               + args[0] + "\n\n"
                               + sysStat + "\n");
        } catch (MalformedURLException e) {
            System.err.println("Eingegebene URL fehlerhaft!");
            e.printStackTrace();
        } catch (AccessException e) {
            System.err.println("Rechte für Zugriff auf Registry nicht vorhanden!");
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println("Der eingegebene Dispatcher ist nicht bekannt!");
            e.printStackTrace();
        } catch (RemoteException e) {
            System.err.println("Fehler bei RMI-Verbindung!");
            e.printStackTrace();
        }
    }
}
