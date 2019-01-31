package com.miragesql.miragesql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.MirageUtil;

/**
 * Entity operator for immutable.
 * This operator does not support Map-like object, for example HashMap and LinkedHashMap.
 */
public class ImmutableEntityOperator implements EntityOperator {
	
	/**
	 * Creates and returns one entity instance from the ResultSet.
	 *
	 * @param <T> the type parameter of entity class
	 * @param entityType the entity class
	 * @param rs the ResultSet
	 * @param meta the ResultSetMetaData
	 * @param columnCount the column count
	 * @param beanDesc the BeanDesc of the entity class
	 * @param dialect the Dialect
	 * @param valueTypes the list of ValueTypes
	 * @param nameConverter the NameConverter
	 *
	 * @return the instance of entity class or Map
	 *
	 * @throws EntityCreationFailedException if {@link EntityOperator} failed to create a result entity
	 */
	@Override
	public <T> T createEntity(Class<T> entityType, ResultSet rs, ResultSetMetaData meta, int columnCount,
		BeanDesc beanDesc, Dialect dialect, List<ValueType<?>> valueTypes, NameConverter nameConverter) {
		try {
			T entity = null;
			Constructor<T>[] constructors = (Constructor<T>[]) entityType.getConstructors();
			for(Constructor<T> constructor: constructors){
				iteration:
				try {
					Class<?>[] types = constructor.getParameterTypes();
					Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
					Object[] params = new Object[types.length];
					Column[] columns = new Column[types.length];
					for(int i = 0; i < params.length; i++) {
						for (Annotation annotation : parameterAnnotations[i]) {
							if (annotation instanceof Column) {
								columns[i] = (Column) annotation;
							}
						}
						if (columns[i] == null) {
							// go to next constructor if column annotation is not found.
							break iteration;
						}
					}
					for(int i = 0; i < params.length; i++){
						ValueType valueType = MirageUtil.getValueType(types[i], null, dialect, valueTypes);
						if(valueType != null){
							String name = columns[i].name();
							params[i] = valueType.get(types[i], rs, name);
						}
					}
					entity = constructor.newInstance(params);
				} catch (InstantiationException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
					// ignore
				}
			}
			if(entity == null) {
				throw new EntityCreationFailedException();
			}
			return entity;
		} catch (SQLException | SecurityException | IllegalArgumentException e) {
			throw new EntityCreationFailedException(e);
		}
	}
	
	
	public PrimaryKeyInfo getPrimaryKeyInfo(Class<?> clazz, PropertyDesc propertyDesc, NameConverter nameConverter) {
		PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);
		if(primaryKey == null){
			return null;
		}
		return new PrimaryKeyInfo(primaryKey.generationType(), primaryKey.generator());
	}
	
	public ColumnInfo getColumnInfo(Class<?> clazz, PropertyDesc propertyDesc, NameConverter nameConverter) {
		Column column = propertyDesc.getAnnotation(Column.class);
		if(column == null){
			return null;
		}
		return new ColumnInfo(column.name());
	}
}
