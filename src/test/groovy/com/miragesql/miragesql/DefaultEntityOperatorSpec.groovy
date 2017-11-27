package com.miragesql.miragesql

import com.miragesql.miragesql.annotation.PrimaryKey
import com.miragesql.miragesql.annotation.Table
import com.miragesql.miragesql.bean.BeanDesc
import com.miragesql.miragesql.bean.BeanDescFactory
import com.miragesql.miragesql.naming.DefaultNameConverter
import spock.lang.Specification


class DefaultEntityOperatorSpec extends Specification {
    def "getPrimaryKeyInfo with data" () {
        when:
            def d = new DefaultEntityOperator()
            BeanDescFactory factory = new BeanDescFactory()
            BeanDesc bd = factory.getBeanDesc(entity);
            def pd = bd.getPropertyDesc(name)
        then:
            d.getPrimaryKeyInfo(entity.getClass(), pd, converter)?.generationType == result
        where:
            entity              |name|converter                     || result
            new BookChanged()         |"id"|new DefaultNameConverter()    || PrimaryKey.GenerationType.IDENTITY
            new Book()                |"id"|new DefaultNameConverter()    || null // until this works for un-annotated entities too.
            new BookPartial()         |"id"|new DefaultNameConverter()    || PrimaryKey.GenerationType.IDENTITY
            [id:1,name:"a"]           |"id"|new DefaultNameConverter()    || PrimaryKey.GenerationType.IDENTITY
            [id:1,name:"a"] as Map    |"id"|new DefaultNameConverter()    || PrimaryKey.GenerationType.IDENTITY
            [id:1,name:"a"] as HashMap|"id"|new DefaultNameConverter()    || PrimaryKey.GenerationType.IDENTITY
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