package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortValueType extends AbstractValueType<Short> {


    public ShortValueType() {
        super(Short.class);
    }

    public Short get(Class<? extends Short> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return rs.getShort(columnIndex);
    }

    public Short get(Class<? extends Short> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
        return rs.getShort(columnName);
    }

    public void set(Class<? extends Short> type, PreparedStatement stmt, Short value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
            return;
        } else {
            stmt.setShort(index, value);
        }
    }

    public Short get(Class<? extends Short> type, CallableStatement cs, int index) throws SQLException {
        short value = cs.getShort(index);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    public Short get(Class<? extends Short> type, CallableStatement cs, String parameterName) throws SQLException {
        short value = cs.getShort(parameterName);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }
}
