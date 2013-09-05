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
 * Container annotation for multiple {@link InRegion} annotations on a type. It
 * is a modeling error for a type to have both an {@link InRegion} and an
 * {@link InRegions} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * This annotation holds a list {@link InRegion} annotations without imposing
 * any further constraint on the program's implementation.
 * 
 * <h3>Examples:</h3>
 * 
 * The class {@code C} declares two regions. The region {@code Position}
 * contains the fields {@code x}, {@code y}, and {@code z}. The region
 * {@code Counters} contains the fields {@code readCounter} and
 * {@code writeCounter}.
 * 
 * <pre>
 * &#064;Regions({
 *   &#064;Region(&quot;private Position&quot;),
 *   &#064;Region(&quot;private Counters&quot;)
 * })
 * &#064;InRegions({
 *   &#064;InRegion(&quot;x, y, z into Position&quot;),
 *   &#064;InRegion(&quot;readCount, writeCount into Counters&quot;)
 * })
 * public class C {
 *   private int x, y, z;
 *   private int readCount;
 *   private int WriteCount;
 *   ...
 * }
 * </pre>
 * 
 * The above example is equivalent to the following use of {@code InRegion} on
 * field declarations.
 * 
 * <pre>
 * &#064;Regions({
 *   &#064;Region(&quot;private Position&quot;),
 *   &#064;Region(&quot;private Counters&quot;)
 * })
 * public class C {
 *   &#064;InRegion("Position"
 *   private int x, y, z;
 *   
 *   &#064;InRegion("Counters")
 *   private int readCount;
 *   
 *   &#064;InRegion("Counters")
 *   private int WriteCount;
 *   ...
 * }
 * </pre>
 * 
 * Which style is preferred is a matter of programmer preference.
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
 *  * &#064;annotate Region(&quot;private Position&quot;)
 *  * &#064;annotate Region(&quot;private Counters&quot;)
 *  * &#064;annotate InRegion(&quot;x, y, z into Position&quot;)
 *  * &#064;annotate InRegion(&quot;readCount, writeCount into Counters&quot;)
 *  *&#047;
 * public class C {
 *   private int x, y, z;
 *   private int readCount;
 *   private int WriteCount;
 *   ...
 * }
 * </pre>
 * 
 * @see InRegion
 */
@Documented
@Target(ElementType.TYPE)
public @interface InRegions {
  /**
   * The {@link InRegion} annotations to apply to the class.
   */
  InRegion[] value();
}
