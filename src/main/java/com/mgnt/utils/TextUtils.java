package com.mgnt.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mgnt.utils.entities.TimeInterval;
import com.mgnt.utils.textutils.InvalidVersionFormatException;
import com.mgnt.utils.textutils.Version;

/**
 * This class provides various utilities for work with String that represents some other type. 
 * <p>In current version this class provides methods for
 * converting a String into its numeric value of various types (Integer, Float, Byte, Double, Long, Short).
 * </p> 
 * <p>
 * There are several methods for retrieving
 * Exception stacktrace as a String in full or shortened version. Shortened version of the stacktrace will contain concise information focusing on
 * specific set of packages or subpackages while removing long parts of irrelevant stacktrace. This could be very useful for logging in web-based architecture
 * where stacktrace may contain long parts of server provided classes trace that could be eliminated with the methods of this class while retaining
 * important parts of the stacktrace relating to user's packages. Also the same utility (starting from version 1.5.0.3) allows to filter and shorten 
 * stacktrace as a string the same way as the stacktrace extracted from exception. So, essentially stacktraces could be filtered "on the fly" at run time 
 * or later on from any text source such as log.</p>
 * <p>Also this class provides methods that work with textual representation of versions.
 * Valid version is a String of the following format:<br>
 * <br>
 * <p>
 * X[.X[.X[...]]]
 * </p>
 * <br>
 * <br>
 * <p>
 * where X is a zero or positive integer not larger than 2147483647. Leading or trailing white spaces in this string are permitted and are ignored.
 * Examples of valid versions are: "1.6", "58", "  7.34.17  " etc. (Note that last example contains both leading and trailing white spaces and it is
 * still a valid version)</p>
 * <p>
 * Another useful feature is parsing String to time interval. It parses Strings with numerical value and optional time unit
 * suffix (for example  string "38s" will be parsed as 38 seconds, "24m" - 24 minutes "4h" - 4 hours, "3d" - 3 days and
 * "45" as 45 milliseconds.) This method may be very useful for parsing time interval properties such as timeouts or waiting
 * periods from configuration files.
 * </p>
 * <p>
 *  Also in this class there is a method that converts String to preserve indentation formatting for html without use of escape
 *  characters. It converts a String in such a way that its spaces are not modified by HTML renderer i.e. it replaces
 *  regular space characters with non-breaking spaces known as '&amp;nbsp;' but they look in your source as regular space
 *  '  ' and not as '&amp;nbsp;' It also replaces new line character with '&lt;br&gt;'.
 * </p>
 * <p>
 *     Note that this class has a loose dependency on slf4J library. If in the project some other compatible logging library is present
 *     (such as Log4J) this class will still work without any ill effects
 * </p>
 *
 * @author Michael Gantman
 */
public class TextUtils {

    private static final Logger logger = LoggerFactory.getLogger(TextUtils.class);

    protected static final TimeUnit DEFAULT_TIMEOUT_TIME_UNIT = TimeUnit.MILLISECONDS;
    protected static final String SECONDS_SUFFIX = "s";
    protected static final String MINUTES_SUFFIX = "m";
    protected static final String HOURS_SUFFIX = "h";
    protected static final String DAYS_SUFFIX = "d";
    private static final long INITIAL_PARSING_VALUE = -1L;
    /*
     * Strings defined bellow are for the use of methods getStacktrace() of this class
     */
    private static String[] RELEVANT_PACKAGE_LIST = null;
    private static final String STANDARD_STAKTRACE_PREFIX = "at ";
    private static final String SKIPPING_LINES_STRING = "\t...";
    private static final String CAUSE_STAKTRACE_PREFIX = "Caused by:";
    private static final String SUPPRESED_STAKTRACE_PREFIX = "Suppressed:";
    private static final String RELEVANT_PACKAGE_SYSTEM_EVIRONMENT_VARIABLE = "MGNT_RELEVANT_PACKAGE";
    private static final String RELEVANT_PACKAGE_SYSTEM_PROPERTY = "mgnt.relevant.package";
    private static final String HTML_NON_BREAKING_SPACE_CHARACTER = StringUnicodeEncoderDecoder.decodeUnicodeSequenceToString("\\u00A0");
    private static final String HTML_NEW_LINE = "<br>";
    private static final String RELEVANT_PACKAGE_DELIMITER = "\\s*;\\s*";

    static {
        initRelevantPackageFromSystemProperty();
    }

    private static void initRelevantPackageFromSystemProperty() {
        String relevantPackage = System.getProperty(RELEVANT_PACKAGE_SYSTEM_PROPERTY);
        if(StringUtils.isBlank(relevantPackage)) {
            relevantPackage = System.getenv(RELEVANT_PACKAGE_SYSTEM_EVIRONMENT_VARIABLE);
        }
        if(StringUtils.isNotBlank(relevantPackage)) {
            setRelevantPackage(relevantPackage.trim().split(RELEVANT_PACKAGE_DELIMITER));
        }
    }

    /**
     * This method compares 2 Strings as versions
     *
     * @param ver1 String that contains version for comparison
     * @param ver2 String that contains version for comparison
     * @return negative integer (-1) if the first version is lesser then the second, 0 if the versions are equal and positive integer (1) if the first
     *         version is greater than second
     * @throws com.mgnt.utils.textutils.InvalidVersionFormatException
     *          if any of the String instances is not a valid Version
     */
    public static int compareVersions(String ver1, String ver2) throws InvalidVersionFormatException {
        return new Version(ver1).compareTo(new Version(ver2));
    }

    /**
     * This method compares a Version to a String that represents a version
     *
     * @param ver1 Version for comparison
     * @param ver2 String that contains version for comparison
     * @return negative integer (-1) if the first version is lesser then the second, 0 if the versions are equal and positive integer (1) if the first
     *         version is greater than second
     * @throws com.mgnt.utils.textutils.InvalidVersionFormatException
     *          if the String parameter is not a valid Version
     */
    public static int compareVersions(Version ver1, String ver2) throws InvalidVersionFormatException {
        return ver1.compareTo(new Version(ver2));
    }

