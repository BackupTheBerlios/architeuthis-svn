/*
 * file:        HashStoreMain.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 12.04.2005 von Dietmar Lippold
 * developers:  Michael Wohlfart, michael.wohlfart@zsw-bw.de
 *
 * This software was developed at the Institute for Intelligent Systems at the
 * University of Stuttgart (http://www.iis.uni-stuttgart.de/) under leadership
 * of Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de).
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
 */


package de.unistuttgart.architeuthis.testenvironment.hashstore;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.user.ProblemComputation;
import de.unistuttgart.architeuthis.remotestore.hashmap.RemoteHashMapGenerator;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;

/**
 * Die Testanwendung, die Architeuthis verwendet.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public final class HashStoreMain {

    /**
     * Die Anzahl der zu erzeugenden Put-Teilprobleme.
     */
    private static int putCreateNr = 1;

    /**
     * Gibt an, ob nur ein zentraler RemoteStore erzeugt werden soll.
     */
    private static boolean centralOnly = true;

    /**
     * Gibt an, ob die dezentralen RemoteStores Methoden des zentralen
     * RemoteStore synchron aufrufen sollen.
     */
    private static boolean syncron;

    /**
     * Konstruktur, der nicht aufgerufen werden soll.
     */
    private HashStoreMain() {
    }

    /**
     * Die ausführbare Methode. Übergibt Architeuthis ein Problem und gibt
     * nach Erhalt die Lösung aus. Wenn nicht angegeben wird, wie viele
     * Teilprobleme erzeugt werden sollen, wird eines erzeugt. Wenn als
     * vierter Parameter <KBD>true</KBD> oder <KBD>false</KBD> angegeben
     * wird, wird ruften die dezentralen RemoteStores die Methoden des
     * zentralen synchron bzw. asynchron auf. Wenn weder das eine noch das
     * andere angegeben wird, gibt es nur einen zentralen RemoteStore.
     *
     * @param args  Die Codebase und die Angabe des Dispatchers sowie optional
     *              die Anzahl der zu erzeugenden Put-Teilprobleme.
     */
    public static void main(String[] args) {
        ProblemComputation computation;
        HashStoreProblemImpl problem;
        RemoteHashMapGenerator generator;
        Serializable solution = null;

        if ((args.length < 2) || (args.length > 4)) {
            System.out.println("Bitte Codebase-URL und Dispather sowie"
                               + " optional die Anzahl der Put-Teilprobleme"
                               + " und die Art der Aufrufe als Argumente"
                               + " angeben");
        } else {
            String codebase = args[0];
            String dispatcher = args[1];
            if (args.length >= 3) {
                putCreateNr = Integer.parseInt(args[2]);
            }
            if ((args.length == 4)
                    && (args[3].equals("true") || args[3].equals("false"))) {
                centralOnly = false;
                syncron = args[3].equals("true");
            }

            try {
                // Archi User-Interface.
                computation = new ProblemComputation();

                // Ein Problem.
                problem = new HashStoreProblemImpl(putCreateNr);

                // Ein RemoteStore-Generator.
                if (centralOnly) {
                    generator = new RemoteHashMapGenerator();
                } else {
                    generator = new RemoteHashMapGenerator(syncron);
                }

                System.out.println("Problem wird abgeschickt");

                // Problem abschicken und auf Lösung warten.
                solution = computation.transmitProblem(problem, generator,
                                                       dispatcher, codebase);

                // Lösung ausgeben.
                System.out.println("Ermittelte Lösung: " + solution);

            } catch (ProblemComputeException ex) {
                ex.printStackTrace();
            } catch (AccessException ex) {
                ex.printStackTrace();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (NotBoundException ex) {
                ex.printStackTrace();
            }
        }
    }
}

