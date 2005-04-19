/*
 * filename:    OperativeComputing.java
 * created:     26.04.2004
 * last change: 19.04.2005 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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


package de.unistuttgart.architeuthis.operative;

import de.unistuttgart.architeuthis.misc.Miscellaneous;
import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.CommunicationPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.NonCommPartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.RemoteStore;

/**
 * Implementierung den Thread zur Berechnung eines Teilproblems.
 *
 * @author J�rgen Heit, Ralf Kible, Dietmar Lippold, Michael Wohlfart
 */
public class OperativeComputing extends Thread {

    /**
     * Das Objekt, das als Operative die Kommunikation mit dem Dispatcher
     * durchf�hrt.
     */
    private OperativeImpl operativeImpl;

    /**
     * Das aktuell berechnete Teilproblem, oder <code>null</code>, falls
     * keines berechnet wird.
     */
    private volatile PartialProblem partialProblem = null;

    /**
     * Dieses Flag schaltet zus�tzliche Debug-Meldungen ein.
     */
    private boolean debugMode = true;

    /**
     * Der verwendete RemoteStore. Das ist entweder ein zentraler RemoteStore
     * oder ein dezentraler RemoteStore oder <CODE>null</CODE>, falls kein
     * RemoteStoreverwendet wird
     */
    private RemoteStore store;

    /**
     * Dieser Konstruktor sollte nicht benutzt werden, muss aber wegen
     * der Ableitung von <code>UnicastRemoteObject</code> �berschrieben
     * werden.
     *
     * @param debug          Flag f�r debug-Meldungen.
     * @param operativeImpl  Die OperativeImpl Implementierung, die diesen
     *                       OperativeComputing verwendet um Berechnungen
     *                       durchzuf�hren.
     */
    OperativeComputing(OperativeImpl operativeImpl, boolean debug) {
        this.operativeImpl = operativeImpl;
        debugMode = debug;
        start();
    }

    /**
     * Wird vom <code>OperativeImpl</code> aufgerufen, um das �bergebenen
     * Teilproblem berechnen zu lassen.
     *
     * @param parProb  Neues Teilproblem f�r den Operative.
     * @param store    Der RemoteStore f�r das Teilproblem.
     *
     * @throws ProblemComputeException  Wenn der Thread bereits ein Teilproblem
     *                                  berechnet.
     */
    synchronized void fetchPartialProblem(PartialProblem parProb,
                                          RemoteStore store)
        throws ProblemComputeException {
        /*
         * note: Um die h�sslichen instanceof-Tests in der run() Methode
         *       loszuwerden, k�nnte man aus dieser Methode zwei Methoden
         *       machen, die jeweils f�r CommunicationPartialProblem und
         *       f�r NonCommPartialProblem implementiert werden.
         *       Allerdings w�rde das die instanceof Tests wohl nur in die
         *       OperativeImpl Klasse verschieben... (MW)
         */

        Miscellaneous.printDebugMessage(debugMode,
                                        "Debug: OperativeComputing hat Aufgabe"
                                        + " vom ComputeManager empfangen.");
        Miscellaneous.printDebugMessage(debugMode,
                                        "Debug: der RemoteStore ist " + store);
        if (partialProblem == null) {
            partialProblem = parProb;
            this.store = store;
            notifyAll();
        } else {
            throw new ProblemComputeException("OperativeComputing bereits"
                                              + " besch�ftigt");
        }
    }

    /**
     * Ermittelt, ob der Thread gerade ein Teilproblem berechnet.
     *
     * @return <code>true</code>, falls der Operative ein Teilproblem berechnet,
     *         sonst <code>false</code>.
     */
    boolean isComputing() {
        return (partialProblem != null);
    }

    /**
     * Wartet auf ein Teilproblem und ruft, wenn dieses vorhanden ist, dessen
     * compute()-Methode auf. Anschlie�end wird <code>partialProblem</code>
     * der Wert <code>null</code> zugewiesen und die L�sung an die Instanz
     * von <code>OperativeImpl</code> �bergeben. Tritt bei der Berechnung ein
     * Fehler auf, wird <code>partialProblem</code> ebenfalls der Wert
     * <code>null</code> zugewiesen und der Fehler an die Instanz von
     * <code>OperativeImpl</code> weitergemeldet. Anschie�end wartet die
     * Methode wieder auf das n�chste Teilproblem.
     */
    public void run() {
        PartialSolution ps = null;

        while (true) {
            synchronized (this) {
                if (partialProblem == null) {
                    // Auf Teilproblem warten
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // Wenn ein Teilproblem zu bearbeiten ist, die
                        // Berechnung beginnen.
                    }
                }
            }

            ps = null;
            try {
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Starte Berechnung");

                if (partialProblem instanceof NonCommPartialProblem) {
                    ps = ((NonCommPartialProblem)partialProblem).compute();
                } else if (partialProblem instanceof CommunicationPartialProblem) {
                    ps = ((CommunicationPartialProblem)partialProblem).compute(store);
                } else {
                    // Fehler �ber den OperativeImpl an den Dispatcher weitergeben:
                    System.err.println("Error: PartialProblem implementiert"
                                       + " kein passendes Interface");
                    operativeImpl.reportException(
                        ExceptionCodes.PARTIALPROBLEM_ERROR,
                        "PartialProblem implementiert kein passendes Interface");

                    // Zum Teilproblem kann keine Teill�sung berechnet werden
                    partialProblem = null;
                }

                if (partialProblem != null) {
                    Miscellaneous.printDebugMessage(
                        debugMode,
                        "Debug: Berechnung beendet");
                    partialProblem = null;
                    // L�sung zur�ckgeben
                    operativeImpl.returnPartialSolution(ps);
                }
            } catch (ProblemComputeException e) {
                partialProblem = null;
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: ProblemComputeException ist aufgetreten : " + e);
                operativeImpl.reportException(
                    ExceptionCodes.PARTIALPROBLEM_ERROR, e.toString());
            } catch (RuntimeException e) {
                partialProblem = null;
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: RuntimeException ist aufgetreten : " + e);
                operativeImpl.reportException(
                    ExceptionCodes.PARTIALPROBLEM_ERROR, e.toString());
            } catch (ThreadDeath e) {
                // Dieser Error darf nicht abgefangen werden.
                throw e;
            } catch (Throwable e) {
                partialProblem = null;
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Error ist aufgetreten : " + e);
                operativeImpl.reportException(
                    ExceptionCodes.PARTIALPROBLEM_ERROR, e.toString());
            }
        }
    }
}

