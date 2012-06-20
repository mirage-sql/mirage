package jp.sf.amateras.mirage.type;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalValueType extends AbstractValueType<BigDecimal> {

	
	public BigDecimalValueType() {
		super(BigDecimal.class);
	}
	
	public BigDecimal get(Class<? extends BigDecimal> type, ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	public BigDecimal get(Class<? extends BigDecimal> type, ResultSet rs, String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	public void set(Class<? extends BigDecimal> type, PreparedStatement stmt, BigDecimal value,
			int index) throws SQLException {
		if (value == null){
			setNull(type, stmt, index);
		} else {
			stmt.setBigDecimal(index, value);
		}
	}

	public BigDecimal get(Class<? extends BigDecimal> type, CallableStatement cs, int index) throws SQLException {
		return cs.getBigDecimal(index);
	}

	public BigDecimal get(Class<? extends BigDecimal> type, CallableStatement cs, String parameterName) throws SQLException {
		return cs.getBigDecimal(parameterName);
	}
}
