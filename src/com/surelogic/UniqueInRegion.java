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
 * Declares that the field to which this annotation is applied is a unique
 * reference to an object. Normally this indicates that the referenced object is
 * not aliased. It is allowed, however, to pass a unique reference to a method
 * as an actual argument or the receiver if formal argument or receiver,
 * respectively, is {@link Borrowed}. That is, {@link Unique} values can be
 * safely passed to the parameter or used as the receiver with the guarantee
 * that they will still be unique when the method returns. Said another way we
 * create a temporary alias on the stack then ensure that it goes away.
 * <p>
 * Setting the <tt>allowRead=true</tt> attribute weakens the uniqueness guarantees:
 * it permits there to be <code>&#64;ReadOnly</code> aliases around.
 * In other words, only the ability to write (mutate) is restricted to this alias.
 * Once a unique reference has granted <tt>allowRead</tt>, there is no going back,
 * unlike with (temporary) borrowed references.  <em>This attribute is not currently 
 * checked by analysis</em>.
 * <p>
 * This annotation differs from {@link Unique} only with regard to the region
 * the state referenced by the annotated field is mapped into. This annotation
 * declares that the {@code Instance} region of the object referenced by the
 * annotated field is mapped into a named region of the object that contains the
 * annotated field. {@link Unique} maps the state into the {@code Instance}
 * region of the object that contains the annotated field if the field is 
 * {@code final}; into the field itself if the field is not {@code final}.
 * Therefore, the two
 * annotations below on the {@code friends} field are equivalent.
 * 
 * <pre>
 * &#064;Unique
 * private final Set&lt;Person&gt; friends = new HashSet&lt;Person&gt;();
 * 
 * &#064;UniqueInRegion(&quot;Instance&quot;)
 * private final Set&lt;Person&gt; friends = new HashSet&lt;Person&gt;();
 * </pre>
 * 
 * In addition, a more complex syntax where regions of the object referenced
 * by the annotated field are allowed to be explicitly mapped to regions of the 
 * object that contains the annotated field. Using this syntax the annotation below is
 * equivalent to the two previous examples.
 * 
 * <pre>
 * &#064;UniqueInRegion(&quot;Instance into Instance&quot;)
 * private final Set&lt;Person&gt; friends = new HashSet&lt;Person&gt;();
 * </pre>
 * 
 * This syntax should be rare in practice, however we show an example of its use
 * in the Examples section below.
 * <p>
 * In the case that the annotated field is non-{@code final} the field and the
 * {@code Instance} region of the object referenced by the annotated field are
 * mapped into a named region of the object that contains the annotated field.
 * This situation is illustrated in the code below.
 * 
 * <pre>
 * &#064;Region(&quot;private ContactData&quot;)
 * class ContactData {
 * 
 *   &#064;UniqueInRegion(&quot;ContactData&quot;)
 *   private final Set&lt;Person&gt; friends = new HashSet&lt;Person&gt;()
 *   
 *   &#064;UniqueInRegion(&quot;ContactData&quot;)
 *   private Set&lt;Person&gt; family = new HashSet&lt;Person&gt;()
 *   ...
 * }
 * </pre>
 * 
 * In this example, the non-{@code final} {@code family} field is mapped into
 * the {@code ContactData} region but {@code final} {@code friends} field is
 * not. The state referenced by both fields is mapped into the
 * {@code ContactData} region.
 * 
 * 
 * <h3>Semantics:</h3>
 * 
 * At all times, the value of the annotated field is either <code>null</code> or
 * is an object that is not referenced by a field of any other object or another
 * field of the same object. The {@code Instance} region (and any named regions
 * if the more complex <tt>into</tt> syntax is used) of the object referenced by
 * the annotated field is mapped into a named region of the object that contains
 * the annotated field.
 * <p>
 * In the case that the annotated field is non-{@code final} the field and the
 * {@code Instance} region (and any named regions if the more complex
 * <tt>into</tt> syntax is used) of the object referenced by the annotated field
 * are mapped into a named region of the object that contains the annotated
 * field.
 * 
 * <h3>Examples:</h3>
 * 
 * The below class defines a region, called {@code ObserverRegion}, that
 * includes the integer {@code notifyCounter} and the state referenced by the
 * {@code observers} field (i.e., the internals of the {@code HashSet} object).
 * 
 * <pre>
 * &#064;Region(&quot;private ObserverRegion&quot;)
 * class Observer {
 * 
 *   &#064;InRegion(&quot;ObserverRegion&quot;)
 *   private int notifyCounter = 0;
 * 
 *   &#064;UniqueInRegion(&quot;ObserverRegion&quot;)
 *   private final Set&lt;Callback&gt; observers = new HashSet&lt;Callback&gt;()
 *   ...
 * }
 * </pre>
 * 
 * The same result can be accomplished with the more complex syntax shown in the
 * example below. However, in this case the more complex syntax is
 * <em>not recommended</em>.
 * 
 * <pre>
 * &#064;Region(&quot;private ObserverRegion&quot;)
 * class Observer {
 * 
 *   &#064;InRegion(&quot;ObserverRegion&quot;)
 *   private int notifyCounter = 0;
 * 
 *   &#064;UniqueInRegion(&quot;Instance into ObserverRegion&quot;)
 *   private final Set&lt;Callback&gt; observers = new HashSet&lt;Callback&gt;()
 *   ...
 * }
 * </pre>
 * 
 * The more complex syntax is necessary if only a portion of a referenced object
 * is to be aggregated is more complex. Consider the highly contrived example
 * below.
 * 
 * <pre>
 * &#064;Regions({
 *   &#064;Region(&quot;private OrderRegion&quot;),
 *   &#064;Region(&quot;private InvRegion&quot;) })
 * &#064;RegionLocks({
 *   &#064;RegionLock(&quot;ShopLock is this protects OrderRegion&quot;),
 *   &#064;RegionLock(&quot;InvLock  is inv  protects InvRegion&quot;) })
 * public class Shop {
 * 
 *   &#064;InRegion(&quot;OrderRegion&quot;)
 *   private int ordersPlaced;
 * 
 *   private static class SandwichInventory {
 *     int bread;
 *     int mustard;
 *     int ordersPlaced;
 *   }
 * 
 *   &#064;UniqueInRegion(&quot;bread into InvRegion, mustard into InvRegion, ordersPlaced into OrderRegion, Instance into Instance&quot;)
 *   private final SandwichInventory inv = new SandwichInventory();
 * 
 *   void order() {
 *     synchronized (inv) {
 *       inv.bread++;
 *       inv.mustard++;
 *     }
 *     synchronized (this) {
 *       ordersPlaced++;
 *       inv.ordersPlaced++;
 *     }
 *   }
 *   ...
 * }
 * </pre>
 * 
 * In the above example the fields that count the number of orders placed are
 * protected by synchronizing on {@code this} while the inventory fields (
 * {@code bread} and {@code mustard}) are protected by synchronizing on
 * {@code inv}. All the state of the {@code SandwichInventory} must be placed
 * into named regions (even if you never use one of the named regions, e.g., you
 * create a <em>JunkRegion</em>) and the aggregation must be completed with a
 * mapping of the region {@code Instance} of the referenced object, usually
 * {@code Instance into Instance}. Finally, it is important in this example that
 * the field {@code inv} is declared to be {@code final}&mdash;otherwise the
 * field itself would have to be mapped into each of the named regions, which,
 * in this case, would be illegal because while {@code inv} could be mapped into
 * {@code InvRegion} or {@code OrderRegion}, it cannot be mapped into both.
 * <p>
 * Models of the sort shown for the {@code Shop} class above are discouraged and
 * should occur rarely in Java code that follows object-oriented design
 * principles.
 * 
 * 
 * <h3>Javadoc usage notes:</h3>
 * 
 * This annotation may placed in Javadoc, which can be useful for Java 1.4 code
 * which does not include language support for annotations, via the
 * <code>&#064;annotate</code> tag.
 * 
 * <pre>
 * /**
 *  * &#064;annotate Region(&quot;private ObserverRegion&quot;)
 *  *&#047;
 * class Observer {
 * 
 *   /**
 *    * &#064;annotate InRegion(&quot;ObserverRegion&quot;)
 *    *&#047;
 *   private int notifyCounter = 0;
 * 
 *   /**
 *    * &#064;annotate UniqueInRegion(&quot;ObserverRegion&quot;)
 *    *&#047;
 *   private final Set&lt;Callback&gt; observers = new HashSet&lt;Callback&gt;()
 *   ...
 * }
 * </pre>
 * 
 * @see Region
 * @see Unique
 */
@Documented
@Target(ElementType.FIELD)
public @interface UniqueInRegion {
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
  String value();
  
  /**
   * Setting the this attribute to <code>true</code> weakens the uniqueness
   * guarantees: it permits there to be <code>&#64;ReadOnly</code> aliases of
   * the referenced object.  In other words, only the ability to write (mutate)
   * is restricted to this alias.  <em>This attribute is not currently 
   * checked by analysis</em>.
   */
  public boolean allowRead() default false;
}
