/*
 * @(#)ClassServer.java    1.4 01/05/22
 *
 * Copyright 1995-2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistributions of source code must retain the above copyright
 * notice, this  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF  OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

/**
 * Changes to the original Source from Sun:
 *
 * 20.08.2004 Michael Wohlfart, general cleanup:
 *  - changed Source formatting to be compatible with the java styleguide
 *  - added some comments
 *  - added logger
 *  - added URLdecoder in the getPath() method
 *
 * Last change: 17. Apr 2006 by Dietmar Lippold
 */


package de.unistuttgart.architeuthis.user;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

/**
 * ClassServer.java -- a simple file server that can serve
 * Http get request in both clear and secure channel.<p>
 *
 * Based on ClassServer.java in tutorial/rmi.
 */
public abstract class ClassServer implements Runnable {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER =
        Logger.getLogger(ClassServer.class.getName());

    /**
     * the socket this server will listen on
     */
    private ServerSocket server = null;

    /**
     * Constructs a ClassServer based on <CODE>ss</CODE> and
     * obtains a file's bytecodes using the method <CODE>getBytes</CODE>.
     * This constructor is protected and only used by subclasses.
     *
     * @param ss  the socket this server will listen to
     */
    protected ClassServer(ServerSocket ss) {
        server = ss;
        // creates an starts a thread
        newListener();
    }

    /**
     * Returns an array of bytes containing the bytes for
     * the file represented by the argument <b>path</b>.
     *
     * @param path  the filepath of the file to serve
     *
     * @return  the bytes for the file
     *
     * @exception IOException  if the file corresponding to <b>path</b>
     *                         could not be loaded
     */
    public abstract byte[] getBytes(String path) throws IOException;

    /**
     * The "listen" thread that accepts a connection to the
     * server, parses the header to obtain the file name
     * and sends back the bytes for the file (or error
     * if the file is not found or the response was malformed).
     */
    public synchronized void run() {
        Socket socket;

        // accept a connection
        try {
            // this statement is blocking until someone connects to this server
            socket = server.accept();
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.severe("ClassServer Thread died: " + e.getMessage());
            }
            e.printStackTrace();
            return;
        }

        // create a new thread to accept the next connection
        newListener();

        try {
            DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());
            try {
                // get path to class file from header
                BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                // parse the header and find the filepath
                String path = getPath(in);
                // retrieve bytecodes
                byte[] bytecodes = getBytes(path);
                // send bytecodes in response (assumes HTTP/1.0 or later)
                try {
                    out.writeBytes("HTTP/1.0 200 OK\r\n");
                    out.writeBytes("Content-Length: "
                                   + bytecodes.length + "\r\n");
                    out.writeBytes("Content-Type: text/html\r\n\r\n");
                    out.write(bytecodes);
                    out.flush();
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine("served file: " + path);
                    }
                } catch (IOException e) {
                    LOGGER.warning(e.getMessage());
                    return;
                }

            } catch (Exception e) {
                // write out error response
                out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
                out.writeBytes("Content-Type: text/html\r\n\r\n");
                out.flush();
            }

        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.warning(e.getMessage());
            }
        }
    }

    /**
     * Create a new thread to listen.
     */
    private void newListener() {
        (new Thread(this)).start();
    }

    /**
     * Returns the path to the file obtained from
     * parsing the HTML header.
     *
     * @param in  reader to get the HTML header from
     *
     * @return  the path of the requested file
     *
     * @exception IOException  thrown if there are problems with the reader
     *                         or the format of the header can't be parsed
     */
    private static String getPath(BufferedReader in) throws IOException {

        // example for a header to parse:

        // GET /filenamePath HTTP/1.1
        // Host: 127.0.0.1:3456
        // User-Agent: Mozilla/5.0 (Windows; U; Wi[...]/20040803 Firefox/0.9.3
        // Accept: text/xml,application/xml,app[...];q=0.8,image/png,*/*;q=0.5
        // Accept-Language: en-us,en;q=0.5
        // Accept-Encoding: gzip,deflate
        // Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
        // Keep-Alive: 300
        // Connection: keep-alive

        // we need to find the filenamePath in the first line
        String line = in.readLine();
        String path = "";
        // start of the first line in the request header
        String get = "GET /";

        // extract class from GET line
        if (line.startsWith(get)) {
            line = line.substring(get.length(), line.length() - 1).trim();
            // blanks in the filename are transformed to %20
            int index = line.lastIndexOf(' ');
            if (index != -1) {
                path = line.substring(0, index);
            }
        }
        // the path may contain encoded character
        // for example the blanks in an URL are converted to %20
        path = URLDecoder.decode(path, "UTF-8");

        // eat the rest of the header
        do {
            line = in.readLine();
        } while ((line.length() != 0)
                && (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

        if (path.length() != 0) {
            return path;
        } else {
            LOGGER.warning("Malforned Header for filerequest");
            throw new IOException("Malformed Header");
        }
    }
}

