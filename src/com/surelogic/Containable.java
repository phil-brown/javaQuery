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
 * The class to which this annotation is applied is declared to be containable.
 * That is, instances of the class can be safely encapsulated via region
 * aggregation into other objects because methods of this class do not leak
 * references to themselves or any of the other objects that they reference,
 * transitively. This implies that
 * <ul>
 * <li>each non-<code>static</code> method must be annotated
 * <code>&#064;Borrowed("this")</code>;
 * <li>each constructor must be annotated <code>&#064;Unique("return")</code>;
 * and
 * <li>each non-primitively typed field must be
 * <ul>
 * <li>of a type annotated <code>&#064;Containable</code>, and
 * <li>annotated <code>&#064;Unique</code> or <code>&#064;UniqueInRegion</code>.
 * </ul>
 * </ul>
 * <p>
 * Typically, subtypes of the annotated type must be explicitly annotated
 * <code>&#064;Containable</code> as well. It is a modeling error if they are
 * not. This annotation has two attributes, <tt>implementationOnly</tt> and
 * <tt>verify</tt>, that control how subtypes of an {@link Containable} type
 * must be annotated. The <tt>implementationOnly</tt> attribute indicates that
 * the implementation of the annotated class should be assured without making a
 * general statement about the visible behavior of the type or its subtypes.
 * There are several rules with regards to the <tt>implementationOnly</tt>
 * attribute on {@link Containable} types:
 * <ol>
 * <li>The <tt>implementationOnly</tt> attribute must be {@code false} when
 * {@link Containable} appears on an interface, because interfaces do not have
 * an implementation.</li>
 * <li>The subinterfaces of an interface annotated with {@link Containable} must
 * be annotated with {@link Containable}; classes that implement an interface
 * annotated with {@link Containable} must be annotated with
 * <tt>&#064;Containable(implementationOnly=false)</tt>.</li>
 * <li>The superclass of a class annotated with
 * <tt>&#064;Containable(implementationOnly=true)</tt> must be annotated with
 * <tt>&#064;Containable(implementationOnly=true)</tt>; there are no constraints
 * on the subclasses.</li>
 * <li>The superclass of a class annotated with
 * <tt>&#064;Containable(implementationOnly=false)</tt> must be annotated with
 * either <tt>&#064;Containable(implementationOnly=false)</tt> or
 * <tt>&#064;Containable(implementationOnly=true)</tt>; the subclasses must be
 * annotated with <tt>&#064;Containable(implementationOnly=false)</tt>.</li>
 * </ol>
 * Finally, it may be possible to implement a class that satisfies the semantics
 * of {@link Containable}, but that is not verifiable using the syntactic and
 * annotation constraints described above. For this case, we provide the
 * "escape hatch" of turning off tool verification for the annotation with the
 * <tt>verify</tt> attribute. For example,
 * <tt>&#064;Containable(verify=false)</tt> would skip tool verification
 * entirely.
 * 
 * <p>A type may not be annotated with both <code>&#064;Containable</code>
 * and <code>&#064;NotContainable</code>.
 * 
 * <h3>Semantics:</h3>
 * 
 * For each object transitively referenced by instances of the annotated type
 * (including the instance itself), no reference to that object is returned by
 * any method/constructor of the class nor is that object referenced by a field
 * of any other object that is not also transitively referenced by the instance
 * (including the instance itself).
 * 
 * <h3>Examples:</h3>
 * 
 * The class <code>Point</code> is a containable class that uses only
 * primitively typed fields. Instances of it are in turn contained by the
 * <code>Rectangle</code> class.
 * 
 * <pre>
 * &#064;Containable
 * public class Point {
 *   private int x;
 *   private int y;
 * 
 *   &#064;Unique(&quot;return&quot;)
 *   &#064;RegionEffects(&quot;none&quot;)
 *   public Point(int x, int y) {
 *     this.x = x;
 *     this.y = y;
 *   }
 * 
 *   &#064;Borrowed(&quot;this&quot;)
 *   &#064;RegionEffects(&quot;writes Instance&quot;)
 *   public void translate(int dx, int dy) {
 *     x += dx;
 *     y += dy;
 *   }
 * }
 * 
 * &#064;Containable
 * public class Rectangle {
 *   &#064;Unique
 *   private final Point topLeft;
 * 
 *   &#064;Unique
 *   private final Point bottomRight;
 * 
 *   &#064;Unique(&quot;return&quot;)
 *   public Rectangle(int x1, int y1, int x2, int y2) {
 *     topLeft = new Point(x1, y1);
 *     bottomRight = new Point(x2, y2);
 *   }
 * 
 *   &#064;Borrowed(&quot;this&quot;)
 *   &#064;RegionEffects(&quot;writes Instance&quot;)
 *   public void translate(int dx, int dy) {
 *     topLeft.translate(dx, dy);
 *     bottomRight.translate(dx, dy);
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
 *  * &#064;annotate Containable
 *  *&#047;
 * public class Point {
 *   ...
 * }
 * </pre>
 * 
 * @see ThreadSafe
 * @see NotContainable
 * @see Vouch
 */
@Documented
@Target(ElementType.TYPE)
public @interface Containable {
  /**
   * Indicates that the implementation of the annotated class should be assured
   * without making a general statement about the visible behavior of the type
   * or its subtypes.
   * 
   * @return {@code true} if only the annotated class should be assured without
   *         making a general statement about the visible behavior of the type
   *         or its subtypes, {@code false} otherwise. The default value for
   *         this attribute is {@code false}.
   */
  public boolean implementationOnly() default false;

  /**
   * Indicates whether or not tool verification should be attempted.
   * 
   * @return {@code true} if the claim should be verified by a tool, such as
   *         SureLogic JSure, {@code false} otherwise. The default value for
   *         this attribute is {@code true}.
   */
  public boolean verify() default true;
}