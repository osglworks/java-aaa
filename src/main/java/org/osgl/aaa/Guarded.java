package org.osgl.aaa;

import org.osgl.$;

/**
 * An {@code Guarded} encapsulates the permission required and the guarded resorce
 *
 * @author greenlaw110@gmail.com
 */
interface Guarded {

    /**
     * Return the {@link Permission} required to access this object
     *
     * @return the permission required
     */
    Permission getPermission();

    /**
     * Return target object that is guarded by the authorization
     * Only used when required right is dynamic as it needs to
     * be checked if the object belongs to the principal
     *
     * @return the target object
     */
    Object getTarget();

    /**
     * Name space for default implementation and factory methods
     */
    enum Factory {
        ;
        private static class Base implements Guarded {
            Permission perm;
            Object tgt;

            Base setPermission(Permission p) {
                perm = $.notNull(p);
                return this;
            }

            Base setTarget(Object o) {
                tgt = $.notNull(o);
                return this;
            }

            @Override
            public Permission getPermission() {
                return perm;
            }

            @Override
            public Object getTarget() {
                return tgt;
            }
        }

        private static Base create() {
            return new Base();
        }

        public static Guarded byPermission(Permission p, Object t) {
            return create().setPermission(p).setTarget(t);
        }

    }

}
