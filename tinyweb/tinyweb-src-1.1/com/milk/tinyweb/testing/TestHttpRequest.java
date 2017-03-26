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

import com.milk.tinyweb.HttpRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link HttpRequest}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestHttpRequest
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestHttpRequest (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the normal constructor, including expected failures due to
     * bad arguments.
     */
    public void testNormalConstructor ()
    {
	// should fail for various null arguments

	try
	{
	    new HttpRequest (null, 
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     null);
	    fail ("constructor failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    new HttpRequest ("1.1", 
			     HttpRequest.REQUEST_GET,
			     null,
			     new String[0],
			     new String[0],
			     null);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    new HttpRequest ("1.1", 
			     HttpRequest.REQUEST_GET,
			     "/",
			     null,
			     new String[0],
			     null);
	    fail ("constructor failed to fail (3)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    new HttpRequest ("1.1", 
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     null,
			     null);
	    fail ("constructor failed to fail (4)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// tests for other bad arguments

	// mismatch of header array lengths
	try
	{
	    new HttpRequest ("1.1", 
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[1],
			     null);
	    fail ("constructor failed to fail (5)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// REQUEST_ constant out of range
	try
	{
	    new HttpRequest ("1.1", 
			     0,
			     "/",
			     new String[0],
			     new String[0],
			     null);
	    fail ("constructor failed to fail (6)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// REQUEST_ constant out of range
	try
	{
	    new HttpRequest ("1.1", 
			     4,
			     "/",
			     new String[0],
			     new String[0],
			     null);
	    fail ("constructor failed to fail (7)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// the rest should work

	new HttpRequest ("1.1", 
			 HttpRequest.REQUEST_GET,
			 "/",
			 new String[0],
			 new String[0],
			 null);

	new HttpRequest ("1.1", 
			 HttpRequest.REQUEST_GET,
			 "/",
			 new String[0],
			 new String[0],
			 new ByteArrayInputStream (new byte[0]));
    }

    /**
     * Test the error instance constructor, including expected failures due
     * to bad arguments. 
     */
    public void testErrorConstructor ()
    {
	// should fail for various null arguments

	try
	{
	    new HttpRequest (null, 100, "foo");
	    fail ("constructor failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    new HttpRequest ("1.1", 100, null);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should fail if the code is out of range

	try
	{
	    new HttpRequest ("1.1", 99, null);
	    fail ("constructor failed to fail (3)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	try
	{
	    new HttpRequest ("1.1", 600, null);
	    fail ("constructor failed to fail (4)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	// should work

	new HttpRequest ("1.1", 500, "Leche!");
    }

    /**
     * Test the method {@link HttpRequest#getHttpVersion}.
     */
    public void testGetHttpVersion ()
    {
	// note use of new String to force it to be uninterned as a
	// parameter

	HttpRequest r = 
	    new HttpRequest (new String ("1.1"), 
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     null);

	assertSame ("1.1", r.getHttpVersion ());

	r = new HttpRequest (new String ("1.0"), 
			     500,
			     "Blort");

	assertSame ("1.0", r.getHttpVersion ());
    }

    /**
     * Test the method {@link HttpRequest#getRequestMethod}.
     */
    public void testGetRequestMethod ()
    {
	HttpRequest r = 
	    new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     null);

	assertEquals (HttpRequest.REQUEST_GET, r.getRequestMethod ());

	r = new HttpRequest (new String ("1.0"), 
			     500,
			     "Blort");

	assertEquals (HttpRequest.REQUEST_ERROR, r.getRequestMethod ());
    }

    /**
     * Test the method {@link HttpRequest#getPath}.
     */
    public void testGetPath ()
    {
	HttpRequest r = 
	    new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/foo/bar",
			     new String[0],
			     new String[0],
			     null);
	assertEquals ("/foo/bar", r.getPath ());

	// test canonicalization a couple ways

	r = new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/foo//bar",
			     new String[0],
			     new String[0],
			     null);
	assertEquals ("/foo/bar", r.getPath ());

	r = new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/foo/biff/../bar",
			     new String[0],
			     new String[0],
			     null);
	assertEquals ("/foo/bar", r.getPath ());

	r = new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/foo/./bar",
			     new String[0],
			     new String[0],
			     null);
	assertEquals ("/foo/bar", r.getPath ());
    }

    /**
     * Test the method {@link HttpRequest#getErrorCode}.
     */
    public void testGetErrorCode ()
    {
	HttpRequest r = 
	    new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     null);

	assertEquals (0, r.getErrorCode ());

	r = new HttpRequest (new String ("1.0"), 
			     123,
			     "Blort");

	assertEquals (123, r.getErrorCode ());
    }

    /**
     * Test the method {@link HttpRequest#getErrorMsg}.
     */
    public void testGetErrorMsg ()
    {
	HttpRequest r = 
	    new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     null);

	assertNull (r.getErrorMsg ());

	r = new HttpRequest (new String ("1.0"), 
			     123,
			     "Blort");

	assertEquals ("Blort", r.getErrorMsg ());
    }

    /**
     * Test the method {@link HttpRequest#getHeader}.
     */
    public void testGetHeader ()
    {
	HttpRequest r = 
	    new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[] { "fooey", "barry" },
			     new String[] { "foo",   "bar" },
			     null);

	// test existing headers
	assertEquals ("foo", r.getHeader ("fooey"));
	assertEquals ("bar", r.getHeader ("barry"));

	// test case-insensitivity
	assertEquals ("bar", r.getHeader ("BARry")); 

	// test non-existent headers
	assertNull (r.getHeader ("bazzy"));
    }

    /**
     * Test the method {@link HttpRequest#getEntityInputStream}.
     */
    public void testGetEntityInputStream ()
	throws Exception
    {
	// note: we don't test the returned stream very thoroughly;
	// the class TestSimpleEntityInputStream (possibly etc.)
	// should be doing that

	// stream should be null if passed in as null
	HttpRequest r = 
	    new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     null);
	assertNull (r.getEntityInputStream ());

	// stream should be null if there are no entity-oriented
	// headers
	r = new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[0],
			     new String[0],
			     new ByteArrayInputStream (new byte[0]));
	assertNull (r.getEntityInputStream ());

	// stream should be non-null (and appear to read the right stuff)
	// if there is a "Content-Length" header
	byte[] stuff = new byte[] { 1, 2, 3, 4, 7 };
	r = new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[] { "Content-Length" },
			     new String[] { "5" },
			     new ByteArrayInputStream (stuff));
	InputStream s = r.getEntityInputStream ();
	assertNotNull (s);
	assertEquals (1, s.read ());
	assertEquals (2, s.read ());
	assertEquals (3, s.read ());
	assertEquals (4, s.read ());
	assertEquals (7, s.read ());

	// ditto, if there's *also* a "Transfer-Encoding" header with
	// the value "identity"
	r = new HttpRequest ("1.1",
			     HttpRequest.REQUEST_GET,
			     "/",
			     new String[] { "Content-Length", 
			                    "Transfer-Encoding" },
			     new String[] { "5",
			                    "identity" },
			     new ByteArrayInputStream (stuff));
	s = r.getEntityInputStream ();
	assertNotNull (s);
	assertEquals (1, s.read ());
	assertEquals (2, s.read ());
	assertEquals (3, s.read ());
	assertEquals (4, s.read ());
	assertEquals (7, s.read ());
    }

    /**
     * Test the method {@link HttpRequest#read}.
     */
    public void testRead ()
	throws Exception
    {
	// note: this doesn't check all the error cases

	// a good GET request
	String rstr = 
	    "GET /foo/bar HTTP/1.1\r\n" +
	    "Host: localhost\r\n" +
	    "\r\n";
	HttpRequest req = 
	    HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertEquals (HttpRequest.REQUEST_GET, req.getRequestMethod ());
	assertEquals ("/foo/bar", req.getPath ());
	assertSame ("1.1", req.getHttpVersion ());
	assertEquals ("localhost", req.getHeader ("host"));
	assertNull (req.getEntityInputStream ());

	// a good HEAD request
	rstr = 
	    "HEAD /biff.txt HTTP/1.0\r\n" +
	    "Blort: biff\r\n" +
	    "  zip\r\n" +
	    "\r\n";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertEquals (HttpRequest.REQUEST_HEAD, req.getRequestMethod ());
	assertEquals ("/biff.txt", req.getPath ());
	assertSame ("1.0", req.getHttpVersion ());
	assertEquals ("biff zip", req.getHeader ("blort"));
	assertNull (req.getEntityInputStream ());

	// a good POST request
	rstr = 
	    "POST /form-me-baby HTTP/1.1\r\n" +
	    "Host:    localhost    \r\n" +
	    "Content-Length: 10\r\n" +
	    "\r\n" +
	    "abcdeabcde";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertEquals (HttpRequest.REQUEST_POST, req.getRequestMethod ());
	assertEquals ("/form-me-baby", req.getPath ());
	assertSame ("1.1", req.getHttpVersion ());
	assertEquals ("localhost", req.getHeader ("host"));
	assertEquals ("10", req.getHeader ("content-length"));
	InputStream s = req.getEntityInputStream ();
	assertNotNull (s);
	assertEquals ((int) 'a', s.read ());
	assertEquals ((int) 'b', s.read ());
	assertEquals ((int) 'c', s.read ());
	assertEquals ((int) 'd', s.read ());
	assertEquals ((int) 'e', s.read ());
	assertEquals ((int) 'a', s.read ());
	assertEquals ((int) 'b', s.read ());
	assertEquals ((int) 'c', s.read ());
	assertEquals ((int) 'd', s.read ());
	assertEquals ((int) 'e', s.read ());

	// bad request method
	rstr = 
	    "BLORT /foo/bar HTTP/1.1\r\n" +
	    "Host: localhost\r\n" +
	    "\r\n";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertTrue (req.getErrorCode () != 0);

	// bad request line
	rstr = 
	    "GET zipple\r\n" +
	    "Host: localhost\r\n" +
	    "\r\n";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertTrue (req.getErrorCode () != 0);

	// bad header line
	rstr = 
	    "GET /foo/bar HTTP/1.1\r\n" +
	    " stuff\r\n" +
	    "\r\n";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertTrue (req.getErrorCode () != 0);

	// another bad header line
	rstr = 
	    "GET /foo/bar HTTP/1.1\r\n" +
	    "Foo: bar\r\n" +
	    "foozort fantastic\r\n" +
	    "\r\n";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertTrue (req.getErrorCode () != 0);

	// yet another bad header line
	rstr = 
	    "GET /foo/bar HTTP/1.1\r\n" +
	    "Foo: bar";
	req = HttpRequest.read (new ByteArrayInputStream (rstr.getBytes ()));
	assertTrue (req.getErrorCode () != 0);
    }
}
