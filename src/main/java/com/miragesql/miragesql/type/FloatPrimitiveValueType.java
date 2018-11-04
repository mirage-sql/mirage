package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatPrimitiveValueType extends AbstractValueType<Float> {


    public FloatPrimitiveValueType() {
        super(Float.TYPE);
    }

    public Float get(Class<? extends Float> type, ResultSet rs, int columnIndex) throws SQLException {
        return rs.getFloat(columnIndex);
    }

    public Float get(Class<? extends Float> type, ResultSet rs, String columnName) throws SQLException {
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
        float value = cs.getFloat(index);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    public Float get(Class<? extends Float> type, CallableStatement cs, String parameterName) throws SQLException {
        float value = cs.getFloat(parameterName);

        if (cs.wasNull()) {
            return null;
        }

        return value;
    }

    @Override
    public Float getDefaultValue(){
        return 0f;
    }

}
