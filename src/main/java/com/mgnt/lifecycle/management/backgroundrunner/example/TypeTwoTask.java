package com.mgnt.lifecycle.management.backgroundrunner.example;

import com.mgnt.lifecycle.management.backgroundrunner.BaseBackgroundRunnable;
import com.mgnt.utils.entities.TimeInterval;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class demonstrates minimalistic example in which there is only required "Execution time Interval" property which
 * requires special parsing
 */
public class TypeTwoTask extends BaseBackgroundRunnable {
    private static final TimeInterval DEFAULT_VALUE = new TimeInterval(15, TimeUnit.SECONDS);

    /*
     * A couple of Properties related to "Execution time Interval" property. One of type String that is read from
     * configuration and a second is to hold the parsed TimeInterval value from that String
     */
    private String executionTimeIntervalTypeTwoStr;
    private TimeInterval executionTimeIntervalTypeTwo;

    public TypeTwoTask() {
        //Cheat for the purposes of this example - See the commentary for method init()
        init();
    }

    /*
     * Method init does manual initialization which in real life should be done by reading from configuration. In other
     * words it reads and initialises String property values.
     */
    private void init() {
        setExecutionTimeIntervalTypeTwoStr("10s");
        /*
         * invoking method initParams is a cheat for the purposes of the example. If this infrastructure where
         * to be used within Spring framework method init params would have been invoked by init() method of
         * BaseBackgroundRunnable class that is annotated with "@PostConstruct" annotation
         */
        initParams();
    }

    @Override
    protected void initParamsForSpecificImplementation() {
        /*
         * this method must be implemented by a user defined Task class, but here is the minimalistic implementation.
         * If your task does not have any other TimeInterval parameters or any other non-trivially parsed parameters
         * then here we only need to make sure that we parse our String value for task execution intervals. Remeber
         * that initTimeIntervalParam() implementation in the parent class will invoke setParamValueImplementation
         * with parsed TimeInterval value
         */
        initTimeIntervalParam(getExecutionTimeIntervalTypeTwoStr(), DEFAULT_VALUE, null);
    }

    @Override
    public TimeInterval getTaskExecutionInterval() {
        /*
         * note that you can name your properties whatever you like. But since the interface BackgroundRunnable
         * requires a specific property so here we just implement the interface required method by wrapping
         * our custom named property method that we parsed in our initParamsForSpecificImplementation() implementation
         */
        return getExecutionTimeIntervalTypeTwo();
    }

    @Override
    public void setParamValue(TimeInterval value, String property) {
        /*
         * Here we may ignore propery parameter, since in this siplistic implementation we know that the only
         * parameter of TimeInterval type we have is our required parameter. SO we always know which setter to call
         */
        setExecutionTimeIntervalTypeTwo(value);
    }

    /*
     * This method should implement the logic of the Task. It is this method that will be executed periodically
     * by this infrastructure. In this case we just print a message that method was invoked as a proof that
     * infrastructure works
     */
    @Override
    public void run() {
        System.out.println(new Date() + " TypeTwoTask is been executed.");
    }

    public String getExecutionTimeIntervalTypeTwoStr() {
        return executionTimeIntervalTypeTwoStr;
    }

    public void setExecutionTimeIntervalTypeTwoStr(String executionTimeIntervalTypeTwoStr) {
        this.executionTimeIntervalTypeTwoStr = executionTimeIntervalTypeTwoStr;
    }

    public TimeInterval getExecutionTimeIntervalTypeTwo() {
        return executionTimeIntervalTypeTwo;
    }

    public void setExecutionTimeIntervalTypeTwo(TimeInterval executionTimeIntervalTypeTwo) {
        this.executionTimeIntervalTypeTwo = executionTimeIntervalTypeTwo;
    }
}
