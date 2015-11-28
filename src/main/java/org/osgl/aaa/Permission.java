package org.osgl.aaa;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;

/**
 * This interface represents a permission, such as that used to grant
 * a particular type of access to a resource.
 */
public interface Permission extends AAAObject, java.security.acl.Permission {

    /**
     * Whether this right is dynamic. An example of dynamic right is a customer has
     * right to access only orders owned by the customer, while order manager has
     * right to access all orders which is a static right.
     *
     * @return true if this right is dynamic, false otherwise
     */
    boolean isDynamic();

    public static abstract class F extends AAAObject.F {

        public static $.Predicate<Permission> IS_DYNAMIC = new $.Predicate<Permission>() {
            @Override
            public boolean test(Permission permission) throws NotAppliedException, $.Break {
                return permission.isDynamic();
            }
        };

        public static $.Predicate<Permission> IS_STATIC = $.F.negate(IS_DYNAMIC);
    }
}
