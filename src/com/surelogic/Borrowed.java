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
 * Declares that the <em>reference</em> passed to the parameter or receiver to which this annotation is applied
 * does not receive any new aliases due to reads from the annotated parameter or receiver during execution of the method or
 * constructor. That is, {@link Unique} values can be safely passed to the
 * parameter or used as the receiver with the guarantee that they will still be
 * unique when the method returns.
 * <p>
 * Annotating <tt>&#64;Borrowed("this")</tt> on a constructor is defined to be
 * equivalent to annotating <tt>&#64;Unique("return")</tt>. Either of these
 * annotations indicates that the object being constructed is not aliased during
 * construction, which implies that the reference "returned" by the {@code new}
 * expression that invokes the constructor is unique. Which annotation is
 * preferred, <tt>&#64;Borrowed("this")</tt> or <tt>&#64;Unique("return")</tt>,
 * is a matter of programmer preference.
 * <p>
 * Annotating a field as <tt>&#64;Borrowed</tt> means that the entity pointed
 * to by the field is unique as long as the object with the borrowed field is being used.  Put another way,
 * the borrowed field is treated as unique as long as the unique object assigned to it isn't being used.  Once the 
 * original reference is used, the borrowed field cannot be used any more.  Such a field
 * can be initialized with a value of a borrowed parameter to a constructor
 * as long as the parameter's borrowed annotation sets the <code>allowsReturn</code>
 * attribute to <code>true</code> and  we have write access to its complete state.
 * A <tt>&#64;Borrowed</tt> field must be <code>final</code> and cannot be
 * <code>static</code>.  
 * <p>
 * Annotating <code>&#64;Borrowed</code> on a field additionally means that the
 * {@code Instance} region of the object referenced by the annotated field is
 * mapped into the {@code Instance} region of the object that contains the
 * annotated field.
 * 
 * <p><em>Borrowed fields are not currently assured by analysis.</em>
 * 
 * <p>
 * It is a modeling error to use this annotation on a parameter whose type is
 * primitive. For example, the method declaration
 * <tt>public void setValue(&#64;Borrowed int value)</tt> would generate a
 * modeling error. It is a modeling error to annotate
 * <tt>&#64;Borrowed("this")</tt> on a {@code static} method. For example, the
 * declaration <tt>&#64;Borrowed("this") public static void process()</tt> would
 * generate a modeling error.
 * <p>
 * Methods that override a method with <code>&#064;Borrowed</code> applied to a
 * parameter (or the receiver), <i>p</i>, must also have <i>p</i> explicitly
 * annotated with <code>&#064;Borrowed</code>. It is a modeling error if they
 * are not.
 * 
 * <h3>Semantics:</h3>
 * 
 * The reference in the annotated parameter or receiver at the start of the
 * method's execution is not read from the parameter and subsequently assigned
 * to a field of any object or returned by the method.
 * 
 * <h3>Examples:</h3>
 * 
 * In the below example, the first parameter to the {@code static} method
 * {@code run} promises that it will not hold onto an alias to the passed
 * {@code Cart} object. The two methods {@code go} and {@code log} promise that
 * they will not hold onto an alias to the receiver. The combination of these
 * three promises allows the model to be verified.
 * 
 * <pre>
 * public class Cart {
 * 
 *   static void run(@Borrowed Cart cart) {
 *     cart.go(10);
 *     cart.log(&quot;started moving&quot;);
 *     cart.go(20);
 *     cart.log(&quot;moved again&quot;);
 *   }
 * 
 *   int x, y;
 *   final List&lt;String&gt; log = new ArrayList&lt;String&gt;();
 * 
 *   &#064;Borrowed(&quot;this&quot;)
 *   void go(int value) {
 *     x += value;
 *     y += value;
 *   }
 * 
 *   &#064;Borrowed(&quot;this&quot;)
 *   void log(String msg) {
 *     log.add(msg);
 *   }
 *   ...
 * }
 * </pre>
 * 
 * This annotation is often used to support a {@link RegionLock} assertion on a
 * constructor because if the receiver is not leaked during object construction
 * then the state under construction will remain within the thread that invoked
 * {@code new}.
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
 * The scoped promise {@link Promise} can be used if the constructor is implicit
 * (i.e., generated by the compiler). It has the ability to place promises on
 * implicit and explicit constructors.
 * 
 * <pre>
 * &#064;RegionLock(&quot;Lock is this protects Instance&quot;)
 * &#064;Promise(&quot;@Borrowed(this) for new(**)&quot;)
 * public class Example {
 *   int x = 1;
 *   int y = 1;
 *   ...
 * }
 * </pre>
 * 
 * Annotating a parameter as &#064;Borrowed only specifies that the method will not alias the reference
 * in the annotated parameter <em>via that parameter</em>.  If the method
 * has access to that reference via another non-borrowed parameter, aliases may
 * be created through the other parameter.  Consider
 * 
 * <pre>
 * public class Example {
 *   private Example x;
 *   private Example y;
 *   
 *   &#064;Unique
 *   private Example u;
 *  
 *   &#064;Unique("return")
 *   public Example() {
 *     super();
 *   }
 *  
 *   public Example method(&#064;Borrowed Example a, Example b) {
 *     a.doStuff();
 *     return b;
 *   }
 *   
 *   &#064;Borrowed("this")
 *   public void doStuff() {
 *     // ...
 *   }
 *  
 *   public void run() {
 *     Example a = method(x, y);
 *     Example b = method(y, y);
 *     Example c = method(u, u); // Illegal: Uniqueness assurance fails here
 *     // ...
 *   }
 * }
 * </pre>
 * 
 * Here the method <code>method()</code> promises to borrow the reference passed
 * to <code>a</code>, but says nothing about the reference passed to
 * <code>b</code>.  No aliases are made from <code>a</code> because the the
 * method <code>doStuff()</code> borrows its receiver.  The method returns an 
 * alias to the reference in <code>b</code> however.
 * 
 * <p>Let's consider the calls to <code>method()</code> from <code>run()</code>:
 * <ul>
 *   <li>The first call passes the values of the <code>this.x</code> and
 *   <code>this.y</code>.  This call is unremarkable.
 *   <li>The second call passes the value of the <code>this.y</code> to both
 *   parameters.  This call does result in an alias to the reference in
 *   <code>this.y</code>, the method return value, but the alias is created
 *   through the non-borrowed parameter <code>b</code>.
 *   <li>The third call passes the value of the <code>&#064;Unique</code>
 *   field <code>u</code> to both parameters.  This case is troubling because
 *   <code>method()</code> returns an alias to the field via the non-borrowed
 *   parameter <code>b</code>.  This call, however, is rejected by uniqueness
 *   assurance because simply passing the value of <code>u</code> to two
 *   arguments of the same method creates aliases
 * </ul>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation may placed in Javadoc, which can be useful for Java 1.4 code
 * which does not include language support for annotations, via the
 * <code>&#064;annotate</code> tag. One complication is that the parameter being
 * annotated must be explicitly specified because the annotation can no longer
 * appear in the context of the parameter declaration.
 * 
 * <pre>
 * /**
 *  * @annotate Borrowed(&quot;a, b, c&quot;)
 *  &#42;/
 * public void m1(Object a, Object b, Object c) { ... }
 * </pre>
 * 
 * This annotation states that the three parameters are borrowed. Alternatively,
 * you can use several annotations as shown below.
 * 
 * <pre>
 * /**
 *  * @annotate Borrowed(&quot;a&quot;)
 *  * @annotate Borrowed(&quot;b&quot;)
 *  * @annotate Borrowed(&quot;c&quot;)
 *  &#42;/
 * public void m1(Object a, Object b, Object c) { ... }
 * </pre>
 * 
 * @see Unique
 */
@Documented
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE })
public @interface Borrowed {
  /**
   * When annotating a constructor or a method, this attribute must be
   * {@code "this"} to clarify the intent that it is the receiver that is
   * borrowed. It is a modeling error if the attribute is not {@code "this"} in
   * this case.
   * 
   * <pre>
   * class C {
   *   &#064;Borrowed(&quot;this&quot;)
   *   public C() { ... }
   * 
   *   &#064;Borrowed(&quot;this&quot;)
   *   void method() { ... }
   *   ...
   * }
   * </pre>
   * 
   * This attribute is not used when annotating a parameter; it is a modeling
   * error for the value to be anything other than the default when annotating a
   * parameter.
   * 
   * <pre>
   * int proc(@Borrowed int value       /* Illegal: Parameter has primitive type &#42;/, 
   *          &#064;Borrowed Object settings /* Legal: Parameter has reference type &#42;/) { ... }
   * </pre>
   * 
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = [&quot;this&quot;] ; See above comments
   * </pre>
   */
  String value() default "";
  
  /**
   * When this attribute is <code>true</code> the annotated parameter (or 
   * receiver) is allowed to be assigned to a borrowed field in the object
   * returned by the method/constructor.  This attribute is not used for 
   * borrowed fields.  <em>Assurance currently ignores this attribute.</em>
   */
  public boolean allowReturn() default false;
}
