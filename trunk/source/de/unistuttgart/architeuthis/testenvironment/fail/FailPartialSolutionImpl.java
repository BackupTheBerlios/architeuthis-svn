/*
 * file:        FailPartialSolutionImpl.java
 * last change: 24.04.2006 by Dietmar LIppold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.testenvironment.fail;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;


public class FailPartialSolutionImpl implements PartialSolution {

    /**
     * Identifikationsnummer, die <code>Problem</code> die Zuordnung
     * erm�glicht.
     */
    private int number = 0;

    /**
     * Constructor FailPartialSolutionImpl.
     * @param number
     */
    public FailPartialSolutionImpl(int num) {
       number = num;
    }

    /**
     * Method getNumber.
     * @return Object
     */
    public Integer getNumber() {
        return new Integer(number);
    }
}

