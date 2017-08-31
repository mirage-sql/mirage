package com.miragesql.miragesql.util;

import java.util.List;

import com.miragesql.miragesql.EntityOperator;
import com.miragesql.miragesql.EntityOperator.ColumnInfo;
import com.miragesql.miragesql.EntityOperator.PrimaryKeyInfo;
import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.annotation.Table;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.parser.SqlContext;
import com.miragesql.miragesql.parser.SqlContextImpl;
import com.miragesql.miragesql.type.ValueType;

public class MirageUtil {

    public static ValueType<?> getValueType(
            Class<?> propertyType, PropertyDesc propertyDesc, Dialect dialect, List<ValueType<?>> valueTypes){

        if(dialect.getValueType() != null){
            ValueType<?> valueType = dialect.getValueType();
            if(valueType.isSupport(propertyType, propertyDesc)){
                return valueType;
            }
        }

        for(ValueType<?> valueType: valueTypes){
            if(valueType.isSupport(propertyType, propertyDesc)){
                return valueType;
            }
        }

        return null;
    }

    /**
     * Returns the {@link SqlContext} instance.
     *
     * @param beanDescFactory the bean descriptor factory
     * @param param the parameter object
     *
     * @return {@link SqlContext} instance
     */
    public static SqlContext getSqlContext(BeanDescFactory beanDescFactory, Object param) {
        SqlContext context = new SqlContextImpl();

        if (param != null) {
            BeanDesc beanDesc = beanDescFactory.getBeanDesc(param);
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
     *
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
     * @param entityOperator the entity operator
     * @param pd the property
     * @param clazz the class
     * @param nameConverter the name converter
     *
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
     * @param beanDescFactory the bean descriptor factory
     * @param entityOperator the entity operator
     * @param clazz the entity class to select
     * @param nameConverter the name converter
     *
     * @return Select SQL
     *
     * @throws RuntimeException the entity class has no primary keys
     */
    public static String buildSelectSQL(BeanDescFactory beanDescFactory, EntityOperator entityOperator, Class<?> clazz, NameConverter nameConverter){
        StringBuilder sb = new StringBuilder();
        BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);

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
     * @param beanDescFactory the bean descriptor factory
     * @param entityOperator the entity operator
     * @param entityType the entity class insert
     * @param nameConverter the name converter
     * @param propDescs the list of parameters
     *
     * @return Insert SQL
     */
    public static String buildInsertSql(BeanDescFactory beanDescFactory, EntityOperator entityOperator, Class<?> entityType, NameConverter nameConverter,
            List<PropertyDesc> propDescs){
        StringBuilder sb = new StringBuilder();
        BeanDesc beanDesc = beanDescFactory.getBeanDesc(entityType);

        sb.append("INSERT INTO ").append(getTableName(entityType, nameConverter)).append(" (");
        {
            int count = 0;
            for(int i = 0; i < beanDesc.getPropertyDescSize(); i++){
                PropertyDesc pd = beanDesc.getPropertyDesc(i);
                PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(entityType, pd, nameConverter);
                if((primaryKey == null || primaryKey.generationType != GenerationType.IDENTITY)
                        && !pd.isTransient() && pd.isReadable() ){
                    if(count != 0){
                        sb.append(", ");
                    }
                    sb.append(getColumnName(entityOperator, entityType, pd, nameConverter));
                    count++;
                }
            }
        }
        sb.append(") VALUES (");
        {
            int count = 0;
            for(int i = 0; i < beanDesc.getPropertyDescSize(); i++){
                PropertyDesc pd = beanDesc.getPropertyDesc(i);
                PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(entityType, pd, nameConverter);
                if((primaryKey == null || primaryKey.generationType != GenerationType.IDENTITY)
                        && !pd.isTransient() && pd.isReadable() ){
                    if(count != 0){
                        sb.append(", ");
                    }
                    sb.append(placeHolderForInsertAndUpdate(pd));

                    propDescs.add(pd);

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
     * @param beanDescFactory the bean descriptor factory
     * @param entityOperator the entity operator
     * @param entityType the entity class to update
     * @param nameConverter the name converter
     * @param propDescs the list of parameters
     *
     * @return Update SQL
     */
    public static String buildUpdateSql(BeanDescFactory beanDescFactory, EntityOperator entityOperator, Class<?> entityType, NameConverter nameConverter,
            List<PropertyDesc> propDescs){
        StringBuilder sb = new StringBuilder();

        sb.append("UPDATE ").append(getTableName(entityType, nameConverter)).append(" SET ");

        BeanDesc beanDesc = beanDescFactory.getBeanDesc(entityType);
        {
            int count = 0;
            for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
                PropertyDesc pd = beanDesc.getPropertyDesc(i);
                PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(entityType, pd, nameConverter);
                if(primaryKey == null && !pd.isTransient() && pd.isReadable() ){
                    if (count != 0) {
                        sb.append(", ");
                    }
                    sb.append(getColumnName(entityOperator, entityType, pd, nameConverter))
                        .append(" = ").append(placeHolderForInsertAndUpdate(pd));
                    propDescs.add(pd);
                    count++;
                }
            }
        }
        sb.append(" WHERE ");
        {
            int count = 0;
            for (int i = 0; i < beanDesc.getPropertyDescSize(); i++) {
                PropertyDesc pd = beanDesc.getPropertyDesc(i);
                PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(entityType, pd, nameConverter);
                if(primaryKey != null && pd.isReadable() ){
                    if(count != 0){
                        sb.append(" AND ");
                    }
                    sb.append(getColumnName(entityOperator, entityType, pd, nameConverter)).append(" = ? ");
                    propDescs.add(pd);
                    count++;
                }
            }
            if(count == 0){
                throw new RuntimeException(
                        "Primary key is not found: " + entityType.getName());
            }
        }

        return sb.toString();
    }

    /**
     * Builds delete SQL and correct parameters from the entity.
     *
     * @param beanDescFactory the bean descriptor factory
     * @param entityOperator the entity operator
     * @param entityType the entity class to delete
     * @param nameConverter the name converter
     * @param propDescs the list of parameters
     *
     * @return Delete SQL
     */
    public static String buildDeleteSql(BeanDescFactory beanDescFactory, EntityOperator entityOperator, Class<?> entityType, NameConverter nameConverter,
            List<PropertyDesc> propDescs){
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(getTableName(entityType, nameConverter));
        sb.append(" WHERE ");

        boolean hasPrimaryKey = false;

        BeanDesc beanDesc = beanDescFactory.getBeanDesc(entityType);

        for(int i=0;i<beanDesc.getPropertyDescSize();i++){
            PropertyDesc pd = beanDesc.getPropertyDesc(i);
            PrimaryKeyInfo primaryKey = entityOperator.getPrimaryKeyInfo(entityType, pd, nameConverter);
            if(primaryKey != null && pd.isReadable()){
                if(!propDescs.isEmpty()){
                    sb.append(" AND ");
                }
                sb.append(getColumnName(entityOperator, entityType, pd, nameConverter)).append("=?");
                propDescs.add(pd);
                hasPrimaryKey = true;
            }
        }

        if(hasPrimaryKey == false){
            throw new RuntimeException(
                    "Primary key is not found: " + entityType.getName());
        }

        return sb.toString();
    }

    private static String placeHolderForInsertAndUpdate(PropertyDesc desc) {
        assert desc != null;
        Column col = desc.getAnnotation(Column.class);
        return col != null ? desc.getAnnotation(Column.class).placeHolder() : "?";
    }

}
