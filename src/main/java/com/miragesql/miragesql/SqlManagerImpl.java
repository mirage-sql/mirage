package com.miragesql.miragesql;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.annotation.ResultSet;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.dialect.StandardDialect;
import com.miragesql.miragesql.exception.IORuntimeException;
import com.miragesql.miragesql.naming.DefaultNameConverter;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.parser.Node;
import com.miragesql.miragesql.parser.SqlContext;
import com.miragesql.miragesql.parser.SqlParserImpl;
import com.miragesql.miragesql.provider.ConnectionProvider;
import com.miragesql.miragesql.type.BigDecimalValueType;
import com.miragesql.miragesql.type.BooleanPrimitiveValueType;
import com.miragesql.miragesql.type.BooleanValueType;
import com.miragesql.miragesql.type.ByteArrayValueType;
import com.miragesql.miragesql.type.DoublePrimitiveValueType;
import com.miragesql.miragesql.type.DoubleValueType;
import com.miragesql.miragesql.type.FloatPrimitiveValueType;
import com.miragesql.miragesql.type.FloatValueType;
import com.miragesql.miragesql.type.IntegerPrimitiveValueType;
import com.miragesql.miragesql.type.IntegerValueType;
import com.miragesql.miragesql.type.LongPrimitiveValueType;
import com.miragesql.miragesql.type.LongValueType;
import com.miragesql.miragesql.type.ShortPrimitiveValueType;
import com.miragesql.miragesql.type.ShortValueType;
import com.miragesql.miragesql.type.SqlDateValueType;
import com.miragesql.miragesql.type.StringValueType;
import com.miragesql.miragesql.type.TimeValueType;
import com.miragesql.miragesql.type.TimestampValueType;
import com.miragesql.miragesql.type.UtilDateValueType;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.type.enumerate.EnumOneBasedOrdinalValueType;
import com.miragesql.miragesql.type.enumerate.EnumOrdinalValueType;
import com.miragesql.miragesql.type.enumerate.EnumStringValueType;
import com.miragesql.miragesql.util.IOUtil;
import com.miragesql.miragesql.util.MirageUtil;
import com.miragesql.miragesql.util.Validate;

public class SqlManagerImpl implements SqlManager {

//	private static final Logger logger = Logger.getLogger(SqlManagerImpl.class.getName());

    protected BeanDescFactory beanDescFactory;

    protected ConnectionProvider connectionProvider;

    protected NameConverter nameConverter;

    protected EntityOperator entityOperator;

    protected Dialect dialect = new StandardDialect();

    protected SqlExecutor sqlExecutor = new SqlExecutor();

    protected CallExecutor callExecutor = new CallExecutor();

    protected Map<SqlResource, Node> nodeCache = new ConcurrentHashMap<>();

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
//		addValueType(new com.miragesql.miragesql.type.DefaultValueType());

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

    /**{@inheritDoc}*/
    public void setNameConverter(NameConverter nameConverter) {
        this.nameConverter = nameConverter;
        this.sqlExecutor.setNameConverter(nameConverter);
        this.callExecutor.setNameConverter(nameConverter);
    }

    public NameConverter getNameConverter(){
        return this.nameConverter;
    }

