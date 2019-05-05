package com.mgnt.lifecycle.management.example;

import com.mgnt.lifecycle.management.BaseEntityFactory;
import java.util.Collection;

/*
 * This is the factory class, and it should look similar for any implementation
 */
public class InfoFormatterFactory extends BaseEntityFactory<InfoFormatter> {
    private static InfoFormatterFactory FACTORY = new InfoFormatterFactory();

    private InfoFormatterFactory() {
    }


    /*
     * This method is not for external use, It is used by the abstract parent class to get the factory to register
     * it in the infrastructure
     */
    public static InfoFormatterFactory getFactoryInstance() {
        return FACTORY;
    }

    /*
     * This is the method to access concrete implementation by its name from the factory
     */
    public static InfoFormatter getInstance(String key) {
        return FACTORY.getEntity(key);
    }

    /*
     * This is convenience method that allows you to get all the available implementations from this factory
     */
    public static Collection<InfoFormatter> getAllInstances() {
        return FACTORY.getAllEntities();
    }
}
