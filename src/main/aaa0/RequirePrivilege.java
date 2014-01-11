package org.osgl.aaa0;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface RequirePrivilege {
    String value();
    /**
     * Set the time of check method access permission, usually it should be true
     * But in rare case when it needs to check after the method executed (e.g. constructor)
     * then it could be set to false;
     * @return
     */
    boolean before() default true;
}
