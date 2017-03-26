// Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.tinyweb.testing;

import com.milk.tinyweb.FileDocument;
import com.milk.util.EmbeddedException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link FileDocument}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestFileDocument
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestFileDocument (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the constructor, including expected failures due to
     * bad arguments.
     */
    public void testConstructor ()
    {
	// should fail if file is null
	try
	{
	    new FileDocument (null, "foo");
	    fail ("constructor failed to fail");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed

	new FileDocument (new File ("foo"), null);
	new FileDocument (new File ("foo"), "foo");
    }

    /**
     * Test the method {@link FileDocument#getContentLength}. This
     * tests the <code>offset</code> query parameter, but not any others.
     */
    public void testGetContentLength ()
	throws Exception
    {
	int length = 0;
	while (length < 65000)
	{
	    File f = makeTempFile (randomBytes (length));
	    FileDocument fd = new FileDocument (f, null);
	    assertEquals (length, fd.getContentLength ());
	    fd = new FileDocument (f, "offset=100");
	    long expected = (length >= 100) ? length - 100 : 0;
	    assertEquals (expected, fd.getContentLength ());
	    fd = new FileDocument (f, "offset=4511");
	    expected = (length >= 4511) ? length - 4511 : 0;
	    assertEquals (expected, fd.getContentLength ());
	    length = length * 2 + 37;
	    f.delete ();
	}

	// now try the file-not-found case
	File f = makeTempFile (new byte[0]);
	f.delete ();
	FileDocument fd = new FileDocument (f, null);
	assertEquals (0, fd.getContentLength ());
    }

    /**
     * Test the method {@link FileDocument#writeBytes}. This tests the
     * <code>offset</code> query parameter, but not any others. 
     */
    public void testWriteBytes ()
	throws Exception
    {
	ByteArrayOutputStream baos = new ByteArrayOutputStream (32000);
	int length = 0;

	while (length < 65000)
	{
	    byte[] b = randomBytes (length);
	    File f = makeTempFile (b);

	    FileDocument fd = new FileDocument (f, null);
	    baos.reset ();
	    fd.writeBytes (baos, fd.getContentLength ());
	    assertTrue (Arrays.equals (b, baos.toByteArray ()));

	    fd = new FileDocument (f, "offset=831");
	    baos.reset ();
	    fd.writeBytes (baos, fd.getContentLength ());
	    assertTrue (Arrays.equals (expectedBytes (b, 831), 
				       baos.toByteArray ()));

	    fd = new FileDocument (f, "offset=-3412");
	    baos.reset ();
	    fd.writeBytes (baos, fd.getContentLength ());
	    assertTrue (Arrays.equals (expectedBytes (b, -3412), 
				       baos.toByteArray ()));

	    f.delete ();
	    length = length * 3 + 15;
	}

	// now try the file-not-found case
	File f = makeTempFile (new byte[0]);
	f.delete ();
	FileDocument fd = new FileDocument (f, null);
	baos.reset ();
	fd.writeBytes (baos, fd.getContentLength ());
	assertEquals (0, baos.size ());
    }

    /** 
     * Test the use of the <code>dynamic</code> query parameter.
     */
    public void testDynamic ()
	throws Exception
    {
	File f = makeTempFile (new byte[0]);

	FileDocument fd = new FileDocument (f, "dynamic=true");
	assertEquals (-1, fd.getContentLength ());

	new SlowWriter (f, 20);
	ByteArrayOutputStream baos = new ByteArrayOutputStream (30);
	fd.writeBytes (baos, -1);
	assertEquals (20, baos.size ());

	assertEquals (-1, fd.getContentLength ());
	new SlowWriter (f, 10);
	baos.reset ();
	fd.writeBytes (baos, -1);
	assertEquals (30, baos.size ());

	f.delete ();
    }



    // ------------------------------------------------------------------------
    // static private methods

    /**
     * Create a temporary file which contains the given contents and
     * return a <code>File</code> representing it.
     *
     * @param contents non-null; the contents of the file-to-be
     * @return non-null; a reference to the created file
     */
    static private File makeTempFile (byte[] contents)
	throws IOException
    {
	File f = File.createTempFile ("TestFileDocument", ".bin");
	f.deleteOnExit ();
	FileOutputStream fos = new FileOutputStream (f);
	fos.write (contents);
	fos.close ();
	return f;
    }

    /**
     * Return an array of random byte values of the given length.
     *
     * @param length the length of the result
     * @return non-null; an appropriately-constructed array of bytes
     */
    static private byte[] randomBytes (int length)
    {
	byte[] result = new byte[length];

	for (int i = 0; i < length; i++)
	{
	    result[i] = (byte) (Math.random () * 255);
	}

	return result;
    }

    /**
     * Return the array of bytes that should be expected if the
     * given offset is specified.
     *
     * @param bytes non-null; the bytes of the entire file
     * @param offset the offset into the file
     * @return non-null; the expected result of doing a get with
     * the given offset
     */
    static private byte[] expectedBytes (byte[] bytes, int offset)
    {
	int len = bytes.length;
	int startAt = (offset >= 0) ? offset : (len + offset);

	if (startAt < 0)
	{
	    startAt = 0;
	}
	else if (startAt >= len)
	{
	    return new byte[0];
	}

	byte[] result = new byte[len - startAt];
	System.arraycopy (bytes, startAt, result, 0, result.length);
	return result;
    }

    // ------------------------------------------------------------------------
    // private helper classes
    
    /**
     * A thread that immediately starts writing the given number of
     * bytes to the end of the given file, at a rate of about one character
     * every 5 msec. The value of all bytes written is <code>0x44</code>
     * (for no good reason).
     */
    private static class SlowWriter
	extends Thread
    {
	private File myFile;
	private int myCount;

	/**
	 * Construct an instance.
	 *
	 * @param f non-null; the file to write to
	 * @param count the number of bytes to write
	 */
	public SlowWriter (File f, int count)
	{
	    myFile = f;
	    myCount = count;
	    
	    setDaemon (true);
	    start ();
	}

	public void run ()
	{
	    try
	    {
		// you'd think that there'd be a constructor for
		// FileOutputStream that took a File object and an append
		// flag, wouldn't you? Well, you'd be wrong. Yay Java!
		FileOutputStream fos = 
		    new FileOutputStream (myFile.getPath (), true);

		for (int i = 0; i < myCount; i++)
		{
		    fos.write (0x44);
		    Thread.sleep (5);
		}

		fos.close ();
	    }
	    catch (IOException ex)
	    {
		throw new EmbeddedException (ex);
	    }
	    catch (InterruptedException ex)
	    {
		throw new EmbeddedException (ex);
	    }
	}
    }
}
