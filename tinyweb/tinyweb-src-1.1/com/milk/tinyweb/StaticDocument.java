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
import java.io.OutputStream;
import java.util.Map;

/**
 * This class holds a complete document in memory. It is suitable
 * for keeping in a cache, and is generally convenient to create.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class StaticDocument
extends Document
{
    /** the bytes of the document */
    private final byte[] myBytes;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance with explicit result code and message. Note
     * that it is not appropriate to hold on to the <code>bytes</code> or
     * <code>extraHeaders</code> parameters after constructing an instance.
     * (It sure would be nice if Java had immutability as a native
     * concept.)
     *
     * @param contentType non-null; the MIME content type of the document
     * @param lastModified the last modified date of the document
     * @param bytes non-null; the bytes of the document
     * @param resultCode the three-digit result code
     * @param resultMsg non-null; the result message
     * @param extraHeaders null-ok; map of additional response headers, if
     * any; if non-<code>null</code>, must be a map of <code>String</code>s
     * to <code>String</code>s
     */
    public StaticDocument (String contentType, long lastModified, byte[] bytes,
			   int resultCode, String resultMsg, Map extraHeaders)
    {
	super (contentType, lastModified, resultCode, resultMsg, extraHeaders);

	if (bytes == null)
	{
	    throw new NullPointerException ("bytes=null");
	}

	myBytes = bytes;
    }

    /**
     * Construct an instance with explicit result code and message, but
     * with no content.
     *
     * @param lastModified the last modified date of the document
     * @param resultCode the three-digit result code
     * @param resultMsg non-null; the result message
     */
    public StaticDocument (long lastModified, int resultCode, String resultMsg)
    {
	super ("", lastModified, resultCode, resultMsg, null);

	myBytes = null;
    }

    /**
     * Construct an instance with explicit result code and message, and
     * extra headers, but with no content. Note that it is not appropriate
     * to hold on to the <code>extraHeaders</code> parameter after
     * constructing an instance. (It sure would be nice if Java had
     * immutability as a native concept.)
     *
     * @param lastModified the last modified date of the document
     * @param resultCode the three-digit result code
     * @param resultMsg non-null; the result message
     * @param extraHeaders null-ok; map of additional response headers, if
     * any; if non-<code>null</code>, must be a map of <code>String</code>s
     * to <code>String</code>s 
     */
    public StaticDocument (long lastModified, int resultCode, String resultMsg,
			   Map extraHeaders)
    {
	super ("", lastModified, resultCode, resultMsg, extraHeaders);

	myBytes = null;
    }

    /**
     * Construct an instance. Note that it is not appropriate to hold
     * on to the <code>bytes</code> parameter after constructing an
     * instance. (It sure would be nice if Java had immutability as
     * a native concept.)
     *
     * @param contentType non-null; the MIME content type of the document
     * @param lastModified the last modified date of the document
     * @param bytes non-null; the bytes of the document
     */
    public StaticDocument (String contentType, long lastModified, byte[] bytes)
    {
	super (contentType, lastModified);

	if (bytes == null)
	{
	    throw new NullPointerException ("bytes=null");
	}

	myBytes = bytes;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's javadoc suffices
    public long getContentLength ()
    {
	if (myBytes != null)
	{
	    return myBytes.length;
	}
	else
	{
	    return CONTENT_LENGTH_NONE;
	}
    }

    // superclass's javadoc suffices
    public void writeBytes (OutputStream stream, long contentLength)
	throws IOException
    {
	if (myBytes != null)
	{
	    stream.write (myBytes);
	}
    }
}
