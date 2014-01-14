package org.osgl.aaa.impl;

import org.osgl._;
import org.osgl.aaa.*;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.List;

/**
 * A simple and immutable {@link org.osgl.aaa.Principal} implementation.
 * <p>
 * This implementation use internal data structure to store the permissions and roles
 * granted to the principal. Sub class might choose to rely on
 * {@link org.osgl.aaa.AuthorizationService authorization service} to build up the acl
 * </p>
 */
public class SimplePrincipal extends AAAObjectBase implements Principal {

    private Privilege privilege;
    private List<? extends Role> roles;
    private List<? extends Permission> perms;

    /**
     * This constructor is designed to be used by tools like ORM to deserialize the object from
     * a certain persistent storage
     */
    protected SimplePrincipal() {}

    /**
     * Construct a principal by name, privilege, list of roles and list of permissions
     *
     * @param name
     * @param privilege
     * @param roles
     * @param perms
     */
    public SimplePrincipal(String name, Privilege privilege, List<? extends Role> roles, List<? extends Permission> perms) {
        super(name);
        this.privilege = privilege;

        List<Role> emptyRoles = C.list();
        this.roles = null == roles ? emptyRoles : roles;

        List<Permission> emptyPerms = C.list();
        this.perms = null == perms ? emptyPerms : perms;
    }

    @Override
    public Privilege getPrivilege() {
        return privilege;
    }

    @Override
    public C.List<Role> getRoles() {
        return (C.List<Role>)C.list(roles);
    }

    @Override
    public C.List<Permission> getPermissions() {
        return (C.List<Permission>)C.list(perms);
    }

    @Override
    public C.List<Permission> getAllPermissions() {
        final C.List<Permission> list = getPermissions().lazy();
        getRoles().accept(new _.Visitor<Role>() {
            @Override
            public void visit(Role role) throws _.Break {
                list.append(role.getPermissions());
            }
        });
        return list;
    }

    public static final Principal createSystemPrincipal(String name) {
        C.List<SimpleRole> roles = C.list();
        C.List<SimplePermission> perms = C.list();
        return new SimplePrincipal(name, null, roles, perms);
    }

    /**
     * The Builder can be used to build up a simple principal
     */
    public static class Builder {
        private String name;
        private SimplePrivilege privilege;
        private C.List<SimpleRole> roles = C.newList();
        private C.List<SimplePermission> perms = C.newList();

        public Builder(String name) {
            E.illegalArgumentIf(S.empty(name));
            this.name = name;
        }

        public Builder setPrivilege(SimplePrivilege p) {
            this.privilege = p;
            return this;
        }

        public Builder addRole(SimpleRole role) {
            roles.add(role);
            return this;
        }

        public Builder removeRole(final String roleName) {
            roles.remove(AAAObject.F.nameMatcher(roleName));
            return this;
        }

        public Builder addPermission(SimplePermission perm) {
            perms.add(perm);
            return this;
        }

        public Builder removePermission(final String permName) {
            perms.remove(AAAObject.F.nameMatcher(permName));
            return this;
        }

        public SimplePrincipal toPrincipal() {
            return new SimplePrincipal(name, privilege, roles, perms);
        }
    }
}
