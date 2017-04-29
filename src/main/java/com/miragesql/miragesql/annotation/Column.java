package com.miragesql.miragesql.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.miragesql.miragesql.naming.NameConverter;

/**
 * Annotation that specifies the DB column name which is mapped to the annotated property.
 * <p>
 * By the default, Mirage-SQL converts the property name to the column name using {@link NameConverter}.
 * However if the entity property has this annotation, Mirage-SQL uses the specified column name instead of
 * <code>NameConverter</code> conversion.
 *
 * @author Naoki Takezoe
 * @author SHUJI Watanabe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Column {

    /**
     * The column name which is mapped to the annotated property.
     */
    String name();

    /**
     * The place holder when generate insert / update SQL.
     * Defaults to value: ?
     */
    String placeHolder() default "?";
}
