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
 * Declares a new region lock for the class to which this annotation is applied.
 * This declaration creates a new named lock that associates a particular lock
 * object with a region of the class. The region may only be accessed when the
 * lock is held.
 * <p>
 * To declare more than one lock for a class use the {@link RegionLocks}
 * annotation. It is a modeling error for a class to have both a
 * {@link RegionLocks} and a {@link RegionLock} annotation.
 * <p>
 * The named lock is a Java object. If the object's type implements
 * {@code java.util.concurrent.locks.Lock} then the lock object must be used
 * according to the protocol of the {@code Lock} interface. Otherwise, the
 * object must be used as a Java intrinsic lock, i.e., with {@code synchronized}
 * blocks.
 * 
 * <h3>Semantics:</h3>
 * 
 * The program must be holding the lock denoted by the {@code lockExpression}
 * when the state specified by the {@code regionName} is read or written, unless
 * <ul>
 *   <li>The state specified by the {@code regionName} is {@code static} and the
 *   access occurs within the class initializer of the class that declares that
 *   state; or
 *   <li>The state specified by the {@code regionName} is non-{@code static} and
 *   the access occurs within a constructor of the class that declares that
 *   state or one of its subclasses, and the object created by the constructor
 *   is only accessed by the thread that invoked the constructor.
 * </ul>
 *
 * <p>
 * The locking policy
 * stated by a {@link RegionLock} annotation likely associated with a
 * higher-level invariant. This annotation does not, however, elicit the
 * higher-level invariant from the programmer.
 * 
 * <h3>Examples:</h3>
 * 
 * A locking policy, named {@code SimpleLock}, that indicates that synchronizing
 * on the instance protects the all of the instance's state.
 * 
 * <pre>
 * &#064;RegionLock(&quot;SimpleLock is this protects Instance&quot;)
 * class Simple { ... }
 * </pre>
 * 
 * A locking policy, named {@code ObserverLock}, that indicates that
 * synchronizing on the field {@code observerLock} (which must be declared to be
 * {@code final}) protects the contents of the set {@code observers}.
 * 
 * <pre>
 * &#064;Region(&quot;private ObserverRegion&quot;)
 * &#064;RegionLock(&quot;ObserverLock is observerLock protects ObserverRegion&quot;)
 * class Observer {
 *   private final Object observerLock = new Object();
 *   ...
 *   &#064;UniqueInRegion(&quot;ObserverRegion&quot;)
 *   private final Set&lt;Callback&gt; observers = new HashSet&lt;Callback&gt;();
 *   ...
 *   private void add(Callback c) {
 *     synchronized (observerLock) { observers.add(c); }
 *   }
 *   ...
 * }
 * </pre>
 * 
 * A locking policy, named {@code StateLock}, that indicates that locking on the
 * {@code java.util.concurrent.Lock} {@code stateLock} protects the two
 * {@code long} fields use to represent the position of the object.
 * 
 * <pre>
 * &#064;Region(&quot;private AircraftState&quot;)
 * &#064;RegionLock(&quot;StateLock is stateLock protects AircraftState&quot;)
 * public class Aircraft {
 *   private final Lock stateLock = new ReentrantLock();
 *   ...
 *   &#064;InRegion(&quot;AircraftState&quot;)
 *   private long x, y;
 *   ...
 *   public void setPosition(long x, long y) {
 *     stateLock.lock();
 *     try {
 *       this.x = x;
 *       this.y = y;
 *     } finally {
 *       stateLock.unlock();
 *     }
 *   }
 *   ...
 * }
 * </pre>
 * 
 * The same lock object may be declared to be multiple locks (including to be
 * a {@link PolicyLock}):
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
 * <b>Constructor annotation to support locking policies:</b> To support the
 * {@link RegionLock} annotation, a {@link Unique} or {@link Borrowed}
 * annotation is needed on each constructor to assure that the object being
 * constructed is confined to the thread that invoked {@code new}. A second less
 * common approach, using effects, is described below.
 * <p>
 * Annotating <code>&#64;Unique("return")</code> on a constructor is defined to
 * be equivalent to annotating <code>&#64;Borrowed("this")</code>. Either of
 * these annotations indicate that the object being constructed is not aliased
 * during construction, which implies that the reference "returned" by the
 * {@code new} expression that invokes the constructor is unique. Which
 * annotation is preferred, <code>&#64;Unique("return")</code> or
 * <code>&#64;Borrowed("this")</code>, is a matter of programmer preference.
 * 
 * <pre>
 * &#064;RegionLock(&quot;Lock is this protects Instance&quot;)
 * public class Example {
 * 
 *   int x = 1;
 *   int y;
 * 
 *   &#064;Unique(&quot;return&quot;)
 *   public Example(int y) {
 *     this.y = y;
 *   }
 *   ...
 * }
 * </pre>
 * 
 * Alternatively, you could use <code>&#64;Borrowed("this")</code>.
 * 
 * <pre>
 * &#064;RegionLock(&quot;Lock is this protects Instance&quot;)
 * public class Example {
 * 
 *   int x = 1;
 *   int y;
 * 
 *   &#064;Borrowed(&quot;this&quot;)
 *   public Example(int y) {
 *     this.y = y;
 *   }
 *   ...
 * }
 * </pre>
 * 
 * It is also possible to support the {@link RegionLock} assertion with effects
 * ({@link Starts} and {@link RegionEffects}) annotations on a constructor
 * instead of using {@link Unique} or {@link Borrowed}. This is useful if the
 * constructor aliases the receiver into a field within the newly constructed
 * object. This situation is uncommon in real-world Java code. In the example
 * below if an explicit lock object is not provided to the constructor then
 * {@code this} is used and, hence, aliased into the field {@code lock}. In this
 * code <code>&#64;Unique("return")</code> cannot be verified so the effects
 * annotations are used on the constructor instead.
 * 
 * <pre>
 * &#064;Region(&quot;private Y&quot;)
 * &#064;RegionLock(&quot;Lock is lock protects Y&quot;)
 * public class Example {
 * 
 *   private final Object lock;
 *   &#064;InRegion(&quot;Y&quot;)
 *   private int y;
 * 
 *   &#064;Starts(&quot;nothing&quot;)
 *   &#064;RegionEffects(&quot;none&quot;)
 *   public Example(int y, Object lock) {
 *     this.y = y;
 *     if (lock == null)
 *       this.lock = this;
 *     else
 *       this.lock = lock;
 *   }
 * 
 *   public int getY() {
 *     synchronized (lock) {
 *       return y;
 *     }
 *   }
 * 
 *   public void setY(int value) {
 *     synchronized (lock) {
 *       y = value;
 *     }
 *   }
 * }
 * </pre>
 * 
 * The scoped promise {@link Promise} can be used if the constructor is implicit
 * (i.e., generated by the compiler). It has the ability to place promises on
 * implicit and explicit constructors.
 * 
 * <pre>
 * &#064;RegionLock(&quot;Lock is this protects Instance&quot;)
 * &#064;Promise(&quot;@Unique(return) for new(**)&quot;)
 * public class Example {
 *   int x = 1;
 *   int y = 1;
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
 *  * @annotate RegionLock(&quot;SimpleLock is this protects Instance&quot;)
 *  &#42;/
 * class Simple { ... }
 * </pre>
 * 
 * @see PolicyLock
 * @see RegionLocks
 * @see RequiresLock
 * @see ReturnsLock
 */
@Documented
@Target(ElementType.TYPE)
public @interface RegionLock {
  /**
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = IDENTIFIER &quot;is&quot; lockExpression &quot;protects&quot; regionName
   * 
   * lockExpression = simpleLockExpression / qualifiedLockExpression
   * 
   * simpleLockExpression = 
   *   &quot;class&quot; /              ; the Class object referenced by the &quot;class&quot; pseudo-field of the annotated class
   *   &quot;this&quot; /               ; the instance itself
   *   [&quot;this&quot; &quot;.&quot;] IDENTIFIER  ; the object referenced by the named field
   * 
   * qualifiedLockExpression = 
   *   namedType &quot;.&quot; &quot;CLASS&quot; /            ; the Class object referenced by the &quot;class&quot; pseudo-field of a named class
   *   namedType &quot;.&quot; &quot;THIS&quot; /             ; a named enclosing instance
   *   namedType &quot;.&quot; IDENTIFIER /         ; a named static field
   *   namedType &quot;.&quot; THIS &quot;.&quot; IDENTIFIER  ; a named field of an enclosing instance
   * 
   * namedType = IDENTIFIER *(&quot;.&quot; IDENTIFIER)
   * 
   * regionName = IDENTIFIER
   * 
   * IDENTIFIER = Legal Java Identifier
   * </pre>
   */
  String value();
}
