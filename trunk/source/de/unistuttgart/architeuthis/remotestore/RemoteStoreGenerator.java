/*
 * file:        RemoteStoreGenerator.java
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

package de.unistuttgart.architeuthis.remotestore;

import java.io.Serializable;

/**
 * Die Aufgabe eines RemoteStoreGenerators ist es zentrale und/oder
 * verteilte Speicher zu erzeugen. Dies Speicher implementieren
 * üblicherweise Collection ähnliche Interfaces, so dass sich darin
 * Objekte ablegen lassen.
 *
 * Ein RemoteStoreGenerator muss Serializable sein,
 * da er an den Dispatcher und an die Operatives übermittelt wird.
 *
 * Die Methode generateCentralRemoteStore wird vom Dispatcher
 * verwendet.
 * Die Methode generateDistRemoteStore wird vom Operative
 * verwendet.
 *
 *
 * @author Michael Wohlfart
 *
 */
public interface RemoteStoreGenerator extends Serializable {

    /**
     * Die Implementierung dieser Methode wird vom Dispatcher
     * verwendet um ein zentrales Speicherobjekt zu erhalten.
     *
     * @return RemoteStore in der JVM des Dispatcher verankert
     */
    RemoteStore generateCentralRemoteStore();

    /**
     * Die Implementierung dieser Methode wird von
     * den Operatives verwendet, um ein dezentrales Speicherobjekt
     * zu erhalten
     *
     * @return RemoteStore in der JVM eines Operatives vernakert
     */
    RemoteStore generateDistRemoteStore();

}
