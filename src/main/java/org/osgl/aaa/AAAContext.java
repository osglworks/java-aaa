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

import org.osgl.aaa.impl.Anonymous;

@SuppressWarnings("unused")
public abstract class AAAContext {

    /**
     * Returns the {@link org.osgl.aaa.AuthenticationService} implementation
     *
     * @return the authentication service implementation
     */
    public abstract AuthenticationService getAuthenticationService();

    /**
     * Returns the {@link org.osgl.aaa.AuthorizationService} implementation
     *
     * @return the authorization service implementation
     */
    public abstract AuthorizationService getAuthorizationService();

    /**
     * Returns the {@link org.osgl.aaa.AAAPersistentService} implementation
     * @return the AAA persistent service
     */
    public abstract AAAPersistentService getPersistentService();

    /**
     * Returns the {@link Auditor} implementation
     * @return the Auditor
     */
    public abstract Auditor getAuditor();

    /**
     * Returns the system principal which is used by system to set up security context for
     * background tasks
     *
     * @return the system principal
     */
    public abstract Principal getSystemPrincipal();

    /**
     * Returns an anonymous principal in case current principal is not provided and
     * the system principal is not allowed.
     * <p>This method returns {@link Anonymous#INSTANCE}. However sub class might
     * choose the override this method so it could return anonymous with different
     * ID, e.g. IP address</p>
     * @return an anonymous principal
     */
    protected Anonymous getAnonymousPrincipal() {
        return Anonymous.INSTANCE;
    }

    public abstract int getSuperUserLevel();

    public abstract boolean allowSuperUser();

    public abstract boolean isSuperUser(Principal principal);

    /**
     * Set the current principal to a thread local variable. If the principal specified
     * is null then the thread local variable should be removed
     * @param user the pricipal
     */
    public abstract void setCurrentPrincipal(Principal user);

    /**
     * Returns a principal that initiate the current session
     * @return the current principal
     */
    public abstract Principal getCurrentPrincipal();

    /**
     * Returns a principal. The logic is
     * <ol>
     *     <li>If {@link #getCurrentPrincipal() current principal} is set, then return it</li>
     *     <li>
     *         If {@code allowSystem} parameter is {@code true}, then tried to return
     *         {@link #getSystemPrincipal()} if it is not {@code null}, or else
     *     </li>
     *     <li>
     *         return {@link #getAnonymousPrincipal()}
     *     </li>
     * </ol>
     * @param allowSystem if {@link #getSystemPrincipal() system principal} is allowed
     * @return a principal as described above
     */
    public Principal getPrincipal(boolean allowSystem) {
        Principal p = getCurrentPrincipal();
        if (null != p) {
            return p;
        }
        if (allowSystem) {
            p = getSystemPrincipal();
        }
        return null != p ? p : getAnonymousPrincipal();
    }

    /**
     * Store a guarded target object to a thread local variable. If target specified
     * is null, then the thread local variable should be removed
     *
     * @param target the guarded object
     * @return the previous guarded target
     */
    public abstract Object setGuardedTarget(Object target);

    /**
     * Get the guarded target object set previously via
     * {@link #setGuardedTarget(Object)} call
     *
     * @return the target
     */
    public abstract Object getGuardedTarget();

    /**
     * Get a {@link Privilege} by level
     * @param level the privilege level
     * @return a privilege object
     */
    public Privilege findPrivilege(int level) {
        return getPersistentService().findPrivilege(level);
    }

    /**
     * Returns all {@link Privilege privileges}
     * @return all privileges in an {@link Iterable}
     */
    public Iterable<Privilege> allPrivileges() {
        return getPersistentService().allPrivileges();
    }

    /**
     * Returns all {@link Permission permissions}
     * @return all permissions in an {@link Iterable}
     */
    public Iterable<Permission> allPermissions() {
        return getPersistentService().allPermissions();
    }

    /**
     * Returns all {@link Role roles}
     * @return all roles in an {@link Iterable}
     */
    public Iterable<Role> allRoles() {
        return getPersistentService().allRoles();
    }

    /**
     * Returns name of all {@link Privilege privileges}
     * @return all privilege names in an {@link Iterable}
     */
    public Iterable<String> allPrivilegeNames() {
        return getPersistentService().allPrivilegeNames();
    }

