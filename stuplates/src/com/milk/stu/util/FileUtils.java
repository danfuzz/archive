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

package com.milk.stu.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Utility methods for dealing with file I/O.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FileUtils
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private FileUtils ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Open the given path for input. This will open it as a file, except
     * if it is <code>"-"</code>, which is taken to mean
     * <code>System.in</code>.
     *
     * @param path non-null; the path to open
     * @return non-null; the opened stream
     */
    static public InputStream openInput (String path)
    {
	if (path.equals ("-"))
	{
	    return System.in;
	}

	try
	{
	    return new FileInputStream (path);
	}
	catch (IOException ex)
	{
	    throw new RuntimeException ("trouble opening file: " + path, ex);
	}
    }

    /**
     * Open the given file for input. This will open it as normal, except
     * if its path is <code>"-"</code>, which is taken to mean
     * <code>System.in</code>.
     *
     * @param file non-null; the file to open
     * @return non-null; the opened stream
     */
    static public InputStream openInput (File file)
    {
	if (file.getPath ().equals ("-"))
	{
	    return System.in;
	}

	try
	{
	    return new FileInputStream (file);
	}
	catch (IOException ex)
	{
	    throw new RuntimeException ("trouble opening file: " + file, ex);
	}
    }

    /**
     * Close the given input stream.
     *
     * @param stream non-null; the stream to close
     */
    static public void closeInput (InputStream stream)
    {
	if (stream == System.in)
	{
	    return;
	}

	try
	{
	    stream.close ();
	}
	catch (IOException ex)
	{
	    throw new RuntimeException ("trouble closing stream: " + stream, 
					ex);
	}
    }

    /**
     * Open the given path for output. This will open it as a file, except
     * if it is <code>"-"</code>, which is taken to mean
     * <code>System.out</code>.
     *
     * @param path non-null; the path to open
     * @return non-null; the opened stream
     */
    static public OutputStream openOutput (String path)
    {
	if (path.equals ("-"))
	{
	    return System.out;
	}

	try
	{
	    return new FileOutputStream (path);
	}
	catch (IOException ex)
	{
	    throw new RuntimeException ("trouble opening file: " + path, ex);
	}
    }

    /**
     * Open the given file for output. This will open it as normal, except
     * if its path is <code>"-"</code>, which is taken to mean
     * <code>System.out</code>.
     *
     * @param file non-null; the file to open
     * @return non-null; the opened stream
     */
    static public OutputStream openOutput (File file)
    {
	if (file.getPath ().equals ("-"))
	{
	    return System.out;
	}

	try
	{
	    return new FileOutputStream (file);
	}
	catch (IOException ex)
	{
	    throw new RuntimeException ("trouble opening file: " + file, ex);
	}
    }

    /**
     * Close the given output stream.
     *
     * @param stream non-null; the stream to close
     */
    static public void closeOutput (OutputStream stream)
    {
	if (stream == System.out)
	{
	    return;
	}

	try
	{
	    stream.close ();
	}
	catch (IOException ex)
	{
	    throw new RuntimeException ("trouble closing stream: " + stream, 
					ex);
	}
    }

    /**
     * Read the given file, returning a <code>byte[]</code> of its contents.
     * The file should already be known to exist, be readable, and be a regular
     * file (not a directory or special file).
     *
     * @param file non-null; the file to read
     * @return non-null; its contents 
     */
    static public byte[] readFile (File file)
	throws IOException
    {
	if (file == null)
	{
	    throw new NullPointerException ("file == null");
	}

	long length = file.length ();
	int len = (int) length;

	if (length != len)
	{
	    // probably won't ever happen
	    throw new RuntimeException ("File too long: " + file);
	}

	byte[] buf = new byte[len];
	FileInputStream fis = new FileInputStream (file);
	
	int at = 0;
	while (at < len)
	{
	    int amt = fis.read (buf, at, len - at);
	    if (amt == -1)
	    {
		throw new RuntimeException ("Unexpected EOF at " + at + 
					    " in file: " + file);
	    }
	    at += amt;
	}

	return buf;
    }

    /**
     * Write the given buffer to the given file. The directory the file
     * will be in should already exist and be known to be writable.
     *
     * @param buf non-null; the buffer to write
     * @param file non-null; the file to write it to
     */
    static public void writeFile (byte[] buf, File file)
	throws IOException
    {
	if (buf == null)
	{
	    throw new NullPointerException ("buf == null");
	}

	if (file == null)
	{
	    throw new NullPointerException ("file == null");
	}

	OutputStream os = openOutput (file);
	os.write (buf);
	closeOutput (os);
    }

    /**
     * Write the given contents to the given file in the given encoding.
     * The directory the file will be in should already exist and be known
     * to be writable.
     *
     * @param chars non-null; the contents to write
     * @param enc non-null; the encoding name
     * @param file non-null; the file to write it to 
     */
    static public void writeFile (CharSequence chars, String enc, File file)
	throws IOException
    {
	if (chars == null)
	{
	    throw new NullPointerException ("chars == null");
	}

	if (enc == null)
	{
	    throw new NullPointerException ("enc == null");
	}

	if (file == null)
	{
	    throw new NullPointerException ("file == null");
	}

	OutputStream os = openOutput (file);
	OutputStreamWriter osw = new OutputStreamWriter (os, enc);

	if (chars instanceof CharSequenceList)
	{
	    ((CharSequenceList) chars).writeTo (osw);
	    osw.flush ();
	}
	else
	{
	    BufferedWriter bw = new BufferedWriter (osw);	
	    int len = chars.length ();
	    for (int i = 0; i < len; i++)
	    {
		bw.write (chars.charAt (i));
	    }
	    bw.flush ();
	}

	closeOutput (os);
    }

    /**
     * Make the directories for the given file to exist in, if they don't
     * already exist. That is, make directories for all but the last
     * component of the given file. Also make sure the final directory is
     * writable, throwing an exception if that is not the case.
     *
     * @param file non-null; the file to make directories for 
     */
    static public void makeDirsFor (File file)
    {
	if (file == null)
	{
	    throw new NullPointerException ("file == null");
	}

	File parent = file.getParentFile ();

	if (! parent.exists ())
	{
	    if (! parent.mkdirs ())
	    {
		throw new RuntimeException ("Unable to make directory: " +
					    parent);
	    }
	}

	if (! parent.isDirectory ())
	{
	    throw new RuntimeException ("Not a directory: " + parent);

	}

	if (! parent.canWrite ())
	{
	    throw new RuntimeException ("Unable to write to directory: " +
					parent);
	}
    }
}
