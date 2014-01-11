package org.osgl.aaa;

/**
 * An {@code Guarded} encapsulates the permission or privilege required
 * to access a certain resource. It could have either permission or privilege
 * but not none. If both permission and privilege exists then if the principal's
 * credential matches any one, the access should be granted
 *
 * @author greenlaw110@gmail.com
 */
public interface Guarded {

    /**
     * Return the {@link Permission} required to access this object
     *
     * @return the permission required
     * @see #getPrivilege()
     */
    Permission getPermission();

    /**
     * Return required {@link org.osgl.aaa.Privilege} to access this object
     *
     * @return privilege
     */
    Privilege getPrivilege();

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
    public static enum Factory {
        ;
        private static class Base implements Guarded {
            Permission perm;
            Privilege priv;
            Object tgt;

            Base setPerm(Permission p) {
                perm = p;
                return this;
            }

            Base setPriv(Privilege p) {
                priv = p;
                return this;
            }

            Base setTarget(Object o) {
                tgt = o;
                return this;
            }

            @Override
            public Permission getPermission() {
                return perm;
            }

            @Override
            public Privilege getPrivilege() {
                return priv;
            }

            @Override
            public Object getTarget() {
                return tgt;
            }
        }

        private static Base create() {
            return new Base();
        }

        public static Guarded byPermission(Permission p) {
            return create().setPerm(p);
        }

        public static Guarded byPrivilege(Privilege p) {
            return create().setPriv(p);
        }

        public static Guarded byBoth(Permission pe, Privilege pr) {
            return create().setPerm(pe).setPriv(pr);
        }

        public static Guarded byPermissionWithTarget(Permission p, Object t) {
            return create().setPerm(p).setTarget(t);
        }

        public static Guarded byPrivilegeWithTarget(Privilege p, Object t) {
            return create().setPriv(p).setTarget(t);
        }

        public static Guarded byBothWithTarget(Permission pe, Privilege pr, Object t) {
            return create().setPerm(pe).setPriv(pr).setTarget(t);
        }
    }

}
