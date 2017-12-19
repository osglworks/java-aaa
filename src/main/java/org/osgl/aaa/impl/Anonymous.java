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

import org.osgl.aaa.Permission;
import org.osgl.aaa.Role;
import org.osgl.util.C;

public class Anonymous extends SimplePrincipal {

    /**
     * The pre-created anonymous instance with ID/name set to "anonymous"
     */
    public static final Anonymous INSTANCE = new Anonymous("anonymous");

    /**
     * Construct an anonymous with an identifier, e.g. ip address
     * @param id the identifier
     */
    public Anonymous(String id) {
        super(id, null, C.<Role>list(), C.<Permission>list());
    }

}
