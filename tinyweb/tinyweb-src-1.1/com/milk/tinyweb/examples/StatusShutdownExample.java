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
import com.milk.tinyweb.HttpRequest;
import com.milk.tinyweb.MethodHandler;
import com.milk.tinyweb.TinyWebServer;
import java.util.Date;

/**
 * This simple test has the form of a status server with a "shutdown"
 * request. There are weird timing issues with getting the sequence of
 * events right during shutdown, and this code is here to help figure out
 * the best way for this sort of thing to work. It takes the following
 * options:
 *
 * <dl>
 * <dt><code>--port=<i>num</i></code></dt>
 * <dd>the port number to take requests on</dd>
 * </dl>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class StatusShutdownExample
{
    /** web server to control */
    private TinyWebServer myTws;

    /** flag indicating whether the worker should be running */
    private boolean myIsRunning;

    /** counter for the worker thread to manipulate */
    private int myCounter;



    // ------------------------------------------------------------------------
    // main

    /**
     * Run the sucker.
     *
     * @param args the command-line arguments
     */
    static public void main (String[] args)
    {
	int port = -1;

	for (int i = 0; i < args.length; i++)
	{
	    if (args[i].startsWith ("--port="))
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

	if (port == -1)
	{
	    System.err.println ("Must specify --port=<num> option.");
	    System.exit (1);
	}

	TinyWebServer tws = new TinyWebServer (port);
	tws.setAcceptTimeout (5000);

	StatusShutdownExample tss = new StatusShutdownExample (tws);

	tws.putDocument ("/index.html", 
			 new MethodHandler (tss, "doIndex"));
	tws.putDocument ("/shutdown.html", 
			 new MethodHandler (tss, "doShutdown"));

	tws.start ();

	System.err.println ("Server started for URL " + tws.getURL ());
	System.err.println ("Main thread now exiting.");
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance
     *
     * @param tws non-null; the web server to control
     */
    private StatusShutdownExample (TinyWebServer tws)
    {
	myTws = tws;
	myCounter = 0;
	myIsRunning = true;
	new WorkerThread ().start ();
    }



    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Generate the index page.
     */
    public Document doIndex (String query, String partialPath,
			     HttpRequest request)
    {
	return 
	    Document.makeHTML ("<html><body><h1>At " +
			       new Date () + ", count is " + myCounter + 
			       ".</h1>" +
			       "<a href=\"/shutdown.html\">Shutdown</a>" +
			       "</body></html>");
    }

    /**
     * Generate the shutdown page.
     */
    public Document doShutdown (String query, String partialPath,
				HttpRequest request)
    {
	myTws.close ();
	shutdownStuff ();

	return 
	    Document.makeHTML ("<html><body><h1>Shutting down now.</h1>" +
			       "</body></html>");
    }

    

    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Perform shutdown-related activities. In a real server, this would
     * very likely be much more involved.
     */
    private void shutdownStuff ()
    {
	myIsRunning = false;
    }



    // ------------------------------------------------------------------------
    // private instance classes

    /**
     * This thread pretends to do useful work, and can be told to shut down.
     */
    private class WorkerThread
	extends Thread
    {
	public void run ()
	{
	    System.out.println ("Worker thread has started.");

	    while (myIsRunning)
	    {
		myCounter++;
		System.out.println ("Worker thread is working: " + myCounter);
		try
		{
		    Thread.sleep (10000);
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }

	    System.out.println ("Worker thread is exiting.");
	}
    }
}

