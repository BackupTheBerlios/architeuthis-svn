/*
 * file:        SystemStatistics.java
 * last change: 26.05.2004 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.userinterfaces.exec;

/**
 * Dieses Interface enthält Methoden zum Zugriff auf Statistik-Werte der
 * allgemeinen Statistik des ComputeSystems
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public interface SystemStatistics extends AbstractStatistics {

    /**
     * Liefert die Anzahl der am System angemeldeten Operatives.
     *
     * @return  Anzahl der angemeldeten Operatives
     */
    public long getRegisteredOperatives();

    /**
     * Liefert die Anzahl der angemeldeten freien Operatives. Ein angemeldeter
     * Operative ist frei, wenn ihm kein Teilproblem zugeordnet ist.
     *
     * @return  Anzahl der freien Operatives
     */
    public long getFreeOperatives();

    /**
     * Liefert die Anzahl der seit dem Start des Dispatcher empfangenen
     * Probleme.
     *
     * @return  Anzahl der bisher empfangenen Probleme
     */
    public long getReceivedProblems();

    /**
     * Liefert die Anzahl der seit dem Start des Dispatcher abgebrochenen
     * Probleme.
     *
     * @return  Anzahl der seit dem Start des Dispatcher abgebrochenen Probleme.
     */
    public long getAbortedProblems();

    /**
     * Liefert die Anzahl der Probleme, die in Bearbeitung sind, zu denen also
     * noch keine Gesamtlösung an den zugehörigen Transmitter gesendet wurde.
     *
     * @return  Anzahl der Probleme in Bearbeitung
     */
    public long getCurrentProblems();

    /**
     * Liefert die Zeit seit dem Start des Dispatchers.
     *
     * @return Zeit in Millisekunden
     */
    public long getDispatcherAge();
}
