package com.miragesql.miragesql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.exception.BreakIterationException;
import com.miragesql.miragesql.exception.SQLRuntimeException;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.provider.ConnectionProvider;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.JdbcUtil;
import com.miragesql.miragesql.util.MirageUtil;
import com.miragesql.miragesql.util.Validate;

public class SqlExecutor {

	private static final Logger logger = LoggerFactory.getLogger(SqlExecutor.class);

	private BeanDescFactory beanDescFactory;
	private NameConverter nameConverter;
	private ConnectionProvider connectionProvider;
	private Dialect dialect;
	private List<ValueType<?>> valueTypes = new ArrayList<>();
	private EntityOperator entityOperator;


	
	public void setBeanDescFactory(BeanDescFactory beanDescFactory) {
		this.beanDescFactory = beanDescFactory;
	}
	
	protected BeanDescFactory getBeanDescFactory() {
		return beanDescFactory;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider){
		this.connectionProvider = connectionProvider;
	}

	public void setNameConverter(NameConverter nameConverter){
		this.nameConverter = nameConverter;
	}

	/**
	 *
	 * @param valueTypes
	 * @throws IllegalArgumentException if the {@code valueTypes} is {@code null} or
	 * an element in the {@code valueTypes} is {@code null}
	 */
	public void setValueTypes(List<ValueType<?>> valueTypes) {
		Validate.notNull(valueTypes);
		this.valueTypes = valueTypes;
	}

	public void addValueType(ValueType<?> valueType){
		this.valueTypes.add(valueType);
	}

	public void setDialect(Dialect dialect){
		this.dialect = dialect;
	}

	public void setEntityOperator(EntityOperator entityOperator){
		this.entityOperator = entityOperator;
	}

	private static void printSql(String sql){
		sql = sql.replace("\r\n", "\n");
		sql = sql.replace("\r", "\n");

		StringBuilder sb = new StringBuilder();
		for(String line: sql.split("\n")){
			if(line.trim().length() != 0){
				sb.append(line).append(System.getProperty("line.separator"));
			}
		}

		logger.info(sb.toString().trim());
	}

	private static void printParameters(PropertyDesc[] propDescs, Object entity){
		if(propDescs == null){
			return;
		}
		for(int i=0; i<propDescs.length; i++){
			logger.info(String.format("params[%d]=%s", i, propDescs[i].getValue(entity)));
		}
	}

	private static void printParameters(Object[] params){
		if(params == null){
			return;
		}
		for(int i=0; i<params.length; i++){
			logger.info(String.format("params[%d]=%s", i, params[i]));
		}
	}

