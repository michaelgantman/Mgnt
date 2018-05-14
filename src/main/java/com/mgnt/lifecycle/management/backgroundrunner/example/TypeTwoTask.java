package com.mgnt.lifecycle.management.backgroundrunner.example;

import com.mgnt.lifecycle.management.backgroundrunner.BaseBackgroundRunnable;
import com.mgnt.utils.entities.TimeInterval;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TypeTwoTask extends BaseBackgroundRunnable {
    private static final TimeInterval DEFAULT_VALUE = new TimeInterval(15, TimeUnit.SECONDS);
    private String executionTimeIntervalTypeTwoStr;
    private TimeInterval executionTimeIntervalTypeTwo;

    public TypeTwoTask() {
        init();
    }

    private void init() {
        setExecutionTimeIntervalTypeTwoStr("10s");
        initParams();
    }

    @Override
    protected void initParamsForSpecificImplementation() {
        initTimeIntervalParam(getExecutionTimeIntervalTypeTwoStr(), DEFAULT_VALUE, null);
    }

    @Override
    public TimeInterval getTaskExecutionInterval() {
        return getExecutionTimeIntervalTypeTwo();
    }

    @Override
    public void setParamValue(TimeInterval value, String property) {
        setExecutionTimeIntervalTypeTwo(value);
    }

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
