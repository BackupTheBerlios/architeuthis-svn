/*
 * file:        DummyClass1.java
 * created:     <???>
 * last change: 11.02.2004 by J�rgen Heit
 * developers:  J�rgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
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
 * Realease 1.0 dieser Software wurde am Institut f�r Intelligente Systeme der
 * Universit�t Stuttgart (http://www.informatik.uni-stuttgart.de/ifi/is/) unter
 * Leitung von Dietmar Lippold (dietmar.lippold@informatik.uni-stuttgart.de)
 * entwickelt.
 */

package de.unistuttgart.architeuthis.testenvironment.caching;

/**
 * Diese Klasse dient nur dem Zweck, zu Testzwecken vom Webserver
 * geladen zu werden, um den Geschwindigeitsverlust ohne Caching von
 * Klassen-Dateien zu untersuchen.
 *
 * @author Ralf Kible
 */
public class DummyClass1 {

    public String testwert = "Dies ist ein langer String, dass diese Klasse "
        + "nicht komplett leer ist.";

    public DummyClass1() {
        super();
    }
}
