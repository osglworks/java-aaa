package org.osgl.aaa;

/*-
 * #%L
 * Java AAA Service
 * %%
 * Copyright (C) 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgl.$;
import org.osgl.aaa.impl.SimplePrincipal;
import org.osgl.aaa.impl.SimplePrivilege;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;
import osgl.version.Version;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The facade to access osgl aaa security library functions
 */
@SuppressWarnings("unused")
public enum  AAA {
    ;

    public static final Version VERSION = Version.get();

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

    private static AAAContext defaultContext;

    public static void setDefaultContext(AAAContext context) {
        defaultContext = $.requireNotNull(context);
    }

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
        AAAContext current = context.get();
        return null != current ? current : defaultContext;
    }

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
     * Check if current user has permission specified on target resource specified (implicitly)
     *
     * This will call {@link #hasPermission(Object, Permission)} with `null` passed in as the
     * guarded target
     *
     * @param permission the permission required
     * @return `true` if the current principal has the permission on the target resource
     */
    public static boolean hasPermission(Permission permission) {
        return hasPermission(null, permission, true, null);
    }

    /**
     * Check if current user has permission specified on target resource specified (implicitly)
     *
     * This will call {@link #hasPermission(Object, String)} with `null` passed in
     * as the guarded resource
     *
     * @param permissionName the name of the permission required
     * @return `true` if the current principal has the permission on the target resource
     */
    public static boolean hasPermission(String permissionName) {
        return hasPermission(null, permissionName, true, null);
    }

    /**
     * Check if current user has permission specified on target resource specified (implicitly)
     *
     * This will call {@link #hasPermission(Object, String)} with `null` passed in
     * as the guarded resource
     *
     * @param permissionEnum enum that provides the name of the permission required
     * @return `true` if the current principal has the permission on the target resource
     */
    public static boolean hasPermission(Enum<?> permissionEnum) {
        return hasPermission(null, permissionEnum.name(), true, null);
    }

    /**
     * Check if current user has permission specified on target resource specified
     *
     * This will call {@link #hasPermission(Object, Permission, boolean)} with
     * `true` as `allowSystem` flag
     *
     * @param guardedResource the guarded resource
     * @param permission the permission required
     * @return `true` if the current principal has the permission on the target resource
     */
    public static boolean hasPermission(Object guardedResource, Permission permission) {
        return hasPermission(guardedResource, permission, true, null);
    }

    /**
     * Check if current user has permission specified on target resource specified
     *
     * This will call {@link #hasPermission(Object, String, boolean)} with
     * `true` as `allowSystem` flag
     *
     * @param guardedResource the guarded resource
     * @param permissionName the name of the permission required
     * @return `true` if the current principal has the permission on the target resource
     */
    public static boolean hasPermission(Object guardedResource, String permissionName) {
        return hasPermission(guardedResource, permissionName, true, null);
    }

    /**
     * Check if current user has permission specified on target resource specified
     *
     * This will call {@link #hasPermission(Object, String, boolean)} with
     * `true` as `allowSystem` flag
     *
     * @param guardedResource the guarded resource
     * @param permissionEnum enum that provides the name of the permission required
     * @return `true` if the current principal has the permission on the target resource
     */
    public static boolean hasPermission(Object guardedResource, Enum<?> permissionEnum) {
        return hasPermission(guardedResource, permissionEnum.name(), true, null);
    }

    /**
     * Check if a user has permission specified on a target
     *
     * This method will call {@link #hasPermission(Object, Principal, Permission, AAAContext)} and pass
     * `null` as context parameter
     *
     * @param guardedResource the guarded object
     * @param principal the user principal
     * @param permission the permission required
     * @return {@code true} if the user has the permission on the target or {@code false} otherwise
     */
    public static boolean hasPermission(Object guardedResource, Principal principal, Permission permission) {
        return hasPermission(guardedResource, principal, permission, null);
    }

    /**
     * Check if a user has permission specified on a target
     *
     * This method will call {@link #hasPermission(Object, Principal, String, AAAContext)} and pass
     * `null` as context parameter
     *
     * @param guardedResource the guarded resource
     * @param principal  the user principal
     * @param permissionName the name of the permission required
     * @return `true` if the principal has permission on the guarded resource
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object guardedResource, Principal principal, String permissionName) {
        return hasPermission(guardedResource, principal, permissionName, null);
    }

    /**
     * Check if a user has permission specified on a target
     *
     * This method will call {@link #hasPermission(Object, Principal, String, AAAContext)} and pass
     * `null` as context parameter
     *
     * @param guardedResource the guarded resource
     * @param principal  the user principal
     * @param permissionEnum the enum that provides the name of the permission required
     * @return `true` if the principal has permission on the guarded resource
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object guardedResource, Principal principal, Enum<?> permissionEnum) {
        return hasPermission(guardedResource, principal, permissionEnum.name(), null);
    }

    /**
     * Check if the current principal has permission specified on the target object.
     *
     * This will call {@link #hasPermission(Object, Permission, boolean, AAAContext)} and
     * pass in the `null` as context
     *
     * @param guardedResource the guarded resource
     * @param permission the permission required
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @return {@code true} if the current principal has permission to the target object
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object guardedResource, Permission permission, boolean allowSystem) {
        return hasPermission(guardedResource, permission, allowSystem, null);
    }

    /**
     * Check if the current principal has permission specified on the target object.
     *
     * This will call {@link #hasPermission(Object, String, boolean, AAAContext)} and
     * pass in the `null` as context
     *
     * @param guardedResource the guarded object
     * @param permissionName the name of the permission required
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @return {@code true} if the current principal has permission to the target object
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object guardedResource, String permissionName, boolean allowSystem) {
        return hasPermission(guardedResource, permissionName, allowSystem, null);
    }

    /**
     * Check if the current principal has permission specified on the target object.
     *
     * This will call {@link #hasPermission(Object, String, boolean, AAAContext)} and
     * pass in the `null` as context
     *
     * @param guardedResource the guarded object
     * @param permissionEnum the enum that provides the name of the permission required
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @return {@code true} if the current principal has permission to the target object
     */
    @SuppressWarnings("unused")
    public static boolean hasPermission(Object guardedResource, Enum<?> permissionEnum, boolean allowSystem) {
        return hasPermission(guardedResource, permissionEnum.name(), allowSystem, null);
    }

    /**
     * Check if the current principal has permission specified on the guarded target resource.
     *
     * The logic of this method is
     *
     * ```java
     * context = ensureContext(context);
     * Principal principal = context.getPrincipal(allowSystem);
     * return hasPermission(guardedResource, principal, permission, context);
     * ```
     *
     * @param guardedResource the guarded target
     * @param permission the permission required
     * @param allowSystem specify if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in the context
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     */
    public static boolean hasPermission(Object guardedResource, Permission permission, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal principal = context.getPrincipal(allowSystem);
        return hasPermission(guardedResource, principal, permission, context);
    }

    /**
     * Check if the current principal has permission specified by name on the target.
     *
     * The logic of this method is
     *
     * ```java
     * context = ensureContext(context);
     * Principal principal = context.getPrincipal(allowSystem);
     * return hasPermission(guardedResource, principal, permissionName, context);
     * ```
     * @param guardedResource the guarded target
     * @param permissionName the name of the permission required
     * @param allowSystem specify if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in the context
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     */
    public static boolean hasPermission(Object guardedResource, String permissionName, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal principal = context.getPrincipal(allowSystem);
        return hasPermission(guardedResource, principal, permissionName, context);
    }

    /**
     * Check if the current principal has permission specified by name on the target.
     *
     * The logic of this method is
     *
     * ```java
     * context = ensureContext(context);
     * Principal principal = context.getPrincipal(allowSystem);
     * return hasPermission(guardedResource, principal, permissionEnum.name(), context);
     * ```
     * @param guardedResource the guarded target
     * @param permissionEnum the enum that provides the name of the permission required
     * @param allowSystem specify if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in the context
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     */
    public static boolean hasPermission(Object guardedResource, Enum<?> permissionEnum, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal principal = context.getPrincipal(allowSystem);
        return hasPermission(guardedResource, principal, permissionEnum.name(), context);
    }

    /**
     * Check if the principal specified has permission specified on the target.
     *
     * Note this method will sanitize on the passed in argument:
     *
     * 1. context - if `null` passed in then it will tried to get the thread local context via {@link #context()} method
     * 1. guardedResource - if `null` passed in then it will tried to get it from `context` instance
     * 1. principal - if `null` passed in then it will tried to get one from the `context` instance
     *
     * @param guardedResource the guarded target
     * @param principal the principal
     * @param permission the permission required
     * @param context the {@link AAAContext context}
     * @return {@code true} if the current principal has the permission
     * @see AAAContext#getGuardedTarget()
     * @see AAAContext#getCurrentPrincipal()
     */
    public static boolean hasPermission(Object guardedResource, Principal principal, Permission permission, AAAContext context) {
        context = ensureContext(context);
        principal = ensurePrincipal(principal, context);
        if (checkSuperUser(principal, context)) {
            return true;
        }

        guardedResource = tryGetGuardedResource(guardedResource, context);
        AuthorizationService auth = context.getAuthorizationService();
        Collection<Permission> allPermissions = auth.getAllPermissions(principal, context);
        boolean hasAccess = allPermissions.contains(permission);
        if (!hasAccess) return false;
        if (!permission.isDynamic() || isSystem(principal)) return true;

        // in other words if permission is static then we do not need
        // a guarded resource instance
        E.illegalStateIf(null == guardedResource, "Cannot determine guarded resource for dynamic permission");
        Class<?> resourceType = guardedResource.getClass();
        DynamicPermissionCheckHelper dpch = dynamicPermissionCheckHelper(permission, resourceType);
        return dpch.isAssociated(guardedResource, principal);
    }

    /**
     * Check if the specified principal has permission specified on the target resource.
     *
     * This method will find out the {@link Permission permission instance} from the
     * `permissionName` specified and then call {@link #hasPermission(Object, Principal, Permission, AAAContext)}
     * API with the permission found. If the permission not found by name then this method will
     * return `false` directly
     *
     * @param guardedResource the guarded resource
     * @param principal   the principal
     * @param permissionName the name of the permission required
     * @param context the {@link AAAContext context}
     * @return {@code true} if the specified user has the permission
     */
    public static boolean hasPermission(Object guardedResource, Principal principal, String permissionName, AAAContext context) {
        context = ensureContext(context);
        AAAPersistentService db = context.getPersistentService();
        Permission perm = db.findByName(permissionName, Permission.class);
        return null != perm && hasPermission(guardedResource, principal, perm, context);
    }

    /**
     * Check if the specified principal has permission specified on the target resource.
     *
     * @param guardedResource the guarded resource
     * @param principal   the principal
     * @param permissionEnum the enum that provides the name of the permission required
     * @param context the {@link AAAContext context}
     * @return {@code true} if the specified user has the permission
     * @see #hasPermission(Object, Principal, String, AAAContext)
     */
    public static boolean hasPermission(Object guardedResource, Principal principal, Enum<?> permissionEnum, AAAContext context) {
        return hasPermission(guardedResource, principal, permissionEnum.name(), context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Permission permission) throws NoAccessException {
        requirePermission(null, permission, true, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permissionName the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(String permissionName) throws NoAccessException {
        requirePermission(null, permissionName, true, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permissionEnum the enum that provides the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Enum<?> permissionEnum) throws NoAccessException {
        requirePermission(null, permissionEnum.name(), true, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permission the permission
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Permission permission, boolean allowSystem) throws NoAccessException {
        requirePermission(null, permission, allowSystem);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permissionName the permission name
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(String permissionName, boolean allowSystem) throws NoAccessException {
        requirePermission(null, permissionName, allowSystem);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     * @param permissionEnum the enum that provides the permission name
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Enum<?> permissionEnum, boolean allowSystem) throws NoAccessException {
        requirePermission(null, permissionEnum.name(), allowSystem);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param permission the permission object
     * @param context the AAAContext
     * @throws NoAccessException when current user does not have permission against current target
     */
    public static void requirePermission(Permission permission, AAAContext context) throws NoAccessException {
        requirePermission(null, permission, true, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param permissionName the permission name
     * @param context  the context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(String permissionName, AAAContext context) throws NoAccessException {
        requirePermission(null, permissionName, true, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param permissionEnum the enum that provides the permission name
     * @param context the context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Enum<?> permissionEnum, AAAContext context) throws NoAccessException {
        requirePermission(null, permissionEnum.name(), true, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param permission the permission
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @param context the {@link AAAContext aaa context}
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Permission permission, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, permission, allowSystem, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param permissionName the permission name
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @param context the {@link AAAContext aaa context}
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(String permissionName, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, permissionName, allowSystem, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param permissionEnum the enum that provides permission name
     * @param allowSystem if {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    in this context
     * @param context the {@link AAAContext aaa context}
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Enum<?> permissionEnum, boolean allowSystem, AAAContext context) throws NoAccessException {
        requirePermission(null, permissionEnum.name(), allowSystem, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permission the permission
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Permission permission) throws NoAccessException {
        requirePermission(guardedResource, permission, true, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionName the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, String permissionName) throws NoAccessException {
        requirePermission(guardedResource, permissionName, true, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionEnum the enum that provides the permission name
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Enum<?> permissionEnum) throws NoAccessException {
        requirePermission(guardedResource, permissionEnum.name(), true, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permission the permission
     * @param context the AAA context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    @SuppressWarnings("unused")
    public static void requirePermission(Object guardedResource, Permission permission, AAAContext context) throws NoAccessException {
        requirePermission(guardedResource, permission, true, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionName the permission name
     * @param context the AAA context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, String permissionName, AAAContext context) throws NoAccessException {
        requirePermission(guardedResource, permissionName, true, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionEnum the enum that provides the permission name
     * @param context the AAA context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Enum<?> permissionEnum, AAAContext context) throws NoAccessException {
        requirePermission(guardedResource, permissionEnum.name(), true, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permission the permission
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Permission permission, boolean allowSystem) {
        requirePermission(guardedResource, permission, allowSystem, null);
    }


    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionName the permission name
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, String permissionName, boolean allowSystem) {
        requirePermission(guardedResource, permissionName, allowSystem, null);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionEnum the enum that provides the permission name
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Enum<?> permissionEnum, boolean allowSystem) {
        requirePermission(guardedResource, permissionEnum.name(), allowSystem, null);
    }


    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permission the permission
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Permission permission, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        requirePermission(guardedResource, user, permission, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionName the permission name
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, String permissionName, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        requirePermission(guardedResource, user, permissionName, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param permissionEnum the enum that provides the permission name
     * @param allowSystem whether {@link AAAContext#getSystemPrincipal() system principal} is allowed
     *                    to access the resource
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Enum<?> permissionEnum, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        requirePermission(guardedResource, user, permissionEnum.name(), context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param principal the principal
     * @param permission the permission
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Principal principal, Permission permission, AAAContext context) {
        context = ensureContext(context);
        Auditor auditor = context.getAuditor();
        boolean hasPermission = hasPermission(guardedResource, principal, permission, context);
        auditor.audit(guardedResource, principal, permission.getName(), null, hasPermission, null);
        if (!hasPermission) {
            noAccess();
        }
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param principal the principal
     * @param permissionName the name of the permission required
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Principal principal, String permissionName, AAAContext context) {
        context = ensureContext(context);
        Permission permission = context.getPersistentService().findByName(permissionName, Permission.class);
        requirePermission(guardedResource, principal, permission, context);
    }

    /**
     * Authorize by permission.
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param guardedResource the guarded object
     * @param principal the principal
     * @param permissionEnum the enum that provides the name of the permission required
     * @param context the AAA Context
     * @throws NoAccessException if the principal does not have permission specified on
     *         the target object
     */
    public static void requirePermission(Object guardedResource, Principal principal, Enum<?> permissionEnum, AAAContext context) {
        context = ensureContext(context);
        Permission permission = context.getPersistentService().findByName(permissionEnum.name(), Permission.class);
        requirePermission(guardedResource, principal, permission, context);
    }

    /**
     * Check if the current principal has privilege required
     * @param privilege the privilege required
     * @return `true` if the current principal has the privilege
     */
    public static boolean hasPrivilege(Privilege privilege) {
        return hasPrivilege(privilege, true, null);
    }

    /**
     * Check if the current principal has privilege required
     * @param privilegeName the name of the privilege required
     * @return `true` if the current principal has the privilege
     */
    public static boolean hasPrivilege(String privilegeName) {
        return hasPrivilege(privilegeName, true, null);
    }

    /**
     * Check if the current principal has privilege required
     * @param privilegeEnum the enum that provides the name of the privilege required
     * @return `true` if the current principal has the privilege
     */
    public static boolean hasPrivilege(Enum<?> privilegeEnum) {
        return hasPrivilege(privilegeEnum.name(), true, null);
    }

    /**
     * Check if the current principal has privilege required
     * @param privilegeLevel the privilege level required
     * @return `true` if the current principal has the privilege
     */
    public static boolean hasPrivilege(int privilegeLevel) {
        return hasPrivilege(privilegeLevel, true, null);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilege the privilege required
     * @param allowSystem should system user be used or not when there is no current principal
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(Privilege privilege, boolean allowSystem) {
        return hasPrivilege(privilege, allowSystem, null);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilegeName the name of the privilege required
     * @param allowSystem should system user be used or not when there is no current principal
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(String privilegeName, boolean allowSystem) {
        return hasPrivilege(privilegeName, allowSystem, null);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilegeEnum the enum that provides the name of the privilege required
     * @param allowSystem should system user be used or not when there is no current principal
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(Enum<?> privilegeEnum, boolean allowSystem) {
        return hasPrivilege(privilegeEnum.name(), allowSystem, null);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilegeLevel the privilege level required
     * @param allowSystem should system user be used or not when there is no current principal
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(int privilegeLevel, boolean allowSystem) {
        return hasPrivilege(privilegeLevel, allowSystem, null);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilege the privilege required
     * @param allowSystem should system user be used or not when there is no current principal
     * @param context the AAAContext instance
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(Privilege privilege, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        return (null != user) && hasPrivilege(user, privilege, context);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilegeName the name of the privilege required
     * @param allowSystem should system user be used or not when there is no current principal
     * @param context the AAAContext instance
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(String privilegeName, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        return (null != user) && hasPrivilege(user, privilegeName, context);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilegeEnum the enum that provides the name of the privilege required
     * @param allowSystem should system user be used or not when there is no current principal
     * @param context the AAAContext instance
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(Enum<?> privilegeEnum, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeEnum.name(), allowSystem, context);
    }

    /**
     * Check if the current principal has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param privilegeLevel the privilege level required
     * @param allowSystem should system user be used or not when there is no current principal
     * @param context the AAAContext instance
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(int privilegeLevel, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        return hasPrivilege(user, privilegeLevel, context);
    }

    /**
     * Check if the principal specified has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param principal the principal to be checked
     * @param privilege the privilege required
     * @param context the AAAContext instance
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(Principal principal, Privilege privilege, AAAContext context) {
        return hasPrivilege(principal, privilege.getLevel(), context);
    }

    /**
     * Check if the principal specified has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param principal the principal to be checked
     * @param privilegeName the name of the privilege required
     * @param context the AAAContext instance
     * @return `true` if the principal or system user has privilege required
     */
    public static boolean hasPrivilege(Principal principal, String privilegeName, AAAContext context) {
        context = ensureContext(context);
        AAAPersistentService persistentService = context.getPersistentService();
        return hasPrivilege(principal, persistentService.findByName(privilegeName, Privilege.class), context);
    }

    /**
     * Check if the principal specified has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param principal the principal to be checked
     * @param privilegeEnum the enum that provides the name of the privilege required
     * @param context the AAAContext instance
     * @return `true` if the principal or system user has privilege required
     */
    public static boolean hasPrivilege(Principal principal, Enum<?> privilegeEnum, AAAContext context) {
        return hasPrivilege(principal, privilegeEnum.name(), context);
    }

    /**
     * Check if the principal specified has privilege required. `allowSystem` flag is passed
     * in to provide the option whether or not {@link AAA#SYSTEM the system user} should used
     * if no current principal found.
     *
     * @param principal the principal to be checked
     * @param privilegeLevel the privilege level required
     * @param context the AAAContext instance
     * @return `true` if the current principal or system user has privilege required
     */
    public static boolean hasPrivilege(Principal principal, int privilegeLevel, AAAContext context) {
        AuthorizationService auth = context.getAuthorizationService();
        Privilege userPrivilege = auth.getPrivilege(principal, context);
        return null != userPrivilege && userPrivilege.getLevel() >= privilegeLevel;
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilege the privilege required
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Privilege privilege) {
        requirePrivilege(privilege, true, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeEnum the name of the privilege required
     * @see #hasPrivilege(Enum)
     */
    public static void requirePrivilege(Enum<?> privilegeEnum) {
        requirePrivilege(privilegeEnum.name(), true, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeName the name of the privilege required
     * @see #hasPrivilege(String)
     */
    public static void requirePrivilege(String privilegeName) {
        requirePrivilege(privilegeName, true, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeLevel the privilege level required
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(int privilegeLevel) {
        requirePrivilege(privilegeLevel, true);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilege the privilege required
     * @param allowSystem whether or not it shall use system user when current principal is not presented
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Privilege privilege, boolean allowSystem) {
        requirePrivilege(privilege, allowSystem, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeName the name of the privilege required
     * @param allowSystem whether or not it shall use system user when current principal is not presented
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(String privilegeName, boolean allowSystem) {
        requirePrivilege(privilegeName, allowSystem, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeEnum the enum that provides the name of the privilege required
     * @param allowSystem whether or not it shall use system user when current principal is not presented
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Enum<?> privilegeEnum, boolean allowSystem) {
        requirePrivilege(privilegeEnum.name(), allowSystem, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeLevel the privilege level required
     * @param allowSystem whether or not it shall use system user when current principal is not presented
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(int privilegeLevel, boolean allowSystem) {
        requirePrivilege(privilegeLevel, allowSystem, null);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilege the privilege required
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Privilege privilege, AAAContext context) {
        requirePrivilege(privilege, true, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeName the name of the privilege required
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(String privilegeName, AAAContext context) {
        requirePrivilege(privilegeName, true, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeEnum the name of the privilege required
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Enum<?> privilegeEnum, AAAContext context) {
        requirePrivilege(privilegeEnum.name(), true, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeLevel the privilege level required
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(int privilegeLevel, AAAContext context) {
        requirePrivilege(privilegeLevel, true, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilege the privilege required
     * @param allowSystem specify whether or not it shall use system user when current principal is not presented
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Privilege privilege, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        requirePrivilege(user, privilege, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeName the name of the privilege required
     * @param allowSystem specify whether or not it shall use system user when current principal is not presented
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(String privilegeName, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        requirePrivilege(user, privilegeName, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeEnum the name of the privilege required
     * @param allowSystem specify whether or not it shall use system user when current principal is not presented
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(Enum<?> privilegeEnum, boolean allowSystem, AAAContext context) {
        requirePrivilege(privilegeEnum.name(), allowSystem, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param privilegeLevel the privilege level required
     * @param allowSystem specify whether or not it shall use system user when current principal is not presented
     * @param context the AAAContext instance
     * @see #hasPrivilege(Privilege)
     */
    public static void requirePrivilege(int privilegeLevel, boolean allowSystem, AAAContext context) {
        context = ensureContext(context);
        Principal user = context.getPrincipal(allowSystem);
        requirePrivilege(user, privilegeLevel, context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param principal the principal to be authorized
     * @param privilege the privilege required
     * @param context the AAAContext
     */
    public static void requirePrivilege(Principal principal, Privilege privilege, AAAContext context) {
        requirePrivilege(principal, privilege.getLevel(), privilege.getName(), context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param principal the principal to be authorized
     * @param privilegeName the name of the privilege required
     * @param context the AAAContext
     */
    public static void requirePrivilege(Principal principal, String privilegeName, AAAContext context) {
        context = ensureContext(context);
        AAAPersistentService persistentService = context.getPersistentService();
        Privilege privilege = persistentService.findByName(privilegeName, Privilege.class);
        requirePrivilege(principal, privilege.getLevel(), privilege.getName(), context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param principal the principal to be authorized
     * @param privilegeEnum the enum that provides the privilege required
     * @param context the AAAContext
     */
    public static void requirePrivilege(Principal principal, Enum<?> privilegeEnum, AAAContext context) {
        requirePermission(privilegeEnum, privilegeEnum.name(), context);
    }

    /**
     * Authorize by privilege
     *
     * This method will audit the success or failure of the authorizing by calling
     * {@link Auditor#audit(Object, Principal, String, String, boolean, String)}, where
     * the auditor is retrieved from {@link AAAContext#getAuditor()}
     *
     * @param principal the principal to be authorized
     * @param privilegeLevel the privilege level required
     * @param context the AAAContext
     */
    public static void requirePrivilege(Principal principal, int privilegeLevel, AAAContext context) {
        context = ensureContext(context);
        AAAPersistentService persistentService = context.getPersistentService();
        Privilege privilege = persistentService.findPrivilege(privilegeLevel);
        requirePrivilege(principal, privilegeLevel, privilege.getName(), context);
    }

    private static void requirePrivilege(Principal principal, int privilegeLevel, String privilegeName, AAAContext context) {
        boolean authorized = hasPrivilege(principal, privilegeLevel, context);
        Auditor auditor = context.getAuditor();
        auditor.audit(null, principal, null, privilegeName, authorized, "");
        if (!authorized) {
            noAccess();
        }
    }

    public static boolean hasPermissionOrPrivilege(Permission permission, Privilege privilege) {
        return hasPrivilege(privilege) || hasPermission(permission);
    }

    public static boolean hasPermissionOrPrivilege(String permissionName, String privilegeName) {
        return hasPrivilege(privilegeName) || hasPermission(permissionName);
    }

    public static boolean hasPermissionOrPrivilege(String permissionName, int privilegeLevel) {
        return hasPrivilege(privilegeLevel) || hasPermission(permissionName);
    }

    public static boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        return hasPrivilege(privilegeEnum) || hasPermission(permissionEnum);
    }

    public static boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel) {
        return hasPrivilege(privilegeLevel) || hasPermission(permissionEnum);
    }

    public static boolean hasPermissionOrPrivilege(Permission permission, Privilege privilege, AAAContext context) {
        return hasPermissionOrPrivilege(null, permission, privilege, true, context);
    }

    public static boolean hasPermissionOrPrivilege(String permissionName, String privilegeName, AAAContext context) {
        return hasPermissionOrPrivilege(null, permissionName, privilegeName, true, context);
    }

    public static boolean hasPermissionOrPrivilege(String permissionName, int privilegeLevel, AAAContext context) {
        return hasPermissionOrPrivilege(null, permissionName, privilegeLevel, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum, AAAContext context) {
        return hasPermissionOrPrivilege(null, permissionEnum, privilegeEnum, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel, AAAContext context) {
        return hasPermissionOrPrivilege(null, permissionEnum, privilegeLevel, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilege, allowSystem, context) || hasPermission(null, permission, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(String permissionName, String privilegeName, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeName, allowSystem, context) || hasPermission(null, permissionName, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(String permissionName, int privilegeLevel, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeLevel, allowSystem, context) || hasPermission(null, permissionName, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeEnum, allowSystem, context) || hasPermission(null, permissionEnum, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeLevel, allowSystem, context) || hasPermission(null, permissionEnum, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Permission permission, Privilege privilege) {
        return hasPrivilege(privilege) || hasPermission(guardedResource, permission);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName) {
        return hasPrivilege(privilegeName) || hasPermission(guardedResource, permissionName);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel) {
        return hasPrivilege(privilegeLevel) || hasPermission(guardedResource, permissionName);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        return hasPrivilege(privilegeEnum) || hasPermission(guardedResource, permissionEnum);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel) {
        return hasPrivilege(privilegeLevel) || hasPermission(guardedResource, permissionEnum);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Permission permission, Privilege privilege, AAAContext context) {
        return hasPrivilege(privilege) || hasPermission(guardedResource, permission);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName, AAAContext context) {
        return hasPermissionOrPrivilege(guardedResource, permissionName, privilegeName, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel, AAAContext context) {
        return hasPermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum, AAAContext context) {
        return hasPermissionOrPrivilege(guardedResource, permissionEnum, privilegeEnum, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel, AAAContext context) {
        return hasPermissionOrPrivilege(guardedResource, permissionEnum, privilegeLevel, true, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Permission permission, Privilege privilege, boolean allowSystem) {
        return hasPrivilege(privilege, allowSystem) || hasPermission(guardedResource, permission, allowSystem);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName, boolean allowSystem) {
        return hasPrivilege(privilegeName, allowSystem) || hasPermission(guardedResource, permissionName, allowSystem);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel, boolean allowSystem) {
        return hasPrivilege(privilegeLevel, allowSystem) || hasPermission(guardedResource, permissionName, allowSystem);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem) {
        return hasPrivilege(privilegeEnum, allowSystem) || hasPermission(guardedResource, permissionEnum, allowSystem);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem) {
        return hasPrivilege(privilegeLevel, allowSystem) || hasPermission(guardedResource, permissionEnum, allowSystem);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilege, allowSystem, context) || hasPermission(guardedResource, permission, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeName, allowSystem, context) || hasPermission(guardedResource, permissionName, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeLevel, allowSystem, context) || hasPermission(guardedResource, permissionName, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeEnum, allowSystem, context) || hasPermission(guardedResource, permissionEnum, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem, AAAContext context) {
        return hasPrivilege(privilegeLevel, allowSystem, context) || hasPermission(guardedResource, permissionEnum, allowSystem, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Principal principal, Permission permission, Privilege privilege, AAAContext context) {
        return hasPrivilege(principal, privilege, context) || hasPermission(guardedResource, principal, permission, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Principal principal, String permissionName, String privilegeName, AAAContext context) {
        return hasPrivilege(principal, privilegeName, context) || hasPermission(guardedResource, principal, permissionName, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Principal principal, String permissionName, int privilegeLevel, AAAContext context) {
        return hasPrivilege(principal, privilegeLevel, context) || hasPermission(guardedResource, principal, permissionName, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Principal principal, Enum<?> permissionEnum, Enum<?> privilegeEnum, AAAContext context) {
        return hasPrivilege(principal, privilegeEnum, context) || hasPermission(guardedResource, principal, permissionEnum, context);
    }

    public static boolean hasPermissionOrPrivilege(Object guardedResource, Principal principal, Enum<?> permissionEnum, int privilegeLevel, AAAContext context) {
        return hasPrivilege(principal, privilegeLevel, context) || hasPermission(guardedResource, principal, permissionEnum, context);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege) {
        requirePermissionOrPrivilege(null, permission, privilege, true, null);
    }

    public static void requirePermissionOrPrivilege(String permissionName, String privilegeLevel) {
        requirePermissionOrPrivilege(null, permissionName, privilegeLevel, true, null);
    }

    public static void requirePermissionOrPrivilege(String permissionName, int privilegeLevel) {
        requirePermissionOrPrivilege(null, permissionName, privilegeLevel, true, null);
    }

    public static void requirePermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        requirePermissionOrPrivilege(null, permissionEnum.name(), privilegeEnum.name(), true, null);
    }

    public static void requirePermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel) {
        requirePermissionOrPrivilege(null, permissionEnum.name(), privilegeLevel, true, null);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, AAAContext context) {
        requirePermissionOrPrivilege(null, permission, privilege, true, context);
    }

    public static void requirePermissionOrPrivilege(String permissionName, String privilegeLevel, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionName, privilegeLevel, true, context);
    }

    public static void requirePermissionOrPrivilege(String permissionName, int privilegeLevel, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionName, privilegeLevel, true, context);
    }

    public static void requirePermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionEnum.name(), privilegeEnum.name(), true, context);
    }

    public static void requirePermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionEnum.name(), privilegeLevel, true, context);
    }

    public static void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem, AAAContext context) {
        requirePermissionOrPrivilege(null, permission, privilege, allowSystem, context);
    }

    public static void requirePermissionOrPrivilege(String permissionName, String privilegeLevel, boolean allowSystem, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionName, privilegeLevel, allowSystem, context);
    }

    public static void requirePermissionOrPrivilege(String permissionName, int privilegeLevel, boolean allowSystem, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionName, privilegeLevel, allowSystem, context);
    }

    public static void requirePermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionEnum.name(), privilegeEnum.name(), allowSystem, context);
    }

    public static void requirePermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem, AAAContext context) {
        requirePermissionOrPrivilege(null, permissionEnum.name(), privilegeLevel, allowSystem, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, Permission permission, Privilege privilege) {
        requirePermissionOrPrivilege(guardedResource, permission, privilege, true, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeLevel) {
        requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, true, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel) {
        requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, true, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        requirePermissionOrPrivilege(guardedResource, permissionEnum.name(), privilegeEnum.name(), true, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel) {
        requirePermissionOrPrivilege(guardedResource, permissionEnum.name(), privilegeLevel, true, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, Permission permission, Privilege privilege, AAAContext context) {
        requirePermissionOrPrivilege(guardedResource, permission, privilege, true, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeLevel, AAAContext context) {
        requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, true, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel, AAAContext context) {
        requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, true, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum, AAAContext context) {
        requirePermissionOrPrivilege(guardedResource, permissionEnum.name(), privilegeEnum.name(), true, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel, AAAContext context) {
        requirePermissionOrPrivilege(guardedResource, permissionEnum.name(), privilegeLevel, true, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Permission permission,
                                                    Privilege privilege,
                                                    boolean allowSystem
    ) {
        requirePermissionOrPrivilege(guardedResource, permission, privilege, allowSystem, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    String permissionName,
                                                    String privilegeLevel,
                                                    boolean allowSystem
    ) {
        requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, allowSystem, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    String permissionName,
                                                    int privilegeLevel,
                                                    boolean allowSystem
    ) {
        requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, allowSystem, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Enum<?> permissionEnum,
                                                    Enum<?> privilegeEnum,
                                                    boolean allowSystem
    ) {
        requirePermissionOrPrivilege(guardedResource, permissionEnum.name(), privilegeEnum.name(), allowSystem, null);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Enum<?> permissionEnum,
                                                    int privilegeLevel,
                                                    boolean allowSystem
    ) {
        requirePermissionOrPrivilege(guardedResource, permissionEnum.name(), privilegeLevel, allowSystem, null);
    }


    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Permission permission,
                                                    Privilege privilege,
                                                    boolean allowSystem,
                                                    AAAContext context
    ) {
        requirePermissionOrPrivilege(guardedResource, ensureContext(context).getPrincipal(allowSystem), permission, privilege, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    String permissionName,
                                                    String privilegeLevel,
                                                    boolean allowSystem,
                                                    AAAContext context
    ) {
        requirePermissionOrPrivilege(guardedResource, ensureContext(context).getPrincipal(allowSystem), permissionName, privilegeLevel, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    String permissionName,
                                                    int privilegeLevel,
                                                    boolean allowSystem,
                                                    AAAContext context
    ) {
        requirePermissionOrPrivilege(guardedResource, ensureContext(context()).getPrincipal(allowSystem), permissionName, privilegeLevel, context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Enum<?> permissionEnum,
                                                    Enum<?> privilegeEnum,
                                                    boolean allowSystem,
                                                    AAAContext context
    ) {
        requirePermissionOrPrivilege(guardedResource, context.getPrincipal(allowSystem), permissionEnum.name(), privilegeEnum.name(), context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Enum<?> permissionEnum,
                                                    int privilegeLevel,
                                                    boolean allowSystem,
                                                    AAAContext context
    ) {
        context = ensureContext(context);
        requirePermissionOrPrivilege(guardedResource, context.getPrincipal(allowSystem), permissionEnum.name(), privilegeLevel, context);
    }


    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Principal user,
                                                    Permission permission,
                                                    Privilege privilege,
                                                    AAAContext context
    ) {
        context = ensureContext(context);
        boolean authorized = hasPermissionOrPrivilege(guardedResource, user, permission, privilege, context);
        Auditor auditor = context.getAuditor();
        auditor.audit(guardedResource, user, permission.getName(), privilege.getName(), authorized, "");
        if (!authorized) {
            noAccess();
        }
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Principal user,
                                                    String permissionName,
                                                    String privilegeLevel,
                                                    AAAContext context
    ) {
        context = ensureContext(context);
        boolean authorized = hasPermissionOrPrivilege(guardedResource, user, permissionName, privilegeLevel, context);
        Auditor auditor = context.getAuditor();
        auditor.audit(guardedResource, user, permissionName, privilegeLevel, authorized, "");
        if (!authorized) {
            noAccess();
        }
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Principal principal,
                                                    String permissionName,
                                                    int privilegeLevel,
                                                    AAAContext context
    ) {
        context = ensureContext(context);
        boolean authorized = hasPermissionOrPrivilege(guardedResource, principal, permissionName, privilegeLevel, context);
        Auditor auditor = context.getAuditor();
        auditor.audit(guardedResource, principal, permissionName, context.getPersistentService().findPrivilege(privilegeLevel).getName(), authorized, "");
        if (!authorized) {
            noAccess();
        }
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Principal principal,
                                                    Enum<?> permissionEnum,
                                                    Enum<?> privilegeEnum,
                                                    AAAContext context
    ) {
        requirePermissionOrPrivilege(guardedResource, principal, permissionEnum.name(), privilegeEnum.name(), context);
    }

    public static void requirePermissionOrPrivilege(Object guardedResource,
                                                    Principal principal,
                                                    Enum<?> permissionEnum,
                                                    int privilegeLevel,
                                                    AAAContext context
    ) {
        requirePermissionOrPrivilege(guardedResource, principal, permissionEnum.name(), privilegeLevel, context);
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
     * @param userName  the username
     * @return a principal as described above
     */
    public static Principal createSuperUser(String userName) {
        return new SimplePrincipal.Builder(userName).grantPrivilege(new SimplePrivilege("root", SUPER_USER)).toPrincipal();
    }

    private static $.T2<Permission, Class> dpchKey(Permission p, Class c) {
        return $.T2(p, c);
    }

    private static AAAContext ensureContext(AAAContext context) {
        context = null == context ? context() : context;
        E.illegalStateIf(null == context, "cannot determine the AAA context");
        return context;
    }

    private static Principal ensurePrincipal(Principal principal, AAAContext context) {
        if (null == principal) {
            principal = context.getCurrentPrincipal();
            E.illegalArgumentIf(null == principal, "principal cannot be null");
        }
        return principal;
    }

    // Note this method will NOT ensure guarded resource is NOT null. It will only
    // try to get the context's guarded resource if the passed in guarded resource
    // is null.
    private static Object tryGetGuardedResource(Object guardedResource, AAAContext context) {
        return null != guardedResource ? guardedResource : context.getGuardedTarget();
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
        public boolean isAssociated(Object guardedResource, Principal principal) {
            return false;
        }
    };

    private static DynamicPermissionCheckHelper dynamicPermissionCheckHelper(Permission p, Class<?> c) {
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

    private static boolean checkSuperUser(Principal principal, AAAContext context) {
        return context.allowSuperUser() && context.isSuperUser(principal);
    }

    private static boolean isSystem(Principal principal) {
        return S.eq(AAA.SYSTEM, principal.getName());
    }

    private static void noAccess() {
        throw new NoAccessException();
    }


    /**
     * Get a {@link Privilege} by level
     * @param level the privilege level
     * @return a privilege object
     */
    public static Privilege findPrivilege(int level) {
        return ensureContext(null).findPrivilege(level);
    }

    /**
     * Returns all {@link Privilege privileges}
     * @return all privileges in an {@link Iterable}
     */
    public static Iterable<Privilege> allPrivileges() {
        return ensureContext(null).allPrivileges();
    }

    /**
     * Returns all {@link Permission permissions}
     * @return all permissions in an {@link Iterable}
     */
    public static Iterable<Permission> allPermissions() {
        return ensureContext(null).allPermissions();
    }

    /**
     * Returns all {@link Role roles}
     * @return all roles in an {@link Iterable}
     */
    public static Iterable<Role> allRoles() {
        return ensureContext(null).allRoles();
    }

    /**
     * Returns name of all {@link Privilege privileges}
     * @return all privilege names in an {@link Iterable}
     */
    public static Iterable<String> allPrivilegeNames() {
        return ensureContext(null).allPrivilegeNames();
    }

    /**
     * Returns name of all {@link Permission permissions}
     * @return all permission names in an {@link Iterable}
     */
    public static Iterable<String> allPermissionNames() {
        return ensureContext(null).allPermissionNames();
    }

    /**
     * Returns name of all {@link Role roles}
     * @return all role names in an {@link Iterable}
     */
    public static Iterable<String> allRoleNames() {
        return ensureContext(null).allRoleNames();
    }

}
