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

import java.util.List;

/**
 * Dynamic permission check helper could be implemented by application to
 * provide a fain grained authorization on instance level
 *
 * @author greenlaw110@gmail.com
 */
public interface DynamicPermissionCheckHelper<T> {

    /**
     * Returns a list of permissions that this dynamic permission check helper
     * could be used to check against the dynamic association.
     * <p>If the helper returns an empty list then it means it could be used
     * on any permission</p>
     * @return a list of permission this dynamic permission check helper effect on
     */
    List<? extends Permission> permissions();

    /**
     * Check if a target resource is associated with a principal.
     *
     * @param target the target resource been guarded
     * @param user the principal who want to access the resource
     * @return {@code true} if the resource is associated with the user
     */
    boolean isAssociated(T target, Principal user);
}
