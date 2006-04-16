/*
 * file:        AbstractFixedSizePriorityPartialProblem.java
 * last change: 12.04.2006 von Dietmar Lippold
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


package de.unistuttgart.architeuthis.abstractproblems;

import de.unistuttgart.architeuthis.userinterfaces.develop.PartialProblem;

/**
 * Abstrakte Klasse f�r ein Teilproblem, das von einer Unterklasse von
 * <code>AbstractFixedSizePriorityProblem</code> verwendet wird.
 * Das Setzen der Priorit�t eines Teilproblems geschieht �ber den Konstruktor
 * {@link AbstractFixedSizePriorityPartialProblem#AbstractFixedSizePriorityPartialProblem(long)},
 * wobei gr��ere Zahlen eine h�here Priorit�t darstellen.
 *
 * @author Andreas Heydlauff
 */
public abstract class AbstractFixedSizePriorityPartialProblem
    implements PartialProblem, Comparable {

    /**
     * Generierte <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 6255594252588525298L;

    /**
     * Priorit�t des Teilproblems.
     */
    private int prio = 0;

    /**
     * Einzig erlaubter Konstruktor, der die Priorit�t eines Teilproblems
     * festlegt.
     *
     * @param priority  Priorit�t eines Teilproblems. Gr��ere Zahlen stellen
     *                  h�here Priorit�ten dar.
     */
    public AbstractFixedSizePriorityPartialProblem(int priority) {
        prio = priority;
    }

    /**
     * Vergleicht die Priorit�t.
     *
     * @param o  Erbe des <code>AbstractFixedSizePriorityPartialProlem</code>,
     *           mit dem verglichen wird.
     *
     * @return  positiver Integer-Wert, falls die eigene Priorit�t kleiner,
     *          0 falls gleich, negativ falls gr��er ist.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {

        AbstractFixedSizePriorityPartialProblem compProb =
            (AbstractFixedSizePriorityPartialProblem) o;
        return (compProb.prio - prio);
    }
}

