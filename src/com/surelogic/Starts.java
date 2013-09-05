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
 * Declares what threads, if any, are started, i.e., by {@link Thread#start()},
 * during the execution of the method or constructor to which this annotation is
 * applied. We refer to this as the thread effects of the method or constructor.
 * <p>
 * Presently this annotation has only one legal form,
 * <code>&#064;Starts("nothing")</code>, that indicates that the
 * method/constructor does not cause any threads to be started. It is a modeling
 * error if {@link #value} is not {@code "nothing"}.
 * <p>
 * Methods that override a method annotated with
 * <code>&#064;Starts("nothing")</code> must also be explicitly annotated
 * <code>&#064;Starts("nothing")</code>. It is a modeling error if they are not.
 * 
 * <h3>Semantics:</h3>
 * 
 * Execution of the annotated method, or any methods that it calls,
 * transitively, do not cause <code>start()</code> to be invoked on any
 * <code>java.lang.Thread</code> object.
 * 
 * <h3>Examples:</h3>
 * 
 * A method called {@code init} that promises to start no threads. Because
 * {@code init()} calls the method {@code internalInit()} that method must also
 * promise to start no threads.
 * 
 * <pre>
 * class Example {
 *   private Object state;
 * 
 *   &#064;Starts(&quot;nothing&quot;)
 *   public void init() {
 *     internalInit();
 *   }
 * 
 *   &#064;Starts(&quot;nothing&quot;)
 *   private void internalInit() {
 *     state = new Object();
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
 * class Example {
 *   private Object state;
 * 
 *   /**
 *    * @annotate Starts(&quot;nothing&quot;)
 *    &#42;/
 *   public void init() {
 *     internalInit();
 *   }
 * 
 *   /**
 *    * @annotate Starts(&quot;nothing&quot;)
 *    &#42;/
 *   private void internalInit() {
 *     state = new Object();
 *   }
 * }
 * </pre>
 */
@Documented
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface Starts {
  /**
   * Must be {@code "nothing"}. Additional values are reserved for future use.
   * 
   * <p>
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = &quot;nothing&quot;
   * </pre>
   */
  public String value();
}
