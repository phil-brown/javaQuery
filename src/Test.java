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

import self.philbrown.javaQuery.*;

/**
 * This is a basic test application.
 * @author Phil Brown
 * @since 8:53:50 AM Aug 29, 2013
 *
 */
public class Test 
{
	/**
	 * Runs a basic command to get the HTML from a <a href="https://github.com/phil-brown">github</a> site.
	 * @param args
	 */
	public static void main(String[] args)
	{
		$.ajaxStart(new Function() {

			@Override
			public void invoke($ javaQuery, Object... params) {
				Log.i("Test", "Start");
			}
			
		});
		
		$.ajaxStop(new Function() {

			@Override
			public void invoke($ javaQuery, Object... params) {
				Log.i("Test", "End");
			}
			
		});
		
		$.ajax(new AjaxOptions().url("https://github.com/phil-brown").success(new Function() {

			@Override
			public void invoke($ javaQuery, Object... params) {
				Log.i("Test", "Success");
				Log.i("Result", params[0].toString());
			}
			
		}).error(new Function() {

			@Override
			public void invoke($ javaQuery, Object... params) {
				Log.e("Test", "Failed");
			}
			
		}).dataType("html").debug(true));
	}
}
