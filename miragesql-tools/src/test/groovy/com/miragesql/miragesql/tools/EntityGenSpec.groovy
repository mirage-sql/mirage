package com.miragesql.miragesql.tools

import spock.lang.Specification

class EntityGenSpec extends Specification {
    def "validateLang() valid data"() {
        expect:
            EntityGen.validateLang(lang) == result

        where:
            lang     || result
            "java"   || true
            "groovy" || true
            "xml"    || true
    }

    def "validateLang() invalid data"() {
        when:
            EntityGen.validateLang(lang)

        then:
            def error = thrown(expectedException)
            error.message == expectedMessage

        where:
            lang     || expectedException          | expectedMessage
            null     || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            ""       || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            "Java"   || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            "Groovy" || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            "scala"  || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            "edn"    || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            "puml"   || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
            "json"   || IllegalArgumentException   | "Argument 'lang' must be 'java', 'groovy' or 'xml' only!"
    }

}
