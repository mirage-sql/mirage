package com.miragesql.miragesql;

import java.util.List;

import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.provider.ConnectionProvider;
import com.miragesql.miragesql.type.ValueType;

/**
 * SqlManager is the main interface to interact with MirageSQL. See the docs for examples on how to use it at
 * <a href="https://github.com/mirage-sql/mirage/wiki">https://github.com/mirage-sql/mirage/wiki</a>.
 */
public interface SqlManager {

    /**
     * Sets the implementation of the {@link NameConverter}.
     * Available name converters are in the {@link com.miragesql.miragesql} package, but custom name converters can also
     * be easily implemented (to follow corporate internal naming conventions).
     *
     * @param converter the name converter
     */
    void setNameConverter(NameConverter converter);

    /**
     * Sets the implementation of the {@link ConnectionProvider}.
     *
     * @param connectionProvider the connection provider
     */
    void setConnectionProvider(ConnectionProvider connectionProvider);

    /**
     * Sets the implementation of the {@link Dialect}.
     *
     * @param dialect the database dialect
     */
    void setDialect(Dialect dialect);

    /**
     * Adds the implementation of the {@link ValueType}.
     *
     * @param valueType the value type
     */
    void addValueType(ValueType<?> valueType);

    /**
     * Sets the implementation of the {@link EntityOperator}.
     *
     * @param entityOperator the entity operator
     */
    void setEntityOperator(EntityOperator entityOperator);

    /**
     * Returns the row count of the result of the given SQL.
     *
     * @param resource the {@link SqlResource} that identifies the SQL
     *
     * @return the row count
     */
    int getCount(SqlResource resource);


    /**
     * Returns the row count of the result of the given SQL.
     *
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param param the parameter object
     *
     * @return the row count
     */
    int getCount(SqlResource resource, Object param);


    /**
     * Returns a list of Objects from the result set for a given SQL.
     *
     * @param <T> the entity type
     * @param clazz the type
     * @param resource the {@link SqlResource} that identifies the SQL
     *
     * @return a List of Objects of type T
     */
    <T> List<T> getResultList(Class<T> clazz, SqlResource resource);

    /**
     * Returns a list of Objects from the result set for a given SQL.
     *
     * @param <T> the entity type
     * @param clazz the type
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param param the parameters for the query
     *
     * @return a List of Objects of type T
     */
    <T> List<T> getResultList(Class<T> clazz, SqlResource resource, Object param);

    /**
     * Iterates over over a list of Entities produced from a result set, and calls a callback on each.
     *
     * @param clazz the type
     * @param callback the callback to execute
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param <T> the entity type
     * @param <R> the return type
     *
     * @return the result of the iteration callbacks
     */
    <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, SqlResource resource);

    /**
     * Iterates over over a list of Entities produced from a result set, and calls a callback on each using parameters.
     *
     * @param clazz the type
     * @param callback the callback to execute
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param param the parameters for the query
     * @param <T> the entity type
     * @param <R> the return type
     *
     * @return the result of the iteration callbacks
     */
    <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, SqlResource resource, Object param);

    /**
     * Executes an SQL and returns only the first row as an entity, or null if nothing is returned.
     *
     * @param clazz the type
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param <T> the entity type
     *
     * @return an entity
     */
    <T> T getSingleResult(Class<T> clazz, SqlResource resource);

    /**
     * Executes an SQL using parameters, and returns only the first row as an entity, or null if nothing is returned.
     *
     * @param clazz the type
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param param the parameters for the query
     * @param <T> the entity type
     *
     * @return an entity
     */
    <T> T getSingleResult(Class<T> clazz, SqlResource resource, Object param);

    /**
     * Executes the SQL identified by the {@link SqlResource}. E.g.
     *
     * @param resource the {@link SqlResource} that identifies the SQL
     *
     * @return the number of updated rows
     */
    int executeUpdate(SqlResource resource);

    /**
     * Executes the SQL identified by the {@link SqlResource} using parameters. E.g.
     *
     * @param resource the {@link SqlResource} that identifies the SQL
     * @param param the parameters which are set to the placeholder
     *
     * @return the number of updated rows
     */
    int executeUpdate(SqlResource resource, Object param);

    /**
     * Inserts the given entity.
     *
     * @param entity the entity to insert
     *
     * @return the number of updated rows
     */
    int insertEntity(Object entity);

    /**
     * Inserts given entities in batch mode.
     *
     * @param <T> the entity type
     * @param entities entities to insert
     *
     * @return the number of inserted rows
     */
    <T> int insertBatch(T... entities);

    /**
     * Inserts given entities in batch mode.
     *
     * @param <T> the entity type
     * @param entities entities to insert
     *
     * @return the number of inserted rows
     */
    <T> int insertBatch(List<T> entities);

    /**
     * Updates the given entity.
     *
     * @param entity the entity to update
     *
     * @return the number of updated rows
     */
    int updateEntity(Object entity);

    /**
     * Updates given entities in batch mode.
     *
     * @param <T> the entity type
     * @param entities entities to update
     * @return the number of updated rows
     */
    <T> int updateBatch(T... entities);

    /**
     * Updates given entities in batch mode.
     *
     * @param <T> the entity type
     * @param entities entities to update
     * @return the number of updated rows
     */
    <T> int updateBatch(List<T> entities);

    /**
     * Deletes the given entity.
     *
     * @param entity the entity to delete
     * @return the number of updated rows
     */
    int deleteEntity(Object entity);

    /**
     * Deletes given entities in batch mode.
     *
     * @param <T> the entity type
     * @param entities entities to delete
     *
     * @return the number of deleted rows
     */
    <T> int deleteBatch(T... entities);

    /**
     * Deletes given entities in batch mode.
     *
     * @param <T> the entity type
     * @param entities entities to delete
     * @return the number of deleted rows
     */
    <T> int deleteBatch(List<T> entities);

    /**
     * Finds the entity by the given primary key.
     *
     * @param <T> the type of entity
     * @param clazz the type of entity
     * @param id the primary key
     *
     * @return the entity. If the entity which corresponds to the given primary key
     *   is not found, this method returns <code>null</code>.
     */
    <T> T findEntity(Class<T> clazz, Object... id);

    /**
     * Invokes the stored procedure.
     *
     * @param procedureName the procedure name
     */
    void call(String procedureName);

    /**
     * Invokes the stored procedure using parameters.
     *
     * @param procedureName the procedure name
     * @param parameter the parameter object
     */
    void call(String procedureName, Object parameter);

    /**
     * Invokes the function.
     *
     * @param resultClass the type
     * @param functionName the function name
     * @param <T> the entity type
     *
     * @return the entity
     */
    <T> T call(Class<T> resultClass, String functionName);

    /**
     * Invokes the function using parameters.
     *
     * @param resultClass the type
     * @param functionName the function name
     * @param param the parameter object
     * @param <T> the entity type
     * @return the entity
     */
    <T> T call(Class<T> resultClass, String functionName, Object param);

    /**
     * Invokes a function that returns a list of entities.
     *
     * @param resultClass the type
     * @param functionName the function name
     * @param <T> the entity type
     *
     * @return a list of entities
     */
    <T> List<T> callForList(Class<T> resultClass, String functionName);

    /**
     * Invokes a function that returns a list of entities, using parameters.
     *
     * @param resultClass the type
     * @param functionName the function name
     * @param param the parameter object
     * @param <T> the entity type
     *
     * @return a list of entities
     */
    <T> List<T> callForList(Class<T> resultClass, String functionName, Object param);

}
