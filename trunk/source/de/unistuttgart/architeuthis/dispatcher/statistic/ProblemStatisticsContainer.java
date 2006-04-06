/*
 * file:        ProblemStatisticsContainer.java
 * created:     17.08.2003
 * last change: 06.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.dispatcher.statistic;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import de.unistuttgart.architeuthis.userinterfaces.exec.ProblemStatistics;

/**
 * Diese Klasse enthält die Statistik-Werte für die problemspezifische
 * Statistik.
 *
 * @see de.unistuttgart.architeuthis.systeminterfaces.ProblemStatistics
 *
 * @author Andreas Heydlauff, Jürgen Heit, Dietmar Lippold
 */
public class ProblemStatisticsContainer
    implements ProblemStatistics, Cloneable {

    /**
     * Die maximale Zeit in Millisekunden, die noch nicht in Sekunden
     * angezeigt wird.
     */
    private static final long SHOW_MAX_MS_TIME = 10000;

    /**
     * Anzahl der der Operatives, die ein Teilproblem berechnen, wobei
     * mehrfach vergebene Teilprobleme mehrfach zählen.
     */
    long computingOperatives = 0;

    /**
     * Anzahl der angeforderten Teilprobleme, d.h. Anzahl der Aufrufe der
     * Methode <code>Problem.getPartialProblem(long)</code>.
     */
    long requestedPartialProblems = 0;

    /**
     * Anzahl der erzeugten Teilprobleme, d.h. Anzahl der Fälle, in denen
     * die Methode <code>Problem.getPartialProblem(long)</code> nicht
     * <code>null</code> geliefert hat.
     */
    long createdPartialProblems = 0;

    /**
     * Anzahl der in Bearbeitung befindlichen Teilprobleme, d.h. Anzahl der
     * erzeugten Teilprobleme, zu denen noch keine Teillösung geliefert
     * wurde.
     */
    long processingPartialProblems = 0;

    /**
     * Anzahl der in Berechnung befindlichen Teilprobleme, d.h. Anzahl der
     * Teilprobleme, die von mindestens einem Operatives gerade berechnet
     * werden.
     */
    long computingPartialProblems = 0;

    /**
     * Anzahl der berechneten Teilprobleme, d.h. Anzahl der Teilprobeme, zu
     * denen ein Operative eine Teillösung geliefert hat.
     */
    long computedPartialProblems = 0;

    /**
     * Anzahl der fertig bearbeiteten Teilprobleme, d.h. Anzahl der Aufrufe
     * von <code>Problem.collectPartialSolution(PartialSolution, PartialProblem)</code>.
     */
    long processedPartialProblems = 0;

    /**
     * Anzahl der bei der Bearbeitung abgebrochenen Teilprobleme, d.h. Anzahl
     * der Teilprobleme, für die das konkrete Problem keine Teillösung
     * erhalten hat.
     */
    long abortedPartialProblems = 0;

    /**
     * Gesamte Zeit der Berechnung aller fertig berechneten Teilprobleme.
     */
    long computationDuration = 0;

    /**
     * Alter des Problems.
     */
    long problemAge = 0;

    /**
     * Liefert die Anzahl der Operatives, die ein Teilproblem berechnen, wobei
     * mehrfach vergebene Teilprobleme mehrfach zählen.
     *
     * @return  Anzahl der der Operatives, die ein Teilproblem berechnen.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.ProblemStatistics#getComputingOperatives()
     */
    public long getComputingOperatives() {
        return computingOperatives;
    }

    /**
     * Liefert die Anzahl der angeforderten Teilprobleme, d.h. die Anzahl der
     * Aufrufe der Methode <code>Problem.getPartialProblem(long)</code>.
     *
     * @return  Anzahl der angeforderten Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.ProblemStatistics#getRequestedPartialProblems()
     */
    public long getRequestedPartialProblems() {
        return requestedPartialProblems;
    }

    /**
     * Liefert die Anzahl der erzeugten Teilprobleme, d.h. die Anzahl der
     * Fälle, in denen die Methode <code>Problem.getPartialProblem(long)</code>
     * nicht <code>null</code> geliefert hat.
     *
     * @return  Anzahl der erzeugten Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getCreatedPartialProblems()
     */
    public long getCreatedPartialProblems() {
        return createdPartialProblems;
    }

    /**
     * Liefert die Anzahl der in Bearbeitung befindlichen Teilprobleme, d.h.
     * die Anzahl der erzeugten Teilprobleme, zu denen noch nicht die Methode
     * <code>Problem.collectPartialSolution(PartialSolution, PartialProblem)</code>
     * aufgerufen wurde.
     *
     * @return  Anzahl der in Bearbeitung befindlichen Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getProcessingPartialProblems()
     */
    public long getProcessingPartialProblems() {
        return processingPartialProblems;
    }

    /**
     * Liefert die Anzahl der in Berechnung befindlichen Teilprobleme, d.h.
     * die Anzahl der unterschiedlichen Teilprobleme, die von mindestens einem
     * Operatives gerade berechnet werden.
     *
     * @return  Anzahl der in Berechnung befindlichen Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getComputingPartialProblems()
     */
    public long getComputingPartialProblems() {
        return computingPartialProblems;
    }

    /**
     * Liefert die Anzahl der berechneten Teilprobleme, d.h. Anzahl der
     * Teilprobeme, zu denen ein Operative eine Teillösung geliefert hat.
     *
     * @return  Anzahl der berechneten Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getComputedPartialProblems()
     */
    public long getComputedPartialProblems() {
        return computedPartialProblems;
    }

    /**
     * Liefert die Anzahl der fertig bearbeiteten Teilprobleme, d.h. Anzahl der
     * Aufrufe von <code>Problem.collectPartialSolution(PartialSolution, PartialProblem)</code>.
     *
     * @return  Anzahl der fertig bearbeiteten Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getProcessedPartialProblems()
     */
    public long getProcessedPartialProblems() {
        return processedPartialProblems;
    }

    /**
     * Liefert die Anzahl der bei der Bearbeitung abgebrochenen Teilprobleme,
     * d.h. die Anzahl der Teilprobleme, für die das konkrete Problem keine
     * Teillösung erhalten hat.
     *
     * @return  Anzahl der bei der Bearbeitung abgebrochenen Teilprobleme.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getAbortedPartialProblems()
     */
    public long getAbortedPartialProblems() {
        return abortedPartialProblems;
    }

    /**
     * Liefert die durchschnittliche Berechnungszeit aller fertig berechneten
     * Teilprobleme zurück. Gestartet wird vor dem Senden eines Teilproblems
     * und gestoppt nach dem Empfang der ersten Teillösung zu diesem
     * Teilproblem. Rechenzeit, die durch Mehrfachverteilung anfällt, wird
     * nicht berücksichtigt.
     *
     * @return  durchschnittliche Zeitdauer in Millisekunden.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getAverageComputationDuration()
     */
    public long getAverageComputationDuration() {
        if (computedPartialProblems == 0) {
            return 0;
        } else {
            return computationDuration / computedPartialProblems;
        }
    }

    /**
     * Liefert die gesamte Berechnungszeit aller fertig berechneten Teilprobleme
     * zurück. Gestartet wird vor dem Senden eines Teilproblems und gestoppt
     * nach dem Empfang der ersten Teillösung zu diesem Teilproblem. Rechenzeit,
     * die durch Mehrfachverteilung anfällt, wird nicht berücksichtigt.
     *
     * @return  gesamte Zeitdauer in Millisekunden
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.AbstractStatistics#getTotalComputationDuration()
     */
    public long getTotalComputationDuration() {
        return computationDuration;
    }

    /**
     * Liefert die Zeitdauer seit dem Empfang des Problems.
     *
     * @return  Zeitdauer in Millisekunden.
     *
     * @see de.unistuttgart.architeuthis.systeminterfaces.ProblemStatistics#getProblemAge()
     */
    public long getProblemAge() {
        return problemAge;
    }

    /**
     * Liefert eine Kopie dieses Objekts.
     *
     * @return  eine Kopie dieses Objekts.
     */
    public Object clone() {
        Object copy = null;

        try {
            copy = super.clone();
        } catch (CloneNotSupportedException e) {
            // CloneNotSupportedException kann nicht auftreten
        }
        return copy;
    }

    /**
     * Liefert einen Text mit der Werten der Statistik.
     *
     * @return  ein Text mit der Werten der Statistik.
     */
    public String toString() {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss", Locale.GERMAN);
        String text;
        long timeAverageComputationMs = 0;
        long timeAverageComputationSek = 0;
        long timeAverageComputationPartHh = 0;
        long timeAverageComputationPartMin = 0;
        long timeAverageComputationPartSek = 0;
        long timeTotalComputationSek = 0;
        long timeTotalComputationPartHh = 0;
        long timeTotalComputationPartMin = 0;
        long timeTotalComputationPartSek = 0;
        long timeProblemAgeSek = 0;
        long timeProblemAgePartHh = 0;
        long timeProblemAgePartMin = 0;
        long timeProblemAgePartSek = 0;

        text = "Teilprobleme in Berechnung  : "
               + getComputingPartialProblems()
               + "\n"
               + "Teilprobleme in Bearbeitung : "
               + getProcessingPartialProblems()
               + "\n"
               + "Erzeugte Teilprobleme       : "
               + getCreatedPartialProblems()
               + "\n"
               + "Abgefragte Teilprobleme     : "
               + getRequestedPartialProblems()
               + "\n"
               + "Berechnete Teilprobleme     : "
               + getComputedPartialProblems()
               + "\n"
               + "Bearbeitete Teilprobleme    : "
               + getProcessedPartialProblems()
               + "\n"
               + "Abgebrochene Teilprobleme   : "
               + getAbortedPartialProblems()
               + "\n\n"
               + "Berechnende Operatives      : "
               + getComputingOperatives()
               + "\n\n";

        text += "Durchschnittl. Zeit für ein TP: ";
        timeAverageComputationMs = getAverageComputationDuration();
        if (timeAverageComputationMs > SHOW_MAX_MS_TIME) {
            timeAverageComputationSek = timeAverageComputationMs / 1000;
            timeAverageComputationPartHh = timeAverageComputationSek / 3600;
            timeAverageComputationPartMin = timeAverageComputationSek / 60 % 60;
            timeAverageComputationPartSek = timeAverageComputationSek % 60;
            text += timeAverageComputationPartHh
                    + "h "
                    + timeAverageComputationPartMin
                    + "m "
                    + timeAverageComputationPartSek
                    + "s";
        } else {
            text += timeAverageComputationMs + "ms";
        }
        text += "\n";

        text += "Berechnungszeit für alle TP   : ";
        timeTotalComputationSek = getTotalComputationDuration() / 1000;
        timeTotalComputationPartHh = timeTotalComputationSek / 3600;
        timeTotalComputationPartMin = timeTotalComputationSek / 60 % 60;
        timeTotalComputationPartSek = timeTotalComputationSek % 60;
        text += timeTotalComputationPartHh
                + "h "
                + timeTotalComputationPartMin
                + "m "
                + timeTotalComputationPartSek
                + "s";
        text += "\n\n";

        text += "Problem auf Dispatcher seit   : ";
        timeProblemAgeSek = getProblemAge() / 1000;
        timeProblemAgePartHh = timeProblemAgeSek / 3600;
        timeProblemAgePartMin = timeProblemAgeSek / 60 % 60;
        timeProblemAgePartSek = timeProblemAgeSek % 60;
        text += timeProblemAgePartHh
                + "h "
                + timeProblemAgePartMin
                + "m "
                + timeProblemAgePartSek
                + "s";
        text += "\n\n";

        text += "Statistik empfangen um " + fmt.format(new Date());
        text += "\n";

        return text;
    }
}

