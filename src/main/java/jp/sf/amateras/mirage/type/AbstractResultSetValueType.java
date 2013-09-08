package jp.sf.amateras.mirage.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import jp.sf.amateras.mirage.bean.PropertyDesc;

public class AbstractResultSetValueType implements ValueType<Object> {

    private int sqlType;

    public AbstractResultSetValueType(int sqlType) {
        this.sqlType = sqlType;
    }

//	@Override
    public Object getDefaultValue() {
		return null;
	}

//	@Override
	public Object get(Class<?> type, ResultSet rs, int index) throws SQLException {
		throw new UnsupportedOperationException("not supported");
	}

//	@Override
	public Object get(Class<?> type, ResultSet rs, String columnName) throws SQLException {
		throw new UnsupportedOperationException("not supported");
	}

//	@Override
	public Object get(Class<?> type, CallableStatement cs, int index) throws SQLException {
		return cs.getObject(index);
	}

//	@Override
	public Object get(Class<?> type, CallableStatement cs, String parameterName) throws SQLException {
		return cs.getObject(parameterName);
	}

//	@Override
	public boolean isSupport(Class<?> type, PropertyDesc propertyDesc) {
		if (List.class.isAssignableFrom(type)){
			return true;
		}
		return false;
	}

//	@Override
	public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException {
		cs.registerOutParameter(index, sqlType);
	}

//	@Override
	public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName) throws SQLException {
		cs.registerOutParameter(parameterName, sqlType);
	}

//	@Override
	public void set(Class<?> type, PreparedStatement stmt, Object value, int index) throws SQLException {
		throw new UnsupportedOperationException("not supported");
	}

	public Class<?> getJavaType(int sqlType) {
		return null;
	}
}