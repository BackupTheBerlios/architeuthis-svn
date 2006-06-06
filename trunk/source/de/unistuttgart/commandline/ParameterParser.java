/*
 * file:        ParameterParser.java
 * created:     21.10.2004
 * last change: 06.06.2006 by Dietmar Lippold
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;

/**
 * This class implements a simple command line parser.
 * <p/>
 * Basicly this class implements a container for a set of
 * {@link Option Option} objects and some methods to assign command line
 * arguments to these options.<br/>
 * The methods {@link #addOption addOption},
 * {@link #createOptionForKey(String) createOptionForKey(String)} and
 * {@link #createOptionForKey(String, String) createOptionForKey(String, String)}
 * can be used to compose the set of options.<br/>
 * The methods {@link #parseAll parseAll} and {@link #parseOption parseOption}
 * can be used to assign and return the parameters for these options.<p>
 *
 * Ideas come from the JavaWorld Article <cite>Processing command line
 * arguments in Java: Case closed</cite> by Matthias Laux.
 *
 * @author Michael Wohlfart
 */
public class ParameterParser implements Serializable {

    /**
     * The default description for the free parameters.
     */
    private static final String DEFAULT_PARAMETER_DESCRIPTION = "parameter";

    /**
     * The free parameters are at the beginning.
     */
    public static final int START = 1;

    /**
     * The free parameters are at the end.
     */
    public static final int END = 2;

    /**
     * A list for the options.
     */
    private LinkedList optionList = new LinkedList();

    /**
     * Properties which are key-value-pairs of parameters.
     */
    private HashMap properties = new HashMap();

    /**
     * Array to store the command line between the call of the
     * {@link #setComandline(String[]) setComandline} and
     * {@link #parseOption(Option) parseOption} method call.
     */
    private String[] argv = null;

    /**
     * Minimum number of free parameters.
     */
    private int minFreeParameters = 0;

    /**
     * Maximim number of free parameters.
     */
    private int maxFreeParameters = 0;

    /**
     * Storage for free parameters.
     */
    private ArrayList freeParameters = new ArrayList();

    /**
     * The position of the free parameters.
     */
    private int freeParameterPosition = START;

    /**
     * The description for the free parameters in the commandline.
     */
    private String freeParameterDescription = DEFAULT_PARAMETER_DESCRIPTION;

    /**
     * Method to add an option to this instance. Any previous added option
     * with the same key is removed.
     *
     * @param option  Option object to be added to this parser.
     */
    public synchronized void addOption(Option option) {

        optionList.remove(option);
        optionList.add(option);
    }

    /**
     * Method to remove and option from the ParameterParser.
     *
     * @param option  Option object to be removed from this parser
     *
     */
    public synchronized void removeOption(Option option) {
        optionList.remove(option);
    }

    /**
     * Returns the stored option with the specified key. If no such option is
     * stored, <code>null</code> is returned.
     *
     * @param key  The key of the option to be returned.
     *
     * @return  The option with the specified key or <code>null</code>.
     */
    public synchronized Option getOption(String key) {
        Iterator optionIter;
        Option   option;

        optionIter = optionList.iterator();
        while (optionIter.hasNext()) {
            option = (Option) optionIter.next();
            if (option.getKey().equals(key)) {
                return option;
            }
        }
        return null;
    }

    /**
     * Returns a copy of the list of the stored options.
     *
     * @return  The list of stored options.
     */
    public synchronized LinkedList getOptionList() {
        return ((LinkedList) optionList.clone());
    }

