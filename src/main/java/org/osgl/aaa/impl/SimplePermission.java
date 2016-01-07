package org.osgl.aaa.impl;

import org.osgl.aaa.Permission;
import org.osgl.util.C;

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
}
