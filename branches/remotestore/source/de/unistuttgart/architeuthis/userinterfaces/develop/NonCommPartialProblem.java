/*
 * file:        CommunicationPartialProblem.java
 * created:     08.02.2005
 * last change: 29.03.2005 by Dietmar Lippold
 * developers:  Michael Wohlfart, michael.wohlfart@zsw-bw.de
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


package de.unistuttgart.architeuthis.userinterfaces.develop;

import de.unistuttgart.architeuthis.userinterfaces.ProblemComputeException;

/**
 * Interface muß von einem Teilproblem implementiert werden, das mit anderen
 * Teilproblemen keine Daten austauschen will.
 *
 * @author Michael Wohlfart
 */
public interface NonCommPartialProblem extends PartialProblem {

    /**
     * Startet die Berechnung des Teilproblems.
     *
     * @return  Die berechnete Teillösung.
     *
     * @throws ProblemComputeException  Bei beliebigen Fehlern bei der
     *                                  Berechnung.
     */
    public PartialSolution compute() throws ProblemComputeException;
}

