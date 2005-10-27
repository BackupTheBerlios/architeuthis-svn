/*
 * file:        Option.java
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

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The base class for modeling a single commandline option.
 * <p/>
 * This class implements the prefix, key and name of a commandline option as
 * well as minimal and maximal number of parameters. An object of this class
 * can be obtained by the
 * {@link  ParameterParser#createOptionForKey createOptionForKey}
 * or
 * {@link  ParameterParser#createOptionForName createOptionForName}
 * methods of the
 * {@link  ParameterParser ParameterParser}
 * class.
 * <p>
 * Default values are:
 * <ul>
 *  <li>prefix: "-"</li>
 *  <li>key: "key"</li>
 *  <li>name: "option"</li>
 *  <li>isOptional: true</li>
 *  <li>maxParameter: 0</li>
 *  <li>minParameter: 0</li>
 * </ul>
 * @author Michael Wohlfart
 * @see ParameterParser
 *
 */
public class Option {

    /**
     * The name for this option is the default name for all parameters.
     */
    private static final String DEFAULT_NAME = "option";
    /**
     * The default option key string
     */
    private static final String DEFAULT_KEY = "key";
    /**
     * The default value used as prefix for all implementations, the default
     * value can be changed with the setPrefix method.
     */
    private static final String DEFAULT_PREXFIX = "-";
    /**
     * The default pattern String to match parameters with
     * (match any input by default).
     */
    private static final String DEFAULT_PATTERN_STRING = ".*";


    /**
     * No parameters are allowed for this option.
     */
    public static final int ZERO_PARAMETERS_CHECK = 1;
    /**
     * Excactly one parameter required.
     */
    public static final int ONE_PARAMETER_CHECK = 2;
    /**
     * Any numbmer of parameters allowed.
     */
    public static final int ZERO_OR_MORE_PARAMETERS_CHECK = 3;
    /**
     * At least one parameter required.
     */
    public static final int ONE_OR_MORE_PARAMETERS_CHECK = 4;


    /**
     * The maximum number of parameters shown as optional parameters
     * in the toString() method.<br/>
     * Any more parameters are abbreviated by dots.
     */
    static final int MAX_OPT_SHOWN_PARAMETERS = 5;  //package-private


    /**
     * The ArrayList used to store the arguments, this way
     * the order of the parameters is visible to the user.
     */
    private ArrayList parameters = new ArrayList();
    /**
     * Flag becomes <code>true</code> if this option is found in a commandline.
     */
    private boolean isEnabled = false;


    /**
     * The default name of this option.
     */
    private String name = DEFAULT_NAME;
    /**
     * The key used to identify this option.
     */
    private String key = DEFAULT_KEY;
    /**
     * The prefix used for this option, the prefix is usually something
     * like "-" or "--" or may be "/" or "\" on windows.
     */
    private String prefix = DEFAULT_PREXFIX;
    /**
     * Flag indicating if this options is optional.
     */
    private boolean isOptional = true;
    /**
     * The minimum number of parameters.
     */
    private int minParameters = 0;
    /**
     * The maximim number of parameters.
     */
    private int maxParameters = 0;
    /**
     * The compiled pattern to match parameters.
     * (this is not implemented so far)
     */
    private Pattern parameterPattern = Pattern.compile(DEFAULT_PATTERN_STRING);


    /**
     * The default constructor.
     */
    public Option() {

    }

    /**
     * A constructor for setting the key only.
     *
     * @param key the parameter key
     */
    public Option(String key) {
        this.key = key;
        this.name = key;
    }

