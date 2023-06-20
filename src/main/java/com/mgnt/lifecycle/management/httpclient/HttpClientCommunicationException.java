package com.mgnt.lifecycle.management.httpclient;

import java.io.IOException;

/**
 * This Exception is specifically for exclusive use with {@link HttpClient}. It holds {@link ResponseHolder} instance so
 * that it could provide additional information on the problem encountered.
 */
public class HttpClientCommunicationException extends IOException {
    private ResponseHolder responseHolder;

    public HttpClientCommunicationException() {
    }

    public HttpClientCommunicationException(IOException ioe) {
        this(ioe.getMessage(), ioe.getCause());
    }

    public HttpClientCommunicationException(String message) {
        super(message);
    }

    public HttpClientCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientCommunicationException(Throwable cause) {
        super(cause);
    }

    public HttpClientCommunicationException(ResponseHolder responseHolder) {
        super();
        this.responseHolder = responseHolder;
    }

    public HttpClientCommunicationException(String message, ResponseHolder responseHolder) {
        super(message);
        this.responseHolder = responseHolder;
    }

    public HttpClientCommunicationException(String message, Throwable cause, ResponseHolder responseHolder) {
        super(message, cause);
        this.responseHolder = responseHolder;
    }

    public HttpClientCommunicationException(Throwable cause, ResponseHolder responseHolder) {
        super(cause);
        this.responseHolder = responseHolder;
    }

    public ResponseHolder getResponseHolder() {
        return responseHolder;
    }

    public void setResponseHolder(ResponseHolder responseHolder) {
        this.responseHolder = responseHolder;
    }
}
