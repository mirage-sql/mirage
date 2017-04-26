package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlDateValueType extends AbstractValueType<java.sql.Date> {

	
	public SqlDateValueType() {
		super(java.sql.Date.class);
	}
	
	public java.sql.Date get(Class<? extends java.sql.Date> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	public java.sql.Date get(Class<? extends java.sql.Date> type, ResultSet rs, String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	public void set(Class<? extends java.sql.Date> type, PreparedStatement stmt, java.sql.Date value,
			int index) throws SQLException {
		if (value == null){
			setNull(type, stmt, index);
		} else {
			stmt.setDate(index, (java.sql.Date) value);
		}
	}

	public java.sql.Date get(Class<? extends java.sql.Date> type, CallableStatement cs, int index) throws SQLException {
		return cs.getDate(index);
	}

	public java.sql.Date get(Class<? extends java.sql.Date> type, CallableStatement cs, String parameterName) throws SQLException {
		return cs.getDate(parameterName);
	}
}
