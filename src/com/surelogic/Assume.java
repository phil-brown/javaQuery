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
 * Declares an assumption about a portion of the system. This annotation allows
 * the programmer to assume that a promise exists on one or more types in the
 * program&mdash;regardless of if those types have actually been annotated by
 * the programmer or not.
 * <p>
 * This annotation is trusted, i.e., it is <em>not verified</em>.
 * <p>
 * The scope of the assumption is the entire compilation unit that the
 * annotation appears within&mdash;regardless of the annotated element (e.g.,
 * type, constructor, method, or field). Allowing the {@link Assume} annotation
 * on declarations within the compilation unit lets the programmer place the
 * assumption closer, syntactically, to where it is needed. The scope of the
 * assumption is not changed, it remains the entire compilation unit.
 * <p>
 * The general form of an {@link Assume} annotation is
 * <p>
 * <code>&#064;Assume("</code><i>promise</i> <code>for</code> <i>target</i>
 * <code>")</code>
 * <p>
 * Where the <i>promise</i> is assumed on all declarations matched by
 * <i>target</i>. For example, the below assumes a
 * <code>&#064;Starts("nothing")</code> annotation on the no-argument
 * constructor for {@link IllegalArgumentException}. The assumption supports the
 * <code>&#064;Starts("nothing")</code> promised on the method.
 * 
 * <pre>
 * &#064;Starts("nothing")
 * &#064;Assume("Starts(nothing) for new() in IllegalArgumentException in java.lang")
 * public Rendezvous(int parties, RendezvousFunction function) {
 *   if (parties <= 0)
 *     throw new IllegalArgumentException();
 *   ...
 * }
 * </pre>
 * 
 * It is a modeling error to omit the <code>for</code> clause. Further, it is a
 * modeling error for <i>target</i> to match code within the scope of the
 * assumption. For example, the below annotation making an assumption within the
 * scope that the assumption applies is not well-formed.
 * 
 * <pre>
 * package com.surelogic.example;
 * 
 * // BAD - Modeling Error
 * &#064;Assume(&quot;Starts(nothing) for init() in Foo in com.surelogic.example&quot;)
 * class Foo {
 * 
 *   public void init() { ... }
 * }
 * </pre>
 * 
 * The syntax for <i>promise</i> is the same as if the promise was written in
 * the code except that all &quot;'s are removed (e.g.,
 * <code>&#064;Borrowed("this")</code> becomes <code>&#064;Borrowed(this)</code>
 * ).
 * <p>
 * To declare more than one scoped promise for an entity use the {@link Assumes}
 * annotation. It is a modeling error for an entity to have both a
 * {@link Assumes} and a {@link Assume} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * This annotation creates a "virtual" promise on the specified set of types.
 * Within the scope of the compilation unit the {@link Assume} annotation
 * appears, from the point of view of the verifying analyses, it is as if these
 * virtual promises were annotated by the programmer. If a virtual promise
 * exists as a real promise then the assumption has no semantics. If the virtual
 * promise does not exist as a real promise then it is "trusted" (i.e., not
 * verified). The results reported from tools, such as SureLogic JSure, should
 * make it clear to the programmer that the promise has not been verified.
 * <p>
 * Note that {@link Assume} serves a different role than {@link Vouch}.
 * Assumptions are used to express an expectation by the programmer about the
 * evolving model&mdash;irrespective of whether or not the expectation can be
 * verified. Assumptions act as a cut point for the programmer, allowing them to
 * formally express their belief that the assumed property is true, and continue
 * model expression based upon that belief. For example, the programmer assumes
 * something about code that they are not allowed to change, perhaps due to an
 * organizational constraint of some sort. In contrast, vouches made by the
 * programmer are typically applied in situations where a static verification
 * tool, such as JSure, is unable to verify the programmer's implementation to
 * be consistent with the expressed model.
 * 
 * <h3>Examples:</h3>
 * 
 * Consider the following example {@link Assume} annotations on a class
 * {@code C}:
 * <dl>
 * <dt>&#064;Assume("&#064;Borrowed(this) for new(**) in Foo")</dt>
 * <dd>Applies the assumption to any constructor in {@code Foo}. The '<b>**</b>'
 * pattern indicates any number of parameters including zero. If there is more
 * than one {@code Foo} type then the assumption is made for all of them.</dd>
 * 
 * <dt>
 * &#064;Assume("&#064;Borrowed(this) for !static **(**) in * in com.surelogic")
 * </dt>
 * <dd>Applies the assumption to any instance method or constructor declared in
 * any type in the package {@code com.surelogic}. The '<b>**(**)</b>' pattern
 * matches both methods and constructors. The '<b>*(**)</b>' pattern only
 * matches methods.</dd>
 * 
 * <dt>&#064;Assume(
 * "&#064;Borrowed(this) for !static *(**) | new(**) in * in com.surelogic")</dt>
 * <dd>Equivalent to the assumption above but without using the '<b>**(**)</b>'
 * shorthand.</dd>
 * </dl>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation may placed in Javadoc, which can be useful for Java 1.4 code
 * which does not include language support for annotations, via the
 * <code>&#064;annotate</code> tag.
 * 
 * <pre>
 * /**
 *  * &#064;annotate Assume(&quot;Starts(nothing) for new() in IllegalArgumentException in java.lang&quot;)
 *  *&#047;
 * public Rendezvous(int parties, RendezvousFunction function) { ... }
 * </pre>
 * 
 * @see Assumes
 * @see Vouch
 */
