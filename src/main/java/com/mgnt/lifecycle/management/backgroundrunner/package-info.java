/**
 * This package contains infrastructure that can run user implemented Task classes in a separate thread at configured
 * time interval. In order to create such task user will need to create a class that extends
 * {@link com.mgnt.lifecycle.management.backgroundrunner.BaseBackgroundRunnable} and override 4 methods:
 * {@link com.mgnt.lifecycle.management.backgroundrunner.BaseBackgroundRunnable#initParamsForSpecificImplementation()}<br>,
 * {@link com.mgnt.lifecycle.management.backgroundrunner.BackgroundRunnable#getTaskExecutionInterval()}<br>,
 * {@link com.mgnt.lifecycle.management.backgroundrunner.BackgroundRunnable#setParamValue(com.mgnt.utils.entities.TimeInterval,
 * java.lang.String)}<br>
 * and {@link java.lang.Runnable#run()}<br>. This infrastructure is intended for use with Spring framework although it may
 * be used in other environments as well. Before going any farther lets address a very glaring question. Why would anyone
 * want to use this infrastructure which already looks like requiring hard work if Spring provides an annotation
 * <b>{@code @Scheduled}</b> that can be used for any method and that would work just fine. Well The annotation
 * <b>{@code @Scheduled}</b> takes time interval property i.e. the time between 2 sequential executions in milliseconds or as
 * a cron expression. Neither of those formats are user friendly. Imagine that you need to run a task each 9 hours.
 * So you parameter that you will have to provide to <b>{@code @Scheduled}</b> annotation
 * would look like <b>{@code @Scheduled(fixedRate = 32400000)}</b>. (32400000 is number of milliseconds in 9 hours) This is
 * hardly intuitive... What if you could write <b>{@code @Scheduled(fixedRate = 9h)}</b>? Well, that would be great, but you
 * can not (at least with currently available latest versions). This is what this infrastructure provides. Especially
 * if you annotate one of your properties with annotation <b>{@code @Value("${task.retry.interval}")}</b> and then you will
 * have a properties file that will have a property <b>{@code task.retry.interval=9h}</b> instead of
 * <b>{@code task.retry.interval=32400000}</b>. So there is a tradeof here. This infrastructure definitely requires more effort
 * from a programmer then mere method annotation with <b>{@code @Scheduled}</b>, but provides very intuitive and humanly readable
 * way to define time interval properties. Internally this framework uses utility provided by this library to parse those
 * time intervals. (For details see {@link com.mgnt.utils.TextUtils#parsingStringToTimeInterval(java.lang.String)}) Also
 * this infrastructure allows you to parse other time interval properties (if you class has any) with the same format.
 * <p>
 *     package <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b> contains working source code that demonstrates
 *     how this infrastructure is used. Assume that you have implemented your tasks
 *     {@link com.mgnt.lifecycle.management.backgroundrunner.example.TypeOneTask} and
 *     {@link com.mgnt.lifecycle.management.backgroundrunner.example.TypeTwoTask} (The same classes that are used in package
 *     <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b>). If you work in Spring environment you will need
 *     to annotate those classes with annotation <b>{@code Component}</b> (or define them as beans in your xml configuration files)
 *     and you will need to add one more declaration in your xml configuration file:<br>
 *     <b>{@code <bean id="backgroundThreadsRunner" name="backgroundThreadsRunner"
 *     class="com.mgnt.lifecycle.management.backgroundrunner.BackgroundThreadsRunner" depends-on="typeOneTask,typeTwoTask"/>}</b>
 *     The reason is that your tasks must be instantiated before instance of
 *     {@link com.mgnt.lifecycle.management.backgroundrunner.BackgroundThreadsRunner} is created by Spring
 * </p>
 * For the details on how to implement your tasks please refer to source code in the package
 * <b>{@code com.mgnt.lifecycle.management.backgroundrunner.example}</b> the code there is concise and well commented with
 * detailed explanation
 */
package com.mgnt.lifecycle.management.backgroundrunner;