    /**
     * A constructor for setting key and option name.
     *
     * @param key the parameter key
     * @param name the option name
     */
    public Option(String key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * A constructor for setting key, name and parameter check.
     *
     * @param key the parameter key
     * @param name the option name
     * @param parameterCheck defines the number of expected parameters
     */
    public Option(String key, String name, int parameterCheck) {
        this.key = key;
        this.name = name;
        this.setParameterNumberCheck(parameterCheck);
    }

    /**
     * A constructor for setting key, name and optional parameter.
     *
     * @param key the parameter key
     * @param name the option name
     * @param isOptional flag or optional/non-optional attribute
     */
    public Option(String key, String name, boolean isOptional) {
        this.key = key;
        this.name = name;
        this.isOptional = isOptional;
    }


    /**
     * A constructor for setting key, name, optional parameter and
     * paramter check.
     *
     * @param key the parameter key
     * @param name the option name
     * @param isOptional flag or optional/non-optional attribute
     * @param parameterCheck defines the number of expected parameters
     */
    public Option(String key, String name,
            boolean isOptional, int parameterCheck) {
        this.key = key;
        this.name = name;
        this.isOptional = isOptional;
        this.setParameterNumberCheck(parameterCheck);
    }



    /**
     * The setter for the prefix attribute.
     *
     * @param prefix the prefix being used
     *
     * @return this object
     */
    public Option setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * The setter for the key attribute.
     *
     * @param key the key being used
     *
     * @return this object
     */
    public Option setKey(String key) {
        this.key = key;
        return this;
    }
    /**
     * The setter for the optional attribute.
     *
     * @param isOptional true if this options is not optional
     *
     * @return this object
     */
    public Option setOptional(boolean isOptional) {
        this.isOptional = isOptional;
        return this;
    }

    /**
     * Used to set the allowed number of parameters for this option.
     *
     * @param parameterCheck defining the expected number of paramters
     * for this option
     * @return this object
     *
     */
    public Option setParameterNumberCheck(int parameterCheck) {
        switch (parameterCheck) {
        case ZERO_OR_MORE_PARAMETERS_CHECK:
            minParameters = 0;
            maxParameters = Integer.MAX_VALUE;
            break;
        case ONE_OR_MORE_PARAMETERS_CHECK:
            minParameters = 1;
            maxParameters = Integer.MAX_VALUE;
            break;
        case ZERO_PARAMETERS_CHECK:
            minParameters = 0;
            maxParameters = 0;
            break;
        case ONE_PARAMETER_CHECK:
            minParameters = 1;
            maxParameters = 1;
            break;
        default:
            assert (false);
        }
        return this;
    }

    /**
     * Set a regular expression to match the parameters with.
     *
     * @param regex a regular expression defining the syntax of
     * the parameter
     *
     * @return this object
     */
    public Option setParameterPatternCheck(String regex) {
        parameterPattern = Pattern.compile(regex);
        return this;
    }

    /**
     * Set the name of all parameters, ascending numbers are appended
     * to the optionName if more than on parameter is allowed for this option.
     *
     * @param name String for the option, this String is used
     * for any the toString Method
     *
     * @return this object
     */
    public Option setName(String name) {
        this.name = name;
        return this;
    }



    ///////////////////////////////////////////////////////////////////////
    // the following methods should only be accessed by the parameter parser

    /**
     * The setter for the key attribute.
     *
     * @return the key for this option
     */
    String getKey() {  // package-private
        return key;
    }

    /**
     * The setter for the key attribute.
     *
     * @return the key for this option
     */
    String getName() {  // package-private
        return name;
    }

    /**
     * Method to check if a String matches prefix + key of this option.
     *
     * @param argument a String found in the argv list
     * @return true if the String matches prefix + key
     */
    boolean isMatch(String argument) {  // package-private
        String tag = prefix + key;
        return (argument.equals(tag));
    }


    /**
     * The setter for the <code>isEnabled</code> attribute.
     *
     * @param isEnabled true if this option is found in a commandline
     */
    void setEnabled(boolean isEnabled) {  // package-private
        this.isEnabled = isEnabled;
    }

    /**
     * Check if this option is enabled.
     *
     * @return true if this option is enabled, this means it is found
     * in a parsed commandline
     */
    boolean isEnabled() {  // package-private
        return isEnabled;
    }

    /**
     * Check if this Option is valid.
     *
     * @throws ParameterParserException if this option is not valid
     *
     */
    public void checkValid() throws ParameterParserException {

        if (!isOptional() && !isEnabled()) {
            // option is not enabled and not optional
            throw new ParameterParserException(
                                               "Missing non optional Option: "
                                               + this);
        }

        if (isEnabled()) {
            if (parameters.size() < minParameters) {
                // option is not enabled and not optional
                throw new ParameterParserException(
                                             "Missing parameters for Option: "
                                             + this);
            }

            if (parameters.size() > maxParameters) {
                // option is not enabled and not optional
                throw new ParameterParserException(
                                             "Too many parameters for Option: "
                                             + this);
            }
        }
    }


    /**
     * Check if this option is optional.
     *
     * @return true if this is an optional option
     */
    boolean isOptional() {  // package-private
        return isOptional;
    }

    /**
     * Check if this opject can take one more parameter.
     *
     * @return true if this option can take one more parameter for
     * it's parameter list
     */
    boolean canTakeParameter() {  // package-private
        return (parameters.size() < maxParameters);
    }

    /**
     * Check if a parameter matches the parameter pattern of this option.
     *
     * @param parameter the parameter for this option
     * @return true if the parameter matches the parameter pattern
     */
    boolean canMatchParameter(String parameter) {
        return parameterPattern.matcher(parameter).matches();
    }

    /**
     * This method should only be called by the
     * {@link net.wohlfart.ParameterParser ParameterParser}
     * and is used to add a parameter to the parameter list of this option.
     *
     * @param parameter a string to be added to the parameter list
     *
     * @throws ParameterParserException if there are any problems with the
     * added parameter
     */
    //  package-private
    void addParameter(String parameter) throws ParameterParserException {
        // check if we can take a parameter
        if (!canTakeParameter()) {
            throw new ParameterParserException(
            "Option can't take parameter");
        }
        // check if the parameter matches the pattern
        if (!canMatchParameter(parameter)) {
            throw new ParameterParserException(
            "Parameter doesn't match the defined pattern");
        }
        parameters.add(parameter);
    }


    /**
     * This method resets any status data of this option. Used by the
     * ParameterParser if a new command line is about to be parsed.
     *
     */
    void reset() {  // package-private
        parameters.clear();
        isEnabled = false;
    }

    /**
     * Accessor for the parameter vector.
     *
     * @return a vector of parameters
     */
    ArrayList getParameterList() {  // package-private
        return parameters;
    }

    /**
     * Accessor for the first parameter of the parameter vector.
     *
     * @return the first parameter
     */
    String getParameter() {  // package-private
        assert (parameters.size() > 0);
        return (String) parameters.get(0);
    }




    /**
     * Method used to return the syntax of this option. This method is used by
     * the ParameterParser to compose a syntax String for a complete comand
     * line.
     *
     * @return a String for debugging
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(prefix);
        result.append(key);

        if (isOptional()) {
            result.insert(0, "[");
        }

        // append the parameter names:

        // all non-optional (minimal) stuff:
        for (int i = 1; i <= minParameters; i++) {
            result.append(" <");
            result.append(name);
            if (maxParameters > 1) {
                result.append(i);
            }
            result.append(">");
        }

        // the optional (maximal) parameters:
        if (MAX_OPT_SHOWN_PARAMETERS < (maxParameters - minParameters)) {
            // we don't show all, just some dots
            result.append(" [");
            result.append(" <");
            result.append(name);
            result.append(minParameters + 1);
            result.append("> ... ");
            result.append("]");
        } else {
            // show all
            if (maxParameters > minParameters) {
                result.append(" [");
                for (int i = minParameters + 1; i <= maxParameters; i++) {
                    result.append(" <");
                    result.append(name);
                    if ((maxParameters - minParameters) > 1) {
                        result.append(i);
                    }
                    result.append(">");
                }
                result.append("]");
            }
        }


        if (isOptional()) {
            result.append("]");
        }


        return result.toString();
    }
}
