package org.osgl.aaa;

/**
 * Define
 */
public interface AAAPersistentService {
    void save(AAAObject aaaObject);
    void remove(AAAObject aaaObject);
    <T extends AAAObject> T findByName(String name, Class<T> clz);
}
