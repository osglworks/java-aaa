package org.osgl.aaa0;

/**
 * Unlike {@link IRight right} which implements a fine grained authorization scheme,  
 * <code>IPrivilege</code> implements a coarse grained authorization.
 * 
 * <p>an {@link IAuthorizeable} could request for a <code>IPrivilege</code> in addition
 * to {@link IRight}, while right based authorization needs an exactly match, a privilege
 * based authorization could do a comparable match. I.e. if a {@link IAccount principal}
 * has a privilege that is superior (larger) than the required, then the permission is 
 * granted.
 * 
 * @author greenlaw110
 * @version 1.0 21/12/2010
 */
public interface IPrivilege extends Comparable<IPrivilege>, IAAAEntity, AAAObject {
   String getName();
   int getLevel();
   
   /**
    * Factory method to find privilege by name
    * @param name
    * @return
    * @deprecated use getByName instead
    */
   IPrivilege findByName(String name);

    /**
     * Factory method to find privilege by name
     * @param name
     * @return
     */
    IPrivilege getByName(String name);

    /**
    * Factory method to create a privilege instance of the implementation
    * 
    * @param name
    * @param level
    * @return
    */
   IPrivilege create(String name, int level);
}
