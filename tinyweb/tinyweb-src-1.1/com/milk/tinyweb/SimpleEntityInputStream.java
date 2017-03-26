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

/**
 * Input stream that reads an entity from an HTTP request, where that
 * entity has no transfer-encoding. That is, the HTTP request came with a
 * <code>Content-Length</code> header but not a
 * <code>Transfer-Encoding</code> header, or it came with a
 * <code>Transfer-Encoding</code> header with the value
 * <code>"identity"</code>. See RFC2616, sec. 7 in particular, for
 * salient details.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class SimpleEntityInputStream
extends InputStream
{
    /** the underlying stream to read from */
    private InputStream myRawStream;

    /** how many bytes are left to read */
    private long myBytesLeft;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param rawStream non-null; the raw (presumably network) stream to
     * get data from
     * @param contentLength the total number of bytes to be read from the
     * stream 
     */
    public SimpleEntityInputStream (InputStream rawStream, long contentLength)
    {
	if (rawStream == null)
	{
	    throw new NullPointerException ("rawStream = null");
	}

	if (contentLength < 0)
	{
	    throw new IllegalArgumentException ("contentLength < 0");
	}

	myRawStream = rawStream;
	myBytesLeft = contentLength;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's documentation suffices
    public int available ()
	throws IOException
    {
	if (myRawStream == null)
	{
	    throw new IOException ("stream is closed");
	}

	int result = myRawStream.available ();
	return (result > myBytesLeft) ? (int) myBytesLeft : result;
    }

    // superclass's documentation suffices
    public void close ()
	throws IOException
    {
	if (myRawStream == null)
	{
	    return;
	}

	// skip the rest of the input on the raw stream, if any, so that 
	// the stream is "positioned" past the end of the entity, in
	// case this stream is being used for multiple requests
	myRawStream.skip (myBytesLeft);

	myRawStream = null;
    }

    // superclass's documentation suffices
    public int read ()
	throws IOException
    {
	if (myBytesLeft == 0)
	{
	    return -1;
	}

	if (myRawStream == null)
	{
	    throw new IOException ("stream is closed");
	}

	// note, order is important here, because of possibly-thrown
	// exceptions
	int result = myRawStream.read ();
	myBytesLeft--;
	return result;
    }

    // superclass's documentation suffices
    public int read (byte[] b, int off, int len)
	throws IOException
    {
	if (b == null)
	{
	    throw new NullPointerException ("b = null");
	}

	if (len == 0)
	{
	    return 0;
	}

	if ((off < 0) || (len < 0) || ((off + len) > b.length))
	{
	    throw new IndexOutOfBoundsException ();
	}

	if (myRawStream == null)
	{
	    throw new IOException ("stream is closed");
	}

	if (myBytesLeft == 0)
	{
	    return -1;
	}

	if (len > myBytesLeft)
	{
	    len = (int) myBytesLeft;
	}

	// note, order is important here, because of possibly-thrown
	// exceptions
	int result = myRawStream.read (b, off, len);
	myBytesLeft -= result;
	return result;
    }

    // superclass's documentation suffices
    public long skip (long n)
	throws IOException
    {
	if (myRawStream == null)
	{
	    throw new IOException ("stream is closed");
	}

	if (n > myBytesLeft)
	{
	    n = myBytesLeft;
	}

	// note, order is important here, because of possibly-thrown
	// exceptions
	long result = myRawStream.skip (n);
	myBytesLeft -= result;
	return result;
    }
}
