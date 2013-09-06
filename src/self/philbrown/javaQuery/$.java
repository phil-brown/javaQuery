/*
 * Copyright 2013 Phil Brown
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package self.philbrown.javaQuery;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpUriRequest;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.TimingTargetAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import self.philbrown.javaQuery.SwipeDetector.SwipeListener;
import self.philbrown.javaQuery.animation.AccelerateDecelerateInterpolator;
import self.philbrown.javaQuery.animation.AccelerateInterpolator;
import self.philbrown.javaQuery.animation.AnimatorSet;
import self.philbrown.javaQuery.animation.AnticipateInterpolator;
import self.philbrown.javaQuery.animation.AnticipateOvershootInterpolator;
import self.philbrown.javaQuery.animation.BounceInterpolator;
import self.philbrown.javaQuery.animation.DecelerateInterpolator;
import self.philbrown.javaQuery.animation.LinearInterpolator;
import self.philbrown.javaQuery.animation.OvershootInterpolator;

/**
 * Partial port of jQuery to Java
 * @author Phil Brown
 * @since 1:20:41 PM Aug 28, 2013
 *
 */
public class $ 
{

	
	/**
	 * Data types for <em>ajax</em> request responses
	 */
	public static enum DataType
	{
		/** JavaScript Object Notation */
		JSON, 
		/** Extensible Markup Language */
		XML, 
		/** Textual response */
		TEXT, 
		/** Bourne Script response*/
		SCRIPT
	}
	
	/**
	 * Relates to the interpolator used for <em>javaQuery</em> animations
	 */
	public static enum Easing
	{
		/** Rate of change starts out slowly and then accelerates. */
		ACCELERATE,
		/** Rate of change starts and ends slowly but accelerates through the middle. */
		ACCELERATE_DECELERATE,
		/** change starts backward then flings forward. */
		ANTICIPATE,
		/** change starts backward, flings forward and overshoots the target value, then finally goes back to the final value. */
		ANTICIPATE_OVERSHOOT,
		/** change bounces at the end. */
		BOUNCE,
		/** Rate of change starts out quickly and and then decelerates. */
		DECELERATE,
		/** Rate of change is constant. */
		LINEAR,
		/** change flings forward and overshoots the last value then comes back. */
		OVERSHOOT
	}
	
	/** Used to correctly call methods that use simple type parameters via reflection */
	private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_MAP = buildPrimitiveTypeMap();

	/** Inflates the mapping of data types to primitive types */
	private static Map<Class<?>, Class<?>> buildPrimitiveTypeMap()
	{
	    Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
	    map.put(Float.class, float.class);
	    map.put(Double.class, double.class);
	    map.put(Integer.class, int.class);
	    map.put(Boolean.class, boolean.class);
	    map.put(Long.class, long.class);
	    map.put(Short.class, short.class);
	    map.put(Byte.class, byte.class);
	    return map;
	}
	
	/** 
	 * Optional data referenced by this javaQuery Object. Best practice is to make this a 
	 * {@link WeakReference} to avoid memory leaks.
	 */
	private Object data;
	
	/** The current views that will be manipulated */
	private List<Component> views;
	/** The lowest level view registered with {@code this} javaQuery. */
	private Component rootView;
	/** Function to be called when this{@link #view} gains focus */
	private Function onFocus;
	/** Function to be called when this {@link #view} no longer has focus. */
	private Function offFocus;
	/** Function to be called when a key is pressed down when this {@link #view} has the focus. */
	private Function keyDown;
	/** Function to be called when a key is pressed when this {@link #view} has the focus. */
	private Function keyPress;
	/** Function to be called when a key is released when this {@link #view} has the focus. */
	private Function keyUp;
	/** Function to be called when a swipe event is captured by this {@link #view}. */
	private Function swipe;
	/** Function to be called when a swipe-up event is captured by this {@link #view}. */
	private Function swipeUp;
	/** Function to be called when a swipe-down event is captured by this {@link #view}. */
	private Function swipeDown;
	/** Function to be called when a swipe-left event is captured by this {@link #view}. */
	private Function swipeLeft;
	/** Function to be called when a swipe-right event is captured by this {@link #view}. */
	private Function swipeRight;
	/** Used to detect swipes on this {@link #view}. */
	private SwipeDetector swiper;
	
	/** Contains a mapping of {@code javaQuery} extensions. */
	private static Map<String, Constructor<?>> extensions = new HashMap<String, Constructor<?>>();
	
	
	public $(Component view)
	{
		
		this.views = new ArrayList<Component>();
		this.views.add(view);
		this.rootView = view;
	}
	
	public $(List<Component> views)
	{
		if (views == null)
		{
			throw new NullPointerException("Cannot create javaQuery Instance with null List.");
		}
		else if (views.isEmpty())
		{
			throw new NullPointerException("Cannot create javaQuery Instance with empty List.");
		}
		this.views = views;
		this.rootView = views.get(0);
	}
	
	public $(Component... views)
	{
		if (views == null)
		{
			throw new NullPointerException("Cannot create javaQuery Instance with null Array.");
		}
		else if (views.length == 0)
		{
			throw new NullPointerException("Cannot create javaQuery Instance with empty Array.");
		}
		this.rootView = views[0];
		this.views = Arrays.asList(views);
	}
	
	public static $ with(Component view)
	{
		return new $(view);
	}
	
	public static $ with(List<Component> views)
	{
		return new $(views);
	}
	
	public static $ with(Component... views)
	{
		return new $(views);
	}
	
	private Component findComponentWithName(String name)
	{
		return findComponentWithName(name, rootView);
	}
	
	/**
	 * Finds a component that is identified by the given name
	 * @param name
	 * @return the found component, or {@code null} if it was not found.
	 */
	public static Component findComponentWithName(String name, Component root)//FIXME: debug to ensure this is working!
	{
		if (root.getName() != null && root.equals(name))
			return root;
		else if (root instanceof Container)
		{
			for (Component c : ((Container) root).getComponents())
			{
				Component found = findComponentWithName(name, c);
				if (found != null)
				{
					return found;
				}
			}
		}
		return null;
	}
	
	/** Sets the set of views to the parents of all currently-selected views */
	public $ parent()
	{
		List<Component> _views = new ArrayList<Component>();
		for (Component view : this.views)
		{
			Component parent = view.getParent();
			if (parent != null && !_views.contains(parent))
			{
				_views.add(parent);
			}
		}
		this.views.clear();
		this.views = _views;
		return this;
	}
	
