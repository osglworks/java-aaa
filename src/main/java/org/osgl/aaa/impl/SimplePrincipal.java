package org.osgl.aaa.impl;

import org.osgl.$;
import org.osgl.aaa.*;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.Collection;
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
    private List<? extends Role> roles = C.list();
    private List<? extends Permission> perms = C.list();

    /**
     * This constructor is designed to be used by tools like ORM to deserialize the object from
     * a certain persistent storage
     */
    public SimplePrincipal() {}

    /**
     * Construct a principal by name, privilege, list of roles and list of permissions
     *
     * @param name the name of the principal
     * @param privilege the privilege
     * @param roles a collection of roles
     * @param perms a collection of permissions
     */
    public SimplePrincipal(String name, Privilege privilege, Collection<? extends Role> roles, Collection<? extends Permission> perms) {
        super(name);
        this.privilege = privilege;

        List<Role> emptyRoles = C.list();
        this.roles = null == roles ? emptyRoles : C.list(roles);

        List<Permission> emptyPerms = C.list();
        this.perms = null == perms ? emptyPerms : C.list(perms);
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
        getRoles().accept(new $.Visitor<Role>() {
            @Override
            public void visit(Role role) throws $.Break {
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
        private Privilege privilege;
        private C.List<Role> roles = C.newList();
        private C.List<Permission> perms = C.newList();

        public Builder(Principal copy) {
            name = copy.getName();
            privilege = copy.getPrivilege();
            roles.addAll(copy.getRoles());
            perms.addAll(copy.getPermissions());
        }

        public Builder(String name) {
            E.illegalArgumentIf(S.blank(name));
            this.name = name;
        }

        public Builder grantPrivilege(Privilege p) {
            this.privilege = p;
            return this;
        }

        public Builder revokePrivilege() {
            this.privilege = null;
            return this;
        }

        public Builder grantRole(Role role) {
            roles.add(role);
            return this;
        }

        public Builder revokeRole(final String roleName) {
            roles = roles.remove(AAAObject.F.nameMatcher(roleName));
            return this;
        }

        public Builder revokeAllRoles() {
            roles.clear();
            return this;
        }

        public Builder grantPermission(Permission perm) {
            perms.add(perm);
            return this;
        }

        public Builder revokePermission(final String permName) {
            perms = perms.remove(AAAObject.F.nameMatcher(permName));
            return this;
        }

        public Builder revokeAllPermissions() {
            perms.clear();
            return this;
        }

        public SimplePrincipal toPrincipal() {
            return new SimplePrincipal(name, privilege, roles, perms);
        }
    }
}
