package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.utils.entities.TimeInterval;

public interface BackgroundRunnable extends Runnable {
    TimeInterval getTaskExecutionInterval();

    void initTimeIntervalParam(String valueStr, TimeInterval defaultValue, String propertyName);

    void setParamValue(TimeInterval value, String property);

    boolean isInitialized();
    void setInitialized(boolean initialized);

}
