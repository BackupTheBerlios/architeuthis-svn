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

/*
 * Class to parse a sequence of Commandline Options.
 * Ideas from the JavaWorld Article "Processing command line
 * arguments in Java: Case closed" by Matthias Laux.
 *
 *
 *
 *
 */
package de.unistuttgart.architeuthis.misc.commandline;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

/**
 * This class implements a simple command line parser.
 * <p/>
 * Basicly this class implements a container for a set of
 * {@link Option Option}
 * objects and some methods to assign command line arguments to
 * these options.<br/>
 * The methods
 * {@link #addOption addOption},
 * {@link #createOptionForKey createOptionForKey} and
 * {@link #createOptionForName createOptionForName} can be
 * used to compose the set of options.<br/>
 * The methods
 * {@link #parseAll parseAll} and
 * {@link #parseOption parseOption} can be used to assign and return
 * the parameters for these options.
 *
 * @author Michael Wohlfart
 *
 */
public class ParameterParser {
    /**
     * The default name for the free parameters.
     */
    private static final String DEFAULT_PARAMETER_NAME = "parameter";

    /**
     * The free parameters are at the beginning.
     */
    public static final int START = 1;

    /**
     * The free parameters are at the end.
     */
    public static final int END = 2;


    /**
     * A set for the option keys.
     */
    //private HashMap optionKeyMap = new HashMap();
    private HashSet optionSet = new HashSet();

    /**
     * Array to store the command line between the call of the
     * {@link #setComandline(String[]) setComandline}
     * and
     * {@link #parseOption(Option) parseOption}
     * method call
     */
    private String[] argv = null;

    /**
     * minimum number of parameters
     */
    private int minParameters = 0;

    /**
     * maximim number of parameters
     */
    private int maxParameters = 0;


    /**
     * storage for free parameters
     */
    private ArrayList freeParameters = new ArrayList();

    /**
     * the position of the free parameters
     */
    private int freeParameterPosition = START;

    /**
     * the name for the free parameters in this commandline
     */
    private String parameterName = DEFAULT_PARAMETER_NAME;

    /**
     * Method to add an option to the ParameterParser. Any previous added
     * option with the same key or the same name are removed.
     *
     * @param option Option object to be added to this parser
     */
    public synchronized void addOption(Option option) {
        optionSet.add(option);

        //optionKeyMap.put(option.getKey(), option);
    }

    /**
     * Method to remove and option fromt the ParameterParser.
     *
     * @param option Option object to be removed from this parser
     *
     */
    public synchronized void removeOption(Option option) {
        optionSet.remove(option);
    }


    /**
     * return true if any option matches with a given string form the
     * commandline
     *
     * @param string the string to be checked
     * @return true if any option matches the string
     */
    private boolean anyMatch(String string) {
        boolean foundMatch = false;

        Iterator iterator = optionSet .iterator();
        while (iterator.hasNext() && !foundMatch) {
            Option option = (Option) iterator.next();
            // keep looping as long as we don't have a match
            foundMatch = option.isMatch(string);
        }
        return foundMatch;
    }


    /**
     * A method to find and parse a single option ind the command line.</br>
     * This method can be used to pick a single option without parsing all
     * strings of a command line.
     *
     * @param option the option to be parsed
     * @throws ParameterParserException if there are any problems with the
     * parameters
     * @return the parsed option
     */
    public synchronized Option parseOption(Option option)
    throws ParameterParserException {
        assert (argv != null);
        // find the index for the matching string in the commandline
        int i = 0;
        while ((i < argv.length) && (!option.isMatch(argv[i]))) {
            i++;
        }

        if (i >= argv.length) {
            assert (false);
        } else {
            option.setEnabled(true);
            // move to the next string (should be the first parameter)
            i++;
            // parse as many parameters as we can for this option:
            while ((i < argv.length) && (!anyMatch(argv[i]))) {
                if (option.canTakeParameter()
                        && option.canMatchParameter(argv[i])) {
                    try {
                        // add the string to the last option
                        option.addParameter(argv[i]);
                    } catch (ParameterParserException ex) {
                        // leave the while loop
                        // (something went really wrong here)
                        break;
                    }
                } else {
                    break;
                }
                // move to next string
                i++;
            }
        }

        return option;
    }

    /**
     * Set an array of stings (usually the command line).<br/>
     * This method should be used before calling the
     * {@link #parseOption(Option) parseOption}
     * method.
     *
     * @param argv an array of strings
     */
    public void setComandline(String[] argv) {
        this.argv = argv;
    }


    /**
     * Method to set an array of strings and assign the parameters to the
     * option Opjects.
     *
     * @param argv array of strings
     * @throws ParameterParserException if there are any problems with
     * the parameters
     */
    public synchronized void parseAll(String[] argv)
    throws ParameterParserException {
        setComandline(argv);
        parseAll();
    }

