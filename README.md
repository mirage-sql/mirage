Mirage-SQL [![Build](https://github.com/mirage-sql/mirage/actions/workflows/gradle.yml/badge.svg)](https://github.com/mirage-sql/mirage/actions/workflows/gradle.yml) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.miragesql/miragesql/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.miragesql/miragesql) [![Join the chat at https://gitter.im/mirage-sql/mirage-sql](https://badges.gitter.im/mirage-sql/mirage-sql.svg)](https://gitter.im/mirage-sql/mirage-sql?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
======

**`Mirage-SQL`** is an easy and powerful SQL-centric database access library for Java (or JVM based languages) which provides dynamic SQL templates in plain SQL files.

### Using
You can get **`Mirage-SQL`** from the Maven Central repository. Add the following fragment into your `pom.xml`.

```xml
<dependency>
    <groupId>com.miragesql</groupId>
    <artifactId>miragesql</artifactId>
    <version>2.1.2</version>
</dependency>
```
or in a Gradle based project add to your ```build.gradle``` the following line:
```groovy
compile 'com.miragesql:miragesql:2.1.2'
```

or just download it from the [Release Page](https://github.com/mirage-sql/mirage/releases).


Other **`Mirage-SQL`** Modules:

Module|Description|Gradle
---   |---        |---
**Mirage-SQL Test**|The testing functionality.| `testCompile 'com.miragesql:miragesql-test:2.1.2'`
**Mirage-SQL Tools**|The development tools.| `testCompile 'com.miragesql:miragesql-tools:2.1.2'`
**Mirage-SQL Integration** |The integration with [Spring](https://projects.spring.io/spring-framework/), [Guice](https://github.com/google/guice) and [Seasar2](http://www.seasar.org/en/)..|`compile 'com.miragesql:miragesql-integration:2.1.2'`


If you are updating your application from a previous **`Mirage-SQL`** version, see the [Migration Guide](https://github.com/mirage-sql/mirage/wiki/Migration-Guide).

### Example

This is a simple example of a SQL template:

```sql
SELECT * FROM BOOK
/*BEGIN*/
  WHERE
  /*IF author != null */
        AUTHOR = /*author*/'Naoki Takezoe'
  /*END*/
  /*IF minPrice != null */
    AND PRICE >= /*minPrice*/20
  /*END*/
  /*IF maxPrice != null */
    AND PRICE <= /*maxPrice*/100
  /*END*/
/*END*/
ORDER BY BOOK_ID ASC
```

With **`Mirage-SQL`** you can embed variables or conditions using `special` SQL comments, so it's a plain SQL that can be run with any SQL client tool directly. 
This feature used in the Mirage's SQL template it's called [2Way SQL](https://github.com/mirage-sql/mirage/wiki/2WaySQL).

### Links:
 - A **detailed documentation** is provided in the [Wiki](https://github.com/mirage-sql/mirage/wiki).
 - **User Support** in English is provided in the [Gitter Chatroom](https://gitter.im/mirage-sql/mirage-sql).
 - If you find any **bugs or issues**, please report them in the [GitHub Issue Tracker](https://github.com/mirage-sql/mirage/issues).
 - **[Release Notes](https://github.com/mirage-sql/mirage/wiki/Releases)** of all previous Mirage-SQL versions.
 - Mirage-SQL also has support for other **JVM based languages**:
   - [Scala language](http://www.scala-lang.org/) support - [Mirage-SQL Scala](https://github.com/mirage-sql/mirage-scala)
