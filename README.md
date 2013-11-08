Mirage-SQL
======

Mirage is an easy and powerful SQL centric database access library in Java which provides the dynamic SQL template by plain SQL.

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

See details at the [Wiki](https://github.com/takezoe/mirage/wiki)
