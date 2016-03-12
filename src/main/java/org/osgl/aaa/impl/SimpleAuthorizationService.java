package org.osgl.aaa.impl;

import org.osgl.aaa.*;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;
import org.osgl.util.C;

import java.util.Collection;
import java.util.Set;

/**
 * A simple authorization service implementation
 */
public class SimpleAuthorizationService implements AuthorizationService {

    private static Logger logger = LogManager.get(SimpleAuthorizationService.class);

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
        logger.info("perms: %s", perms);
        C.list(getRoles(principal, context)).accept(Role.F.PERMISSION_GETTER.andThen(C.F.addAllTo(perms)));
        Set<Permission> retVal = C.newSet();
        for (Permission p : perms) {
            if (null == p) {
                logger.warn(new RuntimeException(), "Null permission found on principal %s", principal.getName());
                continue;
            }
            logger.info("collect implied permission on: %s", p.getName());
            collectPermission(retVal, p);
        }
        return retVal;
    }

    private void collectPermission(Set<Permission> set, Permission p) {
        Set<Permission> implied = p.implied();
        if (null == implied) {
            logger.warn(new RuntimeException(""), "Null implied found on permission: %s", p.getName());
        }
        for (Permission p0 : p.implied()) {
            collectPermission(set, p0);
        }
        set.add(p);
    }
}
