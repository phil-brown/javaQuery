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
 * The class to which this annotation is applied is a singleton, meaning it
 * has only one instance and provides a global point of access to that instance.
 * That is, the class is a Java implementation of the <i>Singleton pattern</i>
 * described by Gamma, Helm, Johnson, and Vlissides in <i>Design Patterns:
 * Elements of Reusable Object-Oriented Software</i> (Addison-Wesley 1995).
 * Several Java implementation patterns are supported. The supported singleton
 * implementation patterns are presented by Joshua Bloch in <i>Effective Java
 * (Second Edition)</i> (Addison-Wesley 2008) Item 1.
 * <p>
 * The recommended implementation approach, by Bloch, is to use the {@code enum}
 * singleton implementation pattern, this implies
 * <ul>
 * <li>the {@code enum} has only a single element that is recommended, but not
 * mandated, to be called {@code INSTANCE}.</li>
 * </ul>
 * This implementation approach is concise, handles serialization properly, and
 * will always have one instance&mdash;even when serialization or reflection are
 * used in an attempt to create multiple instances.
 * <p>
 * Three implementation approaches are allowed if the singleton is implemented
 * as a class. All require that the class is declared <tt>final</tt> and that
 * the constructor is <tt>private</tt>. Further, if the singleton class is
 * <i>serializable</i> then it must
 * <ul>
 * <li>implement <tt>Serializable</tt>,
 * <li>declare all instance fields <tt>transient</tt>, and
 * <li>provide a <tt>readResolve</tt> method that returns the singleton instance
 * (typically referenced in the <tt>static</tt> field <tt>INSTANCE</tt>).</li>
 * </ul>
 * The first implementation pattern uses a <tt>public static final</tt> field to
 * hold the singleton reference. The recommended, but not mandated, field name
 * is <tt>INSTANCE</tt>.
 * <p>
 * The second implementation pattern uses a <tt>private static final</tt> field
 * to hold the singleton reference and a <tt>public static</tt> method to obtain
 * the instance. The recommended, but not mandated, field name is
 * <tt>INSTANCE</tt>. The recommended, but not mandated, method name is
 * <tt>getInstance</tt>.
 * <p>
 * The third implementation pattern uses the <i>lazy initialization holder class
 * idiom</i> described by Joshua Bloch in <i>Effective Java (Second Edition)</i>
 * (Addison-Wesley 2008) Item 71. In this approach the singleton contains a
 * <tt>private static class</tt> that holds the reference to the singleton
 * object in a <tt>static final</tt> field. The recommended, but not mandated,
 * nested class name is <tt>LazyInitilizationHolder</tt>. The singleton contains
 * a <tt>public static</tt> method to obtain the instance. The recommended, but
 * not mandated, field name is <tt>INSTANCE</tt>. The recommended, but not
 * mandated, method name is <tt>getInstance</tt>.
 * <p>
 * Access to the singleton object via <tt>INSTANCE</tt> or <tt>getInstance</tt>
 * is multi-thread safe. Note that this does not mean that the singleton's state is
 * thread safe&mdash;it may be or it may not be&mdash;just that access to the
 * reference is safely shared if clients use <tt>INSTANCE</tt> or
 * <tt>getInstance</tt> to obtain the reference to the singleton.
 * <p>
 * It is a modeling error to apply this annotation to an interface.
 * 
 * <h3>Semantics:</h3>
 * 
 * The class to which this annotation is applied has one and only one instance
 * and provides a global point of access to that instance. Further, the class is
 * not allowed to be subclassed. The class may have mutable state.
 * 
 * <h3>Examples:</h3>
 * 
 * An example of the {@code enum} singleton implementation pattern is shown in
 * the code below.
 * 
 * <pre>
 * &#064;Singleton
 * public enum Elvis {
 * 	INSTANCE;
 * 
 * 	private int age;
 * 
 * 	public int getAge() {
 * 		return age;
 * 	}
 * 
 * 	public void setAge(int value) {
 * 		age = value;
 * 	}
 * }
 * </pre>
 * 
 * An example of the <tt>public static field</tt> singleton implementation
 * pattern is shown in the code below.
 * 
 * <pre>
 * &#064;Singleton
 * public final class Elvis {
 * 	public static final Elvis INSTANCE = new Elvis();
 * 
 * 	private Elvis() {
 * 		// only one
 * 	}
 * 
 * 	private int age;
 * 
 * 	public int getAge() {
 * 		return age;
 * 	}
 * 
 * 	public void setAge(int value) {
 * 		age = value;
 * 	}
 * }
 * </pre>
 * 
 * An example of the <tt>private static field</tt> singleton implementation
 * pattern is shown in the code below.
 * 
 * <pre>
 * &#064;Singleton
 * public final class Elvis {
 * 	private static final Elvis INSTANCE = new Elvis();
 * 
 * 	private Elvis() {
 * 		// only one
 * 	}
 * 
 * 	public static Elvis getInstance() {
 * 		return INSTANCE;
 * 	}
 * 
 * 	private int age;
 * 
 * 	public int getAge() {
 * 		return age;
 * 	}
 * 
 * 	public void setAge(int value) {
 * 		age = value;
 * 	}
 * }
 * </pre>
 * 
 * An example of a singleton that is serializable. This approach is supported,
 * but <i>not</i> recommended, use the <tt>enum</tt> implementation pattern
 * instead.
 * 
 * <pre>
 * &#064;Singleton
 * public final class Elvis implements Serializable {
 * 	private static final long serialVersionUID = -5264712062432607599L;
 * 
 * 	private Object readResolve() {
 * 		return INSTANCE;
 * 	}
 * 
 * 	private static final Elvis INSTANCE = new Elvis();
 * 
 * 	private Elvis() {
 * 		// singleton
 * 	}
 * 
 * 	public static Elvis getInstance() {
 * 		return INSTANCE;
 * 	}
 * 
 * 	private transient int age;
 * 
 * 	public int getAge() {
 * 		return age;
 * 	}
 * 
 * 	public void setAge(int value) {
 * 		age = value;
 * 	}
 * }
 * </pre>
 * 
 * An example of the lazy initialization singleton implementation pattern is
 * shown in the code below.
 * 
 * <pre>
 * &#064;Singleton
 * public final class Elvis {
 * 
 * 	private static class LazyInitilizationHolder {
 * 		private static final Elvis INSTANCE = new Elvis();
 * 	}
 * 
 * 	private Elvis() {
 * 		// only one
 * 	}
 * 
 * 	public static Elvis getInstance() {
 * 		return LazyInitilizationHolder.INSTANCE;
 * 	}
 * 
 * 	private int age;
 * 
 * 	public int getAge() {
 * 		return age;
 * 	}
 * 
 * 	public void setAge(int value) {
 * 		age = value;
 * 	}
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
 *  * &#064;annotate Singleton
 *  *&#047;
 * public final class Elvis {
 *   ...
 * }
 * </pre>
 */
@Documented
@Target(ElementType.TYPE)
public @interface Singleton {
  // Marker annotation
}
