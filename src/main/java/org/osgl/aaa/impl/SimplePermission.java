package org.osgl.aaa.impl;

import org.osgl.aaa.AAAObject;
import org.osgl.aaa.Permission;
import org.osgl.aaa.Role;
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
        return C.set(implied);
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