	/** Sets the set of views to the children of the current set of views with the given child index */
	public $ child(int index)
	{
		List<Component> _views = new ArrayList<Component>();
		for (Component view : views)
		{
			if (view instanceof Container)
			{
				Component v = ((Container) view).getComponent(index);
				if (v != null)
					_views.add(v);
			}
		}
		views.clear();
		views = _views;
		
		return this;
	}

	
	/**
	 * Refreshes the listeners for focus changes
	 */
	private void setupFocusListener()
	{
		for (final Component view : views)
		{
			view.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent event) {
					if (onFocus != null)
						onFocus.invoke($.with(view));
				}

				@Override
				public void focusLost(FocusEvent event) {
					if (offFocus != null)
						offFocus.invoke($.with(view));
				}
				
			});
		}
	}
	
	/**
	 * Refreshes the listeners for key events
	 */
	private void setupKeyListener()
	{
		for (final Component view : views)
		{
			view.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent event) {
					if (keyDown != null)
						keyDown.invoke($.with(view), event.getKeyCode(), event);
				}

				@Override
				public void keyReleased(KeyEvent event) {
					if (keyUp != null)
						keyUp.invoke($.with(view), event.getKeyCode(), event);
				}

				@Override
				public void keyTyped(KeyEvent event) {
					if (keyPress != null)
						keyPress.invoke($.with(view), event.getKeyCode(), event);
				}
				
			});
		}
	}
	
	/**
	 * Refreshes the listeners for swipe events
	 */
	private void setupSwipeListener()
	{
		for (Component view : views)
		{
			view.addMouseListener(new SwipeDetector(view, new SwipeListener() {

				@Override
				public void onUpSwipe(Component v) {
					if (swipeUp != null)
					{
						swipeUp.invoke($.with(v));
					}
					else if (swipe != null)
					{
						swipe.invoke($.with(v), SwipeDetector.Direction.UP);
					}
				}

				@Override
				public void onRightSwipe(Component v) {
					if (swipeRight != null)
					{
						swipeRight.invoke($.with(v));
					}
					else if (swipe != null)
					{
						swipe.invoke($.with(v), SwipeDetector.Direction.RIGHT);
					}
				}

				@Override
				public void onLeftSwipe(Component v) {
					if (swipeLeft != null)
					{
						swipeLeft.invoke($.with(v));
					}
					else if (swipe != null)
					{
						swipe.invoke($.with(v), SwipeDetector.Direction.LEFT);
					}
				}

				@Override
				public void onDownSwipe(Component v) {
					if (swipeDown != null)
					{
						swipeDown.invoke($.with(v));
					}
					else if (swipe != null)
					{
						swipe.invoke($.with(v), SwipeDetector.Direction.DOWN);
					}
				}

				@Override
				public void onStartSwipe(Component v) {
					if (swipe != null)
					{
						swipe.invoke($.with(v), SwipeDetector.Direction.START);
					}
				}

				@Override
				public void onStopSwipe(Component v) {
					if (swipe != null)
					{
						swipe.invoke($.with(v), SwipeDetector.Direction.STOP);
					}
				}
				
			}));
		}
	}
	
	////Effects
	
	/**
	 * Animates the selected views using the JSON properties, the given duration, the easing function,
	 * and with the onComplete callback
	 * @param properties JSON String of an {@link AnimationOptions} Object
	 * @param duration the duration of the animation, in milliseconds
	 * @param easing the Easing function to use
	 * @param complete the Function to invoke once the animation has completed for all views
	 * @return this
	 * @see Easing
	 * @see #animate(Map, long, Easing, Function)
	 * @see #animate(String, AnimationOptions)
	 * @see #animate(Map, AnimationOptions)
	 */
	public $ animate(String properties, long duration, Easing easing, Function complete)
	{
		return animate(properties, AnimationOptions.create().duration(duration).easing(easing).complete(complete));
	}
	
	/**
	 * Animate the current views. Example:
	 * <pre>
	 * $.with(mView).animate("{
	 *                           left: 1000px,
	 *                           top: 0%,
	 *                           width: 50%,
	 *                           alpha: 0.5
	 *                        }", 
	 *                        new AnimationOptions("{ duration: 3000,
	 *                                                easing: linear
	 *                                            }").complete(new Function() {
	 *                        						public void invoke($ javaQuery, Object... args) {
	 *                        							javaQuery.alert("Animation Complete!");
	 *                        						}
	 *                        					  });
	 * </pre>
	 * @param properties to animate, in CSS representation
	 * @param options the {@link AnimationOptions} for the animation
	 * @return this
	 */
	public $ animate(String properties, AnimationOptions options)
	{
		try
		{
			JSONObject props = new JSONObject(properties);
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = props.keys();
			Map<String, Object> map = new HashMap<String, Object>();
			while (iterator.hasNext()) {
		        String key = iterator.next();
		        try {
		            Object value = props.get(key);
		            
		            map.put(key, value);
		            
		        } catch (JSONException e) {
		        	Log.w("javaQuery", "Cannot handle CSS String. Some values may not be animated.");
		        }
		    }
			return animate(map, options);
		} catch (JSONException e)
		{
			Log.w("javaQuery", "Cannot handle CSS String. Unable to animate.");
			return this;
		}
	}
	
	/**
	 * Animate the currently selected views
	 * @param properties mapping of {@link AnimationOptions} attributes
	 * @param duration the length of time for the animation to last
	 * @param easing the Easing to use to interpolate the animation
	 * @param complete the Function to call once the animation has completed or has been canceled.
	 * @return this
	 * @see QuickMap
	 */
	public $ animate(Map<String, Object> properties, long duration, Easing easing, final Function complete)
	{
		return animate(properties, AnimationOptions.create().duration(duration).easing(easing).complete(complete));
	}
	
	/**
	 * This reusable chunk of code can set up the given animation using the given animation options
	 * @param options the options used to manipulate how the animation behaves
	 * @return the container for placing views that will be animated using the given options
	 */
	private AnimatorSet animationWithOptions(final AnimationOptions options, List<Animator> animators)
	{
		AnimatorSet animation = new AnimatorSet();
		animation.playTogether(animators);
		animation.setDuration(options.duration());
		animation.addTarget(new TimingTarget() {

			@Override
			public void begin(Animator source) {}

			@Override
			public void cancel(Animator animation) {
				if (options.fail() != null)
					options.fail().invoke($.this);
				if (options.complete() != null)
					options.complete().invoke($.this);
			}
			
			@Override
			public void end(Animator source) {
				if (options.success() != null)
					options.success().invoke($.this);
				if (options.complete() != null)
					options.complete().invoke($.this);
			}

			@Override
			public void repeat(Animator source) {}

			@Override
			public void reverse(Animator source) {}

			@Override
			public void timingEvent(Animator source, double fraction) {}
			
		});
		animation.addTarget(new TimingTarget(){

			@Override
			public void cancel(Animator animation) {
				if (options.fail() != null)
					options.fail().invoke($.this);
				if (options.complete() != null)
					options.complete().invoke($.this);
			}

			@Override
			public void end(Animator animation) {
				if (options.success() != null)
					options.success().invoke($.this);
				if (options.complete() != null)
					options.complete().invoke($.this);
			}

			@Override
			public void repeat(Animator animation) {}

			@Override
			public void begin(Animator animation) {}

			@Override
			public void reverse(Animator source) {}

			@Override
			public void timingEvent(Animator source, double fraction) {}
			
		});
		Interpolator interpolator = null;
		if (options.easing() == null)
			options.easing(Easing.LINEAR);
		final Easing easing = options.easing();
		switch(easing)
		{
		case ACCELERATE : {
			interpolator = new AccelerateInterpolator();
			break;
		}
		case ACCELERATE_DECELERATE : {
			interpolator = new AccelerateDecelerateInterpolator();
			break;
		}
		case ANTICIPATE : {
			interpolator = new AnticipateInterpolator();
			break;
		}
		case ANTICIPATE_OVERSHOOT : {
			interpolator = new AnticipateOvershootInterpolator();
			break;
		}
		case BOUNCE : {
			interpolator = new BounceInterpolator();
			break;
		}
		case DECELERATE : {
			interpolator = new DecelerateInterpolator();
			break;
		}
		case OVERSHOOT : {
			interpolator = new OvershootInterpolator();
			break;
		}
		//linear is default.
		case LINEAR :
		default :
			interpolator = new LinearInterpolator();
			break;
		}
		
		//allow custom interpolator
		if (options.specialEasing() != null)
			interpolator = options.specialEasing();
		
		animation.setInterpolator(interpolator);
		
		return animation;
	}
	
	/**
	 * Interprets the CSS-style String and sets the value
	 * @param view the view that will change.
	 * @param key the name of the attribute
	 * @param _value the end animation value
	 * @return the computed value
	 */
	private Object getAnimationValue(Component view, String key, String _value)
	{
		Object value = null;
		
		String[] split = (_value).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		if (split.length == 1)
		{
			if (split[0].contains("."))
			{
				value = Float.valueOf(split[0]);
			}
			else
			{
				value = Integer.valueOf(split[0]);
			}
		}
		else
		{
			if (split.length > 2)
			{
				Log.w("javaQuery", "parsererror for key " + key);
				return null;
			}
			if (split[1].equalsIgnoreCase("px"))
			{
				//this is the default. Just determine if float or int
				if (split[0].contains("."))
				{
					value = Float.valueOf(split[0]);
				}
				else
				{
					value = Integer.valueOf(split[0]);
				}
			}
			else if (split[1].equalsIgnoreCase("dip") || split[1].equalsIgnoreCase("dp") || split[1].equalsIgnoreCase("sp"))
			{
				Log.w("$", "Dimension not supported");
				if (split[0].contains("."))
				{
					value = Float.valueOf(split[0]);
				}
				else
				{
					value = Integer.valueOf(split[0]);
				}
			}
			else if (split[1].equalsIgnoreCase("in"))
			{
				float pt = view(0).getGraphics().getFontMetrics().getFont().deriveFont(1).getSize2D()/72;
				if (split[0].contains("."))
				{
					value = Float.parseFloat(split[0])*pt;
				}
				else
				{
					value = Integer.parseInt(split[0])*pt;
				}
			}
			else if (split[1].equalsIgnoreCase("mm"))
			{
				float pt = view(0).getGraphics().getFontMetrics().getFont().deriveFont(1).getSize2D()/72;
				if (split[0].contains("."))
				{
					value = Float.parseFloat(split[0])*pt/25.4;
				}
				else
				{
					value = Integer.parseInt(split[0])*pt/25.4;
				}
			}
			else if (split[1].equalsIgnoreCase("pt"))
			{
				if (split[0].contains("."))
				{
					value = view(0).getGraphics().getFontMetrics().getFont().deriveFont(Float.parseFloat(split[0])).getSize2D();
				}
				else
				{
					value = view(0).getGraphics().getFontMetrics().getFont().deriveFont(Integer.parseInt(split[0])).getSize2D();
				}
			}
			else if (split[1].equals("%"))
			{
				Rectangle windowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				Component parent = view.getParent();
				float pixels = 0;
				if (parent == null)
				{
					pixels = windowBounds.width;
					//use best guess for width or height dpi
					if (split[0].equalsIgnoreCase("y") || split[0].equalsIgnoreCase("top") || split[0].equalsIgnoreCase("bottom"))
					{
						pixels = windowBounds.height;
					}
				}
				else
				{
					pixels = parent.getWidth();
					if (split[0].equalsIgnoreCase("y") || split[0].equalsIgnoreCase("top") || split[0].equalsIgnoreCase("bottom"))
					{
						pixels = parent.getHeight();
					}
				}
				float percent = 0;
				if (pixels != 0)
					percent = Float.valueOf(split[0])/100*pixels;
				if (split[0].contains("."))
				{
					value = percent;
				}
				else
				{
					value = (int) percent;
				}
			}
			else
			{
				Log.w("javaQuery", "invalid units for Object with key " + key);
				return null;
			}
		}
		return value;
	}
	
	/**
	 * Animate multiple view properties at the same time. Example:
	 * <pre>
	 * $.with(myView).animate(new QuickMap(QuickEntry.qe("alpha", .8f), QuickEntry.qe("width", 50%)), 400, Easing.LINEAR, null);
	 * </pre>
	 * @param properties mapping of property names and final values to animate
	 * @param options the options for setting the duration, easing, etc of the animation
	 * @return this
	 */
	public $ animate(Map<String, Object> properties, final AnimationOptions options)
	{
		List<Animator> animations = new ArrayList<Animator>();
		for (Entry<String, Object> entry : properties.entrySet())
		{
			final String key = entry.getKey();
			//Java sometimes will interpret these Strings as Numbers, so some trickery is needed below
			Object value = entry.getValue();
			
			for (final Component view : this.views)
			{
				Animator anim = null;
				if (value instanceof String)
					value = getAnimationValue(view, key, (String) value);
				if (value != null)
				{
					//special color cases
					if (key.equals("alpha") || key.equals("red") || key.equals("green") || key.equals("blue"))
					{
						if (key.equals("alpha")  && view instanceof JComponent)
						{
							((JComponent) view).setOpaque(false);
						}
						try
						{
							final Method getComponent = Color.class.getMethod(Log.buildString("get", capitalize(key)));
							final int colorComponent = (Integer) getComponent.invoke(view.getBackground());
							final ColorHelper color = new ColorHelper(view.getBackground());
							final Method setComponent = ColorHelper.class.getMethod(Log.buildString("set", capitalize(key)), new Class<?>[]{int.class});
							anim = new Animator();
							
							//if integer - assume 0-255
							if (value instanceof Integer || is(value, int.class))
							{
								anim.addTarget(PropertySetter.getTarget(color, key, colorComponent, Integer.parseInt(value.toString())));
							}
							//if float - assume 0.0-1.0
							else if (value instanceof Float || is(value, float.class))
							{
								anim.addTarget(PropertySetter.getTarget(color, key, colorComponent, (int) (255*Float.parseFloat(value.toString()))));
							}
							anim.addTarget(new TimingTargetAdapter(){

								@Override
								public void timingEvent(Animator source, double fraction) {
									double d = source.getInterpolator().interpolate(fraction);
									try {
										setComponent.invoke(color, (int) d);
										view.setBackground(color.getColor());
									} catch (Throwable t) {
										if (options.debug())
											t.printStackTrace();
									}
								}
							});
						}
						catch (Throwable t)
						{
							if (options.debug())
								t.printStackTrace();
						}
						
					}
					else
					{
						final Rectangle params = view.getBounds();
						try {
							final Field field = params.getClass().getField(key);
							if (field != null)
							{
								anim = new Animator();
								anim.addTarget(PropertySetter.getTarget(params, key, field.get(params), value));
								
								anim.addTarget(new TimingTargetAdapter(){

									@Override
									public void timingEvent(Animator source, double fraction) {
										Rectangle bounds = view.getBounds();
										double d = source.getInterpolator().interpolate(fraction);
										try {
											field.set(bounds, d);
										} catch (Throwable t) {
											if (options.debug())
												t.printStackTrace();
										}
										view.setBounds(bounds);

										if (options.progress() != null)
										{
											options.progress().invoke($.with(view), key, d, source.getDuration() - source.getTotalElapsedTime());
										}
									}
									
								});
							}
							
						} catch (Throwable t) {
							
							if (options.debug())
								Log.w("$", String.format(Locale.US, "%s is not a LayoutParams attribute.", key));
						}
						
						if (anim == null)
						{
							anim = new Animator();
							anim.addTarget(PropertySetter.getTarget(view, key, value));
							
							if (options.progress() != null)
							{
								anim.addTarget(new TimingTargetAdapter(){

									@Override
									public void timingEvent(Animator source, double fraction) {
										double d = source.getInterpolator().interpolate(fraction);
										if (options.progress() != null)
										{
											options.progress().invoke($.with(view), key, d, source.getDuration() - source.getTotalElapsedTime());
										}
									}
									
								});
							}
						}
					}
					
					anim.setRepeatCount(options.repeatCount());
					if (options.reverse())
						anim.setRepeatBehavior(Animator.RepeatBehavior.REVERSE);
					animations.add(anim);
				}
				
			}
		}
		AnimatorSet animation = animationWithOptions(options, animations);
		animation.start();
		
		return this;
	}

	/**
	 * Shortcut method for animating the alpha attribute of this {@link #view} to 1.0.
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fadeIn(AnimationOptions options)
	{
		this.animate(new QuickMap(QuickEntry.qe("alpha", new Float(1.0f))), options);
	}
	
	/**
	 * Shortcut method for animating the alpha attribute of the selected views to 1.0.
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void fadeIn(long duration, final Function complete)
	{
		fadeIn(new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * Shortcut method for animating the alpha attribute of the selected views to 0.0.
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fadeOut(AnimationOptions options)
	{
		this.animate(new QuickMap(QuickEntry.qe("alpha", new Float(0.0f))), options);
	}
	
	/**
	 * Shortcut method for animating the alpha attribute of this {@link #view} to 0.0.
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void fadeOut(long duration, final Function complete)
	{
		fadeOut(new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * Shortcut method for animating the alpha attribute of the selected views to the given value.
	 * @param opacity the alpha value at the end of the animation
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fadeTo(float opacity, AnimationOptions options)
	{
		this.animate(new QuickMap(QuickEntry.qe("alpha", new Float(opacity))), options);
	}
	
	/**
	 * Shortcut method for animating the alpha attribute of this {@link #view} to the given value.
	 * @param duration the length of time the animation should last
	 * @param opacity the alpha value at the end of the animation
	 * @param complete the function to call when the animation has completed
	 */
	public void fadeTo(long duration, float opacity, final Function complete)
	{
		fadeTo(opacity, new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * For each selected view, if its alpha is less than 0.5, it will fade in. Otherwise, it will
	 * fade out.
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void fadeToggle(long duration, final Function complete)
	{
		List<Component> zeros = new ArrayList<Component>();
		List<Component> ones = new ArrayList<Component>();
		for (Component view : this.views)
		{
			if (view.getGraphics().getColor().getAlpha() < 0.5)
				zeros.add(view);
			else
				ones.add(view);
		}
		$.with(zeros).fadeIn(duration, complete);
		$.with(ones).fadeOut(duration, complete);
	}
	
	/**
	 * If this {@link #view} has an alpha of less than 0.5, it will fade in. Otherwise, it will
	 * fade out.
	 * @param options use to modify the behavior of the animation
	 */
	public void fadeToggle(AnimationOptions options)
	{
		List<Component> zeros = new ArrayList<Component>();
		List<Component> ones = new ArrayList<Component>();
		for (Component view : this.views)
		{
			if (view.getGraphics().getColor().getAlpha() < 0.5)
				zeros.add(view);
			else
				ones.add(view);
		}
		$.with(zeros).fadeIn(options);
		$.with(ones).fadeOut(options);
	}
	
	/**
	 * Animates the selected views out of their parent views by sliding it down, past its bottom
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void slideDown(long duration, final Function complete)
	{
		slideDown(new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it down, past its bottom
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings("unchecked")
	public void slideDown(final AnimationOptions options)
	{
		for (final Component view : this.views)
		{
			Component parent = view.getParent();
			float y = 0;
			if (parent != null)
			{
				y = parent.getHeight();
			}
			else
			{
				Rectangle display = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				
				y = display.height;
			}
			$.with(view).animate(QuickMap.qm(QuickEntry.qe("y", y)), options);
		}
		
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it up, past its top
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void slideUp(long duration, final Function complete)
	{
		slideUp(new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it up, past its top
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings("unchecked")
	public void slideUp(final AnimationOptions options)
	{		
		animate(QuickMap.qm($.entry("y", 0f)), options);
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it right, past its edge
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void slideRight(long duration, final Function complete)
	{
		slideRight(new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it right, past its edge
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings("unchecked")
	public void slideRight(final AnimationOptions options)
	{		
		for (final Component view : this.views)
		{
			Component parent = view.getParent();
			float x = 0;
			if (parent != null)
			{
				x = parent.getWidth();
			}
			else
			{
				Rectangle display = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				x = display.height;
			}
			$.with(view).animate(QuickMap.qm($.entry("x", x)), options);
		}
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it left, past its edge
	 * @param duration the length of time the animation should last
	 * @param complete the function to call when the animation has completed
	 */
	public void slideLeft(long duration, final Function complete)
	{
		slideLeft(new AnimationOptions().duration(duration).complete(complete));
	}
	
	/**
	 * Animates the selected views out of its parent by sliding it left, past its edge
	 * @param options use to modify the behavior of the animation
	 */
	@SuppressWarnings("unchecked")
	public void slideLeft(final AnimationOptions options)
	{
		animate(QuickMap.qm($.entry("x", 0)), options);
	}
	
	/**
	 * Gets the value for the given attribute of the first view in the current selection. 
	 * This is done using reflection, and as such
	 * expects a <em>get-</em> or <em>is-</em> prefixed method name for the view.
	 * @param s the name of the attribute to retrieve
	 * @return the value of the given attribute name on the first view in the current selection
	 */
	public Object attr(String s)
	{
		try
		{
			Method m = view(0).getClass().getMethod("get" + capitalize(s));
			return m.invoke(view(0));
		}
		catch (Throwable t)
		{
			try
			{
				Method m = view(0).getClass().getMethod("is" + capitalize(s));
				return m.invoke(view(0));
			}
			catch (Throwable t2)
			{
				Log.w("javaQuery", view(0).getClass().getSimpleName() + "has no getter method for the variable " + s + ".");
				return null;
			}
		}
	}
	
	/**
	 * Sets the value of the given attribute on each view in the current selection. This is done 
	 * using reflection, and as such a <em>set-</em>prefixed method name for each view.
	 * @param s the name of the attribute to set
	 * @param o the value to set to the given attribute
	 * @return this
	 */
	public $ attr(String s, Object o)
	{
		for (Component view : this.views)
		{
			try
			{
				Class<?> objClass = o.getClass();
				Class<?> simpleClass = PRIMITIVE_TYPE_MAP.get(objClass);
				if (simpleClass != null)
				{
					objClass = simpleClass;
				}
				try {
					Method m = view.getClass().getMethod("set" + capitalize(s), new Class<?>[]{objClass});
					m.invoke(view, o);
				}
				catch (Throwable t) {
					//try using NineOldAndroids
					Log.w("javaQuery", view.getClass().getSimpleName() + ".set" + capitalize(s) + "(" + o.getClass().getSimpleName() + ") is not a method!");
				}
				
			}
			catch (Throwable t)
			{
				Log.w("javaQuery", view.getClass().getSimpleName() + ".set" + capitalize(s) + "(" + o.getClass().getSimpleName() + ") is not a method!");
			}
		}
		return this;
	}
	
	/**
	 * @return the view at the given index of the current selection
	 */
	public Component view(int index)
	{
		return this.views.get(index);
	}
	
	/**
	 * @return the current data.
	 */
	public Object data()
	{
		return this.data;
	}
	
	/**
	 * Sets the data associated with this javaQuery
	 * @param data the data to set
	 * @return this
	 */
	public $ data(Object data)
	{
		this.data = data;
		return this;
	}
	
	/**
	 * Adds a subview to the first view in the selection
	 * @param v the subview to add
	 * @return this
	 */
	public $ add(Component v)
	{
		if (v == null || v.getParent() != null)
		{
			Log.w("javaQuery", "Cannot add View");
			return this;
		}
		if (view(0) instanceof Container)
		{
			((Container) view(0)).add(v);
		}
		return this;
	}
	
	/**
	 * Removes a subview from the first view in the current selection
	 * @param v the subview to remove
	 * @return this
	 */
	public $ remove(Component v)
	{
		if (view(0) instanceof Container)
		{
			((Container) view(0)).remove(v);
		}
		return null;
	}
	
	/**
	 * Sets the visibility of the current selection to {@link View#VISIBLE}
	 * @return this
	 */
	public $ hide()
	{
		for (Component view : views)
		{
			view.setVisible(true);
		}
		return this;
	}
	
	/**
	 * Sets the visibility of this {@link #view} to {@link View#INVISIBLE}
	 * @return this
	 */
	public $ show()
	{
		for (Component view : views)
		{
			view.setVisible(false);
		}
		return this;
	}
	
	///Event Handler Attachment
	
	/**
	 * Listen for all events triggered using the {@link #notify(String)} or {@link #notify(String, Map)}
	 * method
	 * @param event the name of the event
	 * @param callback the function to call when the event is triggered.
	 * @see #listenToOnce(String, Function)
	 */
	public static void listenTo(String event, Function callback)
	{
		EventCenter.bind(event, callback, null);
	}
	
	/**
	 * Listen for the next event triggered using the {@link #notify(String)} or 
	 * {@link #notify(String, Map)} method
	 * @param event the name of the event
	 * @param callback the function to call when the event is triggered.
	 * @see #listenTo(String, Function)
	 */
	public static void listenToOnce(final String event, final Function callback)
	{
		EventCenter.bind(event, new Function() {

			@Override
			public void invoke($ javaQuery, Object... params) {
				callback.invoke(javaQuery, params);
				EventCenter.unbind(event, this, null);
			}
			
		}, null);
	}
	
	/**
	 * Stop listening for events triggered using the {@link #notify(String)} and
	 * {@link #notify(String, Map)} methods
	 * @param event the name of the event
	 * @param callback the function to no longer call when the event is triggered.
	 * @see #listenTo(String, Function)
	 */
	public static void stopListening(String event, Function callback )
	{
		EventCenter.unbind(event, callback, null);
	}
	
	/**
	 * Trigger a notification for functions registered to the given event String
	 * @param event the event string to which registered listeners will respond
	 * @see #listenTo(String, Function)
	 * @see #listenToOnce(String, Function)
	 */
	public $ notify(String event)
	{
		EventCenter.trigger(this, event, null, null);
		return this;
	}
	
	/**
	 * Trigger a notification for functions registered to the given event String
	 * @param event the event string to which registered listeners will respond
	 * @param data Object passed to the notified functions
	 * @see #listenTo(String, Function)
	 * @see #listenToOnce(String, Function)
	 */
	public $ notify(String event, Map<String, Object> data)
	{
		EventCenter.trigger(this, event, data, null);
		return this;
	}
	
	/**
	 * Defines an event type that is handled by {@link $#change(Function)}.
	 * @author Phil Brown
	 * @since 1:16:05 PM Sep 4, 2013
	 *
	 */
	public enum ChangeEvent
	{
		INPUT_CARET_POSITION_CHANGED(InputMethodEvent.class),
		INPUT_METHOD_TEXT_CHANGED(InputMethodEvent.class),
		HIERARCHY_ANCESTOR_MOVED(HierarchyEvent.class),
		HIERARCHY_ANCENSTOR_RESIZED(HierarchyEvent.class),
		HIERARCHY_CHANGED(HierarchyEvent.class),
		PROPERTY_CHANGED(PropertyChangeEvent.class),
		COMPONENT_HIDDEN(ComponentEvent.class),
		COMPONENT_MOVED(ComponentEvent.class),
		COMPONENT_RESIZED(ComponentEvent.class),
		COMPONENT_SHOWN(ComponentEvent.class),
		COMPONENT_ADDED(ContainerEvent.class),
		COMPONENT_REMOVED(ContainerEvent.class);
		
		/** Class of the event received by the callback function */
		private Class<?> eventClass;
		
		/**
		 * Constructor
		 * @param eventClass
		 */
		ChangeEvent(Class<?> eventClass)
		{
			this.eventClass = eventClass;
		}
		
		/** Get the class of the event received by the callback function. */
		public Class<?> getEventClass()
		{
			return eventClass;
		}
		
	};
	
	/**
	 * Registers common component changes
	 * @param function receives an instance of javaQuery with the changed view selected, as well as the
	 * following arguments:
	 * <ol>
	 * <li>{@link changeEvent} to define the type of event that was triggered
	 * <li>The associated event
	 * </ol>
	 * @return
	 * @see changeEvent
	 */
	public $ change(final Function function)
	{
		for (final Component component : views)
		{
			component.addInputMethodListener(new InputMethodListener() {

				@Override
				public void caretPositionChanged(InputMethodEvent event) {
					function.invoke($.with(component), ChangeEvent.INPUT_CARET_POSITION_CHANGED, event);
				}

				@Override
				public void inputMethodTextChanged(InputMethodEvent event) {
					function.invoke($.with(component), ChangeEvent.INPUT_METHOD_TEXT_CHANGED, event);
				}
				
			});
			
			component.addHierarchyBoundsListener(new HierarchyBoundsListener() {

				@Override
				public void ancestorMoved(HierarchyEvent event) {
					function.invoke($.with(component), ChangeEvent.HIERARCHY_ANCESTOR_MOVED, event);
				}

				@Override
				public void ancestorResized(HierarchyEvent event) {
					function.invoke($.with(component), ChangeEvent.HIERARCHY_ANCENSTOR_RESIZED, event);
				}
				
			});
			
			component.addHierarchyListener(new HierarchyListener() {

				@Override
				public void hierarchyChanged(HierarchyEvent event) {
					function.invoke($.with(component), ChangeEvent.HIERARCHY_CHANGED, event);
				}
				
			});
			
			component.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					function.invoke($.with(component), ChangeEvent.PROPERTY_CHANGED, event);
				}
			});
			
			component.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(ComponentEvent event) {
					function.invoke($.with(component), ChangeEvent.COMPONENT_HIDDEN, event);
				}

				@Override
				public void componentMoved(ComponentEvent event) {
					function.invoke($.with(component), ChangeEvent.COMPONENT_MOVED, event);
				}

				@Override
				public void componentResized(ComponentEvent event) {
					function.invoke($.with(component), ChangeEvent.COMPONENT_RESIZED, event);
				}

				@Override
				public void componentShown(ComponentEvent event) {
					function.invoke($.with(component), ChangeEvent.COMPONENT_SHOWN, event);
				}
				
			});
			
			if (component instanceof Container)
			{
				((Container) component).addContainerListener(new ContainerListener() {

					@Override
					public void componentAdded(ContainerEvent event) {
						function.invoke($.with(component), ChangeEvent.COMPONENT_ADDED, event);
					}

					@Override
					public void componentRemoved(ContainerEvent event) {
						function.invoke($.with(component), ChangeEvent.COMPONENT_REMOVED, event);
					}
					
				});
			}
		}
		return this;
	}
	

	
	/**
	 * Get the value associated with the first view in the current selection. If the view is a 
	 * JTextComponent, this method returns the String text. If it is a Button, the boolean selected 
	 * state is returned. If it is a JLable, the Icon is returned.
	 * @return the value of this view, or <em>null</em> if not applicable.
	 */
	public Object val()
	{
		if (view(0) instanceof JTextComponent)
		{
			return ((JTextComponent) view(0)).getText();
		}
		else if (view(0) instanceof AbstractButton)
		{
			return ((AbstractButton) view(0)).isSelected();
		}
		else if (view(0) instanceof JLabel)
		{
			return ((JLabel) view(0)).getIcon();
		}
		return null;
	}
	
	/**
	 * Set the value associated with the views in the current selection. If the view is a JTextComponent, 
	 * this method sets the String text. If it is a Button, the boolean selected state is set. 
	 * If it is an JLabel, the Icon (ImageIcon, BufferedImage, or String) is set. All other view types 
	 * are ignored.
	 * @return this
	 */
	public $ val(Object object)
	{
		for (Component view : this.views)
		{
			if (view instanceof JTextComponent && object instanceof String)
			{
				((JTextComponent) view).setText((String) object);
			}
			else if (view instanceof AbstractButton && object instanceof Boolean)
			{
				((AbstractButton) view).setSelected((Boolean) object);
			}
			else if (view instanceof JLabel)
			{
				if (object instanceof ImageIcon)
				{
					((JLabel) view).setIcon((ImageIcon) object);
				}
				else if (object instanceof String)
				{
					$.with(view).image((String) object);
				}
				else if (object instanceof Image)
				{
					((JLabel) view).setIcon(new ImageIcon((Image) object));
				}				
			}
			else if (view instanceof Window)
			{
				if (object instanceof Image)
					((Window) view).setIconImage((Image) object);
			}
		}
		
		return this;
	}
	

	/**
	 * Triggers a click event on the views in the current selection
	 * @return this
	 */
	public $ click()
	{
		for (Component view : this.views)
		{
			for (MouseListener ml : view.getMouseListeners())
			{
				ml.mousePressed(new MouseEvent(view, 0, 0, 0, view.getWidth()/2, view.getHeight()/2, 1, false));
			}
		}
		return this;
	}
	
	/**
	 * Invokes the given Function every time each view in the current selection is clicked. The only 
	 * parameter passed to the given function is a javaQuery instance containing the clicked view
	 * @param function the function to call when this view is clicked
	 * @return this
	 */
	public $ click(final Function function)
	{
		for (final Component view : this.views)
		{
			if (view instanceof AbstractButton)
			{
				((AbstractButton) view).addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event) {
						function.invoke($.with(view));
					}
					
				});
			}
			else
			{
				view.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						function.invoke($.with(view));
					}

					@Override
					public void mouseEntered(MouseEvent e) {}

					@Override
					public void mouseExited(MouseEvent e) {}

					@Override
					public void mousePressed(MouseEvent e) {}

					@Override
					public void mouseReleased(MouseEvent e) {}
					
				});
			}
			
		}
		return this;
	}
	
	/**
	 * Invokes the given Function for click events on each view in the current selection. 
	 * The function will receive two arguments:
	 * <ol>
	 * <li>a javaQuery containing the clicked view
	 * <li>{@code eventData}
	 * </ol>
	 * @param eventData the second argument to pass to the {@code function}
	 * @param function the function to invoke
	 * @return
	 */
	public $ click(final Object eventData, final Function function)
	{
		for (final Component view : this.views)
		{
			view.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					function.invoke($.with(view), eventData);
				}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}
				
			});
		}
		return this;
	}
	

	
	/**
	 * Triggers a long-click event on this each view in the current selection
	 * @return this
	 */
	public $ dblclick()
	{
		for (Component view : this.views)
		{
			for (MouseListener ml : view.getMouseListeners())
			{
				ml.mousePressed(new MouseEvent(view, 0, 0, 0, view.getWidth()/2, view.getHeight()/2, 2, false));
			}
		}
		return this;
	}
	
	/**
	 * Invokes the given Function every time each view in the current selection is long-clicked. 
	 * The only parameter passed to the given function a javaQuery instance with the long-clicked view.
	 * @param function the function to call when this view is long-clicked
	 * @return this
	 */
	public $ dblclick(final Function function)
	{
		for (final Component view : this.views)
		{
			view.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2)
						function.invoke($.with(view));
				}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}
				
			});
		}
		return this;
	}
	
	/**
	 * Invokes the given Function for long-click events on the views in the current selection. 
	 * The function will receive two arguments:
	 * <ol>
	 * <li>a javaQuery containing the long-clicked view
	 * <li>{@code eventData}
	 * </ol>
	 * @param eventData the second argument to pass to the {@code function}
	 * @param function the function to invoke
	 * @return
	 */
	public $ dblclick(final Object eventData, final Function function)
	{
		for (final Component view : this.views)
		{
			view.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2)
						function.invoke($.with(view), eventData);
				}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}
				
			});
		}
		return this;
	}
	

	/**
	 * Handles swipe events. This will override any onTouchListener added.
	 * @param function will receive this javaQuery and a {@link SwipeDetector.Direction} corresponding
	 * to the direction of the swipe.
	 * @return this
	 */
	public $ swipe(Function function)
	{
		swipe = function;
		setupSwipeListener();
		return this;
	}
	
	/**
	 * Sets the function that is called when the user swipes Up. This will cause any function set by
	 * {@link #swipe(Function)} to not get called for up events.
	 * @param function the function to invoke
	 * @return this
	 */
	public $ swipeUp(Function function)
	{
		swipeUp = function;
		setupSwipeListener();
		return this;
	}
	
	/**
	 * Sets the function that is called when the user swipes Left. This will cause any function set by
	 * {@link #swipe(Function)} to not get called for left events.
	 * @param function the function to invoke
	 * @return this
	 */
	public $ swipeLeft(Function function)
	{
		swipeLeft = function;
		setupSwipeListener();
		return this;
	}
	
	/**
	 * Sets the function that is called when the user swipes Down. This will cause any function set by
	 * {@link #swipe(Function)} to not get called for down events.
	 * @param function the function to invoke
	 * @return this
	 */
	public $ swipeDown(Function function)
	{
		swipeDown = function;
		setupSwipeListener();
		return this;
	}
	
	/**
	 * Sets the function that is called when the user swipes Right. This will cause any function set by
	 * {@link #swipe(Function)} to not get called for right events.
	 * @param function the function to invoke
	 * @return this
	 */
	public $ swipeRight(Function function)
	{
		swipeRight = function;
		setupSwipeListener();
		return this;
	}
	
	/**
	 * Triggers a swipe-up event on the current selection
	 * @return this
	 */
	public $ swipeUp()
	{
		if (swiper != null)
			swiper.performSwipeUp();
		return this;
	}
	
	/**
	 * Triggers a swipe-down event on the current selection
	 * @return this
	 */
	public $ swipeDown()
	{
		if (swiper != null)
			swiper.performSwipeDown();
		return this;
	}
	
	/**
	 * Triggers a swipe-left event on the current selection
	 * @return this
	 */
	public $ swipeLeft()
	{
		if (swiper != null)
			swiper.performSwipeLeft();
		return this;
	}
	
	/**
	 * Triggers a swipe-right event on the current selection
	 * @return this
	 */
	public $ swipeRight()
	{
		if (swiper != null)
			swiper.performSwipeRight();
		return this;
	}
	
	/**
	 * Sets the function to call when a view in the current selection has gained focus. This function
	 * will receive an instance of javaQuery for this view as its only parameter
	 * @param function the function to invoke
	 * @return this
	 */
	public $ focus(Function function)
	{
		onFocus = function;
		setupFocusListener();//fixes any changes to the onfocuschanged listener
		return this;
	}
	
	/**
	 * Gives focus to the first focusable view in the current selection.
	 * @return this
	 */
	public $ focus()
	{
		for (Component view : this.views)
		{
			view.requestFocus();
			if (view.hasFocus()) {
				break;
			}
		}
		return this;
	}
	
	/**
	 * Sets the function to call when this {@link #view} loses focus.
	 * @param function the function to invoke. Will receive a javaQuery instance containing
	 * the view as its only parameter
	 * @return this
	 */
	public $ focusout(Function function)
	{
		offFocus = function;
		setupFocusListener();
		return this;
	}
	
	/**
	 * Removes focus from all views in the current selection.
	 * @return this
	 */
	public $ focusout()
	{
		for (Component view : this.views)
		{
			view.setFocusable(false);
			view.setFocusable(true);
		}
		return this;
	}
	
	/**
	 * Set the function to call when a key-down event has been detected on this view.
	 * @param function the Function to invoke. Receives a javaQuery containing the responding view
	 * and two variable arguments:
	 * <ol>
	 * <li>the Integer key code
	 * <li>the {@link KeyEvent} Object that was produced
	 * </ol>
	 * @return this
	 */
	public $ keydown(Function function)
	{
		keyDown = function;
		setupKeyListener();
		return this;
	}
	
	/**
	 * Set the function to call when a key-press event has been detected on this view.
	 * @param function the Function to invoke. Receives a javaQuery containing the responding view
	 * and two variable arguments:
	 * <ol>
	 * <li>the Integer key code
	 * <li>the {@link KeyEvent} Object that was produced
	 * </ol>
	 * @return this
	 */
	public $ keypress(Function function)
	{
		keyPress = function;
		setupKeyListener();
		return this;
	}
	
	/**
	 * Set the function to call when a key-up event has been detected on this view.
	 * @param function the Function to invoke. Receives a javaQuery containing the responding view
	 * and two variable arguments:
	 * <ol>
	 * <li>the Integer key code
	 * <li>the {@link KeyEvent} Object that was produced
	 * </ol>
	 * @return this
	 */
	public $ keyup(Function function)
	{
		keyUp = function;
		setupKeyListener();
		return this;
	}
	
	/////Miscellaneous
	
	/**
	 * Invokes the given function for each view in the current selection. Function receives a 
	 * javaQuery instance containing the single view, and an integer of the current index
	 * @param function the function to invoke
	 * @return this
	 */
	public $ each(Function function)
	{
		for (int i = 0; i < views.size(); i++)
		{
			function.invoke($.with(views.get(i)), i);
		}
		return this;
	}
	
	/**
	 * Gets the number of child views contained in the first selected view
	 * @return the number of child views contained in the first selected view
	 */
	public int children()
	{
		if (view(0) instanceof Container)
		{
			return ((Container) view(0)).getComponentCount();
		}
		else
			return 0;
	}
	
	/**
	 * If the first view of the current selection is a subclass of {@link AdapterView}, this will loop through all the 
	 * adapter data and invoke the given function, passing the varargs:
	 * <ol>
	 * <li>the item from the adapter
	 * <li>the index
	 * </ol>
	 * Otherwise, if the first view in the current selection is a subclass of {@link Container}, {@code each} will
	 * loop through all the child views, and wrap each one in a javaQuery object. The invoked
	 * function will receive it, and an int for the index of the selected child view.
	 * @param function Function the function to invoke
	 * @return this
	 */
	public $ children(Function function)
	{
		if (view(0) instanceof Container)
		{
			Container group = (Container) view(0);
			for (int i = 0; i < group.getComponentCount(); i++)
			{
				function.invoke($.with(group.getComponent(i)), i);
			}
		}
		return this;
	}
	
	/**
	 * Loops through all the sibling views of the first view in the current selection, and wraps 
	 * each in a javaQuery object. When invoked, the given function will receive two parameters:
	 * <ol>
	 * <li>the javaQuery for the view
	 * <li>the child index of the sibling
	 * </ol>
	 * @param function receives the javaQuery for the view, and the index for arg1
	 */
	public $ siblings(Function function)
	{
		Component parent = view(0).getParent();
		if (parent != null && parent instanceof Container)
		{
			Container group = (Container) parent;
			for (int i = 0; i < group.getComponentCount(); i++)
			{
				function.invoke($.with(group.getComponent(i)), i);
			}
		}
		return this;
	}
	
	/**
	 * Gets all the views in the current selection after the given start index
	 * @param start the starting position of the views to pass to the new instance of javaQuery.
	 * @return a javaQuery object containing the views from {@code start} to the end of the list.
	 */
	public $ slice(int start)
	{
		return $.with(this.views.subList(start, this.views.size()));
	}
	
	/**
	 * Gets all the views in the current selection after the given start index and before the given
	 * end index
	 * @param start the starting position of the views to pass to the new instance of javaQuery.
	 * @return a javaQuery object containing the views from {@code start} to {@code end}.
	 */
	public $ slice(int start, int end)
	{
		return $.with(this.views.subList(start, end));
	}
	
	/** @return the number of views that are currently selected */
	public int length()
	{
		return this.views.size();
	}
	
	/** @return the number of views that are currently selected */
	public int size()
	{
		return length();
	}
	
	/**
	 * Checks to see if the first view in the current selection is a subclass of the given class name
	 * @param className the name of the superclass to check
	 * @return {@code true} if the view is a subclass of the given class name. 
	 * Otherwise, {@code false}.
	 */
	public boolean is(String className)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			if (clazz.isInstance(view(0)))
				return true;
			return false;
		}
		catch (Throwable t)
		{
			return false;
		}
	}
	
	/**
	 * Checks to see if the given Object is a subclass of the given class name
	 * @param obj the Object to check
	 * @param className the name of the superclass to check
	 * @return {@code true} if the view is a subclass of the given class name. 
	 * Otherwise, {@code false}.
	 */
	public static boolean is(Object obj, String className)
	{
		try
		{
			Class<?> clazz = Class.forName(className);
			if (clazz.isInstance(obj))
				return true;
			return false;
		}
		catch (Throwable t)
		{
			return false;
		}
	}
	
	/**
	 * Checks to see if the given Object is a subclass of the given class
	 * @param obj the Object to check
	 * @param clazz the class to check
	 * @return {@code true} if the view is a subclass of the given class name. 
	 * Otherwise, {@code false}.
	 */
	public static boolean is(Object obj, Class<?> clazz)
	{
		if (clazz.isInstance(obj))
			return true;
		return false;
	}
	

	/**
	 * Removes each view in the current selection from the layout
	 */
	public void remove()
	{
		for (Component view : this.views)
		{
			Component parent = view.getParent();
			if (parent != null && parent instanceof Container)
			{
				((Container) parent).remove(view);
			}
		}
		
	}
	
	/////Selectors
	
	/**
	 * Recursively selects all subviews of the given view
	 * @param v the parent view of all the suclasses to select
	 * @return a list of all views that are subviews in the 
	 * view hierarchy with the given view as the root
	 */
	private List<Component> recursivelySelectAllSubViews(Component v)
	{
		List<Component> list = new ArrayList<Component>();
		if (v instanceof Container)
		{
			for (int i = 0; i < ((Container) v).getComponentCount(); i++)
			{
				list.addAll(recursivelySelectAllSubViews(((Container) v).getComponent(i)));
			}
		}
		list.add(v);
		return list;
	}
	
	/**
	 * Recursively selects all subviews of the given view that are subclasses of the given Object type
	 * @param v the parent view of all the suclasses to select
	 * @return a list of all views that are subviews in the 
	 * view hierarchy with the given view as the root
	 */
	private List<Component> recursivelySelectByType(Component v, Class<?> clazz)
	{
		List<Component> list = new ArrayList<Component>();
		if (v instanceof Container)
		{
			for (int i = 0; i < ((Container) v).getComponentCount(); i++)
			{
				list.addAll(recursivelySelectByType(((Container) v).getComponent(i), clazz));
			}
		}
		if (clazz.isInstance(v))
			list.add(v);
		return list;
	}
	
	/**
	 * Select all subviews of the currently-selected views
	 * @return a javaQuery Object with all the views
	 */
	public $ selectAll()
	{
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectAllSubViews(view));
		}
		return $.with(subviews);
	}
	
	/**
	 * Select all subviews of the currently-selected views that are subclasses of the given {@code className}. 
	 * @param className
	 * @return all the selected views in a javaQuery wrapper
	 */
	public $ selectByType(String className)
	{
		Class<?> clazz = null;
		try
		{
			clazz = Class.forName(className);
		}
		catch (Throwable t)
		{
			return null;
		}
		
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectByType(view, clazz));
		}
		return $.with(subviews);
		
	}
	
	/**
	 * Selects the child views of the first view in the current selection
	 * @return a javaQuery Objects containing the child views. If the view is a subclass of 
	 * {@link AdapterView}, the {@link #data() data} of the javaQuery will be set to an Object[]
	 * of adapter items.
	 */
	public $ selectChildren()
	{
		List<Component> list = new ArrayList<Component>();
		if (view(0) instanceof Container)
		{
			for (int i = 0; i < ((Container) view(0)).getComponentCount(); i++)
			{
				list.add(((Container) view(0)).getComponent(i));
			}
		}
		return $.with(list);
	}
	
	/**
	 * Selects all subviews of the given view that do not contain subviews
	 * @param v the view whose subviews will be retrieved
	 * @return a list empty views
	 */
	private List<Component> recursivelySelectEmpties(Component v)
	{
		List<Component> list = new ArrayList<Component>();
		if (v instanceof Container && ((Container) v).getComponentCount() > 0)
		{
			for (int i = 0; i < ((Container) v).getComponentCount(); i++)
			{
				list.addAll(recursivelySelectEmpties(((Container) v).getComponent(i)));
			}
		}
		else
		{
			list.add(v);
		}
		return list;
	}
	
	/**
	 * Select all non-Containers, or Containers with no children, that lay within the view
	 * hierarchy of the current selection
	 * @return a javaQuery object containing the selection
	 */
	public $ selectEmpties()
	{
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectEmpties(view));
		}
		return $.with(subviews);
	}
	
	/**
	 * Searches the view hierarchy rooted at the given view in order to find the currently
	 * selected view
	 * @param view the view to search within
	 * @return the selected view, or null if no view in the given hierarchy was found.
	 */
	private Component recursivelyFindSelectedSubView(Component view)
	{
		if (view.hasFocus())
			return view;
		else if (view instanceof Container)
		{
			Component v = null;
			for (int i = 0; i < ((Container) view).getComponentCount(); i++)
			{
				v = recursivelyFindSelectedSubView(((Container) view).getComponent(i));
				if (v != null)
					return v;
			}
			return null;
		}
		else
			return null;
	}
	
	/**
	 * Selects the currently-focused view.
	 * @return a javaQuery Object created with the currently-selected View, if there is one
	 */
	public $ selectFocused()
	{
		Component focused = recursivelyFindSelectedSubView(rootView);
		if (focused != null)
			return $.with(focused);
		for (Component view : this.views)
		{
			focused = recursivelyFindSelectedSubView(view);
			if (focused != null)
				return $.with(focused);
		}
		
		return $.with(view(0));
	}
	
	/**
	 * Select all {@link View#INVISIBLE invisible}, {@link View#GONE gone}, and 0-alpha views within the 
	 * view hierarchy rooted at the given view
	 * @param v the view hierarchy in which to search
	 * @return a list the found views
	 */
	private List<Component> recursivelySelectHidden(Component v)
	{
		List<Component> list = new ArrayList<Component>();
		if (v instanceof Container)
		{
			for (int i = 0; i < ((Container) v).getComponentCount(); i++)
			{
				list.addAll(recursivelySelectHidden(((Container) v).getComponent(i)));
			}
		}
		if (!v.isVisible() || v.isOpaque())
			list.add(v);
		return list;
	}
	
	/**
	 * Select all {@link View#VISIBLE visible} and 1-alpha views within the given view hierarchy
	 * @param v the view to search in
	 * @return a list the found views
	 */
	private List<Component> recursivelySelectVisible(Component v)
	{
		List<Component> list = new ArrayList<Component>();
		if (v instanceof Container)
		{
			for (int i = 0; i < ((Container) v).getComponentCount(); i++)
			{
				list.addAll(recursivelySelectVisible(((Container) v).getComponent(i)));
			}
		}
		if (v.isVisible() || !v.isOpaque())
			list.add(v);
		return list;
	}
	
	/**
	 * Select all {@link View#INVISIBLE invisible}, {@link View#GONE gone}, and 0-alpha views within 
	 * the view hierarchy of the currently selected views
	 * @return a javaQuery Object containing the found views
	 */
	public $ selectHidden()
	{
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectHidden(view));
		}
		return $.with(subviews);
	}
	
	/**
	 * Select all {@link View#VISIBLE visible} and 1-alpha views within the view hierarchy
	 * of the currenly selected views
	 * @return a javaQuery Object containing the found views
	 */
	public $ selectVisible()
	{
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectVisible(view));
		}
		return $.with(subviews);
	}
	
	/**
	 * Set the current selection to the view with the given name
	 * @param name the name of the view to manipulate
	 * @return this
	 */
	public $ name(String name)
	{
		Component view = this.findComponentWithName(name);
		if (view != null)
		{
			this.views.clear();
			this.rootView = view;
			this.views.add(view);
		}
		return this;
	}

	/**
	 * Selects all views within the given current selection that are the single 
	 * children of their parent views
	 * @param v the view whose hierarchy will be checked
	 * @return a list of the found views.
	 */
	private List<Component> recursivelySelectOnlyChilds(Component v)
	{
		List<Component> list = new ArrayList<Component>();
		if (v instanceof Container)
		{
			for (int i = 0; i < ((Container) v).getComponentCount(); i++)
			{
				list.addAll(recursivelySelectOnlyChilds(((Container) v).getComponent(i)));
			}
		}
		if (v.getParent() instanceof Container && ((Container) v.getParent()).getComponentCount() == 1)
			list.add(v);
		return list;
	}
	
	/**
	 * Selects all views within the current selection that are the single children of their 
	 * parent views
	 * @return a javaQuery Object containing the found views.
	 */
	public $ selectOnlyChilds()
	{
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectOnlyChilds(view));
		}
		return $.with(subviews);
	}
	
	/**
	 * Selects all views in the current selection that can contain other child views
	 * @return a javaQuery Object containing the found Containers
	 */
	public $ selectParents()
	{
		List<Component> subviews = new ArrayList<Component>();
		for (Component view : this.views)
		{
			subviews.addAll(recursivelySelectByType(view, Container.class));
		}
		return $.with(subviews);
	}
	
	
	/////Extensions
	
	/**
	 * Add an extension with the reference <em>name</em>
	 * @param name String used by the {@link #ext(String)} method for calling this extension
	 * @param clazz the name of the subclass of {@link $Extension} that will be mapped to {@code name}.
	 * Calling {@code $.with(this).ext(myExtension); } will now create a new instance of the given
	 * {@code clazz}, passing in {@code this} instance of <em>$</em>, then calling the {@code invoke}
	 * method.
	 * @throws ClassNotFoundException if {@code clazz} is not a valid class name, or if it is not a 
	 * subclass of {@code $Extension}.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @note be aware that there is no check for if this extension overwrites a different extension.
	 */
	public static void extend(String name, String clazz) throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException
	{
		Class<?> _class = Class.forName(clazz);
		Class<?> _super = _class.getSuperclass();
		if (_super == null || _super != $Extension.class)
		{
			throw new ClassNotFoundException("clazz must be subclass of $Extension!");
		}
		Constructor<?> constructor = _class.getConstructor(new Class<?>[]{$.class});
		extensions.put(name, constructor);
		
	}
	
	/**
	 * Add an extension with the reference <em>name</em>
	 * @param name String used by the {@link #ext(String)} method for calling this extension
	 * @param clazz subclass of {@link $Extension} that will be mapped to {@code name}.
	 * Calling {@code $.with(this).ext(MyExtension.class); } will now create a new instance of the given
	 * {@code clazz}, passing in {@code this} instance of <em>$</em>, then calling the {@code invoke}
	 * method.
	 * @throws ClassNotFoundException if {@code clazz} is not a valid class name, or if it is not a 
	 * subclass of {@code $Extension}.
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @note be aware that there is no check for if this extension overwrites a different extension.
	 */
	public static void extend(String name, Class<?> clazz) throws ClassNotFoundException, SecurityException, NoSuchMethodException
	{
		Class<?> _super = clazz.getSuperclass();
		if (_super == null || _super != $Extension.class)
		{
			throw new ClassNotFoundException("clazz must be subclass of $Extension!");
		}
		Constructor<?> constructor = clazz.getConstructor(new Class<?>[]{$.class});
		extensions.put(name, constructor);
	}
	
	/**
	 * Load the extension with the name defined in {@link #extend(String, String)}
	 * @param extension the name of the extension to load
	 * @return the new extension instance
	 */
	public $Extension ext(String extension, Object... args)
	{
		Constructor<?> constructor = extensions.get(extension);
		try {
			$Extension $e = ($Extension) constructor.newInstance(this);
			$e.invoke(args);
			return $e;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
		
	}
	
	/////File IO
	
	/**
	 * Write a byte stream to file
	 * @param s the bytes to write to the file
	 * @param path defines the save location of the file
	 * @param append {@code true} to append the new data to the end of the file. {@code false} to overwrite any existing file.
	 * @param async {@code true} if the operation should be performed asynchronously. Otherwise, {@code false}.
	 */
	public static void write(byte[] s, String fileName, boolean append)
	{
		write(s, fileName, append, null, null);
		
	}
	
	/**
	 * Write a String to file
	 * @param s the String to write to the file
	 * @param path defines the save location of the file
	 * @param append {@code true} to append the new String to the end of the file. {@code false} to overwrite any existing file.
	 * @param async {@code true} if the operation should be performed asynchronously. Otherwise, {@code false}.
	 */
	public static void write(String s, String fileName, boolean append)
	{
		write(s.getBytes(), fileName, append, null, null);
		
	}
	
	/**
	 * Write a String to file, and execute functions once complete. 
	 * @param s the String to write to the file
	 * @param path defines the save location of the file
	 * @param append {@code true} to append the new String to the end of the file. {@code false} to overwrite any existing file.
	 * @param async {@code true} if the operation should be performed asynchronously. Otherwise, {@code false}.
	 * @param success Function to invoke on a successful file-write. Parameters received will be:
	 * <ol>
	 * <li>the String to write
	 * <li>the File that was written (to)
	 * </ol>
	 * @param error Function to invoke on a file I/O error. Parameters received will be:
	 * <ol>
	 * <li>the String to write
	 * <li>the String reason
	 * </ol>
	 */
	@SuppressWarnings("unchecked")
	public static void write(final String s, final String fileName, boolean append, final String notifySuccess, String notifyError)
	{
		File file = new File(fileName);
		if (!file.canWrite())
		{
			if (notifyError != null)
				EventCenter.trigger(null, notifyError, (Map<String, Object>) $.map($.entry("data", s.getBytes()), $.entry("message", "You do not have file write privelages")), null);
			return;
		}
		try {
			FileWriter writer = new FileWriter(file);
			if (append)
			{
				writer.append(s);
			}
			else
			{
				writer.write(s);
			}
			if (notifySuccess != null)
				EventCenter.trigger(null, notifySuccess, (Map<String, Object>) $.map($.entry("data", s.getBytes()), $.entry("message", "Success")), null);
		} catch (Throwable t)
		{
			if (notifyError != null)
				EventCenter.trigger(null, notifyError, (Map<String, Object>) $.map($.entry("data", s.getBytes()), $.entry("message", "IO Error")), null);
		}
	}
	
	/**
	 * Write a byte stream to file, and execute functions once complete. 
	 * @param s the bytes to write to the file
	 * @param path defines the save location of the file
	 * @param append {@code true} to append the new bytes to the end of the file. {@code false} to overwrite any existing file.
	 * @param async {@code true} if the operation should be performed asynchronously. Otherwise, {@code false}.
	 * @param notifySuccess Notification to post with parameters on a successful file write. Parameters received will be:
	 * <ol>
	 * <li>"data" : the byte[] to write
	 * <li>"message" : a message
	 * </ol>
	 * @param notifyError. Notification to post with parameters on a failed file write. Parameters received will be:
	 * <ol>
	 * <li>"data" : the byte[] to write
	 * <li>"message" : a message
	 * </ol>
	 */
	public static void write(final byte[] s, String fileName, boolean append, String notifySuccess, String notifyError)
	{
		write(new String(s), fileName, append, notifySuccess, notifyError);
	}
	
	////Utilities
	
	/**
	 * Convert an Object Array to a JSONArray
	 * @param array an Object[] containing any of: JSONObject, JSONArray, String, Boolean, Integer, 
	 * Long, Double, NULL, or null. May not include NaNs or infinities. Unsupported values are not 
	 * permitted and will cause the JSONArray to be in an inconsistent state.
	 * @return the newly-created JSONArray Object
	 */
	public static JSONArray makeArray(Object[] array)
	{
		JSONArray json = new JSONArray();
		for (Object obj : array)
		{
			json.put(obj);
		}
		return json;
	}
	
	/**
	 * Convert a JSONArray to an Object Array
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static Object[] makeArray(JSONArray array)
	{
		Object[] obj = new Object[array.length()];
		for (int i = 0; i < array.length(); i++)
		{
			try
			{
				obj[i] = array.get(i);
			}
			catch (Throwable t)
			{
				obj[i] = JSONObject.NULL;
			}
			
		}
		return obj;
		
		
	}
	
	/**
	 * Converts a JSON String to a Map
	 * @param json the String to convert
	 * @return a Key-Value Mapping of attributes declared in the JSON string.
	 * @throws JSONException thrown if the JSON string is malformed or incorrectly written
	 */
	public static Map<String, ?> map(String json) throws JSONException
	{
		return map(new JSONObject(json));
	}
	
	/**
	 * Convert a {@code JSONObject} to a Map
	 * @param json the JSONObject to parse
	 * @return a Key-Value mapping of the Objects set in the JSONObject
	 * @throws JSONException if the JSON is malformed
	 */
	public static Map<String, ?> map(JSONObject json) throws JSONException
	{
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = json.keys();
		Map<String, Object> map = new HashMap<String, Object>();
		
	    while (iterator.hasNext()) {
	        String key = iterator.next();
	        try {
	            Object value = json.get(key);
	            map.put(key, value);
	        } catch (JSONException e) {
	        	throw e;
	        } catch (Throwable t)
	        {
	        	if (key != null)
	        		Log.w("AjaxOptions", "Could not set value " + key);
	        	else
	        		throw new NullPointerException("Iterator reference is null.");
	        }
	    }
		return map;
	}
	
	/**
	 * Shortcut method for creating a Map of Key-Value pairings. For example:<br>
	 * $.map($.entry(0, "Zero"), $.entry(1, "One"), $.entry(2, "Two"));
	 * @param entries the MapEntry Objects used to populate the map.
	 * @return a new map with the given entries
	 * @see #entry(Object, Object)
	 */
	public static Map<?, ?> map(Entry<?, ?>... entries)
	{
		return QuickMap.qm(entries);
	}
	
	/**
	 * Shortcut method for creating a Key-Value pairing. For example:<br>
	 * $.map($.entry(0, "Zero"), $.entry(1, "One"), $.entry(2, "Two"));
	 * @param key the key
	 * @param value the value
	 * @return the Key-Value pairing Object
	 * @see #map(Entry...)
	 */
	public static Entry<?, ?> entry(Object key, Object value)
	{
		return QuickEntry.qe(key, value);
	}
	
	/** 
	 * Merges the contents of two arrays together into the first array. 
	 */
	public static void merge(Object[] array1, Object[] array2)
	{
		Object[] newArray = new Object[array1.length + array2.length];
		for (int i = 0; i < array1.length; i++)
		{
			newArray[i] = array1[i];
		}
		for (int i = 0; i < array2.length; i++)
		{
			newArray[i] = array2[i];
		}
		array1 = newArray;
	}
	
	/**
	 * @return a new Function that does nothing when invoked
	 */
	public static Function noop()
	{
		return new Function() {
			@Override
			public void invoke($ javaQuery, Object... args) {}
		};
	}
	
	/**
	 * @return the current time, in milliseconds
	 */
	public static long now()
	{
		return new Date().getTime();
	}
	
	/**
	 * Parses a JSON string into a JSONObject or JSONArray
	 * @param json the String to parse
	 * @return JSONObject or JSONArray (depending on the given string) if parse succeeds. Otherwise {@code null}.
	 */
	public Object parseJSON(String json)
	{
		try {
			if (json.startsWith("{"))
        		return new JSONObject(json);
        	else
        		return new JSONArray(json);
		} catch (JSONException e) {
			return null;
		}
	}
	
	/**
	 * Parses XML into an XML Document
	 * @param xml the XML to parse
	 * @return XML Document if parse succeeds. Otherwise {@code null}.
	 */
	public Document parseXML(String xml)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			return factory.newDocumentBuilder().parse(xml);
		} catch (Throwable t) {
			return null;
		}
	}
	
	
	/////AJAX
	
	/**
	 * Perform a new Ajax Task using the AjaxOptions set in the given Key-Value Map
	 * @param options {@link AjaxOptions} options
	 */
	public static void ajax(Map<String, Object> options)
	{
		ajax(new JSONObject(options));
	}
	
	/**
	 * Perform a new Ajax Task using the given JSON string to configure the {@link AjaxOptions}
	 * @param options {@link AjaxOptions} as a JSON String
	 */
	public static void ajax(String options)
	{
		try
		{
			ajax(new JSONObject(options));
		}
		catch (Throwable t)
		{
			throw new NullPointerException("Could not parse JSON!");
		}
	}
	
	/**
	 * Perform a new Ajax Task using the given JSONObject to configure the {@link AjaxOptions}
	 * @param options {@link AjaxOptions} as a JSONObject Object
	 */
	public static void ajax(JSONObject options)
	{
		try
		{
			new AjaxTask(options).execute();
		}
		catch (Throwable t)
		{
			Log.e("javaQuery", "Could not complete ajax task!");
		}
	}
	
	/**
	 * Perform an Ajax Task using the given {@code AjaxOptions}
	 * @param options the options to set for the Ajax Task
	 */
	public static void ajax(AjaxOptions options)
	{
		try
		{
			new AjaxTask(options).execute();
		}
		catch (Throwable t)
		{
			Log.e("javaQuery", "Could not complete ajax task!");
		}
	}
	
	/**
	 * Perform an Ajax Task. This is usually done as the result of an Ajax Error
	 * @param request the request
	 * @param options the configuration
	 * @see AjaxTask.AjaxError
	 */
	public static void ajax(HttpUriRequest request, AjaxOptions options)
	{
		new AjaxTask(request, options).execute();
	}
	
	///////ajax shortcut methods
	
	/**
	 * Shortcut method to use the default AjaxOptions to perform an Ajax GET request
	 * @param url the URL to access
	 * @param data the data to pass, if any
	 * @param success the Function to invoke once the task completes successfully.
	 * @param dataType the type of data to expect as a response from the URL. See 
	 * {@link AjaxOptions#dataType()} for a list of available data types
	 */
	public static void get(String url, Object data, Function success, String dataType)
	{
		$.ajax(new AjaxOptions().url(url).data(data).success(success).dataType(dataType));
	}
	
	/**
	 * Shortcut method to use the default Ajax Options to perform an Ajax GET request and receive
	 * a JSON-formatted response
	 * @param url the URL to access
	 * @param data the data to send, if any
	 * @param success Function to invoke once the task completes successfully.
	 */
	public static void getJSON(String url, Object data, Function success)
	{
		get(url, data, success, "JSON");
	}
	
	/**
	 * Shortcut method to use the default Ajax Options to perform an Ajax GET request and receive
	 * a Script response
	 * @param url the URL to access
	 * @param data the data to send, if any
	 * @param success Function to invoke once the task completes successfully.
	 * @see {@link ScriptResponse}
	 */
	public static void getScript(String url, Function success)
	{
		$.ajax(new AjaxOptions().url(url).success(success).dataType("SCRIPT"));
	}
	
	/**
	 * Shortcut method to use the default Ajax Options to perform an Ajax POST request
	 * @param url the URL to access
	 * @param data the data to post
	 * @param success Function to invoke once the task completes successfully.
	 * @param dataType the type of data to expect as a response from the URL. See 
	 * {@link AjaxOptions#dataType()} for a list of available data types
	 */
	public static void post(String url, Object data, Function success, String dataType)
	{
		$.ajax(new AjaxOptions().type("POST")
				                .url(url)
				                .data(data)
				                .success(success)
				                .dataType(dataType));
	}
	
	/**
	 * Register an event to invoke a Function every time a global Ajax Task completes
	 * @param complete the Function to call.
	 */
	public static void ajaxComplete(Function complete)
	{
		EventCenter.bind("ajaxComplete", complete, null);
	}
	
	/**
	 * Manually invoke the Function set to be called every time a global Ajax Task completes.
	 * @see #ajaxComplete(Function)
	 */
	public static void ajaxComplete()
	{
		EventCenter.trigger("ajaxComplete", null, null);
	}
	
	/**
	 * Register an event to invoke a Function every time a global Ajax Task receives an error
	 * @param error the Function to call.
	 */
	public static void ajaxError(Function error)
	{
		EventCenter.bind("ajaxError", error, null);
	}
	
	/**
	 * Manually invoke the Function set to be called every time a global Ajax Task receives an error.
	 * @see #ajaxError(Function)
	 */
	public static void ajaxError()
	{
		EventCenter.trigger("ajaxError", null, null);
	}

	/**
	 * Register an event to invoke a Function every time a global Ajax Task sends data
	 * @param send the Function to call.
	 */
	public static void ajaxSend(Function send)
	{
		EventCenter.bind("ajaxSend", send, null);
	}
	
	/**
	 * Manually invoke the Function set to be called every time a global Ajax Task sends data.
	 * @see #ajaxSend(Function)
	 */
	public static void ajaxSend()
	{
		EventCenter.trigger("ajaxSend", null, null);
	}
	
	/**
	 * Register an event to invoke a Function every time a global Ajax Task begins, if no other
	 * global task is running.
	 * @param start the Function to call.
	 */
	public static void ajaxStart(Function start)
	{
		EventCenter.bind("ajaxStart", start, null);
	}
	
	/**
	 * Manually invoke the Function set to be called every time a global Ajax Task begins, if 
	 * no other global task is running.
	 * @see #ajaxStart(Function)
	 */
	public static void ajaxStart()
	{
		EventCenter.trigger("ajaxStart", null, null);
	}
	
	/**
	 * Register an event to invoke a Function every time a global Ajax Task stops, if it was
	 * the last global task running
	 * @param stop the Function to call.
	 */
	public static void ajaxStop(Function stop)
	{
		EventCenter.bind("ajaxStop", stop, null);
	}

	/**
	 * Manually invoke the Function set to be called every time a global Ajax Task stops, if it was
	 * the last global task running
	 * @see #ajaxStop(Function)
	 */
	public static void ajaxStop()
	{
		EventCenter.trigger("ajaxStop", null, null);
	}
	
	/**
	 * Register an event to invoke a Function every time a global Ajax Task completes successfully.
	 * @param start the Function to invoke.
	 */
	public static void ajaxSuccess(Function success)
	{
		EventCenter.bind("ajaxSuccess", success, null);
	}

	/**
	 * Manually invoke the Function set to be called every time a global Ajax Task completes 
	 * successfully.
	 * @see #ajaxSuccess(Function)
	 */
	public static void ajaxSuccess()
	{
		EventCenter.trigger("ajaxSuccess", null, null);
	}
	
	/**
	 * Handle custom Ajax options or modify existing options before each request is sent and before they are processed by $.ajax().
	 * Note that this will be run in the background thread, so any changes to the UI must be made through a call to Activity.runOnUiThread().
	 * @param prefilter {@link Function} that will receive one Map argument with the following contents:
	 * <ul>
	 * <li>"options" : AjaxOptions for the request
	 * <li>"request" : HttpClient request Object
	 * </ul>
	 * 
	 */
	public static void ajaxPrefilter(Function prefilter)
	{
		EventCenter.bind("ajaxPrefilter", prefilter, null);
	}
	
	/**
	 * Setup global options
	 * @param options
	 */
	public static void ajaxSetup(AjaxOptions options)
	{
		AjaxOptions.ajaxSetup(options);
	}
	
	/**
	 * Clears async task queue
	 */
	public static void ajaxKillAll()
	{
		AjaxTask.killTasks();
	}
	
	/**
	 * Load data from the server and place the returned HTML into the matched element
	 * @param url A string containing the URL to which the request is sent.
	 * @param data A plain object or string that is sent to the server with the request.
	 * @param complete A callback function that is executed when the request completes. Will receive
	 * two arguments: 1. response text, 2. text status
	 */
	public void load(String url, Object data, final Function complete)
	{
		$.ajax(new AjaxOptions().url(url).data(data).complete(new Function() {

			@Override
			public void invoke($ javaQuery, Object... params) {
				$.this.html(params[0].toString());
				complete.invoke($.this, params);
			}
			
		}));
	}
	

	//// Convenience
	
	/**
	 * Include the html string the selected views. If a view has a setText method, it is used. Otherwise,
	 * a new TextView is created. This html can also handle image tags for both urls and local files.
	 * Local files should be the name (for example, for R.id.ic_launcher, just use ic_launcher).
	 * @param html the HTML String to include
	 */
	public $ html(String html)
	{
		for (Component view : this.views)
		{
			try
			{
				Method m = view.getClass().getMethod("setText", new Class<?>[]{String.class});
				m.invoke(view, html);
			}
			catch (Throwable t)
			{
				if (view instanceof Container)
				{
					try
					{
						//no setText method. Try a TextView
						JLabel label = new JLabel();
						int rgba = Color.HSBtoRGB(0, 0, 0);
						rgba |= (0 & 0xff);
						label.setBackground(new Color(rgba, true));
						label.setBounds(label.getParent().getBounds());
						((Container) view).add(label);
						label.setText(html);
					}
					catch (Throwable t2)
					{
						//unable to set content
						Log.w("javaQuery", "unable to set HTML content");
					}
				}
				else
				{
					//unable to set content
					Log.w("javaQuery", "unable to set textual content");
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Includes the given text string inside of the selected views. If a view has a setText method, it is used
	 * otherwise, if possible, a textview is added as a child to display the text.
	 * @param text the text to include
	 */
	public $ text(String text)
	{
		for (Component view : this.views)
		{
			try
			{
				Method m = view.getClass().getMethod("setText", new Class<?>[]{String.class});
				m.invoke(view, text);
			}
			catch (Throwable t)
			{
				if (view instanceof Container)
				{
					try
					{
						//no setText method. Try a TextView
						JLabel label = new JLabel();
						int rgba = Color.HSBtoRGB(0, 0, 0);
						rgba |= (0 & 0xff);
						label.setBackground(new Color(rgba, true));
						label.setBounds(label.getParent().getBounds());
						((Container) view).add(label);
						label.setText(text);
					}
					catch (Throwable t2)
					{
						//unable to set content
						Log.w("javaQuery", "unable to set textual content");
					}
				}
				else
				{
					//unable to set content
					Log.w("javaQuery", "unable to set textual content");
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Includes the given image inside of the selected views. If a view is an `ImageView`, its image
	 * is set. Otherwise, the background image of the view is set.
	 * @param image the drawable image to include
	 * @return this
	 */
	public $ image(Image image)
	{
		for (Component v : views)
		{
			if (v instanceof JLabel)
			{
				((JLabel) v).setIcon(new ImageIcon(image));
			}
			else if (v instanceof Window)
			{
				((Window) v).setIconImage(image);
			}
			else
			{
				if (v instanceof Container)
				{
					JLabel l = new JLabel(new ImageIcon(image));
					l.setBounds(v.getBounds());
					((Container) v).add(l);
				}
				else if (v.getParent() != null && v.getParent() instanceof Container)
				{
					JLabel l = new JLabel(new ImageIcon(image));
					l.setBounds(v.getBounds());
					((Container) v.getParent()).add(l);
				}
				
				
			}
		}
		return this;
	}
	
	/**
	 * For `ImageView`s, this will set the image to the given asset or url. Otherwise, it will set the
	 * background image for the selected views.
	 * @param source asset path, file path (starting with "file://") or URL to image
	 * @return this
	 */
	public $ image(String source)
	{
		return image(source, -1, -1, null);
	}
	
	/**
	 * For `ImageView`s, this will set the image to the given asset or url. Otherwise, it will set the
	 * background image for the selected views.
	 * @param source asset path, file path (starting with "file://") or URL to image
	 * @param width specifies the output bitmap width
	 * @param height specifies the output bitmap height
	 * @param error if the given source is a file or asset, this receives a javaQuery wrapping the 
	 * current context and the {@code Throwable} error. Otherwise, this will receive an
	 * Ajax error.
	 * @return this
	 * @see AjaxOptions#error(Function)
	 */
	public $ image(String source, int width, int height, Function error)
	{
		if (source.startsWith("file://"))
		{
			try {
				Image image = ImageIO.read(new File(source.substring(7)));
				
				for (Component v : views)
				{
					if (v instanceof JLabel)
					{
						((JLabel) v).setIcon(new ImageIcon(image));
					}
					else if (v instanceof Window)
					{
						((Window) v).setIconImage(image);
					}
					else
					{
						if (v instanceof Container)
						{
							JLabel l = new JLabel(new ImageIcon(image));
							l.setBounds(v.getBounds());
							((Container) v).add(l);
						}
						else if (v.getParent() != null && v.getParent() instanceof Container)
						{
							JLabel l = new JLabel(new ImageIcon(image));
							l.setBounds(v.getBounds());
							((Container) v.getParent()).add(l);
						}
						
						
					}
				}
			}
			catch(Throwable t) {
				if (error != null) {
					error.invoke($.with(view(0)), t);
				}
			}
		}
		else
		{
			boolean fallthrough = false;
			try {
				new URL(source);
			}
			catch (Throwable t)
			{
				fallthrough = true;
			}
			if (fallthrough)
			{
				AjaxOptions options = new AjaxOptions().url(source)
						                               .type("GET")
						                               .dataType("image")
						                               .context(view(0))
						                               .global(false)
						                               .success(new Function() {
					@Override
					public void invoke($ javaQuery, Object... params) {
						Image image = (Image) params[0];
						for (Component v : views)
						{
							if (v instanceof JLabel)
							{
								((JLabel) v).setIcon(new ImageIcon(image));
							}
							else if (v instanceof Window)
							{
								((Window) v).setIconImage(image);
							}
							else
							{
								if (v instanceof Container)
								{
									JLabel l = new JLabel(new ImageIcon(image));
									l.setBounds(v.getBounds());
									((Container) v).add(l);
								}
								else if (v.getParent() != null && v.getParent() instanceof Container)
								{
									JLabel l = new JLabel(new ImageIcon(image));
									l.setBounds(v.getBounds());
									((Container) v.getParent()).add(l);
								}
								
								
							}
						}
					}
				});
				
				if (error != null) {
					options.error(error);
				}
				if (width >= 0)
				{
					options.imageWidth(width);
				}
				if (height >= 0)
				{
					options.imageHeight(height);
				}
				$.ajax(options);
			}
		}
		return this;
	}
	
	/**
	 * Iterates through the selected views and sets the images to the given images (in order)
	 * @param source asset path, file path (starting with "file://") or URL to image
	 * @return this
	 */
	public $ image(final List<String> sources)
	{
		this.each(new Function() {
			@Override
			public void invoke($ javaQuery, Object... params) {
				javaQuery.image(sources.get((Integer) params[0]));
			}
		});
		return this;
	}
	
	/**
	 * Iterates through the selected views and sets the images to the given images (in order)
	 * @param sources the file paths or URLs to set
	 * @param width the output width of the image
	 * @param height the output height of the image
	 * @param error if the given source is a file or asset, this receives a javaQuery wrapping the 
	 * current context and the {@code Throwable} error. Otherwise, this will receive an
	 * Ajax error.
	 * @return this
	 * @see AjaxOptions#error(Function)
	 */
	public $ image(final List<String> sources, final int width, final int height, final Function error)
	{
		this.each(new Function() {
			@Override
			public void invoke($ javaQuery, Object... params) {
				javaQuery.image(sources.get((Integer) params[0]), width, height, error);
			}
		});
		return this;
	}
	

	/**
	 * Show an alert.
	 * @param text the message to display.
	 * @see #alert(String, String)
	 */
	public static void alert(String text)
	{
		alert(null, text);
	}
	
	/**
	 * Show an alert
	 * @param context used to display the alert window
	 * @param title the title of the alert window. Use {@code null} to show no title
	 * @param text the alert message
	 * @see #alert(String)
	 */
	public static void alert(String title, String text)
	{
		JDialog alert = new JDialog();
		if (title != null)
			alert.setTitle(title);
		if (text != null)
			alert.add(new JLabel(text, JLabel.CENTER));
		alert.setVisible(true);
	}
	
	////Callbacks
	
	/**
	 * A multi-purpose callbacks list object that provides a powerful way to manage callback lists.
	 * Registered callback functions will receive a {@code null} for their <em>javaQuery</em>
	 * variable. To receive a non-{@code null} variable, you must provide a <em>Context</em>.
	 * @return a new instance of {@link Callbacks}
	 */
	public static Callbacks Callbacks()
	{
		return new Callbacks();
	}
	
	//////CSS-based
	
	/**
	 * @return the computed height for the first view in the current selection
	 */
	public int height()
	{
		return view(0).getHeight();
	}
	
	/**
	 * Set the height of the selected views
	 * @param height the new height
	 * @return this
	 */
	public $ height(int height)
	{
		for (Component view : this.views)
		{
			Rectangle bounds = view.getBounds();
			bounds.height = height;
			view.setBounds(bounds);
		}
		return this;
	}
	
	/**
	 * @return the computed width for the first view in the current selection
	 */
	public int width()
	{
		return view(0).getWidth();
	}
	
	/**
	 * Set the width of the views in the current selection
	 * @param width the new width
	 * @return this
	 */
	public $ width(int width)
	{
		for (Component view : this.views)
		{
			Rectangle bounds = view.getBounds();
			bounds.width = width;
			view.setBounds(bounds);
		}
		return this;
	}
	
	/**
	 * @return the coordinates of the first view in the current selection.
	 */
	public Point offset()
	{
		return new Point(view(0).getBounds().x, view(0).getBounds().y);
	}
	
	/**
	 * Set the coordinates of each selected view, relative to the document.
	 * @param x the x-coordinate, in pixels
	 * @param y the y-coordinate, in pixels
	 * @return this
	 */
	public $ offset(int x, int y)
	{
		return position(x, y);
	}
	
	/**
	 * @return the coordinates of the first view in the current selection, 
	 * relative to the offset parent.
	 */
	public Point position()
	{
		return new Point(view(0).getX(), view(0).getY());
	}
	
	/**
	 * Sets the coordinates of each selected view, relative to its offset parent
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return 
	 */
	public $ position(int x, int y)
	{
		for (Component view : this.views)
		{
			Rectangle bounds = view.getBounds();
			bounds.x = x;
			bounds.y = y;
			view.setBounds(bounds);
		}
		return this;
	}
	
	//Timer Functions
	
	/**
	 * Schedule a task for single execution after a specified delay.
	 * @param function the task to schedule. Receives no args. Note that the function will be
	 * run on a Timer thread, and not the UI Thread.
	 * @param delay amount of time in milliseconds before execution.
	 * @return the created Timer
	 */
	public static Timer setTimeout(final Function function, long delay)
	{
		Timer t = new Timer();
		t.schedule(new TimerTask(){

			@Override
			public void run() {
				function.invoke(null);
			}
			
		}, delay);
		return t;
	}
	
	/**
	 * Schedule a task for repeated fixed-rate execution after a specific delay has passed.
	 * @param the task to schedule. Receives no args. Note that the function will be
	 * run on a Timer thread, and not the UI Thread.
	 * @param delay amount of time in milliseconds before execution.
	 * @return the created Timer
	 */
	public static Timer setInterval(final Function function, long delay)
	{
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				function.invoke(null);
			}
			
		}, 0, delay);
		return t;
	}
	
	/** 
	 * Capitalizes the first letter of the given string.
	 * @param string the string whose first letter should be capitalized
	 * @return the given string with its first letter capitalized
	 * @throws NullPointerException if the string is null or empty
	 */
	public static String capitalize(String string)
	{
		if (string == null || string.trim().length() == 0)
			throw new NullPointerException("Cannot handle null or empty string");
		
		StringBuilder strBuilder = new StringBuilder(string);
		strBuilder.setCharAt(0, Character.toUpperCase(strBuilder.charAt(0)));
		return strBuilder.toString();
	}
}
