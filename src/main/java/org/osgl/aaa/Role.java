package org.osgl.aaa;

/*-
 * #%L
 * Java AAA Service
 * %%
 * Copyright (C) 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
     * @param permission permission
     * @return `true` if the role has the permission or `false` otherwise
     */
    boolean hasPermission(Permission permission);

    public static abstract class F extends AAAObject.F {

        public static $.F1<Role, C.List<Permission>> PERMISSION_GETTER = new $.F1<Role, C.List<Permission>>() {
            @Override
            public C.List<Permission> apply(Role role) throws NotAppliedException, $.Break {
                return C.list(role.getPermissions());
            }
        };

        public static $.Visitor<Role> permissionVisitor(final $.Visitor<Permission> visitor) {
            return new $.Visitor<Role>() {
                @Override
                public void visit(Role role) throws $.Break {
                    C.list(role.getPermissions()).accept(visitor);
                }
            };
        }
    }
}
