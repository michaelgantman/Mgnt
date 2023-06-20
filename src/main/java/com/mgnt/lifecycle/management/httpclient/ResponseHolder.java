package com.mgnt.lifecycle.management.httpclient;

import java.util.List;
import java.util.Map;

/**
 * This class contains the actual response data and metadata received upon sending Http Request by {@link HttpClient}
 * This class is used as a return type by all versions of <code>sendHttpRequest</code> method of {@link HttpClient} (See for example
 * {@link HttpClient#sendHttpRequest(String, HttpClient.HttpMethod, String)})
 * @param <C> is expected to be either <code>String</code> or <code>ByteBuffer</code> depending whether the returned data type is 
 *           textual or binary. (See methods {@link HttpClient#sendHttpRequest(String, HttpClient.HttpMethod, String)} and
 *           {@link HttpClient#sendHttpRequestForBinaryResponse(String, HttpClient.HttpMethod, String)} for example
 */
public class ResponseHolder<C> {
    private int responseCode = -1;
    private String responseMessage = null;
    private Map<String, List<String>> responseHeaders = null;
    private C responseContent;

    /**
     *
     * @return an HTTP response code of the executed HTTP request (200 for success for example)
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * This method sets the HTTP response code of the executed HTTP request
     * @param responseCode HTTP response code
     */
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
