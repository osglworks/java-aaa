package org.osgl.aaa;

import org.osgl.$;
import org.osgl.aaa.impl.SimplePrincipal;
import org.osgl.aaa.impl.SimplePrivilege;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The facade to access osgl aaa security library functions
 */
public enum  AAA {
    ;

    public static final Logger logger = LogManager.get(AAA.class);

    /**
     * The recommended name of system principal
     */
    public static final String SYSTEM = "__sys";

    /**
     * The recommended super user privilege level
     */
    public static final int SUPER_USER = 9999;

    private static final Map<$.T2<Permission, Class>, DynamicPermissionCheckHelper> dynamicCheckers = C.newMap();

    private static final ThreadLocal<AAAContext> context = new ThreadLocal<AAAContext>();

    private static final Permission NULL_PERMISSION = null;

    /**
     * Set AAAContext to thread local
     *
     * @param context the context to be set to ThreadLocal
     */
    @SuppressWarnings("unused")
    public static void setContext(AAAContext context) {
        if (null == context) {
            clearContext();
        } else {
            AAA.context.set(context);
        }
    }

    /**
     * Clear AAAContext thread local
     */
    public static void clearContext() {
        AAAContext ctx = AAA.context.get();
        if (null != ctx) ctx.setCurrentPrincipal(null);
        AAA.context.remove();
    }

    /**
     * Return the {@link AAAContext context} from the current thread local
     * @return the context
     */
    public static AAAContext context() {
        return context.get();
    }

    @SuppressWarnings("unused")
    public static <T> void registerDynamicPermissionChecker(DynamicPermissionCheckHelper<T> checker, Class<T> clz) {
        List<? extends Permission> l = checker.permissions();
        if (l.isEmpty()) {
            dynamicCheckers.put(dpchKey(NULL_PERMISSION, clz), checker);
        } else {
            for (Permission p : l) {
                dynamicCheckers.put(dpchKey(p, clz), checker);
            }
        }
    }

    /**
     * Check if a user has access to a guarded resource
     *
     * @param user the principal
     * @param guarded the guarded object
     * @param context the AAA context
     * @return {@code true} if the user has access to the resource or {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
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
        DynamicPermissionCheckHelper dc = cachedDynamicPermissionCheckHelper(peG, c);
        return dc.isAssociated(o, user);
    }

    private static DynamicPermissionCheckHelper searchForDynamicPermissionCheckHelper(Permission p, Class<?> c) {
        DynamicPermissionCheckHelper dc;
        while (c != Object.class && c != null) {
            dc = searchDpchFromInterfaces(p, c);
            if (null != dc) {
                return dc;
            }
            c = c.getSuperclass();
        }
        return null;
    }

    private static DynamicPermissionCheckHelper searchDpchFromInterfaces(Permission p, Class<?> c) {
        Class[] intfs = c.getInterfaces();
        C.List<Class> cl = C.listOf(intfs).append(c);
        for (Class c0: cl) {
            DynamicPermissionCheckHelper dc = dynamicCheckers.get(dpchKey(p, c0));
            if (null != dc) {
                return dc;
            }
        }
        if (NULL_PERMISSION != p) {
            for (Class c0: cl) {
                DynamicPermissionCheckHelper dc = dynamicCheckers.get(dpchKey(NULL_PERMISSION, c0));
                if (null != dc) {
                    return dc;
                }
            }
        }
        return null;
    }

    private static final DynamicPermissionCheckHelper NULL_DPCH = new DynamicPermissionCheckHelper() {
        @Override
        public List<Permission> permissions() {
            return C.list();
        }

        @Override
        public boolean isAssociated(Object target, Principal user) {
            return false;
        }
    };

    private static DynamicPermissionCheckHelper cachedDynamicPermissionCheckHelper(Permission p, Class<?> c) {
        $.T2<Permission, Class> key = dpchKey(p, c);
        DynamicPermissionCheckHelper dc = dynamicCheckers.get(key);
        if (null == dc) {
            dc = searchForDynamicPermissionCheckHelper(p, c);
            if (null == dc) {
                dc = NULL_DPCH;
            }
            dynamicCheckers.put(key, dc);
        }
        return dc;
    }

    private static void noAccess() {
        throw new NoAccessException();
    }

    /**
     * Check if the current principal has permission specified on the target object.
     * <p>
     * The current principal is get from the current context via
     * {@link AAAContext#getPrincipal(boolean)} call, while
     * the current context is get via {@link AAA#context()} call.
     * </p>
     *
     * @param target the guarded object
     * @param permission the name of the permission required
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @return {@code true} if the current principal has permission to the target object
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object target, String permission, boolean allowSystem) {
        return hasPermission(target, permission, allowSystem, context());
    }

    /**
     * Check if the current principal has permission specified on the target.
     * <p>
     *     The current principal is get from the current context via
     *     {@link AAAContext#getPrincipal(boolean)} call on the
     *     {@link AAAContext context} specified
     * </p>
     * @param target the guarded target
     * @param permission the name of the permission required
     * @param allowSystem specify if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in the context
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     */
    public static boolean hasPermission(Object target, String permission, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        AAAPersistentService db = context.getPersistentService();
        Permission perm = db.findByName(permission, Permission.class);
        return null != perm && hasPermission(target, user, perm, context);
    }

