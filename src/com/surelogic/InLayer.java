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
 * Declares that the annotated type is part of the named layers.
 * 
 * <h3>Semantics:</h3>
 * 
 * Including an annotated type into a named layer does not constrain the
 * implementation of the program, it simply gives a name to a subset of the
 * program's types.
 * 
 * <h3>Examples:</h3>
 * 
 * Declaring a type to be part of a layer.
 * 
 * <pre>
 * &#064;InLayer(&quot;edu.afit.smallworld.text.ui.CONSOLE_UI&quot;)
 * class Example { ... }
 * </pre>
 * 
 * Declaring a type to be part of two different layers.
 * 
 * <pre>
 * &#064;InLayer(&quot;edu.afit.smallworld.text.ui.CONSOLE_UI, edu.afit.smallworld.text.ui.SWING_UI&quot;)
 * class Example { ... }
 * </pre>
 * 
 * Alternatively:
 * 
 * <pre>
 * &#064;InLayer(&quot;edu.afit.smallworld.text.ui.{CONSOLE_UI, SWING_UI}&quot;)
 * class Example { ... }
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
 *  * @annotate InLayer(&quot;edu.afit.smallworld.text.ui.{CONSOLE_UI, SWING_UI}&quot;)
 *  &#42;/
 * class Example { ... }
 * </pre>
 * 
 * @see Layer
 */
@Documented
@Target(ElementType.TYPE)
public @interface InLayer {
  /**
   * The one or more layers that the type is part of. The attribute is
   * restricted to strings that match the following grammar:
   * 
   * <p>
   * value = layer_spec *("<tt>,</tt>" layer_spec)
   * 
   * <p>
   * layer_spec = dotted_name <i>; Layer name</i><br>
   * layer_spec /= dotted_name "<tt>.</tt>" "<tt>{</tt>" name *("<tt>,</tt>
   * " name) "<tt>}</tt>" <i>; Enumeration of layers</i>
   * 
   * <p>
   * The braces "<tt>{</tt>" "<tt>}</tt>" are syntactic sugar used to enumerate
   * a list of layers that share the same prefix.
   */
  String value();
}
