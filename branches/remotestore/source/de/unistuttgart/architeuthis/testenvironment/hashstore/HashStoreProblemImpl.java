/*
 * file:        HashStoreProblemImpl.java
 * created:     15.02.2005 von Michael Wohlfart
 * last change: 15.02.2005 von Michael Wohlfart
 * developers:  Michael Wohlfart michael.wohlfart@zsw-bw.de
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

package de.unistuttgart.architeuthis.testenvironment.hashstore;

import java.io.Serializable;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;
import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;
import de.unistuttgart.architeuthis.userinterfaces.develop.Problem;
import de.unistuttgart.architeuthis.userinterfaces.develop.SerializableProblem;

/**
 * erzeugt zwei Teilprobleme:
 * - das erste Teilproblem legt ein Objekt in den RemoteStore
 * - das zweite Teilproblem liefert dieses Objekt zurück
 *
 *
 */
public class HashStoreProblemImpl implements SerializableProblem {
	
	/**
	 * key für den RemoteStore
	 */
	private static final String KEY = "key";
	/**
	 * Lösung, die in den RemoteStore gelegt wird
	 */
	private static final  String LOESUNG = "loesung";
	
	
	/**
	 * die berechnetet Lösung
	 */
	private PartialSolution solution = null;
	
	
	/**
	 * PartialProblem, das die Lösung in den RemoteStore legt
	 */
	private HashStorePut put = new HashStorePut(KEY, LOESUNG);
	
	/**
	 * PartialProblem, das die Lösung aus dem RemoteStore holt
	 */
	private HashStoreGet get = new HashStoreGet(KEY);
	
	/**
	 * Flag, das anzeigt, ob HashStorePut bereits vergeben wurde
	 */
	private  boolean putActive = false;
	
	/**
	 * Flag, das anzeigt, ob HashStoreGet bereits vergeben wurde
	 */
	private  boolean getActive = false;
	
	/**
	 * Flag, das anzeigt, ob HashStorePut bereits bearbeitet wurde
	 */
	private  boolean putReturned = false;
	
	
	/**
	 * Erzeugt die Teilprobleme
	 *
	 * @param number  Anzahl zu generierender Teilprobleme, wird nicht
	 *                verwendet.
	 * @return das Teilproblem
	 */
	public PartialProblem getPartialProblem(long number) {
		if (!putActive) {
			putActive = true;
			System.out.println("### sent put");
			return put;
		}
		if (putReturned && (!getActive)) {
			getActive = true;
			System.out.println("### sent get");
			return get;
		}
		return null;
	}
	
	/**
	 * Erzeugt die gesamtlösung aus den Teillösungen
	 *
	 * @param parProb Teilproblem
	 * @param parSol Teillösung
	 */
	public void collectResult(PartialSolution parSol, PartialProblem parProb) {
		if (parProb == put) {
			System.out.println("### put returned");
			putReturned = true;
		}
		if (parProb == get) {
			System.out.println("### get returned");
			solution = parSol;
		}
	}
	
	/**
	 * liefert die Lösung an die Anwendung zurück
	 *
	 * @return die Lösung
	 */
	public Serializable getSolution() {
		System.out.println("### loesung bisher: " + solution);
		return solution;
	}
	
}
