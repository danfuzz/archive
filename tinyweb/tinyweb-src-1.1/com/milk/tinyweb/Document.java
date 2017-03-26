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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * This class is a representation of a document that can be returned over
 * an HTTP connection. This abstract base class knows about content types
 * and modification dates, but leaves open the way that the actual content
 * is delivered. As an implementor of the {@link DocumentHandler}
 * interface, it will ignore query components and will return itself when
 * asked, but will error (return <code>null</code>) when the requested path
 * is non-<code>null</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public abstract class Document
implements DocumentHandler
{
    /** constant for the return value from {@link #getContentLength}
     * representing existing content of unknown length */
    static public final long CONTENT_LENGTH_UNKNOWN = -1;

    /** constant for the return value from {@link #getContentLength}
     * representing the fact that there is no content */
    static public final long CONTENT_LENGTH_NONE = -2;

    /** 0-length byte array, used for making no-content documents */
    static private final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /** the MIME content type of the document */
    private final String myContentType;

    /** the last modified date of the document */
    private final long myLastModified;

    /** the three-digit result code */
    private final int myResultCode;

    /** non-null; the result message */
    private final String myResultMsg;

    /** null-ok; map of additional response headers, if any */
    private final Map myExtraHeaders;



    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param contentType non-null; the MIME content type of the document
     * @param lastModified the last modified date of the document
     * @param resultCode the three-digit result code
     * @param resumtMsg non-null; the result message
     * @param extraHeaders null-ok; map of additional response headers, if
     * any; if non-<code>null</code>, must be a map of <code>String</code>s
     * to <code>String</code>s; and it must not be modified once it is
     * handed to this constructor
     */
    public Document (String contentType, long lastModified,
		     int resultCode, String resultMsg, Map extraHeaders)
    {
	if (contentType == null)
	{
	    throw new NullPointerException ("contentType=null");
	}

	if ((resultCode < 100) || (resultCode > 599))
	{
	    throw new IllegalArgumentException ("resultCode out of range");
	}

	if (resultMsg == null)
	{
	    throw new NullPointerException ("resultMsg=null");
	}

	myContentType = contentType;
	myLastModified = lastModified;
	myResultCode = resultCode;
	myResultMsg = resultMsg;
	myExtraHeaders = extraHeaders;
    }

    /**
     * Construct an instance which represents a normal contentful
     * document. That is, the result code is <code>200</code> and
     * there are no extra headers.
     *
     * @param contentType non-null; the MIME content type of the document
     * @param lastModified the last modified date of the document
     */
    public Document (String contentType, long lastModified)
    {
	this (contentType, lastModified, 200, "OK", null);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // interface's javadoc suffices
    final public Document handleRequest (String query, String partialPath,
					 HttpRequest request)
    {
	if (partialPath != null)
	{
	    // Documents shouldn't be in the middle of a path
	    return null;
	}

	return this;
    }

    // interface's javadoc suffices
    final public void putDocument (String partialPath, DocumentHandler doc)
    {
	throw new RuntimeException ("putDocument() not supported.");
    }

    /**
     * Get the MIME content type.
     *
     * @return non-null; the MIME content type
     */
    final public String getContentType ()
    {
	return myContentType;
    }

    /**
     * Get the last modified date.
     *
     * @return the last modified date
     */
    final public long getLastModified ()
    {
	return myLastModified;
    }

    /**
     * Get the three-digit result code associated with this document.
     * This is <code>200</code> for documents representing the results
     * of successful contentful requests.
     *
     * @return the three-digit result code
     */
    final public int getResultCode ()
    {
	return myResultCode;
    }

    /**
     * Get the result message associated with this document.
     * This is <code>"OK"</code> for documents representing the results
     * of successful contentful requests.
     *
     * @return non-null; the result message
     */
    final public String getResultMsg ()
    {
	return myResultMsg;
    }

    /**
     * Get the map of extra headers, if any. If non-<code>null</code>, it
     * is a map of <code>String</code>s to <code>String</code>s. The return
     * value must not be modified.
     *
     * @return null-ok; the map of extra headers, if any
     */
    final public Map getExtraHeaders ()
    {
	return myExtraHeaders;
    }



    // ------------------------------------------------------------------------
    // abstract public instance methods

    /**
     * Get the content length of this instance. The result should be the
     * number of bytes that will be written by {@link #writeBytes}, the
     * constant <code>CONTENT_LENGTH_UNKNOWN</code> if the content exists
     * and is a stream of indeterminate length, or the constant
     * <code>CONTENT_LENGTH_NONE</code> if there is no content at all.
     * Subclasses should implement this method to return an appropriate
     * value.
     *
     * @return the content length 
     */
    abstract public long getContentLength ();

    /**
     * Write the bytes of this instance to the given stream. The given
     * content length will have been returned from a previous call to
     * {@link #getContentLength} on this instance, and is provided so that
     * an instance can deal with a possibly-variable-length external
     * resource in a consistent manner. Subclasses should implement this
     * method to do something appropriate.
     *
     * @param stream the stream to write to 
     * @param contentLength the number of bytes that should be written
     */
    abstract public void writeBytes (OutputStream stream, long contentLength)
	throws IOException;

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Construct an instance (of {@link StaticDocument} actually, but it's
     * easier to mention the name of this class) from a string of HTML,
     * and with an explicitly given result code and message. The
     * modification date is taken to be the current time.
     *
     * @param text non-null; the text of the document
     * @param resultCode the three-digit result code
     * @param resultMsg non-null; the result message
     * @return non-null; the new instance 
     */
    static public Document makeHTML (String text, int resultCode, 
				     String resultMsg)
    {
	try
	{
	    return new StaticDocument ("text/html; charset=iso-8859-1",
				       System.currentTimeMillis (),
				       text.getBytes ("ISO-8859-1"),
				       resultCode,
				       resultMsg,
				       null);
	}
	catch (UnsupportedEncodingException ex)
	{
	    // shouldn't happen; all VMs are supposed to support
	    // ISO-8859-1
	    throw new EmbeddedException ("shouldn't happen", ex);
	}
    }

    /**
     * Construct an instance (of {@link StaticDocument} actually, but it's
     * easier to mention the name of this class) from a string of HTML. The
     * modification date is taken to be the current time.
     *
     * @param text non-null; the text of the document
     * @return non-null; the new instance 
     */
    static public Document makeHTML (String text)
    {
	try
	{
	    return new StaticDocument ("text/html; charset=iso-8859-1",
				       System.currentTimeMillis (),
				       text.getBytes ("ISO-8859-1"));
	}
	catch (UnsupportedEncodingException ex)
	{
	    // shouldn't happen; all VMs are supposed to support
	    // ISO-8859-1
	    throw new EmbeddedException ("shouldn't happen", ex);
	}
    }

    /**
     * Construct an instance (of {@link StaticDocument} actually, but it's
     * easier to mention the name of this class) from a string of text. The
     * modification date is taken to be the current time.
     *
     * @param text non-null; the text of the document
     * @return non-null; the new instance 
     */
    static public Document makeText (String text)
    {
	try
	{
	    return new StaticDocument ("text/plain; charset=iso-8859-1",
				       System.currentTimeMillis (),
				       text.getBytes ("ISO-8859-1"));
	}
	catch (UnsupportedEncodingException ex)
	{
	    // shouldn't happen; all VMs are supposed to support
	    // ISO-8859-1
	    throw new EmbeddedException ("shouldn't happen", ex);
	}
    }

    /**
     * Construct an instance (of {@link StaticDocument} actually, but it's
     * easier to mention the name of this class) that contains no content.
     * The modification date is taken to be the current time.
     *
     * @param resultCode the three-digit result code
     * @param resultMsg non-null; the result message
     * @return non-null; the new instance 
     */
    static public Document makeNoContent (int resultCode, String resultMsg)
    {
	return new StaticDocument (System.currentTimeMillis (),
				   resultCode,
				   resultMsg);
    }

    /**
     * Construct an instance (of {@link StaticDocument} actually, but it's
     * easier to mention the name of this class) that contains no content.
     * The modification date is taken to be the current time. Note that it
     * is not appropriate to hold on to the <code>extraHeaders</code>
     * parameter after constructing an instance. (It sure would be nice if
     * Java had immutability as a native concept.)
     *
     * @param resultCode the three-digit result code
     * @param resultMsg non-null; the result message
     * @param extraHeaders null-ok; map of additional response headers, if
     * any; if non-<code>null</code>, must be a map of <code>String</code>s
     * to <code>String</code>s
     * @return non-null; the new instance 
     */
    static public Document makeNoContent (int resultCode, String resultMsg,
					  Map extraHeaders)
    {
	return new StaticDocument (System.currentTimeMillis (),
				   resultCode,
				   resultMsg,
				   extraHeaders);
    }
}
