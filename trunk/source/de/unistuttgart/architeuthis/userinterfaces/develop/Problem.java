/*
 * file:        Problem.java
 * created:     <???>
 * last change: 23.04.2006 by Dietmar Lippold
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
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
 * m�ssen nicht synchronisiert werden, die Synchronisation wird vollst�ndig
 * auf dem ComputeManager vorgenommen.
 *
 * @author J�rgen Heit, Dietmar Lippold
 */
public interface Problem {

    /**
     * Liefert ein neues Teilproblem. Die �bergebene, vorgeschlagene Anzahl
     * der insgesamt zu erzeugenden Teilprobleme h�ngt von der Anzahl der
     * verf�gbaren Operatives ab.
     *
     * @param number  Vorgeschlagene Gesamtanzahl der zu erzeugenden
     *                Teilprobleme. Dieser Wert ist gr�sser oder gleich Eins.
     *
     * @return  Genau ein Teilproblem oder <code>null</code>, falls derzeit
     *          kein neues Teilproblem erzeugt werden soll.
     */
    public PartialProblem getPartialProblem(long number);

    /**
     * Nimmt eine Teill�sung entgegen, um daraus zusammen mit den anderen
     * Teill�sungen eine Gesamtl�sung zu erzeugen.
     *
     * @param parSol   Die zum Teilproblem ermittelte Teill�sung.
     * @param parProb  Referenz auf das Teilproblem, zu dem die Teill�sung
     *                 ermittelt wurde.
     */
    public void collectPartialSolution(PartialSolution parSol,
                                       PartialProblem parProb);

    /**
     * Liefert die Gesamtl�sung, falls diese schon existiert. In dem Fall
     * wird die Berechnung der �brigen Teilprobleme vom ComputeManager
     * abgebrochen. Die Gesamtl�sung mu� sp�testens geliefert werden, wenn die
     * letzte Teill�sung zur�ckgegeben wurde.
     *
     * @return  Die Gesamtl�sung, die an den Problem-�bermittler geschickt
     *          wird oder <code>null</code>, falls die Gesamtl�sung noch nicht
     *          ermittelt werden kann.
     */
    public Serializable getSolution();
}

