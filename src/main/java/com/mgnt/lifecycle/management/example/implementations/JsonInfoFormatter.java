package com.mgnt.lifecycle.management.example.implementations;

import com.mgnt.lifecycle.management.example.BaseInfoFormatter;

public class JsonInfoFormatter extends BaseInfoFormatter {
    private final static String CUSTOM_NAME = DATA_TYPE.JSON.toString();

    public JsonInfoFormatter() {
        super(CUSTOM_NAME);
    }

    @Override
    protected String doFormatMessage(String messageContent) {
        return "{\"value\" : \"" + messageContent + "\"}";
    }
}
