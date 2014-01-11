package org.osgl.aaa;

import java.lang.annotation.*;

/**
 * Indicate {@link AAAContext#getSystemPrincipal()} system} principal could be used if {@link AAAContext#getCurrentPrincipal()} current}
 * principal is <code>null</code>
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0 23/12/2010
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface AllowSystemAccount {
}
