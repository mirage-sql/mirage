Mirage-SQL
======

Mirage-SQL is an easy and powerful SQL centric database access library for Java (or JVM based languages) which provides dynamic SQL templates in plain SQL files.

You can get Mirage-SQL from the Maven Central Repository. Add the following fragment into your `pom.xml`.

```xml
<dependency>
    <groupId>jp.sf.amateras</groupId>
    <artifactId>mirage</artifactId>
    <version>1.2.3</version>
</dependency>
```
or in a Gradle based project add to your ```build.gradle``` the following line:
```groovy
compile :'jp.sf.amateras:mirage:1.2.3'
```

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

In Mirage, you can embed variables or conditions using `special` SQL comments, so it's a plain SQL that can be run with any SQL client tool directly. From this feature, Mirage's SQL template is called [2Way SQL](https://github.com/takezoe/mirage/wiki/2WaySQL).

A detailed documentation is provided in the [Wiki](https://github.com/takezoe/mirage/wiki).
