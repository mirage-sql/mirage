package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import jp.sf.amateras.mirage.exception.BeanDescException;

public class PropertyDescImpl implements PropertyDesc {

	private PropertyWrapper propertyWrapper;

	public PropertyDescImpl(PropertyWrapper propertyWrapper){
		this.propertyWrapper = propertyWrapper;
	}

//	@Override
	public Object getValue(Object obj){
		try {
			return propertyWrapper.get(obj);
		} catch (IllegalAccessException ex) {
			throw new BeanDescException(ex);

		} catch (InvocationTargetException ex) {
			throw new BeanDescException(ex);

		}
	}

//	@Override
	public void setValue(Object obj, Object value){
		try {
			propertyWrapper.set(obj, value);
		} catch (IllegalAccessException ex) {
			throw new BeanDescException(ex);

		} catch (InvocationTargetException ex) {
			throw new BeanDescException(ex);

		}
	}

//	@Override
	public boolean isReadable(){
		return propertyWrapper.isReadable();
	}

//	@Override
	public boolean isWritable(){
		return propertyWrapper.isWritable();
	}

//	@Override
	public Class<?> getPropertyType(){
		return propertyWrapper.getType();
	}

//	@Override
	public String getPropertyName(){
		return propertyWrapper.getName();
	}

//	@Override
	public Field getField(){
		return propertyWrapper.getField();
	}

//	@Override
	public <T extends Annotation> T getAnnotation(Class<T> type){
		return propertyWrapper.getAnnotation(type);
	}

//	@Override
	public boolean isTransient() {
		return propertyWrapper.isTransient();
	}

	@Override
	public String toString() {
		return new StringBuilder(super.toString())
			.append("[").append(getPropertyName()).append(":").append(propertyWrapper.getType() == null ? null :
				propertyWrapper.getType().getSimpleName()).append("]")
			.toString();
	}
}
