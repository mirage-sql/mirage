package jp.sf.amateras.mirage.util;

import java.util.List;

import jp.sf.amateras.mirage.EntityOperator;
import jp.sf.amateras.mirage.EntityOperator.ColumnInfo;
import jp.sf.amateras.mirage.EntityOperator.PrimaryKeyInfo;
import jp.sf.amateras.mirage.annotation.Column;
import jp.sf.amateras.mirage.annotation.PrimaryKey.GenerationType;
import jp.sf.amateras.mirage.annotation.Table;
import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;
import jp.sf.amateras.mirage.dialect.Dialect;
import jp.sf.amateras.mirage.naming.NameConverter;
import jp.sf.amateras.mirage.parser.SqlContext;
import jp.sf.amateras.mirage.parser.SqlContextImpl;
import jp.sf.amateras.mirage.type.ValueType;

public class MirageUtil {

//	/**
//	 * Creates and returns one entity instance from the ResultSet.
//	 *
//	 * @param <T> the type parameter of entity class
//	 * @param clazz the entity class
//	 * @param rs the ResultSet
//	 * @param meta the ResultSetMetaData
//	 * @param columnCount the column count
//	 * @param beanDesc the BeanDesc of the entity class
//	 * @param dialect the Dialect
//	 * @param valueTypes the list of ValueTypes
//	 * @param nameConverter the NameConverter
//	 * @return the instanceo of entity class or Map
//	 * @throws InstantiationException
//	 * @throws IllegalAccessException
//	 * @throws SQLException
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> T createEntity(Class<T> clazz, ResultSet rs,
//			ResultSetMetaData meta, int columnCount, BeanDesc beanDesc,
//			Dialect dialect, List<ValueType> valueTypes, NameConverter nameConverter)
//			throws InstantiationException, IllegalAccessException, SQLException {
//
//		System.out.println("** 1");
//
//		if(dialect.getValueType() != null){
//			ValueType valueType = dialect.getValueType();
//			if(valueType.isSupport(clazz)){
//				return (T) valueType.get(clazz, rs, 1);
//			}
//		}
//
//		System.out.println("** 2");
//
//		for(ValueType valueType: valueTypes){
//			if(valueType.isSupport(clazz)){
//				return (T) valueType.get(clazz, rs, 1);
//			}
//		}
//
//		T entity = null;
//
//		System.out.println("** 3");
//
//		try {
//			if(clazz == Map.class){
//				entity = (T) new HashMap<String, Object>();
//			} else {
//				Constructor<T> constructor = clazz.getConstructor(new Class<?>[0]);
//				entity = constructor.newInstance();
//			}
//
//			for(int i = 0; i < columnCount; i++){
//				String columnName = meta.getColumnName(i + 1);
//				String propertyName = nameConverter.columnToProperty(columnName);
//
//				PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
//
//				System.out.println("---");
//				System.out.println(propertyName);
//
//				if(pd != null){
//					Class<?> fieldType = pd.getPropertyType();
//					ValueType valueType = getValueType(fieldType, dialect, valueTypes);
//					System.out.println(fieldType.toString() + "=" + pd);
//					if(valueType != null){
//						pd.setValue(entity, valueType.get(fieldType, rs, columnName));
//					}
//				}
//			}
//
//			return entity;
//
//		} catch(NoSuchMethodException e){
//			e.printStackTrace();
//		} catch(InvocationTargetException e){
//			e.printStackTrace();
//		}
//
//		System.out.println("** 4");
//
//		// Can't find a default constructor
//		Class<?>[] types = new Class<?>[columnCount];
//		Object[] values = new Object[columnCount];
//		for(int i = 0; i < columnCount; i++){
//			String columnName = meta.getColumnName(i + 1);
//			String propertyName = nameConverter.columnToProperty(columnName);
//
//			PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
//			System.out.println("name[" + i + "]=" + propertyName);
//
//			if(pd != null){
//				Class<?> fieldType = pd.getPropertyType();
//				System.out.println("type[" + i + "]=" + fieldType);
//				ValueType valueType = getValueType(fieldType, dialect, valueTypes);
//				if(valueType != null){
//					System.out.println("valueType[" + i + "]=" + valueType);
//					types[i] = fieldType;
//					values[i] = valueType.get(fieldType, rs, columnName);
//				}
//			}
//		}
//
//		System.out.println("** 5");
//
//		try {
//			Constructor<T> constructor = clazz.getConstructor(types);
//			entity = constructor.newInstance(values);
//
//		} catch(NoSuchMethodException ex){
//			throw new InstantiationException("Constructor is not found.");
//		} catch(InvocationTargetException ex){
//			throw new InstantiationException("Constructor is not found.");
//		}
//
//		System.out.println("** 6");
//
//		return entity;
//	}

	public static ValueType<?> getValueType(
			Class<?> type, Dialect dialect, List<ValueType<?>> valueTypes){

		if(dialect.getValueType() != null){
			ValueType<?> valueType = dialect.getValueType();
			if(valueType.isSupport(type)){
				return valueType;
			}
		}

		for(ValueType<?> valueType: valueTypes){
			if(valueType.isSupport(type)){
				return valueType;
			}
		}

		return null;
	}

	/**
	 * Returns the {@link SqlContext} instance.
	 * 
	 * @param param the parameter object
	 * @return {@link SqlContext} instance
	 */
	public static SqlContext getSqlContext(Object param) {
		SqlContext context = new SqlContextImpl();

		if (param != null) {
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(param);
			for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
				PropertyDesc pd = beanDesc.getPropertyDesc(i);
				context.addArg(pd.getPropertyName(), pd.getValue(param), pd
						.getPropertyType());
			}
		}

		return context;
	}

