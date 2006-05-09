/*
 * file:    InfoOperative.java
 * last change: 30.06.2004 by Dietmar Lippold
 * copyright:   Jürgen Heit,       juergen.heit@gmx.de
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

import de.unistuttgart.architeuthis.systeminterfaces.Operative;

/**
 * Hilfsklasse zur Verwaltung eines Operatives auf dem
 * {@link de.unistuttgart.architeuthis.dispatcher.computemanaging.ComputeManagerImpl}
 *
 * @author Jürgen Heit, Dietmar Lippold
 */
class InfoOperative {

    /**
     * Eine eindeutige, fortlaufende Nummer.
     */
    private static long nextOperativeId = 0;

    /**
     * Referenz auf den verwalteten Operative.
     */
    private Operative operative;

    /**
     * Referenz auf das Teilproblem, das vom verwalteten Operative
     * berechnet wird.
     */
    private volatile InfoParProbWrapper infoParProbWrapper = null;

    /**
     * Eine Nummer, die dieses Objekt und damit den darin gespeicherten
     * Operative eindeutig kennzeichnet.
     */
    private long operativeId;

    /**
     * Aktivitätszustand des Operative.
     */
    private volatile boolean active = true;

    /**
     * Legt den Operative fest, auf die sich die zu verwaltenden
     * Informationen beziehen.
     *
     * @param operative       Referenz auf den Operative
     *
     */
    InfoOperative(Operative operative) {
        this.operative = operative;
        synchronized (getClass()) {
            operativeId = nextOperativeId++;
        }
    }

    /**
     * Liefert zum {@link InfoOperative}-Objekt den gehörigen Operative
     * zurück.
     *
     * @return  Verwalteter Operative
     */
    Operative getOperative() {
        return operative;
    }

    /**
     * Liefert eine Referenz auf das Objekt zurück, das Informationen über das
     * vom Operative aktuell behandelte Teilproblem enthält.
     *
     * @return  Referenz auf die zugehörige Teilproblem-Information
     */
    InfoParProbWrapper getInfoParProbWrapper() {
        return infoParProbWrapper;
    }

    /**
     * Setzt die Referenz auf das Objekt, dass Informationen über das vom
     * Operative zu verwaltende Teilproblem enthält.
     *
     * @param  infoParProbWrap Referenz auf die zugehörige Teilproblem-Information
     */
    void setInfoParProbWrapper(InfoParProbWrapper infoParProbWrap) {
        infoParProbWrapper = infoParProbWrap;
    }

    /**
     * Liefert zurück, ob der Operative aktiv oder inaktiv ist.
     *
     * @return  <code>true</code> falls aktiv
     *          <code>false</code> falls nicht aktiv
     */
    boolean isActive() {
        return active;
    }

    /**
     * Setzt den Zustand des Operatives. Aktiv bedeutet, dass der Operative ein
     * Teilproblem berechnet.
     *
     * @param newStatus  Folgezustand des Operatives.
     */
    void setActive(boolean newStatus) {
        active = newStatus;
    }

    /**
     * Liefert einen Text, der dieses Objekt und den darin gespeicherten
     * Operative eindeutig kennzeichnet.
     *
     * @return  Einen Text, der dieses Objekt und den darin gespeicherten
     *          Operative eindeutig kennzeichnet.
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Operative " + operativeId;
    }
}
