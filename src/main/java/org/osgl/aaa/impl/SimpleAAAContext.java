package org.osgl.aaa.impl;

import org.osgl.aaa.*;

/**
 * Created by luog on 13/01/14.
 */
public class SimpleAAAContext extends AAAContextBase {
    private boolean allowSystem = true;
    private Principal system;
    private int superUserLevel = AAA.SUPER_USER;
    private AuthenticationService authen;
    private AuthorizationService author;
    private AAAPersistentService db;

    public SimpleAAAContext(AuthenticationService authenticationService,
        AuthorizationService authorizationService,
        AAAPersistentService aaaPersistentService,
        int superUserLevel,
        Principal system,
        boolean allowSystem
    ) {
        authen = authenticationService;
        author = authorizationService;
        db = aaaPersistentService;
        this.superUserLevel = superUserLevel;
        this.allowSystem = allowSystem;
        if (allowSystem) {
            this.system = system == null ? SimplePrincipal.createSystemPrincipal(AAA.SYSTEM) : system;
        }
    }

    public SimpleAAAContext(AuthenticationService authenticationService,
            AuthorizationService authorizationService,
            AAAPersistentService aaaPersistentService
    ) {
        this(authenticationService, authorizationService, aaaPersistentService, AAA.SUPER_USER, null, true);
    }


    @Override
    public AuthenticationService getAuthenticationService() {
        return authen;
    }

    @Override
    public AuthorizationService getAuthorizationService() {
        return author;
    }

    @Override
    public AAAPersistentService getPersistentService() {
        return db;
    }

    @Override
    public boolean allowSuperUser() {
        return allowSystem;
    }

    @Override
    public Principal getSystemPrincipal() {
        return system;
    }

    @Override
    public int getSuperUserLevel() {
        return superUserLevel;
    }


}
