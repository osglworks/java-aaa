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

import org.osgl.aaa.Privilege;

/**
 * A simple and immutable {@link org.osgl.aaa.Privilege} implementation
 */
public class SimplePrivilege extends AAAObjectBase implements Privilege {
    private int level;

    // for ORM
    protected SimplePrivilege() {
    }

    public SimplePrivilege(String name, int level) {
        super(name);
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int compareTo(Privilege b) {
        int myLevel = this.getLevel();
        int yourLevel = b.getLevel();
        return (myLevel < yourLevel ? -1 : (myLevel == yourLevel ? 0 : 1));
    }
}
