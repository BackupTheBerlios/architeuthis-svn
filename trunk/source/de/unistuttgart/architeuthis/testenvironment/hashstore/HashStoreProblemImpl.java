/*
 * file:        HashStoreProblemImpl.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 06.04.2006 von Dietmar Lippold
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
 * Erzeugt zwei Arten von Teilproblemen zur Benutzung eines
 * <CODE>RemoteStore</CODE>. Die erste Art von Teilproblemen legt ein Objekt
 * in den RemoteStore und die zweite Art, von der nur eine Instanz erzeugt
 * wird, ruft es daraus wieder ab. Das Teilproblem der zweiten Art wird erst
 * geliefert, wenn alle der erste Art beendet sind.
 *
 * @author Michael Wohlfart, Dietmar Lippold
 */
public class HashStoreProblemImpl implements SerializableProblem {

    /**
     * Generierte SerialVersionUID. Diese muss geändert werden, sobald
     * strukurelle Änderungen an dieser Klasse durchgeführt worden sind.
     */
    private static final long serialVersionUID = -6909831455731796208L;

    /**
     * Logger für diese Klasse.
     */
    private static final Logger LOGGER = Logger.getLogger(HashStoreProblemImpl.class.getName());

    /**
     * Key-Objekt für den <CODE>RemoteStore</CODE>.
     */
    private static final String KEY = "key";

    /**
     * Anfangsbestandteil des Value-Objekts für den <CODE>RemoteStore</CODE>.
     */
    private static final String LOESUNG = "Loesung";

    /**
     * Wert der geliefert wird, wenn kein Value-Objekts ermittelt werden
     * konnte.
     */
    private static final String KEINE_LOESUNG = "Keine Loesung";

    /**
     * Die ermttelte Lösung.
     */
    private Serializable solution = null;

    /**
     * Anzahl der zu erzeugenden Teilprobleme vom Typ
     * <CODE>HashStorePut</CODE>.
     */
    private int putCreateNr;

    /**
     * Anzahl der vergebenen Teilprobleme vom Typ <CODE>HashStorePut</CODE>.
     */
    private int putDeliveredNr = 0;

    /**
     * Anzahl der erhaltenen Teillösungen zu Teilprobleme vom Typ
     * <CODE>HashStorePut</CODE>. 
     */
    private int putReturnedNr = 0;

    /**
     * Flag, das anzeigt, ob das Teilproblem vom Typ <CODE>HashStoreGet</CODE>
     * bereits vergeben wurde. Dies kann erst auf <CODE>true</CODE> gesetzt
     * werden, wenn die vorgegebene Anzahl von Teilproblemen vom Typ
     * <CODE>HashStorePut</CODE> bearbeitet wurde.
     */
    private  boolean getDelivered = false;

    /**
     * Erzeugt eine Instanz, wobei die Anzahl der zu erzeugenden Teilprobleme
     * vom Typ <CODE>HashStorePut</CODE> angegeben wird.
     *
     * @param putParProbNumber  Anzahl zu generierender Teilprobleme vom Typ
     *                          <CODE>HashStorePut</CODE>.
     */
    public HashStoreProblemImpl(int putParProbNumber) {
        putCreateNr = putParProbNumber;
    }

    /**
     * Erzeugt eine Instanz, wobei die Anzahl der zu erzeugenden Teilprobleme
     * nicht angegeben wird. Es wird daher ein Teilproblem vom Typ
     * <CODE>HashStorePut</CODE> erzeugt.
     */
    public HashStoreProblemImpl() {
        this(1);
    }

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
        if (putDeliveredNr < putCreateNr) {
            putDeliveredNr++;
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("delivered put " + putDeliveredNr);
            }
            return (new HashStorePut(KEY, LOESUNG + "-" + putDeliveredNr));
        }

        if ((putReturnedNr == putCreateNr) && (!getDelivered)) {
            getDelivered = true;
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("delivered get");
            }
            return (new HashStoreGet(KEY));
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
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb) {

        if (parProb instanceof HashStorePut) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("put received");
            }
            putReturnedNr++;
        }

        if (parProb instanceof HashStoreGet) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("get received");
            }
            solution = ((ContainerPartialSolution) parSol).getPartialSolution();
            if (solution == null) {
                solution = KEINE_LOESUNG;
            }
        }
    }

    /**
     * Liefert die Gesamtlösung.  Dies ist das aus dem <CODE>RemoteStore</CODE>
     * abgerufene Objekt oder ein Ersatzobjekt, falls kein Objekt abgerufen
     * werden konnte.
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

