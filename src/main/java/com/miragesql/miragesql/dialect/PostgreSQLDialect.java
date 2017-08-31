package com.miragesql.miragesql.dialect;

import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.type.PostgreResultSetValueType;
import com.miragesql.miragesql.type.ValueType;

// TODO LargeObject support
public class PostgreSQLDialect extends StandardDialect {

    private PostgreResultSetValueType valueType = new PostgreResultSetValueType();

    /**{@inheritDoc}**/
    @Override
    public String getName() {
        return "postgresql";
    }

    /**{@inheritDoc}**/
    @Override
    public ValueType<?> getValueType(){
        return valueType;
    }

    /**{@inheritDoc}**/
    @Override
    public String getSequenceSql(String sequenceName) {
        return String.format("SELECT NEXTVAL('%s')", sequenceName);
    }

    /**{@inheritDoc}**/
    @Override
    public boolean supportsGenerationType(GenerationType generationType) {
        if(generationType == GenerationType.IDENTITY){
            return false;
        }
        return true;
    }


}
