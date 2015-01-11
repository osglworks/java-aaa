package org.osgl.aaa.impl;

import org.osgl.aaa.Permission;
import org.osgl.aaa.Role;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.List;

/**
 * A simple and immutable {@link org.osgl.aaa.Privilege} implementation.
 * <p>
 * This implementation use internal data structure to store the permissions
 * granted to the role. Sub class might choose to rely on
 * {@link org.osgl.aaa.AuthorizationService#getPermissions(org.osgl.aaa.Role, org.osgl.aaa.AAAContext)}
 * to get permissions associated to the role
 * </p>
 */
public class SimpleRole extends AAAObjectBase implements Role {

    /**
     * This constructor is designed to be used by tools like ORM to deserialize the object from
     * a certain persistent storage
     */
    protected SimpleRole() {
    }

    private List<? extends Permission> perms;

    /**
     * Construct a role by name and given list of permissions
     *
     * @param name
     * @param perms
     */
    public SimpleRole(String name, List<? extends Permission> perms) {
        super(name);
        E.NPE(perms);
        this.perms = C.list(perms);
    }

    @Override
    public List<Permission> getPermissions() {
        return (List<Permission>)C.list(perms);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return perms.contains(permission);
    }

    public static class Builder {
        protected String name;
        protected C.List<Permission> perms = C.newList();
        public Builder(String name) {
            E.illegalArgumentIf(S.blank(name));
            this.name = name;
        }
        public Builder addPermission(SimplePermission perm) {
            E.NPE(perm);
            if (!perms.contains(perm)) perms.add(perm);
            return this;
        }
        public Role toRole() {
            return new SimpleRole(name, perms);
        }
    }
}
