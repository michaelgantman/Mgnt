/**
 * This package contains some small infrastructure that simplifies and automates working with Factories that provide
 * concrete implementations of an Interface. The package contains just 2 classes:
 * {@link com.mgnt.lifecycle.management.BaseEntityFactory} and {@link com.mgnt.lifecycle.management.BaseEntity}. In short
 * what this infrastructure does is that if you create a factory that extends
 * {@link com.mgnt.lifecycle.management.BaseEntityFactory} and some Interface with all its concrete implementations
 * extending {@link com.mgnt.lifecycle.management.BaseEntity} then each your concrete implementation class will be
 * automatically inserted into your factory. You won't have to worry about how and when to populate your factory. The
 * infrastructure will do it for you when the constructor of your concrete implementation class is invoked. So all you
 * will have to do is to create any number of concrete implementation classes and make sure that for each one constructor
 * in invoked. After that you can use your factory to get any of your concrete implementation classes anywhere in your code.
 * This is short explanation. There are few more details but not that many.
 *
 * <br><br><p>
 * Lets show it with an example. This infrastructure contains Package <b>{@code com.mgnt.lifecycle.management.example}</b>
 * that has some sub-packages that demonstrate how this infrastructure is used. So in this javadoc the same classes will
 * be used. Please see the source code of that package to get all the details. Say you have an Interface called
 * {@link com.mgnt.lifecycle.management.example.InfoFormatter} that has a single method
 * {@link com.mgnt.lifecycle.management.example.InfoFormatter#formatMessage(java.lang.String)}. Obviously this method
 * takes some text and formats it according some logic that would be implemented in each concrete implementation.
 * <p>
 * Next we need to create a factory for our concrete implementations. So we a factory class
 * {@link com.mgnt.lifecycle.management.example.InfoFormatterFactory} that extends
 * {@link com.mgnt.lifecycle.management.BaseEntityFactory} This class look like this:
 * <code><br><br>
 * package com.mgnt.lifecycle.management.example;<br>
 *
 * import com.mgnt.lifecycle.management.BaseEntityFactory;<br>
 * import java.util.Collection;<br>
 *<br>
 * public class InfoFormatterFactory extends BaseEntityFactory<InfoFormatter> {<br>
 *     &#x00A0&#x00A0private static InfoFormatterFactory FACTORY = new InfoFormatterFactory();<br>
 *<br>
 *     &#x00A0&#x00A0private InfoFormatterFactory() {<br>
 *     &#x00A0&#x00A0}<br>
 *<br>
 *     &#x00A0&#x00A0public static InfoFormatterFactory getFactoryInstance() {<br>
 *         &#x00A0&#x00A0&#x00A0&#x00A0return FACTORY;<br>
 *     &#x00A0&#x00A0}<br>
 *<br>
 *     &#x00A0&#x00A0public static InfoFormatter getInstance(String key) {<br>
 *         &#x00A0&#x00A0&#x00A0&#x00A0return FACTORY.getEntity(key);<br>
 *     &#x00A0&#x00A0}<br>
 *<br>
 *     &#x00A0&#x00A0&#x00A0&#x00A0public static Collection<InfoFormatter> getAllInstances() {<br>
 *         &#x00A0&#x00A0&#x00A0&#x00A0return FACTORY.getAllEntities();<br>
 *     &#x00A0&#x00A0}<br>
 * }<br>
 * </code><br>
 * </p>
 *
 *
 * For the purposes of this infrastructure it is recommended that a single abstract parent class extending
 * {@link com.mgnt.lifecycle.management.BaseEntity} and implementing your interface is created. Then all your concrete
 * implementations will extend this class. So in our case we will have
 * {@link com.mgnt.lifecycle.management.example.BaseInfoFormatter} that extends {@link com.mgnt.lifecycle.management.BaseEntity}
 * and implements {@link com.mgnt.lifecycle.management.example.InfoFormatter}.
 *
 * Then  we have 2 concrete implementations: {@link com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter}
 * and {@link com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter}.
 *
 * </p><br>
 *
 *
 *
 *
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
 * method  {@code doFormatMessage(String message, String format)} that based on the value of "{@code format}" parameter
 * needs to format the {@code message} into a particular format, say Xml or Json. (Note that in
 * {@code com.mgnt.lifecycle.management.example} package the role of that class is done by
 * {@link com.mgnt.lifecycle.management.example.implementations.usage.UsageExample} class)
 * Say you have an Interface called {@link com.mgnt.lifecycle.management.example.InfoFormatter} and its implementations
 * {@link com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter} and
 * {@link com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter} (both of them extending their abstract
 * parent class {@link com.mgnt.lifecycle.management.example.BaseInfoFormatter}). In your
 * {@code doFormatMessage(String message, String format)} method you will declare a variable of type
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