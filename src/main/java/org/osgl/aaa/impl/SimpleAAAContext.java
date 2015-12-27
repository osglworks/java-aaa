package org.osgl.aaa.impl;

import org.osgl.$;
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
    private Auditor auditor;

    public SimpleAAAContext(AuthenticationService authenticationService,
        AuthorizationService authorizationService,
        AAAPersistentService aaaPersistentService,
        Auditor auditorImpl,
        int superUserLevel,
        Principal system,
        boolean allowSystem
    ) {
        authen = $.NPE(authenticationService);
        author = $.NPE(authorizationService);
        db = $.NPE(aaaPersistentService);
        auditor = null == auditorImpl ? DumbAuditor.INSTANCE : auditorImpl;
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
        this(authenticationService, authorizationService, aaaPersistentService, DumbAuditor.INSTANCE, AAA.SUPER_USER, null, true);
    }

    public SimpleAAAContext(AuthenticationService authenticationService,
                            AuthorizationService authorizationService,
                            AAAPersistentService aaaPersistentService,
                            Auditor auditor
    ) {
        this(authenticationService, authorizationService, aaaPersistentService, auditor, AAA.SUPER_USER, null, true);
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
    public Auditor getAuditor() {
        return auditor;
    }

    @Override
    public boolean allowSuperUser() {
        return allowSystem;
    }

    @Override
    public boolean isSuperUser(Principal principal) {
        Privilege p = principal.getPrivilege();
        if (null == p) return false;
        return p.getLevel() >= getSuperUserLevel();
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
