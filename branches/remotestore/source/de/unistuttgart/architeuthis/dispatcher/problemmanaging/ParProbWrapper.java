/*
 * file:        ParProbWrapper.java
 * last change: 06.04.2005 by Dietmar Lippold
 * developers:  Dietmar Lippold, dietmar.lippold@informatik.uni-stuttgart.de
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


package de.unistuttgart.architeuthis.dispatcher.problemmanaging;


import de
    .unistuttgart
    .architeuthis
    .dispatcher
    .statistic
    .ProblemStatisticsCollector;
import de.unistuttgart.architeuthis.misc.Numerator;
import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;

/**
 * Wrapper für ein <CODE>PartialProblem</CODE>-Objekt.
 *
 * @author Dietmar Lippold
 */
public class ParProbWrapper {

    /**
     * Ein Zähler für die Anzahl der erzeugten Instanzen diesert Klasse.
     */
    private static Numerator parProbIdNumerator = new Numerator();

    /**
     * Das in diser Instanz gespeicherte <CODE>PartialProblem</CODE>-Objekt.
     */
    private PartialProblem partialProblem = null;

    /**
     * Der <CODE>ProblemWrapper</CODE>, der dieses Objekt erzeugt hat.
     */
    private ProblemWrapper creatingWrapper;

    /**
     * Die Statistik zum Problem des in diser Instanz gespeicherten
     * <CODE>PartialProblem</CODE>-Objekts.
     */
    private ProblemStatisticsCollector problemStatisticCollector = null;

    /**
     * Eine Nummer, die dieses Objekt und das darin gespeicherte Teilproblem
     * eindeutig kennzeichnet.
     */
    private long partialProblemId;

    /**
     * Erzeugt eine neue Instanz mit dem übergebenen
     * <CODE>PartialProblem</CODE>.
     *
     * @param partialProblem    Das konkrete Teilproblem.
     * @param creatingWrapper   Der <CODE>ProblemWrapper</CODE>, der dieses
     *                          Objekt erzeugt hat.
     * @param problemStatisticCollector  Die zum Problem vom <CODE>partialProblem</CODE>
     *                          gehörende Statistik.
     */
    ParProbWrapper(PartialProblem partialProblem,
                   ProblemWrapper creatingWrapper,
                   ProblemStatisticsCollector problemStatisticCollector) {
        this.partialProblem = partialProblem;
        this.creatingWrapper = creatingWrapper;
        this.problemStatisticCollector = problemStatisticCollector;
        partialProblemId = parProbIdNumerator.nextNumber();
    }

    /**
     * Liefert das enthaltene Teilproblem.
     *
     * @return  Das konkrete Teilproblem.
     */
    public PartialProblem getPartialProblem() {
        return partialProblem;
    }

    /**
     * Liefert den <CODE>ProblemWrapper</CODE>, der dieses Objekt erzeugt hat.
     *
     * @return  Den <CODE>ProblemWrapper</CODE>, der dieses Objekt erzeugt
     *          hat.
     */
    public ProblemWrapper getCreatingWrapper() {
        return creatingWrapper;
    }

    /**
     * Liefert die Statistic zum Problem des enthaltenen Teilproblems.
     *
     * @return  Die Statistik zum Teilproblem.
     */
    public ProblemStatisticsCollector getProblemStatisticCollector() {
        return problemStatisticCollector;
    }

    /**
     * Liefert den zentralen RemoteStore zum Problem.
     *
     * @return  Den zentralen RemoteStore zum Problem.
     */
    public RemoteStore getCentralRemoteStore() {
        return creatingWrapper.getCentralRemoteStore();
    }

    /**
     * Liefert den RemoteStoreGenerator zum Problem.
     *
     * @return  Den für diese Problem verwendeten RemoteStoreGenerator.
     */
    public RemoteStoreGenerator getRemoteStoreGenerator() {
        return creatingWrapper.getRemoteStoreGenerator();
    }

    /**
     * Liefert einen Text, der das enthaltene Teilproblem kennzeichnet.
     *
     * @return  Einen Text, der das enthaltene Teilproblem kennzeichnet.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "PartialProblem " + partialProblemId
                                 + " von " + creatingWrapper;
    }
}

