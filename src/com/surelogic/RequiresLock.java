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
 * Declares that the method or constructor to which this annotation applies
 * assumes that the caller holds the named locks. Analysis of the
 * method/constructor proceeds as if the named locks were held; call sites of
 * the method are scrutinized to determine if the precondition is satisfied.
 * <p>
 * The list of locks is allowed to be empty, in which case it means that the
 * method/constructor does not require any locks to be held by the caller.
 * <p>
 * Methods that override a method with a <code>&#64;RequiresLock</code>
 * annotation may remove locks from the set of required locks, but may not add
 * any locks to the set.  That is, the set of required locks is contravariant.
 * 
 * <h3>Semantics:</h3>
 * 
 * Each thread that invokes an annotated method or constructor must hold the
 * lock on each object denoted by the {@code lockSpecification}s in the
 * annotation.
 * 
 * <h3>Examples:</h3>
 * 
 * A locking policy, named {@code StateLock}, that indicates that synchronizing
 * on the field {@code stateLock} (which must be declared to be {@code final})
 * protects the two {@code long} fields use to represent the position of the
 * object. The {@link RequiresLock} annotation is used specify that
 * {@code StateLock} must be held when invoking the {@code setX} or {@code setY}
 * methods.
 * 
 * <pre>
 * &#064;Region(&quot;private AircraftState&quot;)
 * &#064;RegionLock(&quot;StateLock is stateLock protects AircraftState&quot;)
 * public class Aircraft {
 *   private final Object stateLock = new Object();
 * 
 *   &#064;InRegion(&quot;AircraftState&quot;)
 *   private long x, y;
 * 
 *   public void setPosition(long x, long y) {
 *     synchronized(stateLock)
 *       setX(x);
 *       setY(y);
 *     }
 *   }
 * 
 *   &#064;RequiresLock(&quot;StateLock&quot;)
 *   private void setX(long value) {
 *     x = value;
 *   }
 * 
 *   &#064;RequiresLock(&quot;StateLock&quot;)
 *   private void setY(long value) {
 *     y = value;
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
 *  * &#064;annotate Region(&quot;private AircraftState&quot;)
 *  * &#064;annotate RegionLock(&quot;StateLock is stateLock protects AircraftState&quot;)
 *  *&#047;
 * public class Aircraft {
 *   private final Object stateLock = new Object();
 * 
 *   /**
 *    * &#064;annotate InRegion(&quot;AircraftState&quot;)
 *    *&#047;
 *   private long x, y;
 * 
 *   public void setPosition(long x, long y) {
 *     synchronized(stateLock)
 *       setX(x);
 *       setY(y);
 *     }
 *   }
 * 
 *   /**
 *    * &#064;annotate RequiresLock(&quot;StateLock&quot;)
 *    *&#047;
 *   private void setX(long value) {
 *     x = value;
 *   }
 * 
 *   /**
 *    * &#064;annotate RequiresLock(&quot;StateLock&quot;)
 *    *&#047;
 *   private void setY(long value) {
 *     y = value;
 *   }
 * }
 * </pre>
 * 
 * @see RegionLock
 */
@Documented
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface RequiresLock {
  /**
   * A comma-separated list of zero or more lock names. The value of this
   * attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = lockSpecification *(&quot;,&quot; lockSpecification)
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
