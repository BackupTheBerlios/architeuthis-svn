 /*
 * file:        PartialSolutionImpl.java
 * created:     24.05.2003
 * last change: 26.05.2004 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
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


package de.unistuttgart.architeuthis.testenvironment.montecarlo;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Die Klasse, die die Lösung eines Teilproblems abspeichert.
 *
 * @author Achim Linke
 */
public class PartialSolutionImpl implements PartialSolution {


    /**
     * Der berechnete Wert für Pi
     */
    private double piBerechnet;

    /**
     * Konnstruktor, der den berechneten Wert für Pi abspeichert.
     *
     * @param berechnet  Der zu speicherende Wert
     */
    PartialSolutionImpl(double berechnet) {
        piBerechnet = berechnet;
    }

    /**
     * Gibt den gespeicherten Wert von Pi zurück.
     *
     * @return  <code>double</code>, in dem die berechnete Lösung von Pi steckt
     */
    public double getPartialSolution() {
        return piBerechnet;
    }
}
