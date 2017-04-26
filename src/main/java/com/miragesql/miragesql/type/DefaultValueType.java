package com.miragesql.miragesql.type;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.util.IOUtil;

@Deprecated
public class DefaultValueType implements ValueType<Object> {

	private static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

	private static Map<Class<?>, Integer> javaTypeToSqlTypeMap = new HashMap<Class<?>, Integer>(32);

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

//	@Override
	public Object get(Class<?> type, ResultSet rs, int columnIndex) throws SQLException {
		if(type == String.class){
			return rs.getString(columnIndex);

		} else if(type == Integer.class){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return rs.getInt(columnIndex);

		} else if(type == Integer.TYPE){
			return rs.getInt(columnIndex);

		} else if(type == Long.class){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return rs.getLong(columnIndex);

		} else if(type == Long.TYPE){
			return rs.getLong(columnIndex);

		} else if(type == Short.class ){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return rs.getShort(columnIndex);

		} else if(type == Short.TYPE){
			return rs.getShort(columnIndex);

		} else if(type == Double.class){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return rs.getDouble(columnIndex);

		} else if(type == Double.TYPE){
			return rs.getDouble(columnIndex);

		} else if(type == Float.class){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return rs.getFloat(columnIndex);

		} else if(type == Float.TYPE){
			return rs.getFloat(columnIndex);

		} else if(type == Boolean.class){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return rs.getBoolean(columnIndex);

		} else if(type == Boolean.TYPE){
			return rs.getBoolean(columnIndex);

		} else if(type == BigDecimal.class){
			return rs.getBigDecimal(columnIndex);

		} else if(type == java.sql.Date.class){
			return rs.getDate(columnIndex);

		} else if(type == Date.class){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return new Date(rs.getTimestamp(columnIndex).getTime());

		} else if(type == Time.class){
			return rs.getTime(columnIndex);

		} else if(type == Timestamp.class){
			return rs.getTimestamp(columnIndex);

		} else if(type.isArray() && type.getComponentType() == Byte.TYPE){
			if(rs.getObject(columnIndex) == null){
				return null;
			}
			return IOUtil.readStream(rs.getBinaryStream(columnIndex));
		}

		throw new RuntimeException("Unsupported type: " + type.getName());
	}

//	@Override
	public Object get(Class<?> type, ResultSet rs, String columnName) throws SQLException {
		if(type == String.class){
			return rs.getString(columnName);

		} else if(type == Integer.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return rs.getInt(columnName);

		} else if(type == Integer.TYPE){
			return rs.getInt(columnName);

		} else if(type == Long.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return rs.getLong(columnName);

		} else if(type == Long.TYPE){
			return rs.getLong(columnName);

		} else if(type == Short.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return rs.getShort(columnName);

		} else if(type == Short.TYPE){
			return rs.getShort(columnName);

		} else if(type == Double.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return rs.getDouble(columnName);

		} else if(type == Double.TYPE){
			return rs.getDouble(columnName);

		} else if(type == Float.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return rs.getFloat(columnName);

		} else if(type == Float.TYPE){
			return rs.getFloat(columnName);

		} else if(type == Boolean.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return rs.getBoolean(columnName);

		} else if(type == Boolean.TYPE){
			return rs.getBoolean(columnName);

		} else if(type == BigDecimal.class){
			return rs.getBigDecimal(columnName);

		} else if(type == java.sql.Date.class){
			return rs.getDate(columnName);

		} else if(type == Date.class){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return new Date(rs.getTimestamp(columnName).getTime());

		} else if(type == Time.class){
			return rs.getTime(columnName);

		} else if( type == Timestamp.class){
			return rs.getTimestamp(columnName);

		} else if(type.isArray() && type.getComponentType() == Byte.TYPE){
			if(rs.getObject(columnName) == null){
				return null;
			}
			return IOUtil.readStream(rs.getBinaryStream(columnName));
		}

		throw new RuntimeException("Unsupported type: " + type.getName());
	}

//	@Override
	public void set(Class<?> type, PreparedStatement stmt, Object value,
			int index) throws SQLException {
		if (value == null){
			setNull(type, stmt, index);
			return;
		}

		if(type == String.class){
			stmt.setString(index, (String) value);

		} else if(type == Integer.class ){
			stmt.setInt(index, (Integer) value);

		} else if(type == Long.class  || type == Long.TYPE){
			stmt.setLong(index, (Long) value);

		} else if(type == Short.class  || type == Short.TYPE){
			stmt.setShort(index, (Short) value);

		} else if(type == Double.class  || type == Double.TYPE){
			stmt.setDouble(index, (Double) value);

		} else if(type == Float.class  || type == Float.TYPE){
			stmt.setFloat(index, (Float) value);

		} else if(type == Boolean.class  || type == Boolean.TYPE){
			stmt.setBoolean(index, (Boolean) value);

		} else if(type == Boolean.class  || type == Boolean.TYPE){
			stmt.setBoolean(index, (Boolean) value);

		} else if(type == BigDecimal.class){
			stmt.setBigDecimal(index, (BigDecimal) value);

		} else if(type == java.sql.Date.class){
			stmt.setDate(index, (java.sql.Date) value);

		} else if(type == Date.class){
			stmt.setTimestamp(index, new Timestamp(((Date) value).getTime()));

		} else if(type == Time.class){
			stmt.setTime(index, (Time) value);

		} else if(type == Timestamp.class){
			stmt.setTimestamp(index, (Timestamp) value);

		} else if(type.isArray() && type.getComponentType() == Byte.TYPE){
			byte[] bytes = (byte[]) value;
			stmt.setBinaryStream(index, new ByteArrayInputStream(bytes), bytes.length);

		} else {
			throw new RuntimeException("Unsupported type: " + type.getName());
		}
	}

//	@Override
	public Object get(Class<?> type, CallableStatement cs, int index) throws SQLException {
		Object value = null;
		boolean wasNullCheck = false;

		if(type == String.class){
			value = cs.getString(index);

		} else if(type == Integer.class || type == Integer.TYPE){
			value = cs.getInt(index);
			wasNullCheck = true;

		} else if(type == Long.class  || type == Long.TYPE){
			value = cs.getLong(index);
			wasNullCheck = true;

		} else if(type == Short.class  || type == Short.TYPE){
			value = cs.getShort(index);
			wasNullCheck = true;

		} else if(type == Double.class  || type == Double.TYPE){
			value = cs.getDouble(index);
			wasNullCheck = true;

		} else if(type == Float.class  || type == Float.TYPE){
			value = cs.getFloat(index);
			wasNullCheck = true;

		} else if(type == Boolean.class  || type == Boolean.TYPE){
			value = cs.getBoolean(index);
			wasNullCheck = true;

		} else if(type == BigDecimal.class){
			value = cs.getBigDecimal(index);

		} else if(type == java.sql.Date.class){
			value = cs.getDate(index);

		} else if(type == Date.class){
			value = new Date(cs.getTimestamp(index).getTime());

		} else if(type == Time.class){
			value = cs.getTime(index);

		} else if(type == Timestamp.class){
			value = cs.getTimestamp(index);

		} else if(type.isArray() && type.getComponentType() == Byte.TYPE){
			// TODO
			Blob blob = cs.getBlob(index);
			value = IOUtil.readStream(blob.getBinaryStream());
		}

		if (wasNullCheck && value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

//	@Override
	public Object get(Class<?> type, CallableStatement cs, String parameterName) throws SQLException {
		Object value = null;
		boolean wasNullCheck = false;

		if(type == String.class){
			value = cs.getString(parameterName);

		} else if(type == Integer.class || type == Integer.TYPE){
			value = cs.getInt(parameterName);
			wasNullCheck = true;

		} else if(type == Long.class  || type == Long.TYPE){
			value = cs.getLong(parameterName);
			wasNullCheck = true;

		} else if(type == Short.class  || type == Short.TYPE){
			value = cs.getShort(parameterName);
			wasNullCheck = true;

		} else if(type == Double.class  || type == Double.TYPE){
			value = cs.getDouble(parameterName);
			wasNullCheck = true;

		} else if(type == Float.class  || type == Float.TYPE){
			value = cs.getFloat(parameterName);
			wasNullCheck = true;

		} else if(type == Boolean.class  || type == Boolean.TYPE){
			value = cs.getBoolean(parameterName);
			wasNullCheck = true;

		} else if(type == BigDecimal.class){
			value = cs.getBigDecimal(parameterName);

		} else if(type == java.sql.Date.class){
			value = cs.getDate(parameterName);

		} else if(type == Date.class){
			value = new Date(cs.getTimestamp(parameterName).getTime());

		} else if(type == Time.class){
			value = cs.getTime(parameterName);

		} else if(type == Timestamp.class){
			value = cs.getTimestamp(parameterName);

		} else if(type.isArray() && type.getComponentType() == Byte.TYPE){
			Blob blob = cs.getBlob(parameterName);
			value = IOUtil.readStream(blob.getBinaryStream());
		}

		if (wasNullCheck && value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

//	@Override
	public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException {
		int sqlType = javaTypeToSqlType(type);
		cs.registerOutParameter(index, sqlType);
	}

//	@Override
	public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName)
			throws SQLException {
		int sqlType = javaTypeToSqlType(type);
		cs.registerOutParameter(parameterName, sqlType);
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
		return (CharSequence.class.isAssignableFrom(inValueType) ||
				StringWriter.class.isAssignableFrom(inValueType));
	}

	private static boolean isDateValue(Class<?> inValueType) {
		return (java.util.Date.class.isAssignableFrom(inValueType) &&
				!(java.sql.Date.class.isAssignableFrom(inValueType) ||
						java.sql.Time.class.isAssignableFrom(inValueType) ||
						java.sql.Timestamp.class.isAssignableFrom(inValueType)));
	}

    protected void setNull(Class<?> type, PreparedStatement stmt, int index) throws SQLException {
    	int sqlType = javaTypeToSqlType(type);
        stmt.setNull(index, sqlType);
    }

//	@Override
	public boolean isSupport(Class<?> type, PropertyDesc propertyDesc) {
		if(type == String.class
				|| type == Integer.class || type == Integer.TYPE
				|| type == Long.class  || type == Long.TYPE
				|| type == Short.class  || type == Short.TYPE
				|| type == Double.class  || type == Double.TYPE
				|| type == Float.class  || type == Float.TYPE
				|| type == Boolean.class  || type == Boolean.TYPE
				|| type == BigDecimal.class
				|| type == Date.class
				|| type == Time.class
				|| type == Timestamp.class
				|| type.isArray() && type.getComponentType() == Byte.TYPE){
			return true;
		}
		return false;
	}

	public Class<?> getJavaType(int sqlType) {
		for(Map.Entry<Class<?>, Integer> entry: javaTypeToSqlTypeMap.entrySet()){
			if(sqlType == entry.getValue().intValue() && !isPrimitive(entry.getKey())){
				return entry.getKey();
			}
		}
		return null;
	}

	private static boolean isPrimitive(Class<?> type){
		if(type == Integer.TYPE
				|| type == Long.TYPE
				|| type == Short.TYPE
				|| type == Double.TYPE
				|| type == Float.TYPE
				|| type == Boolean.TYPE){
			return true;
		}
		return false;
	}

	public Object getDefaultValue(){
		return null;
	}

}
