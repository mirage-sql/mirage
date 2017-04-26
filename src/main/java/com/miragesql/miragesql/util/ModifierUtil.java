package com.miragesql.miragesql.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ModifierUtil {

    protected ModifierUtil() {
    }

    public static boolean isStatic(int modifier) {
        return Modifier.isStatic(modifier);
    }

    public static boolean isFinal(int modifier) {
        return Modifier.isFinal(modifier);
    }
    
    public static boolean isInstanceField(Field field) {
        int m = field.getModifiers();
        return !isStatic(m) && !isFinal(m);
    }
}
