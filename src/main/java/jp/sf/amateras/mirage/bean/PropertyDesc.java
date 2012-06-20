package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import jp.sf.amateras.mirage.exception.BeanDescException;

public interface PropertyDesc {

	/**
	 *
	 * @param obj
	 * @return
	 * @throws BeanDescException
	 */
	public Object getValue(Object obj);

	/**
	 *
	 * @param obj
	 * @param value
	 * @throws BeanDescException
	 */
	public void setValue(Object obj, Object value);

	public boolean isReadable();

	public boolean isWritable();

	public Class<?> getPropertyType();

	public String getPropertyName();

	public Field getField();

	public boolean isTransient();

	public <T extends Annotation> T getAnnotation(Class<T> type);

}
