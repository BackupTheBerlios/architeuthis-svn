/*
 * file:        Transmitter.java
 * created:     05.04.2005
 * last change: 05.04.2005 by Dietmar Lippold
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

import java.util.logging.Logger;
import java.util.logging.Level;
import java.rmi.RemoteException;

import de.unistuttgart.architeuthis.dispatcher.problemmanaging.BlockingBuffer;

/**
 * Realisiert einen <CODE>Thread</CODE>, der für einen dezentralen RemoteStore
 * Objekte zu einem RelayStore überträgt.
 *
 * @author Dietmar Lippold
 */
public class Transmitter extends Thread {

    /**
     * Standard Logger Pattern.
     */
    private static final Logger LOGGER = Logger.getLogger(Transmitter.class.getName());

    /**
     * Flag um diesen Thread zu beenden.
     */
    private volatile boolean terminated = false;

    /**
     * Puffer mit unbegrenzter Kapazität für die zu übertragenden Daten.
     */
    private BlockingBuffer objectBuffer = new BlockingBuffer(0);

    /**
     * RelayStore, an den die Daten übertragen werden sollen.
     */
    private RemoteStore relayStore;

    /**
     * Eine Instanz mit der Prozedur zur Übertragung eines Objekts an den
     * RelayStore.
     */
    private TransmitProcedure transmitProc;

    /**
     * Erzeugt eine Instanz und startet den Thread.
     *
     * @param relayStore    Der RelayStore, an den die Objekte übertragen
     *                      werden.
     * @param transmitProc  Die Prozedur, der die Objekte zum RelayStore
     *                      überträgt.
     */
    public Transmitter(RemoteStore relayStore, TransmitProcedure transmitProc) {
        this.relayStore = relayStore;
        this.transmitProc = transmitProc;
        start();
    }

    /**
     * Speichert das übergebene Objekt in der Warteschlange zur Übertragung
     * an den RemoteStore.
     *
     * @param object  Das zu speichernde und anschließend zu übertragende
     *                Objekt.
     */
    public void enqueue(Object object) {
        objectBuffer.enqueue(object);
    }

    /**
     * Signalisiert dem Thread, sich zu beenden. Diese Methode wird bei
     * der Beendigung des RemoteStore auf dem Operative aufgerufen.
     */
    public void terminate() {
        terminated = true;

        // Den evtl. wartenden Thread aktivieren.
        objectBuffer.enqueue(null);
    }

    /**
     * Startet den Thread und überträgt fortlaufend Objekte, immer wenn
     * welche verfügbar sind.
     */
    public void run() {
        try {
            while (!terminated) {
                transmitProc.transmit(objectBuffer.dequeue(), relayStore);
            }
        } catch (RemoteException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Übertragung von Objekt zum RelayStore"
                            + " fehlgeschalgen : \n"
                            + e.getMessage());
            }
        }
    }
}

