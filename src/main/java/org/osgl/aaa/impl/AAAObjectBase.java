package org.osgl.aaa.impl;

/*-
 * #%L
 * Java AAA Service
 * %%
 * Copyright (C) 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgl.$;
import org.osgl.aaa.AAAObject;
import org.osgl.aaa.Privilege;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.Map;
import java.util.Set;

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
     * @param name the name
     */
    protected AAAObjectBase(String name) {
        if (S.blank(name)) throw new IllegalArgumentException("name cannot be empty string");
        this.name = name;
    }

    /**
     * hash code of the object is calculated from the name and the class of the object.
     * <p>
     * properties are not considered here on the assumption that name is a unique identifier
     * for a certain type of AAAObject
     * </p>
     * @return hashcode
     */
    @Override
    public int hashCode() {
        if (this instanceof Privilege) {
            return ((Privilege) this).getLevel();
        }
        return $.hc(name, getClass());
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
        if ($.eq(obj.getClass(), getClass())) {
            if (this instanceof Privilege) {
                return $.eq(((Privilege) this).getLevel(),((Privilege)obj).getLevel());
            }
            return $.eq(((AAAObject) obj).getName(), getName());
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setProperty(String key, String value) {
        if (null == value) {
            props.remove(key);
        }
        props.put(key, value);
        return;
    }

    @Override
    public void unsetProperty(String key) {
        props.remove(key);
    }

    @Override
    public String getProperty(String key) {
        return props.get(key);
    }

    @Override
    public Set<String> propertyKeys() {
        return props.keySet();
    }
}
