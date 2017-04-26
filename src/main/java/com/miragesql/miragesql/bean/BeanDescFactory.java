package com.miragesql.miragesql.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDescFactory {

	private Map<Class<?>, BeanDesc> cacheMap = new ConcurrentHashMap<Class<?>, BeanDesc>();
	private boolean cacheEnabled = false;
	private PropertyExtractor propertyExtractor = new DefaultPropertyExtractor();

	public void setCacheEnabled(boolean cacheEnabled){
		this.cacheEnabled = cacheEnabled;
	}

	public boolean isCacheEnabled(){
		return cacheEnabled;
	}

	public void setPropertyExtractor(PropertyExtractor propertyExtractor){
		this.propertyExtractor = propertyExtractor;
	}

	@SuppressWarnings("unchecked")
	public BeanDesc getBeanDesc(Object obj){
		if(obj instanceof Map){
			return new MapBeanDescImpl((Map<String, Object>) obj);
		} else {
			return getBeanDesc(obj.getClass());
		}
	}

	public BeanDesc getBeanDesc(Class<?> clazz){
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
