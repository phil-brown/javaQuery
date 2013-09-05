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
 * Annotating a field as <tt>&#64;BorrowedInRegion</tt> means that the entity pointed
 * to by the field is unique as long as the object with the annotated field is being used.  Put another way,
 * the borrowed field is treated as unique as long as the unique object assigned to it isn't being used.  Once the 
 * original reference is used, the borrowed field cannot be used any more.  Such a field
 * can be initialized with a value of a borrowed parameter to a constructor
 * as long as the parameter's borrowed annotation sets the <code>allowsReturn</code>
 * attribute to <code>true</code> and  we have write access to its complete state.
 * A <tt>&#64;BorrowedInRegion</tt> field must be <code>final</code> and cannot be
 * <code>static</code>.  
 * <p>
 * Annotating <code>&#64;Borrowed</code> on a field additionally means that the
 * {@code Instance} region of the object referenced by the annotated field is
 * mapped into the {@code Instance} region of the object that contains the
 * annotated field.
 * <p>
 * This annotation differs from {@link Borrowed} only with regard to the region
 * the state referenced by the annotated field is mapped into. This annotation
 * declares that the {@code Instance} region of the object referenced by the
 * annotated field is mapped into a named region of the object that contains the
 * annotated field. {@link Borrowed} maps the state into the {@code Instance}
 * region of the object that contains the annotated field.
 * Therefore, the two
 * annotations below on the {@code friends} field are equivalent.
 * 
 * <pre>
 * &#064;Borrowed
 * private final Set&lt;Person&gt; friends; // initialized in constructor
 * 
 * &#064;BorrowedInRegion(&quot;Instance&quot;)
 * private final Set&lt;Person&gt; friends; // initialized in constructor
 * </pre>
 * 
 * In addition, a more complex syntax where regions of the object referenced
 * by the annotated field are allowed to be explicitly mapped to regions of the 
 * object that contains the annotated field. Using this syntax the annotation below is
 * equivalent to the two previous examples.
 * 
 * <pre>
 * &#064;BorrowedInRegion(&quot;Instance into Instance&quot;)
 * private final Set&lt;Person&gt; friends; // initialized in constructor
 * </pre>
 * 
 * This syntax should be rare in practice, however we show an example of its use
 * in the Examples section below.
 *
 * <p><em>Borrowed fields are not currently assured by analysis.</em>
 * 
 * <h3>Semantics:</h3>
 * 
 * 
 * <h3>Examples:</h3>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * @see Borrowed
 */
@Documented
@Target({ ElementType.FIELD })
public @interface BorrowedInRegion {
  /**
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = regionSpecification / regionMapping *(&quot;,&quot; regionMapping)
   * 
   * regionMapping = simpleRegionSpecification &quot;into&quot; regionSpecification
   * 
   * regionSpecification = simpleRegionSpecificaion / qualifiedRegionName
   * 
   * simpleRegionSpecification = IDENTIFIER                         ; Region of the class being annotated
   * 
   * qualifedRegionName = IDENTIFIER *(&quot;.&quot; IDENTIFIER) : IDENTIFER  ; Static region from the named, optionally qualified, class
   * 
   * IDENTIFIER = Legal Java Identifier
   * </pre>
   * 
   * <p>
   * In {@code A into B}, the first RegionSpecification is relative to the
   * object referenced by the field; the second is relative to the object that
   * contains the field.
   */
  String value() default "";
}
