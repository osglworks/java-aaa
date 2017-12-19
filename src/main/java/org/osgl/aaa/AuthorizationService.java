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

import java.util.Collection;

public interface AuthorizationService {

    /**
     * Returns the {@link org.osgl.aaa.Privilege} of a {@link org.osgl.aaa.Principal}
     *
     * @param principal the principal
     * @param context the AAA context
     * @return a privilege granted to the principal or {@code null} if no privilege found
     */
    Privilege getPrivilege(Principal principal, AAAContext context);

    /**
     * Returns all {@link org.osgl.aaa.Role roles} of a {@link org.osgl.aaa.Principal}
     *
     * @param principal the principal
     * @param context the AAA context
     * @return roles of the principal
     */
    Collection<Role> getRoles(Principal principal, AAAContext context);

    /**
     * Returns {@link org.osgl.aaa.Permission permissions} granted to the principal
     * directly
     *
     * @param principal the principal
     * @param context the AAA context
     * @return permissions granted to the principal
     */
    Collection<Permission> getPermissions(Principal principal, AAAContext context);

    /**
     * Returns {@link #getPermissions(Principal, AAAContext) permissions
     * granted to the principal direclty} plus all
     * {@link #getPermissions(Role, AAAContext) permissions} granted to all
     * {@link #getRoles(Principal, AAAContext) roles} granted to the principal
     *
     * @param principal the principal
     * @param context the AAA context
     * @return all permissions the principal implied
     */
    Collection<Permission> getAllPermissions(Principal principal, AAAContext context);

    /**
     * Returns all {@link org.osgl.aaa.Permission permissions} of a {@link org.osgl.aaa.Role} role
     * @param role the role
     * @param context the AAA context
     * @return permissions of the role
     */
    Collection<Permission> getPermissions(Role role, AAAContext context);

}
