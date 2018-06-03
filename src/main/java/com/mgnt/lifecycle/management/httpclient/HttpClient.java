package com.mgnt.lifecycle.management.httpclient;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private String connectionUrl;
    private String contentType;

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /**
     *
     * @param requestUrl
     * @param callMethod
     * @return
     * @throws IOException
     */
    public String sendHttpRequest(String requestUrl, HttpMethod callMethod) throws IOException {
        return sendHttpRequest(requestUrl, callMethod, null);
    }

    /**
     *
     * @param requestUrl
     * @param callMethod
     * @param data
     * @return
     * @throws IOException
     */
    public String sendHttpRequest(String requestUrl, HttpMethod callMethod, String data) throws IOException {
        HttpURLConnection connection = sendRequest(requestUrl, callMethod, data);
        String response = readResponse(connection);
        connection.disconnect();
        return response;
    }

    /**
     *
     * @param connection
     * @return
     * @throws IOException
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        String response = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();
        }
        return response;
    }

    /**
     *
     * @param url
     * @param method
     * @param data
     * @return
     * @throws IOException
     */
    private HttpURLConnection sendRequest(String url, HttpMethod method, String data) throws IOException {
        boolean doOutput = StringUtils.isNotBlank(data);
        HttpURLConnection httpURLConnection = openHttpUrlConnection(url, method, doOutput);
        if (doOutput) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
                dataOutputStream.write(StandardCharsets.UTF_8.encode(data).array());
                dataOutputStream.flush();
            }
        }
        return httpURLConnection;
    }

    protected String getDefaultContentType() {
        return null;
    }

    public String getContentType() {
        return StringUtils.isNotBlank(contentType) ? contentType : getDefaultContentType();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public HttpURLConnection openHttpUrlConnection(String url, HttpMethod method) throws IOException {
        return openHttpUrlConnection(url, method, false);
    }

    /**
     *
     * @param url
     * @param method
     * @param doOutput
     * @return
     * @throws IOException
     */
    public HttpURLConnection openHttpUrlConnection(String url, HttpMethod method, boolean doOutput) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setDoOutput(doOutput);
        connection.setRequestMethod(method.toString());
        connection.setRequestProperty("Content-Type", getContentType());
        return connection;
    }

    public static enum HttpMethod {
        GET,
        PUT,
        POST,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE
    }
}
