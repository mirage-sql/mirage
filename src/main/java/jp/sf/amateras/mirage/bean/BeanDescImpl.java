package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDescImpl implements BeanDesc {

	private Class<?> clazz;
	private Map<String, PropertyDesc> propertyMap = new ConcurrentHashMap<String, PropertyDesc>();
	private PropertyDesc[] propertyArray;

	public BeanDescImpl(Class<?> clazz, Map<String, PropertyWrapper> map){
		this.clazz = clazz;

		List<PropertyDesc> list = new ArrayList<PropertyDesc>();

		for(PropertyWrapper propertyWrapper: map.values()){
			PropertyDesc pd = new PropertyDescImpl(propertyWrapper);

//			if(info.field == null){
//				pd = new PropertyDescImpl(this, info.name, info.type, null, info.getterMethod, info.setterMethod);
//			} else {
//				pd = new PropertyDescImpl(this, info.name, info.type, info.propertyWrapper);
//			}

			list.add(pd);
			this.propertyMap.put(pd.getPropertyName(), pd);
		}

//		// for Scala classes
//		while(clazz != null){
//			Field[] declaredFields = clazz.getDeclaredFields();
//			for(Field field: declaredFields){
//				if(Modifier.isPublic(field.getModifiers())){
//					continue;
//				}
//				String propertyName = field.getName();
//				try {
//					Method method = clazz.getMethod(propertyName, new Class<?>[0]);
//					PropertyInfo info = map.get(propertyName);
//					if(info == null){
//						info = new PropertyInfo();
//						info.name = propertyName;
//						info.getterMethod = method;
//						info.type = method.getReturnType();
//						map.put(propertyName, info);
//					} else if(info.type == method.getReturnType()){
//						info.getterMethod = method;
//					}
//				} catch(NoSuchMethodException e){
//				}
//				try {
//					Method method = clazz.getMethod(propertyName + "_$eq", field.getType());
//					PropertyInfo info = map.get(propertyName);
//					if(info == null){
//						info = new PropertyInfo();
//						info.name = propertyName;
//						info.setterMethod = method;
//						info.type = method.getParameterTypes()[0];
//						map.put(propertyName, info);
//					} else if(info.type == method.getParameterTypes()[0]){
//						info.setterMethod = method;
//					}
//				} catch(NoSuchMethodException e){
//				}
//			}
//			clazz = clazz.getSuperclass();
//		}

		this.propertyArray = list.toArray(new PropertyDesc[list.size()]);
	}

//	@Override
	public Class<?> getType(){
		return clazz;
	}

//	@Override
	public PropertyDesc getPropertyDesc(String name){
		return propertyMap.get(name);
	}

//	@Override
	public int getPropertyDescSize(){
		return propertyArray.length;
	}

//	@Override
	public PropertyDesc getPropertyDesc(int i){
		return propertyArray[i];
	}

//	@Override
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return clazz.getAnnotation(type);
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString())
			.append("[").append(clazz == null ? null : clazz.getSimpleName()).append("]")
			.toString();
	}
}
