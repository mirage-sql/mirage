package com.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

public class MapPropertyDescImpl implements PropertyDesc {

    private Object value;
    private String propertyName;

    public MapPropertyDescImpl(String propertyName, Object value){
        this.propertyName = propertyName;
        this.value = value;
    }

//	@Override
    /**{@inheritDoc}*/
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return null;
    }

//	@Override
    /**{@inheritDoc}*/
    public Field getField() {
        return null;
    }

//	@Override
    /**{@inheritDoc}*/
    public String getPropertyName() {
        return propertyName;
    }

//	@Override
    /**{@inheritDoc}*/
    public Class<?> getPropertyType() {
        return (null == value) ? null : value.getClass();
    }

//	@Override
//	@SuppressWarnings("unchecked")
    /**{@inheritDoc}*/
    public Object getValue(Object entity) {
        return Map.class.cast(entity).get(propertyName);
    }

//	@Override
    /**{@inheritDoc}*/
    public boolean isReadable() {
        return true;
    }

//	@Override
    /**{@inheritDoc}*/
    public boolean isWritable() {
        return true;
    }

//	@Override
    /**{@inheritDoc}*/
    @SuppressWarnings("unchecked")
    public void setValue(Object entity, Object value) {
        ((Map<Object, Object>) entity).put(propertyName, value);
    }

//	@Override
    /**{@inheritDoc}*/
    public boolean isTransient() {
        return false;
    }

    /**{@inheritDoc}*/
    @Override
    public String toString() {
        return "MapPropertyDescImpl [value=" + value + ", propertyName=" + propertyName + "]";
    }
}
