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
import java.util.Locale;

/**
 * Contains static methods for logging and ANSI formatting.
 * @author Phil Brown
 *
 */
public class Log 
{
	
	/** Toggle that determines whether or not to format ansi text when requested. */
	private static boolean ansi = true;
	
	/** Allow methods to format ANSI text when requested. */
	public static void enableANSI()
	{
		ansi = true;
	}
	
	/** Restrict methods from formatting ANSI text when requested. */
	public static void disableANSI()
	{
		ansi = false;
	}
	
	/**
	 * Concatenates Strings together using a StringBuilder. This is faster than using the +-operator.
	 * @param args the Objects to append
	 * @return a concatenation of the Objects in {@code args}
	 */
	public static String buildString(Object... args)
	{
		StringBuilder b = new StringBuilder();
		for (Object obj : args)
		{
			b.append(obj);
		}
		return b.toString();
	}
	
	/**
	 * Handles String format for the default (US) Locale. This should be used for locale-agnostic
	 * messages to prevent locale errors, such as commas used instead of decimals.
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(String format, Object... args)
	{
		return String.format(Locale.US, format, args);
	}
	
	/**
	 * Adds the ANSI code for the given string to appear <b>bold</b> in the terminal
	 * @param string
	 * @return
	 */
	public static String getBoldString(String string)
	{
		if (!ansi)
			return string;
		return buildString("\u001B[1m", string, "\u001B[0m");
	}
	
	/**
	 * Adds the ANSI code for the given string to appear <u>underlined</u> in the terminal
	 * @param string
	 * @return
	 */
	public static String getUnderLinedString(String string)
	{
		if (!ansi)
			return string;
		return buildString("\u001B[4m", string, "\u001B[0m");
	}
	
	/**
	 * Adds the ANSI code for the given string to appear blinking in the terminal
	 * @param string
	 * @return
	 */
	public static String getBlinkingString(String string)
	{
		if (!ansi)
			return string;
		return buildString("\u001B[5m", string, "\u001B[0m");
	}
	
	/**
	 * Adds the ANSI code for the given string to appear with its colors inverted in the terminal.
	 * (i.e. the text will be set to the background color, and the background will be set to
	 * the text color)
	 * @param string
	 * @return
	 */
	public static String getNegativeString(String string)
	{
		if (!ansi)
			return string;
		return buildString("\u001B[7m", string, "\u001B[0m");
	}
	
	/**
	 * Adds the ANSI code for the given string to appear colored in the terminal<br>
	 * The following colors are supported: black, red, green, yellow, blue, magenta, cyan, white
	 * @param string
	 * @param color
	 * @return
	 * @see Color
	 */
	public static String getColoredString(String string, Color color)
	{
		if (!ansi)
			return string;
		if (color == Color.BLACK)
			return buildString("\u001B[30m", string, "\u001B[0m");
		else if (color == Color.RED)
			return buildString("\u001B[31m", string, "\u001B[0m");
		else if (color == Color.GREEN)
			return buildString("\u001B[32m", string, "\u001B[0m");
		else if (color == Color.YELLOW)
			return buildString("\u001B[33m", string, "\u001B[0m");
		else if (color == Color.BLUE)
			return buildString("\u001B[34m", string, "\u001B[0m");
		else if (color == Color.MAGENTA)
			return buildString("\u001B[35m", string, "\u001B[0m");
		else if (color == Color.CYAN)
			return buildString("\u001B[36m", string, "\u001B[0m");
		else if (color == Color.WHITE)
			return buildString("\u001B[37m", string, "\u001B[0m");
		else
		{
			log("Color not supported");
			return string;
		}
	}
	
	/**
	 * Adds the ANSI code for the given string's background to appear colored in the terminal<br>
	 * The following colors are supported: black, red, green, yellow, blue, magenta, cyan, white
	 * @param string
	 * @param color
	 * @return
	 * @see Color
	 */
	public static String getColoredBackgroundString(String string, Color color)
	{
		if (!ansi)
			return string;
		if (color == Color.BLACK)
			return buildString("\u001B[40m", string, "\u001B[0m");
		else if (color == Color.RED)
			return buildString("\u001B[41m", string, "\u001B[0m");
		else if (color == Color.GREEN)
			return buildString("\u001B[42m", string, "\u001B[0m");
		else if (color == Color.YELLOW)
			return buildString("\u001B[43m", string, "\u001B[0m");
		else if (color == Color.BLUE)
			return buildString("\u001B[44m", string, "\u001B[0m");
		else if (color == Color.MAGENTA)
			return buildString("\u001B[45m", string, "\u001B[0m");
		else if (color == Color.CYAN)
			return buildString("\u001B[46m", string, "\u001B[0m");
		else if (color == Color.WHITE)
			return buildString("\u001B[47m", string, "\u001B[0m");
		else
		{
			log("Color not supported");
			return string;
		}
	}

