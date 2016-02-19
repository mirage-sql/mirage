package jp.sf.amateras.mirage.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the property corresponds to the primary key column.
 *
 * @author Naoki Takezoe
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrimaryKey {

	GenerationType generationType();

	String generator() default "";

    /**
     * The type of the generated primary key.
     */
	public static enum GenerationType {
		APPLICATION,
		IDENTITY,
		SEQUENCE
	}

}
