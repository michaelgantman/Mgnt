package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.BaseEntity;
import org.apache.commons.lang3.StringUtils;

/*
 * This is an abstract class that contains some mandatory code that ties every concrete implementation to its factory
 */
public abstract class BaseInfoFormatter extends BaseEntity<BaseInfoFormatter> implements InfoFormatter {

    // This is mandatory part of the code for the infrastructure to work
    private static final String FACTORY_TYPE = BaseInfoFormatter.class.getSimpleName();

    /*
     * This static initializer is the very important part here, it makes the factory accessible and known for
     * the constructors of concrete implementations so they can access its factory and insert themselves into it
     * Without this initializer the whole infrastructure won't work
     */
    static {
        init(FACTORY_TYPE, InfoFormatterFactory.getFactoryInstance());
    }

    /*
     * This constructor is not used in our example, but it is needed if you wish to be able to register
     * your concrete implementations with the class name as its name by default. This probably will be
     * most common use
     */
    public BaseInfoFormatter() {
        super(FACTORY_TYPE);
    }

    /*
     * This is constructor for registering your concrete implementations with custom names (as done in our example
     */
    public BaseInfoFormatter(String customName) {
        super(FACTORY_TYPE, customName);
    }

    // The end of mandatory part

    // Some business logic methods that are common to all concrete implementations
    protected boolean isMessageValid(String messageContent) {
        return StringUtils.isNotEmpty(messageContent);
    }

    //Implementation of interface declared method
    @Override
    public String formatMessage(String messageContent) {
        String result = null;
        if(isMessageValid(messageContent)) {
            result = doFormatMessage(messageContent);
        }
        return result;
    }

    /*
     * Methods declarations that should be implemented by all concrete implementations according to each one's specific
     * business logic
     */
    protected abstract String doFormatMessage(String messageContent);
}
