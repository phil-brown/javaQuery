## javaQuery

### Introduction

__javaQuery__ is an full port of [jQuery](https://github.com/jquery/jquery), and is designed to
be as syntactically alike as possible in Java. *javaQuery* is derived from my full port of *jQuery* to
Android: [droidQuery](http://bit.ly/droidquery).

For those not familiar with *jQuery*, it essentially provides magic for allowing the simultaneous
manipulation of a set of UI entities (using animations, attributes settings, etc), as well as to
perform complex tasks, such as asynchronous network tasks. *javaQuery* can do all of these things too, and
provides support for other common tasks.

Also like *jQuery*, *javaQuery* allows the addition of extensions to add to the power of the library.

### How to Include javaQuery in your Project

The simplest way to include *javaQuery* in your project is to copy the latest release jar
into your project's build path.

### License

Copyright 2013 Phil Brown

*javaQuery* is licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

### How to Use

> Note: javaQuery is a work in progress. If you find any bugs or would like functionality that is missing, please create a new issue (https://github.com/phil-brown/javaQuery/issues).

Below are some of the most common tasks for which *javaQuery* can be used:

To create a new *javaQuery*, simply use the default constructor: `$ javaQuery = new $();`
Or alternatively, the `make` method can be used to the same effect: `$ javaQuery = $.make();`

Once you have the *javaQuery* instance, you can either save it as a variable, or chain calls together, since
many of the methods will return the current instance of *javaQuery*.

**Logging**

*javaQuery* is packaged with an advanced logging library that uses ANSI to print formatted text. If your
console does not support ANSI, it is recommended you either install a plug-in (such as [this one](http://mihai-nita.net/java/)
for *Eclipse*), or simply add this call to disable ANSI output:

    Log.disableANSI();

**Ajax**

To perform an asynchronous network task, you can use *ajax*. The most straight-forward way to create and
start an ajax task is with the `$.ajax(AjaxOptions)` method. For example:

    $.ajax(new AjaxOptions().url("http://www.example.com")
                            .type("GET")
                            .dataType("text")
                            .context(this)
                            .success(new Function() {
                                @Override
                                public void invoke($ javaQuery, Object... params) {
                                    Log.i("Ajax", (String) params[0]);
                                }
                            }).error(new Function() {
                                @Override
                                public void invoke($ javaQuery, Object... params) {
                                    AjaxError error = (AjaxError) params[0];
                                    Log.err(Log.getBoldString("Ajax\tError %d: %s"), error.status, error.reason);
                                }
                            }));

**Callbacks**

The *Callbacks* Object provides a simple way to manage and fire sets of callbacks. To get an instance
of this Object, use `$.Callbacks(this)`.


**Events**

*javaQuery* can be used to register for notification Strings sent by other objects. For example:

    //Register for a notification (one of several methods)
    $.make().listenTo("print", new Function() {
		@Override
		public void invoke($ javaQuery, Object... params) {
		    String message = "";
			if (params != null && params.length > 0)
			{
				if (params[0] instanceof Map) {
					Map map = (Map) params[0];
					if (map.containsKey("message"))
						message = (String) map.get("message");
				}
			}
			
			Log.i("Printer", message);
		}
    });
    
    //send a notification
    $.make().notify("print", $.map($.entry("message", "this is a message"));

**Miscellaneous**

*javaQuery* also comes with several methods that simplify a lot of common tasks. including:

* __map(String)/map(JSONObject)__ - converts a JSON String or a JSONObject to a Map Object
* __map(Entry...)__ - quickly make a Map Object
* __entry(String, Object)__ - quickly make a Map Entry Object
* __write__ - write text to a file
* __parseJSON__ - parses a JSON string and returns a JSONObject
* __parseXML__ - parses an XML string and returns a Document Object


**A note about Scripts**

In *jQuery*, there is an `Ajax` type called `Script`, which can be used to download a `Javascript` file.
This type also exists in *javaQuery*, but instead of `Javascript`, it expects a command-line script, which
is run in a Java process. A request may likely look like this:

    $.ajax("{url: 'http://www.example.com/settings', type: 'post', dataType: 'script', data: '{id: 4, setting: 1}' }");
    
and as long as the request was successful, the script that responds will be run automatically.

If the script calculates some data, the response would include the script output.

    