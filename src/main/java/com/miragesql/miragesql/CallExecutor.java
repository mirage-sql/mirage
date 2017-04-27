package com.miragesql.miragesql;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.miragesql.miragesql.annotation.InOut;
import com.miragesql.miragesql.annotation.Out;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.exception.SQLRuntimeException;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.provider.ConnectionProvider;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.JdbcUtil;
import com.miragesql.miragesql.util.ModifierUtil;
import com.miragesql.miragesql.util.ReflectionUtil;
import com.miragesql.miragesql.util.Validate;

public class CallExecutor {

	private static final Logger logger = LoggerFactory.getLogger(CallExecutor.class);

	private BeanDescFactory beanDescFactory;
	private NameConverter nameConverter;
	private ConnectionProvider connectionProvider;
	private List<ValueType<?>> valueTypes = new ArrayList<ValueType<?>>();
	private Dialect dialect;
	private EntityOperator entityOperator;
	
	
	public void setBeanDescFactory(BeanDescFactory beanDescFactory) {
		this.beanDescFactory = beanDescFactory;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider){
		this.connectionProvider = connectionProvider;
	}

	public void setNameConverter(NameConverter nameConverter){
		this.nameConverter = nameConverter;
	}

	public void setDialect(Dialect dialect){
		this.dialect = dialect;
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

	public void setEntityOperator(EntityOperator entityOperator){
		this.entityOperator = entityOperator;
	}

	public void call(String sql){
		call(sql, null);
	}

	public void call(String sql, Object parameter){
		CallableStatement stmt = null;
		boolean functionCall = false;
		try {
			List<Param> paramList = new ArrayList<Param>();
			List<Param> nonParamList = new ArrayList<Param>();

			stmt = connectionProvider.getConnection().prepareCall(sql);

			prepareParameters(paramList, nonParamList, stmt, parameter);

			setParameter(paramList, stmt);

			boolean resultSetGettable = execute(stmt, sql, paramList);

			handleNonParamResultSets(nonParamList, stmt, parameter, resultSetGettable);
			handleOutParams(paramList, stmt, parameter, functionCall);

		} catch (SQLException e) {
			throw new SQLRuntimeException(e);

		} finally{
			JdbcUtil.close(stmt);
		}
	}

	public <T> T call(Class<T> resultClass, String sql){
		return call(resultClass, sql, null);
	}

	public <T> T call(Class<T> resultClass, String sql, Object parameter){
		CallableStatement stmt = null;
		boolean functionCall = true;
		try {
			List<Param> paramList = new ArrayList<Param>();
			List<Param> nonParamList = new ArrayList<Param>();

			stmt = connectionProvider.getConnection().prepareCall(sql);

			prepareReturnParameter(paramList, false, resultClass);
			prepareParameters(paramList, nonParamList, stmt, parameter);
			setParameter(paramList, stmt);

			boolean resultSetGettable = execute(stmt, sql, paramList);

			handleNonParamResultSets(nonParamList, stmt, parameter, resultSetGettable);

			final T result = this.<T> handleSingleResult(stmt, paramList);
			handleOutParams(paramList, stmt, parameter, functionCall);

			return result;

		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally{
			JdbcUtil.close(stmt);
		}
	}

	public <T> List<T> callForList(Class<T> resultClass, String sql){
		return callForList(resultClass, sql, null);
	}

	public <T> List<T> callForList(Class<T> resultClass, String sql, Object parameter){
		CallableStatement stmt = null;
		boolean functionCall = true;
		try {
			List<Param> paramList = new ArrayList<Param>();
			List<Param> nonParamList = new ArrayList<Param>();

			stmt = connectionProvider.getConnection().prepareCall(sql);

			prepareReturnParameter(paramList, true, resultClass);
			prepareParameters(paramList, nonParamList, stmt, parameter);
			setParameter(paramList, stmt);

			boolean resultSetGettable = execute(stmt, sql, paramList);

			handleNonParamResultSets(nonParamList, stmt, parameter, resultSetGettable);
			final List<T> result = handleResultList(paramList, resultClass, stmt);
			handleOutParams(paramList, stmt, parameter, functionCall);

			return result;

		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		} finally{
			JdbcUtil.close(stmt);
		}
	}

	protected void prepareParameters(List<Param> paramList, List<Param> nonParamList, CallableStatement stmt, Object parameter) throws SQLException {
		if (parameter == null){
			return;
		}
		Class<?> paramClass = parameter.getClass();
		// TODO SqlManagerの他のメソッドもシンプル型のパラメータには対応していないのでとりあえず。
//        if (ValueTypes.isSimpleType(paramClass)) {
//            addParam(paramList, parameter, paramClass);
//            return;
//        }

        for (final ParamDesc paramDesc : getParamDescs(paramClass)) {
            final Class<?> clazz = paramDesc.paramClass;
            final ValueType<?> valueType = paramDesc.valueType;

            switch (paramDesc.paramType) {
	            case RESULT_SET:
	                if (dialect.needsParameterForResultSet()) {
	                    addParam(paramList, paramDesc.propertyDesc, null, clazz, valueType, ParameterType.OUT);
	                } else {
	                    addNonParam(nonParamList, paramDesc.propertyDesc);
	                }
	                break;
	            case IN:
	            	Object inValue = paramDesc.propertyDesc.getValue(parameter);
	                addParam(paramList, paramDesc.propertyDesc, inValue, clazz, valueType, ParameterType.IN);
	                break;
	            case OUT:
	                addParam(paramList, paramDesc.propertyDesc, null, clazz, valueType, ParameterType.OUT);
	                break;
	            case IN_OUT:
	            	Object inOutValue = paramDesc.propertyDesc.getValue(parameter);
	                addParam(paramList, paramDesc.propertyDesc, inOutValue, clazz, valueType, ParameterType.IN_OUT);
	                break;
            }
        }
	}

    protected void prepareReturnParameter(List<Param> paramList, boolean resultList, Class<?> resultClass) {
        final ValueType<?> valueType = getValueType(resultList ? List.class : resultClass, null);
        final Param p = addParam(paramList, null, resultClass, valueType);
        p.paramType = ParameterType.OUT;
    }

    @SuppressWarnings("unchecked")
	protected void setParameter(List<Param> paramList, CallableStatement cs) {
        int size = paramList.size();
        try {
            for (int i = 0; i < size; i++) {
                Param param = paramList.get(i);
                switch (param.paramType) {
	                case IN:
	                	param.valueType.set(param.paramClass, cs, param.value, i + 1);
	                    break;
	                case OUT:
	                	param.valueType.registerOutParameter(param.paramClass, cs, i + 1);
	                    break;
	                case IN_OUT:
	                	param.valueType.set(param.paramClass, cs, param.value, i + 1);
	                	param.valueType.registerOutParameter(param.paramClass, cs, i + 1);
	                    break;
	                default:
	                	// no op
                }
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected boolean execute(CallableStatement stmt, String sql, List<Param> paramList) throws SQLException{
		if(logger.isInfoEnabled()){
			logger.info(sql);
			printParameters(paramList);
		}
    	return stmt.execute();
    }

	private void printParameters(List<Param> paramList){
		if(paramList == null){
			return;
		}
		for (Param param : paramList) {
			if (param.paramType == ParameterType.IN || param.paramType == ParameterType.IN_OUT){
				PropertyDesc pd = param.propertyDesc;
				if (pd != null){
					logger.info(String.format("paramName=%s, value=%s", pd.getPropertyName(), param.value));
				} else {
					logger.info(String.format("paramClass=%s, value=%s", param.paramClass, param.value));
				}
			}
		}
	}

    protected void handleNonParamResultSets(List<Param> nonParamList, final CallableStatement cs,
            Object parameter, final boolean resultSetGettable) {
		if (parameter == null){
			return;
		}
        try {
            if (!resultSetGettable) {
                cs.getMoreResults();
            }
            for (int i = 0; i < nonParamList.size(); i++) {
                final ResultSet rs = getResultSet(cs);
                if (rs == null) {
                    break;
                }
                final Param param = nonParamList.get(i);
                PropertyDesc pd = param.propertyDesc;
                final Object value = handleResultSet(pd , cs.getResultSet());
                pd.setValue(parameter, value);
                cs.getMoreResults();
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected Object handleResultSet(PropertyDesc pd, final ResultSet rs) throws SQLException {
    	// TODO なんかこのへんのロジックがSqlExecuterと違う？
    	// TODO 複数件取得する場合はgetResultList()するんじゃなくてListを渡すのか
        if (!List.class.isAssignableFrom(pd.getField().getType())) {
            return handleSingleResult(pd.getField().getType(), rs);
        }
        final Class<?> elementClass = ReflectionUtil.getElementTypeOfListFromFieldType(pd.getField());
        if (elementClass == null) {
            throw new RuntimeException("field has not generics: " + pd.getField().getName());
        }
        return handleResultList(elementClass, rs);
    }

    protected <T> T handleSingleResult(final Class<T> resultClass, final ResultSet rs) throws SQLException {
    	ResultSetMetaData meta = rs.getMetaData();
    	BeanDesc beanDesc = beanDescFactory.getBeanDesc(resultClass);

    	return entityOperator.createEntity(resultClass, rs, meta, meta.getColumnCount(), beanDesc,
    			dialect, valueTypes, nameConverter);
    }

    @SuppressWarnings("unchecked")
    protected <T> T handleSingleResult(final CallableStatement cs, List<Param> paramList) {
        try {
            final Param param = paramList.get(0);
            return (T) param.valueType.get(param.paramClass, cs, 1);
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> handleResultList(List<Param> paramList, Class<?> resultClass, final CallableStatement cs) {
        try {
            final Param param = paramList.get(0);
            final ResultSet rs = ResultSet.class.cast(param.valueType.get(param.paramClass, cs, 1));
            Class<?> elementClass = null;
            final PropertyDesc pd = param.propertyDesc;
            if (pd == null){
            	elementClass = resultClass;

            } else {
                final Field field = pd.getField();
                if (field == null){
                	elementClass = resultClass;
                } else {
                	elementClass = ReflectionUtil.getElementTypeOfListFromFieldType(field);
                }
            }
            return (List<T>) handleResultList(elementClass, rs);

        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> List<T> handleResultList(final Class<T> elementClass, final ResultSet rs) throws SQLException {

		List<T> list = new ArrayList<T>();

		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();

		BeanDesc beanDesc = beanDescFactory.getBeanDesc(elementClass);

		while(rs.next()){
			T entity = entityOperator.createEntity(elementClass, rs, meta, columnCount, beanDesc,
					dialect, valueTypes, nameConverter);
			list.add(entity);
		}

		return list;
    }

//	protected Object handleResultSet(ResultSetHandler handler, ResultSet rs) {
//		Object result = null;
//		try {
//			result = handler.handle(rs);
//		}
//		catch (SQLException e) {
//			throw new SQLRuntimeException(e);
//		}
//		finally {
//			JdbcUtil.close(rs);
//		}
//		return result;
//	}

    protected void handleOutParams(List<Param> paramList, final CallableStatement cs, Object parameter,
    		boolean functionCall) {
    	if (parameter == null){
    		return;
    	}
        try {
            final int start = functionCall ? 1 : 0;
            for (int i = start; i < paramList.size(); i++) {
                final Param param = paramList.get(i);
                if (param.paramType == ParameterType.IN) {
                    continue;
                }
                PropertyDesc pd = param.propertyDesc;
                @SuppressWarnings("unchecked")
				Object value = param.valueType.get(param.paramClass, cs, i + 1);
                if (value instanceof ResultSet) {
                    value = handleResultSet(pd, (ResultSet) value);
                }
                pd.setValue(parameter, value);
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected ResultSet getResultSet(final CallableStatement cs) {
        try {
            for (;;) {
                final ResultSet rs = cs.getResultSet();
                if (rs != null) {
                    return rs;
                }
                if (cs.getUpdateCount() == -1) {
                    return null;
                }
                cs.getMoreResults();
            }
        } catch (final SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    protected ValueType<?> getValueType(Class<?> type, PropertyDesc propertyDesc){
    	if(dialect.getValueType() != null){
    		ValueType<?> valueType = dialect.getValueType();
    		if(valueType.isSupport(type, propertyDesc)){
    			return valueType;
    		}
    	}
    	for(ValueType<?> valueType: valueTypes){
    		if(valueType.isSupport(type, propertyDesc)){
    			return valueType;
    		}
    	}
    	return null;
    }

    protected void addParam(final List<Param> paramList, final PropertyDesc pd, final Object value, final Class<?> paramClass,
            final ValueType<?> valueType, final ParameterType paramType) {
        final Param p = addParam(paramList, value, paramClass, valueType);
        p.paramType = paramType;
        p.propertyDesc = pd;
    }

    protected Param addParam(List<Param> paramList, Object value, Class<?> paramClass) {
        if (paramClass == null) {
            throw new NullPointerException("paramClass");
        }
        ValueType<?> valueType = getValueType(paramClass, null);
        return addParam(paramList, value, paramClass, valueType);
    }

    protected Param addParam(List<Param> paramList, Object value, Class<?> paramClass, ValueType<?> valueType) {
        Param param = new Param(value, paramClass);
        param.valueType = valueType;
        paramList.add(param);
        return param;
    }

    protected Param addNonParam(final List<Param> nonParamList, final PropertyDesc pd) {
        final Param param = new Param();
        param.propertyDesc = pd;
        param.paramType = ParameterType.OUT;
        nonParamList.add(param);
        return param;
    }

    protected List<ParamDesc> getParamDescs(final Class<?> clazz) {
        return createParamDesc(clazz);
    }

    protected List<ParamDesc> createParamDesc(final Class<?> clazz) {
        BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);
        final List<ParamDesc> paramDescList = new ArrayList<ParamDesc>();

        for (int i = 0; i < beanDesc.getPropertyDescSize(); ++i) {
        	PropertyDesc pd = beanDesc.getPropertyDesc(i);
        	Field field = pd.getField();

            if (!ModifierUtil.isInstanceField(field)) {
                continue;
            }
            field.setAccessible(true);
            final ParamDesc paramDesc = new ParamDesc();
            paramDesc.propertyDesc = pd;
            paramDesc.name = field.getName();
            paramDesc.paramClass = field.getType();
			paramDesc.valueType = getValueType(paramDesc.paramClass, pd);

            if (pd.getAnnotation(com.miragesql.miragesql.annotation.ResultSet.class) != null) {
                paramDesc.paramType = ParameterType.RESULT_SET;
            } else if (pd.getAnnotation(Out.class) != null) {
                paramDesc.paramType = ParameterType.OUT;
            } else if (pd.getAnnotation(InOut.class) != null) {
                paramDesc.paramType = ParameterType.IN_OUT;
            } else {
                paramDesc.paramType = ParameterType.IN;
            }

            if (paramDesc.paramType != null){
            	paramDescList.add(paramDesc);
            }
        }

        return paramDescList;
    }

    protected static enum ParameterType {
        IN,
        IN_OUT,
        OUT,
        RESULT_SET
    }

    protected static class ParamDesc {

        public PropertyDesc propertyDesc;

        public String name;

        public Class<?> paramClass;

        public ParameterType paramType;

        @SuppressWarnings("rawtypes")
		public ValueType valueType;
    }

    protected static class Param {

        public Object value;

        public Class<?> paramClass;

        public ParameterType paramType = ParameterType.IN;

        @SuppressWarnings("rawtypes")
		public ValueType valueType;

        public PropertyDesc propertyDesc;

        public Param() {
        }

        public Param(Object value, Class<?> paramClass) {
            this.value = value;
            this.paramClass = paramClass;
        }
    }

}
