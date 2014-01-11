package org.osgl.aaa.impl;

import org.osgl.aaa.Permission;

/**
 * An immutable permission implementation
 */
public class SimplePermission extends AAAObjectBase implements Permission {

    private boolean dynamic;

    /**
     * Construct an empty privilege object. This is for ORB tool use only
     */
    public SimplePermission() {
        super();
    }

    /**
     * Construct a privilege instance with name and level
     *
     * @param name
     */
    public SimplePermission(String name, boolean isDynamic) {
        super(name);
        dynamic = isDynamic;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }
}
