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
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;

import java.util.Collection;
import java.util.Set;

/**
 * An immutable permission implementation
 */
public class SimplePermission extends AAAObjectBase implements Permission {

    private boolean dynamic;
    private Set<Permission> implied = C.newSet();

    public SimplePermission() {
        super();
    }

    public SimplePermission(String name, boolean isDynamic) {
        this(name, null, isDynamic);
    }

    public SimplePermission(String name, Collection<? extends Permission> implied) {
        this(name, implied, false);
    }

    public SimplePermission(String name, Collection<? extends Permission> implied, boolean dynamic) {
        super(name);
        this.dynamic = dynamic;
        if (null != implied && !implied.isEmpty()) {
            this.implied.addAll(implied);
        }
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public Set<Permission> implied() {
        if (null == implied) {
            return C.Set();
        }
        Set<Permission> set = C.newSet();
        set.addAll(implied);
        set.add(this);
        return set;
    }

    // used for JSON serialization
    public Set<Permission> getImplied() {
        return implied;
    }

    public static class Builder {
        protected String name;
        protected boolean dynamic;
        protected C.List<Permission> implied = C.newList();
        public Builder(String name) {
            E.illegalArgumentIf(S.blank(name));
            this.name = name;
        }
        public Builder dynamic(boolean dynamic) {
            this.dynamic = dynamic;
            return this;
        }
        public Builder addImplied(Permission perm) {
            E.NPE(perm);
            if (!implied.contains(perm)) implied.add(perm);
            return this;
        }
        public Builder removeImplied(final String permName) {
            E.NPE(permName);
            implied = implied.remove(AAAObject.F.nameMatcher(permName));
            return this;
        }
        public Builder removeAllImplied() {
            implied.clear();
            return this;
        }
        public Permission toPermission() {
            return new SimplePermission(name, implied, dynamic);
        }
    }

}
