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

package com.milk.tinyweb;

import com.milk.util.EmbeddedException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * This is a very simple HTTP server. One registers objects at
 * various URLs, and those will be used in order to answer requests.
 * The two things one may register are {@link Document} objects, which
 * will be returned directly for an exactly matched URL, and {@link
 * DocumentHandler} objects, which are queried for documents matching
 * a particular URL prefix. If there is no matching registered object,
 * or if a {@link DocumentHandler} returns <code>null</code>, then the
 * server will report a no-such-document error.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class TinyWebServer
{
    /** the maximum number of simultaneous connections that may be
     * actively handled */
    static private final int MAX_CONNECTIONS = 20;

    /** true if this code should spit out debug messages, cause this to be
     * true by defining the property
     * <code>com.milk.tinyweb.TinyWebServer.debug</code> to be
     * <code>"true"</code>. */
    static private final boolean DEBUG =
	Boolean.getBoolean ("com.milk.tinyweb.TinyWebServer.debug");

    /** date object to use for formatting dates in HTTP headers */
    static private final Date TheDateToFormat = new Date ();

    /** date formatter to use for formatting dates in HTTP headers;
     * see rfc2616 section 3.3.1 */
    static private final SimpleDateFormat TheDateFormatter =
	new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss zzz");

    static
    {
	TheDateFormatter.setTimeZone (TimeZone.getTimeZone ("GMT"));
    }

    /** the port to listen on, or <code>0</code> during construction
     * if an arbitrary port is to be chosen */
    private int myPort;

    /** non-null; the logger to use */
    private TinyWebLogger myLogger;

    /** null-ok; the top-level document handler */
    private DocumentHandler myTopLevelHandler;

    /** the server socket */
    private ServerSocket myServerSocket;

    /** true if this instance is currently running */
    private boolean myIsRunning;

    /** true if this instance should stop running */
    private boolean myShouldStop;

    /** the thread running this instance, if any */
    private Thread myThread;

    /** object to synchronize on (makes inner class stuff more obvious and
     * prevents inadvertant lockup caused by external entities synchronizing
     * on this object directly) */
    private Object mySynch;



    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance that uses the default logger and picks an
     * arbitrary port. After the constructor returns, there will be a
     * socket listening for requests, but those requests won't be serviced
     * until the method {@link #start} is called on the returned instance.
     * The socket will be for an arbitrarily-chosen port, the number of
     * which may be determined by calling {@link #getPort}. 
     */
    public TinyWebServer ()
    {
	this (0, null);
    }

    /**
     * Construct an instance that uses the given logger and picks an
     * arbitrary port. After the constructor returns, there will be a
     * socket listening for requests, but those requests won't be serviced
     * until the method {@link #start} is called on the returned instance.
     * The socket will be for an arbitrarily-chosen port, the number of
     * which may be determined by calling {@link #getPort}. If the
     * logger is specified as <code>null</code>, then a default logger
     * is used.
     *
     * @param logger null-ok; the logger to use
     */
    public TinyWebServer (TinyWebLogger logger)
    {
	this (0, logger);
    }

    /**
     * Construct an instance that uses the default logger and the given
     * port. After the constructor returns, there will be a socket
     * listening for requests, but those requests won't be serviced until
     * the method {@link #start} is called on the returned instance. If the
     * port is specified as <code>0</code>, then an arbitrary port is
     * chosen, the number of which may be determined by calling {@link
     * #getPort}.
     *
     * @param port the port to listen on 
     */
    public TinyWebServer (int port)
    {
	this (port, null);
    }

    /**
     * Construct an instance that uses the given logger and given port.
     * After the constructor returns, there will be a socket listening for
     * requests, but those requests won't be serviced until the method
     * {@link #start} is called on the returned instance. If the port is
     * specified as <code>0</code>, then an arbitrary port is chosen, the
     * number of which may be determined by calling {@link #getPort}. If
     * the logger is specified as <code>null</code>, then a default logger
     * is used.
     *
     * @param port the port to listen on 
     * @param logger null-ok; the logger to use 
     */
    public TinyWebServer (int port, TinyWebLogger logger)
    {
	myPort = port;
	
	if (logger == null)
	{
	    myLogger = new StandardLogger ();
	}
	else
	{
	    myLogger = logger;
	}

	myTopLevelHandler = null;
	myIsRunning = false;
	myShouldStop = false;
	myThread = null;

	try
	{
	    myServerSocket = new ServerSocket (myPort);
	    if (myPort == 0)
	    {
		myPort = myServerSocket.getLocalPort ();
	    }

	    // force accept() to time out at least once a minute, because
	    // Thread.interrupt() won't reliably interrupt a call to
	    // accept(), and so, without this, a call to stop() might hang
	    // indefinitely; Java's standard libraries are rock solid, I
	    // tell ya.
	    myServerSocket.setSoTimeout ((int) 60000);
	}
	catch (IOException ex)
	{
	    throw new EmbeddedException ("Couldn't make server socket",
					 ex);
	}

	mySynch = "Synch for " + this;

	if (DEBUG)
	{
	    StringBuffer sb = new StringBuffer ();
	    sb.append (this + " constructed for URL: ");
	    sb.append (getURL ());
	    myLogger.debug (sb.toString ());
	}
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Return a string form for this instance.
     *
     * @return a string form
     */
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();
	sb.append (getClass ().getName ());
	sb.append ("[socket=");
	sb.append (myServerSocket);
	sb.append (", port=");
	sb.append (myPort);
	sb.append (", isRunning=");
	sb.append (myIsRunning);
	sb.append ("]");
	return sb.toString ();
    }

    /**
     * Set the accept timeout to the given number of milliseconds.
     * It's one minute by default. Setting this is only really useful
     * for making tests run a bit quicker.
     *
     * @param msec the new timeout
     */
    public void setAcceptTimeout (int msec)
    {
	if (myServerSocket == null)
	{
	    throw new RuntimeException (this + " is closed");
	}

	try
	{
	    myServerSocket.setSoTimeout (msec);
	}
	catch (IOException ex)
	{
	    throw new EmbeddedException (ex);
	}
    }

    /**
     * Close this instance. This means that the server no longer listens
     * for requests on the port it used, and the port becomes available for
     * some other use. If there are connections already in progress, then
     * they will finish their current request and then terminate. Once a
     * server is closed, it cannot be restarted, and the methods {@link
     * #getPort}, {@link #getURL}, and {@link #start} throw exceptions to
     * that effect. If this instance is already closed, then this does
     * nothing. 
     */
    public void close ()
    {
	stop ();

	synchronized (mySynch)
	{
	    if (myServerSocket == null)
	    {
		// already closed
		return;
	    }

	    try
	    {
		myServerSocket.close ();
	    }
	    catch (IOException ex)
	    {
		// ignore it
		if (DEBUG)
		{
		    myLogger.error (this + " had trouble closing ServerSocket",
				    ex);
		}
	    }

	    myServerSocket = null;
	}
    }

    /**
     * Return whether or not this instance is closed.
     *
     * @see #close
     *
     * @return <code>true</code> if this instance is closed, or
     * <code>false</code> if not 
     */
    public boolean isClosed ()
    {
	synchronized (mySynch)
	{
	    return (myServerSocket == null);
	}
    }

    /**
     * Get the port that this instance is listening on.
     *
     * @return the port number
     */
    public int getPort ()
    {
	if (myServerSocket == null)
	{
	    throw new RuntimeException (this + " is closed");
	}

	return myPort;
    }

    /**
     * Get the URL for this server. The value is of the form
     * <code>http://<i>hostname</i>:<i>port</i>/</code>, where
     * <code><i>hostname</i></code> is the name of the machine this is
     * running on and <code><i>port</i></code> is the return value from
     * {@link #getPort} in string form.
     *
     * @return the URL for this server 
     */
    public String getURL ()
    {
	if (myServerSocket == null)
	{
	    throw new RuntimeException (this + " is closed");
	}

	String hostname;
	try 
	{
	    hostname = InetAddress.getLocalHost().getHostName();
	} 
	catch (Exception ex) 
	{
	    throw new EmbeddedException (ex);
	}

	return "http://" + hostname + ":" + getPort () + "/";
    }

    /**
     * Run the server in a new thread, if it's not already running.
     */
    public void start ()
    {
	synchronized (mySynch)
	{
	    if (myServerSocket == null)
	    {
		throw new RuntimeException (this + " is closed");
	    }

	    if (myIsRunning)
	    {
		return;
	    }

	    myThread = new ServerThread ();
	    myIsRunning = true;
	}
    }

    /**
     * Stop the server if it was running. Do nothing if it wasn't.
     * By the time this method returns, there will be no thread trying to
     * accept new connections. However, already-established connections
     * will continue processing until the end of their current requests.
     */
    public void stop ()
    {
	synchronized (mySynch)
	{
	    while (myIsRunning)
	    {
		myShouldStop = true;
		myThread.interrupt ();

		try
		{
		    mySynch.wait ();
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }
	}

	myShouldStop = false;
	myThread = null;
    }

    /**
     * Return <code>true</code> if this server is currently running.
     *
     * @return <code>true</code> if this server is currently running
     */
    public boolean isRunning ()
    {
	return myIsRunning;
    }

    /**
     * Put a document or handler at the given absolute path, replacing a
     * pre-existing entity if there is one already at the given path. If
     * need be, virtual directories will be created such that the path will
     * exist. If there are pre-existing handlers along the path, some may
     * not be amenable to having new things placed inside them, which will
     * result in an exception being thrown. The given path should begin
     * with a slash. Passing in just <code>"/"</code> for the path
     * indicates that the given document or handler is to be used for the
     * root of the server.
     *
     * @param path non-null; the absolute path to the document or handler
     * @param doc non-null; the document or handler to store 
     */
    public void putDocument (String path, DocumentHandler doc)
    {
	if (path == null)
	{
	    throw new NullPointerException ("path == null");
	}

	if (doc == null)
	{
	    throw new NullPointerException ("doc == null");
	}

	if (! path.startsWith ("/"))
	{
	    throw new IllegalArgumentException ("path doesn't start with " +
						"\"/\"");
	}

	if (path.length () == 1)
	{
	    myTopLevelHandler = doc;
	}
	else
	{
	    if (myTopLevelHandler == null)
	    {
		myTopLevelHandler = new VirtualDirectory ();
	    }

	    myTopLevelHandler.putDocument (path.substring (1), doc);
	}
    }
    


    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Get the document for the given request and return it, or return
     * <code>null</code> if there is no such document.
     *
     * @param req non-null; the request to serve
     * @return null-ok; the document or <code>null</code> if there is no
     * document for the given path 
     */
    private Document getDocument (HttpRequest req)
    {
	// ask the top-level handler, or return null if there is none

	if (myTopLevelHandler == null)
	{
	    return null;
	}

	String path = req.getPath ();

	if (path.length () == 1)
	{
	    // this is what the DocumentHandler protocol expects
	    path = null;
	}
	else
	{
	    path = path.substring (1);
	}

	return myTopLevelHandler.handleRequest (null, path, req);
    }

    /**
     * Do everything needed for a single connection.
     */
    private void handleNewConnection ()
    {
	Socket s = null;

	try
	{
	    synchronized (mySynch)
	    {
		if (myShouldStop)
		{
		    return;
		}
	    }
	    s = myServerSocket.accept ();
	}
	catch (InterruptedIOException ex)
	{
	    // probably because we were requested to shutdown, or because
	    // of the one-minute timeout to workaround the fact that
	    // Thread.interrupt() won't reliably interrupt a call to
	    // accept()
	    return;
	}
	catch (IOException ex)
	{
	    myLogger.error (this + " got exception accepting connection",
			    ex);
	    return;
	}

	if (DEBUG)
	{
	    myLogger.debug (this + " accepted connection " + s);
	}

	// do the rest in a thread
	new ConnectionThread (s);
    }

    /**
     * Read a HTTP request from the given socket and return a
     * corresponding object, or return <code>null</code> if there was
     * no valid request to be found.
     *
     * @param sock non-null; the socket to deal with
     * @return null-ok; an array consisting of the method, the document
     * being requested, and the HTTP version; or <code>null</code> if the
     * request is invalid 
     */
    private HttpRequest httpReadRequest (Socket sock)
    {
	HttpRequest request;

	try
	{
	    sock.setSoTimeout (10000);
	    request = HttpRequest.read (sock.getInputStream ());
	}
	catch (InterruptedIOException ex)
	{
	    // see rfc2616 sec 10.4.9
	    httpErrorResponse ("1.1", sock, 408, "Request Timed Out");
	    return null;
	}
	catch (IOException ex)
	{
	    httpErrorResponse ("1.1", sock, 400, "Error Reading Request");
	    return null;
	}

	if (DEBUG)
	{
	    myLogger.debug (this.toString () + " got request:");
	    myLogger.debug (request.toDebugString ("  "));
	}

	String httpVer = request.getHttpVersion ();
	int errorCode = request.getErrorCode ();

	if (errorCode != 0)
	{
	    // the request was errorful; abort! abort!
	    httpErrorResponse (httpVer,
			       sock, 
			       errorCode,
			       request.getErrorMsg ());
	    return null;
	}

	// the only thing in the headers we care about, per-rfc (rfc2616
	// sec 14.20), is that we need to reject a request if it contains
	// an "Expect" header (since we don't *really* want to deal with
	// that header, and this is what we can minimally get away with)
	if (request.getHeader ("expect") != null)
	{
	    httpErrorResponse (httpVer, sock, 417, 
			       "Server cannot honor Expect header");
	    return null;
	}

	return request;
    }

    /**
     * Respond with the given document, as found by calling {@link
     * #getDocument} or respond with an error if the named document doesn't
     * exist. The <code>request</code> parameter contains all the
     * information from the original request. This method closes the socket
     * when the response is complete.
     *
     * @param sock non-null; the socket to respond on
     * @param request non-null; the original request
     */
    private void httpDocResponse (Socket sock, HttpRequest request)
    {
	String httpVer = request.getHttpVersion ();

	try
	{
	    Document doc = getDocument (request);

	    if (doc == null)
	    {
		httpErrorResponse (httpVer, sock, 404, "Document Not Found");
		return;
	    }

	    httpResponse (request.getHttpVersion (), 
			  sock, 
			  request.getRequestMethod (), 
			  doc);
	}
	catch (RuntimeException ex)
	{
	    myLogger.error ("Error while processing request", ex);
	    httpErrorResponse (httpVer, sock, 500, 
			       "Error while processing request");
	}
    }

    /**
     * Generate an HTTP error response on the given socket and
     * close the connection.
     *
     * @param httpVer non-null; the HTTP protocol verion
     * @param sock the socket to use
     * @param code the error code
     * @param msg the message string to use
     */
    private void httpErrorResponse (String httpVer, Socket sock, int code, 
				    String msg)
    {
	StringBuffer sb = new StringBuffer (1000);

	sb.append ("<html>\n" +
		   "<head><title>");
	sb.append (code);
	sb.append (' ');
	sb.append (msg);
	sb.append ("</title></head>\n" +
		   "<body><h1>");
	sb.append (code);
	sb.append (' ');
	sb.append (msg);
	sb.append ("</h1>\n" + 
		   "<p>Deal with it, okay?</p></body>\n" +
		   "</html>\n");
	Document doc = Document.makeHTML (sb.toString (), code, msg);

	httpResponse (httpVer, sock, HttpRequest.REQUEST_GET, doc);
    }

    /**
     * Generate an HTTP response. This closes the socket when the
     * response is complete.
     *
     * @param httpVer non-null; the HTTP protocol verion
     * @param sock non-null; the socket to use
     * @param method the request method
     * @param doc non-null; the document to output 
     * message
     */
    private void httpResponse (String httpVer, Socket sock, 
			       int method, Document doc)
    {
	String lastModString = httpDateString (doc.getLastModified ());
	String nowString = httpDateString (System.currentTimeMillis ());
	long contentLength = doc.getContentLength ();
	int resCode = doc.getResultCode ();
	String resMsg = doc.getResultMsg ();

	if (DEBUG)
	{
	    myLogger.debug (this + " response: " + resCode + ' ' + resMsg);
	    myLogger.debug ("  Date: " + nowString);
	    myLogger.debug ("  Last-Modified: " + lastModString);

	    if (contentLength != Document.CONTENT_LENGTH_NONE)
	    {
		myLogger.debug ("  Content-Type: " + doc.getContentType ());
		if (contentLength >= 0)
		{
		    myLogger.debug ("  Content-Length: " + contentLength);
		}
	    }

	    Map eh = doc.getExtraHeaders ();
	    if (eh != null)
	    {
		Iterator i = eh.keySet ().iterator ();
		while (i.hasNext ())
		{
		    String one = (String) i.next ();
		    myLogger.debug ("  " + one + ": " + eh.get (one));
		}
	    }
	}

	try
	{
	    OutputStream os = sock.getOutputStream ();
	    PrintWriter p = new PrintWriter (os);
	    p.print ("HTTP/");
	    p.print (httpVer);
	    p.print (' ');
	    p.print (resCode);
	    p.print (' ');
	    p.print (resMsg);
	    p.println ('\r');

	    p.print ("Date: ");
	    p.print (nowString);
	    p.println ('\r');

	    p.println ("Server: milk.com TinyWebServer\r");
	    p.println ("Expires: 0\r");
 
	    if (httpVer != "1.0")
	    {
		p.println ("Connection: close\r");
		p.println ("Cache-control: no-cache\r");
	    }
	    else
	    {
		p.println ("Pragma: no-cache\r");
	    }

	    Map eh = doc.getExtraHeaders ();
	    if (eh != null)
	    {
		Iterator i = eh.keySet ().iterator ();
		while (i.hasNext ())
		{
		    String one = (String) i.next ();
		    p.print (one);
		    p.print (": ");
		    p.print (eh.get (one));
		    p.println ('\r');
		}
	    }

	    p.print ("Last-Modified: ");
	    p.print (lastModString);
	    p.println ('\r');

	    if (contentLength != Document.CONTENT_LENGTH_NONE)
	    {
		p.print ("Content-Type: ");
		p.print (doc.getContentType ());
		p.println ('\r');

		if (contentLength >= 0)
		{
		    p.print ("Content-Length: ");
		    p.print (contentLength);
		    p.println ('\r');
		}
	    }

	    p.println ('\r');
	    p.flush ();

	    if ((method != HttpRequest.REQUEST_HEAD)
		&& (contentLength != Document.CONTENT_LENGTH_NONE))
	    {
		doc.writeBytes (os, contentLength);
		os.flush ();
	    }
	}
	catch (IOException ex)
	{
	    String msg = ex.getMessage ();
	    Class cls = ex.getClass ();
	    String extra = 
		(cls == IOException.class) ? 
		"" : 
		(" (" + cls.getName () + ")");

	    myLogger.error ("ERROR: " + this + " got IOException" + extra + 
			    " during response to " + sock + ": " + msg);
        }
	catch (Exception ex)
	{
	    myLogger.error (this + " got exception during response to " + sock,
			    ex);
	}

	try
	{
	    sock.shutdownOutput ();
	    sock.shutdownInput ();
	}
	catch (IOException ex)
	{
	    myLogger.error(this + " got exception during response to " + sock,
			   ex);
	}
    }



    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Format the given time as an HTTP-compliant date.
     *
     * @param time the time
     * @return the formatted version
     */
    static private String httpDateString (long time)
    {
	synchronized (TheDateFormatter)
	{
	    TheDateToFormat.setTime (time);
	    return TheDateFormatter.format (TheDateToFormat);
	}
    }    



    // ------------------------------------------------------------------------
    // private instance classes

    /**
     * This is the thread class that runs the server.
     */
    private class ServerThread
	extends Thread
    {
	/**
	 * Construct an instance. It is a daemon thread and immediately
	 * starts running.
	 */
	public ServerThread ()
	{
	    super ("TinyWebServer[" + getURL () + "]");
	    setDaemon (true);
	    this.start ();
	}
	
	/**
	 * Run the server. This never returns, under normal circumstances. 
	 */
	public void run ()
	{
	    if (DEBUG)
	    {
		myLogger.debug (this + " now running");
	    }

	    try
	    {
		for (;;)
		{
		    if (myShouldStop)
		    {
			break;
		    }

		    try
		    {
			handleNewConnection ();
		    }
		    catch (Exception ex)
		    {
			myLogger.error (this + 
					": Exception made it to top-level",
					ex);
		    }
		}
	    }
	    finally
	    {
		synchronized (mySynch)
		{
		    myIsRunning = false;
		    mySynch.notifyAll ();
		}
		
		if (DEBUG)
		{
		    myLogger.debug (this + " now terminated");
		}
	    }
	}
    }

    /**
     * This is the thread class for handling established connections.
     */
    private class ConnectionThread
	extends Thread
    {
	/** non-null; the socket to use */
	private Socket mySocket;

	public ConnectionThread (Socket sock)
	{
	    super (TinyWebServer.this + ": " + sock);
	    mySocket = sock;
	    ConnectionThread.this.start ();
	}

	public void run ()
	{
	    try
	    {
		HttpRequest request = httpReadRequest (mySocket);
		if (request != null)
		{
		    httpDocResponse (mySocket, request);
		}
	    }
	    catch (Exception ex)
	    {
		myLogger.error (this + ": Exception made it to top-level", ex);
	    }
	}
    }
}
