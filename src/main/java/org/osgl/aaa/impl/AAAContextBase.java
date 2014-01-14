package org.osgl.aaa.impl;

import org.osgl.aaa.AAAContext;
import org.osgl.aaa.Principal;

/**
 * Created by luog on 13/01/14.
 */
public abstract class AAAContextBase implements AAAContext {

    private static final ThreadLocal<Principal> current = new ThreadLocal<Principal>();
    private static final ThreadLocal<Object> target = new ThreadLocal<Object>();


    @Override
    public void setCurrentPrincipal(Principal user) {
        if (null == user) current.remove();
        else current.set(user);
    }

    @Override
    public Principal getCurrentPrincipal() {
        return current.get();
    }

    @Override
    public Object setGuardedTarget(Object obj) {
        Object prev = target.get();
        if (null == obj) target.remove();
        else target.set(obj);
        return prev;
    }

    @Override
    public Object getGuardedTarget() {
        return target.get();
    }
}
