package org.osgl.aaa.impl;

import org.osgl.aaa.AAAContext;
import org.osgl.aaa.Principal;

public abstract class AAAContextBase extends AAAContext {

    private Principal principal;
    private Object guarded;


    @Override
    public void setCurrentPrincipal(Principal user) {
        principal = user;
    }

    @Override
    public Principal getCurrentPrincipal() {
        return principal;
    }

    @Override
    public Object setGuardedTarget(Object obj) {
        Object prev = guarded;
        guarded = obj;
        return prev;
    }

    @Override
    public Object getGuardedTarget() {
        return guarded;
    }
}
