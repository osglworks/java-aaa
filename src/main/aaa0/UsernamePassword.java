package org.osgl.aaa0;

import org.osgl.util.E;

/**
 * Created by luog on 13/12/13.
 */
public class UsernamePassword implements ICredential {

    private String username;
    private String password;

    public UsernamePassword(String username, String password) {
        E.NPE(username, password);
        this.username = username;
        this.password = password;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
