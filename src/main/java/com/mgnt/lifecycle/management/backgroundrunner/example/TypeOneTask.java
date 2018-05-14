package com.mgnt.lifecycle.management.backgroundrunner.example;

import com.mgnt.lifecycle.management.backgroundrunner.BaseBackgroundRunnable;
import com.mgnt.utils.TextUtils;
import com.mgnt.utils.entities.TimeInterval;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TypeOneTask extends BaseBackgroundRunnable {

    private static final TimeInterval DEFAULT_VALUE = new TimeInterval(10, TimeUnit.SECONDS);
    private String executionTimeIntervalStr;
    private TimeInterval executionTimeInterval;
    private String timeIntervalPropertyStr;
    private TimeInterval timeIntervalProperty;
    private String parsedIntegerPropertyStr;
    private Integer parsedIntegerProperty;

    public TypeOneTask() {
        init();
    }

    private void init() {
        setExecutionTimeIntervalStr("5s");
        setTimeIntervalPropertyStr("12h");
        setParsedIntegerPropertyStr("5 6 1");
        initParams();
    }

    @Override
    protected void initParamsForSpecificImplementation() {
        initTimeIntervalParam(getExecutionTimeIntervalStr(), DEFAULT_VALUE, PropertyName.EXECUTION_TIME_INTERVAL.name());
        initTimeIntervalParam(getTimeIntervalPropertyStr(), DEFAULT_VALUE, PropertyName.TIME_INTERVAL_PROPERTY.name());
        parseAndSetParsedIntegerProperty(getParsedIntegerPropertyStr());
    }

    @Override
    public TimeInterval getTaskExecutionInterval() {
        return getExecutionTimeInterval();
    }

    @Override
    public void setParamValue(TimeInterval value, String property) {
        PropertyName propertyName = null;
        try {
            propertyName = PropertyName.valueOf(property);
        } catch (IllegalArgumentException iae) {
            //Handle error here, print it to a log, but really this should never happen
        }
        if(propertyName != null) {
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

    private void parseAndSetParsedIntegerProperty(String parsedIntegerPropertyStr) {
        parsedIntegerPropertyStr = parsedIntegerPropertyStr.replaceAll(" ",  "");
        int value = TextUtils.parseStringToInt(parsedIntegerPropertyStr, 10, null, null);
        if(value < 1) {
            value = 10;
        }
        setParsedIntegerProperty(value);
    }

    private static enum PropertyName {
        EXECUTION_TIME_INTERVAL,
        TIME_INTERVAL_PROPERTY;
    }
}
