package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.BaseEntityFactory;

import java.util.Collection;

public class FormattedMessageFactory extends BaseEntityFactory<FormattedMessage> {
    private static FormattedMessageFactory FACTORY = new FormattedMessageFactory();

    private FormattedMessageFactory() {
    }


    public static FormattedMessageFactory getFactoryInstance() {
        return FACTORY;
    }

    public static FormattedMessage getInstance(String key) {
        return FACTORY.getEntity(key);
    }

    public static Collection<FormattedMessage> getAllInstances() {
        return FACTORY.getAllEntities();
    }
}
