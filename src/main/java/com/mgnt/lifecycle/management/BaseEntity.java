package com.mgnt.lifecycle.management;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseEntity<I> {
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseEntity.class.getName());
    private static Map<String, BaseEntityFactory> FACTORY_MAP = new HashMap<>();

    protected static <I>void init(String type, BaseEntityFactory<I> factory) {
        FACTORY_MAP.put(type, factory);
    }

    public BaseEntity(String factoryKey) {
        this(factoryKey, null);
    }

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
