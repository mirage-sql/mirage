package com.miragesql.miragesql.naming

import spock.lang.Shared
import spock.lang.Specification

class DefaultNameConverterSpec extends Specification {
    @Shared converter = new DefaultNameConverter()

    def "column to property with data"(){
        expect:
            converter.columnToProperty(column) == property
        where:
            column             || property
            "USER_ID"          || "userId"
            "user_id"          || "userId"
            "USER"             || "user"
            "user"             || "user"
            "user_"            || "user"
    }

    // todo: these should work too:
    def "column to property with data errors"(){
        expect:
            converter.columnToProperty(column) != property
        where:
            column             || property
            "_user"            || "user"
    }

    def "property to column with data"(){
        expect:
            converter.propertyToColumn(what) == result
        where:
            what               || result
            "userId"           || "USER_ID"
    }

    def "entity to table with data"(){
        expect:
            converter.entityToTable(what) == result
        where:
            what                 || result
            "UserInfo"           || "USER_INFO"
            "entity.UserInfo"    || "USER_INFO"
    }

    // todo: these should work too:
    def "entity to table with data errors"(){
        expect:
            converter.entityToTable(what) != result
        where:
            what                 || result
            "UserInfo\$2"        || "USER_INFO_2"
            "UserInfo\$Detail"   || "USER_INFO_DETAIL"
            "entity.UserInfo\$1" || "USER_INFO_1"
    }


}
