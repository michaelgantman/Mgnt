package com.mgnt.lifecycle.management.httpclient;

import java.util.List;
import java.util.Map;

/**
 * This class contains the actual response data and metadata received upon sending Http Request by {@link HttpClient}
 * This class is used as a return type by all versions of <code>sendHttpRequest</code> method of {@link HttpClient}. (See for example
 * {@link HttpClient#sendHttpRequest(String, HttpClient.HttpMethod, String)})
 * @since 1.7.0.0
 * @param <C> is expected to be either <code>String</code> or <code>ByteBuffer</code> depending on whether the returned data type is
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

    /**
     *
     * @return Message from the server side. It may be null. This message may be useful in case some error occurred and server
     * may respond with some valuable information
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    /**
     * This method sets sets the response message
     * @param responseMessage response message content
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     *
     * @return Map of Response headers from the server side
     */
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * This method sets the response headers
     * @param responseHeaders Map with response headers
     */
    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     *
     * @return the content of the response received from server side. The type is expected to be either <code>String</code> or <code>ByteBuffer</code> depending on whether the returned data type is
     *           textual or binary. (See methods {@link HttpClient#sendHttpRequest(String, HttpClient.HttpMethod, String)} and
     *           {@link HttpClient#sendHttpRequestForBinaryResponse(String, HttpClient.HttpMethod, String)} for example
     */
    public C getResponseContent() {
        return responseContent;
    }

    /**
     * THis method sets the content of the response from server side
     * @param responseContent
     */
    public void setResponseContent(C responseContent) {
        this.responseContent = responseContent;
    }
}
