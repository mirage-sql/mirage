package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerValueType extends AbstractValueType<Integer> {

    public IntegerValueType() {
        super(Integer.class);
    }

    public Integer get(Class<? extends Integer> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return rs.getInt(columnIndex);
    }

    public Integer get(Class<? extends Integer> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
        return rs.getInt(columnName);
    }

    public void set(Class<? extends Integer> type, PreparedStatement stmt, Integer value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setInt(index, value);
        }
    }

    public Integer get(Class<? extends Integer> type, CallableStatement cs, int index) throws SQLException {
        int value = cs.getInt(index);

        if (cs.wasNull()) {
           return null;
        }

        return value;
    }

    public Integer get(Class<? extends Integer> type, CallableStatement cs, String parameterName) throws SQLException {
        int value = cs.getInt(parameterName);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }
}
