package jp.sf.amateras.mirage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.annotation.ResultSet;
import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;
import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.dialect.StandardDialect;
import jp.sf.amateras.mirage.exception.IORuntimeException;
import jp.sf.amateras.mirage.naming.DefaultNameConverter;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.parser.Node;
import jp.sf.amateras.mirage.parser.SqlContext;
import jp.sf.amateras.mirage.parser.SqlParserImpl;
import jp.sf.amateras.mirage.provider.ConnectionProvider;
import jp.sf.amateras.mirage.type.BigDecimalValueType;
import jp.sf.amateras.mirage.type.BooleanPrimitiveValueType;
import jp.sf.amateras.mirage.type.BooleanValueType;
import jp.sf.amateras.mirage.type.ByteArrayValueType;
import jp.sf.amateras.mirage.type.DoublePrimitiveValueType;
import jp.sf.amateras.mirage.type.DoubleValueType;
import jp.sf.amateras.mirage.type.FloatPrimitiveValueType;
import jp.sf.amateras.mirage.type.FloatValueType;
import jp.sf.amateras.mirage.type.IntegerPrimitiveValueType;
import jp.sf.amateras.mirage.type.IntegerValueType;
import jp.sf.amateras.mirage.type.LongPrimitiveValueType;
import jp.sf.amateras.mirage.type.LongValueType;
import jp.sf.amateras.mirage.type.ShortPrimitiveValueType;
import jp.sf.amateras.mirage.type.ShortValueType;
import jp.sf.amateras.mirage.type.SqlDateValueType;
import jp.sf.amateras.mirage.type.StringValueType;
import jp.sf.amateras.mirage.type.TimeValueType;
import jp.sf.amateras.mirage.type.TimestampValueType;
import jp.sf.amateras.mirage.type.UtilDateValueType;
import jp.sf.amateras.mirage.type.ValueType;
import jp.sf.amateras.mirage.type.enumerate.EnumOneBasedOrdinalValueType;
import jp.sf.amateras.mirage.type.enumerate.EnumOrdinalValueType;
import jp.sf.amateras.mirage.type.enumerate.EnumStringValueType;
import jp.sf.amateras.mirage.util.IOUtil;
import jp.sf.amateras.mirage.util.MirageUtil;
import jp.sf.amateras.mirage.util.Validate;

public class SqlManagerImpl implements SqlManager {

//	private static final Logger logger = Logger.getLogger(SqlManagerImpl.class.getName());

	protected BeanDescFactory beanDescFactory;
	
	protected ConnectionProvider connectionProvider;

	protected NameConverter nameConverter;

	protected EntityOperator entityOperator;

	protected Dialect dialect = new StandardDialect();

	protected SqlExecutor sqlExecutor = new SqlExecutor();

	protected CallExecutor callExecutor = new CallExecutor();

	protected Map<SqlResource, Node> nodeCache = new ConcurrentHashMap<SqlResource, Node>();

	protected boolean cacheMode = false;

	public SqlManagerImpl(){
		addValueType(new StringValueType());
		addValueType(new IntegerValueType());
		addValueType(new IntegerPrimitiveValueType());
		addValueType(new LongValueType());
		addValueType(new LongPrimitiveValueType());
		addValueType(new ShortValueType());
		addValueType(new ShortPrimitiveValueType());
		addValueType(new DoubleValueType());
		addValueType(new DoublePrimitiveValueType());
		addValueType(new FloatValueType());
		addValueType(new FloatPrimitiveValueType());
		addValueType(new BooleanValueType());
		addValueType(new BooleanPrimitiveValueType());
		addValueType(new BigDecimalValueType());
		addValueType(new SqlDateValueType());
		addValueType(new UtilDateValueType());
		addValueType(new TimeValueType());
		addValueType(new TimestampValueType());
		addValueType(new ByteArrayValueType());
		addValueType(new EnumStringValueType());
		addValueType(new EnumOrdinalValueType());
		addValueType(new EnumOneBasedOrdinalValueType());
//		addValueType(new jp.sf.amateras.mirage.type.DefaultValueType());

		setDialect(dialect);
		setBeanDescFactory(new BeanDescFactory());
		setNameConverter(new DefaultNameConverter());
		setEntityOperator(new DefaultEntityOperator());
	}

