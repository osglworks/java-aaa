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

    private static final ThreadLocal<AAAContext> context = new ThreadLocal<AAAContext>();

    /**
     * Store a guarded resource to the thread local
     *
     * @param tgt the object to be guarded
     */
    public static void setTarget(Object tgt) {
        target.set(tgt);
    }

    private static Object target() {
        return target.get();
    }

    /**
     * Clear the guarded resource from the thread local
     */
    public static void clearTarget() {
        target.remove();
    }

    /**
     * Set AAAContext to thread local
     *
     * @param context
     */
    public static void setContext(AAAContext context) {
        if (null == context) AAA.context.remove();
        else AAA.context.set(context);
    }

    /**
     * Clear AAAContext thread local
     */
    public static void clearContext() {
        AAA.context.remove();
    }

    private static AAAContext context() {
        return context.get();
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

    private static void noAccess() {
        throw new NoAccessException();
    }

    private static void noAccess(String reason) {
        throw new NoAccessException(reason);
    }

    public static boolean hasPermission(String permName, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        }
        AAAPersistentService db = context.getPersistentService();
        Permission perm = db.findByName(permName, Permission.class);
        if (null == perm) return false;
        AuthorizationService auth = context.getAuthorizationService();
        Collection<Permission> perms = auth.getAllPermissions(user, context);
        return perms.contains(perm);
    }

    /**
     * Authorize by permission. {@link org.osgl.aaa.AllowSystemAccount system account is allowed}
     * @param perm the permission name
     * @throws NoAccessException if the {@link AAAContext#getCurrentPrincipal() current principal}
     *                           does not have required permission
     */
    public static void requirePermission(String perm) throws NoAccessException {
        requirePermission(perm, true);
    }

    public static void requirePermission(String permName, boolean allowSystem) {
        if (!hasPermission(permName, allowSystem)) noAccess();
    }

    public static boolean hasPrivilege(String privName, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        }
        AAAPersistentService db = context.getPersistentService();
        Privilege priv = db.findByName(privName, Privilege.class);
        if (null == priv) return false;
        AuthorizationService auth = context.getAuthorizationService();
        Privilege userPriv = auth.getPrivilege(user, context);
        return userPriv.getLevel() >= priv.getLevel();
    }

    public static void requirePrivilege(String privName, boolean allowSystem) {
        if (!hasPrivilege(privName, allowSystem)) noAccess();
    }

    public static void requirePrivilege(String privName) {
        requirePrivilege(privName, true);
    }

}
