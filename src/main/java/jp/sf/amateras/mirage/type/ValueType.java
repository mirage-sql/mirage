package jp.sf.amateras.mirage.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ValueType<T> {

	public boolean isSupport(Class<?> type);

	public T getDefaultValue();

	/**
	 *
	 * @param type
	 * @param rs
	 * @param index
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedOperationException
	 */
	public T get(Class<? extends T> type, ResultSet rs, int index) throws SQLException;

	/**
	 *
	 * @param type
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedOperationException
	 */
	public T get(Class<? extends T> type, ResultSet rs, String columnName) throws SQLException;

	public T get(Class<? extends T> type, CallableStatement cs, int index) throws SQLException;

	public T get(Class<? extends T> type, CallableStatement cs, String parameterName) throws SQLException;

	/**
	 *
	 * @param type
	 * @param stmt
	 * @param value
	 * @param index
	 * @throws SQLException
	 * @throws UnsupportedOperationException
	 */
	public void set(Class<? extends T> type, PreparedStatement stmt, T value, int index) throws SQLException;

	public Class<?> getJavaType(int sqlType);

    public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException;

    public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName) throws SQLException;

}
