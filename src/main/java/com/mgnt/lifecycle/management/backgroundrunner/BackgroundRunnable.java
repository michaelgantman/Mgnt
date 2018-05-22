package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.utils.entities.TimeInterval;

/**
 * This is the interface that a user periodically executed Task class must implement. This is done when user defined
 * class extends {@link BaseBackgroundRunnable} class. Please see the source code in package
 * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b> that demonstrates the usage of this framework
 */
public interface BackgroundRunnable extends Runnable {

    /**
     *
     * @return {@link TimeInterval} that specifies how often the Task should be run
     */
    TimeInterval getTaskExecutionInterval();

    /**
     * This is internally implemented method (in class {@link BaseBackgroundRunnable}) it allows to initialize
     * other properties of type {@link TimeInterval} If such properties exist then the user will need to override
     * method {@link #setParamValue(TimeInterval, String)} so it will know what setter method to invoke based on
     * <b>propertyName</b> parameter. See the source code for class
     * <b>com.mgnt.lifecycle.management.backgroundrunner.example.TypeOneTask</b> that demonstrates this feature
     * @param valueStr String that contains a Time interval value such as "9h", "3m", "10s" etc
     * @param defaultValue {@link TimeInterval} that holds a default value for the property should parsing of <b>valueStr</b>
     * parameter fails
     * @param propertyName The name of the property to be set
     */
    void initTimeIntervalParam(String valueStr, TimeInterval defaultValue, String propertyName);

    /**
     * This method is a general setter method for all properties that hold {@link TimeInterval} value. It should be
     * implemented by user defined class implementing this interface. See source code for classes
     * <b>{@link com.mgnt.lifecycle.management.backgroundrunner.example.TypeOneTask}</b> and
     * <b>{@link com.mgnt.lifecycle.management.backgroundrunner.example.TypeTwoTask}</b> where one provide complex example
     * in which there are other properties besides Task Execution Interval and second that demonstrates a minimalistic
     * implementation
     * @param value {@link TimeInterval} value for the property
     * @param property name of the property to be set
     */
    void setParamValue(TimeInterval value, String property);

    /**
     * internally used method
     * @return
     */
    boolean isInitialized();

    /**
     * internally used method
     * @param initialized
     */
    void setInitialized(boolean initialized);

}
