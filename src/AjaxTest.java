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

import java.awt.Color;

import self.philbrown.javaQuery.$;
import self.philbrown.javaQuery.AjaxOptions;
import self.philbrown.javaQuery.AjaxTask.AjaxError;
import self.philbrown.javaQuery.Function;
import self.philbrown.javaQuery.Log;


/**
 * Simple Ajax Test
 * @author Phil Brown
 * @since 8:58:23 AM Aug 29, 2013
 */
public class AjaxTest 
{
	/**
	 * Perform an API Test. Uses the format:
	 * <pre>
	 * java AjaxTest [json]
	 * </pre>
	 * For example:
	 * <pre>
	 * java AjaxTest {url: 'http://www.example.com', type: 'get', dataType: 'json'}
	 * </pre>
	 * @param args
	 */
	public static void main(String[] args)
	{
		Log.disableANSI();
		if (args.length == 0)
		{
			printHelp();
			return;
		}
		if (args[0].startsWith("{"))
		{
			//JSON
			StringBuilder builder = new StringBuilder();
			for (String s : args)
			{
				builder.append(" ").append(s);
			}
			try {
				AjaxOptions options = new AjaxOptions(builder.toString());
				if (options.url() == null || options.url().isEmpty())
				{
					Log.e("APITest", "Invalid URL");
				}
				else
				{
					$.ajax(options.success(new Function() {

						@Override
						public void invoke($ arg0, Object... arg1) {
							Log.i("Ajax", "Success!");
							Log.info("%s", arg1[0]);
						}
						
					}).error(new Function() {

						@Override
						public void invoke($ arg0, Object... arg1) {
							AjaxError error = (AjaxError) arg1[0];
							Log.err("Ajax\tError %d: %s", error.status, error.reason);
						}
						
					}));
				}
			}
			catch (Throwable t)
			{
				Log.e("AjaxTest", "Invalid JSON");
			}
		}
		else
		{
			printHelp();
		}
		
	}
	
	public static void printHelp()
	{
		Log.buildString(Log.getColoredString("AjaxTest by Phil Brown", Color.CYAN),
				        "\n------------------------------------\n",
				        "\tjava AjaxTest <json string>\n",
				        Log.getBoldString("For example:\t"),
				        "java AjaxTest {url: 'http://www.example.com', type: 'get', dataType: 'json'}");
	}
}
