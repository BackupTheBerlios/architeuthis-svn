/*
 * file:        AllTests.java
 * created:     21.10.2004
 * last change: 07.12.2004 by Michael Wohlfart
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


package de.unistuttgart.architeuthis.misc.commandline;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The testsuite for the <code>net.wohlfart.getopt</code> package.
 *
 * @author Michael Wohlfart
 *
 */
public class AllTests {


    /**
     * Creates and returns the Testsuit for the
     * <code>net.wohlfart.getopt</code> package.
     *
     * @return Test for this package
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for net.wohlfart.getopt");
        //$JUnit-BEGIN$
        suite.addTestSuite(ParameterParserTest.class);
        suite.addTestSuite(ParameterParserArchiTest.class);
        //$JUnit-END$
        return suite;
    }
}
