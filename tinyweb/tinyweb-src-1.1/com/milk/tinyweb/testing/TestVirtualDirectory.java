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
import com.milk.tinyweb.StaticDocument;
import com.milk.tinyweb.VirtualDirectory;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link VirtualDirectory}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestVirtualDirectory
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestVirtualDirectory (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // public test methods

    /**
     * Test the constructor.
     */
    public void testConstructor ()
    {
	new VirtualDirectory ();
	// that was easy
    }

    /**
     * Test the method {@link VirtualDirectory#putDocument}.
     */
    public void testPutDocument ()
    {
	VirtualDirectory vd = new VirtualDirectory ();

	// put a document at the root level
	vd.putDocument ("foo.html", Document.makeHTML ("foo"));

	// replace a document
	vd.putDocument ("foo.html", Document.makeHTML ("foo"));

	// implicitly create a subdirectory
	vd.putDocument ("foo/bar.html", Document.makeHTML ("bar"));

	// implicitly create a couple subdirectories
	vd.putDocument ("milk/is/yummy.html", Document.makeHTML ("milk"));

	// failure cases

	try
	{
	    vd.putDocument ("foo", null);
	    fail ("putDocument failed to fail (1)");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}

	try
	{
	    vd.putDocument (null, Document.makeHTML ("null!"));
	    fail ("putDocument failed to fail (2)");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}

	try
	{
	    vd.putDocument ("/blort", Document.makeHTML ("null!"));
	    fail ("putDocument failed to fail (3)");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}
    }

    /**
     * Test the method {@link FileSystemHandler#handleRequest}.
     */
    public void testHandleRequest ()
    {
	VirtualDirectory vd = new VirtualDirectory ();
	Document fooDoc = Document.makeHTML ("foo");
	Document barDoc = Document.makeHTML ("bar");
	Document indexDoc = Document.makeHTML ("index");
	vd.putDocument ("foo.html", fooDoc);
	vd.putDocument ("foo/bar.html", barDoc);
	vd.putDocument ("index.html", indexDoc);

	// error request; doesn't matter since it should be ignored
	HttpRequest req = new HttpRequest ("1.1", 500, "Boo");

	assertSame (fooDoc, vd.handleRequest (null, "foo.html", req));
	assertSame (barDoc, vd.handleRequest (null, "foo/bar.html", req));
	assertSame (indexDoc, vd.handleRequest (null, "index.html", req));
	assertSame (indexDoc, vd.handleRequest (null, "", req));
	assertNotNull (vd.handleRequest (null, "foo/", req));

	assertNull (vd.handleRequest (null, "blort", req));
	assertNull (vd.handleRequest (null, "blort/biff", req));
	assertNull (vd.handleRequest (null, "foo/blort", req));
    }
}
