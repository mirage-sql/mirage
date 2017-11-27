package com.miragesql.miragesql.util

import com.miragesql.miragesql.DefaultEntityOperator
import com.miragesql.miragesql.annotation.PrimaryKey
import com.miragesql.miragesql.annotation.Table
import com.miragesql.miragesql.bean.BeanDesc
import com.miragesql.miragesql.bean.BeanDescFactory
import com.miragesql.miragesql.bean.PropertyDesc
import com.miragesql.miragesql.naming.DefaultNameConverter
import spock.lang.Specification

class MirageUtilSpec extends Specification {
    def "getTableName conversion with data"(){
        expect:
            MirageUtil.getTableName(what,converter) == result
        where:
            what               | converter                  || result
            Book.class         | new DefaultNameConverter() || "BOOK"
            BookChanged.class  | new DefaultNameConverter() || "book"
    }

    def "getColumnName conversion with data"(){
        expect:
            MirageUtil.getColumnName(operator,clazz,descriptor,converter) == result
        where:
            operator                   | clazz             | descriptor                                        | converter                  || result
            new DefaultEntityOperator()| BookChanged.class | columnNameHelper(BookChanged.class,'id')   | new DefaultNameConverter() || "ID"
            new DefaultEntityOperator()| BookChanged.class | columnNameHelper(BookChanged.class,'name') | new DefaultNameConverter() || "NAME"
    }

    // SELECT
    def "deprecated buildSelectSQL string with data"(){
        expect:
            MirageUtil.buildSelectSQL(descriptor, operator, clazz,converter) == result
        where:
            descriptor            | operator                    |clazz             |converter                  || result
            new BeanDescFactory() | new DefaultEntityOperator() |BookChanged.class |new DefaultNameConverter() || "SELECT * FROM book WHERE ID = ?"
          //new BeanDescFactory() | new DefaultEntityOperator() |Book.class        |new DefaultNameConverter() || "SELECT * FROM book WHERE ID = ?"
    }

    def "buildSelectSQL string with data"(){
        expect:
        MirageUtil.buildSelectSQL(name,descriptor, operator, clazz,converter) == result
        where:
            name |descriptor           |operator                    |clazz                |converter                 || result
            null |new BeanDescFactory()| new DefaultEntityOperator()|BookChanged.class    |new DefaultNameConverter()|| "SELECT * FROM book WHERE ID = ?"
            null |new BeanDescFactory()| new DefaultEntityOperator()|Book.class           |new DefaultNameConverter()|| "SELECT * FROM BOOK WHERE ID = ?"
            null |new BeanDescFactory()| new DefaultEntityOperator()|BookPartial.class    |new DefaultNameConverter()|| "SELECT * FROM BOOK_PARTIAL WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|Map.class            |new DefaultNameConverter()|| "SELECT * FROM book WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|HashMap.class        |new DefaultNameConverter()|| "SELECT * FROM book WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|LinkedHashMap.class  |new DefaultNameConverter()|| "SELECT * FROM book WHERE ID = ?"
    }

    // INSERT
      def "deprecated buildInsertSql String with data"() {
        expect:
            MirageUtil.buildInsertSql(descriptor, operator, clazz,converter,descs) == result
        where:
            descriptor           |operator                    |clazz            |converter                 |descs                        || result
            new BeanDescFactory()| new DefaultEntityOperator()|BookChanged.class|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO book (NAME, YEAR, TITLE) VALUES (?, ?, ?)"
            new BeanDescFactory()| new DefaultEntityOperator()|Book.class       |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO BOOK (NAME, ID, YEAR, TITLE) VALUES (?, ?, ?, ?)"
            new BeanDescFactory()| new DefaultEntityOperator()|BookPartial.class|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO BOOK_PARTIAL (NAME, YEAR, TITLE) VALUES (?, ?, ?)"
    }

    def "buildInsertSql String with data"() {
        expect:
            MirageUtil.buildInsertSql(name, descriptor, operator, entity,converter,descs) == result
        where:
            name |descriptor           |operator                    |entity                           |converter                 |descs                        || result
            null |new BeanDescFactory()| new DefaultEntityOperator()|new BookChanged()                |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO book (NAME, YEAR, TITLE) VALUES (?, ?, ?)"
            null |new BeanDescFactory()| new DefaultEntityOperator()|new Book()                       |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO BOOK (NAME, ID, YEAR, TITLE) VALUES (?, ?, ?, ?)"
            null |new BeanDescFactory()| new DefaultEntityOperator()|new BookPartial()                |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO BOOK_PARTIAL (NAME, YEAR, TITLE) VALUES (?, ?, ?)"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|[name:"",title:"",year:1999]     |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO book (NAME, TITLE, YEAR) VALUES (?, ?, ?)"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|[id:1,name:"",title:"",year:1999]|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "INSERT INTO book (ID, NAME, TITLE, YEAR) VALUES (?, ?, ?, ?)"
    }

