/*
 * file:        ListQueue.java
 * created:     06.01.2004
 * last change: 06.04.2005 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@t-online.de
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


package de.unistuttgart.architeuthis.misc.util;

import java.util.LinkedList;

import de.unistuttgart.architeuthis.systeminterfaces.Queue;

/**
 * Implementiert eine Schlange mithilfe einer Liste.
 *
 * @author Andreas Heydlauff, Jürgen Heit
 */
public class ListQueue implements Queue {

    /**
     * Liste zur Verwaltung der Schlangeelemente.
     */
    private LinkedList queue = new LinkedList();

    /**
     * Fügt ein Objekt an die Schlange hinten an.
     *
     * @param o  anzufügendes Objekt
     */
    public void enqueue(Object o) {
        queue.addLast(o);
    }

    /**
     * Entfernt das vorderste Objekt aus der Schlange. Wenn die Schlange kein
     * Element enthält, wird <code>null</code> geliefert.
     *
     * @return  das vorderste Objekt aus der Schlange oder <code>null</code>,
     *          wenn dieses nicht existiert.
     */
    public Object dequeue() {
        if (size() > 0) {
            return queue.removeFirst();
        } else {
            return null;
        }
    }

    /**
     * Gibt die Anzahl der Elemente in der Schlange zurück.
     *
     * @return  Größe der Schlange.
     */
    public int size() {
        return queue.size();
    }

    /**
     * Testet auf Leerheit der Schlange.
     *
     * @return   <code>true</code> genau dann, wenn die Schlange keine Elemente
     *           enthält.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Prüft auf das Vorhanden sein eines Objektes in der Schlange.
     *
     * @param o   zu suchendes Objekt
     * @return  <code>true</code> genau dann, wenn das Objekt in der Schlange
     *          ist.
     */
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    /**
     * Entfernt alle Objekte aus der Schlange.
     */
    public void clear() {
        queue.clear();
    }

    /**
     * Entfernt ein beliebiges Objekt aus der Schlange.
     *
     * @param o  zu entfernendes Objekt.
     * @return   <code>true</code> genau dann, wenn das Objekt entfernt wurde.
     */
    public boolean remove(Object o) {
        return queue.remove(o);
    }
}
