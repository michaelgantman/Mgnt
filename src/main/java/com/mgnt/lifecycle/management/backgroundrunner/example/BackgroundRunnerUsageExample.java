package com.mgnt.lifecycle.management.backgroundrunner.example;

import com.mgnt.lifecycle.management.backgroundrunner.BackgroundThreadsRunner;

public class BackgroundRunnerUsageExample {
    static {
        /*
         * This static block performs what Spring framework would have done for us if this infrastructure would have
         * been used within Spring framework context. Method init initializes our "beans" in particular order
         */
        init();
    }

    private static void init() {
        new TypeOneTask();
        new TypeTwoTask();
        new BackgroundThreadsRunner(true);
    }

    /*
     * this is just an empty method that allows us to run this example app. The real action occurs in the background
     * threads in which classes TypeOneTask and TypeTwoTask are periodically executed at intervals that are configurable
     */
    public static void main(String[] args) {
    }
}
