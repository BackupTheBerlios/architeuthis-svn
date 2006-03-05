/*
 * file:        ParameterParserTest.java
 * created:     21.10.2004
 * last change: 05.03.2006 by Dietmar Lippold
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * More tests for the getopt package.
 *
 * @author Michael Wohlfart
 *
 */
public class ParameterParserTest extends TestCase {
    /**
     * platform dependant newline
     */
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * Testing the simplified construction methods.
     *
     */
    public void testNewConstructionMethods() {
        ParameterParser parser = new ParameterParser();

        String[] argv1 = {"-b", "-debug", "--help"};


        Option option1 = parser.createOptionForKey("b")
        .setOptional(false);

        Option option2 = parser.createOptionForKey("debug")
        .setOptional(false);

        Option option3 = parser.createOptionForKey("help")
        .setOptional(false)
        .setPrefix("--");

        try {
            //System.out.println("parser: " + parser);
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            //System.out.println("Exception: " + ex);
            fail();
        }


        assertTrue(option1.isEnabled());
        assertTrue(option2.isEnabled());
        assertTrue(option3.isEnabled());


        // add an optional option an check again:
        Option option4 = parser.createOptionForKey("optional");

        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }

        assertTrue(option1.isEnabled());
        assertTrue(option2.isEnabled());
        assertTrue(option3.isEnabled());
        assertFalse(option4.isEnabled());


