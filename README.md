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

## Extended Library Documentation

This README and the library Javadoc provide a solid overview of what MgntUtils is and how to start using it. In addition, there is a series of **articles about the library and its features** that go deeper into each major utility and the Self-Populating Factory pattern. These articles should be treated as **integral parts of the library’s documentation** and are especially useful for understanding architectural uses and advanced setups.

You can find all of these articles in the **Featured** section of my LinkedIn profile:  
[https://www.linkedin.com/in/michael-gantman-9661521/details/featured/](https://www.linkedin.com/in/michael-gantman-9661521/details/featured/)


### Series-Overview Articles

- **[MgntUtils Open Source Java library with stack trace filtering, Silent String parsing, Unicode converter and Version comparison](https://www.linkedin.com/pulse/open-source-java-library-some-useful-utilities-michael-gantman/)**  
  This general-purpose article introduces the library as a whole, lists its main features, and gives brief usage examples for each.

### Feature-Specific Deep Dives

- **[Java Stacktrace filtering utility](https://www.linkedin.com/pulse/java-stacktrace-filtering-utility-michael-gantman-t003f/)**  
  Focuses on the library’s most popular feature: `TextUtils.getStacktrace()`. It explains how to use it for cleaner logs and how to configure package-based filtering to keep important stack frames and remove noise.<br><br>

- **[Parsing human-readable Strings to Time Intervals - no more crazy numbers in milliseconds](https://www.linkedin.com/pulse/parsing-human-readable-strings-time-intervals-more-crazy-gantman-js3ee/)**  
  Explores the `TextUtils.parseStringToTimeInterval()` and `TextUtils.parseStringToDuration()` utilities in detail, showing how human-readable time-interval strings (like `"5d"`, `"4h"`, `"30m"`) can replace hard-to-read numeric values in configuration and code.<br><br>
  
- **[String to Unicode converter utility](https://www.linkedin.com/pulse/string-unicode-converter-utility-michael-gantman-hd9lf/)**  
  Explores `StringUnicodeEncoderDecoder` and shows how to convert strings to Unicode escape sequences and back. This is especially useful when debugging encoding issues, inspecting Unicode-encoded configuration, or working with non-Latin languages.<br><br>


- **[Infrastructure for Extensible Multi-Stage Workflows Across Multiple Data Types](https://www.linkedin.com/pulse/infrastructure-extensible-multi-stage-workflows-across-gantman-0vu2f/)**  
  This is the most in-depth article in the series. It can be viewed as a **two-part piece**:
  - **Part 1** covers the Self-Populating Factory pattern in greater detail and walks through the **runnable example included in the library**.
  - **Part 2** shows how this pattern can be used to build extensible, multi-stage workflows for multiple data types, allowing you to “extend the flow length-wise and width-wise” (i.e., resolve an N × M matrix-like problem) without modifying existing code - just by adding new data types and stages.

  This article presents a complex architectural pattern. This pattern effectively evolves the library from a **collection of utilities** into an **architectural design pattern** that can be reused across many projects.
 
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
