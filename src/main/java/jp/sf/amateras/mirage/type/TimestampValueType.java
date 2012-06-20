package jp.sf.amateras.mirage.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampValueType extends AbstractValueType<Timestamp> {

	
	public TimestampValueType() {
		super(Timestamp.class);
	}
	
	public Timestamp get(Class<? extends Timestamp> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	public Timestamp get(Class<? extends Timestamp> type, ResultSet rs, String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	public void set(Class<? extends Timestamp> type, PreparedStatement stmt, Timestamp value,
			int index) throws SQLException {
		if (value == null){
			setNull(type, stmt, index);
		} else {
			stmt.setTimestamp(index, value);
		}
	}

	public Timestamp get(Class<? extends Timestamp> type, CallableStatement cs, int index) throws SQLException {
		return cs.getTimestamp(index);
	}

	public Timestamp get(Class<? extends Timestamp> type, CallableStatement cs, String parameterName) throws SQLException {
		return cs.getTimestamp(parameterName);
	}
}
