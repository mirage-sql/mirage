package com.miragesql.miragesql.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.miragesql.miragesql.bean.PropertyDesc;

public interface ValueType<T> {

	public boolean isSupport(Class<?> type, PropertyDesc propertyDesc);

	public T getDefaultValue();

	/**
	 * Returns the value with the type.
	 *
	 * @param type the type class
	 * @param rs the result set
	 * @param index the index column
	 * @return T
	 *
	 * @throws SQLException if something goes wrong
	 * @throws UnsupportedOperationException for unsupported operations
	 */
	public T get(Class<? extends T> type, ResultSet rs, int index) throws SQLException;

	/**
	 * Returns the value with the type.
	 *
	 * @param type the type class
	 * @param rs the result set
	 * @param columnName the index column
	 * @return T
	 *
	 * @throws SQLException if something goes wrong
	 * @throws UnsupportedOperationException for unsupported operations
	 */
	public T get(Class<? extends T> type, ResultSet rs, String columnName) throws SQLException;

	public T get(Class<? extends T> type, CallableStatement cs, int index) throws SQLException;

	public T get(Class<? extends T> type, CallableStatement cs, String parameterName) throws SQLException;

	/**
	 * Sets the value with the type.
	 *
	 * @param type the entity type
	 * @param stmt the prepared statement
	 * @param value the value to set
	 * @param index the column index
	 *
	 * @throws SQLException if something goes wrong
	 * @throws UnsupportedOperationException for unsupported operations
	 */
	public void set(Class<? extends T> type, PreparedStatement stmt, T value, int index) throws SQLException;

	public Class<?> getJavaType(int sqlType);

    public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException;

    public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName) throws SQLException;

}
