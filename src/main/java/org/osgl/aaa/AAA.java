package org.osgl.aaa;

import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The facade to access osgl aaa security library functions
 */
public enum  AAA {
    ;

    /**
     * The recommended name of system principal
     */
    public static final String SYSTEM = "__sys";

    /**
     * The recommended super user privilege level
     */
    public static final int SUPER_USER = 9999;

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
        AAAContext ctx = AAA.context.get();
        if (null != ctx) ctx.setCurrentPrincipal(null);
        AAA.context.remove();
    }

    private static AAAContext context() {
        return context.get();
    }

    public static Principal currentPrincipal() {
        AAAContext ctx = context();
        if (null == ctx) return null;
        return ctx.getCurrentPrincipal();
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
        AuthorizationService auth = context.getAuthorizationService();
        Privilege prU = auth.getPrivilege(user, context);
        Privilege prG = guarded.getPrivilege();
        if (null != prU && null != prG) {
            if (prU.getLevel() >= prG.getLevel()) return true;
        }

        Permission peG = guarded.getPermission();
        if (null == peG) {
            return false;
        }

        Collection<Permission> perms = auth.getAllPermissions(user, context);
        boolean hasAccess = perms.contains(peG);

        if (!hasAccess) return false;
        if (!peG.isDynamic() || S.eq(AAA.SYSTEM, user.getName())) return true;

        Object o = context.getGuardedTarget();
        E.NPE(o);
        Class<?> c = o.getClass();
        DynamicPermissionCheckHelper dc = cachedDynamicPermissionCheckHelper(c);
        return dc.isAssociated(o, user);
    }

    private static DynamicPermissionCheckHelper searchDPCHfromInterfaces(Class<?> c) {
        DynamicPermissionCheckHelper dc = dynamicCheckers.get(c);
        if (null != dc) return dc;
        Class[] classes = c.getClasses();
        Class[] intfs = c.getInterfaces();
        C.List<Class> cl = C.listOf(intfs).append(C.listOf(classes));
        for (Class c0: cl) {
            dc = dynamicCheckers.get(c0);
            if (null != dc) return dc;
        }
        return null;
    }

    private static Map<Class, DynamicPermissionCheckHelper> dpchCache = new HashMap<Class, DynamicPermissionCheckHelper>();

    private static final DynamicPermissionCheckHelper NULL_DPCH = new DynamicPermissionCheckHelper() {
        @Override
        public boolean isAssociated(Object target, Principal user) {
            return false;
        }
    };

    private static DynamicPermissionCheckHelper cachedDynamicPermissionCheckHelper(Class<?> c) {
        DynamicPermissionCheckHelper dc = dpchCache.get(c);
        if (null == dc) {
            dc = searchForDynamicPermissionCheckHelper(c);
            if (null == dc) {
                dc = NULL_DPCH;
            }
            dpchCache.put(c, dc);
        }
        return dc;
    }

    private static DynamicPermissionCheckHelper searchForDynamicPermissionCheckHelper(Class<?> c) {
        DynamicPermissionCheckHelper dc = searchDPCHfromInterfaces(c);
        if (null != dc) return dc;
        while (null == dc && c != null) {
            c = c.getSuperclass();
            dc = searchDPCHfromInterfaces(c);
            if (null != dc) {
                return dc;
            }
        }
        return null;
    }

    private static void noAccess() {
        throw new NoAccessException();
    }

    private static void noAccess(String reason) {
        throw new NoAccessException(reason);
    }

    public static boolean hasPermission(Object target, String permName, boolean allowSystem, AAAContext context) {
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        } else if (context.allowSuperUser() && context.isSuperUser(user)) {
            return true;
        }
        AAAPersistentService db = context.getPersistentService();
        Permission perm = db.findByName(permName, Permission.class);
        if (null == perm) return false;

        if (null == target) {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        }
        Object prevTarget = context.getGuardedTarget();
        context.setGuardedTarget(target);
        try {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        } finally {
            context.setGuardedTarget(prevTarget);
        }
    }

    public static boolean hasPermission(Object target, String permName, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        return hasPermission(target, permName, allowSystem, context);
    }

    public static boolean hasPermission(Object target, Permission permission, boolean allowSystem, AAAContext context) {
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        } else if (context.allowSuperUser() && context.isSuperUser(user)) {
            return true;
        }
        Permission perm = permission;
        if (null == perm) return false;

        if (null == target) {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        }
        Object prevTarget = context.getGuardedTarget();
        context.setGuardedTarget(target);
        try {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        } finally {
            context.setGuardedTarget(prevTarget);
        }
    }

    public static boolean hasPermission(Object target, Permission permission, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        return hasPermission(target, permission, allowSystem, context);
    }

    public static void requirePermission(Permission perm) throws NoAccessException {
        requirePermission(null, perm, true);
    }

    /**
     * Authorize by permission. {@link org.osgl.aaa.AllowSystemAccount system account is allowed}
     * @param perm the permission name
     * @throws NoAccessException if the {@link AAAContext#getCurrentPrincipal() current principal}
     *                           does not have required permission
     */
    public static void requirePermission(String perm) throws NoAccessException {
        requirePermission(null, perm, true);
    }

    public static void requirePermission(String perm, AAAContext context) throws NoAccessException {
        requirePermission(null, perm, true, context);
    }

    public static void requirePermission(Permission perm, boolean allowSystem) throws NoAccessException {
        requirePermission(null, perm, allowSystem);
    }

    public static void requirePermission(Permission perm, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, perm, allowSystem, context);
    }

    public static void requirePermission(String perm, boolean allowSystem) throws NoAccessException {
        requirePermission(null, perm, allowSystem);
    }

    public static void requirePermission(String perm, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, perm, allowSystem, context);
    }

    public static void requirePermission(Object target, Permission perm) throws NoAccessException {
        requirePermission(target, perm, true);
    }

    public static void requirePermission(Object target, Permission perm, AAAContext context) throws NoAccessException {
        requirePermission(target, perm, true, context);
    }

    public static void requirePermission(Object target, String perm) throws NoAccessException {
        requirePermission(target, perm, true);
    }

    public static void requirePermission(Object target, String perm, AAAContext context) throws NoAccessException {
        requirePermission(target, perm, true, context);
    }

    public static void requirePermission(Object target, Permission permission, boolean allowSystem) {
        if (!hasPermission(target, permission, allowSystem)) noAccess();
    }

    public static void requirePermission(Object target, Permission permission, boolean allowSystem, AAAContext context) {
        if (!hasPermission(target, permission, allowSystem, context)) noAccess();
    }

    public static void requirePermission(Object target, String permName, boolean allowSystem) {
        if (!hasPermission(target, permName, allowSystem)) noAccess();
    }


    public static void requirePermission(Object target, String permName, boolean allowSystem, AAAContext context) {
        if (!hasPermission(target, permName, allowSystem, context)) noAccess();
    }

    public static boolean hasPrivilege(Privilege privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        }
        Privilege priv = privilege;
        if (null == priv) return false;
        AuthorizationService auth = context.getAuthorizationService();
        Privilege userPriv = auth.getPrivilege(user, context);
        if (null == userPriv) return false;
        return userPriv.getLevel() >= priv.getLevel();
    }

    public static boolean hasPrivilege(Privilege privilege, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        return hasPrivilege(privilege, allowSystem, context);
    }

    public static boolean hasPrivilege(String privName, boolean allowSystem, AAAContext context) {
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
        if (null == userPriv) return false;
        return userPriv.getLevel() >= priv.getLevel();
    }

    public static boolean hasPrivilege(String privName, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        return hasPrivilege(privName, allowSystem, context);
    }

    public static void requirePrivilege(Privilege privilege) {
        requirePrivilege(privilege, true);
    }

    public static void requirePrivilege(Privilege privilege, AAAContext context) {
        requirePrivilege(privilege, true, context);
    }

    public static void requirePrivilege(String privName) {
        requirePrivilege(privName, true);
    }

    public static void requirePrivilege(String privName, AAAContext context) {
        requirePrivilege(privName, true, context);
    }

    public static void requirePrivilege(Privilege privilege, boolean allowSystem) {
        if (!hasPrivilege(privilege, allowSystem)) noAccess();
    }

    public static void requirePrivilege(Privilege privilege, boolean allowSystem, AAAContext ctx) {
        if (!hasPrivilege(privilege, allowSystem, ctx)) noAccess();
    }

    public static void requirePrivilege(String privName, boolean allowSystem) {
        if (!hasPrivilege(privName, allowSystem)) noAccess();
    }

    public static void requirePrivilege(String privName, boolean allowSystem, AAAContext ctx) {
        if (!hasPrivilege(privName, allowSystem, ctx)) noAccess();
    }

    private static boolean hasPermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        } else if (context.allowSuperUser() && context.isSuperUser(user)) {
            return true;
        }
        Privilege priv = privilege;
        AuthorizationService auth = context.getAuthorizationService();
        if (null != priv) {
            Privilege userPriv = auth.getPrivilege(user, context);
            if (null != userPriv && userPriv.getLevel() >= priv.getLevel()) {
                return true;
            }
        }

        Permission perm = permission;
        if (null == perm) {
            return false;
        }

        if (null == target) {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        }
        Object prevTarget = context.getGuardedTarget();
        context.setGuardedTarget(target);
        try {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        } finally {
            context.setGuardedTarget(prevTarget);
        }
    }

    private static boolean hasPermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        return hasPermissionOrPrivilege(target, permission, privilege, allowSystem, context);
    }

    private static boolean hasPermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getCurrentPrincipal();
        if (null == user) {
            if (!allowSystem) noAccess("Cannot find current principal");
            user = context.getSystemPrincipal();
        } else if (context.allowSuperUser() && context.isSuperUser(user)) {
            return true;
        }
        AAAPersistentService db = context.getPersistentService();
        Privilege priv = db.findByName(privilege, Privilege.class);
        AuthorizationService auth = context.getAuthorizationService();
        if (null != priv) {
            Privilege userPriv = auth.getPrivilege(user, context);
            if (userPriv.getLevel() >= priv.getLevel()) return true;
        }
        Permission perm = db.findByName(permission, Permission.class);
        if (null == perm) {
            return false;
        }

        if (null == target) {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        }
        Object prevTarget = context.getGuardedTarget();
        context.setGuardedTarget(target);
        try {
            return hasAccessTo(user, Guarded.Factory.byPermission(perm), context);
        } finally {
            context.setGuardedTarget(prevTarget);
        }
    }

    private static boolean hasPermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem) {
        AAAContext context = context();
        if (null == context) noAccess("AAA context not found");
        return hasPermissionOrPrivilege(target, permission, privilege, allowSystem, context);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem) {
        if (!hasPermissionOrPrivilege(target, permission, privilege, allowSystem)) noAccess();
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        if (!hasPermissionOrPrivilege(target, permission, privilege, allowSystem, context)) noAccess();
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem) {
        if (!hasPermissionOrPrivilege(target, permission, privilege, allowSystem)) noAccess();
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem, AAAContext ctx) {
        if (!hasPermissionOrPrivilege(target, permission, privilege, allowSystem, ctx)) noAccess();
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege) {
        requirePermissionOrPrivilege(null, permission, privilege, true);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, AAAContext ctx) {
        requirePermissionOrPrivilege(null, permission, privilege, true, ctx);
    }

    public static void requirePermissionOrPrivilege(String permission, String privilege) {
        requirePermissionOrPrivilege(null, permission, privilege, true);
    }

    public static void requirePermissionOrPrivilege(String permission, String privilege, AAAContext ctx) {
        requirePermissionOrPrivilege(null, permission, privilege, true, ctx);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege) {
        requirePermissionOrPrivilege(target, permission, privilege, true);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, AAAContext ctx) {
        requirePermissionOrPrivilege(target, permission, privilege, true, ctx);
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege) {
        requirePermissionOrPrivilege(target, permission, privilege, true);
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege, AAAContext ctx) {
        requirePermissionOrPrivilege(target, permission, privilege, true, ctx);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem, AAAContext ctx) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem, ctx);
    }

    public static void requirePermissionOrPrivilege(String permission, String privilege, boolean allowSystem) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem);
    }

    public static void requirePermissionOrPrivilege(String permission, String privilege, boolean allowSystem, AAAContext ctx) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem, ctx);
    }
}
