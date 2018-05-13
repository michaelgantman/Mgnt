package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.lifecycle.management.BaseEntity;
import com.mgnt.utils.TextUtils;
import com.mgnt.utils.entities.TimeInterval;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

public abstract class BaseBackgroundRunnable extends BaseEntity<BaseBackgroundRunnable> implements BackgroundRunnable {
    private static final String FACTORY_TYPE = BaseBackgroundRunnable.class.getSimpleName();
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseBackgroundRunnable.class);

    private boolean initialized = false;

    static {
        init(FACTORY_TYPE, BackgroundRunnableFactory.getFactoryInstance());
    }

    public BaseBackgroundRunnable() {
        super(FACTORY_TYPE);
    }

    @PostConstruct
    private void init() {
        initParams();
    }

    protected synchronized void initParams() {
        if(!isInitialized()) {
            initParamsForSpecificImplementation();
            setInitialized(true);
        }
    }

    protected abstract void initParamsForSpecificImplementation();

    @Override
    public void initTimeIntervalParam(String valueStr, TimeInterval defaultValue, String propertyName) {
        if (StringUtils.isNotBlank(valueStr)) {
            TimeInterval timeInterval;
            try {
                timeInterval = TextUtils.parsingStringToTimeInterval(valueStr);
                setParamValue(timeInterval, propertyName);
            } catch (IllegalArgumentException iae) {
                LOGGER.error("Error occurred while parsing time interval string: '{}'{}", valueStr, TextUtils.getStacktrace(iae));
                setParamValue(defaultValue, propertyName);
            }
        } else {
            setParamValue(defaultValue, propertyName);
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
