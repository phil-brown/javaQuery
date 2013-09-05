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
 * Container annotation for multiple {@link Assume} annotations. It is a
 * modeling error for an entity to have both an {@code Assumes} and an
 * {@code Assume} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * This annotation holds a list {@link Assume} annotations without imposing any
 * further constraint on the program's implementation.
 * 
 * <h3>Examples:</h3>
 * 
 * Declaring two {@link Assume} annotations for the same declaration. The first
 * places a {@link Starts} annotation the no-argument constructor for the {@code
 * IllegalArgumentExcpetion} in {@code java.lang}. The second places a
 * {@link Starts} annotation on the constructor that takes a single argument of
 * type {@code long} for the {@code WaiterPreferenceSemaphore} class in any
 * package.
 * 
 * <pre>
 * package EDU.oswego.cs.dl.util.concurrent;
 * 
 * public class Rendezvous implements Barrier {
 * 
 *   &#064;Starts("nothing")
 *   &#064;Assumes( {
 *     &#064;Assume("Starts(nothing) for new() in IllegalArgumentException in java.lang"),
 *     &#064;Assume("Starts(nothing) for new(long) in WaiterPreferenceSemaphore")
 *   })
 *   public Rendezvous(int parties, RendezvousFunction function) {
 *     if (parties <= 0)
 *       throw new IllegalArgumentException();
 *     parties_ = parties;
 *     rendezvousFunction_ = function;
 *     entryGate_ = new WaiterPreferenceSemaphore(parties);
 *     slots_ = new Object[parties];
 *   }
 *   ...
 * }
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
 *  * &#064;annotate Assume(&quot;Starts(nothing) for new() in IllegalArgumentException in java.lang&quot;)
 *  * &#064;annotate Assume(&quot;Starts(nothing) for new(long) in WaiterPreferenceSemaphore&quot;)
 *  *&#047;
 * public Rendezvous(int parties, RendezvousFunction function) { ... }
 * </pre>
 * 
 * @see Assume
 */
@Documented
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface Assumes {
  /**
   * The {@link Assume} annotations to apply to the class.
   */
  Assume[] value();
}
