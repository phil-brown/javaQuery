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
 * Declares a named set of types to be used in {@link MayReferTo} and
 * {@link Layer} annotations. A type set is a global entity, and may be referred
 * to by other annotations. Its name is qualified by the name of the package
 * that it annotates. If the reference is within the same package, the name does
 * not need to be qualified.
 * <p>
 * To place more than one {@link TypeSet} annotation on a package, use the
 * {@link TypeSets} annotation. It is a modeling error for a package to have
 * both a {@link TypeSet} and a {@link TypeSets} annotation.
 * 
 * <h3>Semantics:</h3>
 * 
 * Declaration of a type set does not constrain the implementation of the
 * program, it simply gives a name to a subset of the program's types.
 * 
 * <h3>Examples:</h3>
 * 
 * Declaring a type set that represents a set of packages. This annotation is
 * only permitted in a <code>package-info.java</code> file.
 * 
 * <pre>
 * &#064;TypeSet(&quot;IO = org.jdom+ | java.{io, net, util}&quot;)
 * package example;
 * </pre>
 * 
 * Declaring a type set that consists of most, but not all of a package
 * 
 * <pre>
 * &#064;TypeSet(&quot;UTIL = java.util &amp; !(java.util.{Enumeration, Hashtable, Vector}&quot;)
 * package example;
 * </pre>
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * <pre>
 * /**
 *  * @annotate TypeSet(&quot;IO = org.jdom+ | java.{io, net, util}&quot;)
 *  * @annotate TypeSet(&quot;UTIL = java.util &amp; !(java.util.{Enumeration, Hashtable, Vector}&quot;)
 *  &#42;/
 * package example;
 * </pre>
 * 
 * @see TypeSets
 */
@Documented
@Target(ElementType.PACKAGE)
public @interface TypeSet {
  /**
   * The named set of types. This set is declared using a constructive syntax
   * shared with the other annotations. The attribute is restricted to strings
   * that match the following grammar:
   * <p>
   * value = name "<tt>=</tt>" type_set_expr
   * <p>
   * type_set_expr = type_set_disjunct *("<tt>|</tt>" type_set_disjunct) <i>;
   * Set union</i>
   * <p>
   * type_set_disjunct = type_set_conjunct *("<tt>&</tt>" type_set_conjunct)
   * <i>; Set intersection</i>
   * <p>
   * type_set_conjunct = ["<tt>!</tt>"] type_set_leaf <i>; Set complement</i>
   * <p>
   * type_set_leaf = dotted_name <i>; Package name, layer name, type name, or
   * type set name</i> <br>
   * type_set_leaf /= dotted_name "<tt>+</tt>" <i>; Package tree</i> <br>
   * type_set_leaf /= dotted_name "<tt>.</tt>" "<tt>{</tt>" name *("<tt>,</tt>
   * " name) "<tt>}</tt>" <i>; Union of packages/types</i> <br>
   * type_set_leaf /= "<tt>(</tt>" type_set_expr "<tt>)</tt>"
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
