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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class encapsulates all the data associated with a single HTTP
 * request. Of particular note is that the document path, when gotten from
 * an instance of this class, will always be in canonical form, in that it
 * will always start with a slash and there will be no double slashes or
 * path components with the values <code>"."</code> or <code>".."</code>.
 * However, the expansion of <code>%</code> forms is <i>not</i> done; that
 * must be done on a component-by-component basis, since otherwise there
 * could be confusion about www-url-form-encoded values.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class HttpRequest
{
    /** constant representing an errorful request */
    static public final int REQUEST_ERROR = 0;

    /** constant representing a <code>GET</code> request */
    static public final int REQUEST_GET = 1;

    /** constant representing a <code>HEAD</code> request */
    static public final int REQUEST_HEAD = 2;

    /** constant representing a <code>POST</code> request */
    static public final int REQUEST_POST = 3;

    /** non-null; the HTTP version, as an interned string */
    private String myHttpVersion;

    /** the request method; one of the public <code>REQUEST_</code> constants
     * defined in this class */
    private int myRequestMethod;

    /** non-null; the path being requested, in canonical form */
    private String myPath;

    /** non-null; array of header names */
    private String[] myHeaderNames;

    /** non-null; array of header values */
    private String[] myHeaderValues;

    /** request parse error code or <code>0</code> if the request is valid */
    private int myErrorCode;

    /** null-ok; input stream that reads the entity body or
     * <code>null</code> if there is no entity associated with this request
     */
    private InputStream myEntityInputStream;



    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. Note that the <code>path</code> parameter
     * need not be given in canonical form. The constructor will
     * canonicalize it and store it internally as such. The
     * <code>entityInputStream</code> should be the raw stream of bytes for
     * the entity body; the constructor will wrap it appropriately based on
     * the values of the headers (such as <code>"Content-Length"</code>).
     * Note that the header names and values arrays are stored directly by
     * the generated instance, and it is a Bad Idea to keep other
     * references to them (especially to tweak them). Java ought to support
     * immutable data types (particularly arrays), so that caveats like
     * this aren't necessary; c'est la guerre.
     *
     * @param httpVersion non-null; the HTTP version
     * @param requestMethod the request method; one of the public
     * <code>REQUEST_</code> constants defined in this class
     * @param path non-null; the path being requested
     * @param headerNames non-null; array of header names
     * @param headerValues non-null; array of header values, corresponding
     * to the <code>headerNames</code> array
     * @param entityInputStream null-ok; stream that can be used to read
     * the entity body or <code>null</code> if there is no entity associated
     * with this request 
     */
    public HttpRequest (String httpVersion, int requestMethod, String path,
			String[] headerNames, String[] headerValues,
			InputStream entityInputStream)
    {
	if (httpVersion == null)
	{
	    throw new NullPointerException ("httpVersion = null");
	}

	if ((requestMethod < REQUEST_GET) || (requestMethod > REQUEST_POST))
	{
	    throw new IllegalArgumentException ("requestMethod = " + 
						requestMethod);
	}

	if (path == null)
	{
	    throw new NullPointerException ("path = null");
	}

	if (headerNames == null)
	{
	    throw new NullPointerException ("headerNames = null");
	}

	if (headerValues == null)
	{
	    throw new NullPointerException ("headerValues = null");
	}

	if (headerNames.length != headerValues.length)
	{
	    throw new IllegalArgumentException (
                "headerNames.length != headerValues.length");
	}

	myHttpVersion = httpVersion.intern ();
	myRequestMethod = requestMethod;
	myPath = URLUtils.canonicalPath (path);
	myHeaderNames = headerNames;
	myHeaderValues = headerValues;
	myErrorCode = 0;
	setEntityInputStream (entityInputStream);
    }

    /**
     * Construct an instance that corresponds to an errorful request.
     *
     * @param httpVersion non-null; the HTTP version
     * @param errorCode the three-digit error code
     * @param errorMsg non-null; the error message
     */
    public HttpRequest (String httpVersion, int errorCode, String errorMsg)
    {
	initializeError (httpVersion, errorCode, errorMsg);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Return a debug string for this instance, containing pretty much
     * all the information of this instance in a reasonably-palatable
     * form.
     *
     * @param prefix non-null; a prefix to use for each line of the result
     * @return non-null; a debug string
     */
    public String toDebugString (String prefix)
    {
	StringBuffer sb = new StringBuffer (1000);

	sb.append (prefix);

	switch (myRequestMethod)
	{
	    case REQUEST_ERROR: 
	    {
		sb.append ("ERROR ");
		sb.append (myErrorCode);
		sb.append (' ');
		sb.append (myHeaderValues[0]);
		sb.append ('\n');
		return sb.toString ();
	    }
	    case REQUEST_GET:  sb.append ("GET ");  break;
	    case REQUEST_HEAD: sb.append ("HEAD "); break;
	    case REQUEST_POST: sb.append ("POST "); break;
	    default: sb.append ("UNKNOWN(" + myRequestMethod + ") "); break;
	}

	sb.append (myPath);
	sb.append (" HTTP/");
	sb.append (myHttpVersion);
	sb.append ('\n');

	for (int i = 0; i < myHeaderNames.length; i++)
	{
	    sb.append (prefix);
	    sb.append (myHeaderNames[i]);
	    sb.append (": ");
	    sb.append (myHeaderValues[i]);
	    sb.append ('\n');
	}

	return sb.toString ();
    }

    /**
     * Get the HTTP version.
     *
     * @return non-null; the HTTP version, as an interned string
     */
    public String getHttpVersion ()
    {
	return myHttpVersion;
    }

    /**
     * Get the request method.
     *
     * @return the request method; one of the public <code>REQUEST_</code>
     * constants defined in this class
     */
    public int getRequestMethod ()
    {
	return myRequestMethod;
    }

    /**
     * Get the path.
     *
     * @return non-null; the path being requested, in canonical form
     */
    public String getPath ()
    {
	return myPath;
    }

    /**
     * Get the three-digit request parse error code, or <code>0</code> if 
     * the request is valid.
     *
     * @return the error code
     */
    public int getErrorCode ()
    {
	return myErrorCode;
    }

    /**
     * Get the error message associated with this request, or <code>null</code>
     * if the request is valid.
     *
     * @return null-ok; the error message or <code>null</code> if there is
     * none
     */
    public String getErrorMsg ()
    {
	return getHeader ("ERROR-MSG");
    }

    /**
     * Get the value associated with the given header name, or
     * <code>null</code> if there is no such header. The name is compared
     * case-insensitively.
     *
     * @param name non-null; the name of the header to get
     * @return null-ok; the value of the given header, or <code>null</code>
     * if there is no such header 
     */
    public String getHeader (String name)
    {
	for (int i = 0; i < myHeaderNames.length; i++)
	{
	    if (myHeaderNames[i].equalsIgnoreCase (name))
	    {
		return myHeaderValues[i];
	    }
	}

	return null;
    }

    /**
     * Get the input stream to use for reading the entity body.
     * This returns <code>null</code> if this instance does not have
     * an associated entity.
     *
     * @return null-ok; the entity body input stream or <code>null</code>
     */
    public InputStream getEntityInputStream ()
    {
	return myEntityInputStream;
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Initialize this instance to correspond to an errorful request.
     *
     * @param httpVersion non-null; the HTTP version
     * @param errorCode the three-digit error code
     * @param errorMsg non-null; the error message
     */
    private void initializeError (String httpVersion, int errorCode, 
				  String errorMsg)
    {
	if (httpVersion == null)
	{
	    throw new NullPointerException ("httpVersion = null");
	}

	if ((errorCode < 100) || (errorCode > 599))
	{
	    throw new IllegalArgumentException ("errorCode out of range");
	}

	if (errorMsg == null)
	{
	    throw new NullPointerException ("errorMsg = null");
	}

	myHttpVersion = httpVersion.intern ();
	myRequestMethod = REQUEST_ERROR;
	myPath = "/";
	myHeaderNames = new String[] { "ERROR-MSG" };
	myHeaderValues = new String[] { errorMsg };
	myEntityInputStream = null;
	myErrorCode = errorCode;
    }

    /**
     * Wrap the given stream as an entity input stream, as appropriate
     * depending on the values of the header fields, setting the
     * instance variable {@link #myEntityInputStream}. This will set it to
     * <code>null</code> if passed <code>null</code> <i>or</i> if
     * the headers indicate that there's no entity body. It will also
     * transmogrify the whole instance into an errorful instance if
     * it can't make sense of the entity headers (or it <i>can</i>
     * make enough sense of them to know that the particular entity
     * encoding isn't supported).
     *
     * @param orig null-ok; the stream to wrap
     */
    private void setEntityInputStream (InputStream orig)
    {
	if (orig == null)
	{
	    myEntityInputStream = null;
	    return;
	}

	// see rfc2616 sec 4.4 for an explanation of the following logic

	String transferEncoding = getHeader ("transfer-encoding");
	if ((transferEncoding != null)
	    && !transferEncoding.equalsIgnoreCase ("identity"))
	{
	    initializeError (myHttpVersion, 501, 
			     "Transfer Encoding Not Implemented");
	    return;
	}

	String contentLength = getHeader ("content-length");
	if (contentLength == null)
	{
	    myEntityInputStream = null;
	    return;
	}

	long len;
	try
	{
	    len = Long.parseLong (contentLength);
	}
	catch (NumberFormatException ex)
	{
	    // caught below with the other bad cases
	    len = -1;
	}

	if (len < 0)
	{
	    initializeError (myHttpVersion, 400,
			     "Bad Value For Content-Length Header");
	    return;
	}

	myEntityInputStream = 
	    new SimpleEntityInputStream (orig, len);
    }



    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Read in a request from the given input stream.
     *
     * @param stream non-null; the stream to read from
     * @return a newly-constructed request, based on the contents of the
     * stream
     */
    public static HttpRequest read (InputStream stream)
	throws IOException
    {
	InputStreamReader r = new InputStreamReader (stream);
	String firstLine = null;
	ArrayList headers = new ArrayList (20);

	// Note: We can't use a Buffered* class here because it will end up
	// reading too much, in particular, it can end up reading past the
	// end of the headers and into the content, and possibly into the
	// subsequent request. Buffering, if at all, either needs to happen
	// above or below this layer
	char[] lineBuf = new char[200];
	int lineMax = lineBuf.length;
	int lineLen;

	for (;;)
	{
	    // read a line
	    boolean gotEOL = false;
	    lineLen = 0;
	    for (;;)
	    {
		int c = stream.read ();
		if (c == '\r')
		{
		    c = stream.read ();
		    if (c != '\n')
		    {
			// error: newline expected after cr
			return new HttpRequest ( 
                            "1.1", 
			    400,
			    "Bad headers (no newline after cr)");
		    }
		    gotEOL = true;
		    break;
		}
		else if (c == '\n')
		{
		    // lenient about the protocol; officially this isn't
		    // acceptable but many clients are bad
		    gotEOL = true;
		    break;
		}
		else if ((c != '\t') && ((c < ' ') || (c >= 0x7f)))
		{
		    String msg = 
			(c == -1) 
			? "Bad headers (early end of input)"
			: "Bad headers (bad character in input)";
		    
		    return new HttpRequest ("1.1", 400, msg);
		}

		if (lineLen == lineMax)
		{
		    // gotta grow the buffer
		    char[] newBuf = new char[lineMax * 2];
		    System.arraycopy (lineBuf, 0, newBuf, 0, lineMax);
		    lineBuf = newBuf;
		    lineMax = lineBuf.length;
		}

		lineBuf[lineLen] = (char) c;
		lineLen++;
	    }

	    if (! gotEOL)
	    {
		// end of headers and end of input
		break;
	    }

	    if (lineLen == 0)
	    {
		if (firstLine == null)
		{
		    // protocol leniency: it's an extra blank line before
		    // the request
		    continue;
		}
		else
		{
		    // it's the end of the headers
		    break;
		}
	    }

	    String oneLine = new String (lineBuf, 0, lineLen);
	    
	    if (firstLine == null)
	    {
		// it's the http command (e.g., "GET /foo HTTP/1.1")
		firstLine = oneLine;
		continue;
	    }

	    // it's a header line (or a continuation thereof)

	    char c = oneLine.charAt (0);
	    if ((c == ' ') || (c == '\t'))
	    {
		// it's a continuation from a previous line
		int lasth = headers.size ();
		if (lasth == 0)
		{
		    // error: it's a "continuation" at the start of the
		    // headers
		    return new HttpRequest (
                        "1.1", 
                        400,
			"Bad headers (continuation at start of headers)");
		}
		else
		{
		    lasth--;
		    String hval = 
			(String) headers.get (lasth) +
			' ' +
			oneLine.trim ();
		    headers.set (lasth, hval);
		}
		continue;
	    }

	    // it's a new header line; parse it into name and value

	    int colonAt = oneLine.indexOf (':');
	    if (colonAt == -1)
	    {
		return new HttpRequest (
                    "1.1", 
                    400,
		    "Bad headers (colon not found on line)");
	    }

	    String name = oneLine.substring (0, colonAt).trim ();
	    String value = oneLine.substring (colonAt + 1).trim ();
	    headers.add (name);
	    headers.add (value);
	}
	
	if (firstLine == null)
	{
	    return new HttpRequest ("1.1", 400, "Empty Request");
	}

	// at this point firstLine should look something like this:
	// "GET /foo HTTP/1.1"

	int firstSpace = firstLine.indexOf (' ');
	int lastSpace = firstLine.lastIndexOf (' ');
	if ((firstSpace == -1) || (firstSpace == lastSpace))
	{
	    return new HttpRequest ("1.1", 400, "Bad Request Line");
	}

	String httpVer;
	if (firstLine.endsWith (" HTTP/1.1"))
	{
	    httpVer = "1.1";
	}
	else if (firstLine.endsWith (" HTTP/1.0"))
	{
	    httpVer = "1.0";
	}
	else
	{
	    // see rfc2616 sec 10.5.6
	    return new HttpRequest ("1.1", 505, 
				    "Unsupported Protocol Version");
	}

	int method;
	if (firstLine.startsWith ("GET "))
	{
	    method = REQUEST_GET;
	}
	else if (firstLine.startsWith ("HEAD "))
	{
	    method = REQUEST_HEAD;
	}
	else if (firstLine.startsWith ("POST "))
	{
	    method = REQUEST_POST;
	}
	else
	{
	    // see rfc2616 sec 10.5.2
	    return new HttpRequest (httpVer, 501, 
				    "Unsupported Request Method");
	}

	String doc = firstLine.substring (firstSpace + 1, lastSpace).trim ();

	if (doc.length () == 0)
	{
	    return new HttpRequest (httpVer, 400, "No Document Specified");
	}
	else if (doc.charAt (0) == '/')
	{
	    // it's okay as an absolute path form
	}
	else if (doc.startsWith ("http://"))
	{
	    // the rfc says we have to accept this form; we just skip
	    // the hostname part
	    int afterHost = doc.indexOf ('/', 7);
	    if (afterHost == -1)
	    {
		doc = "";
	    }
	    else
	    {
		doc = doc.substring (afterHost);
	    }
	}
	else
	{
	    return new HttpRequest (httpVer, 400, "Bad Document Name");
	}

	int hcount = headers.size () / 2;
	String[] hnames = new String[hcount];
	String[] hvalues = new String[hcount];
	for (int i = 0; i < hnames.length; i++)
	{
	    hnames[i] = (String) headers.get (i * 2);
	    hvalues[i] = (String) headers.get (i * 2 + 1);
	}

	return new HttpRequest (httpVer, method, doc, hnames, hvalues, stream);
    }
}
