package org.osgl.aaa.impl;

import org.osgl._;
import org.osgl.aaa.AAAObject;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.Map;

/**
 * Provides a base AAAObject implementation
 */
public class AAAObjectBase implements AAAObject {
    private String name;

    private final Map<String, String> props = C.newMap();

    /**
     * This constructor is designed to be used by tools like ORM to deserialize the object from
     * a certain persistent storage
     */
    protected AAAObjectBase() {}

    /**
     * Construct the AAAObject by name
     *
     * @param name
     */
    protected AAAObjectBase(String name) {
        if (S.empty(name)) throw new IllegalArgumentException("name cannot be empty string");
        this.name = name;
    }

    /**
     * hash code of the object is calculated from the name and the class of the object.
     * <p>
     * properties are not considered here on the assumption that name is a unique identifier
     * for a certain type of AAAObject
     * </p>
     * @return
     */
    @Override
    public int hashCode() {
        return _.hc(name, getClass());
    }

    /**
     * The method simple returns the name of the object. Sub class can choose to
     * overwrite this method
     * @return the name of object
     */
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (_.eq(obj.getClass(), getClass())) {
            return _.eq(((AAAObject) obj).getName(), getName());
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setProperty(String key, String value) {
        props.put(key, value);
        return;
    }

    @Override
    public String getProperty(String key) {
        return props.get(key);
    }

}
