package com.mgnt.lifecycle.management.example.implementations;

import com.mgnt.lifecycle.management.example.BaseInfoFormatter;

public class XmlInfoFormatter extends BaseInfoFormatter {

    private final static String CUSTOM_NAME = DATA_TYPE.XML.toString();

    public XmlInfoFormatter() {
        super(CUSTOM_NAME);
    }

    @Override
    protected String doFormatMessage(String messageContent) {
        return "<greetings><value>" + messageContent + "</value></greetings>";
    }
}
