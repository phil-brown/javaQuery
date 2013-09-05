/*
 * Copyright (c) 2011 SureLogic, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.surelogic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Container annotation for multiple {@link RegionLock} annotations. It is a
 * modeling error for a class to have both a {@link RegionLocks} and a
 * {@link RegionLock} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * This annotation holds a list {@link RegionLock} annotations without imposing
 * any further constraint on the program's implementation.
 * 
 * <h3>Examples:</h3>
 * 
 * Declaring two lock policies on the same class (assuming the two named regions
 * have been previously declared).
 * 
 * <pre>
 * &#064;RegionLocks({
 *   &#064;RegionLock(&quot;SetLock is this protects SetRegion&quot;),
 *   &#064;RegionLock(&quot;ObserverLock is observerLock protects ObserverRegion&quot;)
 * })
 * class Example { ... }
 * </pre>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation is not supported in Javadoc because Javadoc supports multiple
 * <code>&#064;annotate</code> tags of the same type (see the example below).
 * Javadoc annotation can be useful for Java 1.4 code which does not include
 * language support for annotations.
 * 
 * <pre>
 * /**
 *  * @annotate RegionLock(&quot;SetLock is this protects SetRegion&quot;)
 *  * @annotate RegionLock(&quot;ObserverLock is observerLock protects ObserverRegion&quot;)
 *  &#42;/
 * class Example { ... }
 * </pre>
 * 
 * @see RegionLock
 */
@Documented
@Target(ElementType.TYPE)
public @interface RegionLocks {
  /**
   * The {@link RegionLock} annotations to apply to the class.
   */
  RegionLock[] value();
}
