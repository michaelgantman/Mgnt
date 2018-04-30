package com.mgnt.lifecycle.management;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the parent factory class for all the factories. See the javadoc for the {@link com.mgnt.lifecycle.management}
 * and source code in package {@code com.mgnt.lifecycle.management.example} for detailed explanation on how to use this class
 * @param <I>
 */
public abstract class BaseEntityFactory<I> {
    private Map<String, I> entityHolder = new HashMap<>();

    /**
     * This method is for internal use by the infrastructure. It is used to place any newly instantiated class
     * into the factory as part of new entity instantiation
     * @param key
     * @param bean
     */
    void putEntity(String key, I bean) {
        entityHolder.put(key, bean);
    }

    /**
     * this method is for retrieval of an Instance of concrete implementation from the factory
     * @param key
     * @return
     */
    public I getEntity(String key) {
        return entityHolder.get(key);
    }

    /**
     * This method returns all the implementations
     * @return a Collection that contains all the implementations held in this factory
     */
    public Collection<I> getAllEntities() {
        return entityHolder.values();
    }
}
