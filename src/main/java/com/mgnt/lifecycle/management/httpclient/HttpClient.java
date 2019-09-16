package com.mgnt.lifecycle.management.httpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.mgnt.utils.TextUtils;
import com.mgnt.utils.WebUtils;
import com.mgnt.utils.entities.TimeInterval;

/**
 * This class is meant to be a parent class to any class that wants to implement an http access to a particular
 * cite. In this case the extending class may inherit from this class, set a predefined {@link #connectionUrl} property
 * and then simply add some methods that would be used to contact some different sub-urls with the same url base. Those
 * methods may receive some parameters that would enable to build final url based on pre-set base url. However this
 * class may be used on its own as well. All you will need to do is to set a value for content type using method
 * {@link #setContentType(String)} if needed, or/and any other request property using method 
 * {@link #setRequestProperty(String, String)} and then just call method {@link #sendHttpRequest(String, HttpMethod)}
 * or {@link #sendHttpRequest(String, HttpMethod, String)} (or if the reply is expected to be binary such as image then
 * call the methods {@link #sendHttpRequestForBinaryResponse(String, HttpMethod)} or
 * {@link #sendHttpRequestForBinaryResponse(String, HttpMethod, String)})
 */
public class HttpClient {
	
	private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
	private Map<String, String> requestPropertiesMap = new HashMap<>();
    private String connectionUrl;
    private int lastResponseCode = -1;
    private String lastResponseMessage = null;
    private TimeInterval connectTimeout = new TimeInterval(0, TimeUnit.MILLISECONDS);
    private TimeInterval readTimeout = new TimeInterval(0, TimeUnit.MILLISECONDS);

    /**
     * This method is a getter for connectionUrl property which only makes sense if that property was set. Setting
     * this property is intended for extending classes if such class provides several methods contacting urls based on
     * the same base url. In this case building the final url for each such method can use base url that is common for
     * them
     * @return String that holds common URL prefix
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * Setter for connectionUrl property. This method is intended for use by extending classes if they provide methods
     * that contact URLs that have common prefix
     * @param connectionUrl
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /**
     * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses 
     * specified HTTP method. Obviously it is expected that user should set connectionUrl property by invoking method 
     * {@link #setConnectionUrl(String)} beforehand. It returns response
     * that is expected to be textual such as a String. This method does not send any info through request body.
     * It may send parameters through URL. So this method fits perfectly for using HTTP GET method. If HTTP request
     * uses method POST, PUT or any other methods that allow passing info in the request body and there is some info
     * to be sent then use method {@link #sendHttpRequest(HttpMethod, String)}
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @return String that holds response from the URL
     * @throws IOException
     */
    public String sendHttpRequest(HttpMethod callMethod) throws IOException {
        return sendHttpRequest(getConnectionUrl(), callMethod, null);
    }

    /**
     * This method sends HTTP request to specified URL and uses specified HTTP method. It returns response
     * that is expected to be textual such as a String. This method does not send any info through request body.
     * It may send parameters through URL. So this method fits perfectly for using HTTP GET method. If HTTP request
     * uses method POST, PUT or any other methods that allow passing info in the request body and there is some info
     * to be sent then use method {@link #sendHttpRequest(String, HttpMethod, String)}
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @return String that holds response from the URL
     * @throws IOException
     */
    public String sendHttpRequest(String requestUrl, HttpMethod callMethod) throws IOException {
        return sendHttpRequest(requestUrl, callMethod, null);
    }

    /**
     * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses 
     * specified HTTP method and sends data through request body. Obviously it is expected that user should set connectionUrl 
     * property by invoking method {@link #setConnectionUrl(String)} beforehand.      
     * It returns response that is expected to be textual such as a String. This method fits for using HTTP  methods
     * POST, PUT or any other methods that allow passing info in the body request and there is some info to be sent.
     * If you don't need to send any info as request body, consider using method
     * {@link #sendHttpRequest(HttpMethod)}
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return String that holds response from the URL
     * @throws IOException
     */
    public String sendHttpRequest(HttpMethod callMethod, String data) throws IOException {
    	return sendHttpRequest(getConnectionUrl(), callMethod, data);
    }

    /**
     * This method sends HTTP request to specified URL, uses specified HTTP method and sends data through request body.
     * It returns response that is expected to be textual such as a String. This method fits for using HTTP  methods
     * POST, PUT or any other methods that allow passing info in the body request and there is some info to be sent.
     * If you don't need to send any info as request body, consider using method
     * {@link #sendHttpRequest(String, HttpMethod)}
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return String that holds response from the URL
     * @throws IOException
     */
    public String sendHttpRequest(String requestUrl, HttpMethod callMethod, String data) throws IOException {
        HttpURLConnection connection = sendRequest(requestUrl, callMethod, data);
        String response = readResponse(connection);
        connection.disconnect();
        return response;
    }

