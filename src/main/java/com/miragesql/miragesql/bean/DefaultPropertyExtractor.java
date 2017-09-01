package com.miragesql.miragesql.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The default implementation of {@link PropertyExtractor}.
 *
 * @author Naoki Takezoe
 */
public class DefaultPropertyExtractor implements PropertyExtractor {

    /**{@inheritDoc}*/
    public Map<String, PropertyWrapper> extractProperties(Class<?> clazz) {
        Map<String, PropertyInfo> map = new LinkedHashMap<>();

        Method[] methods = clazz.getMethods();
        for(Method method: methods){
            // ignore java.lang.Object methods
            if(method.getDeclaringClass() == Object.class){
                continue;
            }

            String methodName = method.getName();

            if((methodName.startsWith("get") || methodName.startsWith("is")) && method.getParameterTypes().length == 0){
                String propertyName = getPropertyName(methodName);
                PropertyInfo info = map.get(propertyName);
                if(info == null){
                    info = new PropertyInfo();
                    info.name = propertyName;
                    info.getterMethod = method;
                    info.type = method.getReturnType();
                    map.put(propertyName, info);
                } else if(info.type == method.getReturnType()){
                    info.getterMethod = method;
                }
            }
            if(methodName.startsWith("set") && method.getParameterTypes().length == 1){
                String propertyName = getPropertyName(methodName);
                PropertyInfo info = map.get(propertyName);
                if(info == null){
                    info = new PropertyInfo();
                    info.name = propertyName;
                    info.setterMethod = method;
                    info.type = method.getParameterTypes()[0];
                    map.put(propertyName, info);
                } else if(info.type == method.getParameterTypes()[0]){
                    info.setterMethod = method;
                }
            }
        }

        Field[] fields = clazz.getFields();
        for(Field field: fields){
            int modifiers = field.getModifiers();
            if (map.containsKey(field.getName()) == false
                    && Modifier.isStatic(modifiers) == false
                    && Modifier.isFinal(modifiers) == false) {
                PropertyInfo info = new PropertyInfo();
                info.name = field.getName();
                info.field = field;
                info.type = field.getType();
                map.put(field.getName(), info);
            }
        }

        Map<String, PropertyWrapper> result = new LinkedHashMap<>();
        for(Map.Entry<String, PropertyInfo> e: map.entrySet()){
            PropertyInfo info = e.getValue();
            if(info.field != null){
                result.put(e.getKey(), new PropertyWrapperImpl(info.name, null, null, info.field));
            } else {
                Field field = null;
                try {
                    field = clazz.getDeclaredField(info.name);
                } catch(Exception ex){
                    // ignore
                }
                result.put(e.getKey(), new PropertyWrapperImpl(info.name, info.getterMethod, info.setterMethod, field));
            }
        }
        return result;
    }

    protected static String getPropertyName(String methodName){
        if(methodName.startsWith("get") || methodName.startsWith("set")){
            methodName = methodName.substring(3);
        }
        if(methodName.startsWith("is")){
            methodName = methodName.substring(2);
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<methodName.length();i++){
            char c = methodName.charAt(i);
            if(i == 0){
                sb.append(String.valueOf(c).toLowerCase());
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