    /**
     * This method compares a String that represents a version to a Version
     *
     * @param ver1 String that contains version for comparison
     * @param ver2 Version for comparison
     * @return negative integer (-1) if the first version is lesser then the second, 0 if the versions are equal and positive integer (1) if the first
     *         version is greater than second
     * @throws com.mgnt.utils.textutils.InvalidVersionFormatException
     *          if the String parameter is not a valid Version
     */
    public static int compareVersions(String ver1, Version ver2) throws InvalidVersionFormatException {
        return new Version(ver1).compareTo(ver2);
    }

    /**
     * This method compares two Version instances
     *
     * @param ver1 Version for comparison
     * @param ver2 Version for comparison
     * @return negative integer (-1) if the first version is lesser then the second, 0 if the versions are equal and positive integer (1) if the first
     *         version is greater than second
     */
    public static int compareVersions(Version ver1, Version ver2) {
        return ver1.compareTo(ver2);
    }

    /**
     * This method parses a String to its Numeric value. If parsing does not succeed because the String is not of appropriate number format the
     * default numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is
     * returned and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by
     * project that uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty
     * Strings than the correlating error will not be printed.
     *
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return numeric value parsed from the String
     */
    public static int parseStringToInt(CharSequence num, int defaultValue,
                                       String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
        Integer result = null;
        if (num == null || "".equals(num.toString())) {
            if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
                logger.warn(nullOrEmptyStringErrorMessage);
            }
            result = defaultValue;
        } else {
            try {
                result = Integer.parseInt(num.toString());
            } catch (NumberFormatException nfe) {
                if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
                    warn(numberFormatErrorMessage, nfe);
                }
                result = defaultValue;
            }
        }
        return result;
    }

    /**
     * This method parses a String to its Numeric value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToInt(CharSequence, int, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static int parseStringToInt(CharSequence num, int defaultValue) {
    	return parseStringToInt(num, defaultValue, null, null);
    }
    
    /**
     * This method parses a String to Numeric value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return numeric value parsed from the String
     */
    public static float parseStringToFloat(CharSequence num, float defaultValue,
                                           String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
        Float result = null;
        if (num == null || "".equals(num.toString())) {
            if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
                logger.warn(nullOrEmptyStringErrorMessage);
            }
            result = defaultValue;
        } else {
            try {
                result = Float.parseFloat(num.toString());
            } catch (NumberFormatException nfe) {
                if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
                    warn(numberFormatErrorMessage, nfe);
                }
                result = defaultValue;
            }
        }
        return result;
    }

    /**
     * This method parses a String to its Numeric value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToFloat(CharSequence, float, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static float parseStringToFloat(CharSequence num, float defaultValue) {
    	return parseStringToFloat(num, defaultValue, null, null);
    }
    
    /**
     * This method parses a String to Numeric value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return numeric value parsed from the String
     */
    public static Byte parseStringToByte(CharSequence num, byte defaultValue,
                                         String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
        Byte result = null;
        if (num == null || "".equals(num.toString())) {
            if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
                logger.warn(nullOrEmptyStringErrorMessage);
            }
            result = defaultValue;
        } else {
            try {
                result = Byte.parseByte(num.toString());
            } catch (NumberFormatException nfe) {
                if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
                    warn(numberFormatErrorMessage, nfe);
                }
                result = defaultValue;
            }
        }
        return result;
    }

    /**
     * This method parses a String to its Numeric value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToByte(CharSequence, byte, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static Byte parseStringToByte(CharSequence num, byte defaultValue) {
    	return parseStringToByte(num, defaultValue, null, null);
    }
    
    /**
     * This method parses a String to Numeric value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return numeric value parsed from the String
     */
    public static double parseStringToDouble(CharSequence num, double defaultValue,
                                             String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
        Double result = null;
        if (num == null || "".equals(num.toString())) {
            if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
                logger.warn(nullOrEmptyStringErrorMessage);
            }
            result = defaultValue;
        } else {
            try {
                result = Double.parseDouble(num.toString());
            } catch (NumberFormatException nfe) {
                if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
                    warn(numberFormatErrorMessage, nfe);
                }
                result = defaultValue;
            }
        }
        return result;
    }

    /**
     * This method parses a String to its Numeric value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToDouble(CharSequence, double, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static double parseStringToDouble(CharSequence num, double defaultValue) {
    	return parseStringToDouble(num, defaultValue, null, null);
    }
    
    /**
     * This method parses a String to Numeric value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return numeric value parsed from the String
     */
    public static long parseStringToLong(CharSequence num, long defaultValue,
                                         String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
        Long result = null;
        if (num == null || "".equals(num.toString())) {
            if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
                logger.warn(nullOrEmptyStringErrorMessage);
            }
            result = defaultValue;
        } else {
            try {
                result = Long.parseLong(num.toString());
            } catch (NumberFormatException nfe) {
                if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
                    warn(numberFormatErrorMessage, nfe);
                }
                result = defaultValue;
            }
        }
        return result;
    }

    /**
     * This method parses a String to its Numeric value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToLong(CharSequence, long, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static long parseStringToLong(CharSequence num, long defaultValue) {
    	return parseStringToLong(num, defaultValue, null, null);
    }
    
    /**
     * This method parses a String to Numeric value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return numeric value parsed from the String
     */
    public static Short parseStringToShort(CharSequence num, short defaultValue,
                                           String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
        Short result = null;
        if (num == null || "".equals(num.toString())) {
            if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
                logger.warn(nullOrEmptyStringErrorMessage);
            }
            result = defaultValue;
        } else {
            try {
                result = Short.parseShort(num.toString());
            } catch (NumberFormatException nfe) {
                if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
                    warn(numberFormatErrorMessage, nfe);
                }
                result = defaultValue;
            }
        }
        return result;
    }

    /**
     * This method parses a String to its Numeric value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToShort(CharSequence, short, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static Short parseStringToShort(CharSequence num, short defaultValue) {
    	return parseStringToShort(num, defaultValue, null, null);
    }
    
    /**
     * This method parses String value into {@link TimeInterval}. This method supports time interval suffixes <b>"s"</b>
     * for seconds, <b>"m"</b> for minutes, <b>"h"</b> for hours, and <b>"d"</b> for days. Suffix is case insensitive.
     * If String parameter contains no suffix the default is milliseconds. So for example string "38s" will be parsed
     * as 38 seconds, "24m" - 24 minutes "4h" - 4 hours, "3d" - 3 days and "45" as 45 milliseconds. If the string parses
     * to a negative numerical value or 0 or the string is not a valid numerical value then {@link IllegalArgumentException}
     * is thrown. Note that it is very convenient to extract time value from {@link TimeInterval}, See methods
     * {@link TimeInterval#toMillis()}, {@link TimeInterval#toSeconds()}, {@link TimeInterval#toMinutes()},
     * {@link TimeInterval#toHours()}, {@link TimeInterval#toDays()}.
     * <br><br>
     * This method may be very useful for parsing time interval properties such as timeouts or waiting periods from
     * configuration files. It eliminates unneeded calculations from different time scales to milliseconds back and forth.
     * Consider that you have a {@code methodInvokingInterval} property that you need to set for 5 days. So in order to
     * set the miliseconds value you will need to calculate that 5 days is 432000000 milliseconds (obviously not an
     * impossible task but annoying and error prone) and then anyone else who sees the value 432000000 will have to
     * calculate it back to 5 days which is frustrating. But using this method you will have a property value set to
     * "5d" and invoking the code
     * <br><br>
     *     {@code long milliseconds = TextUtils.parseStringToTimeInterval("5d").toMillis();}
     * <br><br>
     * will solve your conversion problem
     *
     * @param valueStr String value to parse to {@link TimeInterval}
     * @return {@link TimeInterval} parsed from the String
     * @throws IllegalArgumentException if parsed value has invalid suffix, invalid numeric value or negative numeric value or 0
     */
    public static TimeInterval parseStringToTimeInterval(String valueStr) throws IllegalArgumentException {
        if(StringUtils.isBlank(valueStr)) {
            throw new IllegalArgumentException("Attempt to parse null or blank String");
        }
        TimeInterval result = new TimeInterval();
        String potentialSuffix = valueStr.substring(valueStr.length() - 1);
        boolean isLetter = Character.isLetter(potentialSuffix.codePointAt(0));
        String valueToParse = (isLetter) ? valueStr.substring(0, valueStr.length() - 1) : valueStr;
        result.setValue(INITIAL_PARSING_VALUE);
        result = setTimeUnit(isLetter, potentialSuffix, result);
        result = setTimeValue(valueToParse, result);
        return result;
    }

    /**
     * @deprecated Use {@link #parseStringToTimeInterval(String)} instead. The new method the same as this one except the name change. 
     * This method is left for backwards compatibility only and will be removed in future versions.
     * @param valueStr String value to parse to {@link TimeInterval}
     * @return {@link TimeInterval} parsed from the String
     * @throws IllegalArgumentException if parsed value has invalid suffix, invalid numeric value or negative numeric value or 0
     */
    public static TimeInterval parsingStringToTimeInterval(String valueStr) throws IllegalArgumentException {
    	return parseStringToTimeInterval(valueStr);
    }
    
    /**
     * This method parses a String to BigDecimal value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return BigDecimal value parsed from the String
     */
    public static BigDecimal parseStringToBigDecimal(CharSequence num, BigDecimal defaultValue,
            String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
    		BigDecimal result = null;
    		if (num == null || "".equals(num.toString())) {
    			if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
    			logger.warn(nullOrEmptyStringErrorMessage);
    			}
    		result = defaultValue;
    		} else {
    			try {
    			result = new BigDecimal(num.toString());
    			} catch (NumberFormatException nfe) {
    				if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
    					warn(numberFormatErrorMessage, nfe);
    				}
    			result = defaultValue;
    			}
    		}
    		return result;
    }


    /**
     * This method parses a String to its BigDecimal value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToBigDecimal(CharSequence, BigDecimal, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static BigDecimal parseStringToBigDecimal(CharSequence num, BigDecimal defaultValue) {
    	return parseStringToBigDecimal(num, defaultValue, null, null);
    }
    
    /**
     * This method parses a String to BigInteger value. If parsing does not succeed because the String is not of appropriate number format the default
     * numeric value is returned and appropriate error message is printed into log. Also if the String is blank or null the default value is returned
     * and appropriate error message is printed into log. Note that logging relies on slf4j being present and appropriately configured by project that
     * uses this utility. If {@code nullOrEmptyStringErrorMessage} or {@code numberFormatErrorMessage} parameters are null or empty Strings than the
     * correlating error will not be printed.
     *
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
     * @return BigInteger value parsed from the String
     */
    public static BigInteger parseStringToBigInteger(CharSequence num, BigInteger defaultValue,
            String nullOrEmptyStringErrorMessage, String numberFormatErrorMessage) {
    		BigInteger result = null;
    		if (num == null || "".equals(num.toString())) {
    			if (nullOrEmptyStringErrorMessage != null && !"".equals(nullOrEmptyStringErrorMessage)) {
    			logger.warn(nullOrEmptyStringErrorMessage);
    			}
    		result = defaultValue;
    		} else {
    			try {
    			result = new BigInteger(num.toString()); 
    			} catch (NumberFormatException nfe) {
    				if (numberFormatErrorMessage != null && !"".equals(numberFormatErrorMessage)) {
    					warn(numberFormatErrorMessage, nfe);
    				}
    			result = defaultValue;
    			}
    		}
    		return result;
    }
    
    /**
     * This method parses a String to its BigInteger value silently. If parsing does not succeed because the String is not of appropriate number 
     * format or if the String is blank or null the default numeric value is returned but nothing is printed into the log. This method
     * is equivalent to calling method {@link #parseStringToBigInteger(CharSequence, BigInteger, String, String)} with 2 last parameters set to null
     * @param num CharSequence to be parsed
     * @param defaultValue value that will be returned by this method if parsing of the String failed
     * @return numeric value parsed from the String
     */
    public static BigInteger parseStringToBigInteger(CharSequence num, BigInteger defaultValue) {
    	return parseStringToBigInteger(num, defaultValue, null, null);
    }
    
    /**
     * <p>
     * This method retrieves a stacktrace from {@link Throwable} as a String in full or shortened format. Shortened format skips the lines in the
     * stacktrace that do not start with one of the package prefixes in configurable package prefix list and replaces them with "..." line. The stacktrace is viewed as consisting
     * possibly of several parts. If stacktrace contains {@code "caused by"} or {@code "Suppressed"} section, each such section for the purposes of
     * this utility is called "Singular stacktrace". For example the stacktrace bellow contains 2 singular stacktraces: First is 4 top lines and the
     * second starting from the line {@code "Caused by: ..."} and to the end.<br>
     * <br>
     * </p>
     * java.lang.Exception: Bad error<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:80)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.run(UtilsTester.java:30)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.main(UtilsTester.java:25)<br>
     * Caused by: java.lang.NumberFormatException: For input string: "Hello"<br>
     * &emsp; at java.lang.NumberFormatException.forInputString(Unknown Source)<br>
     * &emsp; at java.lang.Integer.parseInt(Unknown Source)<br>
     * &emsp; at java.lang.Integer.parseInt(Unknown Source)<br>
     * &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.parseInt(UtilsTesterHelper.java:8)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester$Invoker.runParser(UtilsTester.java:97)<br>
     * &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.innerInvokeParser(UtilsTesterHelper.java:17)<br>
     * &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.invokeParser(UtilsTesterHelper.java:12)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:76)<br>
     * &emsp; ... 2 more<br>
     * <br>
     * <p>
     * The way this method shortens the stacktrace is as follows. Each "singular" stacktrace is analyzed and shortened separately. For each singular
     * stacktrace the error message is always printed. Then all the lines that follow are printed even if they do not start with one of the prefixes specified by
     * <b>relevantPackages</b>. Once the first line that starts with one of the prefixes is found this line and all immediately following lines that start with one of the relevant
     * package prefixes are printed as well. The first line that does not start with any of the prefixes after a section of the lines that did is also printed.
     * But all the following lines that do not start with any of the prefixes are skipped and replaced with a single line "...". If at some point within the
     * stacktrace a line that starts with one of the prefixes is encountered again this line and all the following lines that start with one of the prefixes + one
     * following line that does not start with any of the prefixes are printed in. And so on. Here is an example: Assume that exception above was passed as a
     * parameter to this method and parameter <b>relevantPackages</b> is set to {@code ["com.plain.analytics.v2.utils.test."]} which means that the lines starting with
     * that prefix are the important or "relevant" lines. (Also the parameter <b>cutTBS</b> set to true which means that stacktrace should be
     * shortened. In this case the result of this method should be as follows:<br>
     * <br>
     * </p>
     * java.lang.Exception: Bad error<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:80)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.run(UtilsTester.java:30)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.main(UtilsTester.java:25)<br>
     * Caused by: java.lang.NumberFormatException: For input string: "Hello"<br>
     * &emsp; at java.lang.NumberFormatException.forInputString(Unknown Source)<br>
     * &emsp; at java.lang.Integer.parseInt(Unknown Source)<br>
     * &emsp; at java.lang.Integer.parseInt(Unknown Source)<br>
     * &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.parseInt(UtilsTesterHelper.java:8)<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester$Invoker.runParser(UtilsTester.java:97)<br>
     * &emsp; at com.plain.analytics.v2.utils.test2.UtilsTesterHelper.innerInvokeParser(UtilsTesterHelper.java:17)<br>
     * &emsp; ...<br>
     * &emsp; at com.plain.analytics.v2.utils.test.UtilsTester.TestGetStackTrace(UtilsTester.java:76)<br>
     * &emsp; ... 2 more<br>
     * <br>
     * <p>
     * Note that the first singular stacktrace is printed in full because all the lines start with the required prefix. The second singular stacktrace
     * prints the first 7 lines because at first all the lines are printed until the first line with one of the relevant prefixes is found, and then all the lines
     * with the prefix (one in our case) are printed + plus one following line without the prefix. And then the second line without the prefix (3d
     * from the bottom) is skipped and replaced with line "...". But then again we encounter a line with the prefix which is printed and finally the
     * last line is printed because it is the first line without prefix following the one with the prefix. In this particular example only one line
     * was skipped over, which is not very much, but for web-based environments for the long stacktraces that contain long traces of server related
     * classes this method could be very effective in removing irrelevant lines and leaving only application related lines making log files more
     * concise and clear.<br>
     * <br>
     * </p>
     * <b>Important Note:</b> Parameter <b>relevantPackages</b> may be left null. In this case the value of relevant package prefix will be taken from
     * <b>RelevantPackages</b> property (See the methods {@link #setRelevantPackage(String...)} and {@link #getRelevantPackage()}). Using method
     * {@link #setRelevantPackage(String...)} to set the value will preset the values of relevant package prefixes for all calls for which parameter
     * <b>relevantPackages</b> is null. In fact there is a convenience method {@link #getStacktrace(Throwable, boolean)} that invokes this method with
     * parameter <b>relevantPackages</b> set to null and relies on the globally set property through method {@link #setRelevantPackage(String...)}.
     * However if the global property was not set and parameter <b>relevantPackages</b> was left null then the method will return stacktrace in full as
     * if the parameter <b>cutTBS</b> was set to false<br>
     *
     * @param e               {@link Throwable} from which stacktrace should be retrieved
     * @param cutTBS          boolean that specifies if stacktrace should be shortened. The stacktrace should be shortened if this flag is set to {@code true}.
     *                        Note that if this parameter set to {@code false} the stacktrace will be printed in full and parameter <b>relevantPackages</b> becomes
     *                        irrelevant.
     * @param relevantPackages {@link String...} that contains the prefix or several prefixes specifying which lines are relevant. It is recommended to be in the following format
     *                        "packag_name1.[package_name2.[...]]." In the example above it should be "com.plain.analytics.v2.utils.test.".
     * @return String with stacktrace value
     */
    public static String getStacktrace(Throwable e, boolean cutTBS, String... relevantPackages) {

        // retrieve full stacktrace as byte array
        ByteArrayOutputStream stacktraceContent = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stacktraceContent));

        return extractStackTrace(cutTBS, stacktraceContent, relevantPackages);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in full or shortened format. This is convenience method that invokes
     * method {@link #getStacktrace(Throwable, boolean, String...)} with last parameter as {@code null}. It relies on relevant package prefix to have
     * been set by method {@link #setRelevantPackage(String...)}. There are several ways to pre-invoke method {@link #setRelevantPackage(String...)}:<br>
     *     <ul>
     *     <li>Set system environment variable <b>"MGNT_RELEVANT_PACKAGE"</b> with relevant package value (for the purposes of our example
     *     it would be "com.plain.")</li>
     *     <li>Run your code with System property <b>"mgnt.relevant.package"</b> set to relevant package value It could be done with
     *     -D: <b>"-Dmgnt.relevant.package=com.plain."</b> Note that System property value would take precedence over environment variable
     *     if both are set. <br><b>IMOPRTANT:</b> Note that for both environment variable and system property if multiple prefixes needed to be set
     *     than list them one after another separated by <b>semicolon (;)</b><br><b>For Example:</b> {@code "com.plain.;com.encrypted."}</li>
     *     <li>In case when Spring framework is used and system property and environment variable described above are not used then it is
     * recommended to add the following bean into your Spring configuration xml file. This will ensure an invocation of method
     * {@link #setRelevantPackage(String...)} which will appropriately initialize the package prefix and enable the use of this method
     *
     * <p>
     *     &lt;bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean"&gt;<br>
     *     &nbsp;&lt;property name="targetClass" value="com.mgnt.utils.TextUtils"&#47;&gt;<br>
     *     &nbsp;&lt;property name="targetMethod" value="setRelevantPackage"&#47;&gt;<br>
     *     &nbsp;&lt;property name="arguments" value="com.plain."&#47;&gt;<br>
     *     &lt;&#47;bean&gt;
     * </p></li>
     * </ul>
     *
     * @param e      {@link Throwable} from which stacktrace should be retrieved
     * @param cutTBS boolean flag that specifies if stacktrace should be shortened or not. It is shortened if the flag value is {@code true}
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String...)
     */
    public static String getStacktrace(Throwable e, boolean cutTBS) {
        return getStacktrace(e, cutTBS, null);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in shortened format. This is convenience method that invokes method
     * {@link #getStacktrace(Throwable, boolean, String...)} with second parameter set to {@code 'true'} and last parameter as {@code null}. It relies on
     * relevant package prefix to have been set by method {@link #setRelevantPackage(String...)}. There are several ways to pre-invoke method {@link #setRelevantPackage(String...)}:<br>
     *     <ul>
     *     <li>Set system environment variable <b>"MGNT_RELEVANT_PACKAGE"</b> with relevant package value (for the purposes of our example
     *     it would be "com.plain.")</li>
     *     <li>Run your code with System property <b>"mgnt.relevant.package"</b> set to relevant package value It could be done with
     *     -D: <b>"-Dmgnt.relevant.package=com.plain."</b> Note that System property value would take precedence over environment variable
     *     if both are set. <br><b>IMOPRTANT:</b> Note that for both environment variable and system property if multiple prefixes needed to be set
     *      *     than list them one after another separated by <b>semicolon (;)</b><br><b>For Example:</b> {@code "com.plain.;com.encrypted."}</li></li>
     *     <li>In case when Spring framework is used and system property and environment variable described above are not used then it is
     * recommended to add the following bean into your Spring configuration xml file. This will ensure an invocation of method
     * {@link #setRelevantPackage(String...)} which will appropriately initialize the package prefix and enable the use of this method
     *
     * <p>
     *     &lt;bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean"&gt;<br>
     *     &nbsp;&lt;property name="targetClass" value="com.mgnt.utils.TextUtils"&#47;&gt;<br>
     *     &nbsp;&lt;property name="targetMethod" value="setRelevantPackage"&#47;&gt;<br>
     *     &nbsp;&lt;property name="arguments" value="com.plain."&#47;&gt;<br>
     *     &lt;&#47;bean&gt;
     * </p></li>
     * </ul>
     * @param e {@link Throwable} from which stacktrace should be retrieved
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String...)
     */
    public static String getStacktrace(Throwable e) {
        return getStacktrace(e, true, null);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in shortened format. This is convenience method that invokes method
     * {@link #getStacktrace(Throwable, boolean, String...)} with second parameter set to {@code 'true'}.
     *
     * @param e               {@link Throwable} from which stacktrace should be retrieved
     * @param relevantPackages {@link String...} that contains the prefix or several prefixes specifying which lines are relevant.
     * It is recommended that each prefix should be in the following format
     *                        "packag_name1.[package_name2.[...]]."
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String...)
     */
    public static String getStacktrace(Throwable e, String... relevantPackages) {
        return getStacktrace(e, true, relevantPackages);
    }

    /**
     * This method retrieves a stacktrace  in shortened format from source stactrace provided as  {@link CharSequence}. 
     * Since this method receives stacktrace as a {@link CharSequence}, it is assumed that it is always desirable to get 
     * shortened format since the full stacktrace is already available as a {@link CharSequence}. To shorten the original 
     * stacktrace this method processes the stacktrace exactly as described in method 
     * {@link #getStacktrace(Throwable, boolean, String...)}.
     * @param stacktrace {@link CharSequence} that holds full stacktrace text
     * @param relevantPackages {@link String...} that contains the prefix or several prefixes specifying which lines are relevant. It is
     * recommended that each prefix should be in the following format "packag_name1.[package_name2.[...]]." (Again for full explanation
     * on how this parameter (and the entire method) works see the method {@link #getStacktrace(Throwable, boolean, String...)}.
     * @return Stacktrace string in shortened format 
     * @see #getStacktrace(Throwable, boolean, String...)
     * @since 1.5.0.3
     */
    public static String getStacktrace(CharSequence stacktrace, String... relevantPackages) {
    	return extractStackTrace(true, convertToByteArray(stacktrace), relevantPackages);
    }

    /**
     * This method retrieves a stacktrace  in shortened format from source stacktrace provided as  {@link CharSequence}.
     * This is convenience method that works the same way as method 
     * {@link #getStacktrace(CharSequence, String...)} with second parameter set to {@code null}. It relies on relevant
     * package prefix to have been set by method {@link #setRelevantPackage(String...)}. There are several options to
     * pre-set this value. For detailed explanation of these options see method {@link #getStacktrace(Throwable, boolean)}. 
     * Since this method receives stacktrace as a {@link CharSequence}, it is assumed that it is always desirable to get 
     * shortened format since the full stacktrace is already available as a {@link CharSequence}
     * 
     * @param stacktrace {@link CharSequence} that holds full stacktrace text
     * @return Stacktrace string in shortened format 
     * @see #getStacktrace(Throwable, boolean)
     * @since 1.5.0.3
     */
    public static String getStacktrace(CharSequence stacktrace) {
    	return extractStackTrace(true, convertToByteArray(stacktrace), null);
    }

    /**
     * This method simply takes a {@link CharSequence} parameter and converts it to {@link ByteArrayOutputStream}.
     * If the parameter is null then blank {@link ByteArrayOutputStream} is returned
     * @param stacktrace {@link CharSequence} that supposed to contain the stacktrace text
     * @return {@link ByteArrayOutputStream} that contains the contents of <b>stacktrace</b> parameter converted
     * to bytes or blank {@link ByteArrayOutputStream} if <b>stacktrace</b> parameter is {@code null} or blank
     */
	private static ByteArrayOutputStream convertToByteArray(CharSequence stacktrace) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(stacktrace != null) {
			byte[] content = stacktrace.toString().getBytes();
			baos.write(content, 0, content.length);
		}
		return baos;
	}

	/**
	 * This method receives the stacktrace content as {@link ByteArrayOutputStream} and processes it exactly as
	 * described in method {@link #getStacktrace(Throwable, boolean, String...)}. Except that it receives the
	 * stacktrace content as byte array, so it is agnostic of the fact whether it came from actual exception in
	 * real time of from a log file or any other source. This method allows working with stacktraces extracted
	 * on the fly at runtime or taken from some static sources (such as log files)
     * @param cutTBS          boolean that specifies if stacktrace should be shortened. The stacktrace should be shortened if this flag is set to {@code true}.
     *                        Note that if this parameter set to {@code false} the stacktrace will be printed in full and parameter <b>relevantPackage</b> becomes
     *                        irrelevant.
     * @param stacktraceContent {@link ByteArrayOutputStream} that contains the stacktrace content
     * @param relevantPackage {@link String...} that contains the prefix or several prefixes specifying which lines are relevant. It is recommended to be in the following format
     *                        "packag_name1.[package_name2.[...]]."
	 * @return
	 */
	private static String extractStackTrace(boolean cutTBS, ByteArrayOutputStream stacktraceContent, String... relevantPackage) {
		StringBuilder result = new StringBuilder("\n");

        // Determine the value of relevant package prefix
        String[] relPack = (relevantPackage != null && relevantPackage.length > 0) ? relevantPackage : RELEVANT_PACKAGE_LIST;
        /*
		 * If the relevant package prefix was not set neither locally nor globally revert to retrieving full stacktrace even if shortening was
		 * requested
		 */
        if (relPack == null || relPack.length == 0) {
            if (cutTBS) {
                cutTBS = false;
                logger.warn("Relevant package list was not set for the method. Stacktrace can not be shortened. Returning full stacktrace");
            }
        }
        if (cutTBS) {
            if (stacktraceContent.size() > 0) {
                try (BufferedReader reader =
                             new BufferedReader(new InputStreamReader(new ByteArrayInputStream(stacktraceContent.toByteArray())))) {
					/*
					 * First line from stacktrace is an actual error message and is always printed. If it happens to be null (which should never
					 * occur) it won't cause any problems as appending null to StringBuilder (within method traverseSingularStacktrace()) will be a
					 * no-op and subsequent readLine will still return null and will be detected in the method traverseSingularStacktrace()
					 */
                    String line = reader.readLine();
                    do {
						/*
						 * Process singular stacktraces until all are processed.
						 */
                        line = traverseSingularStacktrace(result, relPack, reader, line);
                    } while (line != null);
                } catch (IOException ioe) {
					/*
					 * In the very unlikely event of any error just fall back on printing the full stacktrace
					 */
                    error("Error occurred while reading and shortening stacktrace of an exception. Printing the original stacktrace", ioe);
                    result.delete(0, result.length()).append(new String(stacktraceContent.toByteArray()));
                }
            }

        } else {
			/*
			 * This is the branch that prints full stacktrace
			 */
            result.append(new String(stacktraceContent.toByteArray()));
        }
        return result.toString();
	}

	/**
     * This method traverses through Singular stacktrace and skips irrelevant lines from it replacing them by line "..." The resulting shortened
     * stacktrace is appended into {@link StringBuilder} The stacktrace is viewed as consisting possibly of several parts. If stacktrace contains
     * {@code "caused by"} or {@code "Suppressed"} section, each such section for the purposes of this utility is called "Singular stacktrace". For
     * more detailed explanation see method {@link #getStacktrace(Throwable, boolean, String...)}
     *
     * @param result  {@link StringBuilder} to which the resultant stacktrace will be appended
     * @param relPackArray {@link String[]} that contains relevant package prefix array
     * @param reader  {@link BufferedReader} that contains the source from where the stacktrace may be read line by line. Current position in the reader
     *                is assumed to be at the beginning of the second line of the current singular stacktrace, following the line with the name of the
     *                exception and error message
     * @param line    {@link String} that contains the first line of the current singular stacktrace i.e. the line with the name of the exception and
     *                error message
     * @return The first string of the next singular stacktrace or null if current singular stacktrace is the last one in the stacktrace
     * @throws IOException if any error occurs.
     * @see #getStacktrace(Throwable, boolean, String...)
     */
    private static String traverseSingularStacktrace(StringBuilder result, String[] relPackArray, BufferedReader reader, String line)
            throws IOException {
        result.append(line).append("\n");

        // Flag that holds the status for the current line if it should be printed or not
        boolean toBePrinted = true;

        // Flag that holds information on previous line whether or not it starts with relevant prefix package
        boolean relevantPackageReached = false;

        // Flag that specifies if the current line starts with relevant prefix or not
        boolean isCurLineRelevantPack = false;

        // Flag that specifies that skipping line should be printed as next line
        boolean skipLineToBePrinted = false;

        // Main cycle reading lines until reaching the end of stacktrace
        while ((line = reader.readLine()) != null) {
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith(STANDARD_STAKTRACE_PREFIX)) {
				/*
				 * This "if" branch deals with lines that are standard satacktrace lines atarting with "at "
				 */

                //Check if the current line starts with one of the prefixes (after the "at " part)
                String lineContent = trimmedLine.substring(STANDARD_STAKTRACE_PREFIX.length());
                isCurLineRelevantPack = Arrays.stream(relPackArray).anyMatch(relevantPackage -> lineContent.startsWith(relevantPackage));
                if (!relevantPackageReached && isCurLineRelevantPack) {
					/*
					 * If the current line starts with the prefix but previous line did not we change the printing status. This case deals with the
					 * first found line with the prefix and in this case it actually "changes" the flag "toBePrinted" from "true" to "true", but also
					 * it deals with the line with the prefix found after the first section of lines with the prefix was treated and was followed by
					 * some lines without prefix and then again the line with the prefix was found. That is why this "if" branch has to be before the
					 * actual printing. In this case flag "toBePrinted" is changed from "false" to "true". Also if previous line was the first line
					 * without prefix and we were supposed to print a skip line here we cancel that by setting flag "skipLineToBePrinted" back to
					 * false
					 */
                    relevantPackageReached = true;
                    toBePrinted = true;
                    skipLineToBePrinted = false;
                }

                // Add (or "print" the line into result if it is considered to be relevant
                if (toBePrinted) {
                    result.append(line).append("\n");
                } else if (skipLineToBePrinted) {
                    result.append(SKIPPING_LINES_STRING).append("\n");
                    skipLineToBePrinted = false;
                }

				/*
				 * Check if the previous line was with the prefix but current one is not. If this is the case, change the value of the "toBePrinted"
				 * flag to false switch the value of the flag skipLineToBePrinted to true to indicate that next line that should be printed into the
				 * result is the skip line ("...")to indicate that some lines are skipped. Note that the current line already was added to the result
				 * which is by design as one "irrelevant" line is printed after a section of "relevant" lines. See documentation for method
				 * getStacktrace(Throwable e, boolean cutTBS, String relevantPackage) for details. Also note that here we don't actually print the
				 * skip line but just set the flag "skipLineToBePrinted" because we don't know if the next line in the stacktrace may be actually a
				 * relevant line and then the skip line won't be needed to be printed.
				 */
                if (relevantPackageReached && !isCurLineRelevantPack) {
                    relevantPackageReached = false;
                    toBePrinted = false;
                    skipLineToBePrinted = true;
                }
            } else {
				/*
				 * This "else" branch deals with lines in the stacktrace that either start next singular stacktrace or are the last line in current
				 * singular stacktrace and it is of the form "... X more" where X is a number.
				 */
                if (trimmedLine.startsWith(CAUSE_STAKTRACE_PREFIX) || trimmedLine.startsWith(SUPPRESED_STAKTRACE_PREFIX)) {
					/*
					 * If this is the first line of next singular stacktrace we break out of the current cycle and return this line for the next
					 * iteration which will invoke this method again
					 */
                    break;
					/*
					 * If it is last line in current singular stacktrace that starts with "..." then we print it if needed based on the value of the
					 * flag "toBePrinted" or in the case if we needed to add our line "..." instead we just print the original line as it has also the
					 * number of skipped lines
					 */
                } else if (toBePrinted || skipLineToBePrinted) {
                    result.append(line).append("\n");

                    //Just in case
                    skipLineToBePrinted = false;
                }
            }
        }
        return line;
    }

    /**
     * This a getter method for global value of relevant package prefix property
     *
     * @return String that holds the prefix value.
     */
    public static String[] getRelevantPackage() {
        return RELEVANT_PACKAGE_LIST;
    }

    /**
     * This is a setter method for relevant package prefix array property for method {@link #getStacktrace(Throwable, boolean)} Once the value has been set
     * the convenience method {@link #getStacktrace(Throwable, boolean)} could be used instead of method
     * {@link #getStacktrace(Throwable, boolean, String...)}
     *
     * @param relevantPackages {@link String...} that contains the prefix or several prefixes specifying which lines are relevant. It is recommended to be in the following format
     *                        "package_name1.[package_name2.[...]]."
     * @see #getStacktrace(Throwable, boolean, String...)
     */
    public static void setRelevantPackage(String... relevantPackages) {
        RELEVANT_PACKAGE_LIST = relevantPackages;
    }

    /**
     * This method converts a String in such a way that its spaces are not modified by HTML renderer i.e. it replaces
     * regular space characters with non-breaking spaces known as '&amp;nbsp;' but they look in your source as regular
     * space '&nbsp;&nbsp;' and not as '&amp;nbsp;' It also replaces new line character with '&lt;br&gt;'.
     * Here is an example. Lets say that you would like to write a text that has indentations So you can not simply write
     * something like
     * <p><br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is non-indented line<br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is 2 spaces indented line<br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is 4 spaces indented line<br>
     * </p><br>
     * Such a text after rendered by html would result into single non-indented line:<br><br>
     * <p>
     * This is non-indented line This is 2 spaces indented line This is 4 spaces indented line
     *
     * </p><br>
     * The solution would be to write your text as follows:
     * <p>
     *     <br>
     *     This is non-indented line&lt;br&gt;<br>
     *     &amp;nbsp;&amp;nbsp;This is 2 spaces indented line&lt;br&gt;<br>
     *     &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;This is 4 spaces indented line<br>
     * </p><br>
     * That works just fine once rendered in HTML but your source now is not very readable and difficult to maintain
     * if you want to modify your indentations. So in order to remedy this you can pass your original string to this
     * method, say the string looks like this:
     * <p><br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is non-indented line<br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is 2 spaces indented line<br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is 4 spaces indented line<br>
     * </p><br>
     * And it will return you the string that looks like this:
     *<p><br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is non-indented line&lt;br&gt;<br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is 2 spaces indented line&lt;br&gt;<br>
     *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is 4 spaces indented line&lt;br&gt;<br>
     *</p><br>
     * Except, that besides visible addition of &lt;br&gt; at the end of the lines your regular spaces (U+0020) have been
     * replaced with non-breaking spaces (U+00A0) but they look the same in your source. So if you just place this
     * modified string into your HTML source code your indentation will be preserved and you source code is readable.
     * <br><b>IMPORTANT NOTE:</b> if you want to modify indentations later you can NOT just type additional spaces.
     * You will have to either use this method again after you modified your string or you can copy-pace those "normal
     * looking" but actually non-breaking spaces. Also this was tested and found to be working with just regular HTML, but
     * in combination with javascript it could sometimes produce unexpected results and instead of "normal-looking" space
     * may show an 'Â' symbol. So, this method has its limitations. Test your results before you deliver. Use this method
     * at your own risk.  &#x263a;
     * @param rawText to be converted
     * @return String that is converted as described above
     */
    public static String formatStringToPreserveIndentationForHtml(String rawText) {
        String result = rawText;
        if(StringUtils.isNotEmpty(rawText)) {
            result = rawText.replaceAll(" ", HTML_NON_BREAKING_SPACE_CHARACTER)
                    .replaceAll("\n", HTML_NEW_LINE + "\n");
        }
        return result;
    }

    private static void warn(String message, Throwable t) {
        if (RELEVANT_PACKAGE_LIST != null && RELEVANT_PACKAGE_LIST.length > 0) {
            logger.warn(message + getStacktrace(t));
        } else {
            logger.warn(message, t);
        }
    }

    private static void error(String message, Throwable t) {
        if (RELEVANT_PACKAGE_LIST != null && RELEVANT_PACKAGE_LIST.length > 0) {
            logger.error(message + getStacktrace(t));
        } else {
            logger.error(message, t);
        }
    }

    private static void debug(String message, Throwable t) {
        if (RELEVANT_PACKAGE_LIST != null && RELEVANT_PACKAGE_LIST.length > 0) {
            logger.debug(message + getStacktrace(t));
        } else {
            logger.debug(message, t);
        }
    }

    private static void fatal(String message, Throwable t) {
        if (RELEVANT_PACKAGE_LIST != null && RELEVANT_PACKAGE_LIST.length > 0) {
            logger.error(message + getStacktrace(t));
        } else {
            logger.error(message, t);
        }
    }

    private static void info(String message, Throwable t) {
        if (RELEVANT_PACKAGE_LIST != null && RELEVANT_PACKAGE_LIST.length > 0) {
            logger.info(message + getStacktrace(t));
        } else {
            logger.info(message, t);
        }
    }

    private static void trace(String message, Throwable t) {
        if (RELEVANT_PACKAGE_LIST != null && RELEVANT_PACKAGE_LIST.length > 0) {
            logger.trace(message + getStacktrace(t));
        } else {
            logger.trace(message, t);
        }
    }

    private static TimeInterval setTimeValue(String valueToParse, TimeInterval result) {
        if (result.getValue() == INITIAL_PARSING_VALUE) {
            result.setValue(Long.parseLong(valueToParse));
            if (result.getValue() < 1) {
                throw new IllegalArgumentException("Negative or zero value '" + result.getValue() + "' for time interval is illegal");
            }
        }
        return result;
    }

    private static TimeInterval setTimeUnit(boolean isLetter, String potentialSuffix, TimeInterval result) {
        if (isLetter) {
                result.setTimeUnit(getTimeUnitBySuffix(potentialSuffix));
        } else {
            result.setTimeUnit(DEFAULT_TIMEOUT_TIME_UNIT);
        }
        return result;
    }

    private static TimeUnit getTimeUnitBySuffix(String suffix) {
        TimeUnit result;
        switch (suffix.toLowerCase()) {
            case SECONDS_SUFFIX: {
                result = TimeUnit.SECONDS;
                break;
            }
            case MINUTES_SUFFIX: {
                result = TimeUnit.MINUTES;
                break;
            }
            case HOURS_SUFFIX: {
                result = TimeUnit.HOURS;
                break;
            }
            case DAYS_SUFFIX: {
                result = TimeUnit.DAYS;
                break;
            }
            default: {
                throw new IllegalArgumentException("Time Unit Suffix " + suffix + " is invalid. Valid values are:\n'" +
                        SECONDS_SUFFIX + "' for seconds \n'" +
                        MINUTES_SUFFIX + "' for minutes \n'" +
                        HOURS_SUFFIX + "' for hours \n'" +
                        DAYS_SUFFIX + "' for days");
            }
        }
        return result;
    }
}
