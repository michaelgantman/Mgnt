package com.mgnt.utils;

import com.mgnt.utils.entities.TimeInterval;
import com.mgnt.utils.textutils.InvalidVersionFormatException;
import com.mgnt.utils.textutils.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * This class provides various utilities for work with String that represents some other type. In current version this class provides methods for
 * converting a String into its numeric value of various types (Integer, Float, Byte, Double, Long, Short). There are 2 methods for retrieving
 * Exception stacktrace as a String in full or shortened version. Shortened version of the stacktrace will contain concise information focusing on
 * specific package or subpackage while removing long parts of irrelevant stacktrace. This could be very useful for logging in web-based architecture
 * where stacktrace may contain long parts of server provided classes trace that could be eliminated with the methods of this class while retaining
 * important parts of the stacktrace relating to user's packages. Also this class provides methods that work with textual representation of versions.
 * Valid version is a String of the following format:<br>
 * <br>
 * <p/>
 * X[.X[.X[...]]]
 * <p/>
 * <br>
 * <br>
 * where X is a zero or positive integer not larger than 2147483647. Leading or trailing white spaces in this string are permitted and are ignored.
 * Examples of valid versions are: "1.6", "58", "  7.34.17  " etc. (Note that last example contains both leading and trailing white spaces and it is
 * still a valid version)
 * <p>
 *     Note that this class has a loose dependency on slf4J library. If in the project some other compatible logging library is present
 *     (such as Log4J) this class will still work without any ill effects
 * </p>
 *
 * @author Michael Gantman
 */
public class TextUtils {

    private static final Logger logger = LoggerFactory.getLogger(TextUtils.class);

    protected static final TimeUnit DEFAULT_TIMEOUT_TIME_UNIT = TimeUnit.MINUTES;
    protected static final String SECONDS_SUFFIX = "s";
    protected static final String MINUTES_SUFFIX = "m";
    protected static final String HOURS_SUFFIX = "h";
    private static final long INITIAL_PARSING_VALUE = -1L;
    /*
     * Strings defined bellow are for the use of methods getStacktrace() of this class
     */
    private static String RELEVANT_PACKAGE = null;
    private static final String STANDARD_STAKTRACE_PREFIX = "at ";
    private static final String SKIPPING_LINES_STRING = "\t...";
    private static final String CAUSE_STAKTRACE_PREFIX = "Caused by:";
    private static final String SUPPRESED_STAKTRACE_PREFIX = "Suppressed:";
    private static final String RELEVANT_PACKAGE_SYSTEM_EVIRONMENT_VARIABLE = "MGNT_RELEVANT_PACKAGE";
    private static final String RELEVANT_PACKAGE_SYSTEM_PROPERTY = "mgnt.relevant.package";

    static {
        initRelevantPackageFromSystemProperty();
    }

