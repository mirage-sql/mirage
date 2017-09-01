package com.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface PropertyWrapper {

    String getName();

    Class<?> getType();

    void set(Object instance, Object value) throws IllegalAccessException, InvocationTargetException;

    Object get(Object instance) throws IllegalAccessException, InvocationTargetException;

    boolean isReadable();

    boolean isWritable();

    boolean isTransient();

    <T extends Annotation> T getAnnotation(Class<T> type);

    Field getField();

    void setField(Field field);

    Method getGetterMethod();

    void setGetterMethod(Method getter);

    Method getSetterMethod();

    void setSetterMethod(Method setter);

}
