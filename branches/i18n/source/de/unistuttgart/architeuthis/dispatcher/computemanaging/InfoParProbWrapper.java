/*
 * file:        InfoParProbWrapper.java
 * last chnage: 08.10.2004 von Dietmar Lippold
 * developer:   Jürgen Heit,       juergen.heit@gmx.de
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

package de.unistuttgart.architeuthis.dispatcher.computemanaging;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.unistuttgart.architeuthis.dispatcher.problemmanaging.ParProbWrapper;

/**
 * Hilfsklasse zur Verwaltung eines Teilproblem-Wrappers auf dem
 * {@link de.unistuttgart.architeuthis.dispatcher.computemanaging.ComputeManagerImpl}.
 *
 * @author Jürgen Heit, Dietmar Lippold
 */
class InfoParProbWrapper {

    /**
     * Liste der Operative, die den zugeordnete Teilproblem-Wrapper berechnen.
     */
    private List operativeInfos =
        Collections.synchronizedList(new LinkedList());

    /**
     * Zugeordneter Teilproblem-Wrapper.
     */
    private ParProbWrapper parProbWrapper;

    /**
     * Erzeugt eine Instanz für den zu verwaltende Teilproblem-Wrapper, ohne
     * dass bisher ein Operative bekannt ist, der das Teilproblem berechnet.
     *
     * @param parProbWrapper  zu verwaltender Teilproblem-Wrapper
     */
    InfoParProbWrapper(ParProbWrapper parProbWrapper) {
        this.parProbWrapper = parProbWrapper;
    }

    /**
     * Trägt einen weiteren Operative in die Bearbeiterliste für das
     * verwaltete Teilproblem ein.
     *
     * @param operativeInfoObj  Hinzuzufügender Operative
     */
    void addOperativeInfo(InfoOperative operativeInfoObj) {
        if (operativeInfoObj == null) {
            throw new NullPointerException("operativeInfoObj ist NULL");
        } else {
            operativeInfos.add(operativeInfoObj);
        }
    }

    /**
     * Entfernt einen Operative aus der Bearbeiterliste für das verwaltete
     * Teilproblem.
     *
     * @param operativeInfoObj  zu entfernender Operative
     * @return  <code>true</code>, falls der Operative aus der
     *          Bearbeiterliste entfernt wurde.
     */
    boolean removeOperativeInfo(InfoOperative operativeInfoObj) {
        return operativeInfos.remove(operativeInfoObj);
    }

    /**
     * Liefert eine neue Liste der Operatives, die gegenwärtig noch das
     * Teilproblem bearbeiten.
     *
     * @return  <code>Collection</code> der Operatives
     */
    List getOperativeInfos() {
        LinkedList operativeInfosCopy;

        synchronized (operativeInfos) {
            operativeInfosCopy = new LinkedList(operativeInfos);
        }
        return operativeInfosCopy;
    }

    /**
     * Liefert eine Referenz auf den verwaltete Teilproblem-Wrapper.
     *
     * @return  Teilproblem-Wrapper
     */
    ParProbWrapper getParProbWrapper() {
        return parProbWrapper;
    }

    /**
     * Liefert eine eindeutige Beschreibung zum enthaltenen ParProbWrapper
     * Objekt.
     *
     * @return  Eine eindeutige Beschreibung zum enthaltenen ParProbWrapper
     *          Objekt.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return parProbWrapper.toString();
    }

}
