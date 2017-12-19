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

/**
 * An {@code Guarded} encapsulates the permission required and the guarded resorce
 *
 * @author greenlaw110@gmail.com
 */
interface Guarded {

    /**
     * Return the {@link Permission} required to access this object
     *
     * @return the permission required
     */
    Permission getPermission();

    /**
     * Return target object that is guarded by the authorization
     * Only used when required right is dynamic as it needs to
     * be checked if the object belongs to the principal
     *
     * @return the target object
     */
    Object getTarget();

    /**
     * Name space for default implementation and factory methods
     */
    enum Factory {
        ;
        private static class Base implements Guarded {
            Permission perm;
            Object tgt;

            Base setPermission(Permission p) {
                perm = $.notNull(p);
                return this;
            }

            Base setTarget(Object o) {
                tgt = $.notNull(o);
                return this;
            }

            @Override
            public Permission getPermission() {
                return perm;
            }

            @Override
            public Object getTarget() {
                return tgt;
            }
        }

        private static Base create() {
            return new Base();
        }

        public static Guarded byPermission(Permission p, Object t) {
            return create().setPermission(p).setTarget(t);
        }

    }

}
