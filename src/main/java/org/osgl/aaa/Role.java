package org.osgl.aaa;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.C;

import java.util.List;

/**
 * Role can be used to organize a list of {@link Permission permissions} into a group.
 * A {@link org.osgl.aaa.AuthorizationService} can associate a role instead of permission
 * to a {@link org.osgl.aaa.Principal pricipal account}
 */
public interface Role extends AAAObject {

    /**
     * Returns a list of permissions associated with this role.
     *
     * <p>
     * <b>Note</b> the result of the method shall comply to the result of
     * {@link org.osgl.aaa.AuthorizationService#getPermissions(Role, AAAContext)}
     * It is up to the implementation to decide the dependency relationship
     * between the two
     * </p>
     *
     * @return the permission list
     */
    List<Permission> getPermissions();

    /**
     * Check if the role contains a permission. Calling this method shall be equals to calling
     * <code>
     * getPermissions().contains(permission)
     * </code>
     *
     * @param permission
     * @return
     */
    boolean hasPermission(Permission permission);

    public static abstract class F extends AAAObject.F {

        public static $.F1<Role, C.List<Permission>> PERMISSION_GETTER = new $.F1<Role, C.List<Permission>>() {
            @Override
            public C.List<Permission> apply(Role role) throws NotAppliedException, $.Break {
                return C.list(role.getPermissions());
            }
        };

        public static $.Visitor<Role> permissionVisitor(final $.Function<Permission, ?> visitor) {
            return new $.Visitor<Role>() {
                @Override
                public void visit(Role role) throws $.Break {
                    C.list(role.getPermissions()).accept(visitor);
                }
            };
        }
    }
}
