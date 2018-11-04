package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanPrimitiveValueType extends AbstractValueType<Boolean> {


    public BooleanPrimitiveValueType() {
        super(Boolean.TYPE);
    }

    public Boolean get(Class<? extends Boolean> type, ResultSet rs, int columnIndex) throws SQLException {
        return rs.getBoolean(columnIndex);
    }

    public Boolean get(Class<? extends Boolean> type, ResultSet rs, String columnName) throws SQLException {
        return rs.getBoolean(columnName);
    }

    public void set(Class<? extends Boolean> type, PreparedStatement stmt, Boolean value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setBoolean(index, value);
        }
    }

    public Boolean get(Class<? extends Boolean> type, CallableStatement cs, int index) throws SQLException {
        boolean value = cs.getBoolean(index);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    public Boolean get(Class<? extends Boolean> type, CallableStatement cs, String parameterName) throws SQLException {
        boolean value = cs.getBoolean(parameterName);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Boolean getDefaultValue(){
        return false;
    }

}