    /**
     * Return <code>true</code> if and only if any option matches with a given
     * string form the commandline.
     *
     * @param string  the string to be checked.
     *
     * @return  <code>true</code> if and only if any option matches the
     *          string.
     */
    private boolean anyMatch(String string) {
        boolean foundMatch = false;

        Iterator iterator = optionList.iterator();
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
     * @param option  the option to be parsed.
     *
     * @return  the parsed option.
     *
     * @throws ParameterParserException  if there are any problems with the
     *                                   parameters.
     */
    public synchronized Option parseOption(Option option)
        throws ParameterParserException {

        // are there any arguments
        if (argv == null) {
            throw new ParameterParserException(
                    "no commandline paramters, "
                    + "use the setComandline(String[] argv) Method "
                    + "before calling parseOption(Option option)");
        }

        // is the requested option in our list
        if (!optionList.contains(option)) {
            throw new ParameterParserException(
                    "the option " + option
                    + " isn't in the list of option,"
                    + " use addOption(Option option) to add the option"
                    + " to the list");
        }

        // find the index for the matching string in the commandline
        int i = 0;
        while ((i < argv.length) && (!option.isMatch(argv[i]))) {
            i++;
        }

        if (i >= argv.length) {
            // found no match, set the option to disabled
            option.setEnabled(false);
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
     * Read options from a properties HashMap.
     *
     * @param props  HashMap contaning the parameters.
     *
     * @throws ParameterParserException  Wrong name or number of parameters.
     */
    protected synchronized void parseProperties(HashMap props)
        throws ParameterParserException {

        HashMap optionKeyMap = new HashMap();

        // create a key map from keys to options
        Iterator optIter = optionList.iterator();
        while (optIter.hasNext()) {
            Option option = (Option) optIter.next();
            optionKeyMap.put(option.getKey(), option);
        }

        Iterator propIter = props.keySet().iterator();
        while (propIter.hasNext()) {
            String name = (String) propIter.next();

            if (optionKeyMap.containsKey(name)) {
                Option option = (Option) optionKeyMap.get(name);
                String value = (String) props.get(name);

                // there may be no value assigned to this option
                if (value.length() == 0) {
                    option.setEnabled(true);
                } else {
                    option.setEnabled(true);
                    String[] paramValues = value.split("\\s+");
                    for (int pNo = 0; pNo < paramValues.length; pNo++) {
                        if (option.canTakeParameter()
                                && option.canMatchParameter(paramValues[pNo])) {
                            option.addParameter(paramValues[pNo]);
                        } else {
                            throw new ParameterParserException("Unable to assign"
                                                               + " value "
                                                               + paramValues[pNo]
                                                               + " to " + option);
                        }
                    }
                }
            } else {
                throw new ParameterParserException("Unknown properties name: "
                                                   + name);
            }
        }
    }

    /**
     * Method to parse a commandline.
     *
     * @throws ParameterParserException  if there are any problems with
     *                                   the parameters.
     */
    public synchronized void parseAll() throws ParameterParserException {

        assert (argv != null);

        // scratch iterator for options
        Iterator iterator;

        // this list is reduced if we found a match, at the end of this
        // method. It will contain only unused options.
        LinkedList optionsLeft = (LinkedList) optionList.clone();

        // reset all Option objects
        iterator = optionsLeft.iterator();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            option.reset();
        }

        // parse parameters
        parseProperties(this.properties);

        // reset the free parameter list
        freeParameters = new ArrayList();

        // store for strings we can's assign to any option
        ArrayList stringsUnknown = new ArrayList();

        // index to check each commandline string
        int argNo = 0;

        // read free parameters if they are first:
        if (freeParameterPosition == START) {
            while ((argNo < argv.length) && !anyMatch(argv[argNo])) {
                freeParameters.add(argv[argNo]);
                argNo++;
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
        while (argNo < argv.length) {

            // iterator for all remaining Option objects
            iterator = optionsLeft.iterator();
            // run as long as we haven't found a match and still have
            // candidates to try:
            while ((iterator.hasNext()) && (currentOptionFound == null)) {
                Option option = (Option) iterator.next();
                // check for a match
                if (option.isMatch(argv[argNo])) {
                    // found a matching option
                    option.reset();
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
                            && lastOptionFound.canMatchParameter(argv[argNo])) {
                        try {
                            // add the string to the last option
                            lastOptionFound.addParameter(argv[argNo]);

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
            argNo++;
        } // end while

        // read free parameters only if they are last:
        if (freeParameterPosition == END) {
            while (argNo < argv.length) {
                freeParameters.add(argv[argNo]);
                argNo++;
            }
        } else {
            // add remaining strings to the unknown vector:
            while (argNo < argv.length) {
                stringsUnknown.add(argv[argNo]);
                argNo++;
            }
            if (stringsUnknown.size() > 0) {
                // return the first unknown string
                throw new ParameterParserException("Unknown String: "
                                                   + stringsUnknown.toArray()[0]);
            }
        }

        // check for free parameter minimum number
        if (freeParameters.size() < minFreeParameters) {
            throw new ParameterParserException("Missing parameters ");
        }

        // check for free parameter maximum number
        if (freeParameters.size() > maxFreeParameters) {
            throw new ParameterParserException("Too many parameters: "
                                               + freeParameters);
        }

        // delegate checks to the Options:
        iterator = optionList.iterator();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            option.checkValid();
        }
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
     * Sets the parameters.
     *
     * @param props  HashMap contaning the parameters.
     */
    public void setProperties(HashMap props) {
        this.properties = props;
    }

    /**
     * Method to set an array of strings and assign the parameters to the
     * option Opjects.
     *
     * @param argv  array of parameters as strings.
     *
     * @throws ParameterParserException  if there are any problems with
     *                                   the parameters.
     */
    public synchronized void parseAll(String[] argv)
        throws ParameterParserException {

        setComandline(argv);
        parseAll();
    }

    /**
     * Method to set an array of strings and assign the parameters to the
     * option Opjects.
     *
     * @param argv   array of parameters as strings.
     * @param props  HashMap of parameters as key-value-pairs.
     *
     * @throws ParameterParserException  if there are any problems with
     *                                   the parameters.
     */
    public synchronized void parseAll(String[] argv, HashMap props)
        throws ParameterParserException {

        setComandline(argv);
        setProperties(props);
        parseAll();
    }

    /**
     * Set the number of parameters allowed for this commandline.
     *
     * @param parameterCheck  the number of free parameters allowed for the
     *                        commandline.
     */
    public synchronized void setFreeParameterNumberCheck(int parameterCheck) {

        switch (parameterCheck) {
        case Option.ZERO_OR_MORE_PARAMETERS_CHECK:
            minFreeParameters = 0;
            maxFreeParameters = Integer.MAX_VALUE;
            break;
        case Option.ONE_OR_MORE_PARAMETERS_CHECK:
            minFreeParameters = 1;
            maxFreeParameters = Integer.MAX_VALUE;
            break;
        case Option.ZERO_PARAMETERS_CHECK:
            minFreeParameters = 0;
            maxFreeParameters = 0;
            break;
        case Option.ONE_PARAMETER_CHECK:
            minFreeParameters = 1;
            maxFreeParameters = 1;
            break;
        default:
            assert (false);
        }
    }

    /**
     * Set the position for the free parameters
     *
     * @param parameterPosition  <code>START</code> or <code>END</code>.
     */
    public void setFreeParameterPosition(int parameterPosition) {
        this.freeParameterPosition = parameterPosition;
    }

    /**
     * Set the description for the free parameters.
     *
     * @param description  the description to set to.
     */
    public void setFreeParameterDescription(String description) {
        this.freeParameterDescription = description;
    }

    /**
     * Aks for the parameter list of an option.
     *
     * @param option  the option to query for parameters.
     *
     * @return  a Vector of parameter as Strings.
     */
    public ArrayList getParameterList(Option option) {
        return option.getParameterList();
    }

    /**
     * Creates a new Option object and add the option to this parser.
     *
     * @param key  the key of the new option.
     *
     * @return  the new option
     */
    public Option createOptionForKey(String key) {

        Option option = new Option(key);
        addOption(option);
        return option;
    }

    /**
     * Creates a new Option object and add the option to this parser.
     *
     * @param key          the key of the new option.
     * @param description  the description of the new option.
     *
     * @return  the new option.
     */
    public Option createOptionForKey(String key, String description) {

        Option option = new Option(key, description);
        addOption(option);
        return option;
    }

    /**
     * Method to add a free parameter to this command line.
     *
     * @param parameter  a free parameter to add.
     *
     * @throws ParameterParserException  thrown if this parameter can be added
     *                                   as free parameter.
     */
    void addParameter(String parameter) throws ParameterParserException {

        // check if we can take a parameter
        if (!canTakeFreeParameter()) {
            throw new ParameterParserException("can't take free parameter: "
                                               + parameter);
        }
        freeParameters.add(parameter);
    }

    /**
     * Check if this ParameterParser can take a free parameter.
     *
     * @return  <code>true</code> if and only if this ParameterParser can take
     *          a free parameter.
     */
    boolean canTakeFreeParameter() {
        return (freeParameters.size() < maxFreeParameters);
    }

    /**
     * Returns the first parameter of the given option
     *
     * @param option  option to query for the first parameter.
     *
     * @return  the first parameter of the option.
     */
    public String getParameter(Option option) {
        return option.getParameter();
    }

    /**
     * Returns the first parameter of an option as int primitive.
     *
     * @param option  option to query for parameters.
     *
     * @return  the value of the first parameter as <code>int</code>.
     */
    public int getParameterAsInt(Option option) {
        return Integer.parseInt(option.getParameter());
    }

    /**
     * Returns the first parameter of an option as long primitive.
     *
     * @param option  option to query for parameters.
     *
     * @return  the value of the first parameter as <code>long</code>.
     */
    public long getParameterAsLong(Option option) {
        return Long.parseLong(option.getParameter());
    }

    /**
     * Returns the first parameter of an option as float primitive.
     *
     * @param option  option to query for parameters.
     *
     * @return  the value of the first parameter as <code>float</code>.
     */
    public float getParameterAsFloat(Option option) {
        return Float.parseFloat(option.getParameter());
    }

    /**
     * Returns the first parameter of an option as double primitive.
     *
     * @param option  option to query for parameters.
     *
     * @return  the value of the first parameter as <code>double</code>.
     */
    public double getParameterAsDouble(Option option) {
        return Double.parseDouble(option.getParameter());
    }

    /**
     * Accessor to the free parameter list.
     *
     * @return  vector of free parameters.
     */
    public ArrayList getFreeParameterList() {
        return freeParameters;
    }

    /**
     * Return a single parameter.
     *
     * @return  the first parameter of the free prarameter list
     */
    public synchronized String getFreeParameter() {

        assert (freeParameters != null);
        assert (freeParameters.size() > 0);
        return (String) freeParameters.get(0);
    }

    /**
     * Return the state of a parameter.
     *
     * @param option  the option to check.
     *
     * @return  <code>true</code> if and only if the option is enabled.
     */
    public boolean isEnabled(Option option) {
        return option.isEnabled();
    }

    /**
     * Returns a description of this parser.
     *
     * @return  Syntax String for this parser.
     */
    public synchronized String toString() {
        StringBuffer optionString    = new StringBuffer();
        StringBuffer freeParamString = new StringBuffer();

        Iterator iterator = optionList.iterator();
        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();
            optionString.append(option.toString());
            optionString.append(" ");
        }

        // append the freeParameter description
        // all non-optional (minimal) stuff
        for (int i = 1; i <= minFreeParameters; i++) {
            freeParamString.append(" <");
            freeParamString.append(freeParameterDescription);
            if (maxFreeParameters > 1) {
                freeParamString.append("." + i);
            }
            freeParamString.append(">");
        }

        // the optional (maximal) parameters
        if (Option.MAX_OPT_SHOWN_PARAMETERS < (maxFreeParameters
                                               - minFreeParameters)) {
            // we don't show all, just some dots
            freeParamString.append(" [");
            freeParamString.append(" <");
            freeParamString.append(freeParameterDescription);
            freeParamString.append("." + (minFreeParameters + 1));
            freeParamString.append("> ... ");
            freeParamString.append("]");
        } else {
            // show all
            if (maxFreeParameters > minFreeParameters) {
                freeParamString.append(" [");
                for (int i = minFreeParameters + 1; i <= maxFreeParameters; i++) {
                    freeParamString.append(" <");
                    freeParamString.append(freeParameterDescription);
                    if ((maxFreeParameters - minFreeParameters) > 1) {
                        freeParamString.append("." + i);
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