    /**
     * Returns name of all {@link Permission permissions}
     * @return all permission names in an {@link Iterable}
     */
    public Iterable<String> allPermissionNames() {
        return getPersistentService().allPermissionNames();
    }

    /**
     * Returns name of all {@link Role roles}
     * @return all role names in an {@link Iterable}
     */
    public Iterable<String> allRoleNames() {
        return getPersistentService().allRoleNames();
    }

    public boolean hasPermission(Permission permission) throws NoAccessException {
        return AAA.hasPermission(null, permission, true, this);
    }

    public boolean hasPermission(String permissionName) throws NoAccessException {
        return AAA.hasPermission(null, permissionName, true, this);
    }

    public boolean hasPermission(Enum<?> permissionEnum) throws NoAccessException {
        return AAA.hasPermission(null, permissionEnum, true, this);
    }

    public boolean hasPermission(Permission permissionName, boolean allowSystem) throws NoAccessException {
        return AAA.hasPermission(null, permissionName, allowSystem, this);
    }

    public boolean hasPermission(String permissionName, boolean allowSystem) throws NoAccessException {
        return AAA.hasPermission(null, permissionName, allowSystem, this);
    }

    public boolean hasPermission(Enum<?> permissionEnum, boolean allowSystem) throws NoAccessException {
        return AAA.hasPermission(null, permissionEnum, allowSystem, this);
    }

    public boolean hasPermission(Object guardedResource, Permission permission) throws NoAccessException {
        return AAA.hasPermission(guardedResource, permission, true, this);
    }

    public boolean hasPermission(Object guardedResource, String permissionName) throws NoAccessException {
        return AAA.hasPermission(guardedResource, permissionName, true, this);
    }

    public boolean hasPermission(Object guardedResource, Enum<?> permissionEnum) throws NoAccessException {
        return AAA.hasPermission(guardedResource, permissionEnum, true, this);
    }

    public boolean hasPermission(Object guardedResource, Permission permission, boolean allowSystem) {
        return AAA.hasPermission(guardedResource, permission, allowSystem, this);
    }

    public boolean hasPermission(Object guardedResource, String permissionName, boolean allowSystem) {
        return AAA.hasPermission(guardedResource, permissionName, allowSystem, this);
    }

    public boolean hasPermission(Object guardedResource, Enum<?> permissionEnum, boolean allowSystem) {
        return AAA.hasPermission(guardedResource, permissionEnum, allowSystem, this);
    }

    public boolean hasPermission(Principal principal, Permission permission) throws NoAccessException {
        return AAA.hasPermission(null, principal, permission, this);
    }

    public boolean hasPermission(Principal principal, String permissionName) throws NoAccessException {
        return AAA.hasPermission(null, principal, permissionName, this);
    }

    public boolean hasPermission(Principal principal, Enum<?> permissionEnum) throws NoAccessException {
        return AAA.hasPermission(null, principal, permissionEnum, this);
    }

    public boolean hasPermission(Principal principal, Object guardedResource, String permissionName) throws NoAccessException {
        return AAA.hasPermission(guardedResource, principal, permissionName, this);
    }

    public boolean hasPermission(Principal principal, Object guardedResource, Enum<?> permissionEnum) throws NoAccessException {
        return AAA.hasPermission(guardedResource, principal, permissionEnum, this);
    }

    public void requirePermission(Permission permission) throws NoAccessException {
        AAA.requirePermission(null, permission, this);
    }

    public void requirePermission(String permissionName) throws NoAccessException {
        AAA.requirePermission(null, permissionName, this);
    }

    public void requirePermission(Enum<?> permissionEnum) throws NoAccessException {
        AAA.requirePermission(null, permissionEnum, this);
    }

    public void requirePermission(Permission permissionName, boolean allowSystem) throws NoAccessException {
        AAA.requirePermission(null, permissionName, allowSystem, this);
    }

    public void requirePermission(String permissionName, boolean allowSystem) throws NoAccessException {
        AAA.requirePermission(null, permissionName, allowSystem, this);
    }

    public void requirePermission(Enum<?> permissionEnum, boolean allowSystem) throws NoAccessException {
        AAA.requirePermission(null, permissionEnum, allowSystem, this);
    }

    public void requirePermission(Object guardedResource, Permission permission) throws NoAccessException {
        AAA.requirePermission(guardedResource, permission, this);
    }