    // UPDATE
    def "deprecated buildUpdateSql String with data"() {
        expect:
            MirageUtil.buildUpdateSql(descriptor, operator, clazz,converter,descs) == result
        where:
            descriptor           |operator                    |clazz            |converter                 |descs                        || result
            new BeanDescFactory()| new DefaultEntityOperator()|BookChanged.class|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE book SET NAME = ?, YEAR = ?, TITLE = ? WHERE ID = ? "
            // new BeanDescFactory()| new DefaultEntityOperator()|Book.class       |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE BOOK SET NAME = ?, YEAR = ?, TITLE = ? WHERE ID = ? "
            new BeanDescFactory()| new DefaultEntityOperator()|BookPartial.class|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE BOOK_PARTIAL SET NAME = ?, YEAR = ?, TITLE = ? WHERE ID = ? "
    }

    def "buildUpdateSql String with data"() {
        expect:
            MirageUtil.buildUpdateSql(name, descriptor, operator, entity,converter,descs) == result
        where:
            name |descriptor           |operator                    |entity                           |converter                 |descs                        || result
            null |new BeanDescFactory()| new DefaultEntityOperator()|new BookChanged()                |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE book SET NAME = ?, YEAR = ?, TITLE = ? WHERE ID = ?"
          //null |new BeanDescFactory()| new DefaultEntityOperator()|new Book()                       |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE BOOK SET NAME = ?, YEAR = ?, TITLE = ? WHERE ID = ?"
            null |new BeanDescFactory()| new DefaultEntityOperator()|new BookPartial()                |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE BOOK_PARTIAL SET NAME = ?, YEAR = ?, TITLE = ? WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|[id:1,name:"a"]                  |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE book SET NAME = ? WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|[id:1,name:"a",title:"",year:199]|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "UPDATE book SET NAME = ?, TITLE = ?, YEAR = ? WHERE ID = ?"
    }

    // DELETE
    def "deprecated buildDeleteSql String with data"() {
        expect:
            MirageUtil.buildDeleteSql(descriptor, operator, clazz,converter,descs) == result
        where:
            descriptor           |operator                    |clazz            |converter                 |descs                        || result
            new BeanDescFactory()| new DefaultEntityOperator()|BookChanged.class|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM book WHERE ID = ?"
          //new BeanDescFactory()| new DefaultEntityOperator()|Book.class       |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM BOOK WHERE ID = ?"
            new BeanDescFactory()| new DefaultEntityOperator()|BookPartial.class|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM BOOK_PARTIAL WHERE ID = ?"
    }

    def "buildDeleteSql String with data"() {
        expect:
            MirageUtil.buildDeleteSql(name, descriptor, operator, entity,converter,descs) == result
        where:
            name |descriptor           |operator                    |entity                           |converter                 |descs                        || result
            null |new BeanDescFactory()| new DefaultEntityOperator()|new BookChanged()                |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM book WHERE ID = ?"
            // null |new BeanDescFactory()| new DefaultEntityOperator()|new Book()                       |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM BOOK WHERE ID = ?"
            null |new BeanDescFactory()| new DefaultEntityOperator()|new BookPartial()                |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM BOOK_PARTIAL WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|[id:1,name:"a"]                  |new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM book WHERE ID = ?"
           "book"|new BeanDescFactory()| new DefaultEntityOperator()|[id:1,name:"a",title:"",year:199]|new DefaultNameConverter()|new ArrayList<PropertyDesc>()|| "DELETE FROM book WHERE ID = ?"
    }

    def private columnNameHelper(Class clazz, String column){
        BeanDescFactory bdf = new BeanDescFactory()
        BeanDesc beanDesc = bdf.getBeanDesc(clazz)
        PropertyDesc pd = beanDesc.getPropertyDesc(column);
    }
}

// Entity NOT annotated at all
class Book {
    Long    id
    String  name
    String  title
    Integer year
}

// Entity partially annotated
class BookPartial {
    @PrimaryKey(generationType=PrimaryKey.GenerationType.IDENTITY) Long id
    String  name
    String  title
    Integer year
}

// Entity annotated, but with default name changed.
@Table(name = "book") class BookChanged {
    @PrimaryKey(generationType=PrimaryKey.GenerationType.IDENTITY) Long id
    String  name
    String  title
    Integer year
}