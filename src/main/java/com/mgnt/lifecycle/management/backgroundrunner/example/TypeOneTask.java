package com.mgnt.lifecycle.management.backgroundrunner.example;

import com.mgnt.lifecycle.management.backgroundrunner.BaseBackgroundRunnable;
import com.mgnt.utils.TextUtils;
import com.mgnt.utils.entities.TimeInterval;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates more complex example in which besides required "Execution time Interval" property there are two
 * more special properties - one simply called <b>{@code timeIntervalProperty}</b> just to demonstrate how to have
 * additional property of type {@link TimeInterval} and another one called <b>{@code parsedIntergerProperty}</b>. This is
 * just to demonstrate how to use this infrastructure for parsing of non-trivial properties. Lets say that you want to
 * support some string property that should be parsed into integer but you would like to support an arbitrary number
 * of spaces between digits (say value "5 6 1" should be parsed to integer 561). The idea is that for any non-trivially
 * parsed property in your class there will be a {@link String} type property and a real type property (in our case
 * {@link TimeInterval} and {@link Integer}) that will hold a value after the String type property counterpart is
 * successfully parsed. The String value properties should be initialized from configuration values.
 */
public class TypeOneTask extends BaseBackgroundRunnable {

    //THis is the default TimeInterval value
    private static final TimeInterval DEFAULT_VALUE = new TimeInterval(10, TimeUnit.SECONDS);

    //the couple of properties (String type and TimeInteval type for required execution time interval property
    private String executionTimeIntervalStr;
    private TimeInterval executionTimeInterval;

    //Property couple for additional TimeInterval property simply called "TimeIntervalProperty"
    private String timeIntervalPropertyStr;
    private TimeInterval timeIntervalProperty;

    //Property couple for our non-trivially parsed Integer
    private String parsedIntegerPropertyStr;
    private Integer parsedIntegerProperty;

    public TypeOneTask() {
        //Cheat for the purposes of this example - See the commentary for method init()
        init();
    }

    /*
     * Method init does manual initialization which in real life should be done by reading from configuration. In other
     * words it reads and initialises String property values. In our case all three require special "non-trivial"
     * parsing
     */
    private void init() {
        setExecutionTimeIntervalStr("5s");
        setTimeIntervalPropertyStr("12h");
        setParsedIntegerPropertyStr("5 6 1");

        /*
         * invoking method initParams is a cheat for the purposes of the example. If this infrastructure where
         * to be used within Spring framework method init params would have been invoked by init() method of
         * BaseBackgroundRunnable class that is annotated with "@PostConstruct" annotation
         */
        initParams();
    }

    /*
     * This is the implementation of a hook method which is an abstract method in our parent BaseBackgroundRunnable class.
     * Note that initTimeIntervalParam method will parse provided String Value into TimeInterval Instance and then invoke
     * setParamValue() method to set the particular value. So the implementations of initParamsForSpecificImplementation()
     * and setParamValue() methods should have some convention on how the name the properties to be set. In this case
     * I chose to have an enum value for each property.
     */
    @Override
    protected void initParamsForSpecificImplementation() {
        initTimeIntervalParam(getExecutionTimeIntervalStr(), DEFAULT_VALUE, PropertyName.EXECUTION_TIME_INTERVAL.name());
        initTimeIntervalParam(getTimeIntervalPropertyStr(), DEFAULT_VALUE, PropertyName.TIME_INTERVAL_PROPERTY.name());

        /*
         * Note that if you have another non-trivially parsed property but not of type TimeInteval then you should have
         * your own method that parses String value into your type and call it here.
         */
        parseAndSetParsedIntegerProperty(getParsedIntegerPropertyStr());
    }


    @Override
    public TimeInterval getTaskExecutionInterval() {
        /*
         * note that you can name your properties whatever you like. But since the interface BackgroundRunnable
         * requires a specific property so here we just implement the interface required method by wrapping
         * our custom named property method that we parsed in our initParamsForSpecificImplementation() implementation
         */
        return getExecutionTimeInterval();
    }

