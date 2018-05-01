package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.BaseEntity;
import org.apache.commons.lang3.StringUtils;

public abstract class BaseInfoFormatter extends BaseEntity<BaseInfoFormatter> implements InfoFormatter {

    // This is mandatory part of the code for the infrastructure to work
    private static final String FACTORY_TYPE = BaseInfoFormatter.class.getSimpleName();

    static {
        init(FACTORY_TYPE, InfoFormatterFactory.getFactoryInstance());
    }

    public BaseInfoFormatter() {
        super(FACTORY_TYPE);
    }

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
