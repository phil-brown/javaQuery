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
 * Container annotation for multiple {@link Region} annotations. It is a
 * modeling error for a class to have both a {@link Regions} and a
 * {@link Region} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * This annotation holds a list {@link Region} declarations without imposing any
 * further constraint on the program's implementation.
 * 
 * <h3>Examples:</h3>
 * 
 * Declaring two regions for the same class.
 * 
 * <pre>
 * &#064;Regions({
 *   &#064;Region(&quot;private SetRegion&quot;),
 *   &#064;Region(&quot;protected ObserverRegion&quot;)
 * })
 * class Example { ... }
 * </pre>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation is not supported in Javadoc because Javadoc supports multiple
 * <code>&#064;annotate</code> tags of the same type (see the example below).
 * Javadoc annotation can be useful for Java 1.4 code which does not include
 * language support for annotations.
 * 
 * <pre>
 * /**
 *  * &#064;annotate Region(&quot;private SetRegion&quot;)
 *  * &#064;annotate Region(&quot;protected ObserverRegion&quot;)
 *  *&#047;
 * class Example { ... }
 * </pre>
 * 
 * @see Region
 */
@Documented
@Target(ElementType.TYPE)
public @interface Regions {
  /**
   * The {@link Region} annotations to apply to the class.
   */
  Region[] value();
}
