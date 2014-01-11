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
     * Returns the system principal which is used by system to set up security context for
     * background tasks
     *
     * @return the system principal
     */
    Principal getSystemPrincipal();

    /**
     * Returns a principal that initiate the current session
     * @return the current principal
     */
    Principal getCurrentPrincipal();

    /**
     * Store a guarded target object to a thread local variable
     *
     * @param target the guarded object
     * @throws java.lang.NullPointerException if the target object is {@code null}
     */
    void setGuardedTarget(Object target) throws NullPointerException;

    /**
     * Get the guarded target object from the thread local variable
     *
     * @return the target
     */
    Object getGuardedTarget();

}
