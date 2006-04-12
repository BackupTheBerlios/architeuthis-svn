/*
 * file:        ContainerPartialSolution.java
 * last change: 12.04.2006 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.abstractproblems;

import java.io.Serializable;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialSolution;

/**
 * Container für ein Serializable-Objekt, das das Interface
 * <code>PartialSolution</code> implementiert. Das konkrete Objekt wird über
 * den Konstruktor gesetzt und kann über <code>getPartialSolution()</code>
 * ausgelesen werden.
 *
 * @author Ralf Kible, Dietmar Lippold
 */
public class ContainerPartialSolution implements PartialSolution {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 5615189329048827290L;

    /**
     * Das im Container gespeicherte Serializable-Objekt
     */
    private Serializable partialSolution = null;

    /**
     * Mit diesem Konstruktor wird die Teillösung übergeben.
     *
     * @param partialSolution  Die vom Container gekapselte Teillösung.
     */
    public ContainerPartialSolution(Serializable partialSolution) {
        this.partialSolution = partialSolution;
    }

    /**
     * Liefert die enthaltene Teillösung.
     *
     * @return  Die Teillösung.
     */
    public Serializable getPartialSolution() {
        return partialSolution;
    }
}

