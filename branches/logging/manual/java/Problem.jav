/*
 * file:        Problem.java
 * created:     <???>
 * last change: 23.11.2003 von Andreas Heydlauff
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
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */


package de.unistuttgart.architeuthis.userinterfaces;

import java.io.Serializable;

/**
 * Muss von der Problem-Klasse implementiert werden.
 * Die einzelnen Methoden müssen nicht synchronisiert werden, die
 * Synchronisation wird vollständig auf dem ComputeManager vorgenommen.
 *
 * @author Jürgen Heit
 */
public interface Problem {

    /**
     * Teilt dem Problem die gewünschte Anzahl zu generierender Teilprobleme
     * mit. Diese Zahl kann, abhängig von der Netzarchitektur in dem das
     * Compute-System betrieben wird, von der Anzahl der verfügbaren Operatives
     * abhängen.
     *
     * @param number  gewünschte Gesamtanzahl der zu generierenden
     *                Teilprobleme
     * @return  genau ein Teilproblem. Dies ist unabhängig von der Gesamtanzahl
     *          der generierten Teilprobleme. <code>null</code> falls
     *          kein Teilproblem mehr ausgegeben werden soll
     */
    public PartialProblem getPartialProblem(long number);

    /**
     * Diese Methode wird vom ComputeManager aufgerufen um dem eigentlichen
     * Problem-Objekt eine Teillösung zu übermitteln.
     *
     * @param parSol   Teillösung zur übergabe an das Problem-Objekt
     * @param parProb  Referenz auf das Teilproblem, das gelöst wurde
     */
    public void collectResult(PartialSolution parSol, PartialProblem parProb);

    /**
     * Gibt die Gesamtlösung zurück.<p>
     * Nachdem eine Teillösung an den ComputeManager gegeben wurde, wird das
     * Problem nach einer Gesamtlösung gefragt. Falls diese schon existiert wird
     * die Berechnung der übrigen Teilprobleme abgebrochen. Dies sollte
     * spätestens der Fall sein, nachdem die letzte Teillösung zurückgegeben
     * wurde.
     *
     * @return Gesamtlösung, die an den Problem-übermittler geschickt wird.
     *         <code>null</code> falls das Gesamtproblem noch nicht
     *         zusammengefügt werden kann.
     */
    public Serializable getSolution();
}
