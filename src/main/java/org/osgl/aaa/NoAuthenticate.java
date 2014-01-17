package org.osgl.aaa;

import java.lang.annotation.*;

/**
 * Indicate that the annotated method is not subject to Secure authentication
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
public @interface NoAuthenticate {
}
