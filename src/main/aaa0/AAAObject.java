package org.osgl.aaa0;

/**
 * Created by luog on 13/12/13.
 */
public interface AAAObject {

    /**
     * Set property value to the {@code AAAObject}.
     *
     * @param key the property key
     * @param value the property value
     * @return the {@code AAAObject} instance to facilitate fluent method call
     */
    AAAObject setProperty(String key, String value);

    /**
     * Return property value of the {@code AAAObject}
     *
     * @param key the property key
     * @return the property value
     */
    String getProperty(String key);
}
