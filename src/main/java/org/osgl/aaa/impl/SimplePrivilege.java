package org.osgl.aaa.impl;

import org.osgl.aaa.Privilege;

/**
 * A simple and immutable {@link org.osgl.aaa.Privilege} implementation
 */
public class SimplePrivilege extends AAAObjectBase implements Privilege {
    private int level;

    // for ORM
    protected SimplePrivilege() {
    }

    public SimplePrivilege(String name, int level) {
        super(name);
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int compareTo(Privilege b) {
        int myLevel = this.getLevel();
        int yourLevel = b.getLevel();
        return (myLevel < yourLevel ? -1 : (myLevel == yourLevel ? 0 : 1));
    }
}
