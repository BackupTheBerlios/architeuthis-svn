/*
 * file:        Queue.java
 * created:     06.01.2004
 * last change: 25.05.2004 by Dietmar Lippold
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


package de.unistuttgart.architeuthis.systeminterfaces;

/**
 * Interface für eine Schlange.
 * 
 * @author Andreas Heydlauff, Jürgen Heit
 */
public interface Queue {
    
    /**
     * Fügt ein Objekt an die Schlange hinten an.
     * 
     * @param o  anzufügendes Objekt
     */
    public abstract void enqueue(Object o);
    
    /**
     * Entfernt das vorderste Objekt aus der Schlange.
     * 
     * @return  vorderstes Objekt aus der Schlange.
     */
    public abstract Object dequeue();
    
    /**
     * Gibt die Anzahl der Elemente in der Schlange zurück.
     * 
     * @return  Größe der Schlange.
     */
    public abstract int size();
    
    /**
     * Testet auf Leerheit der Schlange.
     * 
     * @return   <code>true</code> genau dann, wenn die Schlange keine Elemente
     *           enthält.
     */
    public abstract boolean isEmpty();
    
    /**
     * Prüft auf das Vorhanden sein eines Objektes in der Schlange.
     * 
     * @param o   zu suchendes Objekt
     * @return  <code>true</code> genau dann, wenn das Objekt in der Schlange ist.
     */
    public abstract boolean contains(Object o);
    
    /**
     * Löscht alle Objekte aus der <code>Queue</code>    
     */
    public abstract void clear();
    
    /**
     * Entfernt ein beliebiges Objekt aus der Schlange.
     * 
     * @param o  zu entfernendes Objekt.
     * @return   <code>true</code> genau dann, wenn das Objekt entfernt wurde.
     */
    public abstract boolean remove(Object o);
}
