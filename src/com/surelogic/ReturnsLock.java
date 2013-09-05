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
 * Declares that the object returned by the method to which this annotation is
 * applied is the named lock. This allows the representation of the lock to be
 * concealed from clients of the object, but still makes the lock accessible to
 * them. It is a modeling error if the return type of the annotated method is a
 * primitive type.
 * <p>
 * Methods that override a method annotated with
 * <code>&#064;ReturnsLock("</code><i>l</i><code>")</code> must also be
 * explicitly annotated <code>&#064;ReturnsLock("</code><i>l</i><code>")</code>.
 * It is a modeling error if they are not.
 * 
 * <h3>Semantics:</h3>
 * 
 * Constrains the return value of the annotated method to be non-{@code null}
 * and reference the lock object declared in the {@link RegionLock} or {@link PolicyLock} declaration
 * for the named lock.
 * 
 * <h3>Examples:</h3>
 * 
 * A parent class, {@code Thing}, returns a lock used to protected subclass
 * state.
 * 
 * <pre>
 * &#064;Region(&quot;protected ThingState&quot;)
 * &#064;RegionLock(&quot;ThingLock is thingLock protects ThingState&quot;)
 * class Thing {
 *   private final Object thingLock = new Object();
 * 
 *   &#064;ReturnsLock(&quot;ThingLock&quot;)
 *   protected Object getThingLock() {
 *     return thingLock;
 *   }
 *   ...
 * }
 * </pre>
 * 
 * The lock getter method can then be used in subclasses such as the
 * {@code Player} below.
 * 
 * <pre>
 * class Player extends Thing {
 *   &#064;InRegion(&quot;ThingState&quot;)
 *   private long x, y;
 * 
 *   public void setPosition(long x, long y) {
 *     synchronized (getThingLock()) {
 *       this.x = x;
 *       this.y = y;
 *     }
 *   }
 *   ...
 * }
 * </pre>
 * 
 * A lock getter method may return a policy lock:
 * 
 * <pre>
 * &#064;PolicyLock("InitLock is initLock")
 * public class System {
 *   private final Object initLock = new Object();
 *   ...
 *   &#064;ReturnsLock("InitLock")
 *   protected Object getInitLock() {
 *     return initLock;
 *   }
 * }
 * </pre>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation may placed in Javadoc, which can be useful for Java 1.4 code
 * which does not include language support for annotations, via the
 * <code>&#064;annotate</code> tag.
 * 
 * <pre>
 * /**
 *  * &#064;annotate Region(&quot;protected ThingState&quot;)
 *  * &#064;annotate RegionLock(&quot;ThingLock is thingLock protects ThingState&quot;)
 *  *&#047;
 * class Thing {
 *   private final Object thingLock = new Object();
 * 
 *   /**
 *    * &#064;annotate ReturnsLock(&quot;ThingLock&quot;)
 *    *&#047;
 *   protected Object getThingLock() {
 *     return thingLock;
 *   }
 *   ...
 * }
 * </pre>
 * 
 * @see RegionLock
 */
@Documented
@Target(ElementType.METHOD)
public @interface ReturnsLock {
  /**
   * The name of the lock returned by the annotated method. The value of this
   * attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = lockSpecification
   * 
   * lockSpecification = qualifiedLockSpecification / simpleLockSpecification
   * 
   * simpleLockSpecification = simpleLockName [&quot;.&quot; (&quot;readLock&quot; / &quot;writeLock&quot;)]
   * 
   * qualifiedLockSpecification = qualifiedLockName [&quot;.&quot; (&quot;readLock&quot; / &quot;writeLock&quot;)]
   * 
   * simpleLockName = IDENTIFIER  ; Lock from the receiver (same as &quot;this:IDENTIFIER&quot;)
   * 
   * qualifiedLockName = parameterLockName / typeQualifiedLockName / innerClassLockName
   * 
   * parameterLockName = simpleExpression &quot;:&quot; IDENTIFIER  ; Lock from a method/constructor parameter
   * 
   * simpleExpression = &quot;this&quot; / IDENTIFER  ; Receiver or parameter name
   * 
   * typeQualifiedLockName = typeExpression &quot;:&quot; IDENTIFIER  ; Static lock qualified by a class name
   * 
   * typeExpression = IDENTIFIER *(&quot;.&quot; IDENTIFIER)
   * 
   * innerClassLockName = namedType &quot;.&quot; &quot;this&quot; &quot;:&quot; IDENTIFIER ; Lock from an enclosing instance
   * 
   * namedType = IDENTIFIER *(&quot;.&quot; IDENTIFIER)
   * 
   * IDENTIFIER = Legal Java Identifier
   * </pre>
   */
  String value();
}
