/*
 * file:        Problem.java
 * created:     <???>
 * last change: 23.04.2006 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *
 * Realease 1.0 dieser Software wurde am Institut für Intelligente Systeme der
 * Universität Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
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

import java.io.Serializable;

/**
 * Muss von der Problem-Klasse implementiert werden. Die einzelnen Methoden
 * müssen nicht synchronisiert werden, die Synchronisation wird vollständig
 * auf dem ComputeManager vorgenommen.
 *
 * @author Jürgen Heit, Dietmar Lippold
 */
public interface Problem {

    /**
     * Liefert ein neues Teilproblem. Die übergebene, vorgeschlagene Anzahl
     * der insgesamt zu erzeugenden Teilprobleme hängt von der Anzahl der
     * verfügbaren Operatives ab.
     *
     * @param number  Vorgeschlagene Gesamtanzahl der zu erzeugenden
     *                Teilprobleme. Dieser Wert ist grösser oder gleich Eins.
     *
     * @return  Genau ein Teilproblem oder <code>null</code>, falls derzeit
     *          kein neues Teilproblem erzeugt werden soll.
     */
    public PartialProblem getPartialProblem(long number);

    /**
     * Nimmt eine Teillösung entgegen, um daraus zusammen mit den anderen
     * Teillösungen eine Gesamtlösung zu erzeugen.
     *
     * @param parSol   Die zum Teilproblem ermittelte Teillösung.
     * @param parProb  Referenz auf das Teilproblem, zu dem die Teillösung
     *                 ermittelt wurde.
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb);

    /**
     * Liefert die Gesamtlösung, falls diese schon existiert. In dem Fall
     * wird die Berechnung der übrigen Teilprobleme vom ComputeManager
     * abgebrochen. Die Gesamtlösung muß spätestens geliefert werden, wenn die
     * letzte Teillösung zurückgegeben wurde.
     *
     * @return  Die Gesamtlösung, die an den Problem-Übermittler geschickt
     *          wird oder <code>null</code>, falls die Gesamtlösung noch nicht
     *          ermittelt werden kann.
     */
    public Serializable getSolution();
}

