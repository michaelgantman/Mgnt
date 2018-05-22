package com.mgnt.lifecycle.management.backgroundrunner;

import com.mgnt.utils.entities.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class is part of the framework and probably will not be accessed externally. However if you use this infrastructure
 * within Spring framework you will need to declare this class as bean in your xml configuration. The the explanation
 * at the end of package description {@link com.mgnt.lifecycle.management.backgroundrunner}
 */
public class BackgroundThreadsRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(BackgroundThreadsRunner.class);
    private ScheduledExecutorService executorService;

    public BackgroundThreadsRunner() {
    }

    /**
     * This constructor is not really needed if this infrastructure is used within Spring. Because Spring will
     * initialaze this class at startup using the default constructor provided that this class is defined as bean.
     * But this constructor had to be added for example provided in package
     * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b>. Since source code in this example does not
     * rely on Spring it has to initialize this class by itself using this constructor. However, if this infrastructure
     * is used in some other then Spring environment which has no way of initializing this class then this constructor
     * may be used
     * @param isSelfInitializing
     */
    public BackgroundThreadsRunner(boolean isSelfInitializing) {
        if(isSelfInitializing) {
            init();
        }
    }

    /**
     * This is the method that initiates the Threadpool and starts the periodic tasks executions. This is the heart
     * of this infrastructure. So something must invoke it. In case of Spring framework annotation {@link PostConstruct}
     * takes care of this. In our non-Spring example in package
     * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b> it is invoked through invocation of
     * constructor {@link #BackgroundThreadsRunner(boolean)} that invokes this method explicitly
     */
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

    /**
     * THis is a cleanup method that shold be invoked when the application using this infrastructure shuts down.
     * Again for Spring annotation {@link PreDestroy} takes care of this. If you work in environment that annotation
     * {@link PreDestroy} has no effect you have to worry about making shure of invoking this method, otherwise your Tasks
     * may continue running using your resources and memory behind the sciences. (Note that source code in package
     * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b> does not invoke this method... Oops!)
     */
    @PreDestroy
    private void cleanup() {
        LOGGER.info("Shutting down background tasks thread pool");
        if(executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
