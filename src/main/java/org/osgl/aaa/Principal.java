package org.osgl.aaa;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.C;

import java.util.List;

/**
 * This interface represents the abstract notion of a principal, which
 * can be used to represent any entity, such as an individual, a
 * corporation, and a login id.
 */
public interface Principal extends AAAObject, java.security.Principal {

    /**
     * Returns the privilege granted to the principal
     *
     * @return the privilege
     */
    Privilege getPrivilege();

    /**
     * Returns roles granted to the principal
     *
     * @return a list of roles that are granted to the principal
     */
    List<Role> getRoles();

    /**
     * Returns permissions been granted to the principal
     *
     * @return a list of permissions been granted to the principal
     */
    List<Permission> getPermissions();

    /**
     * Returns {@link #getPermissions() permissions} granted to the principal
     * plus all permissions in all {@link #getRoles() roles} of the principal
     *
     * @return all permissions been granted to the principal
     */
    List<Permission> getAllPermissions();

    public static abstract class F extends AAAObject.F {

        public static $.F1<Principal, C.List<Role>> ROLE_GETTER = new $.F1<Principal, C.List<Role>>() {
            @Override
            public C.List<Role> apply(Principal principal) throws NotAppliedException, $.Break {
                return C.list(principal.getRoles());
            }
        };

        public static $.Visitor<Principal> roleVisitor(final $.Visitor<Role> visitor) {
            return new $.Visitor<Principal>() {
                @Override
                public void visit(Principal principal) throws $.Break {
                    C.list(principal.getRoles()).accept(visitor);
                }
            };
        }

        public static $.F1<Principal, C.List<Permission>> PERMISSION_GETTER = new $.F1<Principal, C.List<Permission>>() {
            @Override
            public C.List<Permission> apply(Principal principal) throws NotAppliedException, $.Break {
                return C.list(principal.getPermissions());
            }
        };

        public static $.Visitor<Principal> permissionVisitor(final $.Visitor<Permission> visitor) {
            return new $.Visitor<Principal>() {
                @Override
                public void visit(Principal principal) throws $.Break {
                    C.list(principal.getPermissions()).accept(visitor);
                }
            };
        }

        public static $.F1<Principal, C.List<Permission>> ALL_PERMISSION_GETTER = new $.F1<Principal, C.List<Permission>>() {
            @Override
            public C.List<Permission> apply(Principal principal) throws NotAppliedException, $.Break {
                return C.list(principal.getAllPermissions());
            }
        };

        public static $.Visitor<Principal> allPermissionVisitor(final $.Visitor<Permission> visitor) {
            return new $.Visitor<Principal>() {
                @Override
                public void visit(Principal principal) throws $.Break {
                    C.list(principal.getAllPermissions()).accept(visitor);
                }
            };
        }
    }
}
