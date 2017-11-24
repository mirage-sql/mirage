package com.miragesql.miragesql.bean

import spock.lang.Specification
import org.apache.commons.lang3.builder.ToStringBuilder

class BeanDescFactorySpec extends Specification {
    public "getBeanDesc Obj with Data"() {
        when:
            BeanDescFactory factory = new BeanDescFactory()
            def desc = factory.getBeanDesc(obj)
            println "desc:"+ToStringBuilder.reflectionToString(desc)
        then:
            desc.propertyDescSize == size
        where:
            obj               || size
            new BookChanged() || 5
            new BookRaw()     || 5
            [id:1,name:"a"]   || 2
    }

    public "getBeanDesc Class with Data"() {
        when:
            BeanDescFactory factory = new BeanDescFactory()
            def desc = factory.getBeanDesc(clazz)
            println "desc:"+ToStringBuilder.reflectionToString(desc)
        then:
            desc.propertyDescSize == size
        where:
            clazz               || size
            BookChanged.class   || 5
            BookRaw.class       || 5
            Map.class           || 0
            LinkedHashMap.class || 0
    }

}