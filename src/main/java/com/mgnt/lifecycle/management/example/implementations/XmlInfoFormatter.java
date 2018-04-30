package com.mgnt.lifecycle.management.example.implementations;

import com.mgnt.lifecycle.management.example.BaseInfoFormatter;

public class XmlInfoFormatter extends BaseInfoFormatter {

    private final static String CUSTOM_NAME = "XML";

    public XmlInfoFormatter() {
        super(CUSTOM_NAME);
    }

    @Override
    protected String formatMessage(String messageContent) {
        return "<greetings><value>" + messageContent + "</value></greetings>";
    }
}
