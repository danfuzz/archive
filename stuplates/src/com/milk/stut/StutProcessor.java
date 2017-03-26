// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
// reserved, except as follows:
// 
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the condition that the above
// copyright notice and this permission notice shall be included in all copies
// or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.milk.stut;

import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Names;
import com.milk.stu.iface.StuNode;
import com.milk.stu.parser.Parser;
import com.milk.stu.util.FileNames;
import com.milk.stu.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

/**
 * Main processor for a tree of <code>.stut</code> files.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class StutProcessor
{
    /** non-null; the standard name for a preface file */
    static private final String PREFACE_FILE_NAME = "_preface.stu";

    /** non-null; the standard prefix for a file to be ignored */
    static private final String IGNORED_PREFIX = "_";

    /** non-null; path to the base source directory */
    private String myBaseSourceDirectory;

    /** null-ok; path to the base output directory, if outputting
     * a directory */
    private String myBaseOutputDirectory;

    /** non-null; the initial environment to use */
    private Environment myEnvironment;

    /** null-ok; path to the output file, if outputting a file */
    private String myOutputFileName;

    /** null-ok; the stream to output to, if outputting a file */
    private OutputStream myOutputStream;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param baseSourceDirectory non-null; path to the base source
     * directory 
     * @param outputPath non-null; output path (directory or file name)
     * @param outputToFile whether to output to a simple file
     * (<code>true</code>) or to a directory (<code>false</code>) 
     * @param environment non-null; the initial (top-level) variable
     * environment
     */
    public StutProcessor (String baseSourceDirectory, 
			  String outputPath, boolean outputToFile,
			  Environment environment)
    {
	if (baseSourceDirectory == null)
	{
	    throw new NullPointerException ("baseSourceDirectory == null");
	}
	    
	if (outputPath == null)
	{
	    throw new NullPointerException ("outputPath == null");
	}

	if (environment == null)
	{
	    throw new NullPointerException ("environment == null");
	}
	    
	myBaseSourceDirectory = baseSourceDirectory;
	myEnvironment = environment;
	myBaseOutputDirectory = null;
	myOutputFileName = null;
	myOutputStream = null;

	if (outputToFile)
	{
	    myOutputFileName = outputPath;
	}
	else
	{	    
	    myBaseOutputDirectory = outputPath;
	}
    }



    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Run the processor on its tree.
     */
    public void run ()
	throws IOException
    {
	if (myOutputFileName != null)
	{
	    myOutputStream = FileUtils.openOutput (myOutputFileName);
	}

	processDirectory (null, new File (myBaseSourceDirectory));
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Issue a warning.
     *
     * @param msg non-null; the warning message
     */
    private void warning (String msg)
    {
	if (msg == null)
	{
	    throw new NullPointerException ("msg == null");
	}

	// xxx: something better?
	System.err.print ("WARNING: ");
	System.err.println (msg);
    }

    /**
     * Issue an error.
     *
     * @param msg non-null; the error message
     */
    private void error (String msg)
    {
	if (msg == null)
	{
	    throw new NullPointerException ("msg == null");
	}

	// xxx: something better?
	System.err.print ("ERROR: ");
	System.err.println (msg);
    }

    /**
     * Process the named file (or directory), which is a relative path from the
     * base. This method itself merely determines whether it's a good file or
     * directory and then dispatches to another method as appropriate.
     *
     * @param path non-null; the relative path of the file to process
     */
    private void processOne (String path)
	throws IOException
    {
	if (path == null)
	{
	    throw new NullPointerException ("path == null");
	}

	File source = new File (myBaseSourceDirectory, path);
	String baseName = source.getName ();

	if (! source.exists ())
	{
	    warning ("Ignoring nonexistent file: " + path);
	    return;
	}

	if (isIgnoredName (baseName))
	{
	    // then do nothing, intentionally
	    return;
	}

	if (! source.canRead ())
	{
	    warning ("Ignoring unreadable file: " + path);
	}

	System.err.println ("Processing " + path + "...");

	if (source.isDirectory ())
	{
	    processDirectory (path, source);
	}
	else if (source.isFile ())
	{
	    String stutOut = FileNames.stutOutputName (path);
	    String stuOut = FileNames.stuOutputName (path);
	    if (stutOut != null)
	    {
		processStutFile (source, stutOut);
	    }
	    else if (stuOut != null)
	    {
		processStuFile (source, stuOut);
	    }
	    else
	    {
		copyFile (path, source);
	    }
	}
	else
	{
	    warning ("Ignoring special file: " + source);
	}
    }

    /**
     * Write the given buffer to the given output path, or to the output
     * stream if not writing to a directory tree.
     *
     * @param buf non-null; the buffer to write
     * @param outputPath non-null; the file output path
     */
    private void writeOutput (byte[] buf, String outputPath)
	throws IOException
    {
	if (buf == null)
	{
	    throw new NullPointerException ("buf == null");
	}

	if (outputPath == null)
	{
	    throw new NullPointerException ("outputPath == null");
	}

	if (myOutputStream != null)
	{
	    // write it to the output stream
	    myOutputStream.write (buf);
	}
	else
	{
	    // write it out in the output tree
	    File output = new File (myBaseOutputDirectory, outputPath);
	    FileUtils.makeDirsFor (output);
	    FileUtils.writeFile (buf, output);
	}
    }

    /**
     * Write the given chars to the given output path, or to the output
     * stream if not writing to a directory tree.
     *
     * @param chars non-null; the chars to write
     * @param outputPath non-null; the file output path
     */
    private void writeOutput (CharSequence chars, String outputPath)
	throws IOException
    {
	if (chars == null)
	{
	    throw new NullPointerException ("chars == null");
	}

	if (outputPath == null)
	{
	    throw new NullPointerException ("outputPath == null");
	}

	if (myOutputStream != null)
	{
	    // write it to the output stream
	    OutputStreamWriter osw = 
		new OutputStreamWriter (myOutputStream, "UTF-8");
	    osw.write (chars.toString ());
	    osw.flush ();
	}
	else
	{
	    // write it out in the output tree
	    File output = new File (myBaseOutputDirectory, outputPath);
	    FileUtils.makeDirsFor (output);
	    FileUtils.writeFile (chars, "UTF-8", output);
	}
    }

    /**
     * Copy the given file. It is known to exist, be readable, and be
     * a regular file (not a directory). Write a copy of it to the
     * analogous directory in the output tree.
     *
     * @param path non-null; the relative path of the file to process
     * @param source non-null; the source file
     */
    private void copyFile (String path, File source)
	throws IOException
    {
	if (path == null)
	{
	    throw new NullPointerException ("path == null");
	}

	if (source == null)
	{
	    throw new NullPointerException ("source == null");
	}

	// read the file into a byte[] buffer
	byte[] sourceBuf = FileUtils.readFile (source);

	// and write it back out
	writeOutput (sourceBuf, path);
    }

    /**
     * Make an environment based on all the preface files in the directories
     * leading to the given file.
     *
     * @param file non-null; the file
     * @return non-null; the environment to use
     */
    private Environment makeEnvironmentFor (File file)
    {
	if (file == null)
	{
	    throw new NullPointerException ("file == null");
	}

	// gather a list of all the preface files
	LinkedList prefaceList = new LinkedList ();
	File base = new File (myBaseSourceDirectory);
	while (! file.equals (base))
	{
	    file = file.getParentFile ();
	    File one = new File (file, PREFACE_FILE_NAME);
	    if (one.isFile () && one.canRead ())
	    {
		prefaceList.add (0, one);
	    }
	}

	// build up the environment by evaluating the prefaces from top
	// to bottom
	Environment environment = myEnvironment.makeChild ();
	while (prefaceList.size () != 0)
	{
	    File one = (File) prefaceList.remove (0);
	    StuNode node = Parser.parseScript (one);
	    node.evalOutside (environment);
	}

	return environment;
    }

    /**
     * Process the given template file. It is known to exist, be readable,
     * and be a regular file (not a directory). Write the output to given
     * path into the output tree.
     *
     * @param source non-null; the source file
     * @param outputPath non-null; the (partial) path to the output file 
     */
    private void processStutFile (File source, String outputPath)
	throws IOException
    {
	if (source == null)
	{
	    throw new NullPointerException ("source == null");
	}

	if (outputPath == null)
	{
	    throw new NullPointerException ("outputPath == null");
	}

	// make the environment for the source file
	Environment environment = makeEnvironmentFor (source);
	environment.defineAlways (Names.VAR_outputPath, outputPath);

	// now evaluate the file itself
	StuNode node = Parser.parseTemplate (source);
	CharSequence result = node.evalOutsideToChars (environment);

	// and write out the results
	writeOutput (result, outputPath);
    }

    /**
     * Process the given script file. It is known to exist, be readable,
     * and be a regular file (not a directory). Write the output to given
     * path into the output tree.
     *
     * @param source non-null; the source file
     * @param outputPath non-null; the (partial) path to the output file 
     */
    private void processStuFile (File source, String outputPath)
	throws IOException
    {
	if (source == null)
	{
	    throw new NullPointerException ("source == null");
	}

	if (outputPath == null)
	{
	    throw new NullPointerException ("outputPath == null");
	}

	// make the environment for the source file
	Environment environment = makeEnvironmentFor (source);

	// now evaluate the file itself
	StuNode node = Parser.parseScript (source);
	CharSequence result = node.evalOutsideToChars (environment);

	// and write out the results
	writeOutput (result, outputPath);
    }

    /**
     * Process the given directory. It is known to exist, be readable, and
     * be a directory. Simply recurse by calling to process each file in
     * the directory.
     *
     * @param path null-ok; the relative path of the directory to process, or
     * <code>null</code> to indicate the top-level of the tree
     * @param source non-null; the source directory
     */
    private void processDirectory (String path, File source)
	throws IOException
    {
	if (path == null)
	{
	    path = "";
	}
	else
	{
	    path += File.separatorChar;
	}

	String[] names = source.list ();

	for (int i = 0; i < names.length; i++)
	{
	    processOne (path + names[i]);
	}
    }



    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Return whether or not the given name is the name of a file to be
     * ignored (not copied or processed in any way).
     *
     * @param name non-null; the file name in question
     * @return <code>true</code> if the name indicates an ignorable file
     */
    static private boolean isIgnoredName (String name)
    {
	// xxx: should be more generic
	return name.startsWith (IGNORED_PREFIX) ||
	    name.equals ("CVS");
    }
}
