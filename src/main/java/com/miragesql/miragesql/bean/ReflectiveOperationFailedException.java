package jp.sf.amateras.mirage.bean;

import java.lang.reflect.InvocationTargetException;

/**
 * {@link ReflectiveOperationFailedException} is thrown to indicate
 * reflective operation is failed.
 * 
 * @see ClassNotFoundException
 * @see InvocationTargetException
 * @see IllegalAccessException
 * @see InstantiationException
 * @see NoSuchMethodException
 * @see NoSuchFieldException
 * @author daisuke
 */
@SuppressWarnings("serial")
public class ReflectiveOperationFailedException extends RuntimeException {

	public ReflectiveOperationFailedException(ClassNotFoundException e) {
		super(e);
	}
	
	public ReflectiveOperationFailedException(InvocationTargetException e) {
		super(e);
	}
	
	public ReflectiveOperationFailedException(IllegalAccessException e) {
		super(e);
	}
	
	public ReflectiveOperationFailedException(InstantiationException e) {
		super(e);
	}
	
	public ReflectiveOperationFailedException(NoSuchFieldException e) {
		super(e);
	}
	
	public ReflectiveOperationFailedException(NoSuchMethodException e) {
		super(e);
	}


	public ReflectiveOperationFailedException(String message, ClassNotFoundException e) {
		super(message, e);
	}
	
	public ReflectiveOperationFailedException(String message, InvocationTargetException e) {
		super(message, e);
	}
	
	public ReflectiveOperationFailedException(String message, IllegalAccessException e) {
		super(message, e);
	}
	
	public ReflectiveOperationFailedException(String message, InstantiationException e) {
		super(message, e);
	}
	
	public ReflectiveOperationFailedException(String message, NoSuchFieldException e) {
		super(message, e);
	}
	
	public ReflectiveOperationFailedException(String message, NoSuchMethodException e) {
		super(message, e);
	}
}
