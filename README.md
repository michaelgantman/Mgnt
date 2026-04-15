# MgntUtils

![Maven Central](https://img.shields.io/maven-central/v/com.github.michaelgantman/MgntUtils)
![License](https://img.shields.io/github/license/michaelgantman/Mgnt)
![Javadoc](https://img.shields.io/badge/javadoc-available-green)
![GitHub release](https://img.shields.io/github/v/release/michaelgantman/Mgnt)
![GitHub stars](https://img.shields.io/github/stars/michaelgantman/Mgnt)

MgntUtils is an open-source Java library that provides:
* A collection of **utility methods** for common tasks that are either missing from or overly verbose in the standard Java API
* **Self-Populating Factory Micro-Framework**: The most architecturally significant part of this library — a micro-framework whose implementation classes automatically register themselves with their factory upon instantiation, eliminating manual registry maintenance and enabling a powerful N × M extensibility pattern described in the documentation below.

<p>
The Javadoc API is available here: <a href="https://michaelgantman.github.io/Mgnt/docs/">MgntUtils Javadoc API</a>
</p>

## Key Features and Utilities

- **Stack Trace Filtering**  
  One of the most popular features is `TextUtils.getStacktrace()`, which filters out “noise” from stack traces (such as application-server-related packages), making logs more readable and reducing log size. The filtering can be configured using package prefixes to specify which packages are important.<br><br>

- **Human-Readable Time Intervals**  
  The methods `TextUtils.parseStringToTimeInterval()` and `TextUtils.parseStringToDuration()` convert strings like `"5d"`, `"4h"`, or `"30m"` directly into `long` milliseconds or `java.time.Duration` values, making time-based configuration far more maintainable than raw numeric values.<br><br>

- **Unicode Conversion**  
  The `StringUnicodeEncoderDecoder` class converts strings to Unicode escape sequences (e.g., `Hello` → `\u0048\u0065\u006c\u006c\u006f`) and back. This is useful for debugging encoding issues, handling special characters and emojis and working with non-Latin scripts such as Hebrew, Arabic, Slavic languages, Chinese, and others.


- **Silent Numeric Parsing**  
  Utility methods for parsing strings into numeric types (e.g., `Integer`, `Long`, `Double`) without throwing `NumberFormatException`. Instead, they return a default value or `null` when parsing fails, avoiding boilerplate try-catch blocks.<br><br>

- **Version Comparison**  
  The library provides a `Version` type and `VersionRange` support, enabling easy comparison of software versions and checking whether a version falls within a given range. It also supports checking the intersection of version ranges.<br><br>

- **JSON Parsing**  
  Simple JSON serialization and deserialization utilities for basic use cases.<br><br>
  
- **Web and File Utilities**
    - **Simple HTTP Client**: A lightweight, easy-to-configure HTTP client suitable for repeated calls to the same URLs, such as in microservices communication. Configuration is reusable across multiple calls.
    - **HTTP Throttling**: `WebUtils` includes a method for chunked reading of HTTP request content that can auto-throttle to match the client’s consumption speed.
    - **File I/O**: Basic utilities for reading from and writing to files.
 
    
## Self-Populating Factory Micro-Framework

> All other utilities in this library solve individual, well-defined problems. What follows is different in kind: it is a fundamental architectural pattern and by far the most significant part of this library. It is packaged as a **lightweight micro-framework** — two classes — that provides the infrastructure for a self-registering factory pattern. The library ships this infrastructure along with a complete, runnable example. The broader architectural pattern it enables — resolving an N × M extensibility problem across multiple data types and processing stages without modifying existing code — is not part of the library itself, but is documented in detail in the article linked below and demonstrated in a separate companion project. That article should be considered required reading alongside this section.

The framework implementation is located in package `com.mgnt.lifecycle.management`. It provides infrastructure for **factories whose components automatically register themselves** during initialization — similar in spirit to an Inversion of Control container, but without the framework overhead and with no external dependencies. Implementation classes are **factory-aware**: this eliminates manual factory-population logic and centralized registration maps entirely — as long as a concrete implementation is instantiated, it becomes available through the factory.

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

  This article underscores the fact that beyond its collection of utilities, this library provides a foundation for a powerful **architectural design pattern** that can be reused across many projects.
 
## Installing MgntUtils

MgntUtils is available on **Maven Central**:

```xml
<dependency>
    <groupId>com.github.michaelgantman</groupId>
    <artifactId>MgntUtils</artifactId>
    <version>1.7.0.6</version>
</dependency>
```
If you also want Javadoc and sources for your IDE:

```xml
<dependency>
    <groupId>com.github.michaelgantman</groupId>
    <artifactId>MgntUtils</artifactId>
    <version>1.7.0.6</version>
    <classifier>javadoc</classifier>
</dependency>
```

```xml
<dependency>
    <groupId>com.github.michaelgantman</groupId>
    <artifactId>MgntUtils</artifactId>
    <version>1.7.0.6</version>
    <classifier>sources</classifier>
</dependency>
```

If you have any feedback, feel free to drop me a note at [michael_gantman@yahoo.com](mailto:michael_gantman@yahoo.com)
