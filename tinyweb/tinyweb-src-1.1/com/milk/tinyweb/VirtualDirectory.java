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

import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class is used as a directory in a web server. It holds references
 * to documents and other handlers, and can dispatch to the right one
 * based on the first path component of paths it is asked to resolve. If
 * the directory has a document called <code>"index.html"</code> registered
 * with it, then that document will be returned when the directory itself
 * is requested; otherwise, a directory listing will be returned.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class VirtualDirectory
implements DocumentHandler
{
    /** map from path components to documents or handlers */
    private TreeMap myMap;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     */
    public VirtualDirectory ()
    {
	myMap = new TreeMap ();
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // interface's javadoc suffices
    public synchronized Document handleRequest (String query, 
						String partialPath,
						HttpRequest request)
    {
	if ((partialPath == null) || (partialPath.length () == 0))
	{
	    // they want the contents of the directory itself
	    if (myMap.get ("index.html") != null)
	    {
		// there's an "index.html" registered; use it
		return handleRequest (query, "index.html", request);
	    }

	    // no index.html; create a directory listing

	    StringBuffer sb = new StringBuffer ();
	    sb.append ("<html>\n" +
		       "<head>\n" +
		       "<title>Directory</title>\n" +
		       "</head>\n" +
		       "<body>\n" +
		       "<h1>Directory</h1>\n");

	    sb.append ("<code><a href=\"..\">..</a></code> " +
		       "(Parent Directory)<br>\n");

	    Iterator keys = myMap.keySet ().iterator ();
	    while (keys.hasNext ())
	    {
		String one = (String) keys.next ();
		boolean isDir = ! (myMap.get (one) instanceof Document);

		sb.append ("<code><a href=\"");
		sb.append (URLUtils.escapePathComponent (one));

		if (isDir)
		{
		    sb.append ('/');
		}

		sb.append ("\">");
		sb.append (HTMLUtils.literalToHtml (one));

		if (isDir)
		{
		    sb.append ('/');
		}

		sb.append ("</a></code><br>\n");
	    }

	    sb.append ("</body>\n" +
		       "</html>\n");

	    return Document.makeHTML (sb.toString ());
	}

	String[] parsed = URLUtils.parseFirstComponent (partialPath);

	if (parsed == null)
	{
	    return null;
	}

	DocumentHandler found = (DocumentHandler) myMap.get (parsed[0]);

	if (found == null)
	{
	    return null;
	}

	return found.handleRequest (parsed[1], parsed[2], request);
    }

    // interface's javadoc suffices
    public synchronized void putDocument (String partialPath, 
					  DocumentHandler doc)
    {
	if (partialPath == null)
	{
	    throw new NullPointerException ("partialPath == null");
	}

	if (doc == null)
	{
	    throw new NullPointerException ("doc == null");
	}

	String[] parsed = URLUtils.parseFirstComponent (partialPath);

	if (parsed == null)
	{
	    throw new RuntimeException ("Could not parse path: " + 
					partialPath);
	}

	if ((parsed[0] == null) || (parsed[0].length () == 0))
	{
	    throw new RuntimeException ("Empty component detected");
	}

	if (parsed[2] == null)
	{
	    // we need to put something in this instance directly; do so
	    myMap.put (parsed[0], doc);
	    return;
	}

	DocumentHandler found = (DocumentHandler) myMap.get (parsed[0]);

	if (found == null)
	{
	    // need to create a new intermediate directory
	    found = new VirtualDirectory ();
	    myMap.put (parsed[0], found);
	}

	// recurse to do the actual put
	found.putDocument (parsed[2], doc);
    }
}
