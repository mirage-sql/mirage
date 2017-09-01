package com.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Wrapper interface of a property.
 */
public interface PropertyWrapper {

    /**
     * Returns the name
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the type
     *
     * @return the type
     */
    Class<?> getType();

    /**
     * Sets the value
     *
     * @param instance the instance to set the value upon
     * @param value the value to be set
     *
     * @throws IllegalAccessException if the value can't be accessed
     * @throws InvocationTargetException if the value can't be set
     */
    void set(Object instance, Object value) throws IllegalAccessException, InvocationTargetException;


    Object get(Object instance) throws IllegalAccessException, InvocationTargetException;

    /**
     * Returns true if the property is readable
     *
     * @return true if the property is readable
     */
    boolean isReadable();

    /**
     * Returns true if the property is writable
     *
     * @return true if the property is writable
     */
    boolean isWritable();

    /**
     * Returns true if the property is transient
     *
     * @return true if the property is transient
     */
    boolean isTransient();

    /**
     * Returns the property annotation for a field, it's setter or getter of type T.
     *
     * @param type the type of annotation
     * @param <T> the Type
     *
     * @return the annotation of a property
     */
    <T extends Annotation> T getAnnotation(Class<T> type);

    /**
     * Returns the field of a property
     *
     * @return the field
     */
    Field getField();

    /**
     * Sets the field for a property
     *
     * @param field the field to set
     */
    void setField(Field field);

    /**
     * Returns the getter method of a property
     *
     * @return the getter method
     */
    Method getGetterMethod();

    /**
     * Sets a getter method for a property
     *
     * @param getter the getter method to set
     */
    void setGetterMethod(Method getter);

    /**
     * Returns the setter method of a property.
     *
     * @return the setter method
     */
    Method getSetterMethod();

    /**
     * Sets a setter method for a property
     *
     * @param setter the setter method to set
     */
    void setSetterMethod(Method setter);

}
