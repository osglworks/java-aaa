package org.osgl.aaa;

import java.lang.annotation.*;

/**
 * Indicate that the annotated method or class is needs to be authenticated
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface RequireAuthenticate {
}