	/**
	 * Returns the table name from the entity.
	 * <p>
	 * If the entity class has {@link Table} annotation then this method returns the annotated table name,
	 * otherwise creates table name from the entity class name using {@link NameConverter}.
	 *
	 * @param entityClass the entity class
	 * @param nameConverter the name converter
	 * @return the table name
	 */
	public static String getTableName(Class<?> entityClass, NameConverter nameConverter){
		Table table = entityClass.getAnnotation(Table.class);
		if(table != null){
			return table.name();
		} else {
			return nameConverter.entityToTable(entityClass.getName());
		}
	}

	/**
	 * Returns the column name from the property.
	 * <p>
	 * If the property has {@link Column} annotation then this method returns the annotated column name,
	 * otherwise creates column name from the property name using {@link NameConverter}.
	 *
	 * @param pd the property
	 * @param nameConverter the name converter
	 * @return the column name
	 */
	public static String getColumnName(EntityOperator entityOperator, Class<?> clazz, PropertyDesc pd, NameConverter nameConverter){
		ColumnInfo column = entityOperator.getColumnInfo(clazz, pd, nameConverter);
		if(column != null){
			return column.name;
		} else {
			return nameConverter.propertyToColumn(pd.getPropertyName());
		}
	}

	/**
	 * Builds select (by primary keys) SQL from the entity class.
	 *
	 * @param entity the entity class to select
	 * @param nameConverter the name converter
	 * @return Select SQL
	 * @throws RuntimeException the entity class has no primary keys
	 */
	public static String buildSelectSQL(EntityOperator entityOperator, Class<?> clazz, NameConverter nameConverter){
		StringBuilder sb = new StringBuilder();
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);

		sb.append("SELECT * FROM ");
		sb.append(MirageUtil.getTableName(clazz, nameConverter));
		sb.append(" WHERE ");

		int count = 0;

		for(int i=0; i<beanDesc.getPropertyDescSize(); i++){
			PropertyDesc pd = beanDesc.getPropertyDesc(i);
			PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(clazz, pd, nameConverter);
			if(primaryKey != null){
				if(count != 0){
					sb.append(" AND ");
				}
				sb.append(MirageUtil.getColumnName(entityOperator, clazz, pd, nameConverter));
				sb.append(" = ?");
				count++;
			}
		}
		if(count == 0){
			throw new RuntimeException(
					"Primary key is not found: " + clazz.getName());
		}

