package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.lifecycle.management.BaseEntityFactory;

import java.util.Collection;

public class BackgroundRunnableFactory  extends BaseEntityFactory<BackgroundRunnable> {
    private static BackgroundRunnableFactory FACTORY = new BackgroundRunnableFactory();

    private BackgroundRunnableFactory() {
    }

    public static BackgroundRunnableFactory getFactoryInstance() {
        return FACTORY;
    }

    public static BackgroundRunnable getInstance(String key) {
        return FACTORY.getEntity(key);
    }

    public static Collection<BackgroundRunnable> getAllInstances() {
        return FACTORY.getAllEntities();
    }
}
