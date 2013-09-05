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
 * Declares that the parameter, receiver, return value, or field to which this
 * annotation is applied is a unique reference to an object. Normally this
 * indicates that the referenced object is not aliased. It is allowed, however,
 * to pass a unique reference to a method as an actual argument or the receiver
 * if formal argument or receiver, respectively, is {@link Borrowed}. That is,
 * {@link Unique} values can be safely passed to the parameter or used as the
 * receiver with the guarantee that they will still be unique when the method
 * returns. Said another way we create a temporary alias on the stack then
 * ensure that it goes away.
 * <p>
 * Setting the <tt>allowRead=true</tt> attribute weakens the uniqueness guarantees:
 * it permits there to be <code>&#64;ReadOnly</code> aliases around.
 * In other words, only the ability to write (mutate) is restricted to this alias.
 * Once a unique reference has granted <tt>allowRead</tt>, there is no going back,
 * unlike with (temporary) borrowed references.  <em>This attribute is not currently 
 * checked by analysis</em>.
 * <p>
 * Annotating <code>&#64;Unique("return")</code> on a constructor is defined to
 * be equivalent to annotating <code>&#64;Borrowed("this")</code>. Either of
 * these annotations indicates that the object being constructed is not aliased
 * during construction, which implies that the reference "returned" by the
 * {@code new} expression that invokes the constructor is unique. Which
 * annotation is preferred, <code>&#64;Unique("return")</code> or
 * <code>&#64;Borrowed("this")</code>, is a matter of programmer preference.
 * <p>
 * Methods that override a method annotated with
 * <code>&#64;Unique("return")</code> must also be explicitly annotated
 * <code>&#64;Unique("return")</code>. It is a modeling error if they are not.
 * Methods that override a method with a <code>&#64;Unique</code> parameter are
 * <i>not</i> required to maintain that parameter's uniqueness (uniqueness on a
 * parameter is contravariant), but may via explicit annotation.
 * <p>
 * Annotating <code>&#64;Unique</code> on a field is defined to mean that the
 * {@code Instance} region of the object referenced by the annotated field is
 * mapped into the {@code Instance} region of the object that contains the
 * annotated field if the annotated field is {@code final}.  If the annotated 
 * field is not {@code final}, the {@code Instance} region of the object
 * referenced by the annotated field is mapped into the field itself.  A 
 * {@code final} {@code static} field cannot be annotated with
 * <code>&#64;Unique</code>:  the annotation {@link UniqueInRegion} must be
 * used instead.
 * <p>
 * It is a modeling error to annotate a method return value if the return type
 * is primitive (e.g., <code>&#64;Unique("return") public int getValue()</code>
 * would generate a modeling error). It is a modeling error to annotate a
 * parameter if the parameter's type is primitive (e.g.,
 * <code>public void setValue(&#64;Unique int value)</code> would generate a
 * modeling error).
 * 
 * <h3>Semantics:</h3>
 * 
 * <i>Parameter:</i> At the start of the method's execution the annotated
 * parameter is either <code>null</code> or refers to an object that is not
 * referenced by a field of any object.
 * <p>
 * <i>Return Value:</i> The value returned by the annotated method is
 * <code>null</code> or is an object that is not referenced by a field of any
 * object.
 * <p>
 * <i>Field:</i> At all times, the value of the annotated field is either
 * <code>null</code> or is an object that is not referenced by a field of any
 * other object or another field of the same object. The {@code Instance} region
 * of the object referenced by the annotated field is mapped into the
 * {@code Instance} region of the object that contains the annotated field.
 * 
 * <h3>Examples:</h3>
 * 
 * A class with a unique field. Note that the {@code getUniqueObject} ensures
 * that the returned reference is unique by creating a new object.
 * 
 * <pre>
 * public class UniqueField {
 * 
 *   &#064;Unique
 *   private Object o;
 * 
 *   public void setField(@Unique Object value) {
 *     o = value;
 *     process(o);
 *   }
 * 
 *   &#064;Unique(&quot;return&quot;)
 *   public Object getUniqueObject() {
 *     return new Object();
 *   }
 * 
 *   &#064;RegionEffects(&quot;reads value:Instance&quot;)
 *   public static void process(@Borrowed Object value) {
 *     // for future processing
 *   }
 * }
 * </pre>
 * 
 * The call to {@code process} is allowed because that method promises that the
 * alias to {@code o} created by the {@code value} parameter will be returned
 * once the method call completes. However, {@code process} must also promise
 * not to read state outside of its passed parameters. This is done with the
 * {@link RegionEffects} annotation on the {@code process} method declaration.
 * <p>
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
 *   &#064;Unique(&quot;return&quot;)
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
 * <code>&#064;annotate</code> tag. One complication is that the parameter being
 * annotated must be explicitly specified because the annotation can no longer
 * appear in the context of the parameter declaration.
 * 
 * <pre>
 * /**
 *  * @annotate Unique(&quot;a, b, c&quot;)
 *  &#42;/
 * public void m1(Object a, Object b, Object c) { ... }
 * </pre>
 * 
 * This annotation states that the three parameters are unique. Alternatively,
 * you can use several annotations as shown below.
 * 
 * <pre>
 * /**
 *  * @annotate Unique(&quot;a&quot;)
 *  * @annotate Unique(&quot;b&quot;)
 *  * @annotate Unique(&quot;c&quot;)
 *  &#42;/
 * public void m1(Object a, Object b, Object c) { ... }
 * </pre>
 * 
 * @see Borrowed
 * @see Region
 */
@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER })
public @interface Unique {
  /**
   * When annotating a method, this attribute is used to disambiguate whether
   * the annotation refers to the method's receiver, the method's return value,
   * or both. The value is comma separated list of tokens, and has the following
   * set of legal values (ignoring white space issues):
   * <ul>
   * <li>{@code ""}
   * <li>{@code "this"}
   * <li>{@code "return"}
   * <li>{@code "this, return"}
   * <li>{@code "return, this"}
   * </ul>
   * 
   * <p>
   * The values are interpreted thusly
   * <ul>
   * <li>If the list contains the value {@code "this"}, it indicates the
   * receiver is unique. This value is only allowed on methods.
   * <li>If the list contains the value {@code "return"}, it indicates the
   * return value is unique. This value is allowed on methods and constructors.
   * <li>If the list contains both {@code "this"} and {@code "return"}, it
   * indicates that both the receiver and the return value are unique. This
   * value is only allowed on methods.
   * </ul>
   * <p>
   * This attribute is not used when annotating a parameter or a field: the
   * attribute value must be the empty string in these cases.
   * <p>
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = [(&quot;this&quot; [&quot;,&quot; &quot;return&quot;]) / (&quot;return&quot; [&quot;,&quot; &quot;this&quot;])] ; See above comments
   * </pre>
   */
  String value() default "";
  
  /**
   * Setting the this attribute to <code>true</code> weakens the uniqueness
   * guarantees: it permits there to be <code>&#64;ReadOnly</code> aliases of
   * the referenced object.  In other words, only the ability to write (mutate)
   * is restricted to this alias.  <em>This attribute is not currently 
   * checked by analysis</em>.
   */
  public boolean allowRead() default false;
}