    /**
     * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses 
     * specified HTTP method. Obviously it is expected that user should set connectionUrl property by invoking method 
     * {@link #setConnectionUrl(String)} beforehand.
     * This method is the same as {@link #sendHttpRequest(HttpMethod)} except that it returns {@link ByteBuffer}
     * that holds binary info. So this methods fits for retrieving binary response such as Image, Video, Audio or any
     * other info in binary format.
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @return {@link ByteBuffer} that holds response from URL
     * @throws IOException
     */
    public ByteBuffer sendHttpRequestForBinaryResponse(HttpMethod callMethod) throws IOException {
        return sendHttpRequestForBinaryResponse(getConnectionUrl(), callMethod, null);
    }

    /**
     * This method is the same as {@link #sendHttpRequest(String, HttpMethod)} except that it returns {@link ByteBuffer}
     * that holds binary info. So this methods fits for retrieving binary response such as Image, Video, Audio or any
     * other info in binary format.
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @return {@link ByteBuffer} that holds response from URL
     * @throws IOException
     */
    public ByteBuffer sendHttpRequestForBinaryResponse(String requestUrl, HttpMethod callMethod) throws IOException {
        return sendHttpRequestForBinaryResponse(requestUrl, callMethod, null);
    }

    /**
     * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses 
     * specified HTTP method. Obviously it is expected that user should set connectionUrl property by invoking method 
     * {@link #setConnectionUrl(String)} beforehand.     
     * This method is the same as {@link #sendHttpRequest(HttpMethod, String)} except that it returns
     * {@link ByteBuffer} that holds binary info. So this methods fits for retrieving binary response such as Image,
     * Video, Audio or any other info in binary format.
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return {@link ByteBuffer} that holds response from URL
     * @throws IOException
     */
    public ByteBuffer sendHttpRequestForBinaryResponse(HttpMethod callMethod, String data) throws IOException {
    	return sendHttpRequestForBinaryResponse(getConnectionUrl(),callMethod, data);
    }

    /**
     * This method is the same as {@link #sendHttpRequest(String, HttpMethod, String)} except that it returns
     * {@link ByteBuffer} that holds binary info. So this methods fits for retrieving binary response such as Image,
     * Video, Audio or any other info in binary format.
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return {@link ByteBuffer} that holds response from URL
     * @throws IOException
     */
    public ByteBuffer sendHttpRequestForBinaryResponse(String requestUrl, HttpMethod callMethod, String data) throws IOException {
        HttpURLConnection connection = sendRequest(requestUrl, callMethod, data);
        ByteBuffer response = readBinaryResponse(connection);
        setLastResponseCode(connection.getResponseCode());
        setLastResponseMessage(connection.getResponseMessage());
        connection.disconnect();
        return response;
    }

    /**
     * This is getter method for content type property. Returns default value if the property was not set. This
     * method intended for use in extending classes
     * @return content type property value or default value if the property was not set
     */
    public String getContentType() {
    	String contentType = requestPropertiesMap.get(CONTENT_TYPE_HEADER_KEY);
    	if(StringUtils.isBlank(contentType)) {
    		contentType = getDefaultContentType();
    		if(StringUtils.isNotBlank(contentType)) { 
    			requestPropertiesMap.put(CONTENT_TYPE_HEADER_KEY, contentType);
    		}
    	}
        return contentType;
    }
    
    /**
     * This method is a general request property setter.
     * @param propertyName if the value of property name is blank or null this method does nothing
     * @param value Holds the value for the property
     */
    public void setRequestProperty(String propertyName, String value) {
    	if(StringUtils.isNotBlank(propertyName)) {
    		requestPropertiesMap.put(propertyName, value);
    	}
    }
    
    /**
     * This method removes request property
     * @param propertyName if the value of propertyName is blank or null this method does nothing and returns null 
     * @return the value that has been removed or null if the propertyName key was not present in the properties
     */
    public String removeRequestProperty(String propertyName) {
    	String result = null;
    	if(StringUtils.isNotBlank(propertyName)) {
    		result = requestPropertiesMap.remove(propertyName);
    	}
    	return result;
    }
    
    /**
     * This method clears all request properties.
     */
    public void clearAllRequestProperties() {
    	requestPropertiesMap.clear();
    }

    /**
     * This is setter method for content type property. This method intended for use in extending classes
     * @param contentType holds content type value
     */
    public void setContentType(String contentType) {
    	requestPropertiesMap.put(CONTENT_TYPE_HEADER_KEY, contentType);
    }

    public int getLastResponseCode() {
		return lastResponseCode;
	}

	public String getLastResponseMessage() {
		return lastResponseMessage;
	}

