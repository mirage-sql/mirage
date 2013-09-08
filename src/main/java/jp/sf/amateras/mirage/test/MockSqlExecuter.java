package jp.sf.amateras.mirage.test;

import java.util.Collections;
import java.util.List;

import jp.sf.amateras.mirage.IterationCallback;
import jp.sf.amateras.mirage.SqlExecutor;
import jp.sf.amateras.mirage.annotation.PrimaryKey;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;

/**
 * The mock class of {@link SqlExecutor}. This class is used in the {@link MockSqlManager}.
 * <p>
 * This mock intercepts SQL execution and records them into {@link MirageTestContext}.
 * You can verify recorded SQL after the SQL execution.
 * And also this mock can return mocked values as results of SQL execution.
 * You can configure them using <code>MirageTestContext</code>.
 *
 * @author Naoki Takezoe
 * @see MockSqlManager
 * @see MirageTestContext
 */
public class MockSqlExecuter extends SqlExecutor {

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getResultList(Class<T> clazz, String sql, Object[] params) {
		MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, params));

		if(MirageTestContext.hasNextResult()){
			return (List<T>) MirageTestContext.getNextResult();
		}
		return Collections.emptyList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T, R> R iterate(Class<T> clazz, IterationCallback<T, R> callback,
			String sql, Object[] params) {
		MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, params));

		List<T> list = null;

		if(MirageTestContext.hasNextResult()){
			list = (List<T>) MirageTestContext.getNextResult();
		} else {
			list = Collections.emptyList();
		}

		R result = null;
		for(T entity: list){
			result = callback.iterate(entity);
		}

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getSingleResult(Class<T> clazz, String sql, Object[] params) {
		MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, params));

		if(MirageTestContext.hasNextResult()){
			return (T) MirageTestContext.getNextResult();
		}
		return null;
	}

	@Override
	public int executeUpdateSql(String sql, PropertyDesc[] propDescs, Object entity) {
		MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, propDescs, entity));

		if(entity != null){
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(entity.getClass());
			int size = beanDesc.getPropertyDescSize();
			for(int i=0; i < size; i++){
				PropertyDesc propertyDesc = beanDesc.getPropertyDesc(i);
				PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);
				if(primaryKey != null && primaryKey.generationType() == GenerationType.IDENTITY){
					propertyDesc.setValue(entity, MirageTestContext.getNextVal(
							entity.getClass(), propertyDesc.getPropertyName()));
				}
			}
		}

		if(MirageTestContext.hasNextResult()){
			return (Integer) MirageTestContext.getNextResult();
		}
		return 0;
	}

	@Override
	public int executeBatchUpdateSql(String sql, List<PropertyDesc[]> propDescsList, Object[] entities) {
		for (int i = 0; i < propDescsList.size(); i++) {
			PropertyDesc[] propDescs = propDescsList.get(i);
			MirageTestContext.addExecutedSql(new ExecutedSQLInfo(sql, propDescs, entities[i]));
		}

		if(entities != null){
			for(Object entity: entities){
				BeanDesc beanDesc = BeanDescFactory.getBeanDesc(entity.getClass());
				int size = beanDesc.getPropertyDescSize();
				for(int i=0; i < size; i++){
					PropertyDesc propertyDesc = beanDesc.getPropertyDesc(i);
					PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);
					if(primaryKey != null && primaryKey.generationType() == GenerationType.IDENTITY){
						propertyDesc.setValue(entity, MirageTestContext.getNextVal(
								entity.getClass(), propertyDesc.getPropertyName()));
					}
				}
			}
		}

		if(MirageTestContext.hasNextResult()){
			return (Integer) MirageTestContext.getNextResult();
		}

		return 0;
	}

}
