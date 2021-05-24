package com.mgnt.utils;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {

	private static final ObjectReader enabledDefaultTypingObjectReader;
	private static final ObjectWriter enabledDefaultTypingObjectWriter;
	private static final ObjectReader disabledDefaultTypingObjectReader;
	private static final ObjectWriter disabledDefaultTypingObjectWriter;
	
	static {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModules(new JavaTimeModule());
		disabledDefaultTypingObjectReader = objectMapper.reader();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		disabledDefaultTypingObjectWriter = objectMapper.writer();

		objectMapper = new ObjectMapper();
		objectMapper.registerModules(new JavaTimeModule());
		objectMapper.activateDefaultTyping(new DefaultBaseTypeLimitingValidator());
		enabledDefaultTypingObjectReader = objectMapper.reader();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		enabledDefaultTypingObjectWriter = objectMapper.writer();
	}

	public static String writeObjectToJsonString(Object object, boolean enableDefaultTyping) throws JsonProcessingException {
		String jsonData = null;
		if (object != null) {
			ObjectWriter objectWriter = (enableDefaultTyping) ? enabledDefaultTypingObjectWriter : disabledDefaultTypingObjectWriter;
			jsonData = objectWriter.writeValueAsString(object);
		}
		return jsonData;
	}

	public static String writeObjectToJsonString(Object object) throws JsonProcessingException {
		return writeObjectToJsonString(object, false);
	}
	
	public static <T> T readObjectFromJsonString(String s, Class<T> type, boolean enableDefaultTyping) throws IOException {
		T data;
		ObjectReader objectReader = (enableDefaultTyping) ? enabledDefaultTypingObjectReader : disabledDefaultTypingObjectReader;
		data = objectReader.forType(type).readValue(s);
		return data;
	}
	
	public static <T> T readObjectFromJsonString(String s, Class<T> type) throws IOException {
		return readObjectFromJsonString(s, type, false);
	}
}
