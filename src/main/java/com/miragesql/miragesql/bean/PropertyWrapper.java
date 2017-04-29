package com.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface PropertyWrapper {

    public String getName();

    public Class<?> getType();

    public void set(Object instance, Object value) throws IllegalAccessException, InvocationTargetException;

    public Object get(Object instance) throws IllegalAccessException, InvocationTargetException;

    public boolean isReadable();

    public boolean isWritable();

    public boolean isTransient();

    public <T extends Annotation> T getAnnotation(Class<T> type);

    public Field getField();

    public void setField(Field field);

    public Method getGetterMethod();

    public void setGetterMethod(Method getter);

    public Method getSetterMethod();

    public void setSetterMethod(Method setter);

}
