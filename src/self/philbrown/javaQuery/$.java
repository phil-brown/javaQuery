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

import java.io.File;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Partial port of jQuery to Java
 * @author Phil Brown
 * @since 1:20:41 PM Aug 28, 2013
 *
 */
public class $ 
{
	/**
	 * Shortcut to Constructor
	 * @return a new instance of javaQuery ($)
	 */
	public static $ make()
	{
		return new $();
	}
	
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
	};
	
	/** 
	 * Optional data referenced by this javaQuery Object. Best practice is to make this a 
	 * {@link WeakReference} to avoid memory leaks.
	 */
	private Object data;
	
	/** Contains a mapping of {@code javaQuery} extensions. */
	private static Map<String, Constructor<?>> extensions = new HashMap<String, Constructor<?>>();
	
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
	
	//these listenTo methods expose the EventCenter, and provide a behavior similar to that provided
	//by backbone.js
	
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
	public void write(byte[] s, String fileName, boolean append)
	{
		write(s, fileName, append, $.noop(), $.noop());
		
	}
	
	/**
	 * Write a String to file
	 * @param s the String to write to the file
	 * @param path defines the save location of the file
	 * @param append {@code true} to append the new String to the end of the file. {@code false} to overwrite any existing file.
	 * @param async {@code true} if the operation should be performed asynchronously. Otherwise, {@code false}.
	 */
	public void write(String s, String fileName, boolean append)
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
	public void write(final String s, final String fileName, boolean append, final Function success, Function error)
	{
		File file = new File(fileName);
		if (!file.canWrite())
		{
			error.invoke(this, s, "You do not have file write privelages");
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
			success.invoke(this, s, "Success");
		} catch (Throwable t)
		{
			error.invoke(this, s, "IO Error");
		}
	}
	
	/**
	 * Write a byte stream to file, and execute functions once complete. 
	 * @param s the bytes to write to the file
	 * @param path defines the save location of the file
	 * @param append {@code true} to append the new bytes to the end of the file. {@code false} to overwrite any existing file.
	 * @param async {@code true} if the operation should be performed asynchronously. Otherwise, {@code false}.
	 * @param success Function to invoke on a successful file-write. Parameters received will be:
	 * <ol>
	 * <li>the byte[] to write
	 * <li>the path to the file
	 * </ol>
	 * @param error Function to invoke on a file I/O error. Parameters received will be:
	 * <ol>
	 * <li>the byte[] to write
	 * <li>the path to the file
	 * </ol>
	 */
	public void write(final byte[] s, String fileName, boolean append, final Function success, Function error)
	{
		write(new String(s), fileName, append, success, error);
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
	
	////Callbacks
	
	/**
	 * A multi-purpose callbacks list object that provides a powerful way to manage callback lists.
	 * Registered callback functions will receive a {@code null} for their <em>javaQuery</em>
	 * variable. To receive a non-{@code null} variable, you must provide a <em>Context</em>.
	 * @return a new instance of {@link Callbacks}
	 * @see #Callbacks(Context)
	 */
	public static Callbacks Callbacks()
	{
		return new Callbacks();
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
}
