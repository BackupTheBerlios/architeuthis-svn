/*
 * file:        Problem.java
 * created:     <???>
 * last change: 23.11.2003 von Andreas Heydlauff
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
 *
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.userinterfaces;

import java.io.Serializable;

/**
 * Muss von der Problem-Klasse implementiert werden.
 * Die einzelnen Methoden m�ssen nicht synchronisiert werden, die
 * Synchronisation wird vollst�ndig auf dem ComputeManager vorgenommen.
 *
 * @author J�rgen Heit
 */
public interface Problem {

    /**
     * Teilt dem Problem die gew�nschte Anzahl zu generierender Teilprobleme
     * mit. Diese Zahl kann, abh�ngig von der Netzarchitektur in dem das
     * Compute-System betrieben wird, von der Anzahl der verf�gbaren Operatives
     * abh�ngen.
     *
     * @param number  gew�nschte Gesamtanzahl der zu generierenden
     *                Teilprobleme
     * @return  genau ein Teilproblem. Dies ist unabh�ngig von der Gesamtanzahl
     *          der generierten Teilprobleme. <code>null</code> falls
     *          kein Teilproblem mehr ausgegeben werden soll
     */
    public PartialProblem getPartialProblem(long number);

    /**
     * Diese Methode wird vom ComputeManager aufgerufen um dem eigentlichen
     * Problem-Objekt eine Teill�sung zu �bermitteln.
     *
     * @param parSol   Teill�sung zur �bergabe an das Problem-Objekt
     * @param parProb  Referenz auf das Teilproblem, das gel�st wurde
     */
    public void collectResult(PartialSolution parSol, PartialProblem parProb);

    /**
     * Gibt die Gesamtl�sung zur�ck.<p>
     * Nachdem eine Teill�sung an den ComputeManager gegeben wurde, wird das
     * Problem nach einer Gesamtl�sung gefragt. Falls diese schon existiert wird
     * die Berechnung der �brigen Teilprobleme abgebrochen. Dies sollte
     * sp�testens der Fall sein, nachdem die letzte Teill�sung zur�ckgegeben
     * wurde.
     *
     * @return Gesamtl�sung, die an den Problem-�bermittler geschickt wird.
     *         <code>null</code> falls das Gesamtproblem noch nicht
     *         zusammengef�gt werden kann.
     */
    public Serializable getSolution();
}
