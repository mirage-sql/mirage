package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UtilDateValueType extends AbstractValueType<java.util.Date> {

    public UtilDateValueType() {
        super(java.util.Date.class);
    }

    public java.util.Date get(Class<? extends java.util.Date> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return new java.util.Date(rs.getTimestamp(columnIndex).getTime());
    }

    public java.util.Date get(Class<? extends java.util.Date> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
        return new java.util.Date(rs.getTimestamp(columnName).getTime());
    }

    public void set(Class<? extends java.util.Date> type, PreparedStatement stmt, java.util.Date value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setTimestamp(index, new Timestamp(value.getTime()));
        }
    }

    public java.util.Date get(Class<? extends java.util.Date> type, CallableStatement cs, int index) throws SQLException {
        return new java.util.Date(cs.getTimestamp(index).getTime());
    }

    public java.util.Date get(Class<? extends java.util.Date> type, CallableStatement cs, String parameterName) throws SQLException {
        return new java.util.Date(cs.getTimestamp(parameterName).getTime());
    }
}
