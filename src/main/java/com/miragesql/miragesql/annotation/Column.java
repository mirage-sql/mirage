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
 * By default, Mirage-SQL converts the property name to the column name using {@link NameConverter}.
 * However if the entity property has this annotation, Mirage-SQL uses the specified column name instead of
 * <code>NameConverter</code> conversion.
 *
 * @author Naoki Takezoe
 * @author SHUJI Watanabe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface Column {

    /**
     * The column name which is mapped to the annotated property.
     *
     * @return the column  name.
     */
    String name();

    /**
     * The place holder when generate insert / update SQL.
     * Defaults to value: ?
     *
     * @return the place holder
     */
    String placeHolder() default "?";
}
