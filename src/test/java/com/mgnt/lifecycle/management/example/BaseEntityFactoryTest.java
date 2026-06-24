package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter;
import com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class BaseEntityFactoryTest {

    @BeforeAll
    static void registerImplementations() {
        // Instantiating the concrete classes triggers their constructors which call
        // BaseEntity(factoryKey, customName), registering them in the factory.
        // The static block in BaseInfoFormatter ensures the factory itself is set up first.
        new JsonInfoFormatter();
        new XmlInfoFormatter();
    }

    @Test
    void factoryRetrievesJsonFormatter() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("JSON");
        assertNotNull(formatter);
        assertInstanceOf(JsonInfoFormatter.class, formatter);
    }

    @Test
    void factoryRetrievesXmlFormatter() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("XML");
        assertNotNull(formatter);
        assertInstanceOf(XmlInfoFormatter.class, formatter);
    }

    @Test
    void factoryUnknownKeyReturnsNull() {
        assertNull(InfoFormatterFactory.getInstance("UNKNOWN"));
    }

    @Test
    void jsonFormatterFormatsMessage() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("JSON");
        String result = formatter.formatMessage("hello");
        assertNotNull(result);
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("{"));
    }

    @Test
    void xmlFormatterFormatsMessage() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("XML");
        String result = formatter.formatMessage("hello");
        assertNotNull(result);
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("<"));
    }

    @Test
    void formatMessageNullContentReturnsNull() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("JSON");
        assertNull(formatter.formatMessage(null));
    }

    @Test
    void formatMessageEmptyContentReturnsNull() {
        InfoFormatter formatter = InfoFormatterFactory.getInstance("JSON");
        assertNull(formatter.formatMessage(""));
    }

    @Test
    void getAllInstancesContainsBothFormatters() {
        Collection<InfoFormatter> all = InfoFormatterFactory.getAllInstances();
        assertNotNull(all);
        assertTrue(all.size() >= 2);
    }
}