    /**
     * read options from a properties input stream
     *
     * @param in input contaning the parameters
     * @throws ParameterParserException indicating syntax errors of
     * I/O Problems
     */
    public synchronized void parseProperties(HashMap in)
    throws ParameterParserException {


        Iterator input = in.keySet().iterator();

        // create a name map on the fly:
        Iterator iterator = optionSet.iterator();
        HashMap optionNameMap = new HashMap();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            optionNameMap.put(option.getName(), option);
        }


        while (input.hasNext()) {
            String name = (String) input.next();

            //System.out.println("size name list: " + optionNameMap.size());
            //System.out.println("size key list: " + optionNameMap.size());

            if (optionNameMap.containsKey(name)) {
                Option option = (Option) optionNameMap.get(name);
                String value = (String)in.get(name);
                if (option.canTakeParameter()
                        && option.canMatchParameter(value)) {
                    option.addParameter(value);
                } else {
                    throw new ParameterParserException("Unable to assign value "
                                                       + value + " to " + name);
                }
            } else {
                throw new ParameterParserException("Unknown Properties name: "
                                                   + name);
            }
        }



    }


    /**
     * Method to parse a commandline.
     * @throws ParameterParserException
     *
     * @throws ParameterParserException if there are any problems with
     * the parameters
     */
    public synchronized void parseAll() throws ParameterParserException {

        assert (argv != null);

        // scratch iterator for options
        Iterator iterator;

        // this list is reduced if we found a match, at the end of this method
        // this HashMap will contain only unused options
        HashSet optionsLeft = (HashSet) optionSet.clone();

        // reset all Option objects:
        iterator = optionsLeft.iterator();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            option.reset();
        }
        // reset the free parameter list
        freeParameters = new ArrayList();

        // store for strings we can's assign to any option
        ArrayList stringsUnknown = new ArrayList();

        // index to check each commandline string
        int i = 0;

        // read free parameters if they are first:
        if (freeParameterPosition == START) {
            while ((i < argv.length) && !anyMatch(argv[i])) {
                freeParameters.add(argv[i]);
                i++;
            }
        }

        // the valid option from the last loop run
        //   (may need some more parameters)
        Option lastOptionFound = null;
        // option found in the current loop run
        Option currentOptionFound = null;

        // we leave this loop after we finished parsing all strings
        //  from the input
        // (we use breaks in this loop, this is considered a
        //  bad coding-style :-/ )
        while (i < argv.length) {

            // iterator for all remaining Option objects
            iterator = optionsLeft.iterator();
            // run as long as we haven't found a match and still have
            // candidates to try:
            while ((iterator.hasNext()) && (currentOptionFound == null)) {
                Option option = (Option) iterator.next();
                // check for a match
                if (option.isMatch(argv[i])) {
                    // found a matching option
                    option.setEnabled(true);
                    // remove the option from the underlaying collection
                    // (the ArrayList)
                    iterator.remove();
                    currentOptionFound = option;
                }
            }

            // some cleanup if we didn't find an option for the current
            // string:
            if (currentOptionFound == null) {
                // we have an unparsable string, this may be a parameter
                // for the last option:
                // do we have a last Option at all:
                if (lastOptionFound == null) {
                    // leave the while loop
                    break;
                } else {
                    if (lastOptionFound.canTakeParameter()
                            && lastOptionFound.canMatchParameter(argv[i])) {
                        try {
                            // add the string to the last option
                            lastOptionFound.addParameter(argv[i]);
                            // we are still working on the last option, we
                            // need to remember this for the next loop
                            currentOptionFound = lastOptionFound;
                        } catch (ParameterParserException ex) {
                            // leave the while loop
                            // (something went really wrong here)
                            break;
                        }
                    } else {
                        // no idea where to put the argv[i] string, leave
                        // the while loop..
                        break;
                    }
                }
            }

            // if we don't find a matching option we check for parameters
            // for the last option, so we have to remember the last option
            // for the next loop run
            lastOptionFound = currentOptionFound;
            // looking for a new option match in the next loop run:
            currentOptionFound = null;

            // move to the next string
            i++;
        } // end while


        // read free parameters only if they are last:
        if (freeParameterPosition == END) {
            while (i < argv.length) {
                freeParameters.add(argv[i]);
                i++;
            }
        } else {
            // add remaining strings to the unknown vector:
            while (i < argv.length) {
                stringsUnknown.add(argv[i]);
                i++;
            }
            if (stringsUnknown.size() > 0) {
                // return the first unknown string
                throw new ParameterParserException("Unknown String: "
                                               + stringsUnknown.toArray()[0]);
            }
        }

        // check for free parameter number
        if (freeParameters.size() < minParameters) {
            throw new ParameterParserException("Missing parameters ");
        }

        // delegate checks to the Options:
        iterator = optionSet.iterator();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            option.checkValid();
        }
    }

    /**
     *
     * set the number of parameters allowed for this commandline
     *
     * @param parameterCheck tghe number of free parameters allowed for this
     *        commandline
     */
    public void setFreeParameterNumberCheck(int parameterCheck) {
        switch (parameterCheck) {
        case Option.ZERO_OR_MORE_PARAMETERS_CHECK:
            minParameters = 0;
            maxParameters = Integer.MAX_VALUE;
            break;
        case Option.ONE_OR_MORE_PARAMETERS_CHECK:
            minParameters = 1;
            maxParameters = Integer.MAX_VALUE;
            break;
        case Option.ZERO_PARAMETERS_CHECK:
            minParameters = 0;
            maxParameters = 0;
            break;
        case Option.ONE_PARAMETER_CHECK:
            minParameters = 1;
            maxParameters = 1;
            break;
        default:
            assert (false);
        }
    }

    /**
     * set the position for the free parameters
     *
     * @param parameterPosition START or END
     */
    public void setFreeParameterPosition(int parameterPosition) {
        this.freeParameterPosition = parameterPosition;
    }

    /**
     * set the name for the free parameters
     *
     * @param parameterPosition START or END
     */
    public void setFreeParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * aks for the parameter list of an option
     *
     * @param option the option to query for parameters
     * @return a Vector of paraemter Strings
     */
    public ArrayList getParameterList(Option option) {
        return option.getParameterList();
    }


    /**
     * Creates a new Option object and add the option to this parser.
     *
     * @param key the key of the new option
     * @return the new option
     */
    public Option createOptionForKey(String key) {
        Option option = new Option();
        option.setKey(key);
        addOption(option);
        return option;
    }


    /**
     * Creates a new Option object and add the option to this parser.
     *
     * @param name the name of the new option
     * @return the new option
     */
    public Option createOptionForName(String name) {
        Option option = new Option();
        option.setName(name);
        addOption(option);
        return option;
    }


    /**
     * Method to add a free parameter to this command line.
     *
     * @param parameter a free parameter to add
     * @throws ParameterParserException thrown if this parameter can
     *  be added as free parameter
     */
    /* package-private */ void addParameter(String parameter)
    throws ParameterParserException {
        // check if we can take a parameter
        if (!canTakeFreeParameter()) {
            throw new ParameterParserException("can't take free parameter: "
                                       + parameter);
        }
        freeParameters.add(parameter);
    }

    /**
     * check if this ParameterParser can take a free parameter
     *
     * @return true if this ParameterParser can take a free parameter
     */
    /* package-private */ boolean canTakeFreeParameter() {
        return (freeParameters.size() < maxParameters);
    }

    /**
     * returns the first parameter of an option
     *
     * @param option option to query for parameters
     * @return the first parameter
     */
    public String getParameter(Option option) {
        return option.getParameter();
    }

    /**
     * returns the first parameter of an option as int primitive
     *
     * @param option option to query for parameters
     * @return the first parameter as int
     */
    public int getParameterAsInt(Option option) {
        return Integer.parseInt(option.getParameter());
    }

    /**
     * accessor to the free parameter list
     *
     * @return vector of free parameters
     */
    public ArrayList getFreeParameterList() {
        return freeParameters;
    }


    /**
     * return a single parameter
     *
     * @return the first parameter of the free prarameter list
     */
    public String getFreeParameter() {
        assert (freeParameters != null);
        assert (freeParameters.size() > 0);
        return (String) freeParameters.get(0);
    }

    /**
     * return the state of a parameter
     *
     * @param option the option to check
     *
     * @return true if the option is enabled
     */
    public boolean isEnabled(Option option) {
        return option.isEnabled();
    }

    /**
     * returns a String for debugging
     *
     * @return Syntax String for this parser
     */
    public String toString() {

        StringBuffer optionString = new StringBuffer();
        Iterator iterator = optionSet.iterator();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            optionString.append(option.toString());
            optionString.append(" ");
        }

        StringBuffer freeParamString = new StringBuffer();
        // append the freeParameter names:
        // all non-optional (minimal) stuff:
        for (int i = 1; i <= minParameters; i++) {
            freeParamString.append(" <");
            freeParamString.append(parameterName);
            if (maxParameters > 1) {
                freeParamString.append(i);
            }
            freeParamString.append(">");
        }

        // the optional (maximal) parameters:
        if (Option.MAX_OPT_SHOWN_PARAMETERS < (maxParameters - minParameters)) {
            // we don't show all, just some dots
            freeParamString.append(" [");
            freeParamString.append(" <");
            freeParamString.append(parameterName);
            freeParamString.append(minParameters + 1);
            freeParamString.append("> ... ");
            freeParamString.append("]");
        } else {
            // show all
            if (maxParameters > minParameters) {
                freeParamString.append(" [");
                for (int i = minParameters + 1; i <= maxParameters; i++) {
                    freeParamString.append(" <");
                    freeParamString.append(parameterName);
                    if ((maxParameters - minParameters) > 1) {
                        freeParamString.append(i);
                    }
                    freeParamString.append(">");
                }
                freeParamString.append("]");
            }
        }



        // merging depends on the position of the free parameters
        StringBuffer result = null;
        switch (freeParameterPosition) {
        case START:
            result = freeParamString;
            result.append(" ");
            result.append(optionString);
            break;
        case END:
            result = optionString;
            result.append(" ");
            result.append(freeParamString);
            break;
        default:
            assert (false);
        }

        return result.toString();
    }


}
