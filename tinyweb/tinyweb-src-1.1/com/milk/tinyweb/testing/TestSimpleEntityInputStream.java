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

import com.milk.tinyweb.SimpleEntityInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link SimpleEntityInputStream}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestSimpleEntityInputStream
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestSimpleEntityInputStream (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the constructor, including expected failures.
     */
    public void testConstructor ()
    {
	// null stream should fail
	try
	{
	    new SimpleEntityInputStream (null, 1);
	    fail ("constructor failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// negative length should fail too
	try
	{
	    new SimpleEntityInputStream (
	        new ByteArrayInputStream (new byte[0]), 
		-1);
	    fail ("constructor failed to fail (1)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// this should work
	new SimpleEntityInputStream (
	    new ByteArrayInputStream (new byte[0]), 
	    1);
    }

    /**
     * Test the method {@link SimpleEntityInputStream#available}.
     */
    public void testAvailable ()
	throws IOException
    {
	// available should be pegged at the content length, even if the
	// underlying stream has additional data

	SimpleEntityInputStream seis =
	    new SimpleEntityInputStream (
	        new ByteArrayInputStream (new byte[100]), 
		10);
	assertEquals (10, seis.available ());

	// contrawise, if the underlying stream's available() reports less
	// than the content length, that's what should be reported

	seis =
	    new SimpleEntityInputStream (
	        new ByteArrayInputStream (new byte[5]), 
		17);
	assertEquals (5, seis.available ());
    }

    /**
     * Test the method {@link SimpleEntityInputStream#close}.
     */
    public void testClose ()
	throws IOException
    {
	// close should skip over the remainder of the content in
	// the underlying stream and subsequently cause most of the
	// rest of the methods to throw an IOException

	ByteArrayInputStream bais = new ByteArrayInputStream (new byte[52]);
	SimpleEntityInputStream seis = new SimpleEntityInputStream (bais, 10);
	seis.close ();
	assertEquals (42, bais.available ());

	try
	{
	    seis.available ();
	    fail ("available() failed to fail");
	}
	catch (IOException ex)
	{
	    // expected
	}

	try
	{
	    seis.read ();
	    fail ("read() failed to fail (1)");
	}
	catch (IOException ex)
	{
	    // expected
	}

	try
	{
	    seis.read (new byte[1], 0, 1);
	    fail ("read() failed to fail (2)");
	}
	catch (IOException ex)
	{
	    // expected
	}

	try
	{
	    seis.skip (1);
	    fail ("skip() failed to fail");
	}
	catch (IOException ex)
	{
	    // expected
	}
    }

    /**
     * Test the method {@link SimpleEntityInputStream#read()}.
     */
    public void testRead1 ()
	throws IOException
    {
	byte[] barr = new byte[] { 10, 20, 30, 40 };
	ByteArrayInputStream bais = new ByteArrayInputStream (barr);
	SimpleEntityInputStream seis = new SimpleEntityInputStream (bais, 3);

	assertEquals (10, seis.read ());
	assertEquals (20, seis.read ());
	assertEquals (30, seis.read ());
	assertEquals (-1, seis.read ());
    }

    /**
     * Test the method {@link SimpleEntityInputStream#read(byte,int,int)}.
     */
    public void testRead2 ()
	throws IOException
    {
	byte[] barr = new byte[] { 12, 34, 56, 78, 90, 120, 97 };
	ByteArrayInputStream bais = new ByteArrayInputStream (barr);
	SimpleEntityInputStream seis = new SimpleEntityInputStream (bais, 5);

	// should error out of the byte[] is passed as null
	try
	{
	    seis.read (null, 0, 1);
	    fail ("read() failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should always return 0 if the length requested is 0
	assertEquals (0, seis.read (new byte[1], 0, 0));

	// should error out if the final offset into the array is
	// out of bounds
	try
	{
	    seis.read (new byte[4], 3, 2);
	    fail ("read() failed to fail (2)");
	}
	catch (IndexOutOfBoundsException ex)
	{
	    // expected
	}

	// stuff that should work

	byte[] res = new byte[5];

	assertEquals (2, seis.read (res, 1, 2));
	assertEquals (0,  res[0]);
	assertEquals (12, res[1]);
	assertEquals (34, res[2]);
	assertEquals (0,  res[3]);
	assertEquals (0,  res[4]);

	assertEquals (1, seis.read (res, 4, 1));
	assertEquals (0,  res[0]);
	assertEquals (12, res[1]);
	assertEquals (34, res[2]);
	assertEquals (0,  res[3]);
	assertEquals (56, res[4]);

	assertEquals (2, seis.read (res, 0, 5));
	assertEquals (78, res[0]);
	assertEquals (90, res[1]);
	assertEquals (34, res[2]);
	assertEquals (0,  res[3]);
	assertEquals (56, res[4]);

	assertEquals (-1, seis.read (res, 0, 1));
    }

    /**
     * Test the method {@link SimpleEntityInputStream#skip}.
     */
    public void testSkip ()
	throws IOException
    {
	byte[] barr = 
	    new byte[] { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50,
	                 55, 60, 65, 70 };
	ByteArrayInputStream bais = new ByteArrayInputStream (barr);
	SimpleEntityInputStream seis = new SimpleEntityInputStream (bais, 10);

	assertEquals (1, seis.skip (1));
	assertEquals (2, seis.skip (2));
	assertEquals (20, seis.read ());
	assertEquals (6, seis.skip (7));
	assertEquals (0, seis.skip (100));
    }
}
