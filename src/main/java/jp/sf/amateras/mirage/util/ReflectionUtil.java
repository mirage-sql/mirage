package jp.sf.amateras.mirage.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ReflectionUtil {

	public static Field getField(Class<?> clazz, String name){
		while(clazz != Object.class){
			try {
				Field field = clazz.getDeclaredField(name);
				if(field != null){
					return field;
				}
			} catch(Exception ex){
				// ignore
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

    public static Class<?> getElementTypeOfList(final Type parameterizedList) {
        return GenericUtil.getRawClass(GenericUtil
                .getElementTypeOfList(parameterizedList));
    }

    public static Class<?> getElementTypeOfListFromFieldType(final Field field) {
        final Type type = field.getGenericType();
        return getElementTypeOfList(type);
    }

}
