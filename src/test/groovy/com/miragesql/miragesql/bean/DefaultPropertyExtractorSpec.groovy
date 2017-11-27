package com.miragesql.miragesql.bean

import spock.lang.Specification

class DefaultPropertyExtractorSpec extends  Specification {
    def "extractProperties with data"() {
        when:
            DefaultPropertyExtractor dpe = new DefaultPropertyExtractor()
        then:
            dpe.extractProperties(clazz).keySet() == result
        where:
            clazz            ||  result
            BookRaw.class    ||["id","metaClass","name","title","year"] as Set
            BookChanged.class||["id","metaClass","name","title","year"] as Set
    }
}
