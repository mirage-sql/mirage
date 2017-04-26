package com.miragesql.miragesql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.miragesql.miragesql.naming.NameConverter;

/**
 * Annotation that specifies the DB table name which is mapped to the entity class.
 * <p>
 * By the default, Mirage-SQL converts the entity class name to the table name using {@link NameConverter}.
 * However if the entity class has this annotation, Mirage-SQL uses the specified table name instead of
 * <code>NameConverter</code> conversion.
 *
 * @author Naoki Takezoe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Table {

	/**
	 * The table name which is mapped to the annotated entity class.
	 */
	String name();

}