	public void setCacheMode(boolean cacheMode){
		this.cacheMode = cacheMode;
	}
	
	public void setBeanDescFactory(BeanDescFactory beanDescFactory) {
		this.beanDescFactory = beanDescFactory;
		this.sqlExecutor.setBeanDescFactory(beanDescFactory);
		this.callExecutor.setBeanDescFactory(beanDescFactory);
	}

	public void setNameConverter(NameConverter nameConverter) {
		this.nameConverter = nameConverter;
		this.sqlExecutor.setNameConverter(nameConverter);
		this.callExecutor.setNameConverter(nameConverter);
	}

	public NameConverter getNameConverter(){
		return this.nameConverter;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		this.sqlExecutor.setConnectionProvider(connectionProvider);
		this.callExecutor.setConnectionProvider(connectionProvider);
	}

	public void setEntityOperator(EntityOperator entityOperator){
		this.entityOperator = entityOperator;
		this.sqlExecutor.setEntityOperator(entityOperator);
		this.callExecutor.setEntityOperator(entityOperator);
	}

	public ConnectionProvider getConnectionProvider(){
		return this.connectionProvider;
	}

	public void setDialect(Dialect dialect){
		this.dialect = dialect;
		this.sqlExecutor.setDialect(dialect);
		this.callExecutor.setDialect(dialect);
	}

	public Dialect getDialect(){
		return this.dialect;
	}

	protected Node prepareNode(SqlResource resource) {

		if(cacheMode && nodeCache.containsKey(resource)){
			return nodeCache.get(resource);
		}

		String sql = null;
		try {
			InputStream in = resource.getInputStream();
			if (in == null) {
				throw new RuntimeException(String.format(
						"resource: %s is not found.", resource));
			}
			sql = new String(IOUtil.readStream(in), "UTF-8");
		} catch (IORuntimeException ex){
			throw new IORuntimeException(String.format("Failed to load SQL from: %s", resource), ex.getCause());

		} catch (UnsupportedEncodingException e) {
			// must not to be reached here
			throw new RuntimeException(e);

		} catch (IOException e) {
			throw new IORuntimeException(e);
			
		}

		sql = sql.trim();
		if(sql.endsWith(";")){
			sql = sql.substring(0, sql.length() - 1);
		}

		Node node = new SqlParserImpl(sql, beanDescFactory).parse();

		if(cacheMode){
			nodeCache.put(resource, node);
		}

		return node;
	}

	protected SqlContext prepareSqlContext(Object param){
		return MirageUtil.getSqlContext(beanDescFactory, param);
	}

	public int executeUpdate(String sqlPath) {
		return executeUpdate(new ClasspathSqlResource(sqlPath));
	}
	public int executeUpdate(SqlResource resource) {
		return executeUpdate(resource, null);
	}

	public int executeUpdate(String sqlPath, Object param) {
		return executeUpdate(new ClasspathSqlResource(sqlPath), param);
	}
	public int executeUpdate(SqlResource resource, Object param) {
		Node node = prepareNode(resource);
		SqlContext context = prepareSqlContext(param);
		node.accept(context);

		return sqlExecutor.executeUpdateSql(context.getSql(), context.getBindVariables(), null);

	}

	public <T> List<T> getResultList(Class<T> clazz, String sqlPath) {
		return getResultList(clazz, new ClasspathSqlResource(sqlPath));
	}
	public <T> List<T> getResultList(Class<T> clazz, SqlResource resource) {
		return getResultList(clazz, resource, null);
	}