	public TimeInterval getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(TimeInterval connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setConnectTimeout(String timeIntervalStr) throws IllegalArgumentException {
		this.connectTimeout = TextUtils.parseStringToTimeInterval(timeIntervalStr);
	}
	
	public void setConnectTimeout(long timeValue, TimeUnit timeUnit) throws IllegalArgumentException {
		if(timeValue < 0) {
			throw new IllegalArgumentException("Negative value for timeout");
		}
		if(timeUnit == null) {
			throw new IllegalArgumentException("Null Time Unit value for timeout");
		}
		this.connectTimeout = new TimeInterval(timeValue, timeUnit);
	}

	public TimeInterval getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(TimeInterval readTimeout) {
		this.readTimeout = readTimeout;
	}


	public void setReadTimeout(String timeIntervalStr) throws IllegalArgumentException {
		this.readTimeout = TextUtils.parseStringToTimeInterval(timeIntervalStr);
	}
	
	public void setReadTimeout(long timeValue, TimeUnit timeUnit) throws IllegalArgumentException {
		if(timeValue < 0) {
			throw new IllegalArgumentException("Negative value for timeout");
		}
		if(timeUnit == null) {
			throw new IllegalArgumentException("Null Time Unit value for timeout");
		}
		this.readTimeout = new TimeInterval(timeValue, timeUnit);
	}
	
	/**
     * This method creates and opens {@link HttpURLConnection} to specified URL The connection's property
     * doOutput is set to false
     * @param url Url to be connected to
     * @param method {@link HttpMethod} that specifies which HTTP Method to use
     * @return opened {@link HttpURLConnection}
     * @throws IOException
     */
    protected HttpURLConnection openHttpUrlConnection(String url, HttpMethod method) throws IOException {
        return openHttpUrlConnection(url, method, false);
    }

    /**
     * This method creates and opens {@link HttpURLConnection} to specified URL.
     * @param url Url to be connected to
     * @param method {@link HttpMethod} that specifies which HTTP Method to use
     * @param doOutput boolean value that specifies how to set connection's doOutput property
     * @return opened {@link HttpURLConnection}
     * @throws IOException
     */
    protected HttpURLConnection openHttpUrlConnection(String url, HttpMethod method, boolean doOutput) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        setConnectionTimeout(connection);
        setReadTimeout(connection);
        connection.setDoOutput(doOutput);
        connection.setRequestMethod(method.toString());
        for(String requestProperty : requestPropertiesMap.keySet()) {
        	connection.setRequestProperty(requestProperty, requestPropertiesMap.get(requestProperty));
        }
        return connection;
    }
    
    /**
     * This method is intended to be overridden by extending classes if those classes provide methods that send
     * requests with the same content type
     * @return default content type (See {@link #getContentType()})
     */
    protected String getDefaultContentType() {
    	return null;
    }
	
	private void setLastResponseCode(int lastResponseCode) {
		this.lastResponseCode = lastResponseCode;
	}

	private void setLastResponseMessage(String lastResponseMessage) {
		this.lastResponseMessage = lastResponseMessage;
	}

    /**
     * This method reads response from {@link HttpURLConnection} expecting response to be in Textual format
     * @param connection {@link HttpURLConnection} from which to read the response
     * @return String that holds the response
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
     * This method reads response from {@link HttpURLConnection} and returns it in raw binary format
     * @param connection {@link HttpURLConnection} from which to read the response
     * @return {@link ByteBuffer} that contains the response
     * @throws IOException
     */
    private ByteBuffer readBinaryResponse(HttpURLConnection connection) throws IOException {
    	List<ByteBuffer> content = new LinkedList<>();
    	int totalLength = 0;
    	try (InputStream is = connection.getInputStream()) {
    		while(true) {
    			byte[] buff = new byte[WebUtils.DEFAULT_BUFFER_SIZE];
    			int length = is.read(buff);
    			if(length >= 0) {
    				content.add(ByteBuffer.wrap(buff, 0, length));
    				totalLength += length;
    			} else {
    				break;
    			}
    		}
    	}
    	ByteBuffer result = ByteBuffer.allocate(totalLength);
    	for(ByteBuffer byteBuffer : content) {
    		result.put(byteBuffer);
    	}
    	return result;
    }
    
    /**
     * This method sends the HTTP request and returns opened {@link HttpURLConnection}. If <b><i>data</i></b>
     * parameter is not null or empty it sends it as request body content
     * @param url URL to which request is sent
     * @param method {@link HttpMethod} that specifies which HTTP Method to use
     * @param data request body content. May be <b>NULL</b> if no request body needs to be sent
     * @return open {@link HttpURLConnection}
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

	private HttpURLConnection setConnectionTimeout(HttpURLConnection connection) {
		int connectionTimeoutMs = Long.valueOf((getConnectTimeout().toMillis())).intValue();
        if(connectionTimeoutMs > 0L) {
        	connection.setConnectTimeout(connectionTimeoutMs);
        }
        return connection;
	}


	private HttpURLConnection setReadTimeout(HttpURLConnection connection) {
		int readTimeoutMs = Long.valueOf((getReadTimeout().toMillis())).intValue();
        if(readTimeoutMs > 0L) {
        	connection.setReadTimeout(readTimeoutMs);
        }
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
