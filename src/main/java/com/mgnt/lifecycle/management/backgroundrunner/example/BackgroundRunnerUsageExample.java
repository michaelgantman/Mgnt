package com.mgnt.lifecycle.management.backgroundrunner.example;

import com.mgnt.lifecycle.management.backgroundrunner.BackgroundThreadsRunner;

public class BackgroundRunnerUsageExample {
    static {
        init();
    }

    private static void init() {
        new TypeOneTask();
        new TypeTwoTask();
        new BackgroundThreadsRunner(true);
    }

    public static void main(String[] args) {
    }
}
