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
    private ScheduledExecutorService executorService = null;
    private int poolSize = 0;

    /**
     * This constructor creates an instance of this class with default pool size which is the number of tasks submitted to this 
     * BackgroundThreadsRunner. This might be fine for a small number of tasks but may be very resource-wise inefficient for 
     * larger number of tasks. If predefined pool size is needed use constructor {@link #BackgroundThreadsRunner(int)}
     */
    public BackgroundThreadsRunner() {
    }

    /**
     * This constructor creates an instance of this class with predefined pool size rather then default one. If the pool size
     * is set to 0 or negative value or greater then the number of tasks submitted to this 
     * BackgroundThreadsRunner then the pool size will default to the number of tasks submitted to this 
     * BackgroundThreadsRunner. This might be fine for a small number of tasks but may be very resource-wise inefficient for 
     * larger number of tasks.
     * @param poolSize pool size of the thread pool
     */
    public BackgroundThreadsRunner(int poolSize) {
    	setPoolSize(poolSize);
    }

    /**
     * This constructor is not really needed if this infrastructure is used within Spring. Because Spring will
     * initialize this class at startup using the default constructor {@link #BackgroundThreadsRunner()} or 
     * {@link #BackgroundThreadsRunner(int)} provided that this class is defined as bean.
     * But this constructor had to be added for example provided in package
     * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b>. Since source code in this example does not
     * rely on Spring it has to initialize this class by itself using this constructor. However, if this infrastructure
     * is used in some other then Spring environment which has no way of initializing this class then this constructor
     * may be used. This constructor corresponds to {@link #BackgroundThreadsRunner()}
     * @param isSelfInitializing
     */
    public BackgroundThreadsRunner(boolean isSelfInitializing) {
        if(isSelfInitializing) {
            init();
        }
    }
    
    /**
     * This constructor is not really needed if this infrastructure is used within Spring. Because Spring will
     * initialize this class at startup using the default constructor {@link #BackgroundThreadsRunner()} or 
     * {@link #BackgroundThreadsRunner(int)} provided that this class is defined as bean.
     * But this constructor had to be added for example provided in package
     * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b>. Since source code in this example does not
     * rely on Spring it has to initialize this class by itself using this constructor. However, if this infrastructure
     * is used in some other then Spring environment which has no way of initializing this class then this constructor
     * may be used. This constructor corresponds to {@link #BackgroundThreadsRunner(int)}
     * @param isSelfInitializing
     * @param poolSize
     */
    public BackgroundThreadsRunner(boolean isSelfInitializing, int poolSize) {
    	setPoolSize(poolSize);
        if(isSelfInitializing) {
            init();
        }
    }

    public int getPoolSize() {
		return poolSize;
	}

    /**
     * This property defines the pool size of the thread pool that runs all the tasks. If this property
     * is not set (or set to 0 or negative value or greater then the number of tasks submitted to this 
     * BackgroundThreadsRunner) then the pool size will default to the number of tasks submitted to this 
     * BackgroundThreadsRunner. This might be fine for a small number of tasks but may be very resource-wise inefficient for 
     * larger number of tasks. Note that this property must be set before method {@link #init()} is called. Otherwise it will 
     * have no effect. Also calling constructor {@link BackgroundThreadsRunner#BackgroundThreadsRunner(int)} will have the 
     * same effect: it will create an instance of this class with specified size rather then default one
     * @param poolSize pool size of the thread pool
     * @see #BackgroundThreadsRunner(int)
     */
	public void setPoolSize(int poolSize) {
		if(executorService == null) {
			this.poolSize = poolSize;
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
        if(poolSize <= 0 || poolSize > taskCollection.size()) {
        	poolSize = taskCollection.size();
        }
        executorService = Executors.newScheduledThreadPool(poolSize);
        for(BackgroundRunnable task : taskCollection) {
            TimeInterval taskExecutionInterval = task.getTaskExecutionInterval();
            executorService.scheduleAtFixedRate(task, taskExecutionInterval.getValue(),
                    taskExecutionInterval.getValue(), taskExecutionInterval.getTimeUnit());
        }
    }

    /**
     * THis is a cleanup method that should be invoked when the application using this infrastructure shuts down.
     * Again for Spring annotation {@link PreDestroy} takes care of this. If you work in environment that annotation
     * {@link PreDestroy} has no effect you have to worry about making sure of invoking this method, otherwise your Tasks
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
