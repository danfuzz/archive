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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * Document whose content comes from a named file. It can optionally
 * stream documents that dynamically grow, such as log files. If an
 * instance refers to a file that doesn't exist, this will act as if
 * it is just a file of length <code>0</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FileDocument
extends Document
{
    /** the size of the buffer to use when writing documents */
    private static final int BUF_SIZE = 65000;

    /** the amount of time between when a file stops getting written
     * to and the output stream for it gets closed, when sending
     * a dynamically growing document */
    private static final long DYNAMIC_WAIT_MSEC = 15000;

    /** the file to use for the content */
    private File myFile;

    /** whether or not this document is dynamically growing */
    private boolean myDynamic;

    /** the offset to start output from (negative means offset from the
     * end) */
    private long myOffset;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. The content type and modification date are
     * determined from the given file. See {@link FileSystemHandler} for
     * information about the query parameters.
     *
     * @param file non-null; the file to use for the content
     * @param query null-ok; query parameters to determine offset and
     * dynamism
     */
    public FileDocument (File file, String query)
    {
	super (nameToContentType (file.getName ()), file.lastModified ());

	if (file == null)
	{
	    throw new NullPointerException ("file == null");
	}

	myFile = file;

	if (query != null)
	{
	    myDynamic = URLUtils.queryGetBoolean (query, "dynamic", false);
	    myOffset = URLUtils.queryGetLong (query, "offset", 0);
	}
	else
	{
	    myDynamic = false;
	    myOffset = 0;
	}
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's javadoc suffices
    public long getContentLength ()
    {
	if (myDynamic)
	{
	    return CONTENT_LENGTH_UNKNOWN;
	}

	if (! (myFile.exists () && myFile.canRead ()))
	{
	    return 0;
	}

	long len = myFile.length ();
	long startAt = getStartAt ();

	return len - startAt;
    }

    // superclass's javadoc suffices
    public void writeBytes (OutputStream stream, long contentLength)
	throws IOException
    {
	if (contentLength == 0)
	{
	    return;
	}

	FileInputStream fis = new FileInputStream (myFile);
	fis.skip (getStartAt ());

	long giveUpAt = 0;
	byte[] buf;

	if (myDynamic)
	{
	    contentLength = Long.MAX_VALUE;
	}

	if (contentLength < BUF_SIZE)
	{
	    buf = new byte[(int) contentLength];
	}
	else
	{
	    buf = new byte[BUF_SIZE];
	}

	int bufLen = buf.length;

	while (myDynamic || (contentLength > 0))
	{
	    int amt = (contentLength > bufLen) ? bufLen : (int) contentLength;

	    try
	    {
		amt = fis.read (buf, 0, amt);
	    }
	    catch (IOException ex)
	    {
		throw new EmbeddedException ("trouble reading file: " + 
					     myFile, ex);
	    }

	    if (amt == -1)
	    {
		// end of file
		if (! myDynamic)
		{
		    break;
		}

		long now = System.currentTimeMillis ();
		if (giveUpAt == 0)
		{
		    giveUpAt = now + DYNAMIC_WAIT_MSEC;
		}
		else if (giveUpAt < now)
		{
		    // the file hasn't changed in too long; time to
		    // call it quits
		    break;
		}

		stream.flush ();

		try
		{
		    Thread.sleep (1000);
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }
	    else
	    {
		try
		{
		    stream.write (buf, 0, amt);
		}
		catch (IOException ex)
		{
		    if (myDynamic && ex.getMessage ().equals ("Broken pipe"))
		    {
			// squelch the exception if it's the common case of
			// a user hitting "stop" on their browser in the
			// middle of streaming a dynamic document
			return;
		    }
		    else
		    {
			throw ex;
		    }
		}

		contentLength -= amt;
		giveUpAt = 0;
	    }
	}
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Get the offset into the file to start at.
     *
     * @return the offset into the file to start at
     */
    private long getStartAt ()
    {
	long len = myFile.length ();
	long startAt = (myOffset < 0) ? (len + myOffset) : myOffset;

	if (startAt < 0)
	{
	    startAt = 0;
	}
	else if (startAt > len)
	{
	    startAt = len;
	}

	return startAt;
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Get the content type based on the name of a file.
     *
     * @param name non-null; the name of the file
     * @return the content type
     */
    static public String nameToContentType (String name)
    {
	String contentType = 
	    URLConnection.getFileNameMap ().getContentTypeFor (name);

	if (contentType == null)
	{
	    contentType = "text/plain";
	}

	if (contentType.equals ("text/plain")
	    || contentType.equals ("text/html"))
	{
	    contentType += "; charset=iso-8859-1";
	}

	return contentType;
    }
}
