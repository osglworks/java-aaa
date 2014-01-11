package org.osgl.aaa0;

/**
 * An <code>IAuthorizeable</code> object needs certain {@link IRight} or {@link IPrivilege}
 * to access
 *
 * @author greenlaw110@gmail.com
 * @version 1.0 21/12/2010
 */
public interface IAuthorizeable {
    /**
     * Return required {@link IRight} to access this object
     *
     * @return right
     */
    IRight getRequiredRight();

    /**
     * Return requried {@link IPrivilege} to access this object
     *
     * @return privilege
     */
    IPrivilege getRequiredPrivilege();

    /**
     * Return target object to be checked.
     * Only used when required right is dynamic
     *
     * @return
     */
    Object getTargetResource();

    public abstract static class AuthorizeableBase implements IAuthorizeable {
        @Override
        public Object getTargetResource() {
            return null;
        }
    }
}
