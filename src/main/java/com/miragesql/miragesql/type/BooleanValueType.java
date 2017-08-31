package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanValueType extends AbstractValueType<Boolean> {


    public BooleanValueType() {
        super(Boolean.class);
    }

    public Boolean get(Class<? extends Boolean> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return rs.getBoolean(columnIndex);
    }

    public Boolean get(Class<? extends Boolean> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
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
        Boolean value = cs.getBoolean(index);

        if (value != null && cs.wasNull()) {
            value = null;
        }

        return value;
    }

    public Boolean get(Class<? extends Boolean> type, CallableStatement cs, String parameterName) throws SQLException {
        Boolean value = cs.getBoolean(parameterName);

        if (value != null && cs.wasNull()) {
            value = null;
        }

        return value;
    }
}
