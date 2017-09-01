package com.miragesql.miragesql.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * An interface for extracting property information from a class object.
 * <p>
 * You can implement your own PropertyExtractor and enable it by {@link BeanDescFactory#setPropertyExtractor(PropertyExtractor)}.
 *
 * @author Naoki Takezoe
 */
public interface PropertyExtractor {

    /**
     * Extracts the properties from a class
     * @param clazz the class to extract properties from.
     *
     * @return a Map of properties
     */
    Map<String, PropertyWrapper> extractProperties(Class<?> clazz);

    class PropertyInfo {
        public String name;
        public Class<?> type;
        public Method getterMethod;
        public Method setterMethod;
        public Field field;
    }

}