        // check for non optional options:
        String[] argv2 = {"-b", "-debug"};
        try {
            parser.parseAll(argv2);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }
        //System.out.println(parser);
        assertTrue(option1.isEnabled());
        assertTrue(option2.isEnabled());
        assertFalse(option3.isEnabled());
        assertFalse(option4.isEnabled());
    }


    /**
     * Some simple isEnabled tests.
     *
     */
    public void testSimpleEnabledOptions() {
        ParameterParser parser = new ParameterParser();

        String[] argv1 = {"-b", "-debug", "--help"};

        Option option1 = new Option();
        option1.setKey("b");
        option1.setOptional(false);
        parser.addOption(option1);

        Option option2 = new Option();
        option2.setKey("debug");
        option2.setOptional(false);
        parser.addOption(option2);

        Option option3 = new Option();
        option3.setKey("help");
        option3.setPrefix("--");
        option3.setOptional(false);
        parser.addOption(option3);

        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }


        assertTrue(option1.isEnabled());
        assertTrue(option2.isEnabled());
        assertTrue(option3.isEnabled());


        // add an optional option an check again:
        Option option4 = new Option();
        option4.setKey("optional");
        parser.addOption(option4);
        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }

        assertTrue(option1.isEnabled());
        assertTrue(option2.isEnabled());
        assertTrue(option3.isEnabled());
        assertFalse(option4.isEnabled());


        // check for non optional options:
        String[] argv2 = {"-b", "-debug"};
        try {
            parser.parseAll(argv2);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }
        //System.out.println(parser);
        assertTrue(option1.isEnabled());
        assertTrue(option2.isEnabled());
        assertFalse(option3.isEnabled());
        assertFalse(option4.isEnabled());
    }

    /**
     * Some simple parse tests.
     *
     */
    public void testParseParameters() {
        ParameterParser parser = new ParameterParser();
        String[] argv1 = {"-b", "one", "two"};

        Option option1 = new Option();
        option1.setKey("b");
        option1.setOptional(false);
        option1.setParameterNumberCheck(Option.ZERO_OR_MORE_PARAMETERS_CHECK);
        parser.addOption(option1);

        //System.out.println(parser);
        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }


        option1.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);
        //System.out.println(parser);
        try {
            parser.parseAll(argv1);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        option1.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        //System.out.println(parser);
        try {
            parser.parseAll(argv1);
            fail();
        } catch (ParameterParserException ex) {
            // nothing to do
        }

        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.setFreeParameterPosition(ParameterParser.END);
        //System.out.println(parser);
        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }
    }

    /**
     * Soem simple assignment tests.
     *
     */
    public void testParameterAssignment() {
        ParameterParser parser = new ParameterParser();
        String one = "one";
        String two = "two";
        String three = "three";
        String four = "four";

        String[] argv1 = {"-b", one, two, "-c", three, four};

        Option option1 = new Option();
        option1.setKey("b");
        option1.setOptional(false);
        option1.setParameterNumberCheck(Option.ZERO_OR_MORE_PARAMETERS_CHECK);
        parser.addOption(option1);

        Option option2 = new Option();
        option2.setKey("c");
        option2.setOptional(false);
        option2.setParameterNumberCheck(Option.ZERO_OR_MORE_PARAMETERS_CHECK);
        parser.addOption(option2);

        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }
        //System.out.println(parser);

        ArrayList params1 = parser.getParameterList(option1);
        assertTrue(one.equals(params1.get(0)));
        assertTrue(two.equals(params1.get(1)));

        ArrayList params2 = parser.getParameterList(option2);
        assertTrue(three.equals(params2.get(0)));
        assertTrue(four.equals(params2.get(1)));
    }

    /**
     * Some more simple assignment tests.
     *
     */
    public void testFreeParameters() {
        ParameterParser parser = new ParameterParser();
        String one = "one";
        String two = "two";
        String three = "three";
        String four = "four";
        String five = "five";

        String[] argv1 = {"-b", one, two, three, "-c", four, five};

        Option option1 = new Option();
        option1.setKey("b");
        option1.setOptional(false);
        option1.setParameterNumberCheck(Option.ONE_OR_MORE_PARAMETERS_CHECK);
        parser.addOption(option1);

        Option option2 = new Option();
        option2.setKey("c");
        option2.setOptional(false);
        option2.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.addOption(option2);

        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.setFreeParameterPosition(ParameterParser.END);

        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }
        //System.out.println(parser);


        ArrayList params1 = parser.getParameterList(option1);
        assertTrue(one.equals(params1.get(0)));
        assertTrue(two.equals(params1.get(1)));
        assertTrue(three.equals(params1.get(2)));

        ArrayList params2 = parser.getParameterList(option2);
        assertTrue(four.equals(params2.get(0)));

        ArrayList params3 = parser.getFreeParameterList();
        assertTrue(five.equals(params3.get(0)));


    }

    /**
     * Testing the new parseOption method
     *
     */
    public void testSingleOptionParsing() {
        ParameterParser parser = new ParameterParser();
        String one = "one";
        String two = "two";
        String three = "three";
        String four = "four";
        String five = "five";
        String six = "six";

        String[] argv1 = {"-b", one, two, three, "-c", four, five, six};

        Option option1 = new Option();
        option1.setKey("b");
        option1.setOptional(false);
        option1.setParameterNumberCheck(Option.ZERO_OR_MORE_PARAMETERS_CHECK);
        parser.addOption(option1);

        Option option2 = new Option();
        option2.setKey("c");
        option2.setOptional(false);
        option2.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.addOption(option2);

        //System.out.println(parser);
        parser.setComandline(argv1);
        try {
            ArrayList params1 = parser.parseOption(option1).getParameterList();
            assertEquals(params1.size(), 3);
            assertTrue(one.equals(params1.get(0)));
            assertTrue(two.equals(params1.get(1)));
            assertTrue(three.equals(params1.get(2)));
        } catch (ParameterParserException ex) {
            fail();
        }

        try {
            ArrayList params2 = parser.parseOption(option2).getParameterList();
            assertEquals(params2.size(), 1);
            assertTrue(four.equals(params2.get(0)));
        } catch (ParameterParserException ex) {
            fail();
        }

        Option option3 = new Option();
        // test exception for unknown option
        try {
            option3.setKey("d");
            option3.setOptional(false);
            option3.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

            // shouldn't return:
            ArrayList params3 = parser.parseOption(option3).getParameterList();
            fail();
        } catch (ParameterParserException ex) {
        }

        // add the option as optional and check again
        option3.setOptional(true);
        parser.addOption(option3);

        try {
            assertFalse(parser.parseOption(option3).isEnabled());
        } catch (ParameterParserException ex) {
            fail();
        }

    }

    /**
     * Testing the Exceptions
     *
     */
    public void testSingleOptionFailedParsing() {
        ParameterParser parser = new ParameterParser();
        String one = "one";
        String two = "two";
        String three = "three";
        String four = "four";
        String five = "five";
        String six = "six";

        String[] argv1 = {"-b", one, two, three, "-c", four, five, six};

        Option option1 = parser.createOptionForKey("b")
        .setOptional(false)
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        //System.out.println(parser);
        parser.setComandline(argv1);

        try {
            parser.parseAll();
            fail();
        } catch (ParameterParserException ex) {
            // nothign to do
            //System.out.println(ex.toString());
        }


    }

    /**
     * Testing example 1 from the docs
     *
     */
    public void testDocExample1() {
        ParameterParser parser = new ParameterParser();

        Option debug1 = new Option();
        debug1.setKey("d");
        parser.addOption(debug1);

        Option debug2 = new Option();
        debug2.setKey("debug");
        debug2.setPrefix("--");
        parser.addOption(debug2);

        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        String[] argv = { "free", "-d", "--debug"};
        try {
            parser.parseAll(argv);
        } catch (ParameterParserException ex) {
            fail();
        }

        //System.out.println(parser.toString());
    }


    /**
     * Testing example 2 from the docs
     *
     */
    public void testDocExample2() {
        ParameterParser parser = new ParameterParser();
        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option option1 = parser.createOptionForKey("d");

        Option option2 = parser.createOptionForKey("debug").setPrefix("--");

        String[] argv = { "free", "-d", "--debug"};
        try {
            parser.parseAll(argv);
        } catch (ParameterParserException ex) {
            fail();
        }

        //System.out.println(parser.toString());
    }


    /**
     * Testing Example 3 from the docs
     *
     */
    public void testDocExample3() {
        int time = 5;
        int number = 20;
        String file = "a filename";
        int portnumber = 9876;


        // [-deadtime <Zeit>] [-deadtries <Anzahl>]
        //     -c <config-Datei> [-port <Port-Nummer>] [-t]
        String[] argv = {
                "-deadtime", new Integer(time) .toString(),
                "-deadtries", new Integer(number) .toString(),
                "-c", file,
                "-port", new Integer(portnumber) .toString(),
                "-t"
        };

        ParameterParser parser = new ParameterParser();

        Option configFile = parser.createOptionForKey("c")
        .setName("config-Datei")
        .setOptional(false)
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option port = parser.createOptionForKey("port")
        .setName("Port-Nummer")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option deadtime = parser.createOptionForKey("deadtime")
        .setName("Zeit")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option deadtries = parser.createOptionForKey("deadtries")
        .setName("Anzahl")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option additionalThreads = parser.createOptionForKey("t");

        //System.out.println(parser);

        try {
            parser.parseAll(argv);
        } catch (ParameterParserException ex) {
            fail();
        }

        assertEquals(parser.getParameter(configFile), file);
        assertEquals(parser.getParameterAsInt(deadtries), number);
        assertEquals(parser.getParameterAsInt(deadtime), time);
        assertEquals(parser.getParameterAsInt(port), portnumber);
        assertTrue(parser.isEnabled(additionalThreads));

    }


    /**
     * test the properties feature
     *
     */
    public void testPropertiesReading() {
        String portnumber = "50";
        String somename = "noname";

        String testFile =
            "port=" + portnumber + NEWLINE
            + "name=" + somename + NEWLINE;


        // this whole section is just for turning a simple string into an
        // InputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamWriter ow;
        try {
            ow = new OutputStreamWriter(os, "utf-8");
            ow.write(testFile, 0, testFile.length());
            ow.flush();
        } catch (UnsupportedEncodingException e1) {
            fail();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        ParameterParser parser = new ParameterParser();

        Option thePort = parser.createOptionForKey("p")
        .setName("port")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option theName = parser.createOptionForKey("n")
        .setName("name")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        // turning a properties file into a HashMap
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException ex) {
            fail();
        }

        HashMap in = new HashMap(props);

        try {
            //System.out.println(parser);
            parser.parseProperties(in);
        } catch (ParameterParserException ex) {
            System.out.println(ex);
            fail();
        }

        assertEquals(parser.getParameter(thePort), portnumber);
        assertEquals(parser.getParameter(theName), somename);

    }


    /**
     * Testing Example 3 from the docs
     *
     */
    public void testExceptions() {
        int time = 5;
        int number = 20;
        String file = "a filename";
        int portnumber = 9876;


        // [-deadtime <Zeit>] [-deadtries <Anzahl>]
        //     -c <config-Datei> [-port <Port-Nummer>] [-t]
        String[] argv1 = {
                "-deadtime", new Integer(time) .toString(),
                "-deadtries", new Integer(number) .toString(),
                "-c", file,
                "-port", new Integer(portnumber) .toString(),
                "-t"
        };

        String[] argv2 = {
                "-deadtime", new Integer(time) .toString(),
                "-deadtries", new Integer(number) .toString(),
                "-c", file,
                "-invalid",
                "-port", new Integer(portnumber) .toString(),
                "-t"
        };

        String[] argv3 = {
                "-deadtime", new Integer(time) .toString(),
                "-deadtries", new Integer(number) .toString(),
                "-port", new Integer(portnumber) .toString(),
                "-t"
        };


        String[] argv4 = {
                "-deadtime", new Integer(time) .toString(),
                "-deadtries", new Integer(number) .toString(),
                "-c",
                "-port", new Integer(portnumber) .toString(),
                "-t"
        };


        ParameterParser parser = new ParameterParser();

        Option configFile = parser.createOptionForKey("c")
        .setName("config-Datei")
        .setOptional(false)
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option port = parser.createOptionForKey("port")
        .setName("Port-Nummer")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option deadtime = parser.createOptionForKey("deadtime")
        .setName("Zeit")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option deadtries = parser.createOptionForKey("deadtries")
        .setName("Anzahl")
        .setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option additionalThreads = parser.createOptionForKey("t");

        //System.out.println(parser);

        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }

        assertEquals(parser.getParameter(configFile), file);
        assertEquals(parser.getParameterAsInt(deadtries), number);
        assertEquals(parser.getParameterAsInt(deadtime), time);
        assertEquals(parser.getParameterAsInt(port), portnumber);
        assertTrue(parser.isEnabled(additionalThreads));

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

    }

    /**
     * Testing for exception on too many free parameters
     *
     */
    public void testExceptions2() {
        String FREE1 = "free1";
        String FREE2 = "free2";

        String[] argv1 = { FREE1 };
        String[] argv2 = { FREE1, FREE2 };


        ParameterParser parser = new ParameterParser();
        parser.setFreeParameterPosition(ParameterParser.END);
        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        try {
            parser.parseAll(argv1);
        } catch (ParameterParserException ex) {
            fail();
        }

        try {
            parser.parseAll(argv2);
            fail();
        } catch (ParameterParserException ex) {
        }

    }


    /**
     * Testing for unknown parameters exception
     *
     */
    public void testExceptions3() {
        String prefix = "wurzelsepp";
        String key1 = "param1";
        String key2 = "param2";
        String value1 = "value1";
        String value2 = "value2";

        String[] argv1 = { prefix + key1, value1, prefix + key2, value2 };


        Option option1 = new Option();
        option1.setKey(key1);
        option1.setPrefix(prefix);
        option1.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        Option option2 = new Option();
        option2.setKey(key2);
        option2.setPrefix(prefix);
        option2.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);


        ParameterParser parser1 = new ParameterParser();
        parser1.setFreeParameterPosition(ParameterParser.END);
        parser1.addOption(option2);

        try {
            parser1.parseAll(argv1);
            fail();
        } catch (ParameterParserException ex) {
        }




        // prefix + key1, value1, prefix + key2, value2
        ParameterParser parser2 = new ParameterParser();
        parser2.setFreeParameterPosition(ParameterParser.START);
        parser2.setFreeParameterNumberCheck(Option.ZERO_OR_MORE_PARAMETERS_CHECK);
        parser2.addOption(option2);

        try {
            parser2.parseAll(argv1);
            assertEquals(parser2.getParameter(option2), value2);
            // the unknown parameters are parsed as free parameters:
            assertEquals(parser2.getFreeParameterList().toArray()[0], prefix + key1);
            assertEquals(parser2.getFreeParameterList().toArray()[1], value1);
        } catch (ParameterParserException ex) {
            fail();
        }




        // prefix + key1, value1, prefix + key2, value2
        Option option3 = new Option();
        option3.setKey(key2);
        option3.setPrefix(prefix);
        option3.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);


        ParameterParser parser3 = new ParameterParser();
        parser3.setFreeParameterPosition(ParameterParser.START);
        parser3.addOption(option3);

        try {
            parser3.parseAll(argv1);
            fail();
        } catch (ParameterParserException ex) {
        }

        // prefix + key1, value1, prefix + key2, value2
        Option option4 = new Option();
        option4.setKey(key2);
        option4.setPrefix(prefix);
        option4.setParameterNumberCheck(Option.ZERO_PARAMETERS_CHECK);


        ParameterParser parser4 = new ParameterParser();
        parser4.setFreeParameterPosition(ParameterParser.END);
        parser4.addOption(option3);

        try {
            parser4.parseAll(argv1);
            fail();
        } catch (ParameterParserException ex) {
        }


    }



    public void testPropertiesReading2() {
        String filename = "param-config";


        // create a input stream to simulate a properties file
        String fileContent = "param";

        String[] args = { "-config", filename, "zzz" };


        ParameterParser parser = new ParameterParser();
        Option          helpSwitch, paramSwitch, configOption;
        String          freierParameter;
        boolean         paramVorhanden;

        helpSwitch = new Option("help");
        parser.addOption(helpSwitch);

        paramSwitch = new Option("param");
        //paramSwitch.setName("param");  // the name for the properties file !
        parser.addOption(paramSwitch);

        configOption = new Option("config");
        configOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        configOption.setName("configfile");
        parser.addOption(configOption);

        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        parser.setFreeParameterPosition(ParameterParser.END);

        parser.setComandline(args);


        try {
            parser.parseOption(configOption);
        } catch (ParameterParserException e1) {
            fail();
        }
        assertTrue(parser.isEnabled(configOption));
        assertEquals(parser.getParameter(configOption), filename);


        try {
            // convert fileContent to input stream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes("UTF-8"));
            Properties props = new Properties();
            props.load(inputStream);
            HashMap propsHashMap = new HashMap(props);


            try {
                parser.parseProperties(propsHashMap);
                assertTrue(parser.isEnabled(paramSwitch));
            } catch (ParameterParserException e) {
                fail();
            }



        } catch (UnsupportedEncodingException e) {
            fail();
        } catch (IOException e) {
            fail();
        }


    }


    /*
param
optionfile=datei
     */


    public void testPropertiesReading3() {
        String filename = "param-config";


        // create a input stream to simulate a properties file
        String fileContent = "param " + NEWLINE
        + "optionfile=datei" + NEWLINE;

        String[] args = { "-config", filename, "zzz" };


        ParameterParser parser = new ParameterParser();
        Option          helpSwitch, paramSwitch, paramOption, configOption;
        String          optionWert, freierParameter;
        boolean         paramVorhanden;

        helpSwitch = new Option("help");
        parser.addOption(helpSwitch);

        paramSwitch = new Option("param");
        parser.addOption(paramSwitch);

        paramOption = new Option("option");
        paramOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        paramOption.setName("optionfile");
        parser.addOption(paramOption);

        configOption = new Option("config");
        configOption.setParameterNumberCheck(Option.ONE_PARAMETER_CHECK);
        configOption.setName("configfile");
        parser.addOption(configOption);

        parser.setFreeParameterPosition(ParameterParser.END);
        parser.setFreeParameterNumberCheck(Option.ONE_PARAMETER_CHECK);

        parser.setComandline(args);


        try {
            parser.parseOption(helpSwitch);
            assertFalse(parser.isEnabled(helpSwitch));
        } catch (ParameterParserException e1) {
            fail();
        }

        try {
            parser.parseOption(configOption);
            assertTrue(parser.isEnabled(configOption));
            assertEquals(parser.getParameter(configOption), filename);

        } catch (ParameterParserException e1) {
            fail();
        }


        try {
            // convert fileContent to input stream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes("UTF-8"));
            Properties props = new Properties();
            props.load(inputStream);
            HashMap propsHashMap = new HashMap(props);


            try {
                parser.parseAll(args, propsHashMap);
                assertTrue(parser.isEnabled(paramSwitch));

                assertTrue(parser.isEnabled(paramOption));
                assertEquals(parser.getParameter(paramOption), "datei");

                assertTrue(parser.isEnabled(configOption));
                assertEquals(parser.getParameter(configOption), filename);

            } catch (ParameterParserException e) {
                e.printStackTrace();
                fail();
            }


            /*
            try {
                parser.parseAll(args);
            } catch (ParameterParserException e) {
                e.printStackTrace();
                fail();
            }
            */

        } catch (UnsupportedEncodingException e) {
            fail();
        } catch (IOException e) {
            fail();
        }


    }

}