@Documented
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
public @interface Assume {
  /**
   * The value of this attribute must conform to the following grammar (in <a
   * href="http://www.ietf.org/rfc/rfc4234.txt">Augmented Backus&ndash;Naur
   * Form</a>):
   * 
   * <pre>
   * value = Payload [ &quot;for&quot; Target ]
   * 
   * Payload = A promise with &quot;'s removed, e.g., &#064;Borrowed("this") becomes &#064;Borrowed(this)
   * 
   * Target =
   *   TypeDecPat / FieldDecPat / MethodDecPat/ ConstructorDecPat /
   *   Target &quot;|&quot; Target /
   *   Target &quot;&amp;&quot; Target /
   *   [ &quot;!&quot; ] &quot;(&quot; Target &quot;)&quot;
   *   
   * TypeDecPat = [ Visibility ] ( QualifiedName / IdentifierPat [ InPackagePat ] )
   * FieldDecPat = [ Visibility ] [ Static ] TypeSigPat IdentifierPat [ InTypePat ]
   * MethodDecPat = [ Visibility ] [ Static ] IdentifierPat &quot;(&quot; [ ParameterSigPat ] &quot;)&quot; [ InTypePat ]
   * ConstructorDecPat = [ Visibility ] &quot;new&quot; &quot;(&quot; [ ParameterSigPat ] &quot;)&quot; [ InTypePat ]
   * 
   * TypeSigPat =
   *   &quot;*&quot; /
   *   ( &quot;boolean&quot; / &quot;char&quot; / &quot;byte&quot; / &quot;short&quot; /
   *     &quot;int&quot; / &quot;long&quot; / &quot;float&quot; / &quot;double&quot; / &quot;void&quot; /
   *     IdentifierPat / QualifiedName ) *( &quot;[]&quot; )
   * ParameterSigPat = ( TypeSigPat / &quot;**&quot; ) *(&quot;,&quot; ( TypeSigPat / &quot;**&quot; ) )
   * 
   * InNameExp =
   *   QualifiedNamePat /
   *   InNameExp &quot;|&quot; InNameExp /
   *   InNameExp &quot;&amp;&quot; InNameExp /
   *   [ &quot;!&quot; ] &quot;(&quot; InNameExp &quot;)&quot;
   * InNamePat =
   *   QualifiedNamePat /
   *   &quot;(&quot; InNameExp &quot;)&quot;
   * InPackagePat = &quot;in&quot; InNamePat
   * InTypePat    = &quot;in&quot; InNamePat [ InPackagePat ]
   * 
   * Visibility = &quot;public&quot; / &quot;protected&quot; / &quot;private&quot;
   * Static = [ &quot;!&quot; ] &quot;static&quot;
   * IdentifierPat = [ &quot;*&quot; ] ( Identifier *(&quot;*&quot; Identifier) ) [ &quot;*&quot; ]
   * QualifiedNamePat = ( IdentifierPat / &quot;*&quot; / &quot;**&quot; ) *(&quot;.&quot; ( IdentifierPat / &quot;*&quot; / &quot;**&quot; ) )
   * QualifiedName = Identifier *(&quot;.&quot; Identifier)
   * 
   * IDENTIFIER = Legal Java Identifier
   * </pre>
   */
  String value();
}
