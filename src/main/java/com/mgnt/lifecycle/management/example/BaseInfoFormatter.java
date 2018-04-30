package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.BaseEntity;
import org.apache.commons.lang3.StringUtils;

public abstract class BaseInfoFormatter extends BaseEntity<BaseInfoFormatter> implements InfoFormatter {
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

    protected boolean isMessageValid(String messageContent) {
        return StringUtils.isNotEmpty(messageContent);
    }

    @Override
    public String getFormattedMessage(String messageContent) {
        String result = null;
        if(isMessageValid(messageContent)) {
            result = formatMessage(messageContent);
        }
        return result;
    }

    protected abstract String formatMessage(String messageContent);
}
