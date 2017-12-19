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

import org.osgl.aaa.AAAContext;
import org.osgl.aaa.Principal;

public abstract class AAAContextBase extends AAAContext {

    private Principal principal;
    private Object guarded;


    @Override
    public void setCurrentPrincipal(Principal user) {
        principal = user;
    }

    @Override
    public Principal getCurrentPrincipal() {
        return principal;
    }

    @Override
    public Object setGuardedTarget(Object obj) {
        Object prev = guarded;
        guarded = obj;
        return prev;
    }

    @Override
    public Object getGuardedTarget() {
        return guarded;
    }
}
