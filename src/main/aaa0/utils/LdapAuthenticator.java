package org.osgl.aaa0.utils;

import org.osgl.aaa0.IAuthenticator;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class LdapAuthenticator implements IAuthenticator {

	@Override
	public boolean authenticate(String name, String password) {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, Play.configuration.getProperty(ConfigConstants.AUTHENTICATE_PROVIDER_URL));
		env.put(Context.SECURITY_AUTHENTICATION, Play.configuration.getProperty(ConfigConstants.AUTHENTICATE_METHOD, "simple"));
		env.put(Context.SECURITY_PRINCIPAL, name);
		env.put(Context.SECURITY_CREDENTIALS, password);
		
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
		} catch(NamingException ne) {
			Logger.error(ne, "authenticate failed with name: %1$S", name);
			return false;
		} finally {
			if (null != ctx) {
				try {
					ctx.close();
				} catch (NamingException e) {
					// ignore it
				}
			}
		}
		
		return true;
	}

}
