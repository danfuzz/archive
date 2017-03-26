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

import com.milk.tinyweb.Document;
import com.milk.tinyweb.HttpRequest;
import com.milk.tinyweb.MethodHandler;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link MethodHandler}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestMethodHandler
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestMethodHandler (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the constructor, including expected error cases
     */
    public void testConstructor ()
    {
	// null args should fail
	
	try
	{
	    new MethodHandler (null, "foo");
	    fail ("constructor failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}
	
	try
	{
	    new MethodHandler (new Object (), null);
	    fail ("constructor failed to fail (2)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should also fail if the object doesn't have the named method,
	// or it has the named method but not with the right signature

	try
	{
	    new MethodHandler (new Object (), "foo");
	    fail ("constructor failed to fail (3)");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}

	try
	{
	    new MethodHandler (new Object (), "toString");
	    fail ("constructor failed to fail (4)");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}

	// this should work

	new MethodHandler (new MethBunch (), "foo");
    }

    /**
     * Test the method {@link MethodHandler#putDocument}. This
     * just makes sure it fails, since it's not supposed to be supported.
     */
    public void testPutDocument ()
    {
	MethodHandler mh = new MethodHandler (new MethBunch (), "foo");

	try
	{
	    mh.putDocument ("foo", mh);
	    fail ("putDocument failed to fail");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}
    }

    /**
     * Test the method {@link MethodHandler#handleRequest}.
     */
    public void testHandleRequest ()
    {
	MethBunch mb = new MethBunch ();
	HttpRequest req = new HttpRequest ("1.1", 321, "Blort!");

	Document doc =
	    new MethodHandler (mb, "foo").handleRequest ("query", "path", req);
	assertSame ("query", mb.getQuery ());
	assertSame ("path", mb.getPartialPath ());
	assertSame (req, mb.getRequest ());
	assertSame ("foo", mb.getMethodName ());
	assertSame (doc, mb.getDocument ());
	
	doc =
	    new MethodHandler (mb, "bar").handleRequest ("zort", "narf", req);
	assertSame ("zort", mb.getQuery ());
	assertSame ("narf", mb.getPartialPath ());
	assertSame (req, mb.getRequest ());
	assertSame ("bar", mb.getMethodName ());
	assertSame (doc, mb.getDocument ());
	assertSame (doc, mb.getDocument ());

	doc = 
	    new MethodHandler (mb, "baz").handleRequest ("zort", "narf", req);
	assertSame ("baz", mb.getMethodName ());
	assertSame (doc, mb.getDocument ());
    }



    // ------------------------------------------------------------------------
    // private helper classes

    /**
     * Class that has appropriate methods to be wrapped in an instance
     * of {@link MethodHandler}.
     */
    public class MethBunch
    {
	private String myQuery;
	private String myPartialPath;
	private HttpRequest myRequest;
	private String myMethodName;
	private Document myDocument;

	public MethBunch ()
	{
	    // this space intentionally left blank
	}

	public String getQuery ()
	{
	    return myQuery;
	}

	public String getPartialPath ()
	{
	    return myPartialPath;
	}

	public HttpRequest getRequest ()
	{
	    return myRequest;
	}

	public String getMethodName ()
	{
	    return myMethodName;
	}

	public Document getDocument ()
	{
	    return myDocument;
	}

	public Document foo (String q, String p, HttpRequest r)
	{
	    myMethodName = "foo";
	    myQuery = q;
	    myPartialPath = p;
	    myRequest = r;
	    myDocument = Document.makeText ("fooey");
	    return myDocument;
	}

	public Document bar (String q, String p, HttpRequest r)
	{
	    myMethodName = "bar";
	    myQuery = q;
	    myPartialPath = p;
	    myRequest = r;
	    myDocument = Document.makeText ("barry bar bar");
	    return myDocument;
	}

	public Document baz (String q, String p, HttpRequest r)
	{
	    myMethodName = "baz";
	    myQuery = q;
	    myPartialPath = p;
	    myRequest = r;
	    myDocument = Document.makeText ("bazzerific");
	    return myDocument;
	}

	/** This is to make sure that the right overloaded method is
	 * chosen. */
	public void baz (String q)
	{
	    throw new Error ();
	}
    }
}
