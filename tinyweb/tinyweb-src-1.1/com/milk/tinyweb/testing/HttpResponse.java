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

import java.util.HashMap;

/**
 * This is what gets returned by {@link TestHttpGetter#getURL}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class HttpResponse
{
    /** the initial response result line */
    private String myResult;

    /** the headers */
    private HashMap myHeaders;

    /** the content */
    private byte[] myContent;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param result the initial response result line
     * @param headers a <code>{String, String}</code> map of header fields
     * @param content a byte array of the content
     */
    public HttpResponse (String result, HashMap headers, byte[] content)
    {
	myResult = result;
	myHeaders = headers;
	myContent = content;
    }

    // ------------------------------------------------------------------------
    // public methods

    /**
     * Return a string form of this object.
     *
     * @return a string form
     */
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();
	sb.append (getClass ().getName ());
	sb.append ("[result=");
	sb.append (myResult);
	sb.append (", headers=");
	sb.append (myHeaders);
	sb.append (", content=");
	sb.append (new String (myContent));
	sb.append ("]");
	return sb.toString ();
    }

    /**
     * Get the header map.
     *
     * @return the header map
     */
    public HashMap getHeaders ()
    {
	return myHeaders;
    }

    /**
     * Get the value for the given header or <code>null</code> if there
     * is no such header.
     *
     * @param header the header to get
     * @return the value for the header or <code>null</code>
     */
    public String getHeader (String header)
    {
	return (String) myHeaders.get (header);
    }

    /**
     * Get the content.
     *
     * @return the content
     */
    public byte[] getContent ()
    {
	return myContent;
    }

    /**
     * Get the initial response result line.
     *
     * @return the initial response result line
     */
    public String getResult ()
    {
	return myResult;
    }
}
