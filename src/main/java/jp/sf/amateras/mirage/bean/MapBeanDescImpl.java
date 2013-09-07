package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MapBeanDescImpl implements BeanDesc {

	private Map<String, Object> map;
	private PropertyDesc[] propertyArray;
	private Map<String, PropertyDesc> propertyMap = new ConcurrentHashMap<String, PropertyDesc>();

	public MapBeanDescImpl(){
	}

	public MapBeanDescImpl(Map<String, Object> map){
		this.map = map;

		List<PropertyDesc> properties = new ArrayList<PropertyDesc>();

		for(Entry<String, Object> entry: map.entrySet()){
			String propertyName = entry.getKey();
			Object value = entry.getValue();
			PropertyDesc pd = new MapPropertyDescImpl(propertyName, value);
			properties.add(pd);
			this.propertyMap.put(propertyName, pd);
		}

		this.propertyArray = properties.toArray(new PropertyDesc[properties.size()]);
	}

//	@Override
	public PropertyDesc getPropertyDesc(String name) {
		if(this.map == null){
			return new MapPropertyDescImpl(name, "");
		}
		return this.propertyMap.get(name);
	}

//	@Override
	public PropertyDesc getPropertyDesc(int i) {
		if(this.map == null){
			return null;
		}
		return this.propertyArray[i];
	}

//	@Override
	public int getPropertyDescSize() {
		if(this.map == null){
			return 0;
		}
		return this.propertyArray.length;
	}

//	@Override
	public Class<?> getType() {
		if(this.map == null){
			return Map.class;
		}
		return this.map.getClass();
	}

//	@Override
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return null;
	}
}
