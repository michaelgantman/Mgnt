package com.mgnt.lifecycle.management;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the base class for any set of classes that self-insert themselves into a Factory upon instantiation
 * The way it works that a user will need to create an Interface and an abstract class implementing this Interface.
 * The abstract class should also extend this class. Also a Factory that will hold the instances of that Interface
 * needs to be created. The factory should extend {@link BaseEntityFactory}. After that user may create any number of
 * classes that extend the abstract class. Each of these concrete implementations will self-insert itself into its
 * factory. See the javadoc for the {@link com.mgnt.lifecycle.management} package and source code in package
 * {@code com.mgnt.lifecycle.management.example} for detailed explanation on how to use this class
 * @param <I>
 */
public abstract class BaseEntity<I> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseEntity.class.getName());
    private static final Map<String, BaseEntityFactory> FACTORY_MAP = new HashMap<>();

    /**
     * This method puts the relevant factory into internal Factory map. This method MUST be explicitly
     * called by static initializer block in one of the implementations of the interface. It is recommended
     * to place this initializer into abstract parent of all the implementations of the Interface
     * @param type
     * @param factory
     * @param <I>
     */
    protected static <I>void init(String type, BaseEntityFactory<I> factory) {
        FACTORY_MAP.put(type, factory);
    }

    /**
     * Constructor that places the instance into the factory with default key value which is the class name
     * @param factoryKey the key to extract corresponding factory from factory map
     */
    public BaseEntity(String factoryKey) {
        this(factoryKey, null);
    }

    /**
     * This constructor is responsible for inserting instance of any class that extends this class directly or indirectly
     * into corresponding factory
     * @param factoryKey the key to extract corresponding factory from factory map
     * @param key The key to extract an an instance of a class that extends this class from a factory. If null then it is
     *            assumed that the class name is the key
     */
    public BaseEntity(String factoryKey, String key) {
        BaseEntityFactory<I> factory = FACTORY_MAP.get(factoryKey);
        if(factory != null) {
            if(StringUtils.isBlank(key)) {
                key = this.getClass().getSimpleName();
            }
            factory.putEntity(key, (I) this);
        } else {
            LOGGER.error("Factory for type {} was not found", factoryKey);
        }
    }
}
