package jp.sf.amateras.mirage.integration.guice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gives this annotation to transaction boundary methods.
 *
 * @author Naoki Takezoe
 * @see TransactionInterceptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Transactional {

}
