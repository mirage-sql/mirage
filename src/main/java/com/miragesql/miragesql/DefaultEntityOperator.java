package com.miragesql.miragesql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.type.ValueType;
import com.miragesql.miragesql.util.MirageUtil;

public class DefaultEntityOperator implements EntityOperator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultEntityOperator.class);

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
     * @return the instance of entity class or Map
     * @throws EntityCreationFailedException if {@link EntityOperator} failed to create a result entity
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T createEntity(Class<T> entityType, ResultSet rs,
            ResultSetMetaData meta, int columnCount, BeanDesc beanDesc,
            Dialect dialect, List<ValueType<?>> valueTypes, NameConverter nameConverter) {

        try {
            {
                ValueType valueType = MirageUtil.getValueType(entityType, null, dialect, valueTypes);
                if(valueType != null){
                    return (T) ((ValueType<T>) valueType).get(entityType, rs, 1);
                }
            }

            T entity = null;

            if(entityType == Map.class){
                entity = (T) new HashMap<String, Object>();
            } else {
                Constructor<T>[] constructors = (Constructor<T>[]) entityType.getDeclaredConstructors();
                for(Constructor<T> constructor: constructors){
                    try {
                        constructor.setAccessible(true);
                        Class<?>[] types = constructor.getParameterTypes();
                        Object[] params = new Object[types.length];
                        for(int i = 0; i < params.length; i++){
                            ValueType valueType = MirageUtil.getValueType(types[i], null, dialect, valueTypes);
                            if(valueType != null){
                                params[i] = valueType.getDefaultValue();
                            }
                        }
                        entity = constructor.newInstance(params);
                    } catch (InstantiationException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        // ignore
                    }
                }
            }
            if(entity == null) {
                throw new EntityCreationFailedException();
            }

            for(int i = 0; i < columnCount; i++){
                String columnLabel = meta.getColumnLabel(i + 1);
                String columnName = meta.getColumnName(i + 1);
                PropertyDesc pd = null;

                for(int j = 0; j < beanDesc.getPropertyDescSize(); j++){
                    PropertyDesc property = beanDesc.getPropertyDesc(j);
                    Column column = property.getAnnotation(Column.class);
                    if(column != null && columnLabel.equalsIgnoreCase(column.name())){
                        pd = property;
                        break;
                    }
                }

                if(pd == null){
                    for(int j = 0; j < beanDesc.getPropertyDescSize(); j++){
                        PropertyDesc property = beanDesc.getPropertyDesc(j);
                        Column column = property.getAnnotation(Column.class);
                        if(column != null && columnName.equalsIgnoreCase(column.name())){
                            pd = property;
                            break;
                        }
                    }
                }

                if(pd == null){
                    String propertyName = nameConverter.columnToProperty(columnLabel);
                    pd = beanDesc.getPropertyDesc(propertyName);
                }

                if(pd == null){
                    String propertyName = nameConverter.columnToProperty(columnName);
                    pd = beanDesc.getPropertyDesc(propertyName);
                }

                if(pd != null){
                    Class<?> propertyType = pd.getPropertyType();
                    ValueType valueType = MirageUtil.getValueType(propertyType, pd, dialect, valueTypes);
                    if(valueType != null){
                        pd.setValue(entity, valueType.get(propertyType, rs, columnLabel));
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("column [%s] is ignored because property [%s]'s type is not supported: %s",
                                    columnLabel, pd.getPropertyName(), propertyType.getName()));
                        }
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("column [%s] is ignored because property is not found in the bean",
                                columnLabel));
                    }
                }
            }

            return entity;
        } catch (SQLException | SecurityException | IllegalArgumentException e) {
            throw new EntityCreationFailedException(e);
        }
    }

    public PrimaryKeyInfo getPrimaryKeyInfo(Class<?> clazz,
            PropertyDesc propertyDesc, NameConverter nameConverter) {
        PrimaryKey primaryKey = propertyDesc.getAnnotation(PrimaryKey.class);
        if(primaryKey == null){
            return null;
        }
        return new PrimaryKeyInfo(primaryKey.generationType(), primaryKey.generator());
    }

    public ColumnInfo getColumnInfo(Class<?> clazz,
            PropertyDesc propertyDesc, NameConverter nameConverter) {
        Column column = propertyDesc.getAnnotation(Column.class);
        if(column == null){
            return null;
        }
        return new ColumnInfo(column.name());
    }

}
