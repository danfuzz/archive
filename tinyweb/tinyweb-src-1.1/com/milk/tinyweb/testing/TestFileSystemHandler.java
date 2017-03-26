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
import com.milk.tinyweb.FileDocument;
import com.milk.tinyweb.FileSystemHandler;
import com.milk.tinyweb.HttpRequest;
import com.milk.tinyweb.StaticDocument;
import com.milk.tinyweb.VirtualDirectory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import junit.framework.TestCase;

/**
 * Test cases for the class {@link FileSystemHandler}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestFileSystemHandler
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestFileSystemHandler (String name)
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
	    new FileSystemHandler (null);
	    fail ("constructor failed to fail");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// should succeed for a valid File object that simply
	// doesn't correspond to an existing file
	new FileSystemHandler (new File ("splazablooblesplort"));

	// and so we assume it will work for any non-null file
    }

    /**
     * Test the method {@link FileSystemHandler#putDocument}. This
     * just makes sure it fails, since it's not supposed to be supported.
     */
    public void testPutDocument ()
    {
	FileSystemHandler fsh = new FileSystemHandler (new File ("x"));

	try
	{
	    fsh.putDocument ("foo", new VirtualDirectory ());
	    fail ("putDocument failed to fail");
	}
	catch (RuntimeException ex)
	{
	    // expected
	}
    }

    /**
     * Test the method {@link FileSystemHandler#handleRequest}, for the cases
     * where the root file is invalid. In this case, the return value
     * should always be null.
     */
    public void testHandleRequestBadFile ()
	throws IOException
    {
	FileSystemHandler fsh;

	// error request; doesn't matter since it should be ignored
	HttpRequest req = new HttpRequest ("1.1", 500, "Boo");

	// the case where the file doesn't exist
	fsh = new FileSystemHandler (new File ("zubzubzubfooblizzlesplat"));
	assertNull (fsh.handleRequest (null, null, req));
	assertNull (fsh.handleRequest (null, "a", req));
	assertNull (fsh.handleRequest (null, "a/b", req));
	assertNull (fsh.handleRequest (null, "foo/bar/baz", req));
	assertNull (fsh.handleRequest ("dynamic=true", null, req));
	assertNull (fsh.handleRequest ("offset=25", "a", req));
	assertNull (fsh.handleRequest ("squidFactor=squiggly&price=right", 
				       "a/b",
				       req));
	assertNull (fsh.handleRequest ("milk=yummy+goodness", 
				       "foo/bar/baz",
				       req));

	// the case where the file exists but isn't a readable directory
	// (in this case, is readable but is a regular file)
	File f = File.createTempFile ("TestFileSystemHandler", ".txt");
	f.deleteOnExit ();
	fsh = new FileSystemHandler (f);
	assertNull (fsh.handleRequest (null, null, req)); // the root "file"
	assertNull (fsh.handleRequest (null, "z", req));
	assertNull (fsh.handleRequest (null, "m/q", req));
	assertNull (fsh.handleRequest (null, "milk/is/yummy.txt", req));
	assertNull (fsh.handleRequest ("offset=-300", null, req));
	assertNull (fsh.handleRequest ("dynamic=0", "z", req));
	assertNull (fsh.handleRequest ("beer=guinness&x=x+x+X", "m/q", req));
	assertNull (fsh.handleRequest ("a=1", "milk/is/yummy.txt", req));
	f.delete ();
    }

    /**
     * Test the method {@link FileSystemHandler#handleRequest}, for cases
     * where the base file is valid.
     */
    public void testHandleRequestGood ()
	throws IOException
    {
	// make a temporary directory; you'd think that there would
	// be an easier way to do this, wouldn't you? Well, you'd be
	// wrong. Yay Java!
	File f1 = File.createTempFile ("TestFileSystemHandler", "");
	f1.delete ();
	f1.mkdir ();
	f1.deleteOnExit ();

	// make a couple subdirectories and a couple files
	File f2 = new File (f1, "milk");
	f2.mkdir ();
	f2.deleteOnExit ();
	File f3 = new File (f2, "is");
	f3.mkdir ();
	f3.deleteOnExit ();
	File f4 = new File (f3, "yummy.txt");
	writeFile (f4, "Yummy.");
	f4.deleteOnExit ();
	File f5 = new File (f1, "leche.html");
	writeFile (f5, "<i>Leche.</i>");
	f5.deleteOnExit ();
	File f6 = new File (f1, "milche");
	writeFile (f6, "Ich bin ein Milche.");
	f6.deleteOnExit ();

	FileSystemHandler fsh = new FileSystemHandler (f1);

	// error request; doesn't matter since it should be ignored
	HttpRequest req = new HttpRequest ("1.1", 500, "Boo");

	// do basic checks that we get directory listings and
	// the right files

	// should get the main directory
	Document doc = fsh.handleRequest (null, null, req);
	assertTrue (doc instanceof StaticDocument);
	assertTrue (doc.getContentType ().startsWith ("text/html"));

	// try getting the subdirectories
	doc = fsh.handleRequest (null, "milk", req);
	assertTrue (doc instanceof StaticDocument);
	assertTrue (doc.getContentType ().startsWith ("text/html"));
	doc = fsh.handleRequest (null, "milk/is", req);
	assertTrue (doc instanceof StaticDocument);
	assertTrue (doc.getContentType ().startsWith ("text/html"));

	// try capricious %-encoding on the above
	doc = fsh.handleRequest (null, "m%69lk", req);
	assertTrue (doc instanceof StaticDocument);
	assertTrue (doc.getContentType ().startsWith ("text/html"));
	doc = fsh.handleRequest (null, "mi%6ck/i%73", req);
	assertTrue (doc instanceof StaticDocument);
	assertTrue (doc.getContentType ().startsWith ("text/html"));

	// try getting the existing files
	doc = fsh.handleRequest (null, "milk/is/yummy.txt", req);
	assertTrue (doc instanceof FileDocument);
	assertTrue (doc.getContentType ().startsWith ("text/plain"));
	assertEquals (6, doc.getContentLength ());
	doc = fsh.handleRequest (null, "leche.html", req);
	assertTrue (doc instanceof FileDocument);
	assertTrue (doc.getContentType ().startsWith ("text/html"));
	assertEquals (13, doc.getContentLength ());
	doc = fsh.handleRequest (null, "milche", req);
	assertTrue (doc instanceof FileDocument);
	assertEquals (19, doc.getContentLength ());
	
	// make sure getting a couple non-existent files returns null
	doc = fsh.handleRequest (null, "crazy-talk.txt", req);
	assertNull (doc);
	doc = fsh.handleRequest (null, "non/existent", req);
	assertNull (doc);
	doc = fsh.handleRequest (null, "milk/rocks", req);
	assertNull (doc);

	// test that a query makes it through to the FileDocument
	doc = fsh.handleRequest (null, "milk/is/yummy.txt?offset=3", req);
	assertTrue (doc instanceof FileDocument);
	assertTrue (doc.getContentType ().startsWith ("text/plain"));
	assertEquals (3, doc.getContentLength ());

	f6.delete ();
	f5.delete ();
	f4.delete ();
	f3.delete ();
	f2.delete ();
	f1.delete ();
    }



    // ------------------------------------------------------------------------
    // static private methods

    /**
     * Write the given string to the given file.
     *
     * @param file non-null; the file to write to
     * @param str non-null; the string to write
     */
    static private void writeFile (File file, String str)
	throws IOException
    {
	FileWriter fw = new FileWriter (file);
	fw.write (str);
	fw.close ();
    }
}
