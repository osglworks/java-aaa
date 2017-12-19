package org.osgl.aaa;

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
import org.osgl.exception.NotAppliedException;

/**
 * Unlike {@link org.osgl.aaa.Permission permission} which implements a fine grained authorization scheme,
 * <code>Privilege</code> implements a coarse grained authorization.
 *
 * <p>an {@link Guarded} could request for a <code>Privilege</code> in addition
 * to {@link org.osgl.aaa.Permission}, while permission based authorization needs an exactly match
 * the name of the permission required and the one {@link org.osgl.aaa.Principal principal has been granted},
 * a privilege based authorization could do a comparable match. I.e. if a {@link org.osgl.aaa.Principal principal}
 * has a privilege with a level that is greater than the required privilege level, then the permission is
 * granted.
 *
 * @author greenlaw110
 * @version 1.0 21/12/2010
 */
public interface Privilege extends AAAObject, Comparable<Privilege> {

    /**
     * Returns the level of the privilege. Suppose a {@link org.osgl.aaa.Guarded guarded object} require
     * a {@link org.osgl.aaa.Privilege privilege_A} to access. And a {@link org.osgl.aaa.Principal user} has
     * been granted with privilege_B. The logic of authorization is to compare the level of privilege_A
     * to privilege_B. If privilege_A level is greater than that of privilege_B then the user has no access
     * to the object been guarded. if privilege_A level is equals to or less then that of privilege_B, then
     * the A has access to the object
     *
     * @return the level of the privilege
     */
    int getLevel();

    /**
     * Compare a privilege to another privilege. The result shall be align to compare the
     * {@link #getLevel() levels} of the two privileges
     *
     * @param b the privilege to be compared with this privilege
     * @return the comparison result of this privilege and privilege b
     */
    @Override
    int compareTo(Privilege b);

    public static abstract class F extends AAAObject.F {
        ;
        public static $.F1<Privilege, Integer> LEVEL_FETCHER = new $.F1<Privilege, Integer>() {
            @Override
            public Integer apply(Privilege p) throws NotAppliedException, $.Break {
                return p.getLevel();
            }
        };

        public static <T extends Privilege> $.F1<T, Integer> levelFetcher() {
            return ($.F1<T, Integer>)LEVEL_FETCHER;
        }
    }
}
