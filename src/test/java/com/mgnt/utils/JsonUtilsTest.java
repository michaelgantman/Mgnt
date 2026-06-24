package com.mgnt.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    static class Person {
        private String name;
        private int age;

        public Person() {}

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    static class WithOptionalField {
        private String required;
        private String optional;

        public WithOptionalField() {}
        public WithOptionalField(String required, String optional) {
            this.required = required;
            this.optional = optional;
        }

        public String getRequired() { return required; }
        public void setRequired(String required) { this.required = required; }
        public String getOptional() { return optional; }
        public void setOptional(String optional) { this.optional = optional; }
    }

    @Test
    void writeAndReadRoundTrip() throws IOException {
        Person original = new Person("Alice", 30);
        String json = JsonUtils.writeObjectToJsonString(original);
        assertNotNull(json);
        assertTrue(json.contains("\"name\""));
        assertTrue(json.contains("Alice"));

        Person result = JsonUtils.readObjectFromJsonString(json, Person.class);
        assertEquals("Alice", result.getName());
        assertEquals(30, result.getAge());
    }

    @Test
    void writeNullReturnsNull() throws JsonProcessingException {
        assertNull(JsonUtils.writeObjectToJsonString(null));
    }

    @Test
    void writeNullWithDefaultTypingReturnsNull() throws JsonProcessingException {
        assertNull(JsonUtils.writeObjectToJsonString(null, true));
    }

    @Test
    void writeMapProducesExpectedJson() throws JsonProcessingException {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("key", "value");
        assertEquals("{\"key\":\"value\"}", JsonUtils.writeObjectToJsonString(map));
    }

    @Test
    void writeNullFieldExcluded() throws JsonProcessingException {
        WithOptionalField obj = new WithOptionalField("present", null);
        String json = JsonUtils.writeObjectToJsonString(obj);
        assertTrue(json.contains("\"required\""));
        assertFalse(json.contains("\"optional\""));
    }

    @Test
    void readInvalidJsonThrows() {
        assertThrows(IOException.class,
                () -> JsonUtils.readObjectFromJsonString("not-json", Person.class));
    }

    @Test
    void writeListProducesJsonArray() throws JsonProcessingException {
        List<String> list = Arrays.asList("a", "b", "c");
        String json = JsonUtils.writeObjectToJsonString(list);
        assertEquals("[\"a\",\"b\",\"c\"]", json);
    }

    @Test
    void readEmptyObjectJsonProducesDefaultValues() throws IOException {
        Person result = JsonUtils.readObjectFromJsonString("{}", Person.class);
        assertNull(result.getName());
        assertEquals(0, result.getAge());
    }

    @Test
    void writeIntegerProducesJsonNumber() throws JsonProcessingException {
        String json = JsonUtils.writeObjectToJsonString(42);
        assertEquals("42", json);
    }

    @Test
    void readWithDefaultTypingRoundTrip() throws IOException {
        Person original = new Person("Bob", 25);
        String json = JsonUtils.writeObjectToJsonString(original, false);
        Person result = JsonUtils.readObjectFromJsonString(json, Person.class, false);
        assertEquals("Bob", result.getName());
        assertEquals(25, result.getAge());
    }
}
