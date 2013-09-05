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
 * Container annotation for multiple {@link PolicyLock} annotations on a type.
 * It is a modeling error for a class to have both a {@link PolicyLocks} and a
 * {@link PolicyLock} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * This annotation holds a list {@link PolicyLock} annotations without imposing
 * any further constraint on the program's implementation.
 * 
 * <h3>Examples:</h3>
 * 
 * The code below declares two policy locks. The {@code OutsideDoorLock} is an
 * intrinsic lock and the {@code JailLock} is a
 * {@code java.util.concurrent.locks.Lock}.
 * 
 * <pre>
 * &#064;PolicyLocks( {
 *   &#064;PolicyLock("OutsideDoorLock is outsideDoorLock"),
 *   &#064;PolicyLock("JailLock is jailLock")
 * })
 * public class Station {
 * 
 *   private final Object outsideDoorLock = new Object();
 * 
 *   private final Lock jailLock = new ReentrantLock();
 * 
 *   void m1() {
 *     synchronized (outsideDoorLock) {
 *       // work with the door
 *     }
 *     jailLock.lock();
 *     try {
 *       // work with the jail
 *     } finally {
 *       jailLock.unlock();
 *     }
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
 *  * &#064;annotate PolicyLock("OutsideDoorLock is outsideDoorLock")
 *  * &#064;annotate PolicyLock("JailLock is jailLock")
 *  *&#047;
 * public class Station {
 *   ...
 * }
 * </pre>
 * 
 * @see PolicyLock
 */
@Documented
@Target(ElementType.TYPE)
public @interface PolicyLocks {
  /**
   * The {@link PolicyLock} annotations to apply to the class.
   */
  PolicyLock[] value();
}
