package com.miragesql.miragesql.type;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.util.Validate;

public abstract class AbstractValueType<T> implements ValueType<T> {

    protected static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    private static Map<Class<?>, Integer> javaTypeToSqlTypeMap = new HashMap<>(32);

    static {
        javaTypeToSqlTypeMap.put(byte.class, new Integer(Types.TINYINT));
        javaTypeToSqlTypeMap.put(Byte.class, new Integer(Types.TINYINT));
        javaTypeToSqlTypeMap.put(short.class, new Integer(Types.SMALLINT));
        javaTypeToSqlTypeMap.put(Short.class, new Integer(Types.SMALLINT));
        javaTypeToSqlTypeMap.put(int.class, new Integer(Types.INTEGER));
        javaTypeToSqlTypeMap.put(Integer.class, new Integer(Types.INTEGER));
        javaTypeToSqlTypeMap.put(long.class, new Integer(Types.BIGINT));
        javaTypeToSqlTypeMap.put(Long.class, new Integer(Types.BIGINT));
        javaTypeToSqlTypeMap.put(BigInteger.class, new Integer(Types.BIGINT));
        javaTypeToSqlTypeMap.put(float.class, new Integer(Types.FLOAT));
        javaTypeToSqlTypeMap.put(Float.class, new Integer(Types.FLOAT));
        javaTypeToSqlTypeMap.put(double.class, new Integer(Types.DOUBLE));
        javaTypeToSqlTypeMap.put(Double.class, new Integer(Types.DOUBLE));
        javaTypeToSqlTypeMap.put(BigDecimal.class, new Integer(Types.DECIMAL));
        javaTypeToSqlTypeMap.put(java.sql.Date.class, new Integer(Types.DATE));
        javaTypeToSqlTypeMap.put(java.sql.Time.class, new Integer(Types.TIME));
        javaTypeToSqlTypeMap.put(java.sql.Timestamp.class, new Integer(Types.TIMESTAMP));
        javaTypeToSqlTypeMap.put(Blob.class, new Integer(Types.BLOB));
        javaTypeToSqlTypeMap.put(byte[].class, new Integer(Types.BLOB));
        javaTypeToSqlTypeMap.put(Clob.class, new Integer(Types.CLOB));
    }


    public static int javaTypeToSqlType(Class<?> javaType) {
        Integer sqlType = javaTypeToSqlTypeMap.get(javaType);
        if (sqlType != null) {
            return sqlType.intValue();
        }
        if (Number.class.isAssignableFrom(javaType)) {
            return Types.NUMERIC;
        }
        if (isStringValue(javaType)) {
            return Types.VARCHAR;
        }
        if (isDateValue(javaType) || Calendar.class.isAssignableFrom(javaType)) {
            return Types.TIMESTAMP;
        }
        return TYPE_UNKNOWN;
    }

    private static boolean isStringValue(Class<?> inValueType) {
        return (CharSequence.class.isAssignableFrom(inValueType) || StringWriter.class.isAssignableFrom(inValueType));
    }

    private static boolean isDateValue(Class<?> inValueType) {
        return (java.util.Date.class.isAssignableFrom(inValueType) && !(java.sql.Date.class
            .isAssignableFrom(inValueType) || java.sql.Time.class.isAssignableFrom(inValueType) || java.sql.Timestamp.class
            .isAssignableFrom(inValueType)));
    }

    protected static boolean isPrimitive(Class<?> type) {
        if (type == Integer.TYPE || type == Long.TYPE || type == Short.TYPE || type == Double.TYPE
                || type == Float.TYPE || type == Boolean.TYPE) {
            return true;
        }
        return false;
    }


    private final Class<T> type;


    public AbstractValueType(Class<T> type) {
        Validate.notNull(type);
        this.type = type;
    }

    public boolean isSupport(Class<?> type, PropertyDesc propertyDesc) {
        return this.type == type;
    }

    public Class<?> getJavaType(int sqlType) {
        for(Map.Entry<Class<?>, Integer> entry: javaTypeToSqlTypeMap.entrySet()){
            if(sqlType == entry.getValue().intValue() && !isPrimitive(entry.getKey())){
                return entry.getKey();
            }
        }
        return null;
    }

    public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException {
        int sqlType = javaTypeToSqlType(type);
        cs.registerOutParameter(index, sqlType);
    }

    public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName)
            throws SQLException {
        int sqlType = javaTypeToSqlType(type);
        cs.registerOutParameter(parameterName, sqlType);
    }

    protected void setNull(Class<?> type, PreparedStatement stmt, int index) throws SQLException {
        int sqlType = javaTypeToSqlType(type);
        stmt.setNull(index, sqlType);
    }

    public T getDefaultValue() {
        return null;
    }
}
