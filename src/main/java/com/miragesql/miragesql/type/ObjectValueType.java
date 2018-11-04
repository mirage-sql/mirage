package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectValueType extends AbstractValueType<Object> {

    public ObjectValueType() {
        super(Object.class);
    }

    public Object get(Class<? extends Object> type, ResultSet rs, int columnIndex) throws SQLException {
        return rs.getObject(columnIndex);
    }

    public Object get(Class<? extends Object> type, ResultSet rs, String columnName) throws SQLException {
        return rs.getObject(columnName);
    }

    public void set(Class<? extends Object> type, PreparedStatement stmt, Object value, int index) throws SQLException {
        if (value == null) {
            setNull(type, stmt, index);
        } else {
            stmt.setObject(index, value);
        }
    }

    public Object get(Class<? extends Object> type, CallableStatement cs, int index) throws SQLException {
        return cs.getObject(index);
    }

    public Object get(Class<? extends Object> type, CallableStatement cs, String parameterName) throws SQLException {
        return cs.getObject(parameterName);
    }
}
