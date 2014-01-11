package org.osgl.aaa0;

import java.util.Collection;

/**
 * Created by luog on 13/12/13.
 */
public interface IRole extends AAAObject, IAAAEntity {
    String getName();

    Collection<IRight> getRights();

    /**
     * Add a right to the role and return this role
     * @param right
     * @return
     */
    IRole addRight(IRight right);

    IRole addRights(IRight... rights);

    IRole addRights(Collection<IRight> rights);

    /**
     * Remove a right from this role and return this role
     * @param right
     * @return
     */
    IRole removeRight(IRight right);

    IRole removeRights(IRight... rights);

    IRole removeRights(Collection<IRight> rights);


     /**
      * Factory method to return a role instance from given name.
      * @param name
      * @return role instance or <code>null</code> if not found by the name
      */
     IRole getByName(String name);

     /**
     * Factory method to create a right instance
     * @param name
     * @return
     */
    IRole create(String name);
}