    /**{@inheritDoc}*/
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        this.sqlExecutor.setConnectionProvider(connectionProvider);
        this.callExecutor.setConnectionProvider(connectionProvider);
    }

    /**{@inheritDoc}*/
    public void setEntityOperator(EntityOperator entityOperator){
        this.entityOperator = entityOperator;
        this.sqlExecutor.setEntityOperator(entityOperator);
        this.callExecutor.setEntityOperator(entityOperator);
    }

    public ConnectionProvider getConnectionProvider(){
        return this.connectionProvider;
    }

    /**{@inheritDoc}*/
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

    /**{@inheritDoc}*/
    public int executeUpdate(SqlResource resource) {
        return executeUpdate(resource, null);
    }

    /**{@inheritDoc}*/
    public int executeUpdate(SqlResource resource, Object param) {
        Node node = prepareNode(resource);
        SqlContext context = prepareSqlContext(param);
        node.accept(context);

        return sqlExecutor.executeUpdateSql(context.getSql(), context.getBindVariables(), null);
    }

    /**{@inheritDoc}*/
    public <T> List<T> getResultList(Class<T> clazz, SqlResource resource) {
        return getResultList(clazz, resource, null);
    }

    /**{@inheritDoc}*/
    public <T> List<T> getResultList(Class<T> clazz, SqlResource resource, Object param) {
        Node node = prepareNode(resource);
        SqlContext context = prepareSqlContext(param);
        node.accept(context);

        return sqlExecutor.getResultList(clazz, context.getSql(), context.getBindVariables());
    }

    /**{@inheritDoc}*/
    public <T> T getSingleResult(Class<T> clazz, SqlResource resource) {
        return getSingleResult(clazz, resource, null);
    }

    /**{@inheritDoc}*/
    public <T> T getSingleResult(Class<T> clazz, SqlResource resource, Object param) {
        Node node = prepareNode(resource);
        SqlContext context = prepareSqlContext(param);
        node.accept(context);

        return sqlExecutor.getSingleResult(clazz, context.getSql(), context.getBindVariables());
    }

    /**{@inheritDoc}*/
    public int deleteEntity(Object entity) {
        List<PropertyDesc> propDescs = new ArrayList<>();
        String executeSql = MirageUtil.buildDeleteSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

        return sqlExecutor.executeUpdateSql(executeSql, propDescs.toArray(new PropertyDesc[propDescs.size()]), entity);
    }

    /**{@inheritDoc}*/
    public <T> int deleteBatch(T... entities) {
        if(entities.length == 0){
            return 0;
        }

        List<PropertyDesc[]> paramsList = new ArrayList<>();
        String executeSql = null;

        for(Object entity: entities){
            List<PropertyDesc> propDescs = new ArrayList<>();
            String sql = MirageUtil.buildDeleteSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

            if(executeSql == null){
                executeSql = sql;

            } else if(!sql.equals(executeSql)){
                throw new IllegalArgumentException("A different entity is contained in the entity list.");
            }

            paramsList.add(propDescs.toArray(new PropertyDesc[propDescs.size()]));
        }

        return sqlExecutor.executeBatchUpdateSql(executeSql, paramsList, entities);
    }

    /**{@inheritDoc}*/
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

    /**{@inheritDoc}*/
    public int insertEntity(Object entity) {
        fillPrimaryKeysBySequence(entity);

        List<PropertyDesc> propDescs = new ArrayList<>();
        String sql = MirageUtil.buildInsertSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

        return sqlExecutor.executeUpdateSql(sql, propDescs.toArray(new PropertyDesc[propDescs.size()]), entity);
    }

    /**{@inheritDoc}*/
    public <T> int insertBatch(T... entities){
        if(entities.length == 0){
            return 0;
        }

        List<PropertyDesc[]> propDescsList = new ArrayList<>();
        String executeSql = null;

        for(Object entity: entities){
            fillPrimaryKeysBySequence(entity);

            List<PropertyDesc> propDescs = new ArrayList<>();
            String sql = MirageUtil.buildInsertSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

            if(executeSql == null){
                executeSql = sql;

            } else if(!sql.equals(executeSql)){
                throw new IllegalArgumentException("A different entity is contained in the entity list.");
            }

            propDescsList.add(propDescs.toArray(new PropertyDesc[propDescs.size()]));
        }
        // TODO ここ？
        return sqlExecutor.executeBatchUpdateSql(executeSql, propDescsList, entities);
    }

    /**{@inheritDoc}*/
    public <T> int insertBatch(List<T> entities){
        return insertBatch(entities.toArray());
    }

    /**{@inheritDoc}*/
    public int updateEntity(Object entity) {
        List<PropertyDesc> propDescs = new ArrayList<>();
        String executeSql = MirageUtil.buildUpdateSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

        return sqlExecutor.executeUpdateSql(executeSql, propDescs.toArray(new PropertyDesc[propDescs.size()]), entity);
    }

    /**{@inheritDoc}*/
    public <T> int updateBatch(T... entities) {
        if(entities.length == 0){
            return 0;
        }

        List<PropertyDesc[]> propDescsList = new ArrayList<>();
        String executeSql = null;

        for(Object entity: entities){
            List<PropertyDesc> propDescs = new ArrayList<>();
            String sql = MirageUtil.buildUpdateSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, propDescs);

            if(executeSql == null){
                executeSql = sql;

            } else if(!sql.equals(executeSql)){
                throw new IllegalArgumentException("A different entity is contained in the entity list.");
            }

            propDescsList.add(propDescs.toArray(new PropertyDesc[propDescs.size()]));
        }

        return sqlExecutor.executeBatchUpdateSql(executeSql, propDescsList, entities);
    }

    /**{@inheritDoc}*/
    public <T> int updateBatch(List<T> entities) {
        return updateBatch(entities.toArray());
    }

    /**{@inheritDoc}*/
    public <T> T findEntity(Class<T> clazz, Object... id) {
        String executeSql = MirageUtil.buildSelectSQL(beanDescFactory, entityOperator, clazz, nameConverter);
        return sqlExecutor.getSingleResult(clazz, executeSql, id);
    }

    /**
     * Sets the value types.
     *
     * @param valueTypes the value types to set.
     *
     * @throws IllegalArgumentException if the {@code valueTypes} is {@code null} or
     * an element in the {@code valueTypes} is {@code null}
     */
    public void setValueTypes(List<ValueType<?>> valueTypes) {
        Validate.noNullElements(valueTypes);
        this.sqlExecutor.setValueTypes(valueTypes);
        this.callExecutor.setValueTypes(valueTypes);
    }

    /**{@inheritDoc}*/
    public void addValueType(ValueType<?> valueType) {
        this.sqlExecutor.addValueType(valueType);
        this.callExecutor.addValueType(valueType);
    }

    /**{@inheritDoc}*/
    public int getCount(SqlResource resource) {
        return getCount(resource, null);
    }

    /**{@inheritDoc}*/
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

    /**{@inheritDoc}*/
    public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, SqlResource resource) {
        return this.<T, R> iterate(clazz, callback, resource, null);
    }

    /**{@inheritDoc}*/
    public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback, SqlResource resource, Object param) {
        Node node = prepareNode(resource);
        SqlContext context = prepareSqlContext(param);
        node.accept(context);

        return sqlExecutor.<T, R> iterate(clazz, callback, context.getSql(), context.getBindVariables());
    }

    /**{@inheritDoc}*/
    public void call(String procedureName){
        String sql = toCallString(procedureName, false);
        callExecutor.call(sql);
    }

    /**{@inheritDoc}*/
    public void call(String procedureName, Object parameter){
        String sql = toCallString(procedureName, parameter, false);
        callExecutor.call(sql, parameter);
    }

    /**{@inheritDoc}*/
    public <T> T call(Class<T> resultClass, String functionName){
        String sql = toCallString(functionName, true);
        return callExecutor.call(resultClass, sql);
    }

    /**{@inheritDoc}*/
    public <T> T call(Class<T> resultClass, String functionName, Object param){
        String sql = toCallString(functionName, param, true);
        return callExecutor.call(resultClass, sql, param);
    }

    /**{@inheritDoc}*/
    public <T> List<T> callForList(Class<T> resultClass, String functionName){
        String sql = toCallString(functionName, true);
        return callExecutor.callForList(resultClass, sql);
    }

    /**{@inheritDoc}*/
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
}
