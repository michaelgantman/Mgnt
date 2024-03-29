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
 * This class is specialized purpose Http Client. While it can be used as general purpose Http Client this is not what this class
 * is meant for. This class does not provide the entire width of HTTP functionality as other General purpose Http clients do, but
 * provides customized functionality for specific use-case. The main point is that this class is NOT, strictly speaking, thread-safe
 * and this is by design. This Http client is meant to provide easy use for repeated http requests that share the same environment -
 * the same headers with the same values and the same URL or the same root sub-URL. So, this class holds some state (which makes it not
 * Thread-safe). In particular, it holds such properties as {@link #connectionUrl}, a map of Request headers
 * ({@link #requestPropertiesMap}) and timeout values for connection and read operations ({@link #connectTimeout}, {@link #readTimeout}).
 * <br><br>
 * The intended use for this class is that for each instance it's state will be set once and will not be changed, and then
 * it could be used for multiple HTTP requests that share the same state (same headers and their values, and optionally the
 * timeouts and url). If the state of the instance of this class is not changed
 * between invocations of method <code>sendHttpRequest</code> then the instance of this class can be safely used by multiple
 * threads and thread-safety is guaranteed. The use-case for this class is for repeated calls to predefined URL(s) with the same headers.
 * <br><br>Here is an example: Assume that you have some REST API that deals with a "Person" entity. Assume that you have the following
 * endpoints:<br>
 * <b>http://www.some-site/person</b> (GET) for read<br>
 * <b>http://www.some-site/person</b> (POST) for create<br>
 * <b>http://www.some-site/person</b> (PUT) for update<br>
 * <b>http://www.some-site/person</b> (DELETE) for delete<br>
 * and lets assume that you have just another extra endpoint just for updating person Address only:<br>
 * <b>http://www.some-site/person/address</b> (PUT) for update<br>
 *<br> So, There are 2 ways of using this class:
 * <ol>
 *     <li>
 *        	Just use an instance of this class, preset the {@link #connectionUrl} property (use method {@link #setConnectionUrl(String)})
 *  		to value "<code>http://www.some-site/person</code>", preset all the headers (such "Content-Type", and whatever other headers
 *  		required). - Use methods {@link #setContentType(String)} and {@link #setRequestHeader(String, String)}.
 *  		Once this is done use this instance for all invocations to the above APIs:<br>
 *  			
 *  		{@link #sendHttpRequest(HttpMethod)} or {@link #sendHttpRequest(HttpMethod, String)} would be good methods for the first 4 endpoints
 *  		and for the address endpoint use method {@link #sendHttpRequest(String, HttpMethod, String)} where the URL parameter
 *  		should be set to value {@link #getConnectionUrl()} + "/address"
 *  	</li>
 *  	<li>
 *  	 	Extend this class lets say with a Person class that will have the methods <code>get(Long id), update(...),
 *  	 	create(...), delete(Long id) and updateAddress(...)	</code>. In constructor for this class pre-set all the
 *  	 	relevant environment (headers, connection URL) and in the methods mentioned above invoke appropriate
 *  	 	<code>sendHttpRequest()</code> method. This way the code will look like<br><br>
 *  	 	<code>
 *  	 	  	Person person = new Person();<br>
 *  	 	  	person.create(person);<br>
 *  	 	  	person.updateAddress(...);<br>
 *  	 	</code><br>
 *  	 This will even hide away the fact that the operations are done over Http connection and the code just looks like
 *  	 regular java code
 *  	</li>
 * </ol>
 */
public class HttpClient {
	
	private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
	private Map<String, String> requestPropertiesMap = new HashMap<>();
    private String connectionUrl;
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
     * @return {@link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<String> sendHttpRequest(HttpMethod callMethod) throws HttpClientCommunicationException {
        return sendHttpRequest(getConnectionUrl(), callMethod, (String)null);
    }

    /**
     * This method sends HTTP request to specified URL and uses specified HTTP method. It returns response
     * that is expected to be textual such as a String. This method does not send any info through request body.
     * It may send parameters through URL. So this method fits perfectly for using HTTP GET method. If HTTP request
     * uses method POST, PUT or any other methods that allow passing info in the request body and there is some info
     * to be sent then use method {@link #sendHttpRequest(String, HttpMethod, String)}
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @return {@Link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<String> sendHttpRequest(String requestUrl, HttpMethod callMethod) throws HttpClientCommunicationException {
        return sendHttpRequest(requestUrl, callMethod, (String)null);
    }

    /**
     * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses 
     * specified HTTP method and sends data through request body. Obviously it is expected that user should set connectionUrl 
     * property by invoking method {@link #setConnectionUrl(String)} beforehand.      
     * It returns response that is expected to be textual such as a String. This method fits for using HTTP  methods
     * POST, PUT or any other methods that allow passing info in the request body and there is some info to be sent.
     * If you don't need to send any info as request body, consider using method
     * {@link #sendHttpRequest(HttpMethod)}
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return {@Link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<String> sendHttpRequest(HttpMethod callMethod, String data) throws HttpClientCommunicationException {
    	return sendHttpRequest(getConnectionUrl(), callMethod, data);
    }

	/**
	 * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses
	 * specified HTTP method and sends binary data through request body. Obviously it is expected that user should set connectionUrl
	 * property by invoking method {@link #setConnectionUrl(String)} beforehand.
	 * It returns response that is expected to be textual such as a String. This method fits for using HTTP  methods
	 * POST, PUT or any other methods that allow passing info in the request body and there is some binary info to be sent.
	 * This method is useful for uploading files or any other binary info
	 * If you don't need to send any info as request body, consider using method
	 * {@link #sendHttpRequest(HttpMethod)}
	 * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
	 * @param data ByteBuffer that holds the binary data to be sent as request body
	 * @return {@Link ResponseHolder} that holds response content and metadata from the URL
	 * @throws HttpClientCommunicationException
	 */
	public ResponseHolder<String> sendHttpRequest(HttpMethod callMethod, ByteBuffer data) throws HttpClientCommunicationException {
		return sendHttpRequest(getConnectionUrl(), callMethod, data);
	}
    /**
     * This method sends HTTP request to specified URL, uses specified HTTP method and sends data through request body.
     * It returns response that is expected to be textual such as a String. This method fits for using HTTP  methods
     * POST, PUT or any other methods that allow passing info in the request body and there is some info to be sent.
     * If you don't need to send any info as request body, consider using method
     * {@link #sendHttpRequest(String, HttpMethod)}
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return {@Link ResponseHolder} that holds response content and metadata from the URL
     * @throws IOException
     */
    public ResponseHolder<String> sendHttpRequest(String requestUrl, HttpMethod callMethod, String data) throws HttpClientCommunicationException {
		ResponseHolder<String> response = new ResponseHolder<>();
		HttpURLConnection connection = null;
		try {
			connection = sendRequest(requestUrl, callMethod, data);
			response.setResponseCode(connection.getResponseCode());
			response.setResponseMessage(connection.getResponseMessage());
			response.setResponseHeaders(connection.getHeaderFields());
			response.setResponseContent(readResponse(connection));
		} catch (IOException ioe) {
			String errorMessage = buildErrorMessage(response, ioe);
			throw new HttpClientCommunicationException(errorMessage, ioe, response);
		} finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
        return response;
    }

	/**
	 * This method sends HTTP request to specified URL, uses specified HTTP method and sends binary data through request body.
	 * It returns response that is expected to be textual such as a String. This method fits for using HTTP  methods
	 * POST, PUT or any other methods that allow passing info in the request body and there is some binary info to be sent.
	 * This method is useful for uploading files Image, Video, Audio or any other binary info
	 * If you don't need to send any info as request body, consider using method
	 * {@link #sendHttpRequest(String, HttpMethod)}
	 * @param requestUrl URL to which request is to be sent
	 * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
	 * @param data String that holds the data to be sent as request body
	 * @return {@Link ResponseHolder} that holds response content and metadata from the URL
	 * @throws HttpClientCommunicationException
	 */
	public ResponseHolder<String> sendHttpRequest(String requestUrl, HttpMethod callMethod, ByteBuffer data) throws HttpClientCommunicationException {
		ResponseHolder<String> response = new ResponseHolder<>();
		HttpURLConnection connection = null;
		try {
			connection = sendRequest(requestUrl, callMethod, data);
			response.setResponseCode(connection.getResponseCode());
			response.setResponseMessage(connection.getResponseMessage());
			response.setResponseHeaders(connection.getHeaderFields());
			response.setResponseContent(readResponse(connection));
		} catch (IOException ioe) {
			String errorMessage = buildErrorMessage(response, ioe);
			throw new HttpClientCommunicationException(errorMessage, ioe, response);
		} finally {
			connection.disconnect();
		}
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
     * @return {@link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<ByteBuffer> sendHttpRequestForBinaryResponse(HttpMethod callMethod) throws HttpClientCommunicationException {
        return sendHttpRequestForBinaryResponse(getConnectionUrl(), callMethod, (String)null);
    }

    /**
     * This method is the same as {@link #sendHttpRequest(String, HttpMethod)} except that it returns {@link ByteBuffer}
     * that holds binary info. So this methods fits for retrieving binary response such as Image, Video, Audio or any
     * other info in binary format.
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @return {@link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<ByteBuffer> sendHttpRequestForBinaryResponse(String requestUrl, HttpMethod callMethod) throws HttpClientCommunicationException {
        return sendHttpRequestForBinaryResponse(requestUrl, callMethod, (String)null);
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
     * @return {@link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<ByteBuffer> sendHttpRequestForBinaryResponse(HttpMethod callMethod, String data) throws HttpClientCommunicationException {
    	return sendHttpRequestForBinaryResponse(getConnectionUrl(),callMethod, data);
    }

	/**
	 * This method sends HTTP request to pre-set URL. It uses method {@link #getConnectionUrl()} to get the URL and uses
	 * specified HTTP method. Obviously it is expected that user should set connectionUrl property by invoking method
	 * {@link #setConnectionUrl(String)} beforehand.
	 * This method is the same as {@link #sendHttpRequest(HttpMethod, ByteBuffer)} except that it returns
	 * {@link ByteBuffer} that holds binary info. So this method fits for sending binary info such as Image,
	 * Video, Audio or any other info in binary format. and then retrieving binary response as well. So this method
	 * might be a good fit for consuming API that receives and returns  binary info possibly for modifying images or
	 * binary files such as adding a watermark to an Image for example
	 * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
	 * @param data ByteArray that holds some binary data to be sent as request body
	 * @return {@link ResponseHolder} that holds response content and metadata from the URL
	 * @throws HttpClientCommunicationException
	 */
	public ResponseHolder<ByteBuffer> sendHttpRequestForBinaryResponse(HttpMethod callMethod, ByteBuffer data) throws HttpClientCommunicationException {
		return sendHttpRequestForBinaryResponse(getConnectionUrl(),callMethod, data);
	}
    /**
     * This method is the same as {@link #sendHttpRequest(String, HttpMethod, String)} except that it returns
     * {@link ByteBuffer} that holds binary info. So this methods fits for retrieving binary response such as Image,
     * Video, Audio or any other info in binary format.
     * @param requestUrl URL to which request is to be sent
     * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
     * @param data String that holds the data to be sent as request body
     * @return {@link ResponseHolder} that holds response content and metadata from the URL
     * @throws HttpClientCommunicationException
     */
    public ResponseHolder<ByteBuffer> sendHttpRequestForBinaryResponse(String requestUrl, HttpMethod callMethod, String data) throws HttpClientCommunicationException {
		ResponseHolder<ByteBuffer> response = new ResponseHolder<>();
		HttpURLConnection connection = null;
		try {
			connection = sendRequest(requestUrl, callMethod, data);
			response.setResponseCode(connection.getResponseCode());
			response.setResponseMessage(connection.getResponseMessage());
			response.setResponseHeaders(connection.getHeaderFields());
			response.setResponseContent(readBinaryResponse(connection));
		} catch (IOException ioe) {
			String errorMessage = buildErrorMessage(response, ioe);
			throw new HttpClientCommunicationException(errorMessage, ioe, response);
		} finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
		return response;
    }

	/**
	 * This method is the same as {@link #sendHttpRequest(String, HttpMethod, ByteBuffer)} except that it returns
	 * {@link ByteBuffer} that holds binary info. So this method fits for sending binary info such as Image,
	 * Video, Audio or any other info in binary format. and then retrieving binary response as well. So this method
	 * might be a good fit for consuming API that receives and returns  binary info possibly for modifying images or
	 * binary files such as adding a watermark to an Image for example
	 * @param requestUrl URL to which request is to be sent
	 * @param callMethod {@link HttpMethod} that specifies which HTTP method is to be used
	 * @param data ByteBuffer that holds the data to be sent as request body
	 * @return {@link ResponseHolder} that holds response content and metadata from the URL
	 * @throws HttpClientCommunicationException
	 */
	public ResponseHolder<ByteBuffer> sendHttpRequestForBinaryResponse(String requestUrl, HttpMethod callMethod, ByteBuffer data) throws HttpClientCommunicationException {
		ResponseHolder<ByteBuffer> response = new ResponseHolder<>();
		HttpURLConnection connection = null;
		try {
			connection = sendRequest(requestUrl, callMethod, data);
			response.setResponseCode(connection.getResponseCode());
			response.setResponseMessage(connection.getResponseMessage());
			response.setResponseHeaders(connection.getHeaderFields());
			response.setResponseContent(readBinaryResponse(connection));
		} catch (IOException ioe) {
			String errorMessage = buildErrorMessage(response, ioe);
			throw new HttpClientCommunicationException(errorMessage, ioe, response);
		} finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
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
     * This method is a general request header setter. This method is deprecated due to wrong name.
     * Use method {@link #setRequestHeader(String, String)} instead
     * @param headerName if the value of header name is blank or null this method does nothing
     * @param headerValue Holds the value for the header
	 * @return instance of this HttpClient to support chaining
     * @See #setRequestHeader(String, String)
     */
    @Deprecated
    public HttpClient setRequestProperty(String headerName, String headerValue) {
    	return setRequestHeader(headerName, headerValue);
    }
    
    /**
     * This method is a general request header setter.
     * @param headerName if the value of header name is blank or null this method does nothing
     * @param headerValue headerValue Holds the value for the header
	 * @return instance of this HttpClient to support chaining
     */
    public HttpClient setRequestHeader(String headerName, String headerValue) {
    	if(StringUtils.isNotBlank(headerName)) {
    		requestPropertiesMap.put(headerName, headerValue);
    	}
		return this;
    }

    /**
     * This method removes request property
     * @param propertyName if the value of propertyName is blank or null this method does nothing
	 * @return instance of this HttpClient to support chaining
     */
    public HttpClient removeRequestProperty(String propertyName) {
    	String result = null;
    	if(StringUtils.isNotBlank(propertyName)) {
    		result = requestPropertiesMap.remove(propertyName);
    	}
    	return this;
    }
    
    /**
     * This method clears all request properties.
	 * @return instance of this HttpClient to support chaining
	 */
    public HttpClient clearAllRequestProperties() {
    	requestPropertiesMap.clear();
		return this;
    }

    /**
     * This is setter method for content type property. This method intended for use in extending classes
     * @param contentType holds content type value
	 * @return instance of this HttpClient to support chaining
	 */
    public HttpClient setContentType(String contentType) {
    	requestPropertiesMap.put(CONTENT_TYPE_HEADER_KEY, contentType);
		return this;
    }

	/**
	 * This method returns connection timeout currently in effect. The default value (if timeout was never set) is 0 which means 
	 * that the option is disabled (i.e., timeout of infinity). The timeout value remains in effect for all the following
	 * connection requests until changed
	 * @return returns current timeout value in {@link TimeInterval}
	 */
	public TimeInterval getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * This method sets the connection timeout that will remain in effect for all subsequent connection requests until it is
	 * changed by invocation of this or other connection timeout setter methods: {@link #setConnectTimeout(String)}, 
	 * {@link #setConnectTimeout(long, TimeUnit)}
	 * @param connectTimeout
	 */
	public void setConnectTimeout(TimeInterval connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * This is convenience method that allows to set connection timeout from a string that is parsed to {@link TimeInterval}.
	 * @param timeIntervalStr that represents time interval. (See {@link TextUtils#parseStringToTimeInterval(String)} to see
	 * what is expected string format) 
	 * @throws IllegalArgumentException if string parameter could not be parsed to {@link TimeInterval}
	 */
	public void setConnectTimeout(String timeIntervalStr) throws IllegalArgumentException {
		setConnectTimeout(TextUtils.parseStringToTimeInterval(timeIntervalStr));
	}
	
	/**
	 * This is convenience method that allows to set connection timeout from timeValue and TimeUnit parameters that are used
	 * to instantiate   {@link TimeInterval}
	 * @param timeValue numeric time value
	 * @param timeUnit time unit to interpret time value unit
	 * @throws IllegalArgumentException if timeValue is negative or TimeUnit is null
	 */
	public void setConnectTimeout(long timeValue, TimeUnit timeUnit) throws IllegalArgumentException {
		if(timeValue < 0) {
			throw new IllegalArgumentException("Negative value for timeout");
		}
		if(timeUnit == null) {
			throw new IllegalArgumentException("Null Time Unit value for timeout");
		}
		setConnectTimeout(new TimeInterval(timeValue, timeUnit));
	}

	/**
	 * This method returns read timeout currently in effect. The default value (if timeout was never set) is 0 which means 
	 * that the option is disabled (i.e., timeout of infinity). The timeout value remains in effect for all the following
	 * connection requests until changed
	 * @return returns current timeout value in {@link TimeInterval}
	 */
	public TimeInterval getReadTimeout() {
		return readTimeout;
	}

	/**
	 * This method sets the read timeout that will remain in effect for all subsequent connection requests until it is
	 * changed by invocation of this or other read timeout setter methods: {@link #setReadTimeout(String)}, 
	 * {@link #setReadTimeout(long, TimeUnit)}
	 * @param readTimeout
	 */
	public void setReadTimeout(TimeInterval readTimeout) {
		this.readTimeout = readTimeout;
	}


	/**
	 * This is convenience method that allows to set read timeout from a string that is parsed to {@link TimeInterval}.
	 * @param timeIntervalStr that represents time interval. (See {@link TextUtils#parseStringToTimeInterval(String)} to see
	 * what is expected string format) 
	 * @throws IllegalArgumentException if string parameter could not be parsed to {@link TimeInterval}
	 */
	public void setReadTimeout(String timeIntervalStr) throws IllegalArgumentException {
		setReadTimeout(TextUtils.parseStringToTimeInterval(timeIntervalStr));
	}
	
	/**
	 * This is convenience method that allows to set read timeout from timeValue and TimeUnit parameters that are used
	 * to instantiate   {@link TimeInterval}
	 * @param timeValue numeric time value
	 * @param timeUnit time unit to interpret time value unit
	 * @throws IllegalArgumentException if timeValue is negative or TimeUnit is null
	 */
	public void setReadTimeout(long timeValue, TimeUnit timeUnit) throws IllegalArgumentException {
		if(timeValue < 0) {
			throw new IllegalArgumentException("Negative value for timeout");
		}
		if(timeUnit == null) {
			throw new IllegalArgumentException("Null Time Unit value for timeout");
		}
		setReadTimeout(new TimeInterval(timeValue, timeUnit));
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

    /**
     * This method reads response from {@link HttpURLConnection} expecting response to be in Textual format
     * @param connection {@link HttpURLConnection} from which to read the response
     * @return String that holds the response
     * @throws IOException
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
    	String response = "";
    	try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
    		response = doReadResponse(br);
    	} catch(IOException ioe) {
    		InputStream errorInputStream = connection.getErrorStream();
    		if(errorInputStream == null) {
    			throw ioe;
    		} else {
    			BufferedReader br = new BufferedReader(new InputStreamReader(errorInputStream, StandardCharsets.UTF_8));
        		response = doReadResponse(br);
    		}
    	}
    	return response;
    }

    /**
     * This method reads response from {@link BufferedReader} that wraps an {@link InputStream} obtained from the server.
     * @param br {@link BufferedReader} that wraps an {@link InputStream} obtained from the server
     * @return read content as a {@link String}
     * @throws IOException
     */
	private String doReadResponse(BufferedReader br) throws IOException {
		String response;
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		response = sb.toString();
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
		ByteBuffer dataBuffer = (StringUtils.isNotBlank(data)) ? ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)) : null;
		return sendRequest(url, method, dataBuffer);
    }

	private HttpURLConnection sendRequest(String url, HttpMethod method, ByteBuffer data) throws IOException {
		boolean doOutput = (data != null && data.hasArray());
		HttpURLConnection httpURLConnection = openHttpUrlConnection(url, method, doOutput);
		if (doOutput) {
			try (DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
				dataOutputStream.write(data.array());
				dataOutputStream.flush();
			}
		}
		return httpURLConnection;
	}

	private HttpURLConnection setConnectionTimeout(HttpURLConnection connection) {
		int connectionTimeoutMs = Long.valueOf((getConnectTimeout().toMillis())).intValue();
        if(connectionTimeoutMs >= 0L) {
        	connection.setConnectTimeout(connectionTimeoutMs);
        }
        return connection;
	}

	private HttpURLConnection setReadTimeout(HttpURLConnection connection) {
		int readTimeoutMs = Long.valueOf((getReadTimeout().toMillis())).intValue();
        if(readTimeoutMs >= 0L) {
        	connection.setReadTimeout(readTimeoutMs);
        }
        return connection;
	}

	private String buildErrorMessage(ResponseHolder<?> response, Exception e) {
		StringBuilder stringBuilder = new StringBuilder();
		if(response.getResponseCode() > 0) {
			stringBuilder.append("HTTP ")
					.append(response.getResponseCode())
					.append(" ");
		}
		if(StringUtils.isNotBlank(response.getResponseMessage())) {
			stringBuilder.append(response.getResponseMessage())
					.append("\n");
		}
		stringBuilder.append(e.getMessage());
		return stringBuilder.toString();
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
