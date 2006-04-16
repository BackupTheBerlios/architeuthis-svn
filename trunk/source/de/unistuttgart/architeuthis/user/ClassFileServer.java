/*
 * @(#)ClassFileServer.java    1.5 01/05/10
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
 *
 */

/**
 * Changes to the original Source from Sun:
 *
 * 20.08.2004 Michael Wohlfart, general cleanup:
 *  - changed Source formatting to be compatible with the java styleguide
 *  - added some comments
 *  - added logger
 *  - extra method of usage
 *
 * Last change: 16. Apr 2006 by Dietmar Lippold
 */


package de.unistuttgart.architeuthis.user;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * ClassFileServer.java -- a simple file server that can server
 * Http get request in both clear and secure channel.<p>
 *
 * The ClassFileServer implements a ClassServer that
 * reads files from the file system. See the
 * doc for the "Main" method for how to run this
 * server.
 */
public class ClassFileServer extends ClassServer {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER =
        Logger.getLogger(ClassFileServer.class.getName());

    /**
     * default port for this server
     */
    private static final int DEFAULT_SERVER_PORT = 2001;

    /**
     * the root directory where the files are located
     */
    private String docroot;

    /**
     * Constructs a ClassFileServer.
     *
     * @param ss       the Socket where this Server will wait for connects
     * @param docroot  the path where the server locates files
     *
     * @exception IOException any kind of IO Problems
     */
    public ClassFileServer(ServerSocket ss, String docroot) throws IOException {
        // starts a thread
        super(ss);
        this.docroot = docroot;
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("ClassFileServer running on port "
                        + ss.getLocalPort()
                        + " for root "
                        + docroot);
        }
    }

    /**
     * Returns an array of bytes containing the bytes for
     * the file represented by the argument <b>path</b>.
     *
     * @param path  the path to the file within the docroot directory
     *
     * @return  the bytes for the file
     *
     * @exception IOException  if the file corresponding to <b>path</b>
     *                         could not be loaded.
     */
    public byte[] getBytes(String path) throws IOException {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer("serving file from docroot: " + path);
        }

        File f = new File(docroot + File.separator + path);

        // filechecks:
        if (!f.exists()) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.warning("file doesn't exist: " + path);
            }
            throw new IOException("file doesn't exist: " + path);
        }

        if (!f.canRead()) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.warning("file is not readable: " + path);
            }
            throw new IOException("file is not readable: " + path);
        }

        if (!f.isFile()) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.warning("no file at: " + path);
            }
            throw new IOException("no file at: " + path);
        }

        int length = (int) f.length();
        if (length == 0) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.warning("file length is zero: " + path);
            }
            throw new IOException("file length is zero: " + path);
        }

        DataInputStream in = new DataInputStream(new FileInputStream(f));

        byte[] bytecodes = new byte[length];
        in.readFully(bytecodes);

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("end");
        }

        return bytecodes;
    }

    /**
     * print Usage to stdout
     */
    private static void printUsage() { // static because it's called in main()

        System.out.println(
            "USAGE: java ClassFileServer port docroot [TLS [true]]");
        System.out.println("");
        System.out.println(
                "If the third argument is TLS, it will start as\n"
                + "a TLS/SSL file server, otherwise, it will be\n"
                +  "an ordinary file server. \n"
                +  "If the fourth argument is true,it will require\n"
                + "client authentication as well.");
    }

    /**
     * Main method to create the class server that reads
     * files. This takes two command line arguments, the
     * port on which the server accepts requests and the
     * root of the path. To start up the server: <br><br>
     *
     * <code>   java ClassFileServer <port> <path>
     * </code><br><br>
     *
     * <code>   new ClassFileServer(port, docroot);
     * </code>
     *
     * @param args commandline parameters
     */
    public static void main(String[] args) {

        LOGGER.config("");

        // default parameter
        int port = DEFAULT_SERVER_PORT;
        String directory = "";
        String type = "PlainSocket";
        boolean clientAuth = false;

        // do we have enough parameters?
        if (args.length < 2) {
            ClassFileServer.printUsage();
        }

        // read the commandline parameter
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            directory = args[1];
        }
        if (args.length >= 3) {
            type = args[2];  // type may be a "TLS" string
        }
        if (args.length >= 4 && args[3].equals("true")) {
            clientAuth = true;
        }

        try {
            ServerSocketFactory ssf =
                ClassFileServer.getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            // using authentication
            if (clientAuth) {
                ((SSLServerSocket) ss).setNeedClientAuth(true);
            }

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("creating ClassFileServer at "
                        + ss.getLocalSocketAddress());
            }
            // create the classfileserver
            new ClassFileServer(ss, directory);

        } catch (IOException e) {
            LOGGER.severe("unable to start ClassServer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * create a Socket Factory to produce Sockets of the defined type
     *
     *
     * @param type  TLS or PlainSocket String to trigger encryption for the
     *              socket
     *
     * @return  a SocketFactory
     */
    private static ServerSocketFactory getServerSocketFactory(String type) {

        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try {
                // set up key manager to do server authentication
                SSLContext ctx;
                KeyManagerFactory kmf;
                KeyStore ks;
                char[] passphrase = "passphrase".toCharArray();

                ctx = SSLContext.getInstance("TLS");
                kmf = KeyManagerFactory.getInstance("SunX509");
                ks = KeyStore.getInstance("JKS");

                ks.load(new FileInputStream("testkeys"), passphrase);
                kmf.init(ks, passphrase);
                ctx.init(kmf.getKeyManagers(), null, null);

                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // just plain old Socket
            return ServerSocketFactory.getDefault();
        }

        return null;
    }
}

