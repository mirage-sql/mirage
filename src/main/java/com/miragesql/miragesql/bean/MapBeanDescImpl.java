package com.miragesql.miragesql.bean;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;

public class MapBeanDescImpl implements BeanDesc {

    private Map<String, Object> map;
    private PropertyDesc[] propertyArray;

    // keep the order of original properties
    private final Map<String, PropertyDesc> propertyMap = Collections.synchronizedMap(new LinkedHashMap<>());

    public MapBeanDescImpl(){
    }

    public MapBeanDescImpl(Map<String, Object> map){
        this.map = map;

        List<PropertyDesc> properties = new ArrayList<>();

        for(Entry<String, Object> entry: map.entrySet()){
            String propertyName = entry.getKey();
            Object value = entry.getValue();
            PropertyDesc pd = new MapPropertyDescImpl(propertyName, value);
            properties.add(pd);
            addToMap(propertyName, pd);
        }

        this.propertyArray = properties.toArray(new PropertyDesc[properties.size()]);
    }

//	@Override
    /**{@inheritDoc}*/
    public PropertyDesc getPropertyDesc(String name) {
        if(this.map == null){
            return new MapPropertyDescImpl(name, "");
        }
        return this.propertyMap.get(name);
    }

//	@Override
    /**{@inheritDoc}*/
    public PropertyDesc getPropertyDesc(int i) {
        if(this.map == null){
            return null;
        }
        return this.propertyArray[i];
    }

//	@Override
    /**{@inheritDoc}*/
    public int getPropertyDescSize() {
        if(this.map == null){
            return 0;
        }
        return this.propertyArray.length;
    }

//	@Override
    /**{@inheritDoc}*/
    public Class<?> getType() {
        if(this.map == null){
            return Map.class;
        }
        return this.map.getClass();
    }

//	@Override
    /**{@inheritDoc}*/
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return null;
    }

    private void addToMap(String propertyName, PropertyDesc pd) {
        synchronized (propertyMap) {
            propertyMap.put(propertyName,pd);
        }
    }
}
