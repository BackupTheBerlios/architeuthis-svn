/*
 * file:        BlockingBuffer.java
 * created:     04.01.2004
 * last change: 05.04.2005 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@t-online.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
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


package de.unistuttgart.architeuthis.dispatcher.problemmanaging;

import java.util.LinkedList;

import de.unistuttgart.architeuthis.systeminterfaces.Queue;

/**
 * Implementiert einen FIFO-Puffer nach dem Producer-Consumer Schema.
 *
 * @author Andreas Heydlauff, Jürgen Heit, Dietmar Lippold
 */
public class BlockingBuffer implements Queue {

    /**
     * Die Schlange mit den Elementen des Puffers.
     */
    private ListQueue queue = new ListQueue();

    /**
     * Die maximale Anzahl der Elemente des Puffers.
     */
    private int maxSize;

    /**
     * Enthält alle zuletzt aus diesem BlockingBuffer entfernten Objekte. Das
     * nächste hinzuzunehmende Objekt wird nur hinzugenommen, wenn es nicht in
     * dieser Liste enthalten ist.
     */
    private LinkedList removedObjects = new LinkedList();

    /**
     * Erzeugt einen neuen Puffer ohne Beschräkung der Anzahl der Elemente,
     * die er aufnehmen kann.
     */
    public BlockingBuffer() {
        this(0);
    }

    /**
     * Erzeugt eine neue Instanz. Wenn die übergebene maximale Größe des
     * Puffers Null oder negativ ist, kann der Puffer unbegrenzt viele
     * Elemente aufnehmen.
     *
     * @param maximumSize  Die maximale Anzahl der Elemente im Puffer.
     */
    public BlockingBuffer(int maximumSize) {
        maxSize = maximumSize;
    }

    /**
     * Testet ob der Puffer leer ist.
     *
     * @return   <code>true</code> genau dann, wenn der Puffer kein Element
     *           enthält.
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Testet ob der Puffer voll ist.
     *
     * @return   <code>true</code> genau dann, wenn der Puffer kein Element
     *           mehr aufnehmen kann.
     */
    public synchronized boolean isFull() {
        return (maxSize > 0) && (queue.size() >= maxSize);
    }

    /**
     * Gibt die Anzahl der Elemente im Puffer zurück.
     *
     * @return  Größe der Schlange.
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     * Prüft auf das Vorhanden sein eines Objektes im Puffer.
     *
     * @param o   zu suchendes Objekt
     * @return  <code>true</code> genau dann, wenn das Objekt im Puffer
     *          enthalten ist.
     */
    public synchronized boolean contains(Object o) {
        return queue.contains(o);
    }

    /**
     * Fügt dem Puffer ein Objekt hinzu. Wenn der Puffer voll ist, wird der
     * aufrufende Thread in den Wartezustand versetzt, bis ein Element aus
     * dem Puffer entnommen wird. Ein Objekt, für das seit dem letzten Aufruf
     * dieser Methode die Methode <code>remove</code> aufgerufen wurde, wird
     * nicht aufgenommen. Gleiches gilt für den Wert <code>null</code>, wenn
     * seit dem letzten Aufruf dieser Methode die Methode
     * <code>removeNullElements</code> aufgerufen wurde.
     *
     * @param o  hinzuzufügendes Objekt
     */
    public synchronized void enqueue(Object o) {
        while (isFull()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Das übergebene Objekt nur hinzufügen, wenn es vorher nicht entfernt
        // wurde
        if (!removedObjects.contains(o)) {
            // Alle Threads benachrichtigen, die ein Element aus Puffer
            // entnehmen wollen
            notifyAll();
            queue.enqueue(o);
        }
        removedObjects.clear();
    }

    /**
     * Entnimmt dem Puffer ein Objekt. Wenn der Puffer leer ist, wird der
     * aufrufende Thread in den Wartezustand versetzt, bis ein Element zum
     * dem Puffer hinzugefügt wird.
     *
     * @return  entnommenes Objekt
     */
    public synchronized Object dequeue() {
        while (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Alle Threads benachrichtigen, die ein Element zum Puffer
        // hinzufügen wollen
        notifyAll();
        return queue.dequeue();
    }

    /**
     * Entfernt ein Vorkommen des übergebenen Objekts aus dem Puffer.
     *
     * @param o  zu entfernendes Objekt
     * @return   <code>true</code> genau dann, wenn das Objekt entfernt wurde.
     */
    public synchronized boolean remove(Object o) {
        // Alle Threads benachrichtigen, die ein Element zum Puffer
        // hinzufügen wollen
        notifyAll();
        removedObjects.add(o);
        return queue.remove(o);
    }

    /**
     * Entfernt alle Vorkommen des Wertes <CODE>null</CODE> aus dem Puffer
     * und verhindert die Aufnahme des nächsten Wertes beim Aufruf der Methode
     * <CODE>enqueue</CODE>, wenn der übergebene Wert der Wert <CODE>null</CODE>
     * ist.
     *
     * @return   <code>true</code> genau dann, wenn ein Wert <CODE>null</CODE>
     *           aus dem Puffer entfernt wurde.
     */
    public synchronized boolean removeNullElements() {
        boolean removed = false;

        if (queue.remove(null)) {
            // Alle Threads benachrichtigen, die ein Element zum Puffer
            // hinzufügen wollen
            notifyAll();
            while (queue.remove(null)) {
            };
            removed = true;
        }
        removedObjects.add(null);
        return removed;
    }

    /**
     * Entfernt alle Objekte aus dem Puffer. Ein in der Methode
     * <code>enqueue</code> wartender Thread wird sein Objekt aber anschließend
     * hinzufügen.
     */
    public synchronized void clear() {
        // Alle Threads benachrichtigen, die ein Element zum Puffer
        // hinzufügen wollen
        notifyAll();
        queue.clear();
    }
}