	public <T> List<T> getResultList(Class<T> clazz, String sqlPath, Object param) {
		return getResultList(clazz, new ClasspathSqlResource(sqlPath), param);
	}
	public <T> List<T> getResultList(Class<T> clazz, SqlResource resource, Object param) {
		Node node = prepareNode(resource);
		SqlContext context = prepareSqlContext(param);
		node.accept(context);

		return sqlExecutor.getResultList(clazz, context.getSql(), context.getBindVariables());
	}

	public <T> T getSingleResult(Class<T> clazz, String sqlPath) {
		return getSingleResult(clazz, new ClasspathSqlResource(sqlPath));
	}
	public <T> T getSingleResult(Class<T> clazz, SqlResource resource) {
		return getSingleResult(clazz, resource, null);
	}

	public <T> T getSingleResult(Class<T> clazz, String sqlPath, Object param) {
		return getSingleResult(clazz, new ClasspathSqlResource(sqlPath), param);
	}
	public <T> T getSingleResult(Class<T> clazz, SqlResource resource, Object param) {
		Node node = prepareNode(resource);
		SqlContext context = prepareSqlContext(param);
		node.accept(context);

		return sqlExecutor.getSingleResult(clazz, context.getSql(), context.getBindVariables());
	}

	public int deleteEntity(Object entity) {
		List<PropertyDesc> propDescs = new ArrayList<PropertyDesc>();
		String executeSql = MirageUtil.buildDeleteSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

		return sqlExecutor.executeUpdateSql(executeSql, propDescs.toArray(new PropertyDesc[propDescs.size()]), entity);
	}

	public <T> int deleteBatch(T... entities) {
		if(entities.length == 0){
			return 0;
		}

		List<PropertyDesc[]> paramsList = new ArrayList<PropertyDesc[]>();
		String executeSql = null;

		for(Object entity: entities){
			List<PropertyDesc> propDescs = new ArrayList<PropertyDesc>();
			String sql = MirageUtil.buildDeleteSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

			if(executeSql == null){
				executeSql = sql;

			} else if(!sql.equals(executeSql)){
				throw new IllegalArgumentException("Different entity is contained in the entity list.");
			}

			paramsList.add(propDescs.toArray(new PropertyDesc[propDescs.size()]));
		}

		return sqlExecutor.executeBatchUpdateSql(executeSql, paramsList, entities);
	}

	public <T> int deleteBatch(List<T> entities) {
		return deleteBatch(entities.toArray());
	}

