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
import com.milk.tinyweb.TinyWebServer;
import com.milk.util.EmbeddedException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URL;
import junit.framework.TestCase;

/**
 * This class contains tests of {@link TinyWebServer} that are all run
 * in the same VM. That is, the tests and the server share a process.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TestTinyWebServer
extends TestCase
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param name the name of the test, passed to the test framework
     */
    public TestTinyWebServer (String name)
    {
	super (name);
    }



    // ------------------------------------------------------------------------
    // test cases

    /** 
     * This test checks to see that constructing a {@link TinyWebServer}
     * with the no-arg constructor or with the argument <code>0</code>
     * makes it pick arbitrary ports, and that a specified port in fact
     * works as expected. 
     */
    public void testConstructor ()
    {
	TinyWebServer tws1 = new TinyWebServer ();
	TinyWebServer tws2 = new TinyWebServer ();
	TinyWebServer tws3 = new TinyWebServer (0);
	TinyWebServer tws4 = new TinyWebServer (0);

	int p1 = tws1.getPort ();
	int p2 = tws2.getPort ();
	int p3 = tws3.getPort ();
	int p4 = tws4.getPort ();

	assertTrue (p1 != p2);
	assertTrue (p1 != p3);
	assertTrue (p1 != p4);
	assertTrue (p2 != p3);
	assertTrue (p2 != p4);
	assertTrue (p3 != p4);

	try
	{
	    new TinyWebServer (p1);
	    fail ("somehow we constructed a new server that reuses " +
		  "an existing port");
	}
	catch (EmbeddedException ex)
	{
	    // expected; ignore it
	}

	int port = 2000;
	boolean okay = false;
	for (int i = 0; i < 100; i++)
	{
	    port++;
	    try
	    {
		new TinyWebServer (port);
		okay = true;
		break;
	    }
	    catch (EmbeddedException ex)
	    {
		// it could happen
	    }
	}

	if (! okay)
	{
	    fail ("failed to specify port all 100 times");
	}
    }

    /**
     * Test the method {@link TinyWebServer#getURL}.
     */
    public void testGetURL ()
	throws Exception
    {
	String hostName = InetAddress.getLocalHost ().getHostName ();

	for (int i = 0; i < 5; i++)
	{
	    TinyWebServer tws = new TinyWebServer ();
	    int port = tws.getPort ();
	    String expected = "http://" + hostName + ":" + port + "/";
	    assertEquals (expected, tws.getURL ());
	}

	int port = 2000;
	boolean okay = false;
	for (int i = 0; i < 100; i++)
	{
	    port++;
	    try
	    {
		TinyWebServer tws = new TinyWebServer (port);
		port = tws.getPort ();
		String expected = "http://" + hostName + ":" + port + "/";
		assertEquals (expected, tws.getURL ());
		okay = true;
		break;
	    }
	    catch (EmbeddedException ex)
	    {
		// it could happen
	    }
	}

	if (! okay)
	{
	    fail ("failed to specify port all 100 times");
	}
    }

    /**
     * Test that starting a {@link TinyWebServer} makes it
     * responsive to requests, and that the responses all fail in the right
     * way (that is, with appropriate valid HTTP responses). 
     */
    public void testStart ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();
	String urlBase = tws.getURL ();
	assertTrue (! tws.isRunning ());
	tws.start ();
	assertTrue (tws.isRunning ());

	HttpResponse resp = HttpGetter.getURL (urlBase, 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 404 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/html"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertTrue (resp.getContent ().length != 0);

	resp = HttpGetter.getURL (urlBase + "danfuzz/", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 404 "));

	resp = HttpGetter.getURL (urlBase + "milk/is/yummy.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 404 "));
    }

    /**
     * Test that a nonstarted {@link TinyWebServer} (including
     * started then stopped) doesn't respond to requests. 
     */
    public void testNotStarted ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();
	String urlBase = tws.getURL ();

	assertTrue (! tws.isRunning ());

	try
	{
	    HttpGetter.getURL (urlBase, 1000);
	    fail ("request should have timed out");
	}
	catch (RuntimeException ex)
	{
	    assertEquals ("timed out", ex.getMessage ());
	}

	tws.setAcceptTimeout ((int) 5000);
	tws.start ();
	assertTrue (tws.isRunning ());
	HttpGetter.getURL (urlBase, 1000);
	tws.stop ();

	assertTrue (! tws.isRunning ());
	
	try
	{
	    HttpGetter.getURL (urlBase, 1000);
	    fail ("request should have timed out");
	}
	catch (RuntimeException ex)
	{
	    assertEquals ("timed out", ex.getMessage ());
	}
    }

    /**
     * Test the methods {@link TinyWebServer#close} and
     * {@link TinyWebServer#isClosed}.
     */
    public void testClose ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();
	String urlBase = tws.getURL ();

	assertTrue (! tws.isClosed ());

	tws.setAcceptTimeout ((int) 5000);
	tws.start ();

	assertTrue (! tws.isClosed ());

	HttpGetter.getURL (urlBase, 1000);
	tws.close ();
	
	assertTrue (tws.isClosed ());

	try
	{
	    HttpGetter.getURL (urlBase, 1000);
	    fail ("request should have been refused");
	}
	catch (EmbeddedException ex)
	{
	    Throwable emb = ex.getEmbeddedException ();
	    assertTrue (emb instanceof ConnectException);
	}
    }

    /**
     * Test that a {@link TinyWebServer} does in fact serve the documents
     * it's told about. Also test the failure cases.
     */
    public void testPutDocument ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();

	// test failures

	try
	{
	    tws.putDocument (null, Document.makeHTML ("blort"));
	    fail ("putDocument failed to fail (1)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	try
	{
	    tws.putDocument ("", Document.makeHTML ("blort"));
	    fail ("putDocument failed to fail (2)");
	}
	catch (IllegalArgumentException ex)
	{
	    // expected
	}

	try
	{
	    tws.putDocument ("/blort", null);
	    fail ("putDocument failed to fail (3)");
	}
	catch (NullPointerException ex)
	{
	    // expected
	}

	// test the stuff that should work

	tws.putDocument ("/index.html", Document.makeHTML ("index"));
	tws.putDocument ("/foo.html", Document.makeHTML ("foo"));
	tws.putDocument ("/bar/baz.txt", Document.makeText ("baz"));
	
	String urlBase = tws.getURL ();
	tws.setAcceptTimeout ((int) 5000);
	tws.start ();

	HttpResponse resp = HttpGetter.getURL (urlBase, 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 200 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/html"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertEquals ("5", resp.getHeader ("Content-Length"));
	byte[] content = resp.getContent ();
	assertEquals (5, content.length);
	assertEquals ("index", new String (content));

	resp = HttpGetter.getURL (urlBase + "foo.html", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 200 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/html"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertEquals ("3", resp.getHeader ("Content-Length"));
	content = resp.getContent ();
	assertEquals (3, content.length);
	assertEquals ("foo", new String (content));

	resp = HttpGetter.getURL (urlBase + "bar/baz.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 200 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/plain"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertEquals ("3", resp.getHeader ("Content-Length"));
	content = resp.getContent ();
	assertEquals (3, content.length);
	assertEquals ("baz", new String (content));

	tws.stop ();
    }

    /**
     * Test the general form of GET requests, both for success and
     * failure.
     */
    public void testGET ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();

	tws.putDocument ("/foo.txt", Document.makeText ("foo"));
	
	String urlBase = tws.getURL ();
	tws.setAcceptTimeout ((int) 5000);
	tws.start ();

	HttpResponse resp = HttpGetter.getURL (urlBase + "foo.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 200 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/plain"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertEquals ("3", resp.getHeader ("Content-Length"));
	byte[] content = resp.getContent ();
	assertEquals (3, content.length);
	assertEquals ("foo", new String (content));

	resp = HttpGetter.getURL (urlBase + "zorch.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 404 "));
	assertNotNull (resp.getHeader ("Content-Type"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertNotNull (resp.getHeader ("Content-Length"));
	content = resp.getContent ();
	assertTrue (content.length != 0);

	tws.stop ();
    }
    
    /**
     * Test the general form of HEAD request, both for success and
     * failure.
     */
    public void testHEAD ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();

	tws.putDocument ("/foo.txt", Document.makeText ("foo"));
	
	String urlBase = tws.getURL ();
	tws.setAcceptTimeout ((int) 5000);
	tws.start ();

	HttpResponse resp = HttpGetter.headURL (urlBase + "foo.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 200 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/plain"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertEquals ("3", resp.getHeader ("Content-Length"));
	byte[] content = resp.getContent ();
	assertTrue (content.length == 0);

	resp = HttpGetter.headURL (urlBase + "zorch.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 404 "));
	assertNotNull (resp.getHeader ("Content-Type"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertNotNull (resp.getHeader ("Content-Length"));
	content = resp.getContent ();
	assertTrue (content.length != 0);

	tws.stop ();
    }

    /**
     * Test the general form of POST request, both for success and
     * failure, and including no content responses.
     */
    public void testPOST ()
	throws Exception
    {
	TinyWebServer tws = new TinyWebServer ();

	tws.putDocument ("/foo.txt", Document.makeText ("foo"));
	tws.putDocument ("/nulldoc", Document.makeNoContent (204, "NullDoc"));
	
	String urlBase = tws.getURL ();
	tws.setAcceptTimeout ((int) 5000);
	tws.start ();

	HttpResponse resp = HttpGetter.postURL (urlBase + "foo.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 200 "));
	assertTrue (resp.getHeader ("Content-Type").startsWith ("text/plain"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertEquals ("3", resp.getHeader ("Content-Length"));
	byte[] content = resp.getContent ();
	assertTrue (content.length == 3);
	assertEquals ("foo", new String (content));

	resp = HttpGetter.postURL (urlBase + "nulldoc", 1000);
	assertTrue (resp.getResult ().equals ("HTTP/1.1 204 NullDoc"));
	assertNull (resp.getHeader ("Content-Type"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertNull (resp.getHeader ("Content-Length"));
	content = resp.getContent ();
	assertTrue (content.length == 0);

	resp = HttpGetter.postURL (urlBase + "zorch.txt", 1000);
	assertTrue (resp.getResult ().startsWith ("HTTP/1.1 404 "));
	assertNotNull (resp.getHeader ("Content-Type"));
	assertEquals ("close", resp.getHeader ("Connection"));
	assertNotNull (resp.getHeader ("Date"));
	assertNotNull (resp.getHeader ("Server"));
	assertNotNull (resp.getHeader ("Content-Length"));
	content = resp.getContent ();
	assertTrue (content.length != 0);

	tws.stop ();
    }
}
