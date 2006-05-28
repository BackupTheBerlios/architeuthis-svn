/*
 * file:        Option.java
 * created:     21.10.2004
 * last change: 28.05.2006 by Dietmar Lippold
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
import java.util.regex.Pattern;

/**
 * The base class for modeling a single commandline option.
 * <p/>
 * This class implements the prefix, key and name of a commandline option as
 * well as minimal and maximal number of parameters. An object of this class
 * can be obtained by the
 * {@link #createOptionForKey(String) createOptionForKey(String)}
 * or
 * {@link #createOptionForKey(String, String) createOptionForKey(String, String)}
 * methods of the
 * {@link  ParameterParser ParameterParser}
 * class.
 * <p>
 * Default values are:
 * <ul>
 *  <li>prefix: "-"</li>
 *  <li>key: "key"</li>
 *  <li>description: "value"</li>
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
     * The default option key string
     */
    private static final String DEFAULT_KEY = "key";

    /**
     * The default description for all parameters.
     */
    private static final String DEFAULT_DESCRIPTION = "value";

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
    static final int MAX_OPT_SHOWN_PARAMETERS = 5;

    /**
     * The <code>ArrayList</code> used to store the parameter values as
     * strings. This way the order of the parameters is visible to the user.
     */
    private ArrayList parameters = new ArrayList();

    /**
     * Flag becomes <code>true</code> if this option is found in a
     * commandline or in a properties file.
     */
    private boolean isEnabled = false;

    /**
     * The key used to identify this option.
     */
    private String key = DEFAULT_KEY;

    /**
     * The description of this option.
     */
    private String description = DEFAULT_DESCRIPTION;

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
     * The compiled pattern to match parameters (this is not implemented so
     * far).
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
     * @param key  the parameter key.
     */
    public Option(String key) {
        this.key = key;
        this.description = key;
    }

    /**
     * A constructor for setting key and description.
     *
     * @param key          the parameter key.
     * @param description  the option description.
     */
    public Option(String key, String description) {

        this.key = key;
        this.description = description;
    }

    /**
     * A constructor for setting key, name and parameter check.
     *
     * @param key             the parameter key-
     * @param description     the option description.
     * @param parameterCheck  defines the number of expected parameters.
     */
    public Option(String key, String description, int parameterCheck) {

        this.key = key;
        this.description = description;
        this.setParameterNumberCheck(parameterCheck);
    }

    /**
     * A constructor for setting key, description and optional parameter.
     *
     * @param key          the parameter key.
     * @param description  the option description.
     * @param isOptional   flag for optional/non-optional attribute.
     */
    public Option(String key, String description, boolean isOptional) {

        this.key = key;
        this.description = description;
        this.isOptional = isOptional;
    }

    /**
     * A constructor for setting key, description, optional parameter and
     * paramter check.
     *
     * @param key             the parameter key.
     * @param description     the option description.
     * @param isOptional      flag for optional/non-optional attribute.
     * @param parameterCheck  one of the defined constants for the number of
     *                        allowed parameters.
     */
    public Option(String key, String description,
                  boolean isOptional, int parameterCheck) {

        this.key = key;
        this.description = description;
        this.isOptional = isOptional;
        this.setParameterNumberCheck(parameterCheck);
    }

    /**
     * Check if the given object is equal to this instance. That is only the
     * case if the object is an instance of <CODE>Option</CODE> and the key
     * is equal to the key of this instance.
     *
     * @param otherObject  An object which should be checked for equality.
     *
     * @return  <CODE>true</CODE> if and only if the given object is an
     *          instance of <CODE>Option</CODE> and its key is equal to the
     *          key of this instance.
     */
    public boolean equals(Object otherObject) {

        if (!(otherObject instanceof Option)) {
            return false;
        } else {
            return key.equals(((Option) otherObject).getKey());
        }
    }

    /**
     * Returns the hashCode of this object.
     *
     * @return  the hashCode of this objekt.
     */
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * The setter for the prefix attribute.
     *
     * @param prefix  the prefix being used.
     *
     * @return  this object.
     */
    public Option setPrefix(String prefix) {

        this.prefix = prefix;
        return this;
    }

    /**
     * The setter for the key attribute.
     *
     * @param key  the key being used.
     *
     * @return  this object
     */
    public Option setKey(String key) {

        this.key = key;
        return this;
    }

    /**
     * Set the description of all parameters. Ascending numbers are appended
     * to the description if more than on parameter is allowed for this
     * option.
     *
     * @param description  description for the option. This String is used
     *                     for the method toString.
     *
     * @return  this object
     */
    public Option setDescription(String description) {

        this.description = description;
        return this;
    }

    /**
     * The setter for the optional attribute.
     *
     * @param isOptional  <code>true</code> if and only if this options is
     *                    optional.
     *
     * @return  this object.
     */
    public Option setOptional(boolean isOptional) {

        this.isOptional = isOptional;
        return this;
    }

    /**
     * Used to set the allowed number of parameters for this option.
     *
     * @param parameterCheck  defining the allowed number of paramters for
     *                        this option.
     * @return  this object.
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
     * @param regex  a regular expression defining the syntax of the
     *               parameters.
     *
     * @return  this object.
     */
    public Option setParameterPatternCheck(String regex) {

        parameterPattern = Pattern.compile(regex);
        return this;
    }

    /**
     * The getter for the key attribute.
     *
     * @return the key for this option
     */
    String getKey() {
        return key;
    }

    /**
     * The getter for the key attribute.
     *
     * @return the key for this option
     */
    String getDescription() {
        return description;
    }

    /**
     * Check if this option is optional.
     *
     * @return  <code>true</code> if and only if this is an optional option.
     */
    boolean isOptional() {
        return isOptional;
    }

    ////////////////////////////////////////////////////////////////////////
    // the following methods should only be accessed by the parameter parser

    /**
     * Method to check if a String matches prefix + key of this option.
     *
     * @param argument  a String found in the argv list.
     *
     * @return  <code>true</code> if and only if the String matches the
     *          concatenation of the prefix and the key.
     */
    boolean isMatch(String argument) {

        String tag = prefix + key;
        return argument.equals(tag);
    }

    /**
     * The setter for the <code>isEnabled</code> attribute.
     *
     * @param isEnabled  <code>true</code> if and only if this option is
     *                   found in a commandline or in a properties file.
     */
    void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Check if this option is enabled.
     *
     * @return  <code>true</code> if and only if this option is enabled. This
     *          means it is found in a parsed commandline or in a properties
     *          file.
     */
    boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Check if this opject can take one more parameter.
     *
     * @return  <code>true</code> if and only if this option can take one more
     *          parameter for it's parameter list.
     */
    boolean canTakeParameter() {
        return (parameters.size() < maxParameters);
    }

    /**
     * Check if a parameter matches the parameter pattern of this option.
     *
     * @param parameter the parameter for this option.
     *
     * @return  <code>true</code> if and only if the parameter matches the
     *          parameter pattern.
     */
    boolean canMatchParameter(String parameter) {
        return parameterPattern.matcher(parameter).matches();
    }

    /**
     * Check if this Option is valid.
     *
     * @throws ParameterParserException  if this option is not valid.
     *
     */
    void checkValid() throws ParameterParserException {

        if (!isOptional() && !isEnabled()) {
            // option is not enabled and not optional
            throw new ParameterParserException("Missing non optional Option: "
                                               + this);
        }

        if (isEnabled()) {
            if (parameters.size() < minParameters) {
                // too few parameters.
                throw new ParameterParserException("Missing parameters for"
                                                   + " Option: " + this);
            }

            if (parameters.size() > maxParameters) {
                // too many parameters.
                throw new ParameterParserException("Too many parameters for"
                                                   + " Option: " + this);
            }
        }
    }

    /**
     * This method should only be called by the {@link ParameterParser} and
     * is used to add a parameter to the parameter list of this option.
     *
     * @param parameter  a string to be added to the parameter list.
     *
     * @throws ParameterParserException  if there are any problems with the
     *                                   added parameter.
     */
    void addParameter(String parameter) throws ParameterParserException {

        // check if we can take a parameter
        if (!canTakeParameter()) {
            throw new ParameterParserException("Option can't take parameter");
        }

        // check if the parameter matches the pattern
        if (!canMatchParameter(parameter)) {
            throw new ParameterParserException("Parameter doesn't match the"
                                               + " defined pattern");
        }

        parameters.add(parameter);
    }

    /**
     * This method resets any status data of this option. Used by the
     * {@link ParameterParser} if a new command line is about to be parsed.
     */
    void reset() {

        parameters.clear();
        isEnabled = false;
    }

    /**
     * Accessor for the parameter vector.
     *
     * @return  a <code>ArrayList</code> of parameters.
     */
    ArrayList getParameterList() {
        return parameters;
    }

    /**
     * Accessor for the first parameter of the parameter list.
     *
     * @return  the first parameter.
     */
    String getParameter() {  // package-private

        assert (parameters.size() > 0);
        return (String) parameters.get(0);
    }

    /**
     * Method used to return the syntax of this option. This method is used by
     * the {@link ParameterParser} to compose a syntax String for a complete
     * comand line.
     *
     * @return  a String for debugging.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(prefix);
        result.append(key);

        if (isOptional()) {
            result.insert(0, "[");
        }

        // append the parameter description

        // all non-optional (minimal) stuff:
        for (int i = 1; i <= minParameters; i++) {
            result.append(" <");
            result.append(description);
            if (maxParameters > 1) {
                result.append("." + i);
            }
            result.append(">");
        }

        // the optional (maximal) parameters
        if (MAX_OPT_SHOWN_PARAMETERS < (maxParameters - minParameters)) {
            // we don't show all, just some dots
            result.append(" [");
            result.append(" <");
            result.append(description);
            result.append(minParameters + 1);
            result.append("> ... ");
            result.append("]");
        } else {
            // show all
            if (maxParameters > minParameters) {
                result.append(" [");
                for (int i = minParameters + 1; i <= maxParameters; i++) {
                    result.append(" <");
                    result.append(description);
                    if ((maxParameters - minParameters) > 1) {
                        result.append("." + i);
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