    private static void initRelevantPackageFromSystemProperty() {
        String relevantPackage = System.getProperty(RELEVANT_PACKAGE_SYSTEM_PROPERTY);
        if(StringUtils.isBlank(relevantPackage)) {
            relevantPackage = System.getenv(RELEVANT_PACKAGE_SYSTEM_EVIRONMENT_VARIABLE);
        }
        if(StringUtils.isNotBlank(relevantPackage)) {
            setRelevantPackage(relevantPackage);
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
     * @throws com.mgnt.utils.textutils.InvalidVersionFormatException
     *          if the String parameter is not a valid Version
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
     * @param num                        CharSequence to be parsed
     * @param defaultValue                  value that will be returned by this method if parsing of the String failed
     * @param nullOrEmptyStringErrorMessage String that holds an error message that will printed into log if parameter {@code num} is null or blank
     * @param numberFormatErrorMessage      String that holds an error message that will printed into log if parameter {@code num} is not in appropriate format
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
     * This method parses String value into {@link TimeInterval}. This method supports time interval suffixes <b>"s"</b> for seconds,
     * <b>"m"</b> for minutes and <b>"h"</b> for hours. If String parameter contains no suffix the default is minutes. So for example string
     * "38s" will be parsed as 38 seconds, "24m" - 24 minutes "4h" - 4 hours, "45" as 45 minutes. If the string parses to a
     * negative numerical value or the string is not a valid numerical value then the {@code defaultValue} parameter is returned
     * assuming it is a value in minutes. So invoking this method with say parameters ("hello", 10L) would return 10 minutes.
     * Note that it is very convenient to extract time value from {@link TimeInterval}, See methods {@link TimeInterval#toMillis()},
     * {@link TimeInterval#toSeconds()}, {@link TimeInterval#toMinutes()}, {@link TimeInterval#toHours()},
     * {@link TimeInterval#toDays()}.  Various parsing errors if occur in this method are logged
     * @param valueStr String value to parse to {@link TimeInterval}
     * @param defaultValue long default value in minutes (must be positive)
     * @return {@link TimeInterval} parsed from the String or {@code defaultValue} parameter in minutes if parsing failed
     */
    public static TimeInterval parsingStringToTimeInterval(String valueStr, long defaultValue) {
        TimeInterval result = new TimeInterval();
        String potentialSuffix = valueStr.substring(valueStr.length() - 1);
        boolean isLetter = Character.isLetter(potentialSuffix.codePointAt(0));
        String valueToParse = (isLetter) ? valueStr.substring(0, valueStr.length() - 1) : valueStr;
        result.setValue(INITIAL_PARSING_VALUE);
        result = setTimeUnit(isLetter, potentialSuffix, defaultValue, result);
        result = setTimeValue(valueToParse, defaultValue, result);
        return result;
    }


    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in full or shortened format. Shortened format skips the lines in the
     * stacktrace that do not start with a configurable package prefix and replaces them with "..." line. The stacktrace is viewed as consisting
     * possibly of several parts. If stacktrace contains {@code "caused by"} or {@code "Suppressed"} section, each such section for the purposes of
     * this utility is called "Singular stacktrace". For example the stacktrace bellow contains 2 singular stacktraces: First is 4 top lines and the
     * second starting from the line {@code "Caused by: ..."} and to the end.<br>
     * <br>
     * <p/>
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
     * <p/>
     * The way this method shortens the stacktrace is as follows. Each "singular" stacktraces are analyzed and shortened separately. For each singular
     * stacktrace the error message is always printed. Then all the lines that follow are printed even if they do not start with prefix specified by
     * <b>relevantPackage</b>. Once the first line with the prefix is found this line and all immediately following lines that start with the relevant
     * package prefix are printed as well. The first line that does not start with the prefix after a section of the lines that did is also printed.
     * But all the following lines that do not start with the prefix are skipped and replaced with a single line "...". If at some point within the
     * stacktrace a line that starts with the prefix is encountered again this line and all the following line that start with the prefix + one
     * following line that does not start with the prefix are printed in. And so on. Here is an example: Assume that exception above was passed as a
     * parameter to this method and parameter <b>relevantPackage</b> is set to {@code "com.plain.analytics.v2.utils.test."} which means that the lines starting with
     * that prefix are the important or "relevant" lines. (Also the parameter <b>cutTBS</b> set to true which means that stacktrace should be
     * shortened at all. In this case the result of this method should be as follows:<br>
     * <br>
     * <p/>
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
     * <p/>
     * Note that the first singular stacktrace is printed in full because all the lines start with the required prefix. The second singular stacktrace
     * prints the first 7 lines because at first all the lines are printed until the first line with relevant prefix is found, and then all the lines
     * with the prefix (one in our case) are printed + plus one following line without the prefix. And then the second line without the prefix (3d
     * from the bottom) is skipped and replaced with line "...". But then again we encounter a line with the prefix which is printed and finally the
     * last line is printed because it is the first line without prefix following the one with the prefix. In this particular example only one line
     * was skipped over which is not very much, but for web-based environments for the long stacktraces that contain long traces of server related
     * classes this method could be very effective in removing irrelevant lines and leaving only application related lines making log files more
     * concise and clear.<br>
     * <br>
     * <p/>
     * <b>Important Note:</b> Parameter <b>relevantPackage</b> may be left null. In this case the value of relevant package prefix will be taken from
     * <b>RelevantPackage</b> property (See the methods {@link #setRelevantPackage(String)} and {@link #getRelevantPackage()}). Using method
     * {@link #setRelevantPackage(String)} to set the value will preset the value of relevant package prefix for all calls for which parameter
     * <b>relevantPackage</b> is null. In fact there is a convinience method {@link #getStacktrace(Throwable, boolean)} that invokes this method with
     * parameter <b>relevantPackage</b> set to null and relies on the globally set property through method {@link #setRelevantPackage(String)}.
     * However if the global property was not set and parameter <b>relevantPackage</b> was left null then the method will return stacktrace in full as
     * if the parameter <b>cutTBS</b> was set to false<br>
     *
     * @param e               {@link Throwable} from which stacktrace should be retrieved
     * @param cutTBS          boolean that specifies if stacktrace should be shortened. The stacktrace should be shortened if this flag is set to {@code true}.
     *                        Note that if this parameter set to {@code false} the stacktrace will be printed in full and parameter <b>relevantPackage</b> becomes
     *                        irrelevant.
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is recommended to be in the following format
     *                        "packag_name1.[package_name2.[...]]." In the example above it should be "com.plain.analytics.v2.utils.test.".
     * @return String with stacktrace value
     */
    public static String getStacktrace(Throwable e, boolean cutTBS, String relevantPackage) {
        StringBuilder result = new StringBuilder("\n");

        // retrieve full stacktrace as byte array
        ByteArrayOutputStream stacktraceContent = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stacktraceContent));

        // Determine the value of relevant package prefix
        String relPack = (relevantPackage != null && !relevantPackage.isEmpty()) ? relevantPackage : RELEVANT_PACKAGE;
        /*
		 * If the relevant package prefix was not set neither locally nor globally revert to retrieving full stacktrace even if shortening was
		 * requested
		 */
        if (relPack == null || "".equals(relPack)) {
            if (cutTBS) {
                cutTBS = false;
                logger.warn("Relevant package was not set for the method. Stacktrace can not be shortened. Returning full stacktrace");
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
     * This method retrieves a stacktrace from {@link Throwable} as a String in full or shortened format. This is convenience method that invokes
     * method {@link #getStacktrace(Throwable, boolean, String)} with last parameter as {@code null}. It relies on relevant package prefix to have
     * been set by method {@link #setRelevantPackage(String)}. There are several ways to pre-invoke method {@link #setRelevantPackage(String)}:<br>
     *     <ul>
     *     <li>Set system environment variable <b>"MGNT_RELEVANT_PACKAGE"</b> with relevant package value (for the purposes of our example
     *     it would be "com.plain.")</li>
     *     <li>Run your code with System property <b>"mgnt.relevant.package"</b> set to relevant package value It could be done with
     *     -D: <b>"-Dmgnt.relevant.package=com.plain."</b> Note that System property value would take precedence over environment variable
     *     if both are set</li>
     *     <li>In case when Spring framework is used and system property and environment variable described above are not used then it is
     * recommended to add the following bean into your Spring configuration xml file. This will ensure an invocation of method
     * {@link #setRelevantPackage(String)} which will appropriately initialize the package prefix and enable the use of this method
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
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static String getStacktrace(Throwable e, boolean cutTBS) {
        return getStacktrace(e, cutTBS, null);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in shortened format. This is convenience method that invokes method
     * {@link #getStacktrace(Throwable, boolean, String)} with second parameter set to {@code 'true'} and last parameter as {@code null}. It relies on
     * relevant package prefix to have been set by method {@link #setRelevantPackage(String)}. There are several ways to pre-invoke method {@link #setRelevantPackage(String)}:<br>
     *     <ul>
     *     <li>Set system environment variable <b>"MGNT_RELEVANT_PACKAGE"</b> with relevant package value (for the purposes of our example
     *     it would be "com.plain.")</li>
     *     <li>Run your code with System property <b>"mgnt.relevant.package"</b> set to relevant package value It could be done with
     *     -D: <b>"-Dmgnt.relevant.package=com.plain."</b> Note that System property value would take precedence over environment variable
     *     if both are set</li>
     *     <li>In case when Spring framework is used and system property and environment variable described above are not used then it is
     * recommended to add the following bean into your Spring configuration xml file. This will ensure an invocation of method
     * {@link #setRelevantPackage(String)} which will appropriately initialize the package prefix and enable the use of this method
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
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static String getStacktrace(Throwable e) {
        return getStacktrace(e, true, null);
    }

    /**
     * This method retrieves a stacktrace from {@link Throwable} as a String in shortened format. This is convenience method that invokes method
     * {@link #getStacktrace(Throwable, boolean, String)} with second parameter set to {@code 'true'}.
     *
     * @param e               {@link Throwable} from which stacktrace should be retrieved
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is recommended to be in the following format
     *                        "packag_name1.[package_name2.[...]]."
     * @return String that contains the stacktrace
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static String getStacktrace(Throwable e, String relevantPackage) {
        return getStacktrace(e, true, relevantPackage);
    }

    /**
     * This method traverses through Singular stacktrace and skips irrelevant lines from it replacing them by line "..." The resulting shortened
     * stacktrace is appended into {@link StringBuilder} The stacktrace is viewed as consisting possibly of several parts. If stacktrace contains
     * {@code "caused by"} or {@code "Suppressed"} section, each such section for the purposes of this utility is called "Singular stacktrace". For
     * more detailed explanation see method {@link #getStacktrace(Throwable, boolean, String)}
     *
     * @param result  {@link StringBuilder} to which the resultant stacktrace will be appended
     * @param relPack {@link String} that contains relevant package prefix
     * @param reader  {@link BufferedReader} that contains the source from where the stacktrace may be read line by line. Current position in the reader
     *                is assumed to be at the beginning of the second line of the current singular stacktrace, following the line with the name of the
     *                exception and error message
     * @param line    {@link String} that contains the first line of the current singular stacktrace i.e. the line with the name of the exception and
     *                error message
     * @return The first string of the next singular stacktrace or null if current singular stacktrace is the last one in the stacktrace
     * @throws IOException if any error occurs.
     * @see #getStacktrace(Throwable, boolean, String)
     */
    private static String traverseSingularStacktrace(StringBuilder result, String relPack, BufferedReader reader, String line)
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

                //Check if the current line starts with thge prefix (after the "at " part)
                isCurLineRelevantPack = trimmedLine.substring(STANDARD_STAKTRACE_PREFIX.length()).startsWith(relPack);
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
    public static String getRelevantPackage() {
        return RELEVANT_PACKAGE;
    }

    /**
     * This is a setter method for relevant package prefix property for method {@link #getStacktrace(Throwable, boolean)} Once the value has been set
     * the convenience method {@link #getStacktrace(Throwable, boolean)} could be used instead of method
     * {@link #getStacktrace(Throwable, boolean, String)}
     *
     * @param relevantPackage {@link String} that contains the prefix specifying which lines are relevant. It is recommended to be in the following format
     *                        "packag_name1.[package_name2.[...]]."
     * @see #getStacktrace(Throwable, boolean, String)
     */
    public static void setRelevantPackage(String relevantPackage) {
        RELEVANT_PACKAGE = relevantPackage;
    }

    private static void warn(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.warn(message + getStacktrace(t));
        } else {
            logger.warn(message, t);
        }
    }

    private static void error(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.error(message + getStacktrace(t));
        } else {
            logger.error(message, t);
        }
    }

    private static void debug(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.debug(message + getStacktrace(t));
        } else {
            logger.debug(message, t);
        }
    }

    private static void fatal(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.error(message + getStacktrace(t));
        } else {
            logger.error(message, t);
        }
    }

    private static void info(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.info(message + getStacktrace(t));
        } else {
            logger.info(message, t);
        }
    }

    private static void trace(String message, Throwable t) {
        if (RELEVANT_PACKAGE != null && !RELEVANT_PACKAGE.isEmpty()) {
            logger.trace(message + getStacktrace(t));
        } else {
            logger.trace(message, t);
        }
    }

    private static TimeInterval setTimeValue(String valueToParse, long defaultValue, TimeInterval result) {
        if (result.getValue() == INITIAL_PARSING_VALUE) {
            try {
                result.setValue(Long.parseLong(valueToParse));
                if(result.getValue() < 1) {
                    throw new IllegalArgumentException("Negative value '" + result.getValue() + "' for time interval is illegal");
                }
            } catch (Exception e) {
                logger.warn(
                        "Error occurred while parsing String \"{}\" to Long for time interval value. Using default value ({} minutes) instead. {}",
                        valueToParse, defaultValue, TextUtils.getStacktrace(e));
                result.setValue(defaultValue);
                result.setTimeUnit(DEFAULT_TIMEOUT_TIME_UNIT);
            }
        }
        return result;
    }

    private static TimeInterval setTimeUnit(boolean isLetter, String potentialSuffix, long defaultTimeValue, TimeInterval result) {
        if (isLetter) {
            try {
                result.setTimeUnit(getTimeUnitBySuffix(potentialSuffix));
            } catch (IllegalArgumentException iae) {
                result.setValue(defaultTimeValue);
                result.setTimeUnit(DEFAULT_TIMEOUT_TIME_UNIT);
                logger.warn(
                        "Error occurred while parsing suffix \"{}\" to TimeUnit. Using default value ({} minutes) instead. {}",
                        potentialSuffix, defaultTimeValue, TextUtils.getStacktrace(iae));
            }
        } else {
            result.setTimeUnit(DEFAULT_TIMEOUT_TIME_UNIT);
        }
        return result;
    }

    private static TimeUnit getTimeUnitBySuffix(String suffix) {
        TimeUnit result;
        if (SECONDS_SUFFIX.equalsIgnoreCase(suffix)) {
            result = TimeUnit.SECONDS;
        } else if (MINUTES_SUFFIX.equalsIgnoreCase(suffix)) {
            result = TimeUnit.MINUTES;
        } else if (HOURS_SUFFIX.equalsIgnoreCase(suffix)) {
            result = TimeUnit.HOURS;
        } else {
            throw new IllegalArgumentException("Time Unit Suffix " + suffix + " is invalid. Valid values are:\n'" +
                    SECONDS_SUFFIX + "' for seconds \n'" +
                    MINUTES_SUFFIX + "' for minutes \n'" +
                    HOURS_SUFFIX + "' for hours");
        }
        return result;
    }
}
