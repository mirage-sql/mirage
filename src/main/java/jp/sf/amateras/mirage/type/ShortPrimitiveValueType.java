package jp.sf.amateras.mirage.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortPrimitiveValueType extends AbstractValueType<Short> {


	public ShortPrimitiveValueType() {
		super(Short.TYPE);
	}

	public Short get(Class<? extends Short> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	public Short get(Class<? extends Short> type, ResultSet rs, String columnName) throws SQLException {
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
		Short value = cs.getShort(index);

		if (value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

	public Short get(Class<? extends Short> type, CallableStatement cs, String parameterName) throws SQLException {
		Short value = cs.getShort(parameterName);

		if (value != null && cs.wasNull()) {
			value = null;
		}

		return value;
	}

	@Override
	public Short getDefaultValue(){
		return 0;
	}

}
