package org.osgl.aaa;

/**
 * Define a persistent storage for AAAObject
 */
public interface AAAPersistentService {

    /**
     * Save the AAAObject to persistent storage
     *
     * @param aaaObject the object
     */
    void save(AAAObject aaaObject);

    /**
     * Remove the AAAObject from persistent storage
     * @param aaaObject the object
     */
    void remove(AAAObject aaaObject);

    /**
     * Remove all AAAObject from persistent store
     * @param clz the class of the object to be removed
     * @param <T> the generic type of the object to be removed
     */
    <T extends AAAObject> void removeAll(Class<T> clz);

    /**
     * Get the AAAObject by name and type. Where type should be one of
     * <ul>
     * <li>{@link org.osgl.aaa.Permission Permission.class}</li>
     * <li>{@link org.osgl.aaa.Privilege Privilege.class}</li>
     * <li>{@link org.osgl.aaa.Role Role.class}</li>
     * <li>{@link org.osgl.aaa.Principal Principal.class}</li>
     * </ul>
     *
     * @param name the object name
     * @param clz the class of the object
     * @param <T> the generic type of the object
     * @return the AAAObject instance
     */
    <T extends AAAObject> T findByName(String name, Class<T> clz);

    /**
     * Get a {@link Privilege} by level
     * @param level the privilege level
     * @return a privilege object
     */
    Privilege findPrivilege(int level);
}
