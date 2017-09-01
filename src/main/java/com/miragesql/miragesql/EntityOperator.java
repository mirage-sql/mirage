package com.miragesql.miragesql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.miragesql.miragesql.annotation.Column;
import com.miragesql.miragesql.annotation.PrimaryKey;
import com.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.dialect.Dialect;
import com.miragesql.miragesql.naming.NameConverter;
import com.miragesql.miragesql.type.ValueType;

/**
 * An interface for operation for the entity. This interface is used for the following operations:
 * <ul>
 *   <li>Creating an entity instance from ResultSet</li>
 *   <li>Retrieving metadata from the entity class</li>
 * </ul>
 * You can implement your own EntityOperator and enable it by {@link SqlManager#setEntityOperator(EntityOperator)}.
 *
 * @author Naoki Takezoe
 */
public interface EntityOperator {

    /**
     * Creates and returns one entity instance from the ResultSet.
     *
     * @param <T> the type parameter of entity class
     * @param clazz the entity class
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
    <T> T createEntity(Class<T> clazz, ResultSet rs,
                       ResultSetMetaData meta, int columnCount, BeanDesc beanDesc,
                       Dialect dialect, List<ValueType<?>> valueTypes, NameConverter nameConverter);

    /**
     * Retrieves the metadata of the primary key from the given PropertyDesc.
     *
     * @param clazz the entity class
     * @param propertyDesc the property description
     * @param nameConverter the NameConverter
     * @return the metadata of the primary key
     */
    PrimaryKeyInfo getPrimaryKeyInfo(Class<?> clazz,
                                     PropertyDesc propertyDesc, NameConverter nameConverter);

    /**
     * Retrieves the metadata of the column from the given PropertyDesc.
     *
     * @param clazz the entity class
     * @param propertyDesc the property description
     * @param nameConverter the NameConverter
     * @return the metadata of the column
     */
    ColumnInfo getColumnInfo(Class<?> clazz,
                             PropertyDesc propertyDesc, NameConverter nameConverter);

    /**
     * DTO for metadata of the primary key which is specified by {@link PrimaryKey} annotation.
     *
     * @see PrimaryKey
     */
    public class PrimaryKeyInfo {
        public GenerationType generationType;
        public String generator;

        public PrimaryKeyInfo(GenerationType generationType){
            this(generationType, null);
        }

        public PrimaryKeyInfo(GenerationType generationType, String generator){
            this.generationType = generationType;
            this.generator = generator;
        }
    }

    /**
     * DTO for metadata of the column which is specified by {@link Column} annotation.
     *
     * @see Column
     */
    public class ColumnInfo {
        public String name;

        public ColumnInfo(String name){
            this.name = name;
        }
    }

}
