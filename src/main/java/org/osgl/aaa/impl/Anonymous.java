package org.osgl.aaa.impl;

import org.osgl.aaa.Permission;
import org.osgl.aaa.Role;
import org.osgl.util.C;

public class Anonymous extends SimplePrincipal {

    /**
     * The pre-created anonymous instance with ID/name set to "anonymous"
     */
    public static final Anonymous INSTANCE = new Anonymous("anonymous");

    /**
     * Construct an anonymous with an identifier, e.g. ip address
     * @param id the identifier
     */
    public Anonymous(String id) {
        super(id, null, C.<Role>list(), C.<Permission>list());
    }

}
