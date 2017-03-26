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

package com.milk.tinyweb.examples;

import com.milk.tinyweb.Document;
import com.milk.tinyweb.DocumentHandler;
import com.milk.tinyweb.HttpRequest;
import com.milk.tinyweb.FileSystemHandler;
import com.milk.tinyweb.TinyWebServer;
import com.milk.util.EmbeddedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This simple test harness will serve up files from a particular directory
 * or just dump all requests to the console. The options are as follows:
 *
 * <dl>
 * <dt><code>--base=<i>directory</i></code></dt>
 * <dd>the directory to serve files from, or <code>"dump"</code> to
 * accept all URLs and simply dump the requests to the console</dd>
 * <dt><code>--port=<i>num</i></code></dt>
 * <dd>the port number to take requests on</dd>
 * </dl>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class TinyExample
{
    // ------------------------------------------------------------------------
    // main

    /**
     * Run the sucker.
     *
     * @param args the command-line arguments
     */
    static public void main (String[] args)
    {
	String base = null;
	int port = -1;

	for (int i = 0; i < args.length; i++)
	{
	    if (args[i].startsWith ("--base="))
	    {
		base = args[i].substring (args[i].indexOf ('=') + 1);
	    }
	    else if (args[i].startsWith ("--port="))
	    {
		String portStr = args[i].substring (args[i].indexOf ('=') + 1);
		port = Integer.parseInt (portStr);
		if ((port < 0) || (port > 65535))
		{
		    System.err.println ("Bad value for --port option.");
		    System.exit (1);
		}
	    }
	    else
	    {
		System.err.println ("Unknown option: " + args[i]);
		System.exit (1);
	    }
	}

	if (base == null)
	{
	    System.err.println ("Must specify --base=<dir> option.");
	    System.exit (1);
	}

	if (port == -1)
	{
	    System.err.println ("Must specify --port=<num> option.");
	    System.exit (1);
	}

	TinyWebServer tws = new TinyWebServer (port);

	if (base.equals ("dump"))
	{
	    addDumper (tws);
	}
	else
	{
	    tws.putDocument ("/", new FileSystemHandler (new File (base)));
	}

	tws.start ();

	System.err.println ("Server started for URL " + tws.getURL ());

	// sleep forever
	for (;;)
	{
	    System.err.println ("Main thread sleeping forever...");
	    try
	    {
		Thread.sleep (60000);
	    }
	    catch (InterruptedException ex)
	    {
		// ignore it
	    }
	}
    }

    // ------------------------------------------------------------------------
    // static private methods

    /**
     * Set up a handler that accepts all requests and dumps the
     * details to the console.
     */
    private static void addDumper (TinyWebServer tws)
    {
	tws.putDocument ("", new DumpHandler ());
    }

    // ------------------------------------------------------------------------
    // static private classes

    /**
     * The handler class for doing dumps.
     */
    static private class DumpHandler
	implements DocumentHandler
    {
	public Document handleRequest (String query, String partialPath,
				       HttpRequest request)
	{
	    System.err.println (request.toDebugString (""));

	    InputStream s = request.getEntityInputStream ();
	    if (s != null)
	    {
		byte[] buf = new byte[1024];
		try
		{
		    for (;;)
		    {
			int len = s.read (buf);
			if (len == -1)
			{
			    break;
			}
			
			System.err.write (buf, 0, len);
		    }
		}
		catch (IOException ex)
		{
		    throw new EmbeddedException (ex);
		}

		System.err.println ();
	    }

	    return Document.makeNoContent (204, "No Content");
	}

	public void putDocument (String partialPath, DocumentHandler doc)
	{
	    throw new Error ();
	}
    }
}
