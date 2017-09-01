/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/10/21
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.miragesql.miragesql.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An implementation of {@link PropertyExtractor} which always retrieves property types and names
 * from the entity field if it has getters and setters.
 *
 * @since 1.1.4
 * @author daisuke
 */
public class FieldPropertyExtractor implements PropertyExtractor {

    /**{@inheritDoc}*/
    public Map<String, PropertyWrapper> extractProperties(Class<?> clazz) {
        Map<String, PropertyWrapper> map = new LinkedHashMap<>();
        extractProperties0(clazz, map);
        return map;
    }

    private void extractProperties0(Class<?> clazz, Map<String, PropertyWrapper> map) {
        if (clazz == null) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            int modifiers = field.getModifiers();
            if (map.containsKey(field.getName()) == false
                    && Modifier.isStatic(modifiers) == false
                    && Modifier.isFinal(modifiers) == false) {
                map.put(field.getName(), new ReadableWritablePropertyWrapperImpl(field.getName(), null, null, field));
            }
        }
        extractProperties0(clazz.getSuperclass(), map);
    }

    private static class ReadableWritablePropertyWrapperImpl extends PropertyWrapperImpl {

        private ReadableWritablePropertyWrapperImpl(String name, Method getter, Method setter, Field field) {
            super(name, getter, setter, field);
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWritable() {
            return true;
        }
    }
}
