package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.utils.entities.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BackgroundThreadsRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(BackgroundThreadsRunner.class);
    private ScheduledExecutorService executorService;

    public BackgroundThreadsRunner() {
    }

    public BackgroundThreadsRunner(boolean isSelfInitializing) {
        if(isSelfInitializing) {
            init();
        }
    }

    @PostConstruct
    private void init() {
        Collection<BackgroundRunnable> taskCollection = BackgroundRunnableFactory.getAllInstances();
        executorService = Executors.newScheduledThreadPool(taskCollection.size());
        for(BackgroundRunnable task : taskCollection) {
            TimeInterval taskExecutionInterval = task.getTaskExecutionInterval();
            executorService.scheduleAtFixedRate(task, taskExecutionInterval.getValue(),
                    taskExecutionInterval.getValue(), taskExecutionInterval.getTimeUnit());
        }
    }

    @PreDestroy
    private void cleanup() {
        LOGGER.info("Shutting down background tasks thread pool");
        if(executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
