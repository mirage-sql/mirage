package com.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.miragesql.miragesql.annotation.Transient;
import com.miragesql.miragesql.exception.BeanDescException;

/**
 * Descriptor of a property of an entity.
 */
public interface PropertyDesc {

    /**
     * Returns the value of this property which the {@code entity} has.
     *
     * @param entity entity object
     *
     * @return the property value
     *
     * @throws BeanDescException TODO TBD
     */
    Object getValue(Object entity);

    /**
     * Sets the value to this property to the {@code entity}.
     *
     * @param entity entity object
     * @param value the property value
     *
     * @throws BeanDescException TODO TBD
     */
    void setValue(Object entity, Object value);

    /**
     * Tests whether this property is readable.
     *
     * @return true if this property is readable
     */
    boolean isReadable();

    /**
     * Tests whether this property is writable.
     *
     * @return {@code true} if this property is writable
     */
    boolean isWritable();

    /**
     * Returns the type of property.
     *
     * @return the property type
     */
    Class<?> getPropertyType();

    /**
     * Returns the name of property.
     *
     * @return the property name
     */
    String getPropertyName();

    /**
     * Returns the field object of this property.
     *
     * @return {@link Field} or {@code null} if the property's implementation is not a field.
     */
    Field getField();

    /**
     * Returns whether this property is transient (transient field or {@link Transient} annotated property).
     *
     * @return {@code true} if this property is transient
     */
    boolean isTransient();

    /**
     * Returns {@link Annotation} which declared at this property.
     *
     * @param <T> the annotation type
     * @param type type of {@link Annotation}
     *
     * @return {@link Annotation} or {@code null} if no {@link Annotation} with this {@code type} is found
     */
    <T extends Annotation> T getAnnotation(Class<T> type);

}
