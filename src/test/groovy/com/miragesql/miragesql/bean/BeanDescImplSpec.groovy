package com.miragesql.miragesql.bean

import com.miragesql.miragesql.annotation.PrimaryKey
import com.miragesql.miragesql.annotation.Table
import spock.lang.Specification

class BeanDescImplSpec extends Specification {
    def "Bean class checking with data"() {
        when:
            BeanDescFactory factory = new BeanDescFactory()
            BeanDesc bd = factory.getBeanDesc(BookRaw.class);
        then:
            bd.getPropertyDesc(name)?.getPropertyName() == result
        where:
            name    || result
            "id"    || "id"
            "name"  || "name"
            "title" || "title"
            "year"  || "year"
    }

    def "Bean object checking with data"() {
        when:
            BeanDescFactory factory = new BeanDescFactory()
            // BookRaw br = new BookRaw(id:1,name:"a",title: "b", year: 1999)
            BookRaw br = new BookRaw()
            BeanDesc bd = factory.getBeanDesc(br);
            // BeanDesc bd = new BeanDescImpl(BookRaw.class, propertyExtractor.extractProperties(BookRaw.class));
        then:
            bd.getPropertyDesc(name)?.getPropertyName() == result
        where:
            name    || result
            "id"    || "id"
            "name"  || "name"
            "title" || "title"
            "year"  || "year"
    }

    def "Map checking with data"() {
        when:
            BeanDescFactory factory = new BeanDescFactory()
            def br = [id:1,name:"a",title: "b", year: 1999]
            // BookRaw br = new BookRaw(id:1,name:"a",title: "b", year: 1999)
            // BookRaw br = new BookRaw()
            BeanDesc bd = factory.getBeanDesc(br);
            // BeanDesc bd = new BeanDescImpl(BookRaw.class, propertyExtractor.extractProperties(BookRaw.class));
        then:
            bd.getPropertyDesc(name)?.getPropertyName() == result
        where:
            name    || result
            "id"    || "id"
            "name"  || "name"
            "title" || "title"
            "year"  || "year"
    }
}

// not annotated entity
class BookRaw {
    Long    id
    String  name
    String  title
    Integer year
}

// annotated entity
@Table(name = "book") class BookChanged {
    @PrimaryKey(generationType=PrimaryKey.GenerationType.IDENTITY) Long id
    String  name
    String  title
    Integer year
}

class BookOther{
    Long    id
    String  title
    String  name
    Integer age
}