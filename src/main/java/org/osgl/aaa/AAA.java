package org.osgl.aaa;

import org.osgl.util.C;
import org.osgl.util.E;

import java.util.Collection;
import java.util.Map;

/**
 * Created by luog on 8/01/14.
 */
public enum  AAA {
    ;

    /**
     * The name of system principal
     */
    public static final String SYSTEM = "__sys";

    private static final ThreadLocal<Object> target = new ThreadLocal<Object>();

    private static final Map<Class, DynamicPermissionCheckHelper> dynamicCheckers = C.newMap();

    /**
     * Store a guarded resource to the thread local
     *
     * @param tgt the object to be guarded
     */
    public static void setTarget(Object tgt) {
        target.set(tgt);
    }

    private static Object getTarget() {
        return target.get();
    }

    /**
     * Clear the guarded resource from the thread local
     */
    public static void clearTarget() {
        target.remove();
    }

    public static <T> void registerDynamicPermissionChecker(DynamicPermissionCheckHelper<T> checker, Class<T> clz) {
        dynamicCheckers.put(clz, checker);
    }

    /**
     * Check if a user has access to a guarded resource
     *
     * @param user
     * @param guarded
     * @param context
     * @return {@code true} if the user has access to the resource or {@code false} otherwise
     */
    public static boolean hasAccessTo(Principal user, Guarded guarded, AAAContext context) {
        E.NPE(user, guarded, context);
        AuthorizationService author = context.getAuthorizationService();
        Privilege prU = author.getPrivilege(user, context);
        Privilege prG = guarded.getPrivilege();
        if (null != prU && null != prG) {
            if (prU.getLevel() >= prG.getLevel()) return true;
        }

        Permission peG = guarded.getPermission();
        if (null == peG) {
            return false;
        }

        Collection<Role> roles = author.getRoles(user, context);
        boolean hasAccess = false;
        for (Role role: roles) {
            Collection<Permission> perms = author.getPermissions(role, context);
            if (perms.contains(peG)) {
                hasAccess = true;
                break;
            }
        }

        if (!hasAccess) return false;
        if (!peG.isDynamic()) return true;

        Object o = context.getGuardedTarget();
        E.NPE(o);
        Class<?> c = o.getClass();
        DynamicPermissionCheckHelper dc = dynamicCheckers.get(c);
        if (null == dc) return false;
        return dc.isAssociated(o, user);
    }
}
