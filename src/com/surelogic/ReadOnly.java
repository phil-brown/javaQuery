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
 * annotation is applied cannot be used to mutate an object. A readonly variable
 * differs from a variable to an immutable object in that readonly does not exclude
 * the existence of references used for mutation.  A readonly parameter, unlike
 * a borrowed parameter for which mutation is not permitted, may be compromised/aliased.
 * Thus we normally do not recommend variables be annotated readonly unless
 * the callee wishes to retain a reference.
 * <p>
 * A {@link Borrowed} field may be annotated readonly to indicate that the 
 * field cannot be used for mutation.
 * <p>
 * It is a modeling error to annotate a variable of
 * primitive type (e.g., <code>&#64;ReadOnly("return") public int getValue()</code>
 * would generate a modeling error). It is a modeling error to annotate a
 * parameter if the parameter's type is primitive (e.g.,
 * <code>public void setValue(&#64;Unique int value)</code> would generate a
 * modeling error).
 * <p>
 * <em>This annotation is not currently checked by analysis and is still experimental.</em>
 * It may be used for documentation purposes.
 * 
 * <h3>Semantics:</h3>
 * 
 * <i>Parameter:</i> 
 * <p>
 * <i>Return Value:</i> 
 * <p>
 * <i>Field:</i> 
 * 
 * <h3>Examples:</h3>
 * 
 * Under development.
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
 *  * @annotate ReadOnly(&quot;a, b, c&quot;)
 *  &#42;/
 * public void m1(Object a, Object b, Object c) { ... }
 * </pre>
 * 
 * This annotation states that the three parameters are readonly. Alternatively,
 * you can use several annotations as shown below.
 * 
 * <pre>
 * /**
 *  * @annotate ReadOnly(&quot;a&quot;)
 *  * @annotate ReadOnly(&quot;b&quot;)
 *  * @annotate ReadOnly(&quot;c&quot;)
 *  &#42;/
 * public void m1(Object a, Object b, Object c) { ... }
 * </pre>
 * 
 * @see Borrowed
 * @see Immutable
 */
@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER })
public @interface ReadOnly {
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
   * receiver is readonly. This value is only allowed on methods.
   * <li>If the list contains the value {@code "return"}, it indicates the
   * return value is readonly. This value is allowed on methods and constructors.
   * <li>If the list contains both {@code "this"} and {@code "return"}, it
   * indicates that both the receiver and the return value are readonly. This
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
}