	/**
	 * Sets GenerationType.SEQUENCE properties value.
	 */
	private void fillPrimaryKeysBySequence(Object entity){
		if(!dialect.supportsGenerationType(GenerationType.SEQUENCE)){
			return;
		}

		BeanDesc beanDesc = beanDescFactory.getBeanDesc(entity.getClass());
		int size = beanDesc.getPropertyDescSize();

		for(int i=0; i < size; i++){
			PropertyDesc propertyDesc = beanDesc.getPropertyDesc(i);
			PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);

			if(primaryKey != null && primaryKey.generationType() == GenerationType.SEQUENCE){
				String sql = dialect.getSequenceSql(primaryKey.generator());
				Object value = sqlExecutor.getSingleResult(propertyDesc.getPropertyType(), sql, new Object[0]);
				propertyDesc.setValue(entity, value);
			}
		}
	}

	public int insertEntity(Object entity) {
		fillPrimaryKeysBySequence(entity);

		List<PropertyDesc> propDescs = new ArrayList<PropertyDesc>();
		String sql = MirageUtil.buildInsertSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

		return sqlExecutor.executeUpdateSql(sql, propDescs.toArray(new PropertyDesc[propDescs.size()]), entity);
	}

	public <T> int insertBatch(T... entities){
		if(entities.length == 0){
			return 0;
		}

		List<PropertyDesc[]> propDescsList = new ArrayList<PropertyDesc[]>();
		String executeSql = null;

		for(Object entity: entities){
			fillPrimaryKeysBySequence(entity);

			List<PropertyDesc> propDescs = new ArrayList<PropertyDesc>();
			String sql = MirageUtil.buildInsertSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

			if(executeSql == null){
				executeSql = sql;

			} else if(!sql.equals(executeSql)){
				throw new IllegalArgumentException("Different entity is contained in the entity list.");
			}

			propDescsList.add(propDescs.toArray(new PropertyDesc[propDescs.size()]));
		}
		// TODO ここ？
		return sqlExecutor.executeBatchUpdateSql(executeSql, propDescsList, entities);
	}

	public <T> int insertBatch(List<T> entities){
		return insertBatch(entities.toArray());
	}

	public int updateEntity(Object entity) {
		List<PropertyDesc> propDescs = new ArrayList<PropertyDesc>();
		String executeSql = MirageUtil.buildUpdateSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

		return sqlExecutor.executeUpdateSql(executeSql, propDescs.toArray(new PropertyDesc[propDescs.size()]), entity);
	}

	public <T> int updateBatch(T... entities) {
		if(entities.length == 0){
			return 0;
		}

		List<PropertyDesc[]> propDescsList = new ArrayList<PropertyDesc[]>();
		String executeSql = null;

		for(Object entity: entities){
			List<PropertyDesc> propDescs = new ArrayList<PropertyDesc>();
			String sql = MirageUtil.buildUpdateSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

			if(executeSql == null){
				executeSql = sql;

			} else if(!sql.equals(executeSql)){
				throw new IllegalArgumentException("Different entity is contained in the entity list.");
			}

			propDescsList.add(propDescs.toArray(new PropertyDesc[propDescs.size()]));
		}

		return sqlExecutor.executeBatchUpdateSql(executeSql, propDescsList, entities);
	}

	public <T> int updateBatch(List<T> entities) {
		return updateBatch(entities.toArray());
	}

	//	@Override
	public <T> T findEntity(Class<T> clazz, Object... id) {
		String executeSql = MirageUtil.buildSelectSQL(beanDescFactory, entityOperator, clazz, nameConverter);
		return sqlExecutor.getSingleResult(clazz, executeSql, id);
	}

	/**
	 *
	 * @param valueTypes
	 * @throws IllegalArgumentException if the {@code valueTypes} is {@code null} or
	 * an element in the {@code valueTypes} is {@code null}
	 */
	public void setValueTypes(List<ValueType<?>> valueTypes) {
		Validate.noNullElements(valueTypes);
		this.sqlExecutor.setValueTypes(valueTypes);
		this.callExecutor.setValueTypes(valueTypes);
	}

//	@Override
	public void addValueType(ValueType<?> valueType) {
		this.sqlExecutor.addValueType(valueType);
		this.callExecutor.addValueType(valueType);
	}

//	@Override
	public int getCount(String sqlPath) {
		return getCount(new ClasspathSqlResource(sqlPath), null);
	}
	public int getCount(SqlResource resource) {
		return getCount(resource, null);
	}

//	@Override
	public int getCount(String sqlPath, Object param) {
		return getCount(new ClasspathSqlResource(sqlPath), param);
	}
	public int getCount(SqlResource resource, Object param) {
		Node node = prepareNode(resource);
		SqlContext context = prepareSqlContext(param);
		node.accept(context);
		String sql = dialect.getCountSql(context.getSql());

		Integer result = sqlExecutor.getSingleResult(Integer.class, sql, context.getBindVariables());
		if(result == null){
			return 0;
		}
		return result.intValue();
	}

	public int getCountBySql(String sql) {
		return getCountBySql(sql, new Object[0]);
	}

	public int getCountBySql(String sql, Object... params) {
		return getSingleResultBySql(Integer.class, sql, params);
	}

//	@Override
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, String sqlPath) {
		return iterate(clazz, callback, new ClasspathSqlResource(sqlPath));
	}
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, SqlResource resource) {
		return this.<T, R> iterate(clazz, callback, resource, null);
	}

