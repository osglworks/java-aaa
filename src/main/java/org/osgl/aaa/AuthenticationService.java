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

/**
 * An `AuthenticationService` implementation shall provide the logic to
 * authenticate a {@link Principal} with the supplied `username` and
 * `password`
 */
public interface AuthenticationService {
    /**
     * Authenticate using username and password. Returns a {@link org.osgl.aaa.Principal} if
     * the username and password can be authenticated. or {@code null} if no principal found
     * matching the username and password combination.
     *
     * @param username the name of the principal
     * @param password the password
     * @return the principal matches the username and password
     */
    Principal authenticate(String username, String password);

    /**
     * Authenticate using username and password. Returns a {@link org.osgl.aaa.Principal} if
     * the username and password can be authenticated. or {@code null} if no principal found
     * matching the username and password combination.
     *
     * @param username the name of the principal
     * @param password the password
     * @return the princpal matches the username and password
     */
    Principal authenticate(String username, char[] password);
}
