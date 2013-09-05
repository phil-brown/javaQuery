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
 * Constrains the set of types that are allowed to reference the annotated
 * program element. This annotation may target a type, a method, a constructor,
 * or a field.
 * 
 * <h3>Semantics:</h3>
 * 
 * Places a structural constraint on the program's implementation. Only
 * explicitly listed types are allowed to reference the annotated program
 * element.
 * 
 * <h3>Examples:</h3>
 * 
 * Declaring that a constructor can only be called from within a particular
 * type.
 * 
 * <pre>
 * package com.surelogic.smallworld.model;
 * 
 * public class Place {
 * 
 *   &#064;AllowsReferencesFrom(&quot;World&quot;)
 *   Place(World world, String name, String description) { ... }
 *   ...
 * }
 * </pre>
 * 
 * The constructor for the {@code Place} class shown above is allowed, by the
 * Java language, to be invoked from any type within the
 * {@code com.surelogic.smallworld.model} package. The
 * {@link AllowsReferencesFrom} annotation restricts this visibility further to
 * only the {@code World} class in the same package.
 * 
 * <pre>
 * public class Aircraft {
 * 
 *   &#064;AllowsReferencesFrom(&quot;org.controls.Altimeter&quot;)
 *   public double altitude;
 *   ...
 * }
 * </pre>
 * 
 * The publicly-visible <tt>altitude</tt> field is constrained to only be
 * accessed from the <tt>org.controls.Altimeter</tt> class.
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation may placed in Javadoc, which can be useful for Java 1.4 code
 * which does not include language support for annotations, via the
 * <code>&#064;annotate</code> tag.
 * 
 * <pre>
 * public class Place {
 * 
 *   /**
 *    * @annotate AllowsReferencesFrom(&quot;World&quot;)
 *    &#42;/
 *   Place(World world, String name, String description) { ... }
 *   ...
 * }
 * </pre>
 * 
 * @see MayReferTo
 * @see TypeSet
 */
@Documented
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
public @interface AllowsReferencesFrom {
  /**
   * The set of types that may refer to the type. This set is declared using a
   * constructive syntax shared with several other annotations. The attribute is
   * restricted to strings that match the following grammar:
   * 
   * <p>
   * value = type_set_expr
   * 
   * <p>
   * type_set_expr = type_set_disjunct *("<tt>|</tt>" type_set_disjunct) <i>;
   * Set union</i>
   * 
   * <p>
   * type_set_disjunct = type_set_conjunct *("<tt>&</tt>" type_set_conjunct)
   * <i>; Set intersection</i>
   * 
   * <p>
   * type_set_conjunct = ["<tt>!</tt>"] type_set_leaf <i>; Set complement</i>
   * 
   * <p>
   * type_set_leaf = dotted_name <i>; Package name, layer name, type name, or
   * type set name</i> <br>
   * type_set_leaf /= dotted_name "<tt>+</tt>" <i>; Package tree</i> <br>
   * type_set_leaf /= dotted_name "<tt>.</tt>" "<tt>{</tt>" name *("<tt>,</tt>
   * " name) "<tt>}</tt>" <i>; Union of packages/types</i> <br>
   * type_set_leaf /= "<tt>(</tt>" type_set_expr "<tt>)</tt>"
   * 
   * <p>
   * The union, intersection, and complement operators, as well as the
   * parentheses have the obvious meanings, and standard precedence order. A
   * package name signifies all the types in that package; a named type
   * indicates a specific type. A named layer stands for all the types in the
   * layer. A named type set stands for the type set specified by the given
   * name, as defined by a {@code @TypeSet} annotation. The package tree suffix
   * "<tt>+</tt>" indicates that all the types in the package and its
   * subpackages are part of the set. The braces "<tt>{</tt>" "<tt>}</tt>" are
   * syntactic sugar used to enumerate a union of packages/types that share the
   * same prefix.
   */
  public String value();
}
