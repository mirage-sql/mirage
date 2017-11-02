package com.miragesql.miragesql.util

import com.miragesql.miragesql.annotation.Table
import com.miragesql.miragesql.naming.DefaultNameConverter
import spock.lang.Specification

class MirageUtilTest extends Specification {
    def "table name conversion with data"(){
        expect:
            MirageUtil.getTableName(what,converter) == result
        where:
            what               | converter                  || result
            Book.class         | new DefaultNameConverter() || "BOOK"
            BookChanged.class  | new DefaultNameConverter() || "book"
    }
}

// not annotated entity
class Book {
    String name
}

// annotated entity
@Table(name = "book") class BookChanged {
    String name
}