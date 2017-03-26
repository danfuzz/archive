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

import com.milk.util.EmbeddedException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

/**
 * This just houses static methods for getting urls.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class HttpGetter
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private HttpGetter ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Request the given URL.
     * 
     * @param meth non-null; the request method
     * @param url non-null; the URL to request
     * @param timeOut the amount of time to wait for the response before
     * giving up, or <code>0</code> for an infinite timeout
     * @return non-null; an {@link HttpResponse} with the results
     * @wxception RuntimeException thrown if there's any trouble (including
     * timeout)
     */
    public static HttpResponse requestURL (String meth, String url, 
					   long timeOut)
    {
	URL u;

	try
	{
	    u = new URL (url);
	}
	catch (MalformedURLException ex)
	{
	    throw new EmbeddedException (ex);
	}

	if (! u.getProtocol ().equals ("http"))
	{
	    throw new RuntimeException ("Bad URL protocol for " + url);
	}

	String host = u.getHost ();
	int port = u.getPort ();
	String path = u.getFile ();
	if (port == -1)
	{
	    port = 80;
	}

	Connector c = new Connector (host, port, path, meth);
	return c.waitFor (timeOut);
    }

    /**
     * Get the given URL. That is, perform a <code>GET</code> request.
     * 
     * @param url non-null; the URL to get
     * @param timeOut the amount of time to wait for the response before
     * giving up, or <code>0</code> for an infinite timeout
     * @return non-null; an {@link HttpResponse} with the results
     * @wxception RuntimeException thrown if there's any trouble (including
     * timeout)
     */
    public static HttpResponse getURL (String url, long timeOut)
    {
	return requestURL ("GET", url, timeOut);
    }

    /**
     * Get the header for the given URL. That is, perform a
     * <code>HEAD</code> request.
     * 
     * @param url non-null; the URL to query
     * @param timeOut the amount of time to wait for the response before
     * giving up, or <code>0</code> for an infinite timeout
     * @return non-null; an {@link HttpResponse} with the results
     * @wxception RuntimeException thrown if there's any trouble (including
     * timeout) 
     */
    public static HttpResponse headURL (String url, long timeOut)
    {
	return requestURL ("HEAD", url, timeOut);
    }

    /**
     * Post to the the given URL (but with no content). That is, perform a
     * <code>POST</code> request.
     * 
     * @param url non-null; the URL to post
     * @param timeOut the amount of time to wait for the response before
     * giving up, or <code>0</code> for an infinite timeout
     * @return non-null; an {@link HttpResponse} with the results
     * @wxception RuntimeException thrown if there's any trouble (including
     * timeout) 
     */
    public static HttpResponse postURL (String url, long timeOut)
    {
	return requestURL ("POST", url, timeOut);
    }



    // ------------------------------------------------------------------------
    // private helper classes

    /**
     * This is the thread class that actually does the whole connection
     * thing.
     */
    private static class Connector
	extends Thread
    {
	/** the host */
	private String myHost;

	/** the port */
	private int myPort;

	/** the path */
	private String myPath;

	/** the request method */
	private String myMeth;

	/** the response or <code>null</code> */
	private HttpResponse myResponse;

	/** the exception or <code>null</code> */
	private RuntimeException myException;

	/** true if the request is done */
	private boolean myDone;

	/**
	 * Construct an instance and start it going.
	 *
	 * @param host the host
	 * @param port the port
	 * @param path the path
	 * @param meth the request method
	 */
	public Connector (String host, int port, String path, String meth)
	{
	    myHost = host;
	    myPort = port;
	    myPath = path;
	    myMeth = meth;
	    myResponse = null;
	    myException = null;
	    myDone = false;
	    setDaemon (true);
	    start ();
	}

	/**
	 * Wait up to the given time (in msec) for the request to 
	 * complete, and either return the response or throw the
	 * problem.
	 *
	 * @param timeOut the amount of time to allow for the request
	 * to complete
	 * @return the response
	 */
	public HttpResponse waitFor (long timeOut)
	{
	    synchronized (this)
	    {
		if (! myDone)
		{
		    try
		    {
			wait (timeOut);
		    }
		    catch (InterruptedException ex)
		    {
			// ignore it
		    }
		}

		if (! myDone)
		{
		    throw new RuntimeException ("timed out");
		}
		else if (myException != null)
		{
		    throw myException;
		}
		else
		{
		    return myResponse;
		}
	    }
	}	    
	
	/**
	 * Run the thread and capture errors, if any.
	 */
	public void run ()
	{
	    try
	    {
		doit ();
	    }
	    catch (RuntimeException ex)
	    {
		myException = ex;
	    }
	    catch (Exception ex)
	    {
		myException = new EmbeddedException (ex);
	    }
	    finally
	    {
		synchronized (this)
		{
		    myDone = true;
		    notifyAll ();
		}
	    }
	}

	/**
	 * Do the connection et al.
	 */
	public void doit ()
	    throws Exception
	{
	    Socket sock = new Socket (myHost, myPort);
	    OutputStreamWriter osw = 
		new OutputStreamWriter (sock.getOutputStream ());
	    osw.write (myMeth);
	    osw.write (' ');
	    osw.write (myPath);
	    osw.write (" HTTP/1.1\n\n");
	    osw.flush ();

	    InputStream is = sock.getInputStream ();
	    String resultLine = null;
	    HashMap headers = new HashMap ();
	    byte[] buf = new byte[10000];
	    int inBuf = 0;
	    boolean gotCr = false;
	    for (;;)
	    {
		int b = is.read ();
		if (gotCr && (b == '\n'))
		{
		    // ignore nl after cr
		}
		else if ((b == '\n') || (b == '\r') || (b == -1))
		{
		    if (inBuf == 0)
		    {
			// blank line means beginning of content
			gotCr = (b == '\r');
			break;
		    }
		    String line = new String (buf, 0, inBuf);
		    if (line.startsWith ("HTTP/"))
		    {
			resultLine = line;
		    }
		    else
		    {
			int colonAt = line.indexOf (':');
			if (colonAt == -1)
			{
			    throw new RuntimeException ("Bad header: " + line);
			}
			String key = line.substring (0, colonAt).trim ();
			String value = line.substring (colonAt + 1).trim ();
			headers.put (key, value);
		    }
		    inBuf = 0;
		}
		else
		{
		    buf[inBuf] = (byte) b;
		    inBuf++;
		}

		gotCr = (b == '\r');

		if (b == -1)
		{
		    break;
		}
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream (10000);
	    if (gotCr)
	    {
		// deal with possible pre-content newline
		int b = is.read ();
		if ((b != -1) && (b != '\n'))
		{
		    baos.write ((byte) b);
		}
	    }

	    for (;;)
	    {
		int amt = is.read (buf);
		if (amt == -1)
		{
		    break;
		}
		if (amt > 0)
		{
		    baos.write (buf, 0, amt);
		}
	    }

	    myResponse = 
		new HttpResponse (resultLine, headers, baos.toByteArray ());
	}
    }
}
