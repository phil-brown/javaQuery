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
 * Declares a new policy lock for the class to which this annotation is applied.
 * This declaration creates a new named lock representing a particular lock
 * object. Unlike a lock declared with {@link RegionLock}, a policy lock is not
 * associated with any particular object state. Instead, a policy lock is used
 * to enforce a higher-level invariant that requires a section of code to
 * execute atomically with respect to some other section of code.
 * <p>
 * To declare more than one policy lock for a class use the {@link PolicyLocks}
 * annotation. It is a modeling error for a class to have both a
 * {@link PolicyLocks} and a {@link PolicyLock} annotation.
 * <p>
 * The named lock is a Java object. If the object's type implements
 * {@code java.util.concurrent.locks.Lock} then the lock object must be used
 * according to the protocol of the {@code Lock} interface. Otherwise, the
 * object must be used as a Java intrinsic lock, i.e., with {@code synchronized}
 * blocks. This is the only check performed, otherwise this annotation is
 * trusted, i.e., the higher-level invariant is <em>not verified</em>. Its use
 * is for documentation.
 * 
 * <h3>Semantics:</h3>
 * 
 * Declares a name for the given lock object, and conveys the intent that the
 * lock is used to enforce a higher-level invariant that requires a section of
 * code to execute atomically with respect to some other section of code. This
 * annotation does not, however, elicit the higher-level invariant from the
 * programmer.
 * 
 * <h3>Examples:</h3>
 * 
 * The code below declares a policy lock named {@code OutsideDoorLock} for a
 * Java intrinsic lock.
 * 
 * <pre>
 * &#064;PolicyLock("OutsideDoorLock is outsideDoorLock")
 * public class Station {
 * 
 *   private final Object outsideDoorLock = new Object();
 * 
 *   void m1() {
 *     synchronized (outsideDoorLock) {
 *       // work with the door
 *     }
 *   }
 *   ...
 * }
 * </pre>
 * 
 * The code below declares a policy lock named {@code JailLock} for a
 * {@code java.util.concurrent.locks.Lock}.
 * 
 * <pre>
 * &#064;PolicyLock("JailLock is jailLock")
 * public class Station {
 * 
 *   private final Lock jailLock = new ReentrantLock();
 * 
 *   void m1() {
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
 * The same lock object may be declared to be multiple locks (including to be
 * a {@link RegionLock}):
 * 
 * <pre>
 * &#064;Regions({
 *   &#064;Region("protected Color"),
 *   &#064;Region("protected Position"),
 *   &#064;Region("protected Label")
 * })
 * &#064;RegionLocks({
 *   &#064;RegionLock("L1 is this protects Color"),
 *   &#064;RegionLock("L2 is this protects Position"),
 *   &#064;RegionLock("L3 is lockField protects Label")
 * })
 * &#064;PolicyLocks({
 *   &#064;PolicyLock("P1 is this"),
 *   &#064;PolicyLock("L1 is lockField")
 * })
 * public class Sprite {
 *   protected final Object lockField = new Object();
 *   ...
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
 *  * &#064;annotate PolicyLock("OutsideDoorLock is outsideDoorLock")
 *  *&#047;
 * public class Station {
 *   ...
 * }
 * </pre>
 * 
 * @see PolicyLocks
 * @see RegionLock
 */
@Documented
@Target(ElementType.TYPE)
public @interface PolicyLock {
  /**
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = IDENTIFIER &quot;is&quot; lockExpression
   * 
   * lockExpression = simpleLockExpression / qualifiedLockExpression
   * 
   * simpleLockExpression = 
   *   &quot;class&quot; /              ; the Class object referenced by the &quot;class&quot; pseudo-field of the annotated class
   *   &quot;this&quot; /               ; the instance itself
   *   &quot;this&quot; &quot;.&quot; IDENTIFIER  ; the object referenced by the named field
   * 
   * qualifiedLockExpression = 
   *   namedType &quot;.&quot; &quot;CLASS&quot; /            ; the Class object referenced by the &quot;class&quot; pseudo-field of a named class
   *   namedType &quot;.&quot; &quot;THIS&quot; /             ; a named enclosing instance
   *   namedType &quot;.&quot; IDENTIFIER /         ; a named static field
   *   namedType &quot;.&quot; THIS &quot;.&quot; IDENTIFIER  ; a named field of an enclosing instance
   * 
   * namedType = IDENTIFIER *(&quot;.&quot; IDENTIFIER)
   * 
   * IDENTIFIER = Legal Java Identifier
   * </pre>
   */
  String value();
}
