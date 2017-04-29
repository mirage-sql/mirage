package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongPrimitiveValueType extends AbstractValueType<Long> {


	public LongPrimitiveValueType() {
		super(Long.TYPE);
	}

	public Long get(Class<? extends Long> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	public Long get(Class<? extends Long> type, ResultSet rs, String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	public void set(Class<? extends Long> type, PreparedStatement stmt, Long value,
			int index) throws SQLException {
		if (value == null){
			setNull(type, stmt, index);
			return;
		} else {
			stmt.setLong(index, value);
		}
	}

	public Long get(Class<? extends Long> type, CallableStatement cs, int index) throws SQLException {
		Long value = cs.getLong(index);

		if (value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

	public Long get(Class<? extends Long> type, CallableStatement cs, String parameterName) throws SQLException {
		Long value = cs.getLong(parameterName);

		if (value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

	@Override
	public Long getDefaultValue(){
		return 0L;
	}

}
