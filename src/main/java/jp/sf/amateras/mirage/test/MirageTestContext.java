package jp.sf.amateras.mirage.test;

import static junit.framework.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.amateras.mirage.DefaultEntityOperator;
import jp.sf.amateras.mirage.EntityOperator;
import jp.sf.amateras.mirage.SqlManager;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;
import jp.sf.amateras.mirage.naming.DefaultNameConverter;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.util.MirageUtil;
import junit.framework.Assert;

/**
 *
 * @author Naoki Takezoe
 * @see MockSqlManager
 */
public class MirageTestContext {

	private static BeanDescFactory beanDescFactory = new BeanDescFactory();
	private static NameConverter nameConverter = new DefaultNameConverter();
	private static EntityOperator entityOperator = new DefaultEntityOperator();
	private static List<Object> resultList = new ArrayList<Object>();
	private static List<ExecutedSQLInfo> executedSqlList = new ArrayList<ExecutedSQLInfo>();
	private static Map<String, Long> sequenceMap = new HashMap<String, Long>();

	/**
	 * Initializes the MirageTestContext.
	 * <p>
	 * This method clears the result list and the executed sql list.
	 * You have to invoke this method at <code>setUp()</code> of your test case.
	 */
	public static void initMirageTestContext(){
		resultList.clear();
		executedSqlList.clear();
		sequenceMap.clear();
	}

	public static void setNameConverter(NameConverter nameConverter){
		MirageTestContext.nameConverter = nameConverter;
	}

	public static void addResult(Object result){
		resultList.add(result);
	}

	static boolean hasNextResult(){
		return !resultList.isEmpty();
	}

	static Object getNextResult(){
		return resultList.remove(0);
	}

	static void addExecutedSql(ExecutedSQLInfo executedSql){
		System.out.println(String.format("[SQL] %s", normalizeSql(executedSql.getSql())));

		Object[] params = executedSql.getParams();
		for(int i=0; i < params.length; i++){
			System.out.println(String.format("[SQL] params[%d]: %s", i, params[i]));
		}

		executedSqlList.add(executedSql);
	}

	static long getNextVal(Class<?> entity, String propertyName){
		String key = entity.getClass().getName() + "#" + propertyName;
		Long value = sequenceMap.get(key);
		if(value == null){
			value = -1l;
		}
		value = value + 1;
		sequenceMap.put(key, value);
		return value;
	}

	public static ExecutedSQLInfo getExecutedSQLInfo(int index){
		return executedSqlList.get(index);
	}

	/**
	 * Verifies the number of executed SQLs.
	 *
	 * @param expected the expected number of executed SQLs
	 */
	public static void verifySqlNumber(int expected){
		assertEquals(expected, executedSqlList.size());
	}

	/**
	 * Verifies the executed SQL.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param sql the expected SQL
	 */
	public static void verifySql(int indexOfSql, String sql){
		ExecutedSQLInfo executedSql = executedSqlList.get(indexOfSql);
		String result = executedSql.getSql();
		Assert.assertEquals(normalizeSql(sql), normalizeSql(result));
	}

	/**
	 * Verifies the executed SQL and parameters.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param sql the expected SQL
	 * @param values PreparedStatement parameters
	 */
	public static void verifySql(int indexOfSql, String sql, Object... values){
		verifySql(indexOfSql, sql);
		verifyParameters(indexOfSql, values);
	}

	/**
	 * Verifies the executed SQL using the regular expression.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param regexp the pattern of expected SQL
	 */
	public static void verifySqlByRegExp(int indexOfSql, String regexp){
		ExecutedSQLInfo executedSql = executedSqlList.get(indexOfSql);
		String result = normalizeSql(executedSql.getSql());
		Assert.assertTrue(result.matches(regexp));
	}

	/**
	 * Verifies the executed SQL and parameters using the regular expression.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param regexp the pattern of expected SQL
	 * @param values PreparedStatement parameters
	 */
	public static void verifySqlByRegExp(int indexOfSql, String regexp, Object... values){
		verifySqlByRegExp(indexOfSql, regexp);
		verifyParameters(indexOfSql, values);
	}

	/**
	 * Verifies the SQL and parameters which is executed by {@link SqlManager#findEntity(Class, Object...)}.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param id the values of primary key
	 */
	public static void verifyFindSql(int indexOfSql, Class<?> entityClass, Object... id){
		verifySql(indexOfSql, MirageUtil.buildSelectSQL(beanDescFactory, entityOperator, entityClass, nameConverter));
		verifyParameters(indexOfSql, id);
	}

	/**
	 * Verifies the SQL and parameters which is executed by {@link SqlManager#insertEntity(Object)}.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param entity the entity which should be inserted
	 */
	public static void verifyInsertSql(int indexOfSql, Object entity){
		List<PropertyDesc> values = new ArrayList<PropertyDesc>();
		verifySql(indexOfSql, MirageUtil.buildInsertSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, values));
		verifyParameters(indexOfSql, entity, values.toArray(new PropertyDesc[values.size()]));
	}

	/**
	 * Verifies the SQL and parameters which is executed by {@link SqlManager#updateEntity(Object)}.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param entity the entity which should be updated
	 */
	public static void verifyUpdatedSql(int indexOfSql, Object entity){
		List<PropertyDesc> values = new ArrayList<PropertyDesc>();
		verifySql(indexOfSql, MirageUtil.buildUpdateSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, values));
		verifyParameters(indexOfSql, entity, values.toArray(new PropertyDesc[values.size()]));
	}

	/**
	 * Verifies the SQL and parameters which is executed by {@link SqlManager#deleteEntity(Object)}.
	 *
	 * @param indexOfSql the index of executed SQL
	 * @param entity the entity which should be deleted
	 */
	public static void verifyDeleteSql(int indexOfSql, Object entity){
		List<PropertyDesc> values = new ArrayList<PropertyDesc>();
		verifySql(indexOfSql, MirageUtil.buildDeleteSql(beanDescFactory, entityOperator, entity.getClass(), nameConverter, values));
		verifyParameters(indexOfSql, entity, values.toArray(new PropertyDesc[values.size()]));
	}

	private static void verifyParameters(int indexOfSql, Object entity, PropertyDesc[] propDescs){
		ExecutedSQLInfo executedSql = executedSqlList.get(indexOfSql);
		Object[] params = executedSql.getParams();

		assertEquals(propDescs.length, params.length);

		for(int i=0; i < propDescs.length; i++){
			assertEquals(propDescs[i].getValue(entity), params[i]);
		}
	}

	private static void verifyParameters(int indexOfSql, Object[] values){
		ExecutedSQLInfo executedSql = executedSqlList.get(indexOfSql);
		Object[] params = executedSql.getParams();

		assertEquals(values.length, params.length);

		for(int i=0; i < values.length; i++){
			assertEquals(values[i], params[i]);
		}
	}

	private static String normalizeSql(String sql){
		sql = sql.replaceAll("--.*", " ");
		sql = sql.replaceAll("\r\n", "\n");
		sql = sql.replaceAll("\r", "\n");
		sql = sql.replaceAll("\n", " ");
		sql = sql.replaceAll("/\\*.*\\*/", " ");
		sql = sql.replaceAll("[ \t]+", " ");
		sql = sql.toUpperCase().trim();

		return sql;
	}

}
