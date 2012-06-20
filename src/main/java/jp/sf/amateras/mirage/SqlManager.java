package jp.sf.amateras.mirage;

import java.util.List;

import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.provider.ConnectionProvider;
import jp.sf.amateras.mirage.type.ValueType;

public interface SqlManager {

	/**
	 * Sets the implementation of {@link NameConverter}.
	 *
	 * @param converter the name converter
	 */
	public void setNameConverter(NameConverter converter);

	/**
	 * Sets the implementation of {@link ConnectionProvider}.
	 *
	 * @param connectionProvider the connection provider
	 */
	public void setConnectionProvider(ConnectionProvider connectionProvider);

//	/**
//	 * Returns the next value of the sequence.
//	 *
//	 * @param sequenceName the sequence name
//	 * @return the next value of the sequence
//	 * @throws UnsupportedOperationException When the dialect does not support sequence.
//	 */
//	public long getSequenceNextValue(String sequenceName);

	/**
	 * Sets the implementation of the {@link Dialect}.
	 *
	 * @param dialect the database dialect
	 */
	public void setDialect(Dialect dialect);

	/**
	 * Adds the implementation of the {@link ValueType}.
	 *
	 * @param valueType the value type
	 */
	public void addValueType(ValueType<?> valueType);

	/**
	 * Sets the implementation of the {@link EntityOperator}.
	 *
	 * @param entityOreator the entity operator
	 */
	public void setEntityOperator(EntityOperator entityOreator);

	/**
	 * Returns the row count of the result of the given SQL.
	 *
	 * @param sqlPath the SQL file path
	 * @return the row count
	 */
	public int getCount(String sqlPath);

	/**
	 * Returns the row count of the result of the given SQL.
	 *
	 * @param sqlPath the SQL file path
	 * @param param the parameter object
	 * @return the row count
	 */
	public int getCount(String sqlPath, Object param);

	public int getCountBySql(String sql);

	public int getCountBySql(String sql, Object... params);

	public <T> List<T> getResultList(Class<T> clazz, String sqlPath);

	public <T> List<T> getResultList(Class<T> clazz, String sqlPath, Object param);

	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, String sqlPath);

	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, String sqlPath, Object param);

	public <T> T getSingleResult(Class<T> clazz, String sqlPath);

	public <T> T getSingleResult(Class<T> clazz, String sqlPath, Object param);

	public int executeUpdate(String sqlPath);

	public int executeUpdate(String sqlPath, Object param);

	/**
	 * Inserts the given entity.
	 *
	 * @param entity the entity to insert
	 * @return the number of updated rows
	 */
	public int insertEntity(Object entity);

	/**
	 * Inserts given entities in batch mode.
	 *
	 * @param <T> the entity type
	 * @param entities entities to insert
	 * @return the number of inserted rows
	 */
	public <T> int insertBatch(T... entities);

	/**
	 * Inserts given entities in batch mode.
	 *
	 * @param <T> the entity type
	 * @param entities entities to insert
	 * @return the number of inserted rows
	 */
	public <T> int insertBatch(List<T> entities);

	/**
	 * Updates the given entity.
	 *
	 * @param entity the entity to update
	 * @return the number of updated rows
	 */
	public int updateEntity(Object entity);

	/**
	 * Updates given entities in batch mode.
	 *
	 * @param <T> the entity type
	 * @param entities entities to update
	 * @return the number of updated rows
	 */
	public <T> int updateBatch(T... entities);

	/**
	 * Updates given entities in batch mode.
	 *
	 * @param <T> the entity type
	 * @param entities entities to update
	 * @return the number of updated rows
	 */
	public <T> int updateBatch(List<T> entities);

	/**
	 * Deletes the given entity.
	 *
	 * @param entity the entity to delete
	 * @return the number of updated rows
	 */
	public int deleteEntity(Object entity);

	/**
	 * Deletes given entities in batch mode.
	 *
	 * @param <T> the entity type
	 * @param entities entities to delete
	 * @return the number of deleted rows
	 */
	public <T> int deleteBatch(T... entities);

	/**
	 * Deletes given entities in batch mode.
	 *
	 * @param <T> the entity type
	 * @param entities entities to delete
	 * @return the number of deleted rows
	 */
	public <T> int deleteBatch(List<T> entities);

	/**
	 * Finds the entity by the given primary key.
	 *
	 * @param <T> the type of entity
	 * @param clazz the type of entity
	 * @param id the primary key
	 * @return the entity. If the entity which corresponds to the given primary key
	 *   is not found, this method returns <code>null</code>.
	 */
	public <T> T findEntity(Class<T> clazz, Object... id);

	/**
	 * Invokes the stored procedure.
	 *
	 * @param procedureName the procedure name
	 */
	public void call(String procedureName);

	/**
	 * Invokes the stored procedure.
	 *
	 * @param procedureName the procedure name
	 * @param parameter the parameter object
	 */
	public void call(String procedureName, Object parameter);

	public <T> T call(Class<T> resultClass, String functionName);

	public <T> T call(Class<T> resultClass, String functionName, Object param);

	public <T> List<T> callForList(Class<T> resultClass, String functionName);

	public <T> List<T> callForList(Class<T> resultClass, String functionName, Object param);

	public <T> List<T> getResultListBySql(Class<T> clazz, String sql);

	public <T> List<T> getResultListBySql(Class<T> clazz, String sql, Object... params);

	public <T> T getSingleResultBySql(Class<T> clazz, String sql);

	public <T> T getSingleResultBySql(Class<T> clazz, String sql, Object... params);

	public <T, R> R iterateBySql(Class<T> clazz, IterationCallback<T, R> callback, String sql);

	public <T, R> R iterateBySql(Class<T> clazz, IterationCallback<T, R> callback, String sql, Object... params);

	/**
	 * Executes the given SQL.
	 * <pre>
	 * int rows = sqlManager.executeUpdateBySql("DELETE FROM EMPLOYEE");
	 * </pre>
	 *
	 * @param sql the SQL to execute
	 * @return the number of updated rows
	 */
	public int executeUpdateBySql(String sql);

	/**
	 * Executes the given SQL with parameters.
	 * <pre>
	 * int rows = sqlManager.executeUpdateBySql("DELETE FROM EMPLOYEE WHERE ID=?", id);
	 * </pre>
	 *
	 * @param sql the SQL to execute which contains placeholder (This is not a 2waySQL)
	 * @param params the parameters which are set to the placeholder
	 * @return the number of updated rows
	 */
	public int executeUpdateBySql(String sql, Object... params);

}
