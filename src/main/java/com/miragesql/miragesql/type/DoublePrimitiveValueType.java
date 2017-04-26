package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoublePrimitiveValueType extends AbstractValueType<Double> {


	public DoublePrimitiveValueType() {
		super(Double.TYPE);
	}

	public Double get(Class<? extends Double> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	public Double get(Class<? extends Double> type, ResultSet rs, String columnName) throws SQLException {
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
		Double value = cs.getDouble(index);

		if (value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

	public Double get(Class<? extends Double> type, CallableStatement cs, String parameterName) throws SQLException {
		Double value = cs.getDouble(parameterName);

		if (value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

	@Override
	public Double getDefaultValue(){
		return 0d;
	}

}