    /**
     * Check if the current principal has permission specified on the target.
     * <p>
     *     The current principal is get from the current context via
     *     {@link AAAContext#getPrincipal(boolean)} call on the
     *     {@link AAAContext context} specified
     * </p>
     * @param target the guarded target
     * @param permission the permission required
     * @param allowSystem specify if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in the context
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     */
    public static boolean hasPermission(Object target, Permission permission, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        return hasPermission(target, user, permission, context);
    }

    /**
     * Check if the current principal has permission specified on the target object.
     * <p>
     * The current principal is get from the current context via
     * {@link AAAContext#getPrincipal(boolean)} call, while
     * the current context is get via {@link AAA#context()} call.
     * </p>
     *
     * @param target the guarded object
     * @param permission the permission required
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @return {@code true} if the current principal has permission to the target object
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object target, Permission permission, boolean allowSystem) {
        return hasPermission(target, permission, allowSystem, context());
    }

    /**
     * Check if the principal specified has permission specified on the target.
     * <p>
     *     The current principal is get from the current context via
     *     {@link AAAContext#getPrincipal(boolean)} call on the
     *     {@link AAAContext context} specified
     * </p>
     * @param target the guarded target
     * @param user the principal
     * @param permission the permission required
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     */
    public static boolean hasPermission(Object target, Principal user, Permission permission, AAAContext context) {
        if (null == user || null == permission || null == context) {
            return false;
        }
        if (context.allowSuperUser() && context.isSuperUser(user)) {
            return true;
        }
        Object prevTarget = null;
        if (null != target) {
            prevTarget = context.getGuardedTarget();
            context.setGuardedTarget(target);
        }
        try {
            return hasAccessTo(user, Guarded.Factory.byPermission(permission), context);
        } finally {
            if (null != prevTarget) {
                context.setGuardedTarget(prevTarget);
            }
        }
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(String permission) throws NoAccessException {
        requirePermission(null, permission, true);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with argument {@code allowSystem} set to {@code allowSystem} specified
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission name
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(String permission, boolean allowSystem) throws NoAccessException {
        requirePermission(null, permission, allowSystem);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(String permission, AAAContext context) throws NoAccessException {
        requirePermission(null, permission, true, context);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission name
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @param context the {@link AAAContext aaa context}
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(String permission, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, permission, allowSystem, context);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Permission permission) throws NoAccessException {
        requirePermission(null, permission, true);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with argument {@code allowSystem} set to specified {@code allowSystem}
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Permission permission, boolean allowSystem) throws NoAccessException {
        requirePermission(null, permission, allowSystem);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with argument {@code allowSystem} set to specified {@code allowSystem}
     *     </li>
     *     <li>Guarded object - established via {@link AAAContext#getGuardedTarget()} call</li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @param context the {@link AAAContext aaa context}
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Permission permission, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, permission, allowSystem, context);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Object target, Permission permission) throws NoAccessException {
        requirePermission(target, permission, true);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission
     * @param context the AAA context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Object target, Permission permission, AAAContext context) throws NoAccessException {
        requirePermission(target, permission, true, context);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Object target, String permission) throws NoAccessException {
        requirePermission(target, permission, true);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} set to {@code true}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission name
     * @param context the AAA context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object target, String permission, AAAContext context) throws NoAccessException {
        requirePermission(target, permission, true, context);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} argument set to specified {@code allowSystem}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object target, Permission permission, boolean allowSystem) {
        requirePermission(target, permission, allowSystem, context());
    }


    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>{@link AAAContext context} - established via {@link AAA#context()} call</li>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} argument set to specified {@code allowSystem}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission name
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object target, String permission, boolean allowSystem) {
        requirePermission(target, permission, allowSystem, context());
    }


    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} argument set to specified {@code allowSystem}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object target, Permission permission, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        requirePermission(target,user, permission, context);
    }

    /**
     * Authorize by permission.
     * <p>This method call has implied a set of implicit objects:</p>
     * <ul>
     *     <li>
     *         {@link Principal principal} - established via {@link AAAContext#getPrincipal(boolean)}
     *         call with {@code allowSystem} argument set to specified {@code allowSystem}
     *     </li>
     * </ul>
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param permission the permission name
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object target, String permission, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        Permission perm = context.getPersistentService().findByName(permission, Permission.class);
        requirePermission(target,user, perm, context);
    }

    /**
     * Authorize by permission.
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param target the guarded object
     * @param user the principal
     * @param permission the permission
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object target, Principal user, Permission permission, AAAContext context) {
        Auditor auditor = context.getAuditor();
        if (!hasPermission(target, user, permission, context)) {
            auditor.audit(target, user, permission.getName(), null, false, null);
            noAccess();
        } else {
            auditor.audit(target, user, permission.getName(), null, true, null);
        }
    }

    public static boolean hasPrivilege(Privilege privilege) {
        return hasPrivilege(privilege, true);
    }

    public static boolean hasPrivilege(Privilege privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        return (null != user) && hasPrivilege(user, privilege, context);
    }

    public static boolean hasPrivilege(Privilege privilege, boolean allowSystem) {
        return hasPrivilege(privilege, allowSystem, context());
    }

    public static boolean hasPrivilege(String privName, boolean allowSystem, AAAContext context) {
        AAAPersistentService db = context.getPersistentService();
        Privilege priv = db.findByName(privName, Privilege.class);
        return null != priv && hasPrivilege(priv, allowSystem, context);
    }

    public static boolean hasPrivilege(String privName, boolean allowSystem) {
        return hasPrivilege(privName, allowSystem, context());
    }

    public static boolean hasPrivilege(Principal user, Privilege privilege, AAAContext context) {
        AuthorizationService auth = context.getAuthorizationService();
        Privilege userPrivilege = auth.getPrivilege(user, context);
        return null != userPrivilege && userPrivilege.getLevel() >= privilege.getLevel();
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
        requirePrivilege(privilege, allowSystem, context());
    }

    public static void requirePrivilege(Privilege privilege, boolean allowSystem, AAAContext ctx) {
        Principal user = ctx.getPrincipal(allowSystem);
        requirePrivilege(user, privilege, ctx);
    }

    public static void requirePrivilege(String privilege, boolean allowSystem) {
        requirePrivilege(privilege, allowSystem, context());
    }

    public static void requirePrivilege(String privilege, boolean allowSystem, AAAContext ctx) {
        AAAPersistentService db = ctx.getPersistentService();
        Privilege p = db.findByName(privilege, Privilege.class);
        Principal user = ctx.getPrincipal(allowSystem);
        requirePrivilege(user, p, ctx);
    }

    public static void requirePrivilege(Principal user, Privilege privilege, AAAContext context) {
        Auditor auditor = context.getAuditor();
        if (!hasPrivilege(user, privilege, context)) {
            auditor.audit(null, user, null, privilege.getName(), false, "");
            noAccess();
        } else {
            auditor.audit(null, user, null, privilege.getName(), true, "");
        }
    }

    public static boolean hasPermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem) {
        return hasPermissionOrPrivilege(target, permission, privilege, allowSystem, context());
    }

    public static boolean hasPermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem) {
        return hasPermissionOrPrivilege(target, permission, privilege, allowSystem, context());
    }

    public static boolean hasPermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        return null != user && hasPermissionOrPrivilege(target, user, permission, privilege, context);
    }

    public static boolean hasPermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        return null != user && hasPermissionOrPrivilege(target, user, permission, privilege, context);
    }

    public static boolean hasPermissionOrPrivilege(Object target, Principal user, String permission, String privilege, AAAContext context) {
        E.illegalArgumentIf(null == permission && null == privilege);
        E.illegalArgumentIf(null == user);
        AAAPersistentService persistentService = context.getPersistentService();
        Permission perm = null != permission ? persistentService.findByName(permission, Permission.class) : null;
        Privilege priv = null != privilege ? persistentService.findByName(privilege, Privilege.class) : null;
        if (null == perm && null != permission) {
            logger.warn("Unknown permission %s", permission);
        }
        if (null == priv && null != privilege) {
            logger.warn("Unknown privilege %s", privilege);
        }
        return (null != perm || null != priv) && hasPermissionOrPrivilege(target, user, perm, priv, context);
    }

    public static boolean hasPermissionOrPrivilege(Object target, Principal user, Permission permission, Privilege privilege, AAAContext context) {
        E.illegalArgumentIf(null == permission && null == privilege);
        if (null == user) {
            return false;
        }
        boolean retVal = null != privilege && hasPrivilege(user, privilege, context);
        return retVal || (null != permission && hasPermission(target, user, permission, context));
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem) {
        requirePermissionOrPrivilege(target, permission, privilege, allowSystem, context());
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        requirePermissionOrPrivilege(target, user, permission, privilege, context);
    }

    public static void requirePermissionOrPrivilege(Object target, Principal user, String permission, String privilege, AAAContext ctx) {
        Auditor auditor = ctx.getAuditor();
        if (!hasPermissionOrPrivilege(target, user, permission, privilege, ctx)) {
            auditor.audit(target, user, permission, privilege, false, "");
            noAccess();
        } else {
            auditor.audit(target, user, permission, privilege, true, "");
        }
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

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege) {
        requirePermissionOrPrivilege(target, permission, privilege, true);
    }

    public static void requirePermissionOrPrivilege(Object target, String permission, String privilege, AAAContext ctx) {
        requirePermissionOrPrivilege(target, permission, privilege, true, ctx);
    }

    public static void requirePermissionOrPrivilege(String permission, String privilege, boolean allowSystem) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem);
    }

    public static void requirePermissionOrPrivilege(String permission, String privilege, boolean allowSystem, AAAContext ctx) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem, ctx);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege) {
        requirePermissionOrPrivilege(target, permission, privilege, true);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, AAAContext ctx) {
        requirePermissionOrPrivilege(target, permission, privilege, true, ctx);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem) {
        requirePermissionOrPrivilege(target, permission, privilege, allowSystem, context());
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem, AAAContext ctx) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem, ctx);
    }

    public static void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        Principal user = context.getPrincipal(allowSystem);
        requirePermissionOrPrivilege(target, user, permission, privilege, context);
    }

    public static void requirePermissionOrPrivilege(Object target,
                                                    Principal user,
                                                    Permission permission,
                                                    Privilege privilege,
                                                    AAAContext context
    ) {
        Auditor auditor = context.getAuditor();
        if (!hasPermissionOrPrivilege(target, user, permission, privilege, context)) {
            auditor.audit(target, user, permission.getName(), privilege.getName(), false, "");
            noAccess();
        } else {
            auditor.audit(target, user, permission.getName(), privilege.getName(), true, "");
        }
    }

    /**
     * Create a default super user principal with name "{@code su}"
     * @return a principal as described above
     */
    public static Principal createSuperUser() {
        return createSuperUser("su");
    }

    /**
     * Create a super user principal with name specified. The super user will have the {@link #SUPER_USER} privilege
     * @return a principal as described above
     */
    public static Principal createSuperUser(String userName) {
        return new SimplePrincipal.Builder(userName).grantPrivilege(new SimplePrivilege("root", SUPER_USER)).toPrincipal();
    }

    private static $.T2<Permission, Class> dpchKey(Permission p, Class c) {
        return $.T2(p, c);
    }

}
