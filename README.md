## javaQuery

### Introduction

__javaQuery__ is an Java *port* of [jQuery](https://github.com/jquery/jquery), and is designed to
be as syntactically alike as possible in Java. *javaQuery* is derived from my original Android port
[droidQuery](http://bit.ly/droidquery).

For those not familiar with *jQuery*, it essentially provides magic for allowing the simultaneous
manipulation of a set of UI entities (using animations, attributes settings, etc), as well as to
perform complex tasks, such as asynchronous network tasks. *javaQuery* can do all of these things.

Essentially, *javaQuery* provides this same type of magic for the view hierarchy and `AsyncTasks`, and
can be used to perform other frequent jobs, such as showing alert messages. Also like *jQuery*, 
*javaQuery* allows the addition of extensions to add to the power of the library. A list of known 
extensions is available on the [wiki](https://github.com/phil-brown/javaQuery/wiki/known-extensions).

### How to Include javaQuery in your Project

The simplest way to include *javaQuery* in your project is to copy the latest release jar
into your project's build path, and if building from the command line, to reference the jar
in your *classpath*.

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

> Note: If you find any bugs or would like functionality that is missing, please create a new issue (https://github.com/phil-brown/javaQuery/issues).

Below are some of the most common tasks for which *javaQuery* can be used.
A sample application can also be found in the `samples` directory. The relevant code can be found
in [Example.java](https://github.com/phil-brown/javaQuery/blob/master/samples/javaQueryTest/src/Example.java).
You may also browse the *javadocs* [here](http://phil-brown.github.io/javaQuery/doc/).
Finally, most of the [jQuery API Documentation](http://api.jquery.com) is sufficient to explain the *javaQuery* API.

To **instantiate** a new *droidQuery*, you need to pass in a `Component`, or set of `Component`s. The
simplest way to create the instance is using the `with` static methods:

    $.with(Component);
    $.with(List<Component>);
    $.with(Component[]);
    
Once you have the *javaQuery* instance, you can either save it as a variable, or chain calls to manipulate
the selected `Component` or `Component`s.

**Ajax**

To perform an asynchronous network task, you can use *ajax*. The most straight-forward way to create and
start an ajax task is with the `$.ajax(AjaxOptions)` method. For example:

    $.ajax(new AjaxOptions().url("http://www.example.com")
                            .type("GET")
                            .dataType("text")
                            .success(new Function() {
                                @Override
                                public void invoke($ javaQuery, Object... params) {
                                    javaQuery.alert((String) params[0]);
                                }
                            }).error(new Function() {
                                @Override
                                public void invoke($ javaQuery, Object... params) {
                                    int statusCode = (Integer) params[1];
                                    String error = (String) params[2];
                                    Log.e("Ajax", statusCode + " " + error);
                                }
                            }));

**Logging**

*javaQuery* is packaged with an advanced logging library that uses ANSI to print formatted text. If your
console does not support ANSI, it is recommended you either install a plug-in (such as [this one](http://mihai-nita.net/java/)
for *Eclipse*), or simply add this call to disable ANSI output:

    Log.disableANSI();

**Attributes**

*javaQuery* can be used to get or change the attributes of its selected `Component`s. The most common
methods include `attr()` to get an attribute, `attr(String, Object)` to set an attribute, `val()` to
get the value of a UI element (such as `String` for `JLabel`s, etc), and `val(Object)` to set the value.

**Callbacks**

The *Callbacks* Object provides a simple way to manage and fire sets of callbacks. To get an instance
of this Object, use `$.Callbacks()`.

**Effects**

*javaQuery* can be used to animate the selected `Component`s. The simplest way to perform a custom animation
is by using the `animate(String, long, Easing, Function)` method. For example:

    $.with(myComponent).children().animate("{left: 100px, top: 100, width: 50%, height: 50% }", 400, Easing.LINEAR, new Function() {
    	@Override
    	public void invoke($ javaQuery, Object... params)
    	{
    		Log.info("animation complete");
    	}
    });

It can also be used to perform pre-configured animations, such as fades (using `fadeIn`, `fadeOut`, 
`fadeTo`, and `fadeToggle`) and slides (`slideUp`, `slideDown`, `slideLeft`, and `slideRight`).

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

**Selectors**

The real magic behind *javaQuery* is its ability to manipulate a set of UI elements at one instance.
a `Component` or a set of `Components`s can be passed to a *javaQuery* instance using any of the *with* methods,
or a new instance of *javaQuery* containing a set of *Component*s can be created using any of the selector
methods, including `view`, `child`, `parent`, `children`, `siblings`, `slice`, `selectAll`, `selectByType`,
`selectChildren`, `selectEmpties`, `selectFocused`, `selectHidden`, `selectVisible`, `id`, `selectComponentWithName`
`selectOnlyChilds`, and `selectParents`, among others.

**Miscellaneous**

*javaQuery* also comes with several methods that simplify a lot of common tasks. including:

* __map(String)/map(JSONObject)__ - converts a JSON String or a JSONObject to a Map Object
* __map(Entry...)__ - quickly make a Map Object
* __entry(String, Object)__ - quickly make a Map Entry Object
* __alert__ - show an alert dialog
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

**Special Thanks**

This project uses a modified version of the [Timing Framework](https://java.net/projects/timingframework) 
for core animations. Without this project, which is similar to *Android* libraries I am familiar with, this
port would have taken longer to complete.