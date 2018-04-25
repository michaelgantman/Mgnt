package com.mgnt.lifecycle.management;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseEntityFactory<I> {
    private Map<String, I> entityHolder = new HashMap<>();

    void putEntity(String key, I bean) {
        entityHolder.put(key, bean);
    }

    public I getEntity(String key) {
        return entityHolder.get(key);
    }
    public Collection<I> getAllEntities() {
        return entityHolder.values();
    }
}
