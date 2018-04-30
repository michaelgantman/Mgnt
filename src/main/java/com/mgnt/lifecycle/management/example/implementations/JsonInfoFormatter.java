package com.mgnt.lifecycle.management.example.implementations;

import com.mgnt.lifecycle.management.example.BaseInfoFormatter;

public class JsonInfoFormatter extends BaseInfoFormatter {
    private final static String CUSTOM_NAME = "JSON";

    public JsonInfoFormatter() {
        super(CUSTOM_NAME);
    }

    @Override
    protected String formatMessage(String messageContent) {
        return "{\"value\" : \"" + messageContent + "\"}";
    }
}
