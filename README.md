# Mgnt
Mgnt Utilities
This utility package provides some convenience Utilities That provide some manipulations with several data types.
It introduces a new type "Version" as well as "VersionRange" and provides tools for version comparison, conversion from
and to String as well as work with Version ranges.<br>
Probably the most interesting and useful feature is stacktrace extractor that allows to extract full or shortened
version of stacktrace. In particular it is very useful in J2EE environment. Exceptions on the server side usually
come with <b>HUGE</b> tail of application server related packages. That makes logs very verbose and hard to read and
search. TextUtils provides various getStacktrace methods that may drastically reduce such stacktraces in a very smart
way so all the important information is preserved. It sifts through "caused by" and "suppressed" parts of the stacktrace
as well.<br>Other features include parsing String into most of the implementations of Number interface without having
to catch NullPointerException of NumberFormatException. The methods take String value to parse, default value in case of
failure and messages (optional) that will be printed into log if some error occurred.<br> 
Another useful feature is parsing String to time interval. It parses Strings with numerical value and optional time unit
suffix (for example  string "38s" will be parsed as 38 seconds, "24m" - 24 minutes "4h" - 4 hours, "3d" - 3 days and "45"
as 45 milliseconds.) This method may be very useful for parsing time interval properties such as timeouts or waiting
periods from configuration files.<br>
<p>
 Also there is a feature that converts String to preserve indentation formatting for html without use of escape
 characters. It converts a String in such a way that its spaces are not modified by HTML renderer i.e. it replaces
 regular space characters with non-breaking spaces known as '&amp;nbsp;' but they look in your source as regular space
 '  ' and not as '&amp;nbsp;' It also replaces new line character with '&lt;br&gt;'.
</p>
Also class
StringUnicodeEncoderDecoder converts String into sequence of unicodes and vise-versa.<br> Finally WebUtils class provides
a method for chunked reading of HttpRequest content. This could be useful when receiving large files from client on the
server side and the reading speed of the server is faster then writing speed of the client. The utility allows for
auto throttle to adjust to the client. This package was tested with Java versions 1.8 and 1.7 but probably would compile
and work with version as early as 5 and up. This library is available on Maven Central. Here are the artifacts:<br>
<p>
        &ltdependency&gt;<br>
            &nbsp&ltgroupId&gt;com.github.michaelgantman&lt&#47;groupId&gt;<br>
            &nbsp&ltartifactId&gt;MgntUtils&lt&#47;artifactId&gt;<br>
            &nbsp&ltversion&gt;1.1.0.4&lt&#47;version&gt;<br>
        &lt&#47;dependency&gt;<br><br>
        &ltdependency&gt;<br>
            &nbsp&ltgroupId&gt;com.github.michaelgantman&lt&#47;groupId&gt;<br>
            &nbsp&ltartifactId&gt;MgntUtils&lt&#47;artifactId&gt;<br>
            &nbsp&ltversion&gt;1.1.0.4&lt&#47;version&gt;<br>
            &nbsp&ltclassifier&gt;javadoc&lt&#47;classifier&gt;<br>
        &lt&#47;dependency&gt;<br><br>
        &ltdependency&gt;<br>
            &nbsp&ltgroupId&gt;com.github.michaelgantman&lt&#47;groupId&gt;<br>
            &nbsp&ltartifactId&gt;MgntUtils&lt&#47;artifactId&gt;<br>
            &nbsp&ltversion&gt;1.1.0.4&lt&#47;version&gt;<br>
            &nbsp&ltclassifier&gt;sources&lt&#47;classifier&gt;<br>
        &lt&#47;dependency&gt;<br>
</p>

If have any feedback feel free to drop me a note at michael_gantman@yahoo.com
