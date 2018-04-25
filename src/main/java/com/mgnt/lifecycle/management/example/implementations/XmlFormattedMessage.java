package com.mgnt.lifecycle.management.example.implementations;

import com.mgnt.lifecycle.management.example.BaseFormattedMessage;

public class XmlFormattedMessage extends BaseFormattedMessage {

    private final static String CUSTOM_NAME = "XML";

    public XmlFormattedMessage() {
        super(CUSTOM_NAME);
    }

    @Override
    protected String formatMessage(String messageContent) {
        return "<greetings><value>" + messageContent + "</value></greetings>";
    }
}
