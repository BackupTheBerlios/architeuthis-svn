/*
 * file:        ExceptionCodes.java
 * created:     18.01.2004
 * last change: 06.04.2006 by Dietmar Lippold
 * developers:  Andreas Heydlauff, AndiHeydlauff@gmx.de
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


package de.unistuttgart.architeuthis.systeminterfaces;

/**
 * Diese Klasse enthält Nachrichten-Code Konstanten für Fehlermeldungen, die im
 * Compute-System generiert werden.
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public class ExceptionCodes {

    /**
     * Signalisiert, dass bei der Erstellung eines Teilproblems eine Ausnahme
     * auftrat. Der Grund für diese Ausnahme liegt wahrscheinlich an der Klasse,
     * die {@link Problem#getPartialProblem(long)} implementiert.
     */
    public static final int PARTIALPROBLEM_CREATE_EXCEPTION = 0;

    /**
     * Signalisiert, dass beim Verarbeiten einer Teillösung eine Ausnahme
     * auftrat. Der Grund für diese Ausnahme liegt wahrscheinlich an der Klasse,
     * die {@link Problem#collectPartialSolution(PartialSolution, PartialProblem)}
     * implementiert.
     */
    public static final int PARTIALSOLUTION_COLLECT_EXCEPTION = 1;

    /**
     * Signalisiert, dass bei der Erstellung der Gesamtlösung eine Ausnahme
     * auftrat. Der Grund für diese Ausnahme liegt wahrscheinlich an der Klasse,
     * die {@link Problem#getSolution()} implementiert.
     */
    public static final int SOLUTION_CREATE_EXCEPTION = 2;

    /**
     * Signalisiert, dass das Senden eines Teilproblems nicht erfolgen konnte.
     */
    public static final int PARTIALPROBLEM_SEND_EXCEPTION = 3;

    /**
     * Signalisiert, dass das Senden einer Teillösung nicht erfolgen konnte.
     */
    public static final int PARTIALSOLUTION_SEND_EXCEPTION = 4;

    /**
     * Signalisiert, dass das Senden der Gesamtlösung nicht erfolgen konnte.
     */
    public static final int SOLUTION_SEND_EXCEPTION = 5;

    /**
     * Signalisiert, dass das Teilproblem bei der Berechnung eine
     * {@link ProblemComputeException} geworfen hat. Der Grund für diese
     * Ausnahme liegt wahrscheinlich an der Klasse, die
     * {@link PartialProblem#compute()} implementiert.
     */
    public static final int PARTIALPROBLEM_COMPUTE_EXCEPTION = 6;

    /**
     * Signalisiert, dass ein <CODE>RemoteStore</CODE> nicht erzeugt werden
     * konnte.
     */
    public static final int REMOTE_STORE_GEN_EXCEPTION = 7;

    /**
     * Signalisiert, dass <CODE>RemoteStore</CODE> nicht angemeldet,
     * abgemeldet oder beendet werden konnte.
     */
    public static final int REMOTE_STORE_EXCEPTION = 8;

    /**
     * Signalisiert, dass bei der Berechnung eines Teilproblems eine
     * Runtime-Exception auftrat. Der Grund für diese Ausnahme liegt
     * wahrscheinlich an der Klasse, die {@link PartialProblem#compute()}
     * implementiert.
     */
    public static final int PARTIALPROBLEM_ERROR = 9;

    /**
     * Signalisiert, dass das Problem einerseites keine Teilprobleme mehr
     * bereitstellt und andererseits keine Gesamtlösung liefert.
     */
    public static final int PROBLEM_INCORRECT_ERROR = 10;

    /**
     * Signalisiert, dass der ComputeManager heruntergefahren wird und alle
     * Probleme abgebrochen werden.
     */
    public static final int DISPATCHER_SHUTDOWN = 11;

    /**
     * Signalisiert, dass keine Operatives am System angemeldet sind.
     */
    public static final int NO_OPERATIVES_REGISTERED = 12;

    /**
     * Signalisiert, dass sich wieder ein Operative am System angemeldet hat,
     * nachdem zuvor keiner mehr angemeldet war.
     */
    public static final int NEW_OPERATIVES_REGISTERED = 13;

    /**
     * Signalisiert, dass der Benutzter sein Problem abgebrochen hat.
     */
    public static final int USER_ABORT_PROBLEM = 14;
}

