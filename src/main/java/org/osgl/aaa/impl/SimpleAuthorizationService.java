package org.osgl.aaa.impl;

import org.osgl.aaa.*;
import org.osgl.util.C;

import java.util.Collection;

/**
 * A simple authorization service implementation
 */
public class SimpleAuthorizationService implements AuthorizationService {
    @Override
    public Privilege getPrivilege(Principal principal, AAAContext context) {
        return principal.getPrivilege();
    }

    @Override
    public Collection<Role> getRoles(Principal principal, AAAContext context) {
        return principal.getRoles();
    }

    @Override
    public Collection<Permission> getPermissions(Role role, AAAContext context) {
        return role.getPermissions();
    }

    @Override
    public Collection<Permission> getPermissions(Principal principal, AAAContext context) {
        return principal.getPermissions();
    }

    @Override
    public Collection<Permission> getAllPermissions(Principal principal, AAAContext context) {
        C.List<Permission> perms = C.newList(getPermissions(principal, context)).lazy();
        C.list(getRoles(principal, context)).accept(Role.F.PERMISSION_GETTER.andThen(C.F.addAllTo(perms)));
        return perms;
    }
}
