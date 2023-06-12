package com.mgnt.lifecycle.management.httpclient;

import java.util.List;
import java.util.Map;

public class ResponseHolder<C> {
    private int responseCode = -1;
    private String responseMessage = null;
    private Map<String, List<String>> responseHeaders = null;
    private C responseContent;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public C getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(C responseContent) {
        this.responseContent = responseContent;
    }
}
