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

import java.lang.annotation.*;

/**
 * Indicates that a method invocation shall be logged
 * @author greenlaw110@gmail.com
 * @version 1.0 23/12/2010
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface RequireAuditing {
    /**
     * Indicates the message to be logged
     *
     * @return the auditing messsage
     */
    String value() default "";
    /**
     * Set the time of auditing log, usually it should be true
     * But in rare case when it needs to log after the method executed (e.g. constructor)
     * then it could be set to false;
     * @return `true` if it shall audit before authorization or `false` otherwise
     */
    boolean before() default true;
}
