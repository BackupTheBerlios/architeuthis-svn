/*
 * file:        Transmitter.java
 * created:     05.04.2005
 * last change: 17.04.2006 by Dietmar Lippold
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

import de.unistuttgart.architeuthis.misc.util.BlockingBuffer;

/**
 * Realisiert einen <CODE>Thread</CODE>, der übergebene Objekte mittels einer
 * vorher übergebenen Prozedur überträgt.
 *
 * @author Dietmar Lippold
 */
public class Transmitter extends Thread {

    /**
     * Standard Logger Pattern.
     */
    private static final Logger LOGGER = Logger.getLogger(Transmitter.class.getName());

    /**
     * Gibt an, ob der Thread dabei ist, sich zu beenden.
     */
    private volatile boolean terminating = false;

    /**
     * Gibt an, ob der Thread beendet ist.
     */
    private volatile boolean terminated = false;

    /**
     * Puffer mit unbegrenzter Kapazität für die zu übertragenden Daten.
     */
    private BlockingBuffer objectBuffer = new BlockingBuffer(0);

    /**
     * Eine Instanz mit der Prozedur zur Übertragung eines Objekts an den
     * RelayStore.
     */
    private TransmitProcedure transmitProc;

    /**
     * Erzeugt eine Instanz und startet den Thread.
     *
     * @param transmitProc  Die Prozedur, der die Objekte zum RelayStore
     *                      überträgt.
     */
    public Transmitter(TransmitProcedure transmitProc) {
        this.transmitProc = transmitProc;
        start();
    }

    /**
     * Speichert das übergebene Objekt in der Warteschlange zur Übertragung
     * durch die TransmitProcedure.
     *
     * @param object  Das zu speichernde und anschließend zu übertragende
     *                Objekt.
     */
    public void enqueue(TransmitObject object) {
        objectBuffer.enqueue(object);
    }

    /**
     * Signalisiert dem Thread, sich zu beenden. Bevor der Thread endet,
     * werden noch alle gespeicherten Objekte übertragen.
     */
    public void terminate() {
        terminating = true;

        // Den evtl. wartenden Thread aktivieren.
        objectBuffer.enqueue(null);

        // Prüfen, ob noch zu übertragende Objekte vorhanden sind.
        synchronized (this) {
            if (!terminated) {
                // Warten, bis alle Objekte aus objectBuffer übertragen sind.
                try {
                    wait();
                } catch (InterruptedException e) {
                    LOGGER.info("Warten auf Ende der Übertragung abgebrochen");
                }
            }
        }
    }

    /**
     * Startet den Thread und überträgt fortlaufend Objekte, immer wenn
     * welche verfügbar sind.
     */
    public void run() {
        TransmitObject transmitObject;

        try {
            while (!terminating || !objectBuffer.isEmpty()) {
                transmitObject = (TransmitObject) objectBuffer.dequeue();
                if (transmitObject != null) {
                    transmitProc.transmit(transmitObject);
                }
            }
        } catch (RemoteException e) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Übertragung von Objekt zum RelayStore"
                            + " fehlgeschalgen : \n"
                            + e.getMessage());
            }
        }

        synchronized (this) {
            // Den möglicherweise wartenden Thread aus dem Aufruf von
            // terminate aufwecken.
            terminated = true;
            notifyAll();
        }
    }
}

