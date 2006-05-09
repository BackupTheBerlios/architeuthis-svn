/*
 * file:        ParameterParserArchiTest.java
 * created:     21.10.2004
 * last change: 06.03.2006 by Dietmar Lippold
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


package de.unistuttgart.commandline;

import junit.framework.TestCase;

/**
 * Test for architeuthis cammand line parameters
 *
 */
public class ParameterParserArchiTest extends TestCase {

    /**
     *  test this syntax:
     *  <address> [-d] [--debug]
     */
    public void testOperativeParameters() {
        ParameterParser parser = new ParameterParser();

        Option debug1 = new Option();
        Option debug2 = new Option();


        debug1.setKey("d");
        parser.addOption(debug1);

        debug2.setKey("debug");
        debug2.setPrefix("--");
        parser.addOption(debug2);

        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        // valid:
        String[] argv1 = {"host:port", "--debug"};
        String[] argv2 = {"host:port", "-d"};
        String[] argv3 = {"host:port"};
        // fail:
        String[] argv4 = {"host:port", "-d", "invalid"};
        String[] argv5 = {"-d", "invalid", "host:port"};
        String[] argv6 = {"-d", "invalid"};

        try {
            parser.setComandline(argv1);
            parser.parseAll();
            assertTrue(parser.isEnabled(debug2));
            assertFalse(parser.isEnabled(debug1));
            assertEquals(parser.getFreeParameter(), "host:port");

            parser.setComandline(argv2);
            parser.parseAll();
            assertTrue(parser.isEnabled(debug1));
            assertFalse(parser.isEnabled(debug2));

            parser.setComandline(argv3);
            parser.parseAll();
            assertFalse(parser.isEnabled(debug1));
            assertFalse(parser.isEnabled(debug2));
        } catch (ParameterParserException ex) {
            fail();
        }

        parser.setComandline(argv4);
        try {
            parser.parseAll();
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        try {
            parser.setComandline(argv5);
            parser.parseAll();
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        try {
            parser.setComandline(argv6);
            parser.parseAll();
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        //System.out.println(parser);

    }


    /**
     * test this syntax
     *
     *   -c <config-Datei>
     *   -port <Port-Nummer>
     *   -deadtime <Zeit>
     *   -deadtries <Anzahl>
     *
     *
     */
    public void testDispatcherParameters() {
        ParameterParser parser = new ParameterParser();

        Option configFile = new Option();
        Option port = new Option();
        Option deadtime = new Option();
        Option deadtries = new Option();

        configFile.setKey("c");
        configFile.setDescription("config-Datei");
        configFile.setOptional(false);
        configFile.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.addOption(configFile);

        port.setKey("port");
        port.setDescription("Port-Nummer");
        port.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.addOption(port);

        deadtime.setKey("deadtime");
        deadtime.setDescription("Zeit");
        deadtime.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.addOption(deadtime);

        deadtries.setKey("deadtries");
        deadtries.setDescription("Anzahl");
        deadtries.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.addOption(deadtries);



        // valid:
        String[] argv1 = {"-c", "filename", "-deadtries", "5"};
        // fail:
        String[] argv2 = {"host:port", "-d"};
        String[] argv3 = {"host:port"};
        String[] argv4 = {"host:port", "-d", "invalid"};
        String[] argv5 = {"-d", "invalid", "host:port"};
        String[] argv6 = {"-d", "invalid"};

        //System.out.println(parser);
        try {
            parser.parseAll(argv1);
            assertEquals(parser.getParameter(configFile), "filename");
            assertTrue(parser.isEnabled(deadtries));
            assertEquals(parser.getParameter(deadtries), "5");
        } catch (ParameterParserException ex) {
            System.out.println(ex);
            fail();
        }


        try {
            parser.parseAll(argv2);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        try {
            parser.parseAll(argv3);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        try {
            parser.parseAll(argv4);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        try {
            parser.parseAll(argv5);
            fail();
        } catch (ParameterParserException ex) {
             // nothing to do
        }

        try {
            parser.parseAll(argv6);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }


    }
}

