/*
 * file:        PartialProblemCollector.java
 * created:     18.01.2004
 * last change: 08.10.2004 by Dietmar Lippold
 * developer:   Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@t-online.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
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


package de.unistuttgart.architeuthis.dispatcher.problemmanaging;

/**
 * Ruft vom
 * {@link de.unistuttgart.architeuthis.interfaces.ProblemManager} die jeweils
 * nächsten Teilproblem-Wrapper ab und speichert diese in einer Schlange.
 *
 * @author Jürgen Heit, Dietmar Lippold
 */
class PartialProblemCollector extends Thread {

    /**
     * Flag um Puffer-Füll-Thread zu beenden.
     */
    private volatile boolean terminated = false;

    /**
     * Referenz auf den
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ProblemManager}
     */
    private ProblemManagerImpl problemManager;

    /**
     * Teilproblempuffer
     */
    private BlockingBuffer parProbWrapperBuffer;

    /**
     * Erzeugt eine neue Instanz.
     *
     * @param problemManager       Der ProblemManager, von dem die Teilprobleme geholt
     *                      werden.
     * @param parProbWrapperBuffer  Ein Buffer, in die die geholten Teilprobleme
     *                      eingestellt werden.
     * {@link de.unistuttgart.architeuthis.systeminterfaces.ProblemManager}
     */
    public PartialProblemCollector(ProblemManagerImpl problemManager,
                                   BlockingBuffer parProbWrapperBuffer) {
        super();
        this.problemManager = problemManager;
        this.parProbWrapperBuffer = parProbWrapperBuffer;
        start();
    }

    /**
     * Holt neue Teilproblem-Wrapper und legt sie im Puffer ab. Wenn aktuell
     * kein neuer Teilproblem-Wrapper erhältich ist, wird der Wert <code>null</code>
     * im Puffer abgelegt. Nach dem Ablegen von <code>null</code> werden beim
     * nächsten Teilproblem, das nicht <code>null</code> ist, alle Wert
     * <code>null</code> aus dem Puffer gelöscht und die passiven Operatives
     * reaktiviert.
     */
    public void collectPartialProblems() {
        boolean terminate = false;
        ParProbWrapper lastParProbWarp = null;
        ParProbWrapper partProbWrap;

        while (!terminated) {
            partProbWrap = problemManager.collectParProbWrapper();
            if ((partProbWrap != null) && (lastParProbWarp == null)) {
                // null-Objekte aus Teilproblem-Puffer entfernen
                parProbWrapperBuffer.removeNullElements();
                //  Passive Operatives reaktivieren
                problemManager.reactivatePassiveOperatives();
            }
            parProbWrapperBuffer.enqueue(partProbWrap);
            lastParProbWarp = partProbWrap;
        }
        parProbWrapperBuffer.clear();
    }

    /**
     * Signalisiert dem Thread, der den Teilproblem-Puffer auffüllt, sich zu
     * beenden. Diese Methode wird bei der Beendigung des ComputeManagers
     * aufgerufen.
     */
    public void terminate() {
        terminated = true;
    }

    /**
     * Startet den Thread zum Holen neuer Teilprobleme.
     */
    public void run() {
        collectPartialProblems();
    }
}
