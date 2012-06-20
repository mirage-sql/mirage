package jp.sf.amateras.mirage.bean;

import java.lang.annotation.Annotation;


public interface BeanDesc {

	public Class<?> getType();

	public PropertyDesc getPropertyDesc(String name);

	public int getPropertyDescSize();

	public PropertyDesc getPropertyDesc(int i);

	public <T extends Annotation> T getAnnotation(Class<T> type);

}