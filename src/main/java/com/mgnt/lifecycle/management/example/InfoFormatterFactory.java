package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.BaseEntityFactory;
import java.util.Collection;

public class InfoFormatterFactory extends BaseEntityFactory<InfoFormatter> {
    private static InfoFormatterFactory FACTORY = new InfoFormatterFactory();

    private InfoFormatterFactory() {
    }


    public static InfoFormatterFactory getFactoryInstance() {
        return FACTORY;
    }

    public static InfoFormatter getInstance(String key) {
        return FACTORY.getEntity(key);
    }

    public static Collection<InfoFormatter> getAllInstances() {
        return FACTORY.getAllEntities();
    }
}
