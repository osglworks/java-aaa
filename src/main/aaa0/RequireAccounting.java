package org.osgl.aaa0;

import java.lang.annotation.*;

/**
 * Indicates that a method invocation shall be logged
 * @author greenlaw110@gmail.com
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface RequireAccounting {
    /**
     * Indicates the message to be logged
     *
     * @return
     */
    String value() default "";
    /**
     * Set the time of accouting log, usually it should be true
     * But in rare case when it needs to log after the method executed (e.g. constructor)
     * then it could be set to false;
     * @return
     */
    boolean before() default true;
}
