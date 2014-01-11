package org.osgl.aaa0;

/**
 * A <code>IRole</code> represent a group of @{link IRight} and can be granted to
 * an {@link IAccount}
 *
 * Created by luog on 13/12/13.
 */
public interface IRight extends AAAObject, IAAAEntity {
    /**
     * Return the name of this right. Two rights are considered equals if the name
     * of the two are equal
     * 
     * @return name of the right
     */
    String getName();
    
    /**
     * Whether this right is dynamic. An example of dynamic right is a customer has
     * right to access only orders owned by the customer, while order manager has 
     * right to access all orders which is a static right.
     * 
     * <p>In addition to normal {@link IAccount#hasAccessTo(IAuthorizeable) access check},
     * dynamic right also require the {@link IDynamicRightChecker#hasAccess() dynamic right 
     * check} to determine whether program should go or stop
     * 
     * @return true if this right is dynamic, false otherwise
     */
    boolean isDynamic();
    
    void setDynamic(boolean dynamic);
    
    /**
     * Factory method to find an instance with given name 
     * 
     * @param name
     * @return
     * @deprecated use getByName instead
     */
    IRight findByName(String name);
 
     /**
      * Factory method to find an instance with given name
      *
      * @param name
      * @return
      */
     IRight getByName(String name);
 
     /**
     * Factory method to create a right instance
     * @param name
     * @return
     */
    IRight create(String name);
}
