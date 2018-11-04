package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongValueType extends AbstractValueType<Long> {

    public LongValueType() {
        super(Long.class);
    }

    public Long get(Class<? extends Long> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return rs.getLong(columnIndex);
    }

    public Long get(Class<? extends Long> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
        return rs.getLong(columnName);
    }

    public void set(Class<? extends Long> type, PreparedStatement stmt, Long value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setLong(index, (Long) value);
        }
    }

    public Long get(Class<? extends Long> type, CallableStatement cs, int index) throws SQLException {
        long value = cs.getLong(index);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    public Long get(Class<? extends Long> type, CallableStatement cs, String parameterName) throws SQLException {
        long value =  cs.getLong(parameterName);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }
}
