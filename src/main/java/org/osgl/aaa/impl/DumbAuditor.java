package org.osgl.aaa.impl;

import org.osgl.aaa.Auditor;

import java.io.Serializable;

/**
 * Do nothing implementation of {@link org.osgl.aaa.Auditor}
 */
public class DumbAuditor implements Auditor, Serializable {

    public static final Auditor INSTANCE = new DumbAuditor();

    @Override
    public void audit(Object target, String principal, String permission, String privilege, boolean success, String message) {
        // do nothing
    }

    private Object readResolve() {
        return INSTANCE;
    }

}
