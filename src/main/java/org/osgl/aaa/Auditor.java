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
 * The implementation of this interface shall provide the underline infrastructure
 * to log the authorizing process
 */
public interface Auditor {
    /**
     * Log the authorizing process
     * @param target the guarded resource
     * @param principal who is trying to access the resource
     * @param permission the permission required to access the resource.
     *                   might be {@code null} if the {@code privilege}
     *                   is provided
     * @param privilege  the privilege requried to access the resource.
     *                   might be {@code null} if the {@code permission}
     *                   is provided
     * @param success    if the access is granted or not
     * @param message    any additional message
     */
    void audit(Object target, Principal principal, String permission, String privilege, boolean success, String message);

    /**
     * It is recommended that the object which can be a {@code target} of auditing
     * to implement this interface
     */
    public interface Target {
        /**
         * Returns a string that can identify the audit target in the log
         * @return a string that represent the audit target
         */
        String auditTag();
    }
}
