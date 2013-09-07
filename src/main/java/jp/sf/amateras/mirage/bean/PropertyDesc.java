package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import jp.sf.amateras.mirage.exception.BeanDescException;

/**
 * Descriptor of the property of entity.
 */
public interface PropertyDesc {

	/**
	 * Returns the value of this prperty which the {@code entity} has.
	 * 
	 * @param entity entity object
	 * @return the property value
	 * @throws BeanDescException TODO TBD
	 */
	public Object getValue(Object entity);

	/**
	 * Sets the value to this property to the {@code entity}.
	 * 
	 * @param entity entity object
	 * @param value the property value
	 * @throws BeanDescException TODO TBD
	 */
	public void setValue(Object entity, Object value);

	public boolean isReadable();

	public boolean isWritable();

	/**
	 * Returns the type of property.
	 * 
	 * @return the property type
	 */
	public Class<?> getPropertyType();

	/**
	 * Returns the name of property.
	 * 
	 * @return the property name
	 */
	public String getPropertyName();

	public Field getField();

	public boolean isTransient();

	/**
	 * Returns {@link Annotation} which declared at this property.
	 * 
	 * @param type type of {@link Annotation}
	 * @return {@link Annotation} or {@code null} if no {@link Annotation} with this {@code type} is found
	 */
	public <T extends Annotation> T getAnnotation(Class<T> type);

}
