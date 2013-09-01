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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a Script, which consists of lines of commands
 * @author Phil Brown
 */
public class Script
{		
	/** The lines of code in this Script */
	private String[] commands;
	
	/**
	 * Constructor.
	 * @param commands the lines of code in the new Script
	 */
	public Script(String... commands)
	{
		this.commands = commands;
	}
	
	/**
	 * Constructor. Creates a Script given the File where the script is located
	 * @param file the Script file
	 * @return a new Script with the {@link #commands} set to the lines of code in {@code file}
	 * @throws FileNotFoundException if {@code file} does not exist
	 * @throws IOException if {@code file} does not exist 
	 *                  OR if this reader is closed or some other I/O error occurs.
	 *                  OR if an error occurs while closing this reader.
	 */
	public Script(File file) throws FileNotFoundException, IOException
	{
		this(new BufferedReader(new FileReader(file)));	
	}
	
	/**
	 * Private Constructor.
	 * @param br the buffered reader that reads in the code as a stream
	 * @throws IOException if {@code file} does not exist 
	 *                  OR if this reader is closed or some other I/O error occurs.
	 *                  OR if an error occurs while closing this reader.
	 */
	private Script(BufferedReader br) throws IOException
	{
		List<String> cmds = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null) {
			cmds.add(line);
		}
		br.close();
		commands = new String[cmds.size()];
		commands = cmds.toArray(commands);
	}
	
	/**
	 * @return the lines of code in this Script
	 */
	public String[] getCommands()
	{
		return commands;
	}
	
	/**
	 * Executes a script line by line in the Android Shell. If the first line
	 * begins with <em>#!</em>, the following text will be executed to open the appropriate shell.
	 * Note the Android Shell Path: <em>/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin</em>
	 * @param commands the list of commands to execute
	 * @param args arguments to pass to the shell script
	 * @throws IOException if the requested program can not be executed.
	 * @throws InterruptedException if the native thread is interrupted.
	 * @note The default shell is bourne (sh). There is no bash.
	 * @return output string, or null
	 */
	public String execute(String... args) throws IOException, InterruptedException
	{
		File file = File.createTempFile("script", ".tmp");
		
		$ javaQuery = new $();
		for (String s : getCommands())
		{
			javaQuery.write(s.getBytes(), file.getName(), true);
		}
		file.setExecutable(true);
		
		List<String> command = new ArrayList<String>();
		command.add(file.getAbsolutePath());
		for (String s : args)
		{
			command.add(s);
		}
		
		ProcessBuilder b = new ProcessBuilder(command);
	    Process process = b.start(); 
	    InputStream is = process.getInputStream();
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuilder output = new StringBuilder();
	    while ((line = br.readLine()) != null)
	    {
	        output.append("\n").append(line);
	    }
	    br.close();
	    is.close();
	    
	    file.delete();
	    
	    return output.toString();
	}
	
	/**
	 * Utility class for constructing a Shell Script from multiple text sources
	 * @author Phil Brown
	 */
	public static class ScriptBuilder
	{
		/** script commands */
		private List<String> commands;
		
		/**
		 * Constructor.
		 */
		public ScriptBuilder()
		{
			commands = new ArrayList<String>();
		}
		
		/**
		 * Constructor. Takes the first String as the parameter
		 * @param first the first String in the script 
		 */
		public ScriptBuilder(String first)
		{
			this();
			commands.add(first);
		}
		
		/**
		 * Add a new command to the end of the list
		 * @param cmd the command to add
		 * @return <em>this</em> ScriptBuilder, so that these calls can be chained
		 */
		public ScriptBuilder addCommand(String cmd)
		{
			commands.add(cmd);
			return this;
		}
		
		/**
		 * Generates a Script using {@link #commands}
		 * @return a new Shell {@link Script} Object
		 */
		public Script toScript()
		{
			String[] cmds = new String[commands.size()];
			cmds = commands.toArray(cmds);
			return new Script(cmds);
		}
	}
}
