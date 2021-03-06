/*
 * filename:    OperativeComputing.java
 * created:     26.04.2004
 * last change: 21.06.2004 by Dietmar Lippold
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

import de.unistuttgart.architeuthis.systeminterfaces.ExceptionCodes;
import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.misc.Miscellaneous;

/**
 * Implementierung den Thread zur Berechnung eines Teilproblems.
 *
 * @author J�rgen Heit, Ralf Kible, Dietmar Lippold
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
     * Dieser Konstruktor sollte nicht benutzt werden, muss aber wegen
     * der Ableitung von <code>UnicastRemoteObject</code> �berschrieben
     * werden.
     *
     * @throws RemoteException  bei RMI-Verbindungsproblemen
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
     * @param parProb  Neues Teilproblem f�r den Operative
     *
     * @throws ProblemComputeException  wenn der Thread bereits ein Teilproblem
     *                                  berechnet.
     */
    synchronized void fetchPartialProblem(PartialProblem parProb)
        throws ProblemComputeException {

        Miscellaneous.printDebugMessage(
            debugMode,
            "Debug: OperativeComputing hat Aufgabe vom ComputeManager"
                + " empfangen.");
        if (partialProblem == null) {
            partialProblem = parProb;
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
                    }
                }
            }

            ps = null;
            try {
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Berechnung gestartet");
                ps = partialProblem.compute();
                Miscellaneous.printDebugMessage(
                    debugMode,
                    "Debug: Berechnung beendet");
                partialProblem = null;
                operativeImpl.returnPartialSolution(ps);
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
            } catch (Error e) {
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
