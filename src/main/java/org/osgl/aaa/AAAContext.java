package org.osgl.aaa;

/**
 * Created by luog on 8/01/14.
 */
public interface AAAContext {

    /**
     * Returns the {@link org.osgl.aaa.AuthenticationService} implementation
     *
     * @return the authentication service implementation
     */
    AuthenticationService getAuthenticationService();

    /**
     * Returns the {@link org.osgl.aaa.AuthorizationService} implementation
     *
     * @return the authorization service implementation
     */
    AuthorizationService getAuthorizationService();

    /**
     * Returns the {@link org.osgl.aaa.AAAPersistentService} implementation
     * @return the AAA persistent service
     */
    AAAPersistentService getPersistentService();

    /**
     * Returns the system principal which is used by system to set up security context for
     * background tasks
     *
     * @return the system principal
     */
    Principal getSystemPrincipal();

    int getSuperUserLevel();

    boolean allowSuperUser();

    /**
     * Set the current principal to a thread local variable. If the principal specified
     * is null then the thread local variable should be removed
     * @param user
     */
    void setCurrentPrincipal(Principal user);

    /**
     * Returns a principal that initiate the current session
     * @return the current principal
     */
    Principal getCurrentPrincipal();

    /**
     * Store a guarded target object to a thread local variable. If target specified
     * is null, then the thread local variable should be removed
     *
     * @param target the guarded object
     * @return the previous guarded target
     */
    Object setGuardedTarget(Object target);


    /**
     * Get the guarded target object from the thread local variable
     *
     * @return the target
     */
    Object getGuardedTarget();

}
