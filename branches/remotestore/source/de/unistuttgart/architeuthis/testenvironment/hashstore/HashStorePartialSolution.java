/*
 * file:        HashStorePartialSolution.java
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

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;


public class HashStorePartialSolution implements PartialSolution {

    /**
     * das Lösungsobjekt
     */
    private Object solution = null;

    /**
     * Konstruktor
     *
     * @param solution die berechnete Lösung
     */
    public HashStorePartialSolution(Object solution) {
        this.solution = solution;
    }


    /**
     * liefert die Lösung an die Anwendung zurück
     *
     * @return die berechnete Lösung
     */
    public Object getSolution() {
        return this.solution;
    }
}
