package org.osgl.aaa.impl;

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

import org.osgl.aaa.AAAObject;
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
     * @param name the name
     * @param perms the permissions
     */
    public SimpleRole(String name, List<? extends Permission> perms) {
        super(name);
        E.NPE(perms);
        this.perms = C.list(perms);
    }

    @Override
    public List<Permission> getPermissions() {
        return C.list(perms);
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
        public Builder grantPermission(Permission perm) {
            E.NPE(perm);
            if (!perms.contains(perm)) perms.add(perm);
            return this;
        }
        public Builder revokePermission(final String permName) {
            E.NPE(permName);
            perms = perms.remove(AAAObject.F.nameMatcher(permName));
            return this;
        }
        public Builder revokeAllPermissions() {
            perms.clear();
            return this;
        }
        public Role toRole() {
            return new SimpleRole(name, perms);
        }
    }
}
