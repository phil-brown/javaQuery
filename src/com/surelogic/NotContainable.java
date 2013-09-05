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
 * The class to which this annotation is applied is not containable. That is,
 * instances of the class can <i>not</i> be safely encapsulated via region
 * aggregation into other objects because methods of this class leak references
 * to themselves or other objects that they reference, transitively.
 * <p>
 * This annotation primarily exists for clarifying the non-containability of a
 * class that might otherwise be assumed to be containable, despite the fact
 * that it is a bad idea to assume a class is containable without good reason.
 * <p>
 * This annotation is trusted, i.e., it is <em>not verified</em>. Its use is for
 * documentation.
 * 
 * <p>A type may not be annotated with both <code>&#064;Containable</code>
 * and <code>&#064;NotContainable</code>.
 * 
 * <h3>Semantics:</h3>
 * 
 * Documenting that a type is not containable does not constrain the
 * implementation of the program, it simply clarifies the programmer's intent.
 * 
 * <h3>Examples:</h3>
 * 
 * The Swing panel <tt>ControlPanel</tt> listed below is not containable. The
 * implementation registers with the Swing framework to be called back when the
 * <tt>exit</tt> button is pressed. This registration aliases the
 * <tt>ControlPanel</tt> object and makes the implementation not containable.
 * 
 * <pre>
 * import java.awt.*;
 * import java.awt.event.*;
 * import javax.swing.*;
 * 
 * &#064;NotContainable
 * public final class ControlPanel extends JPanel implements ActionListener {
 * 
 *   public ControlPanel() {
 *     final JButton exit = new JButton(&quot;Press to Exit&quot;);
 *     add(exit, BorderLayout.CENTER);
 *     exit.addActionListener(this);
 *   }
 * 
 *   private boolean f_exitPressed = false;
 * 
 *   public void actionPerformed(ActionEvent e) {
 *     f_exitPressed = true;
 *   }
 * 
 *   public boolean exitPressed() {
 *     return f_exitPressed;
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
 * /**
 *  * &#064;annotate NotContainable
 *  *&#047;
 * public final class ControlPanel extends JPanel implements ActionListener {
 *   ...
 * }
 * </pre>
 * 
 * @see Containable
 */
@Documented
@Target(ElementType.TYPE)
public @interface NotContainable {
  // Marker annotation
}
