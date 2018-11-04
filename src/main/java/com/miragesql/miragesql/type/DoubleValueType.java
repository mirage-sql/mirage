package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleValueType extends AbstractValueType<Double> {


    public DoubleValueType() {
        super(Double.class);
    }

    public Double get(Class<? extends Double> type, ResultSet rs, int columnIndex) throws SQLException {
        if(rs.getObject(columnIndex) == null){
            return null;
        }
        return rs.getDouble(columnIndex);
    }

    public Double get(Class<? extends Double> type, ResultSet rs, String columnName) throws SQLException {
        if(rs.getObject(columnName) == null){
            return null;
        }
        return rs.getDouble(columnName);
    }

    public void set(Class<? extends Double> type, PreparedStatement stmt, Double value,
            int index) throws SQLException {
        if (value == null){
            setNull(type, stmt, index);
        } else {
            stmt.setDouble(index, (Double) value);
        }
    }

    public Double get(Class<? extends Double> type, CallableStatement cs, int index) throws SQLException {
        double value = cs.getDouble(index);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    public Double get(Class<? extends Double> type, CallableStatement cs, String parameterName) throws SQLException {
        double value = cs.getDouble(parameterName);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }
}
