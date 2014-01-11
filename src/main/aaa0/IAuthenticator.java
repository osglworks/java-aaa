package org.osgl.aaa0;

/**
 * Created by luog on 13/12/13.
 */
public interface IAuthenticator {
    boolean authenticate(ICredential credential);
}
