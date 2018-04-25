package com.mgnt.lifecycle.management.example.implementations;

import com.mgnt.lifecycle.management.example.BaseFormattedMessage;

public class JsonFormattedMessage extends BaseFormattedMessage {
    private final static String CUSTOM_NAME = "JSON";

    public JsonFormattedMessage() {
        super(CUSTOM_NAME);
    }

    @Override
    protected String formatMessage(String messageContent) {
        return "{\"value\" : \"" + messageContent + "\"}";
    }
}
