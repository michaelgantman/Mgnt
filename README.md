# MgntUtils

![Maven Central](https://img.shields.io/maven-central/v/com.github.michaelgantman/MgntUtils)
![License](https://img.shields.io/github/license/michaelgantman/Mgnt)
![Javadoc](https://img.shields.io/badge/javadoc-available-green)
![GitHub release](https://img.shields.io/github/v/release/michaelgantman/Mgnt)
![GitHub stars](https://img.shields.io/github/stars/michaelgantman/Mgnt)

MgntUtils is an Open Source Java library that provides:
* Collection of **utility methods** for common tasks that are either missing from or overly verbose in the standard Java API
* **Self-Populating Factory Infrastructure**: A small framework for implementing factories whose components automatically register themselves with the factory during initialization. (Similar to Inversion Of Control (IOC) pattern)

<p>
The Javadoc API is available here: <a href="https://michaelgantman.github.io/Mgnt/docs/">MgntUtils Javadoc API</a> 

## Key Features and Utilities

- **Stack Trace Filtering**  
  One of the most popular features is `TextUtils.getStacktrace()`, which filters out “noise” from stack traces (such as application-server-related packages), making logs more readable and reducing log size. The filtering can be configured using package prefixes to specify which packages are important.<br><br>

- **Human-Readable Time Intervals**  
  The methods `TextUtils.parseStringToTimeInterval()` and `TextUtils.parseStringToDuration()` convert strings like `"5d"`, `"4h"`, or `"30m"` directly into `long` milliseconds or `java.time.Duration` values, making time-based configuration far more maintainable than raw numeric values.<br><br>

- **Unicode Conversion**  
  The `StringUnicodeEncoderDecoder` class converts strings to Unicode escape sequences (e.g., `Hello` → `\u0048\u0065\u006c\u006c\u006f`) and back. This is useful for debugging encoding issues, handling special characters and emojis and working with non-Latin scripts such as Hebrew, Arabic, Slavic, Chinese, and others.


- **Silent Numeric Parsing**  
  Utility methods for parsing strings into numeric types (e.g., `Integer`, `Long`, `Double`) without throwing `NumberFormatException`. Instead, they return a default value or `null` when parsing fails, avoiding boilerplate try-catch blocks.<br><br>

- **Version Comparison**  
  The library provides a `Version` type and `VersionRange` support, enabling easy comparison of software versions and checking whether a version falls within a given range. It also supports version ranges intersection check<br><br>

- **JSON Parsing**  
  Simple JSON serialization and deserialization utilities for basic use cases.<br><br>
  
- **Web and File Utilities**
    - **Simple HTTP Client**: A lightweight, easy-to-configure HTTP client suitable for repeated calls to the same URLs, such as in microservices communication. Configuration is reusable across multiple calls.
    - **HTTP Throttling**: `WebUtils` includes a method for chunked reading of HTTP request content that can auto-throttle to match the client’s consumption speed.
    - **File I/O**: Basic utilities for reading from and writing to files.
 
    
## Self-Populating Factory Infrastructure

The framework implementation is located in package `com.mgnt.lifecycle.management`. It provides infrastructure for **factories whose components automatically register themselves** during initialization.

In this model, implementation classes are **factory-aware**: when an instance is created, it automatically registers with its factory. This removes the need for manual factory-population logic or centralized registration maps. As long as a concrete implementation is instantiated, it becomes available through the factory.

The package-level Javadoc contains a detailed explanation of the design and a **complete, runnable example** included with the library source.

See the Javadoc for details:  
[Self-Populating Factory package documentation](https://michaelgantman.github.io/Mgnt/docs/com/mgnt/lifecycle/management/package-summary.html)

## Extended Library documentation
This README and the library Javadoc are a good starting point to see what this library is about. However, there is a set of articles about the library and its features that provide deep dive into details about those features and should be considered as part of this library documentation. Here is the list of those articles and short description of what each article is about. All the articles could be found in [featured section of my LinkedIn profile](https://www.linkedin.com/in/michael-gantman-9661521/details/featured/)

* [MgntUtils Open Source Java library with stack trace filtering, Silent String parsing, Unicode converter and Version comparison](https://www.linkedin.com/pulse/open-source-java-library-some-useful-utilities-michael-gantman/)
<br>This is a general article that describes the library as a whole and lists most of the features and brief explanation on how to use them<br><br>

* [Java Stacktrace filtering utility](https://www.linkedin.com/pulse/java-stacktrace-filtering-utility-michael-gantman-t003f/)
<br>This is probably the most popular utility in the library. It smartly filters out "noise" from stack traces, making them far more readable while also reducing log size. The filtering can be configured using package prefixes to specify which packages should be considered important and which should be filtered out from the output. <br><br>

* [Parsing human-readable Strings to Time Intervals - no more crazy numbers in milliseconds](https://www.linkedin.com/pulse/parsing-human-readable-strings-time-intervals-more-crazy-gantman-js3ee/)
<br>This article discusses a utility that converts human-readable strings like "5d" (5 days) or "38s" (38 seconds) directly into milliseconds or `java.time.Duration` objects, making time interval configuration far more readable and maintainable.<br><br>

* [String to Unicode converter utility](https://www.linkedin.com/pulse/string-unicode-converter-utility-michael-gantman-hd9lf/)
<br>This utility converts strings to Unicode escape sequences and vice versa. For example, the string `Hello World` becomes `\u0048\u0065\u006c\u006c\u006f\u0020\u0057\u006f\u0072\u006c\u0064`. This can be useful when debugging thorny encoding issues, inspecting Unicode-encoded configuration (such as `.properties` files), or working with non-Latin languages such as Hebrew, Arabic, Ukrainian, Chinese, and others.<br><br>

* [Infrastructure for Extensible Multi-Stage Workflows Across Multiple Data Types](https://www.linkedin.com/pulse/infrastructure-extensible-multi-stage-workflows-across-gantman-0vu2f/)
<br>Finally, the most in-depth article in the series. This one is the longest by far. It can be viewed as two-part article.<br><br>
    * The first part deals with Self-populated Factory pattern, explaining it in greater detail and walking through code example provided in the library<br><br>
    * The second part proposes an idea on how this pattern could be utilized to build extensible multi-stage workflow for multiple data types. The proposed idea allows to add stages to the flow and add data types to each stage - i.e. extend your flow length-wise and width-wise (resolving N * M matrix problem) without modifying the pre-existing code - just adding new data-types or stages. This article describes a complex architectural solution. This framework — together with the article — takes the library a significant step forward, evolving it from a collection of useful utilities into an architectural design solution. <br><br>
 
This library is available on Maven Central. Here are the artifacts:<br>
<p>

	<dependency>
		<groupId>com.github.michaelgantman</groupId>
		<artifactId>MgntUtils</artifactId>
		<version>1.7.0.5</version>
	</dependency>

	<dependency>
		<groupId>com.github.michaelgantman</groupId>
		<artifactId>MgntUtils</artifactId>
		<version>1.7.0.5</version>
		<classifier>javadoc</classifier>
	</dependency>

	<dependency>
		<groupId>com.github.michaelgantman</groupId>
		<artifactId>MgntUtils</artifactId>
		<version>1.7.0.5</version>
		<classifier>sources</classifier>
	</dependency>
	
</p>

If have any feedback feel free to drop me a note at [michael_gantman@yahoo.com](mailto:michael_gantman@yahoo.com)
