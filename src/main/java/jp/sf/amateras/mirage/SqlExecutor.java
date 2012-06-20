package jp.sf.amateras.mirage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;
import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.exception.BreakIterationException;
import jp.sf.amateras.mirage.exception.SQLRuntimeException;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.provider.ConnectionProvider;
import jp.sf.amateras.mirage.type.ValueType;
import jp.sf.amateras.mirage.util.JdbcUtil;
import jp.sf.amateras.mirage.util.MirageUtil;
import jp.sf.amateras.mirage.util.Validate;

public class SqlExecutor {

	private static final Logger logger = Logger.getLogger(SqlExecutor.class.getName());

	private NameConverter nameConverter;
	private ConnectionProvider connectionProvider;
	private Dialect dialect;
	private List<ValueType<?>> valueTypes = new ArrayList<ValueType<?>>();
	private EntityOperator entityOreator;

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
	 * @author daisuke
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

	public void setEntityOperator(EntityOperator entityOreator){
		this.entityOreator = entityOreator;
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

	private static void printParameters(Object[] params){
		if(params == null){
			return;
		}
		for(int i=0; i<params.length; i++){
			logger.info(String.format("params[%d]=%s", i, params[i]));
		}
	}

	/**
	 *
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public <T> List<T> getResultList(Class<T> clazz, String sql, Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connectionProvider.getConnection().prepareStatement(sql);
			setParameters(stmt, params);

			List<T> list = new ArrayList<T>();

			if(logger.isLoggable(Level.INFO)){
				printSql(sql);
				printParameters(params);
			}

			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);

			while(rs.next()){
				T entity = entityOreator.createEntity(clazz, rs, meta, columnCount, beanDesc,
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
	 *
	 * @param clazz
	 * @param callback
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, String sql, Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connectionProvider.getConnection().prepareStatement(sql);
			setParameters(stmt, params);

			if(logger.isLoggable(Level.INFO)){
				printSql(sql);
				printParameters(params);
			}

			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
			R result = null;

			while(rs.next()){
				T entity = entityOreator.createEntity(clazz, rs, meta, columnCount, beanDesc,
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
	 *
	 * @param clazz
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public <T> T getSingleResult(Class<T> clazz, String sql, Object[] params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connectionProvider.getConnection().prepareStatement(sql);
			setParameters(stmt, params);

			if(logger.isLoggable(Level.INFO)){
				printSql(sql);
				printParameters(params);
			}

			rs = stmt.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();

			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);

			if(rs.next()){
				T entity = entityOreator.createEntity(clazz, rs, meta, columnCount, beanDesc,
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
	 * Exceutes the update SQL.
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

			if(logger.isLoggable(Level.INFO)){
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
	 * @param paramsList the list of parameter arrays.
	 * @param entities the entities object in insertion, otherwise null
	 * @return the number of updated rows
	 * @throws SQLRuntimeException if a database access error occurs
	 */
	public int executeBatchUpdateSql(String sql, List<Object[]> paramsList, Object[] entities) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			Connection conn = connectionProvider.getConnection();

			if(logger.isLoggable(Level.INFO)){
				printSql(sql);
				for(int i=0; i < paramsList.size(); i++){
					Object[] params = paramsList.get(i);
					logger.info("[" + i + "]");
					printParameters(params);
				}
			}

			if(entities != null && dialect.supportsGenerationType(GenerationType.IDENTITY)){
				stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			} else {
				stmt = conn.prepareStatement(sql);
			}

			for(Object[] params: paramsList){
				setParameters(stmt, params);
				stmt.addBatch();
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
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(entity.getClass());
		int size = beanDesc.getPropertyDescSize();

		for(int i=0; i < size; i++){
			PropertyDesc propertyDesc = beanDesc.getPropertyDesc(i);
			PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);
			if(primaryKey != null && primaryKey.generationType() == GenerationType.IDENTITY){
				if(rs.next()){
					Class<?> type = propertyDesc.getPropertyType();
					@SuppressWarnings("rawtypes")
					ValueType valueType = MirageUtil.getValueType(type, dialect, valueTypes);

					if(valueType != null){
						 propertyDesc.setValue(entity, valueType.get(type, rs, 1));
					}
				}
			}
		}
	}

	/**
	 * Sets parameters to the PreparedStatement.
	 */
	@SuppressWarnings("unchecked")
	protected void setParameters(PreparedStatement stmt, Object[] vars) throws SQLException {
		for (int i = 0; i < vars.length; i++) {
			if(vars[i] == null){
				stmt.setObject(i + 1, null);
			} else {
				Class<?> type = vars[i].getClass();
				@SuppressWarnings("rawtypes")
				ValueType valueType = MirageUtil.getValueType(type, dialect, valueTypes);
				if(valueType != null){
					valueType.set(type, stmt, vars[i], i + 1);
				}
			}
		}
	}

}
