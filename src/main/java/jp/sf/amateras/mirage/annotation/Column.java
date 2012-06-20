package jp.sf.amateras.mirage.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jp.sf.amateras.mirage.naming.NameConverter;

/**
 * Specifies the column name which is mapped to the property.
 * <p>
 * By the default, Mirage converts the property name to the column name using {@link NameConverter}.
 * However if the entity property has this annotation, Mirage uses the specified column name instead of
 * <code>NameConverter</code> conversion.
 *
 * @author Naoki Takezoe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Column {

	/**
	 * The column name which is mapped to the annotead property.
	 */
	String name();

}