//	@Override
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, String sqlPath, Object param) {
		return iterate(clazz, callback, new ClasspathSqlResource(sqlPath), param);
	}
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, SqlResource resource, Object param) {

		Node node = prepareNode(resource);
		SqlContext context = prepareSqlContext(param);
		node.accept(context);

		return sqlExecutor.<T, R> iterate(clazz, callback, context.getSql(), context.getBindVariables());
	}

	public void call(String procedureName){
		String sql = toCallString(procedureName, false);
		callExecutor.call(sql);
	}

	public void call(String procedureName, Object parameter){
		String sql = toCallString(procedureName, parameter, false);
		callExecutor.call(sql, parameter);
	}

	public <T> T call(Class<T> resultClass, String functionName){
		String sql = toCallString(functionName, true);
		return callExecutor.call(resultClass, sql);
	}

	public <T> T call(Class<T> resultClass, String functionName, Object param){
		String sql = toCallString(functionName, param, true);
		return callExecutor.call(resultClass, sql, param);
	}

	public <T> List<T> callForList(Class<T> resultClass, String functionName){
		String sql = toCallString(functionName, true);
		return callExecutor.callForList(resultClass, sql);
	}

	public <T> List<T> callForList(Class<T> resultClass, String functionName, Object param){
		String sql = toCallString(functionName, param, true);
		return callExecutor.callForList(resultClass, sql, param);
	}

	protected String toCallString(String moduleName, boolean function){
		return toCallString(moduleName, null, function);
	}

	protected String toCallString(String moduleName, Object param, boolean function){
		StringBuilder sb = new StringBuilder();

		if(function){
			sb.append("{? = call ");
		} else {
			sb.append("{call ");
		}

		sb.append(moduleName);
		sb.append("(");
		if (param != null){
			StringBuilder p = new StringBuilder();
			BeanDesc beanDesc = beanDescFactory.getBeanDesc(param);
			int parameterCount = 0;
			for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
				PropertyDesc pd = beanDesc.getPropertyDesc(i);
				if (needsParameter(pd)){
					if (parameterCount > 0) {
						p.append(", ");
					}
					if (parameterCount >= 0) {
						p.append("?");
					}
					parameterCount++;
				}
			}
			sb.append(p.toString());
		}
		sb.append(")");
		sb.append("}");

		return sb.toString();
	}

	protected boolean needsParameter(PropertyDesc pd){
		ResultSet resultSet = pd.getAnnotation(ResultSet.class);
		if (resultSet != null){
			if (dialect.needsParameterForResultSet()){
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public <T> List<T> getResultListBySql(Class<T> clazz, String sql) {
		return getResultListBySql(clazz, sql, new Object[0]);
	}

	public <T> List<T> getResultListBySql(Class<T> clazz, String sql, Object... params) {
		return sqlExecutor.getResultList(clazz, sql, params);
	}

	public <T> T getSingleResultBySql(Class<T> clazz, String sql) {
		return getSingleResultBySql(clazz, sql, new Object[0]);
	}

	public <T> T getSingleResultBySql(Class<T> clazz, String sql, Object... params) {
		return sqlExecutor.getSingleResult(clazz, sql, params);
	}

	public <T, R> R iterateBySql(Class<T> clazz, IterationCallback<T, R> callback, String sql) {
		return this.<T, R> iterateBySql(clazz, callback, sql, new Object[0]);
	}

	public <T, R> R iterateBySql(Class<T> clazz, IterationCallback<T, R> callback, String sql, Object... params) {
		return sqlExecutor.<T, R> iterate(clazz, callback, sql, params);
	}

	public int executeUpdateBySql(String sql) {
		return executeUpdateBySql(sql, new Object[0]);
	}

	public int executeUpdateBySql(String sql, Object... params) {
		return sqlExecutor.executeUpdateSql(sql, params, null);
	}

//	public long getSequenceNextValue(String sequenceName) {
//		String sql = dialect.getSequenceSql(sequenceName);
//		if(sql == null){
//			throw new UnsupportedOperationException();
//		}
//		return getSingleResultBySql(Long.class, sql);
//	}

}