		return sb.toString();
	}

	/**
	 * Builds insert SQL and correct parameters from the entity.
	 *
	 * @param entity the entity to insert
	 * @param nameConverter the name converter
	 * @param params the list of parameters
	 * @return Insert SQL
	 */
	public static String buildInsertSql(EntityOperator entityOperator, Object entity, NameConverter nameConverter, List<Object> params){
		StringBuilder sb = new StringBuilder();
		Class<?> clazz = entity.getClass();
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);

		sb.append("INSERT INTO ").append(getTableName(clazz, nameConverter)).append(" (");
		{
			int count = 0;
			for(int i = 0; i < beanDesc.getPropertyDescSize(); i++){
				PropertyDesc pd = beanDesc.getPropertyDesc(i);
				PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(clazz, pd, nameConverter);
				if((primaryKey == null || primaryKey.generationType != GenerationType.IDENTITY)
						&& !pd.isTransient() && pd.isReadable() ){
					if(count != 0){
						sb.append(", ");
					}
					sb.append(getColumnName(entityOperator, clazz, pd, nameConverter));
					count++;
				}
			}
		}
		sb.append(") VALUES (");
		{
			int count = 0;
			for(int i = 0; i < beanDesc.getPropertyDescSize(); i++){
				PropertyDesc pd = beanDesc.getPropertyDesc(i);
				PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(clazz, pd, nameConverter);
				if((primaryKey == null || primaryKey.generationType != GenerationType.IDENTITY)
						&& !pd.isTransient() && pd.isReadable() ){
					if(count != 0){
						sb.append(", ");
					}
					sb.append("?");

					params.add(pd.getValue(entity));

					count++;
				}
			}
		}
		sb.append(")");

		return sb.toString();
	}

	/**
	 * Builds update SQL and correct parameters from the entity.
	 *
	 * @param entity the entity to update
	 * @param nameConverter the name converter
	 * @param params the list of parameters
	 * @return Update SQL
	 */
	public static String buildUpdateSql(EntityOperator entityOperator, Object entity, NameConverter nameConverter, List<Object> params){
		StringBuilder sb = new StringBuilder();
		Class<?> clazz = entity.getClass();

		sb.append("UPDATE ").append(getTableName(clazz, nameConverter)).append(" SET ");

		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
		{
			int count = 0;
			for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
				PropertyDesc pd = beanDesc.getPropertyDesc(i);
				PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(clazz, pd, nameConverter);
				if(primaryKey == null && !pd.isTransient() && pd.isReadable() ){
					if (count != 0) {
						sb.append(", ");
					}
					sb.append(getColumnName(entityOperator, clazz, pd, nameConverter)).append(" = ?");
					params.add(pd.getValue(entity));
					count++;
				}
			}
		}
		sb.append(" WHERE ");
		{
			int count = 0;
			for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
				PropertyDesc pd = beanDesc.getPropertyDesc(i);
				PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(clazz, pd, nameConverter);
				if(primaryKey != null && pd.isReadable() ){
					if(count != 0){
						sb.append(" AND ");
					}
					sb.append(getColumnName(entityOperator, clazz, pd, nameConverter)).append(" = ? ");
					params.add(pd.getValue(entity));
					count++;
				}
			}
			if(count == 0){
				throw new RuntimeException(
						"Primary key is not found: " + entity.getClass().getName());
			}
		}

		return sb.toString();
	}

	/**
	 * Builds delete SQL and correct parameters from the entity.
	 *
	 * @param entity the entity to delete
	 * @param nameConverter the name converter
	 * @param params the list of parameters
	 * @return Delete SQL
	 */
	public static String buildDeleteSql(EntityOperator entityOperator, Object entity, NameConverter nameConverter, List<Object> params){
		StringBuilder sb = new StringBuilder();
		sb.append("DELETE FROM ").append(getTableName(entity.getClass(), nameConverter));
		sb.append(" WHERE ");

		boolean hasPrimaryKey = false;

		Class<?> clazz = entity.getClass();
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);

		for(int i=0;i<beanDesc.getPropertyDescSize();i++){
			PropertyDesc pd = beanDesc.getPropertyDesc(i);
			PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(clazz, pd, nameConverter);
			if(primaryKey != null && pd.isReadable()){
				if(!params.isEmpty()){
					sb.append(" AND ");
				}
				sb.append(getColumnName(entityOperator, clazz, pd, nameConverter)).append("=?");
				params.add(pd.getValue(entity));
				hasPrimaryKey = true;
			}
		}

		if(hasPrimaryKey == false){
			throw new RuntimeException(
					"Primary key is not found: " + entity.getClass().getName());
		}

		return sb.toString();
	}

}
