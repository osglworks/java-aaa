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
                return C.list(allPermissionsOf(principal));
            }
        };

        public static $.Visitor<Principal> allPermissionVisitor(final $.Visitor<Permission> visitor) {
            return new $.Visitor<Principal>() {
                @Override
                public void visit(Principal principal) throws $.Break {
                    C.list(allPermissionsOf(principal)).accept(visitor);
                }
            };
        }

        public static List<Permission> allPermissionsOf(Principal principal) {
            final C.Set<Permission> set = C.newSet(principal.getPermissions());
            for (Role role : principal.getRoles()) {
                set.addAll(role.getPermissions());
            }
            return C.list(set);
        }
    }
}
