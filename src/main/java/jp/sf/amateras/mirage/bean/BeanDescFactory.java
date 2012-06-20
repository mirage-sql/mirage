package jp.sf.amateras.mirage.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDescFactory {

	private static Map<Class<?>, BeanDesc> cacheMap = new ConcurrentHashMap<Class<?>, BeanDesc>();
	private static boolean cacheEnabled = false;
	private static PropertyExtractor propertyExtractor = new DefaultPropertyExtractor();

	public static void setCacheEnabled(boolean cacheEnabled){
		BeanDescFactory.cacheEnabled = cacheEnabled;
	}

	public static boolean isCacheEnabled(){
		return BeanDescFactory.cacheEnabled;
	}

	public static void setPropertyExtractor(PropertyExtractor propertyExtractor){
		BeanDescFactory.propertyExtractor = propertyExtractor;
	}

	@SuppressWarnings("unchecked")
	public static BeanDesc getBeanDesc(Object obj){
		if(obj instanceof Map){
			return new MapBeanDescImpl((Map<String, Object>) obj);
		} else {
			return getBeanDesc(obj.getClass());
		}
	}

	public static BeanDesc getBeanDesc(Class<?> clazz){
		if(clazz == Map.class){
			return new MapBeanDescImpl();
		}

		if(cacheEnabled && cacheMap.containsKey(clazz)){
			return cacheMap.get(clazz);
		}

		BeanDesc beanDesc = new BeanDescImpl(clazz, propertyExtractor.extractProperties(clazz));

		if(cacheEnabled){
			cacheMap.put(clazz, beanDesc);
		}

		return beanDesc;
	}

}
