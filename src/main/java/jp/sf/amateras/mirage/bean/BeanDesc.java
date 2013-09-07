package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;

/**
 * Descriptor of the entity.
 */
public interface BeanDesc {

	/**
	 * Returns class of entity.
	 * 
	 * @return class of entity
	 */
	public Class<?> getType();

	/**
	 * Returns {@link PropertyDesc} of this bean.
	 * 
	 * <p>The {@code name} parameter is a {@link String} specifying the simple name of the desired property.</p>
	 * 
	 * @param name property name
	 * @return the {@link PropertyDesc} or {@code null} if no property with this name is found
	 */
	public PropertyDesc getPropertyDesc(String name);

	/**
	 * Returns the size of {@link PropertyDesc} which this entity has.
	 * 
	 * @return size
	 */
	public int getPropertyDescSize();

	/**
	 * Returns {@link PropertyDesc} of this bean.
	 * 
	 * @param i index number
	 * @return the {@link PropertyDesc}
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public PropertyDesc getPropertyDesc(int i);

	/**
	 * Returns {@link Annotation} which declared at this entity.
	 * 
	 * @param type type of {@link Annotation}
	 * @return {@link Annotation} or {@code null} if no {@link Annotation} with this {@code type} is found
	 */
	public <T extends Annotation> T getAnnotation(Class<T> type);

}