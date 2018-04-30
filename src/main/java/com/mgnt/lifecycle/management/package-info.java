/**
 * This package contains infrastructure that allows creation of sets of classes implementing single interface, that will
 * self-inject themselves into their respective factory upon their instantiation. So as as user of this infrastructure
 * you can create a particular interface, a corresponding factory that would extend
 * {@link com.mgnt.lifecycle.management.BaseEntityFactory} that will hold your instances and an abstract class
 * implementing the interface and extending {@link com.mgnt.lifecycle.management.BaseEntity} that would be a parent to
 * all your implementations and may contain some logic common to all (or some) of the implementations. After that, all
 * you have to worry about is to create any number of implementations
 * where each one will extend your abstract parent and implement your interface. Upon its implementation, each class
 * will self-insert itself into its factory with the name passed as a constructor parameter or with its own class name if
 * default constructor is used. (This is done by the infrastructure, you as a user of this infrastructure don't have to
 * worry about this) After that in your code when you need a particular implementation of your interface, you will easily
 * extract it from its factory by its name.<br><br>
 *
 * <p>Here is an example when such infrastructure could be useful. Package {@code com.mgnt.lifecycle.management.example}
 * contains the simple example implementation that demonstrates the usage of this infrastructure. Here is a description
 * of this example. Please see the source code in the {@code com.mgnt.lifecycle.management.example} package with detailed
 * explanations. <br><br>
 * Lets say that use Spring Framework. Spring is designed to be non-intrusive, meaning that your domain logic code
 * generally has no dependencies on the framework itself. Say that you have a class {@code GeneralMessageFormatter}
 * defined as a bean that has a
 * method  {@code formatMessage(String message, String format)} that based on the value of "{@code format}" parameter
 * needs to format the {@code message} into a particular format, say Xml or Json. (Note that in
 * {@code com.mgnt.lifecycle.management.example} package the role of that class is done by
 * {@link com.mgnt.lifecycle.management.example.implementations.usage.UsageExample} class)
 * Say you have an Interface called {@link com.mgnt.lifecycle.management.example.InfoFormatter} and its implementations
 * {@link com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter} and
 * {@link com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter} (both of them extending their abstract
 * parent class {@link com.mgnt.lifecycle.management.example.BaseInfoFormatter}). In your
 * {@code formatMessage(String message, String format)} method you will declare a variable of type
 * {@link com.mgnt.lifecycle.management.example.InfoFormatter} that you will
 * need to initialize based on the value of your {@code format} method parameter. So here is a trick:
 * you don't want to inject into your {@code GeneralMessageFormatter} all concrete implementations of
 * {@link com.mgnt.lifecycle.management.example.InfoFormatter} because there may be too many (not just 2 as in our
 * example) and you might want to add more formats later on. So if this infrastructure is used and all your concrete
 * implementations are also defined as beans they will be instantiated by Spring and thus self-inserted into
 * {@link com.mgnt.lifecycle.management.example.InfoFormatterFactory}. By instantiating your Beans Spring would do the
 * work for you and insert all your concrete implementations into their factory. So now anywhere in your code you can
 * easily extract needed concrete implementation from the factory. And you won't need to use Spring dependent context
 * to access those beans, thus preserving non-intrusive nature of Spring usage in your code. This explanation may be a
 * bit confusing, please see the example source code in {@code com.mgnt.lifecycle.management.example} package to make
 * things more clear
 *
 * </p><p><br><br>
 *     Of course this infrastructure can be used to access Spring managed beans in part of the code that is not Spring
 *     managed. Or in any other environment. As long as something or someone actually takes care of instanciating your
 *     concrete implementations this infrastructure will make sure that those instances are inserted into their factory
 *     as part of their instantiation process
 * </p>
 */
package com.mgnt.lifecycle.management;