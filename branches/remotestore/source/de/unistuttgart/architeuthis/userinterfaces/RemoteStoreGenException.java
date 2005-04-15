/*
 * file:        RemoteStoreGenException.java
 * last change: 15.04.2005 by Dietmar Lippold
 * developers:  Jürgen Heit,       juergen.heit@gmx.de
 *              Andreas Heydlauff, AndiHeydlauff@gmx.de
 *              Achim Linke,       achim81@gmx.de
 *              Ralf Kible,        ralf_kible@gmx.de
 *              Dietmar Lippold,   dietmar.lippold@informatik.uni-stuttgart.de
 *              Michael Wohlfart,  michael.wohlfart@zsw-bw.de
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


package de.unistuttgart.architeuthis.userinterfaces;

/**
 * Ausnahme, die bei der Erzeugung eines {@link RemoteStore} auftreten kann.
 *
 * @author Andreas Heydlauff, Dietmar Lippold
 */
public class RemoteStoreGenException extends Exception {

    /**
     * Constructs a new exception with null as its detail message. The cause is
     * not initialized, and may subsequently be initialized by a call to
     * Throwable.initCause(java.lang.Throwable).
     */
    public RemoteStoreGenException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized, and may subsequently be initialized by a call to
     * Throwable.initCause(java.lang.Throwable).
     *
     * @param message  the detail message. The detail message is saved for later
     * retrieval by the Throwable.getMessage() method.
     */
    public RemoteStoreGenException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.<p>
     * Note that the detail message associated with cause is not automatically
     * incorporated in this exception's detail message.
     *
     * @param message  the detail message (which is saved for later retrieval by
     *                 the Throwable.getMessage() method).
     * @param cause  the cause (which is saved for later retrieval by the
     *               Throwable.getCause() method). (A null value is permitted,
     *               and indicates that the cause is nonexistent or unknown.)
     */
    public RemoteStoreGenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message
     * of (cause==null ? null : cause.toString()) (which typically contains the
     * class and detail message of cause). This constructor is useful for
     * exceptions that are little more than wrappers for other throwables
     * (for example, PrivilegedActionException).
     *
     * @param cause  the cause (which is saved for later retrieval by the
     *               Throwable.getCause() method). (A null value is permitted,
     *               and indicates that the cause is nonexistent or unknown.)
     */
    public RemoteStoreGenException(Throwable cause) {
        super(cause);
    }
}

