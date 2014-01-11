package org.osgl.aaa0;

import java.security.Principal;
import java.util.Collection;

/**
 * An <code>IAccount</code> is an abstraction of a principal request authorization
 *
 * Created by luog on 13/12/13.
 */
public interface IAccount extends AAAObject, IAAAEntity, Principal {
    /**
     * Return name of this account
     * <p/>
     * <p>Name should be unique through out the account database</p>
     *
     * @return
     */
    String getName();

    /**
     * Factory method to return an {@link IAccount} with given
     * name and password.
     * <p/>
     * <p>Implementation of this method will in the end go to
     * account storage to search for a matched records with given
     * name and password. The implementation might perform hash
     * on password before searching for the sake of security
     *
     * @param name     the given account name
     * @param password the given account password
     * @return an account instance if name and password match one record found
     *         in account storage or <code>null</code> if no matching found
     */
    IAccount authenticate(String name, String password);

    /**
     * Return a collection of roles assigned to this account
     *
     * @return all roles assigned to this account or an empty collection if no roles has been assigned
     *         to this account
     */
    Collection<IRole> getRoles();

    /**
     * Return this account's privilege
     *
     * @return this account's privilege or null if this account has not been assigned with a privilege
     */
    IPrivilege getPrivilege();

    /**
     * Determine whether this account has access to the given {@link IAuthorizeable} object.
     * <p/>
     * <p>The implementation shall check both {@link IPrivilege} and {@link IRole roles} and hence
     * {@link IRight rights} of this account against the required {@link IAuthorizeable#getRequiredPrivilege()
     * privilege} and {@link IAuthorizeable#getRequiredRight()} of the given object. Note right checking will
     * also apply the dynamic access checking
     *
     * @param object which needs certain authority to access
     * @return true if this account has access to the given object, false otherwise
     */
    boolean hasAccessTo(IAuthorizeable object);

    /**
     * Check whether the account has been granted a specified role
     * @param role
     * @return
     */
    boolean is(IRole role);

    boolean hasRole(IRole role);

    boolean hasRole(String role);

    void checkAccess(IAuthorizeable object) throws NoAccessException;

    boolean hasRight(String right, Object target);

    boolean hasRight(IRight right, Object target);

    void checkRight(String right, Object target) throws NoAccessException;

    void checkRight(IRight right, Object target) throws NoAccessException;

    boolean hasPrivilege(String privilege);

    boolean hasPrivilege(IPrivilege privilege);

    void checkPrivilege(String privilege) throws NoAccessException;

    void checkPrivilege(IPrivilege privilege) throws NoAccessException;


    /**
     * Set password to this account and return this account
     *
     * @param password
     * @return
     */
    IAccount setPassword(String password);

    /**
     * Assign roles to this account and return this account
     * @deprecated use grantRole instead
     *
     * @param roles
     * @return
     */
    IAccount assignRole(IRole... roles);

    /**
     * Grant roles to this account and return this account
     *
     * @param roles
     * @return
     */
    IAccount grantRole(IRole... roles);

    IAccount assignRole(Collection<IRole> roles);

    /**
     * Grant a role to this account and return this account
     *
     * @param roles
     * @return
     */
    IAccount grantRole(Collection<IRole> roles);

    /**
     * Revoke role from this account and return this account
     *
     * @param roles
     * @return
     */
    IAccount revokeRole(IRole... roles);

    IAccount revokeRole(Collection<IRole> roles);

    IAccount revokeAllRoles();

    /**
     * Assign privilege to this account and return this account
     *
     * @param privilege
     * @return
     */
    IAccount assignPrivilege(IPrivilege privilege);

    /**
     * Remvoke privilege from this account and return this account
     *
     * @return
     */
    IAccount revokePrivilege();

    /**
     * Factory method to return an account instance from given name.
     *
     * @param name
     * @return account instance or <code>null</code> if not found by the name
     */
    IAccount getByName(String name);

    /**
     * Factory method to return an account instance associated with current execution context. A
     * typical implementation might be get from a ThreadLocal package or system cache with session key etc.
     *
     * @return
     */
    IAccount getCurrent();

    /**
     * Factory method to set an account instance associated with current execution context.
     *
     * @param account
     */
    void setCurrent(IAccount account);

    /**
     * Factory method to return a system account instance
     *
     * @return
     */
    IAccount getSystemAccount();

    /**
     * Factory method to create an account instance
     *
     * @param name
     * @return
     */
    IAccount create(String name);

    /**
     * The recommended system account name. However it's subject to account implementation to determine
     * whether use it or not
     */
    public final String SYSTEM = "_system";
}
