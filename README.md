# MgntUtils
Mgnt Utilities
This utility package provides some convenience Utilities That provide some manipulations with several data types.
It introduces a new type "Version" as well as "VersionRange" and provides tools for version comparison, conversion from
and to String as well as work with Version ranges.
<p>
The Javadoc API could be found here: <p><a href="http://michaelgantman.github.io/Mgnt/docs/">MgntUtils javadoc API</a></p>
Probably the most interesting and useful feature is stacktrace extractor that allows to extract full or shortened
version of stacktrace. In particular it is very useful in J2EE environment. Exceptions on the server side usually
come with <b>HUGE</b> tail of application server related packages. That makes logs very verbose and hard to read and
search. TextUtils provides various getStacktrace methods that may drastically reduce such stacktraces in a very smart
way so all the important information is preserved. It sifts through "caused by" and "suppressed" parts of the stacktrace
as well. Also the same utility (starting from version 1.5.0.3) allows to filter and shorten stacktrace as a string the same way 
as the stacktrace extracted from exception. So, essentially stacktraces could be filtered "on the fly" at run time or later on from 
any text source such as log.</p>
<p>
Another important feature (since 1.6.0.0) is JSON parser (JsonUtils class) that provides some simple methods to serialize
any object into JSON String and parse (deserialize) JSON string back into the instance of the class. This class is a simplifying wrapper 
over basic functionality of Jason-Jackson library. It provides simplicity but doesn't handle well deserialization of List/Set/Map of Objects.
If that functionality is needed than just use the original Json-Jackson library (or any other JSON library) 
</p>
<p>Other features include parsing String into most of the implementations of Number interface without having
to catch NullPointerException of NumberFormatException. The methods take String value to parse, default value in case of
failure and messages (optional) that will be printed into log if some error occurred.</p>
<p> 
Another useful feature is parsing String to time interval. It parses Strings with numerical value and optional time unit
suffix (for example  string "38s" will be parsed as 38 seconds, "24m" - 24 minutes "4h" - 4 hours, "3d" - 3 days and "45"
as 45 milliseconds.) This method may be very useful for parsing time interval properties such as timeouts or waiting
periods from configuration files.</p>
<p>
 Also there is a feature that converts String to preserve indentation formatting for html without use of escape
 characters. It converts a String in such a way that its spaces are not modified by HTML renderer i.e. it replaces
 regular space characters with non-breaking spaces known as '&amp;nbsp;' but they look in your source as regular space
 '  ' and not as '&amp;nbsp;' It also replaces new line character with '&lt;br&gt;'.
</p>
<p>
Another feature is some small infrastructure that simplifies and automates working with Factories that provide concrete 
implementations of an Interface. The package contains just 2 classes: BaseEntityFactory and BaseEntity. In short what 
this infrastructure does is that if you create a factory that extends BaseEntityFactory and some Interface with all its 
concrete implementations extending BaseEntity then each your concrete implementation class instances will be 
automatically inserted into your factory. You won't have to worry about how and when to populate your factory. 
The infrastructure will do it for you when the constructor of your concrete implementation class is invoked. So all you 
will have to do is to create any number of concrete implementation classes and make sure that for each one constructor 
is invoked. After that you can use your factory to get any of your concrete implementation classes anywhere in your code. 
</p>
<p>
An infrastructure that can run user implemented Task classes in a separate thread at configured 
time interval. The interval could be parsed from String in human readable format such as "9h" for 9 hours. This is based 
on use of the parsing String to time interval feature mentioned above 
</p>
<p>
Class HttpClient provides a base class for implementations that can open HTTP connections to spesific URLs, but also can
be used on its own for sending HTTP requests.
</p>
<p>Also class
StringUnicodeEncoderDecoder converts String into sequence of unicodes and vise-versa.</p> 
<p>Finally WebUtils class provides
a method for chunked reading of HttpRequest content. This could be useful when receiving large files from client on the
server side and the reading speed of the server is faster then writing speed of the client. The utility allows for
auto throttle to adjust to the client. </p>
This library is available on Maven Central. Here are the artifacts:<br>
<p>
        &lt;dependency&gt;<br>
            &nbsp;&nbsp;&lt;groupId&gt;com.github.michaelgantman&lt;&#47;groupId&gt;<br>
            &nbsp;&nbsp;&lt;artifactId&gt;MgntUtils&lt;&#47;artifactId&gt;<br>
            &nbsp;&nbsp;&lt;version&gt;1.7.0.2&lt;&#47;version&gt;<br>
        &lt;&#47;dependency&gt;<br><br>
        &lt;dependency&gt;<br>
            &nbsp;&nbsp;&lt;groupId&gt;com.github.michaelgantman&lt;&#47;groupId&gt;<br>
            &nbsp;&nbsp;&lt;artifactId&gt;MgntUtils&lt;&#47;artifactId&gt;<br>
            &nbsp;&nbsp;&lt;version&gt;1.7.0.2&lt;&#47;version&gt;<br>
            &nbsp;&nbsp;&lt;classifier&gt;javadoc&lt;&#47;classifier&gt;<br>
        &lt;&#47;dependency&gt;<br><br>
        &lt;dependency&gt;<br>
            &nbsp;&nbsp;&lt;groupId&gt;com.github.michaelgantman&lt;&#47;groupId&gt;<br>
            &nbsp;&nbsp;&lt;artifactId&gt;MgntUtils&lt;&#47;artifactId&gt;<br>
            &nbsp;&nbsp;&lt;version&gt;1.7.0.2&lt;&#47;version&gt;<br>
            &nbsp;&nbsp;&lt;classifier&gt;sources&lt;&#47;classifier&gt;<br>
        &lt;&#47;dependency&gt;<br>
</p>

If have any feedback feel free to drop me a note at michael_gantman@yahoo.com
