/**
 * This package contains some small infrastructure that simplifies and automates working with Factories that provide
 * concrete implementations of an Interface. The package contains just 2 classes:
 * {@link com.mgnt.lifecycle.management.BaseEntityFactory} and {@link com.mgnt.lifecycle.management.BaseEntity}. In short
 * what this infrastructure does is that if you create a factory that extends
 * {@link com.mgnt.lifecycle.management.BaseEntityFactory} and some Interface with all its concrete implementations
 * extending {@link com.mgnt.lifecycle.management.BaseEntity} then each your concrete implementation class instances will be
 * automatically inserted into your factory. You won't have to worry about how and when to populate your factory. The
 * infrastructure will do it for you when the constructor of your concrete implementation class is invoked. So all you
 * will have to do is to create any number of concrete implementation classes and make sure that for each one constructor
 * is invoked. After that you can use your factory to get any of your concrete implementation classes anywhere in your code.
 * This is short explanation. There are few more details but not that many.
 *
 * <br><br><p>
 * Lets show it with an example. This infrastructure has Package <b>{@code com.mgnt.lifecycle.management.example}</b>
 * that has some sub-packages that contains source code example that demonstrate how this infrastructure is used. So in
 * this javadoc the same classes will be used. Please see the source code of that package to get all the details.
 * Say you have an Interface called
 * {@link com.mgnt.lifecycle.management.example.InfoFormatter} that has a single method
 * {@link com.mgnt.lifecycle.management.example.InfoFormatter#formatMessage(java.lang.String)}. Obviously this method
 * takes some text and formats it according some logic that would be implemented in each concrete implementation.
 * <p>
 * Next we need to create a factory for our concrete implementations. So we have a factory class
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
 * This class should look like this:
 * <p><br><code>
 *
 *&nbsp&nbsp public abstract class BaseInfoFormatter extends BaseEntity<BaseInfoFormatter> implements InfoFormatter {<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp // This is mandatory part of the code for the infrastructure to work<br>
 *    &nbsp&nbsp&nbsp&nbsp private static final String FACTORY_TYPE = BaseInfoFormatter.class.getSimpleName();<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp static {<br>
 *        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp init(FACTORY_TYPE, InfoFormatterFactory.getFactoryInstance());<br>
 *    &nbsp&nbsp&nbsp&nbsp }<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp public BaseInfoFormatter() {<br>
 *        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp super(FACTORY_TYPE);<br>
 *    &nbsp&nbsp&nbsp&nbsp }<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp public BaseInfoFormatter(String customName) {<br>
 *        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp super(FACTORY_TYPE, customName);<br>
 *    &nbsp&nbsp&nbsp&nbsp }<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp // The end of mandatory part<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp // Some business logic methods that are common to all concrete implementations<br>
 * 	  &nbsp&nbsp&nbsp&nbsp //...<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp //Implementation of interface declared method<br>
 * 	  &nbsp&nbsp&nbsp&nbsp //...<br>
 *&nbsp&nbsp }<br>
 *<br></code></p>
 *
 * Then  we have 2 concrete implementations: {@link com.mgnt.lifecycle.management.example.implementations.JsonInfoFormatter}
 * and {@link com.mgnt.lifecycle.management.example.implementations.XmlInfoFormatter} (both of them extending their abstract
 * parent class {@link com.mgnt.lifecycle.management.example.BaseInfoFormatter}). Here is how one of them might look,
 * (the second looks very similar so it is ommitted here)
 * <p><br><code>
 *&nbsp&nbsp public class JsonInfoFormatter extends BaseInfoFormatter {<br>
 *    &nbsp&nbsp&nbsp&nbsp private final static String CUSTOM_NAME = "JSON";<br>
 *<br>
 *    &nbsp&nbsp&nbsp&nbsp public JsonInfoFormatter() {<br>
 *        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp super(CUSTOM_NAME);<br>
 *    &nbsp&nbsp&nbsp&nbsp }<br>
 *<br>
 * &nbsp&nbsp&nbsp&nbsp	//Implementation of abstract method or overriding methods goes here<br>
 *&nbsp&nbsp }<br>
 *<br></code></p>
 *
 * So this is our row material so to speak.
 * So lets see how this is used. Look at the class
 * {@link com.mgnt.lifecycle.management.example.implementations.usage.UsageExample} and in particular its methods
 * {@link com.mgnt.lifecycle.management.example.implementations.usage.UsageExample#init()} (that is invoked in the main()
 * method) and looks as folllows
 * <p><br><code>
 *    &nbsp&nbsp private static void init() { <br>
 *       &nbsp&nbsp&nbsp&nbsp new JsonInfoFormatter();<br>
 *       &nbsp&nbsp&nbsp&nbsp new XmlInfoFormatter();<br>
 *    &nbsp&nbsp }<br>
 * </code>
 * </p><br>
 * and
 * {@link com.mgnt.lifecycle.management.example.implementations.usage.UsageExample#printFormattedGreetings()} that looks
 * as follows
 * <p><br><code>
 *    &nbsp&nbsp private static void printFormattedGreetings() { <br>
 *     &nbsp&nbsp&nbsp&nbsp InfoFormatter formatter = InfoFormatterFactory.getInstance("JSON"); <br>
 *     &nbsp&nbsp&nbsp&nbsp System.out.println("JSON greeting: " + formatter.formatMessage(MESSAGE)); <br>
 *     &nbsp&nbsp&nbsp&nbsp formatter = InfoFormatterFactory.getInstance("XML"); <br>
 *     &nbsp&nbsp&nbsp&nbsp System.out.println("XML greeting: " + formatter.formatMessage(MESSAGE)); <br>
 *     &nbsp&nbsp&nbsp&nbsp List<String> allMessages = new ArrayList<>(); <br>
 *     &nbsp&nbsp&nbsp&nbsp for(InfoFormatter formattedMessage : InfoFormatterFactory.getAllInstances()) { <br>
 *       &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp allMessages.add(formattedMessage.formatMessage(MESSAGE)); <br>
 *     &nbsp&nbsp&nbsp&nbsp } <br>
 *     &nbsp&nbsp&nbsp&nbsp System.out.println("All greetings: " + allMessages);<br>
 *  &nbsp&nbsp } <br>
 * </code>
 * </p>
 * Note that we simply use {@link com.mgnt.lifecycle.management.example.InfoFormatterFactory#getInstance(java.lang.String)}
 * to get ahold of our concrete implementations. This works because method init was invoked before. Note that in init method
 * we simply instantiate our concrete implementations and not saving any references to them. Thanks to the infrastructure,
 * during its instantiation each instance inserted itself into its factory and that allows it to be accessed through the
 * factory. As for the names ("XML" and "JSON") they are in our case defined in each concrete class and passed to the factory
 * through use of its parent constructor
 * {@link com.mgnt.lifecycle.management.example.BaseInfoFormatter#BaseInfoFormatter(java.lang.String)} which in turn will
 * invoke its parent constructor {@link com.mgnt.lifecycle.management.BaseEntity#BaseEntity(java.lang.String, java.lang.String)}.
 * However, {@link com.mgnt.lifecycle.management.BaseEntity} provides another constructor
 * {@link com.mgnt.lifecycle.management.BaseEntity#BaseEntity(java.lang.String)} where custom name for entity is not
 * required. And the entity would be registered in its factory by its class name.
 * <p>
 *     <br>
 *  That was about how this infrastructure works. Now where would it be convenient and beneficial to use it? Note that in order
 *  for infrastructure to work we had to invoke constructor for each concrete class. If only someone or something could
 *  have done that for us, that would be magical. Well, this could be done for us by Spring framework. Remember that Spring
 *  instantiate all it's defined beans during its initialization. So within Spring context if we simply declare our concrete
 *  implementations as Spring beans, Spring would instantiate them for us, thus initializing their factory automatically.
 *  This could be very convenient. Imagine that you have some bean that has a property of type
 *  {@link com.mgnt.lifecycle.management.example.InfoFormatter}, but which actual implementation would be needed is determined
 *  at runtime. So at that moment you can use
 *  {@link com.mgnt.lifecycle.management.example.InfoFormatterFactory#getInstance(java.lang.String)} to access needed
 *  implementation. This will allow you not to inject ALL your concrete instantiations into your bean and you won't have to
 *  use Spring BeanFactory to access a Spring defined bean as that would violate non-intrusiveness of Spring (meaning 
 *  you can write components which have no dependency on Spring). Also, If at some later stage you will need to add
 *  more concrete implementations, all you will have to do is to add your implementation classes to you code and declare
 *  them to be Spring beans. The rest will be done by Spring and this infrastructure!
 * </p>
 */
package com.mgnt.lifecycle.management;