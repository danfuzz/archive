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

import com.milk.tinyweb.StaticDocument;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link StaticDocument}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestStaticDocument
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestStaticDocument (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the three-arg contentful constructor, including expected
     * failures due to bad arguments. 
     */
    public void testConstructor1 ()
    {
	// should fail if contentType is null
	try
	{
	    new StaticDocument (null, 0, new byte[0]);
	    fail ("constructor failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should fail if bytes is null
	try
	{
	    new StaticDocument ("text/plain", 0, null);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed
	new StaticDocument ("text/plain", 0, new byte[0]);
	new StaticDocument ("text/html", 0, new byte[100]);
    }

    /**
     * Test the six-arg constructor, including expected failures due to
     * bad arguments.
     */
    public void testConstructor2 ()
    {
	// should fail if contentType is null
	try
	{
	    new StaticDocument (null, 0, new byte[0], 200, "OK", null);
	    fail ("constructor failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should fail if bytes is null
	try
	{
	    new StaticDocument ("text/plain", 0, null, 200, "OK", null);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should fail if the code is out of range
	try
	{
	    new StaticDocument ("text/plain", 0, new byte[0], 99, "OK", null);
	    fail ("constructor failed to fail (3)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// should fail if the result message is null
	try
	{
	    new StaticDocument ("text/plain", 0, new byte[0], 100, null, null);
	    fail ("constructor failed to fail (4)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed
	new StaticDocument ("text/plain", 0, new byte[0], 101, "Foo", null);
	new StaticDocument ("text/html", 0, new byte[100], 500, "Blort", null);
	new StaticDocument ("text/html", 0, new byte[10], 500, "Blort",
			    new HashMap ());
    }

    /**
     * Test the three-arg no-content constructor, including expected
     * failures due to bad arguments. 
     */
    public void testConstructor3 ()
    {
	// should fail if the code is out of range
	try
	{
	    new StaticDocument (0, 99, "OK");
	    fail ("constructor failed to fail (1)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// should fail if the result message is null
	try
	{
	    new StaticDocument (0, 299, null);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed
	new StaticDocument (0, 100, "Foo");
	new StaticDocument (0, 320, "Zorch");
    }

    /**
     * Test the four-arg no-content constructor, including expected
     * failures due to bad arguments. 
     */
    public void testConstructor4 ()
    {
	HashMap eh = new HashMap ();

	// should fail if the code is out of range
	try
	{
	    new StaticDocument (0, 99, "OK", eh);
	    fail ("constructor failed to fail (1)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// should fail if the result message is null
	try
	{
	    new StaticDocument (0, 299, null, eh);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// the rest should succeed
	new StaticDocument (0, 100, "Foo", eh);
	new StaticDocument (0, 320, "Zorch", eh);
	new StaticDocument (0, 100, "Foo", null);
	new StaticDocument (0, 320, "Zorch", null);
    }

    /**
     * Test the method {@link StaticDocument#getContentType}.
     */
    public void testGetContentType ()
    {
	byte[] bytes = new byte[5];

	StaticDocument sd = new StaticDocument ("text/plain", 0, bytes);
	assertEquals ("text/plain", sd.getContentType ());

	sd = new StaticDocument ("fooblort", 0, bytes);
	assertEquals ("fooblort", sd.getContentType ());
    }

    /**
     * Test the method {@link StaticDocument#getLastModified}.
     */
    public void testGetLastModified ()
    {
	byte[] bytes = new byte[5];

	for (long lm = 0; lm < 1000000; lm = lm * 7 + 12341)
	{
	    StaticDocument sd = new StaticDocument ("text/plain", lm, bytes);
	    assertEquals (lm, sd.getLastModified ());
	}
    }

    /**
     * Test the method {@link StaticDocument#getResultCode}.
     */
    public void testResultCode ()
    {
	StaticDocument sd = new StaticDocument ("text/plain", 0, new byte[0]);
	assertEquals (200, sd.getResultCode ());

	sd = new StaticDocument ("text/plain", 0, new byte[0], 321, "Foo",
				 null);
	assertEquals (321, sd.getResultCode ());
    }

    /**
     * Test the method {@link StaticDocument#getResultMsg}.
     */
    public void testResultMsg ()
    {
	StaticDocument sd = new StaticDocument ("text/plain", 0, new byte[0]);
	assertEquals ("OK", sd.getResultMsg ());

	sd = new StaticDocument ("text/plain", 0, new byte[0], 321, "Foo",
				 null);
	assertEquals ("Foo", sd.getResultMsg ());
    }

    /**
     * Test the method {@link StaticDocument#getExtraHeaders}.
     */
    public void testGetExtraHeaders ()
    {
	StaticDocument sd = new StaticDocument (10032, 200, "OK");
	assertNull (sd.getExtraHeaders ());

	HashMap eh = new HashMap ();
	eh.put ("Foo", "Fooblort");
	eh.put ("Bar", "Barblort");

	sd = new StaticDocument (10032, 200, "OK", eh);
	Map eh2 = sd.getExtraHeaders ();
	
	assertEquals (2, eh2.size ());
	assertEquals ("Fooblort", eh2.get ("Foo"));
	assertEquals ("Barblort", eh2.get ("Bar"));
    }

    /**
     * Test the method {@link StaticDocument#getContentLength}.
     */
    public void testGetContentLength ()
    {
	int length = 0;
	while (length < 65000)
	{
	    byte[] bytes = new byte[length];
	    StaticDocument sd = new StaticDocument ("text/plain", 0, bytes);
	    assertEquals (length, sd.getContentLength ());
	    length = length * 5 + 2;
	}
    }

    /**
     * Test the method {@link StaticDocument#writeBytes}.
     */
    public void testWriteBytes ()
	throws Exception
    {
	ByteArrayOutputStream baos = new ByteArrayOutputStream (32000);
	int length = 0;

	while (length < 65000)
	{
	    byte[] b = new byte[length];
	    for (int i = 0; i < length; i++)
	    {
		b[i] = (byte) i;
	    }

	    StaticDocument sd = new StaticDocument ("text/plain", 0, b);
	    baos.reset ();
	    sd.writeBytes (baos, sd.getContentLength ());
	    assertTrue (Arrays.equals (b, baos.toByteArray ()));

	    length = length * 2 + 91;
	}
    }
}
