/*
 * Copyright (c) 2005 Brian Goetz and Tim Peierls
 * Released under the Creative Commons Attribution License
 *   (http://creativecommons.org/licenses/by/2.5)
 * Official home: http://www.jcip.net
 *
 * Any republication or derived work distributed in source code form
 * must include this copyright and license notice.
 * 
 * 
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
 * The type to which this annotation is applied is not thread-safe. This
 * annotation primarily exists for clarifying the non-thread-safety of a class
 * that might otherwise be assumed to be thread-safe, despite the fact that it
 * is a bad idea to assume a class is thread-safe without good reason.
 * <p>
 * This annotation is trusted, i.e., it is <em>not verified</em>. Its use is for
 * documentation.
 * 
 * <p>A type may not be annotated with both <code>&#064;ThreadSafe</code>
 * and <code>&#064;NotThreadSafe</code>.
 * 
 * <h3>Relationship with <code>&#064;Immutable</code></h3>
 * 
 * <p>Thread safety and immutability are two points along the same axis.  This set of 
 * annotations can actually describe three points along the axis:
 * <dl>
 *   <dt><code>&#064;Mutable</code> and <code>&#064;NotThreadSafe</code>
 *   <dd>This is the same as being unannotated, or just
 *   <code>&#064;Mutable</code>, or just <code>&#064;NotThreadSafe</code>.  The type
 *   contains mutable state that is not safe to access concurrently from
 *   multiple threads.
 *   
 *   <dt><code>&#064;Mutable</code> and <code>&#064;ThreadSafe</code>
 *   <dd>This is the same as <code>&#064;ThreadSafe</code>.  The type contains mutable 
 *   state that is safe to access concurrently from multiple threads.
 *   
 *   <dt><code>&#064;Immutable</code> and <code>&#064;ThreadSafe</code>
 *   <dd>This is the same as <code>&#064;Immutable</code>.  The type contains no mutable
 *   state, and is thus safe to access concurrently from multiple threads.
 * </dl>
 * 
 * <p>The combination <code>&#064;Immutable</code> and <code>&#064;NotThreadSafe</code> is a
 * modeling error because an immutable type is obviously thread safe.  
 * 
 * <h3>Semantics:</h3>
 * 
 * Documenting that a type is not thread-safe does not constrain the
 * implementation of the program, it simply clarifies the programmer's intent.
 * 
 * <h3>Examples:</h3>
 * 
 * Most of the collection implementations provided in {@code java.util} are not
 * thread-safe. This could be documented for {@code java.util.ArrayList}, for
 * example, as shown below.
 * 
 * <pre>
 * package java.util;
 * 
 * &#064;NotThreadSafe
 * public class ArrayList extends ... {
 *   ...
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
 *  * &#064;annotate NotThreadSafe
 *  *&#047;
 * public class ArrayList extends ... {
 *   ...
 * }
 * </pre>
 * 
 * <i>Implementation note:</i> This annotation is derived from
 * <code>&#064;NotThreadSafe</code> proposed by Brian Goetz and Tim Peierls in
 * the book <i>Java Concurrency in Practice</i> (Addison-Wesley 2006) we have
 * simply adapted it to have semantics as a promise. Further, the annotation in
 * {@code net.jcip.annotations} may be used instead of this one with the same
 * tool behavior. One difference between the two annotations is that the the
 * annotation in {@code net.jcip.annotations} has retention policy of
 * {@link java.lang.annotation.RetentionPolicy#RUNTIME} while the annotation in
 * {@code com.surelogic} has a retention policy of
 * {@link java.lang.annotation.RetentionPolicy#CLASS}.
 * 
 * @see ThreadSafe
 */
@Documented
@Target(ElementType.TYPE)
public @interface NotThreadSafe {
  // Marker annotation
}