	/**
	 * This method is used by the core logging functions to nicely format output to the terminal
	 * @param format the text to display, using java string format arguments
	 * @param args the arguments to show in the format string.
	 */
	public static void log(String format, Object... args)
	{
		try{ 
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			StringBuilder b = new StringBuilder();
			b.append(format).append(", ");
			int index = 1;
			String name = null;
			boolean loop = true;
			do {
				index++;
				if (trace != null && index < trace.length)
				{
					name = trace[index].getClassName();
					
					if (name != null)
					{
						if (!name.contains("com.npeinc.module_NPECore.model.NPE"))
						{
							loop = false;
						}
					}
				}
				else
				{
					index = 1;
					loop = false;
				}
				
			} while(loop);
			b.append(formatStackTrace(trace[index]));
			b.append(buildCommaSeparatedString(args));
			
			System.out.println(String.format(Locale.US, format, args));
			
		} catch (Throwable t)
		{
			//this should not occur
		}
	}
	
	/**
	 * Logs a green message to the terminal (if {@link #ansi} is enabled). Otherwise, it works
	 * identical to {@link #log(String, Object...)}
	 * @param format
	 * @param args
	 */
	public static void info(String format, Object... args)
	{
		log(getColoredString(format, Color.GREEN), args);
	}
	
	/**
	 * Logs a yellow message to the terminal (if {@link #ansi} is enabled). Otherwise, it works
	 * identical to {@link #log(String, Object...)}
	 * @param format
	 * @param args
	 */
	public static void warn(String format, Object... args)
	{
		log(getColoredString(format, Color.YELLOW), args);
	}
	
	/**
	 * Logs a red message to the terminal (if {@link #ansi} is enabled). Otherwise, it works
	 * identical to {@link #log(String, Object...)}
	 * @param format
	 * @param args
	 */
	public static void err(String format, Object... args)
	{
		log(getColoredString(format, Color.RED), args);
	}
	
	/**
	 * generic log (no color or formatting)
	 * @param format
	 * @param args
	 */
	public static void logdebug(String format, Object... args)
	{
		log(format, args);
	}
	
	/**
	 * Formats a stack trace into a single line that provides relevant information for debugging
	 * @param element the element to format
	 * @return a well-formatted stack-trace line containing the class name, method name, and line number
	 * that, when clicked in the logcat, will display the line or source from where the message originated.
	 */
	public static String formatStackTrace(StackTraceElement element)
	{
		StringBuilder b = new StringBuilder();
		
		b.append(" at ");
		String clazz = element.getClassName();
		b.append(clazz).append(".");
		b.append(element.getMethodName()).append("(");
		b.append(clazz.substring(clazz.lastIndexOf(".") + 1)).append(".java:");
		b.append(element.getLineNumber()).append(")").append(" , ##");
		return b.toString();
	}
	
	/**
	 * Takes a list of Objects and calls their {@link #toString()} methods to get their string representation, then
	 * inserts a comma between all of them
	 * @param args a list of Objects to get as a comma-separated list 
	 * @return a comma-separated list of the given {@code args}
	 */
	public static String buildCommaSeparatedString(Object... args)
	{
		if (args == null) return "";
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < args.length; i++)
		{
			b.append(args[i]);
			if (i != args.length-1)
			{
				b.append(", ");
			}
		}
		return b.toString();
	}
	
	//alias methods - modeled after Android methods
	public static void i(String tag, String text)
	{
		info("%s\t%s", tag, text);
	}
	public static void d(String tag, String text)
	{
		logdebug("%s\t%s", tag, text);
	}
	public static void v(String tag, String text)
	{
		System.out.println(format("%s\t%s", tag, text));
	}
	public static void e(String tag, String text)
	{
		err("%s\t%s", tag, text);
	}
	public static void w(String tag, String text)
	{
		warn("%s\t%s", tag, text);
	}
	public static void wtf(String tag, String text)
	{
		System.out.println(format("%s\t%s", tag, text));
	}
}