    public void requirePermission(Object guardedResource, String permissionName) throws NoAccessException {
        AAA.requirePermission(guardedResource, permissionName, this);
    }

    public void requirePermission(Object guardedResource, Enum<?> permissionEnum) throws NoAccessException {
        AAA.requirePermission(guardedResource, permissionEnum, this);
    }

    public void requirePermission(Object guardedResource, Permission permission, boolean allowSystem) {
        AAA.requirePermission(guardedResource, permission, allowSystem, this);
    }

    public void requirePermission(Object guardedResource, String permissionName, boolean allowSystem) {
        AAA.requirePermission(guardedResource, permissionName, allowSystem, this);
    }

    public void requirePermission(Object guardedResource, Enum<?> permissionEnum, boolean allowSystem) {
        AAA.requirePermission(guardedResource, permissionEnum, allowSystem, this);
    }

    public void requirePermission(Principal principal, Permission permission) throws NoAccessException {
        AAA.requirePermission(null, principal, permission, this);
    }

    public void requirePermission(Principal principal, String permissionName) throws NoAccessException {
        AAA.requirePermission(null, principal, permissionName, this);
    }

    public void requirePermission(Principal principal, Enum<?> permissionEnum) throws NoAccessException {
        AAA.requirePermission(null, principal, permissionEnum, this);
    }

    public void requirePermission(Principal principal, Object guardedResource, String permissionName) throws NoAccessException {
        AAA.requirePermission(guardedResource, principal, permissionName, this);
    }

    public void requirePermission(Principal principal, Object guardedResource, Enum<?> permissionEnum) throws NoAccessException {
        AAA.requirePermission(guardedResource, principal, permissionEnum, this);
    }

    public boolean hasPrivilege(Privilege privilege) {
        return AAA.hasPrivilege(privilege, true, this);
    }

    public boolean hasPrivilege(String privilegeName) {
        return AAA.hasPrivilege(privilegeName, true, this);
    }

    public boolean hasPrivilege(Enum<?> privilegeEnum) {
        return AAA.hasPrivilege(privilegeEnum, true, this);
    }

    public boolean hasPrivilege(int privilegeLevel) {
        return AAA.hasPrivilege(privilegeLevel, true, this);
    }

    public boolean hasPrivilege(Principal principal, Privilege privilege) {
        return AAA.hasPrivilege(principal, privilege, this);
    }

    public boolean hasPrivilege(Principal principal, String privilegeName) {
        return AAA.hasPrivilege(principal, privilegeName, this);
    }

    public boolean hasPrivilege(Principal principal, Enum<?> privilegeEnum) {
        return AAA.hasPrivilege(principal, privilegeEnum, this);
    }

    public boolean hasPrivilege(Principal principal, int privilegeLevel) {
        return AAA.hasPrivilege(principal, privilegeLevel, this);
    }

    public boolean hasPrivilege(Privilege privilege, boolean allowSystem) {
        return AAA.hasPrivilege(privilege, allowSystem, this);
    }

    public boolean hasPrivilege(String privilegeName, boolean allowSystem) {
        return AAA.hasPrivilege(privilegeName, allowSystem, this);
    }

    public boolean hasPrivilege(Enum<?> privilegeEnum, boolean allowSystem) {
        return AAA.hasPrivilege(privilegeEnum, allowSystem, this);
    }

    public boolean hasPrivilege(int privilegeLevel, boolean allowSystem) {
        return AAA.hasPrivilege(privilegeLevel, allowSystem, this);
    }

    public void requirePrivilege(Privilege privilege) {
        AAA.requirePrivilege(privilege, this);
    }

    public void requirePrivilege(String privilegeName) {
        AAA.requirePrivilege(privilegeName, this);
    }

    public void requirePrivilege(Enum<?> privilegeEnum) {
        AAA.requirePrivilege(privilegeEnum, this);
    }

    public void requirePrivilege(int privilegeLevel) {
        AAA.requirePrivilege(privilegeLevel, this);
    }

    public void requirePrivilege(Principal principal, Privilege privilege) {
        AAA.requirePrivilege(principal, privilege, this);
    }

    public void requirePrivilege(Principal principal, String privilegeName) {
        AAA.requirePrivilege(principal, privilegeName, this);
    }

