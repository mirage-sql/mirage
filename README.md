Mirage-SQL
======

Mirage-SQL is an easy and powerful SQL centric database access library for Java which provides the dynamic SQL template by plain SQL.

You can get Mirage-SQL from the Maven central repository. Add the following fragment into your `pom.xml`.

```xml
<dependencies>
  <dependency>
    <groupId>jp.sf.amateras</groupId>
    <artifactId>mirage</artifactId>
    <version>1.2.3</version>
  </dependency>
</dependencies>
```

This is a simple example of SQL template:

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

In Mirage, As you see, you can embed variables or conditions using SQL comment, so it's a plain SQL and possible to run using SQL client tool directly. From this feature, Mirage's SQL template is called [2Way SQL](https://github.com/takezoe/mirage/wiki/2WaySQL).

See details at the [Wiki](https://github.com/takezoe/mirage/wiki).
