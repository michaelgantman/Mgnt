package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.lifecycle.management.BaseEntity;
import com.mgnt.utils.TextUtils;
import com.mgnt.utils.entities.TimeInterval;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * This is the base class for any user defined periodic Task classes See source code example in package
 * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b> that demonstrates how it is done
 */
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

    /**
     * A hook method to be implemented by user extensions of this class. See source code example in package
     * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b>
     */
    protected abstract void initParamsForSpecificImplementation();

    /**
     * This method parses String value into a {@link TimeInterval} and then invokes method
     * {@link #setParamValue(TimeInterval, String)} to set required property value. If parsing failed then default value
     * is used.
     * @param valueStr String that contains a Time interval value such as "9h", "3m", "10s" etc
     * @param defaultValue {@link TimeInterval} that holds a default value for the property should parsing of <b>valueStr</b>
     * parameter fails
     * @param propertyName The name of the property to be set
     */
    @Override
    public void initTimeIntervalParam(String valueStr, TimeInterval defaultValue, String propertyName) {
        if (StringUtils.isNotBlank(valueStr)) {
            TimeInterval timeInterval;
            try {
                timeInterval = TextUtils.parseStringToTimeInterval(valueStr);
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
