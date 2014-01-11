package org.osgl.aaa;

/**
 * Dynamic permission check helper could be implemented by application to
 * provide a fain grained authorization on instance level
 *
 * @author greenlaw110@gmail.com
 */
public interface DynamicPermissionCheckHelper<T> {

    /**
     * Check if a target resource is associated with a principal.
     *
     * @param target the target resource been guarded
     * @param user the principal who want to access the resource
     * @return {@code true} if the resource is associated with the user
     */
    boolean isAssociated(T target, Principal user);
}
