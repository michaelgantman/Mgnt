package com.mgnt.utils;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * This class provides basic JSON parsing (serialization/deserialization) functionality. It allows any class serialization
 * (or List/Set/Map that contains any class) into a valid JSON string and to deserialize any JSON String back into any class.
 * However, it doesn't work well with deserializing List/Set/Map that contains some classes and not just primitives. Note
 * that this class uses JSON-JACKSON library and is making some operations simpler. However, the price for simplicity is that
 * it can not handle properly deserialization of data structures. For that you will need to to work directly with JSON-JACKSON
 * library (or any other JSON library). Or as an alternative you can deserialize it into a Map&lt;String, Object&gt; or 
 * List&lt;Object&gt; and than deserialize each Object separately.
 *
 * @author Michael Gantman
 */
public class JsonUtils {

	private static final ObjectReader enabledDefaultTypingObjectReader;
	private static final ObjectWriter enabledDefaultTypingObjectWriter;
	private static final ObjectReader disabledDefaultTypingObjectReader;
	private static final ObjectWriter disabledDefaultTypingObjectWriter;
	
	static {
		ObjectMapper objectMapper = JsonMapper.builder().build();
		objectMapper.registerModules(new JavaTimeModule());
		disabledDefaultTypingObjectReader = objectMapper.reader();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		disabledDefaultTypingObjectWriter = objectMapper.writer();

		PolymorphicTypeValidator ptv = new DefaultBaseTypeLimitingValidator();
		objectMapper =  JsonMapper.builder().activateDefaultTyping(ptv).build();
		objectMapper.registerModules(new JavaTimeModule());
		enabledDefaultTypingObjectReader = objectMapper.reader();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		enabledDefaultTypingObjectWriter = objectMapper.writer();
	}

	/**
	 * This method writes (serializes) an Object into JSON String with option to allow or disallow default typing.
	 * If Default typing is enabled then the type validator used is class 
	 * {@code com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator}.
	 * If you are not familiar with default typing concept in JASON-JACKSON library than simply use helper method
	 * {@link #writeObjectToJsonString(Object)}. It will do the job in most cases
	 * @see {@link #writeObjectToJsonString(Object)}
	 * @param object an Object to be deserialized
	 * @param enableDefaultTyping boolean flag that determines if default typing is allowed
	 * @return valid JSON string that is the result of the Object deserialization
	 * @throws JsonProcessingException
	 */
	public static String writeObjectToJsonString(Object object, boolean enableDefaultTyping) throws JsonProcessingException {
		String jsonData = null;
		if (object != null) {
			ObjectWriter objectWriter = (enableDefaultTyping) ? enabledDefaultTypingObjectWriter : disabledDefaultTypingObjectWriter;
			jsonData = objectWriter.writeValueAsString(object);
		}
		return jsonData;
	}

	/**
	 * This is a helper method that invokes {@link #writeObjectToJsonString(Object, boolean)} with the second parameter set to 
	 * {@code false}. In most cases (when you don't need to worry about default typing) this method should be used instead of 
	 * invoking method {@link #writeObjectToJsonString(Object, boolean)}
	 * @param object an Object to be deserialized
	 * @return valid JSON string that is the result of the Object deserialization
	 * @throws JsonProcessingException
	 */
	public static String writeObjectToJsonString(Object object) throws JsonProcessingException {
		return writeObjectToJsonString(object, false);
	}
	
	/**
	 * This method reads (deserializes) JSON string into specific Object type with option to allow or disallow default typing.
	 * If Default typing is enabled then the type validator used is class 
	 * {@code com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator}.
	 * If you are not familiar with default typing concept in JASON-JACKSON library than simply use helper method
	 * {@link #readObjectFromJsonString(String, Class)}. It will do the job in most cases
	 * <p>
	 * Just a short example: Assume that there is a class {@code Person} and an instance of this class was serialized to JSON
	 * String. And now there is a need to deserialize it back. So the code to do so would look like this: <br><br>
	 * {@code Person person = JSONUtils.readObjectFromJsonString(jsonString, Person.class, false);}
	 * </p>
	 * @see {@link #readObjectFromJsonString(String, Class)}
	 * @param <T> A class instance of which will be returned if deserialization succeeds.
	 * @param jsonString JSON string to be deserialized
	 * @param type deserialization target class
	 * @param enableDefaultTyping boolean flag that determines if default typing is allowed
	 * @return instance of class T deserialized from JSON string
	 * @throws IOException
	 */
	public static <T> T readObjectFromJsonString(String jsonString, Class<T> type, boolean enableDefaultTyping) throws IOException {
		T data;
		ObjectReader objectReader = (enableDefaultTyping) ? enabledDefaultTypingObjectReader : disabledDefaultTypingObjectReader;
		data = objectReader.forType(type).readValue(jsonString);
		return data;
	}
	
	/**
	 * This is a helper method that invokes {@link #readObjectFromJsonString(String, Class, boolean)} with the second parameter 
	 * set to {@code false}. In most cases (when you don't need to worry about default typing) this method should be used instead 
	 * of invoking method {@link #readObjectFromJsonString(String, Class, boolean)}
	 * <p>
	 * Just a short example: Assume that there is a class {@code Person} and an instance of this class was serialized to JSON
	 * String. And now there is a need to deserialize it back. So the code to do so would look like this: <br><br>
	 * {@code Person person = JSONUtils.readObjectFromJsonString(jsonString, Person.class);}
	 * </p>
	 * @param <T> A class instance of which will be returned if deserialization succeeds.
	 * @param jsonString JSON string to be deserialized
	 * @param type deserialization target class
	 * @return instance of class T deserialized from JSON string
	 * @throws IOException
	 */
	public static <T> T readObjectFromJsonString(String jsonString, Class<T> type) throws IOException {
		return readObjectFromJsonString(jsonString, type, false);
	}
}
