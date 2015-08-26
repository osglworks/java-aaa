package org.osgl.aaa;

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
     * Returns the system principal which is used by system to set up security context for
     * background tasks
     *
     * @return the system principal
     */
    public abstract Principal getSystemPrincipal();

    public abstract int getSuperUserLevel();

    public abstract boolean allowSuperUser();

    public abstract boolean isSuperUser(Principal principal);

    /**
     * Set the current principal to a thread local variable. If the principal specified
     * is null then the thread local variable should be removed
     * @param user
     */
    public abstract void setCurrentPrincipal(Principal user);

    /**
     * Returns a principal that initiate the current session
     * @return the current principal
     */
    public abstract Principal getCurrentPrincipal();

    /**
     * Store a guarded target object to a thread local variable. If target specified
     * is null, then the thread local variable should be removed
     *
     * @param target the guarded object
     * @return the previous guarded target
     */
    public abstract Object setGuardedTarget(Object target);


    /**
     * Get the guarded target object from the thread local variable
     *
     * @return the target
     */
    public abstract Object getGuardedTarget();

    public void requirePermission(Object target, Permission perm) throws NoAccessException {
        AAA.requirePermission(target, perm, this);
    }

    public void requirePermission(Object target, String perm) throws NoAccessException {
        AAA.requirePermission(target, perm, this);
    }

    public void requirePermission(Object target, Permission permission, boolean allowSystem) {
        AAA.requirePermission(target, permission, allowSystem, this);
    }

    public void requirePermission(Object target, String permName, boolean allowSystem) {
        AAA.requirePermission(target, permName, allowSystem, this);
    }

    public boolean hasPrivilege(Privilege privilege, boolean allowSystem) {
        return AAA.hasPrivilege(privilege, allowSystem, this);
    }

    public boolean hasPrivilege(String privName, boolean allowSystem) {
        return AAA.hasPrivilege(privName, allowSystem, this);
    }

    public void requirePrivilege(Privilege privilege) {
        AAA.requirePrivilege(privilege, true, this);
    }

    public void requirePrivilege(String privName) {
        AAA.requirePrivilege(privName, this);
    }

    public void requirePrivilege(Privilege privilege, boolean allowSystem) {
        AAA.requirePrivilege(privilege, allowSystem, this);
    }

    public void requirePrivilege(String privName, boolean allowSystem) {
        AAA.requirePrivilege(privName, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(target, permission, privilege, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Object target, String permission, String privilege, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(target, permission, privilege, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(Permission permission, Privilege privilege) {
        AAA.requirePermissionOrPrivilege(permission, privilege, this);
    }

    public void requirePermissionOrPrivilege(String permission, String privilege) {
        AAA.requirePermissionOrPrivilege(permission, privilege, this);
    }

    public void requirePermissionOrPrivilege(Object target, Permission permission, Privilege privilege) {
        AAA.requirePermissionOrPrivilege(target, permission, privilege, this);
    }

    public void requirePermissionOrPrivilege(Object target, String permission, String privilege) {
        AAA.requirePermissionOrPrivilege(target, permission, privilege, this);
    }

    public void requirePermissionOrPrivilege(Permission permission, Privilege privilege, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permission, privilege, allowSystem, this);
    }

    public void requirePermissionOrPrivilege(String permission, String privilege, boolean allowSystem) {
        AAA.requirePermissionOrPrivilege(permission, privilege, allowSystem, this);
    }
}
