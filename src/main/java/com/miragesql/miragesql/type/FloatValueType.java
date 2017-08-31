package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatValueType extends AbstractValueType<Float> {


    public FloatValueType() {
        super(Float.class);
    }

    public Float get(Class<? extends Float> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return rs.getFloat(columnIndex);
    }

    public Float get(Class<? extends Float> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
        return rs.getFloat(columnName);
    }

    public void set(Class<? extends Float> type, PreparedStatement stmt, Float value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setFloat(index, (Float) value);
        }
    }

    public Float get(Class<? extends Float> type, CallableStatement cs, int index) throws SQLException {
        Float value = cs.getFloat(index);

        if (value != null && cs.wasNull()) {
            value = null;
        }

        return value;
    }

    public Float get(Class<? extends Float> type, CallableStatement cs, String parameterName) throws SQLException {
        Float value = cs.getFloat(parameterName);

        if (value != null && cs.wasNull()) {
            value = null;
        }

        return value;
    }
}
