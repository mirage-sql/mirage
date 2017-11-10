package com.miragesql.miragesql.util

import com.miragesql.miragesql.DefaultEntityOperator
import com.miragesql.miragesql.annotation.PrimaryKey
import com.miragesql.miragesql.annotation.Table
import com.miragesql.miragesql.bean.BeanDesc
import com.miragesql.miragesql.bean.BeanDescFactory
import com.miragesql.miragesql.bean.PropertyDesc
import com.miragesql.miragesql.naming.DefaultNameConverter
import spock.lang.Specification

class MirageUtilTest extends Specification {
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
            operator                   |clazz            |descriptor                                        |converter                  ||result
            new DefaultEntityOperator()|BookChanged.class|columnNameHelper(BookChanged.class,'id')   |new DefaultNameConverter() ||"ID"
            new DefaultEntityOperator()|BookChanged.class|columnNameHelper(BookChanged.class,'name') |new DefaultNameConverter() ||"NAME"
    }

    def "buildSelectSQL string with data"(){
        expect:
            MirageUtil.buildSelectSQL(descriptor, operator, clazz,converter) == result
        where:
            descriptor|operator|clazz|converter||result
            new BeanDescFactory()| new DefaultEntityOperator()|BookChanged.class|new DefaultNameConverter()|| "SELECT * FROM book WHERE ID = ?"
    }

    def "buildInsertSql String with data"() {

    }
    def private columnNameHelper(Class clazz, String column){
        BeanDescFactory bdf = new BeanDescFactory()
        BeanDesc beanDesc = bdf.getBeanDesc(clazz)
        PropertyDesc pd = beanDesc.getPropertyDesc(column);
    }
}

// not annotated entity
class Book {
    String name
}

// annotated entity
@Table(name = "book") class BookChanged {
    @PrimaryKey(generationType=PrimaryKey.GenerationType.IDENTITY) Long id
    String name
}