	/**
	 * Returns a list of entities from the DB after executing the SQL.
	 *
	 * @param clazz the class of the entity to select
	 * @param sql the SQL to execute
	 * @param params parameters required to execute the SQL
	 * @return a List of entities
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public <T> List<T> getResultList(Class<T> clazz, String sql, Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connectionProvider.getConnection().prepareStatement(sql);
			setParameters(stmt, params);

			List<T> list = new ArrayList<>();

			if(logger.isInfoEnabled()){
				printSql(sql);
				printParameters(params);
			}

			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);

			while(rs.next()){
				T entity = entityOperator.createEntity(clazz, rs, meta, columnCount, beanDesc,
						dialect, valueTypes, nameConverter);
				list.add(entity);
			}

			return list;

		} catch (SQLException ex) {
			throw new SQLRuntimeException(ex);

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/**
	 * Iterate over a ResultSet with entities.
	 * @param clazz the class of the entity
	 * @param callback callback to execute
	 * @param sql the SQL to execute
	 * @param params the parameters of the SQL
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, String sql, Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connectionProvider.getConnection().prepareStatement(sql);
			setParameters(stmt, params);

			if(logger.isInfoEnabled()){
				printSql(sql);
				printParameters(params);
			}

			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);
			R result = null;

			while(rs.next()){
				T entity = entityOperator.createEntity(clazz, rs, meta, columnCount, beanDesc,
						dialect, valueTypes, nameConverter);
				try {
					result = callback.iterate(entity);
				} catch(BreakIterationException ex){
					break;
				}
			}

			return result;

		} catch (SQLException ex) {
			throw new SQLRuntimeException(ex);

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/**
	 * Returns a single entity from the first row of an SQL.
	 * @param clazz the class of the entity
	 * @param sql the SQL to execute
	 * @param params the parameters to execute the SQL
	 * @return the entity from the result set.
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public <T> T getSingleResult(Class<T> clazz, String sql, Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connectionProvider.getConnection().prepareStatement(sql);
			setParameters(stmt, params);

			if(logger.isInfoEnabled()){
				printSql(sql);
				printParameters(params);
			}

			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);

			if(rs.next()){
				T entity = entityOperator.createEntity(clazz, rs, meta, columnCount, beanDesc,
						dialect, valueTypes, nameConverter);
				return entity;
			}

			return null;

		} catch (SQLException ex) {
			throw new SQLRuntimeException(ex);

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/**
	 * Executes an update SQL.
	 *
	 * @param sql the update SQL to execute
	 * @param propDescs the array of parameters
	 * @param entity the entity object in insertion, otherwise null
	 * @return the number of updated rows
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public int executeUpdateSql(String sql, PropertyDesc[] propDescs, Object entity){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Connection conn = connectionProvider.getConnection();

			if(logger.isInfoEnabled()){
				printSql(sql);
 				printParameters(propDescs);
			}

			if(entity != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement(sql);
			}
			setParameters(stmt, propDescs, entity);

			int result = stmt.executeUpdate();

			// Sets GenerationType.IDENTITY properties value.
			if(entity != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				rs = stmt.getGeneratedKeys();
				fillIdentityPrimaryKeys(entity, rs);
			}

			return result;

		} catch(SQLException ex){
			throw new SQLRuntimeException(ex);

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/**
	 * Executes the update SQL.
	 *
	 * @param sql the update SQL to execute
	 * @param params the array of parameters
	 * @param entity the entity object in insertion, otherwise null
	 * @return the number of updated rows
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public int executeUpdateSql(String sql, Object[] params, Object entity){
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Connection conn = connectionProvider.getConnection();

			if(logger.isInfoEnabled()){
				printSql(sql);
 				printParameters(params);
			}

			if(entity != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement(sql);
			}
			setParameters(stmt, params);

			int result = stmt.executeUpdate();

			// Sets GenerationType.IDENTITY properties value.
			if(entity != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				rs = stmt.getGeneratedKeys();
				fillIdentityPrimaryKeys(entity, rs);
			}

			return result;

		} catch(SQLException ex){
			throw new SQLRuntimeException(ex);

		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/**
	 * Executes the update SQL.
	 *
	 * @param sql the update SQL to execute
	 * @param propDescsList the list of parameter arrays.
	 * @param entities the entities object in insertion, otherwise null
	 * @return the number of updated rows
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public int executeBatchUpdateSql(String sql, List<PropertyDesc[]> propDescsList, Object[] entities) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Connection conn = connectionProvider.getConnection();

			if(logger.isInfoEnabled()){
				printSql(sql);
				for(int i=0; i < propDescsList.size(); i++){
					PropertyDesc[] propDescs = propDescsList.get(i);
					logger.info("[" + i + "]");
					printParameters(propDescs, entities[i]);
				}
			}

			if(entities != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement(sql);
			}
			
			if(entities != null) {
				for (int i = 0; i < propDescsList.size(); i++) {
					PropertyDesc[] propDescs = propDescsList.get(i);
					setParameters(stmt, propDescs, entities[i]);
					stmt.addBatch();
				}
			} else {
				for(PropertyDesc[] propDescs: propDescsList){
					setParameters(stmt, propDescs, null);
					stmt.addBatch();
				}
			}

			int[] results = stmt.executeBatch();

			int updateRows = 0;
			for(int result: results){
				updateRows += result;
			}

			// Sets GenerationType.IDENTITY properties value.
			if(entities != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				rs = stmt.getGeneratedKeys();
				for(Object entity: entities){
					fillIdentityPrimaryKeys(entity, rs);
				}
			}

			return updateRows;

		} catch(SQLException ex){
			throw new SQLRuntimeException(ex);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}

	/**
	 * Sets GenerationType.IDENTITY properties value.
	 */
	@SuppressWarnings("unchecked")
	protected void fillIdentityPrimaryKeys(Object entity, ResultSet rs) throws SQLException {
		BeanDesc beanDesc = beanDescFactory.getBeanDesc(entity.getClass());
		int size = beanDesc.getPropertyDescSize();

		for(int i=0; i < size; i++){
			PropertyDesc propertyDesc = beanDesc.getPropertyDesc(i);
			PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);
			if(primaryKey != null && primaryKey.generationType() == GenerationType.IDENTITY){
				if(rs.next()){
					Class<?> propertyType = propertyDesc.getPropertyType();
					@SuppressWarnings("rawtypes")
					ValueType valueType = MirageUtil.getValueType(propertyType, propertyDesc, dialect, valueTypes);

					if(valueType != null){
						 propertyDesc.setValue(entity, valueType.get(propertyType, rs, 1));
					}
				}
			}
		}
	}

	/**
	 * Sets parameters to the PreparedStatement.
	 */
	@SuppressWarnings("unchecked")
	protected void setParameters(PreparedStatement stmt, PropertyDesc[] propDescs, Object entity) throws SQLException {
		for (int i = 0; i < propDescs.length; i++) {
			PropertyDesc propertyDesc = propDescs[i];
			if(propertyDesc == null /*|| propertyDesc.getValue(entity) == null*/){
				stmt.setObject(i + 1, null);
			} else {
				Class<?> propertType = propertyDesc.getPropertyType();
				@SuppressWarnings("rawtypes")
				ValueType valueType = MirageUtil.getValueType(propertType, propertyDesc, dialect, valueTypes);
				if(valueType != null){
					valueType.set(propertType, stmt, propertyDesc.getValue(entity), i + 1);
				} else {
					if(logger.isWarnEnabled()) {
						logger.warn("valueType for " + propertType.getName() + " not found.");
					}
				}
			}
		}
	}

	/**
	 * Sets parameters to the PreparedStatement.
	 */
	@SuppressWarnings("unchecked")
	protected void setParameters(PreparedStatement stmt, Object[] params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			if(params[i] == null){
				stmt.setObject(i + 1, null);
			} else {
				Class<?> propertType = params[i].getClass();
				@SuppressWarnings("rawtypes")
				ValueType valueType = MirageUtil.getValueType(propertType, null, dialect, valueTypes);
				if(valueType != null){
					valueType.set(propertType, stmt, params[i], i + 1);
				} else {
					if(logger.isWarnEnabled()) {
						logger.warn("valueType for " + propertType.getName() + " not found.");
					}
				}
			}
		}
	}
}
