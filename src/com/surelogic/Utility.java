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
 * The class to which this annotation is applied is a utility, meaning it only
 * has <tt>static</tt> methods and state. This annotation is similar to the UML
 * stereotype &laquo;utility&raquo; which is applied to a class that does not
 * have instances, but whose attributes and operations have class scope. Joshua
 * Bloch in <i>Effective Java (Second Edition)</i> (Addison-Wesley 2008) refers
 * to such classes as <i>utility classes</i>, (in Item 4) which implies that
 * <ul>
 * <li>the class is declared {@code public};</li>
 * <li>all the class's fields and methods are declared {@code static};</li>
 * <li>the class has only one constructor that is {@code private}, takes no
 * parameters, and does nothing except, optionally, throw {@link AssertionError}
 * (i.e., <tt>{}</tt> or <tt>{throw new AssertionError();}</tt>); and</li>
 * <li>this constructor is not invoked by the class's implementation.
 * </ul>
 * 
 * It is recommended that a utility class be declared {@code final} to convey
 * the intent that subclasses are prohibited. However this is not required
 * because the {@code private} no-arg constructor ensures noninstantiability of
 * the utility class and makes it impossible to subclass.
 * <p>
 * It is recommended that a utility class extend only {@link java.lang.Object}.
 * However this is not required because the noninstantiability of the utility
 * class makes its parent irrelevant. Further, some security coding standards
 * require all classes to extend a common base class&mdash;including utilities.
 * <p>
 * Note that there are no restrictions on nested classes within a utility class.
 * A utility provides an interface that makes a subsystem or some piece of the
 * program's functionality easy to use and may use nested types to achieve its
 * objective.
 * <p>
 * Utility classes may be, but not required, to be immutable, in which case the
 * {@link Immutable} annotation should be added to the class (for example, if
 * the utility class declares no fields). In general, a utility class may have
 * mutable state.
 * <p>
 * It is a modeling error to apply this annotation to an interface.
 * 
 * <h3>Semantics:</h3>
 * 
 * The class to which this annotation is applied is not allowed to have any
 * instances&mdash;all the class's attributes and operations have class scope.
 * Further, the class is not allowed to be subclassed. The class may have
 * mutable state.
 * 
 * <h3>Examples:</h3>
 * 
 * The internalization class below is a utility.
 * 
 * <pre>
 * &#064;Utility
 * public final class I18N {
 * 
 *   private static final ResourceBundle ERR = ResourceBundle.getBundle(I18N.class.getPackage().getName() + &quot;.Err&quot;);
 * 
 *   private static final String ERROR_FORMAT = &quot;(Timing Framework #%d) %s&quot;;
 * 
 *   private static String getString(final ResourceBundle bundle, final String keyTemplate, final Object... args) {
 *     return bundle.getString(String.format(keyTemplate, args));
 *   }
 * 
 *   public static String err(final int number, Object... args) {
 *     return String.format(I18N.err(number), args);
 *   }
 * 
 *   // Suppress default constructor for noninstantiability
 *   private I18N() {
 *     throw new AssertionError();
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
 *  * &#064;annotate Utility
 *  *&#047;
 * public final class I18N {
 *   ...
 * }
 * </pre>
 */
@Documented
@Target(ElementType.TYPE)
public @interface Utility {
  // Marker annotation
}
