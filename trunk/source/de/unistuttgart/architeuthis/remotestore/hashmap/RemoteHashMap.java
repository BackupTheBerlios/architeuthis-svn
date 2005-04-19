/*
 * file:        RemoteHashMap.java
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
package de.unistuttgart.architeuthis.remotestore.hashmap;

import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.remotestore.RemoteStore;


/**
 * Dieses Interface gibt die Methoden vor, die für einen RemoteStore
 * mit HashMap-Funktionalität implementiert werden.
 *
 * @author Michael Wohlfart
 *
 */
public interface RemoteHashMap extends RemoteStore {

    /**
     * Schnittstelle für den Anwendungsprogrammierer, um auf den
     * Speicher zuzugreifen
     *
     * @param key Hash-Key
     * @param object Objekt, das gespeichert werden soll
     *
     * @throws RemoteException RMI Probleme
     */
    void put(Object key, Object object) throws RemoteException;


    /**
     * Schnittstelle für den Anwendungsprogrammierer, um auf den
     * Speicher zuzugreifen.
     *
     * @param key Hash-Key
     *
     * @throws RemoteException RMI Probleme
     *
     * @return das gesuchte Objekt
     */
    Object get(Object key) throws RemoteException;


    /**
     * Schnittstelle für die Plattform, um die Speicher zu
     * synchronisieren. Sollte lediglich für die Implementierung des
     * RemoteStore verwendet werden nicht von der Anwendung selbst.
     *
     * @param origin RemoteStore von dem die Änderung kommt
     * @param key Hash-Key für das neue Objekt
     * @param object neues Objekt für den Speicher
     *
     * @throws RemoteException RMI Probleme
     */
    void putRemote(Object origin, Object key, Object object) throws RemoteException;

    /**
     * Schnittstelle für die Plattform um die Speicher zu
     * synchronisieren. Sollte lediglich für die Implementierung des
     * RemoteStore verwendet werden nicht von der Anwendung selbst.
     *
     * @param key Hash-Key für das gewünschte Objekt
     *
     * @throws RemoteException RMI Probleme
     *
     * @return das gesuchte Objekt
     */
    Object getLocal(Object key) throws RemoteException;

}
