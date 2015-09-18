package org.elevenframework.web.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Ruici
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Interceptors {
    /**
     * @return interceptor classes.
     */
    @SuppressWarnings("rawtypes")
    Class[] value();
}