    public void requirePrivilege(Principal principal, Enum<?> privilegeEnum) {
        AAA.requirePrivilege(principal, privilegeEnum, this);
    }

    public void requirePrivilege(Principal principal, int privilegeLevel) {
        AAA.requirePrivilege(principal, privilegeLevel, this);
    }

    public void requirePrivilege(Privilege privilege, boolean allowSystem) {
        AAA.requirePrivilege(privilege, allowSystem, this);
    }

    public void requirePrivilege(String privilegeName, boolean allowSystem) {
        AAA.requirePrivilege(privilegeName, allowSystem, this);
    }

    public void requirePrivilege(Enum<?> privilegeEnum, boolean allowSystem) {
        AAA.requirePrivilege(privilegeEnum, allowSystem, this);
    }

    public void requirePrivilege(int privilegeLevel, boolean allowSystem) {
        AAA.requirePrivilege(privilegeLevel, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Permission permission, Privilege privilege) {
        return AAA.hasPermissionOrPrivilege(permission, privilege, this);
    }

    public boolean hasPermissionOrPrivilege(String permissionName, String privilegeName) {
        return AAA.hasPermissionOrPrivilege(permissionName, privilegeName, this);
    }

    public boolean hasPermissionOrPrivilege(String permissionName, int privilegeLevel) {
        return AAA.hasPermissionOrPrivilege(permissionName, privilegeLevel, this);
    }

    public boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        return AAA.hasPermissionOrPrivilege(permissionEnum, privilegeEnum, this);
    }

    public boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel) {
        return AAA.hasPermissionOrPrivilege(permissionEnum, privilegeLevel, this);
    }

    public boolean hasPermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(permission, privilege, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(String permissionName, String privilegeName, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(permissionName, privilegeName, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(String permissionName, int privilegeLevel, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(permissionName, privilegeLevel, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(permissionEnum, privilegeEnum, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(permissionEnum, privilegeLevel, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Object GuardedResource, Permission permission, Privilege privilege) {
        return AAA.hasPermissionOrPrivilege(GuardedResource, permission, privilege, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionName, privilegeName, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionEnum, privilegeEnum, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionEnum, privilegeLevel, this);
    }

    public boolean hasPermissionOrPrivilege(Object GuardedResource, Permission permission, Privilege privilege, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(GuardedResource, permission, privilege, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionName, privilegeName, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionEnum, privilegeEnum, allowSystem, this);
    }

    public boolean hasPermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem) {
        return AAA.hasPermissionOrPrivilege(guardedResource, permissionEnum, privilegeLevel, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Permission permission, Privilege privilege) {
        AAA.requirePermissionOrPrivilege(permission, privilege, this);
    }

    public void requirePermissionOrPrivilege(String permissionName, String privilegeName) {
        AAA.requirePermissionOrPrivilege(permissionName, privilegeName, this);
    }

    public void requirePermissionOrPrivilege(String permissionName, int privilegeLevel) {
        AAA.requirePermissionOrPrivilege(permissionName, privilegeLevel, this);
    }

    public void requirePermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        AAA.requirePermissionOrPrivilege(permissionEnum, privilegeEnum, this);
    }

    public void requirePermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel) {
        AAA.requirePermissionOrPrivilege(permissionEnum, privilegeLevel, this);
    }

    public void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permission, privilege, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(String permissionName, String privilegeName, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permissionName, privilegeName, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(String permissionName, int privilegeLevel, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permissionName, privilegeLevel, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permissionEnum, privilegeEnum, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permissionEnum, privilegeLevel, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object GuardedResource, Permission permission, Privilege privilege) {
        AAA.requirePermissionOrPrivilege(GuardedResource, permission, privilege, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionName, privilegeName, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionEnum, privilegeEnum, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionEnum, privilegeLevel, this);
    }

    public void requirePermissionOrPrivilege(Object GuardedResource, Permission permission, Privilege privilege, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(GuardedResource, permission, privilege, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, String permissionName, String privilegeName, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionName, privilegeName, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, String permissionName, int privilegeLevel, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionName, privilegeLevel, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, Enum<?> privilegeEnum, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionEnum, privilegeEnum, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object guardedResource, Enum<?> permissionEnum, int privilegeLevel, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(guardedResource, permissionEnum, privilegeLevel, allowSystem, this);
    }

}
