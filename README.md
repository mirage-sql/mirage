Mirage-SQL [![Build Status](https://travis-ci.org/mirage-sql/mirage.svg?branch=master)](https://travis-ci.org/mirage-sql/mirage) [![Join the chat at https://gitter.im/mirage-sql/mirage-sql](https://badges.gitter.im/mirage-sql/mirage-sql.svg)](https://gitter.im/mirage-sql/mirage-sql?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
======

Mirage-SQL is an easy and powerful SQL centric database access library for Java (or JVM based languages) which provides dynamic SQL templates in plain SQL files.

#### Using
You can get Mirage-SQL from the Maven Central Repository. Add the following fragment into your `pom.xml`.

```xml
<dependency>
    <groupId>com.miragesql</groupId>
    <artifactId>miragesql</artifactId>
    <version>1.3.0</version>
</dependency>
```
or in a Gradle based project add to your ```build.gradle``` the following line:
```groovy
compile 'com.miragesql:miragesql:1.3.0'
```

If you are updating your application from a previous Mirage-SQL version, see the [Migration Guide](https://github.com/mirage-sql/mirage/wiki/Migration-Guide).

#### Example

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

In Mirage, you can embed variables or conditions using `special` SQL comments, so it's a plain SQL that can be run with any SQL client tool directly. 
This feature used in the Mirage's SQL template it's called [2Way SQL](https://github.com/mirage-sql/mirage/wiki/2WaySQL).

#### Links
 - A **detailed documentation** is provided in the [Wiki](https://github.com/mirage-sql/mirage/wiki).
 - Support in English is provided in the [Gitter Chatroom](https://gitter.im/mirage-sql/mirage-sql).
 - If you find any bugs or issues, please report them in the [GitHub Issue Tracker](https://github.com/mirage-sql/mirage/issues).
 - Mirage-SQL has integrations with other JVM based languages too:
   - [Scala Integration](https://github.com/mirage-sql/mirage-scala)
   - [Groovy Integration](https://github.com/mirage-sql/mirage-groovy)