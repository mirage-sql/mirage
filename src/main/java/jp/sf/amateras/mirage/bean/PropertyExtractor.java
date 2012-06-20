package jp.sf.amateras.mirage.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * An interface for extracting properties information from a class object.
 * <p>
 * You can implement your own PropertyExtractor and enable it by {@link BeanDescFactory#setPropertyExtractor(PropertyExtractor)}.
 *
 * @author Naoki Takezoe
 */
public interface PropertyExtractor {

	public Map<String, PropertyWrapper> extractProperties(Class<?> clazz);

	public static class PropertyInfo {
		public String name;
		public Class<?> type;
		public Method getterMethod;
		public Method setterMethod;
		public Field field;
	}

}
