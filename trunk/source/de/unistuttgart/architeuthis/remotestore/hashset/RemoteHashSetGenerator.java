/*
 * file:        RemoteHashSetGenerator.java
 * created:     08.02.2005
 * last change: 08.02.2005 by Michael Wohlfart
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
package de.unistuttgart.architeuthis.remotestore.hashset;

import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;
import de.unistuttgart.architeuthis.remotestore.RemoteStoreGenerator;

/**
 * Klasse zur Erzeugung HashSet ähnlicher RemoteStores für Architeuthis.
 * Über ein Flag im Konstruktor kann gesteuert werden, ob ein
 * zentraler oder mehrere dezentrale Speicher verwendet werden.
 *
 * @author Michael Wohlfart
 *
 */
public class RemoteHashSetGenerator implements RemoteStoreGenerator {

    /**
     * generierte <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258688797494424632L;

    /**
     * Defaulmässig wird lediglich zentraler Speicher verwendet
     */
    private boolean isCentralOnly = true;

    /**
     * einfacher Konstruktor für die Defaulteinstellungen
     */
    public RemoteHashSetGenerator() {
    }

    /**
     * Konstruktor
     *
     * @param isCentralOnly flase, wenn dezentraler Speicher verwendet
     * werden soll
     */
    public RemoteHashSetGenerator(boolean isCentralOnly) {
        this.isCentralOnly = isCentralOnly;
    }

    /**
     * liefert den zentralen RemoteStore
     *
     * @return a remoteStore obejct
     */
    public RemoteStore generateCentralRemoteStore() {
        try {
            return new RemoteHashSetImpl();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Liefert den dezentralen RemoteStore oder null, falls
     * nur ein zentraler Speicher verwendet werden soll.
     *
     * @return a remoteStore obejct
     */
    public RemoteStore generateDistRemoteStore() {
        if (isCentralOnly) {
            return null;
        } else {
            try {
                return new RemoteHashSetImpl();
            } catch (RemoteException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

}
