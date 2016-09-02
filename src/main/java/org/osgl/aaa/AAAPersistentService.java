package org.osgl.aaa;

/**
 * Define a persistent storage for AAAObject
 */
public interface AAAPersistentService {

    /**
     * Save the AAAObject to persistent storage
     *
     * @param aaaObject
     */
    void save(AAAObject aaaObject);

    /**
     * Remove the AAAObject from persistent storage
     * @param aaaObject
     */
    void remove(AAAObject aaaObject);

    /**
     * Remove all AAAObject from persistent store
     * @param clz
     * @param <T>
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
     * @param name
     * @param clz
     * @param <T>
     * @return the AAAObject instance
     */
    <T extends AAAObject> T findByName(String name, Class<T> clz);

    /**
     * Get a {@link Privilege} by level
     * @param level
     * @return a privilege object
     */
    Privilege findPrivilege(int level);
}
