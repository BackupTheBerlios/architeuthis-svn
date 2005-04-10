/*
 * file:        HashStoreProblemImpl.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 10.04.2005 von Dietmar Lippold
 * developers:  Michael Wohlfart michael.wohlfart@zsw-bw.de
 *              Dietmar Lippold,  dietmar.lippold@informatik.uni-stuttgart.de
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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.unistuttgart.architeuthis.abstractproblems.ContainerPartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * Erzeugt zwei Teilprobleme zur Benutzung eines <CODE>RemoteStore</CODE>. Das
 * erste Teilproblem legt ein Objekt in den RemoteStore und das zweite ruft es
 * daraus wieder ab. Das zweite Teilproblem wird erst geliefert, wenn das
 * erste beendet ist.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class HashStoreProblemImpl implements SerializableProblem {

    /**
     * Logger für diese Klasse.
     */
    private static final Logger LOGGER = Logger.getLogger(HashStoreProblemImpl.class.getName());

    /**
     * Generierte SerialVersionUID. Diese muss geändert werden, sobald
     * strukurelle Änderungen an dieser Klasse durchgeführt worden sind.
     */
    private static final long serialVersionUID = 3258135769033094193L;

    /**
     * Key-Objekt für den <CODE>RemoteStore</CODE>.
     */
    private static final String KEY = "key";

    /**
     * Value-Objekt für den <CODE>RemoteStore</CODE>.
     */
    private static final  String LOESUNG = "loesung";

    /**
     * Die ermttelte Lösung.
     */
    private Serializable solution = null;

    /**
     * <CODE>PartialProblem</CODE>, das die Lösung in den RemoteStore ablegt.
     */
    private HashStorePut put = new HashStorePut(KEY, LOESUNG);

    /**
     * <CODE>PartialProblem</CODE>, das die Lösung aus dem RemoteStore abruft.
     */
    private HashStoreGet get = new HashStoreGet(KEY);

    /**
     * Flag, das anzeigt, ob <CODE>HashStorePut</CODE> bereits vergeben wurde.
     */
    private  boolean putDelivered = false;

    /**
     * Flag, das anzeigt, ob <CODE>HashStorePut</CODE> bereits bearbeitet
     * wurde.
     */
    private  boolean putReturned = false;

    /**
     * Flag, das anzeigt, ob <CODE>HashStoreGet</CODE> bereits vergeben wurde.
     * Dies kann erst auf <CODE>true</CODE> gesetzt werden, wenn das
     * Teilproblem <CODE>HashStorePut</CODE> bearbeitet wurde.
     */
    private  boolean getDelivered = false;

    /**
     * Erzeugt die Teilprobleme.
     *
     * @param number  Anzahl zu generierender Teilprobleme. Wird nicht
     *                verwendet.
     *
     * @return  Das erzeugte Teilproblem oder <CODE>null</CODE>, wenn keines
     *          erzeugt wurde.
     */
    public PartialProblem getPartialProblem(long number) {
        if (!putDelivered) {
            putDelivered = true;
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("sent put");
            }
            return put;
        }

        if (putReturned && (!getDelivered)) {
            getDelivered = true;
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("sent get");
            }
            return get;
        }

        return null;
    }

    /**
     * Nimmt eine Teillösung entgegen. Wenn es sich dabei um die Teillösung
     * des get-Teilproblems handelt, wird sie als Gesamtlösung verwendet.
     *
     * @param parProb  Das Teilproblem, zu dem die Teillösung geliefert
     *                 wird.
     * @param parSol   Die gelieferte Teillösung.
     */
    public void collectResult(PartialSolution parSol, PartialProblem parProb) {
        if (parProb == put) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("put returned");
            }
            putReturned = true;
        }

        if (parProb == get) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("get returned");
            }
            solution = ((ContainerPartialSolution) parSol).getPartialSolution();
        }
    }

    /**
     * Liefert die Gesamtlösung.  Dies ist das aus dem <CODE>RemoteStore</CODE>
     * abgerufene Objekt.
     *
     * @return  Die Gesamtlösung.
     */
    public Serializable getSolution() {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Lösung bisher: " + solution);
        }
        return solution;
    }
}