    /*
     * This method will be called by method initTimeIntervalParam() of our parent class that we invoked in our
     * implementation of a hook method initParamsForSpecificImplementation(). So when this method is called
     * particular property name value it should be in agreement with method initParamsForSpecificImplementation()
     * as to what it means. In tis case the contract is the enum "PropertyName"
     */
    @Override
    public void setParamValue(TimeInterval value, String property) {
        PropertyName propertyName = null;
        try {
            /*
             * Here we read and validate the name of the property that should be set
             */
            propertyName = PropertyName.valueOf(property);
        } catch (IllegalArgumentException iae) {
            //Handle error here, print it to a log, but really this should never happen
        }
        if(propertyName != null) {
            /*
             * Here based on the name of required property we invoke the appropriate setter to set the TimeInterval
             * value that was parsed from String value
             */
            switch (propertyName) {
                case EXECUTION_TIME_INTERVAL: {
                    setExecutionTimeInterval(value);
                    break;
                }
                case TIME_INTERVAL_PROPERTY: {
                    setTimeIntervalProperty(value);
                    break;
                }
            }
        }
    }

    /*
     * This method should implement the logic of the Task. It is this method that will be executed periodically
     * by this infrastructure. In this case we just print the parsed values of the properties to make a point that
     * String values were successfully parsed
     */
    @Override
    public void run() {
        System.out.println(new Date() + " TypeOneTask is been executed. TimeInterval property is: " +
                getTimeIntervalProperty().getValue() + " " + getTimeIntervalProperty().getTimeUnit() +
                " Parsed integer property: " + getParsedIntegerProperty());
    }

    public String getExecutionTimeIntervalStr() {
        return executionTimeIntervalStr;
    }

    public void setExecutionTimeIntervalStr(String executionTimeIntervalStr) {
        this.executionTimeIntervalStr = executionTimeIntervalStr;
    }

    public TimeInterval getExecutionTimeInterval() {
        return executionTimeInterval;
    }

    public void setExecutionTimeInterval(TimeInterval executionTimeInterval) {
        this.executionTimeInterval = executionTimeInterval;
    }

    public String getTimeIntervalPropertyStr() {
        return timeIntervalPropertyStr;
    }

    public void setTimeIntervalPropertyStr(String timeIntervalPropertyStr) {
        this.timeIntervalPropertyStr = timeIntervalPropertyStr;
    }

    public TimeInterval getTimeIntervalProperty() {
        return timeIntervalProperty;
    }

    public void setTimeIntervalProperty(TimeInterval timeIntervalProperty) {
        this.timeIntervalProperty = timeIntervalProperty;
    }

    public String getParsedIntegerPropertyStr() {
        return parsedIntegerPropertyStr;
    }

    public void setParsedIntegerPropertyStr(String parsedIntegerPropertyStr) {
        this.parsedIntegerPropertyStr = parsedIntegerPropertyStr;
    }

    public Integer getParsedIntegerProperty() {
        return parsedIntegerProperty;
    }

    public void setParsedIntegerProperty(Integer parsedIntegerProperty) {
        this.parsedIntegerProperty = parsedIntegerProperty;
    }

    /*
     * This is the custom method of parsing a String value into integer where spaces between digits are supported
     */
    private void parseAndSetParsedIntegerProperty(String parsedIntegerPropertyStr) {
        parsedIntegerPropertyStr = parsedIntegerPropertyStr.replaceAll(" ",  "");
        int value = TextUtils.parseStringToInt(parsedIntegerPropertyStr, 10, null, null);
        if(value < 1) {
            value = 10;
        }
        setParsedIntegerProperty(value);
    }

    /*
     * This is the enum that implements the "contract" between initParamsForSpecificImplementation() method and
     * setParamValue() method
     */
    private static enum PropertyName {
        EXECUTION_TIME_INTERVAL,
        TIME_INTERVAL_PROPERTY;
    }
}
