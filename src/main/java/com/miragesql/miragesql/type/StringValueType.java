package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringValueType extends AbstractValueType<String> {
	
	public StringValueType() {
		super(String.class);
	}
	
	public String get(Class<? extends String> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}
	
	public String get(Class<? extends String> type, ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}
	
	public void set(Class<? extends String> type, PreparedStatement stmt, String value, int index) throws SQLException {
		if (value == null) {
			setNull(type, stmt, index);
		} else {
			stmt.setString(index, (String) value);
		}
	}
	
	public String get(Class<? extends String> type, CallableStatement cs, int index) throws SQLException {
		return cs.getString(index);
	}
	
	public String get(Class<? extends String> type, CallableStatement cs, String parameterName) throws SQLException {
		return cs.getString(parameterName);
	}
}
