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

import java.io.File;
import java.util.Arrays;

/**
 * This class is used to mirror a real file system hierarchy as a hierarchy
 * in a web server. It knows how to create directory listings for the
 * directories under its purview. This handler recognizes two query parameters:
 *
 * <dl>
 * <dt><code>dynamic=1</code></dt>
 * <dd>When set, the file is taken to be a dynamically growing one, and
 * the content will be incrementally streamed. This is useful for things
 * like logfiles.</dd>
 * <dt><code>offset=<i>offset</i></code></dt>
 * <dd>This sets the starting offset into the file. Positive numbers mean
 * offsets from the beginning of the file, and negative numbers mean offsets
 * from the end of the file. This is also useful for things like logfiles.</dd>
 * </dl>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FileSystemHandler
implements DocumentHandler
{
    /** the length limit of a document */
    private static final int DOCUMENT_MAX_LENGTH = 50 * 1024;

    /** the base directory that this instance uses */
    private File myBaseDirectory;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance. The given directory should refer to an
     * actual readable directory. If not, then this instance won't fail,
     * per se, but it will always return <code>null</code> to any document
     * requests it receives.
     *
     * @param baseDirectory non-null; the base directory that this instance
     * uses 
     */
    public FileSystemHandler (File baseDirectory)
    {
	if (baseDirectory == null)
	{
	    throw new NullPointerException ("baseDirectory == null");
	}

	myBaseDirectory = baseDirectory;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    // interface's javadoc suffices
    public Document handleRequest (String query, String partialPath,
				   HttpRequest request)
    {
	// recursively walk down the filesystem, until we find the
	// requested document or run out of places to go

	File curFile = myBaseDirectory;

	if (! curFile.isDirectory ())
	{
	    // better start with a direc
	    return null;
	}

	for (;;)
	{
	    if (partialPath == null)
	    {
		// we have reached the end

		if (curFile.isDirectory ())
		{
		    // they want the contents of the directory itself
		    return makeDirectoryDocument (curFile);
		}
		else
		{
		    // they want the contents of this file
		    if (curFile.isFile () 
			&& curFile.canRead ()
			&& (curFile != myBaseDirectory))
		    {
			return new FileDocument (curFile, query);
		    }

		    // file not found or not readable or (weird case)
		    // the base "directory" turned out to be a regular file
		    return null;
		}
	    }

	    String[] parsed = URLUtils.parseFirstComponent (partialPath);

	    if (parsed == null)
	    {
		return null;
	    }

	    File f = new File (curFile, parsed[0]);

	    if (! (f.exists () && f.canRead ()))
	    {
		// ran into an unreadable/nonexistent file/dir
		return null;
	    }

	    curFile = f;
	    query = parsed[1];
	    partialPath = parsed[2];
	}
    }

    // interface's javadoc suffices
    public void putDocument (String partialPath, DocumentHandler doc)
    {
	throw new RuntimeException ("putDocument() not supported.");
    }

    // ------------------------------------------------------------------------
    // private static methods

    /**
     * Create and return a directory listing document for the given
     * directory.
     *
     * @param dir non-null; the directory to query
     * @return a directory listing document
     */
    static private Document makeDirectoryDocument (File dir)
    {
	if (dir == null)
	{
	    throw new NullPointerException ("dir == null");
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("<html>\n" +
		   "<head>\n" +
		   "<title>Directory</title>\n" +
		   "</head>\n" +
		   "<body>\n" +
		   "<h1>Directory</h1>\n");

	sb.append ("<code><a href=\"..\">..</a></code> " +
		   "(Parent Directory)<br>\n");

	File[] files = null;

	if (dir.canRead () && dir.isDirectory ())
	{
	    files = dir.listFiles ();
	}

	if (files == null)
	{
	    files = new File[0];
	}

	Arrays.sort (files);

	for (int i = 0; i < files.length; i++)
	{
	    File one = files[i];
	    boolean isDir = one.isDirectory ();
	    String name = one.getName ();

	    sb.append ("<code><a href=\"");
	    sb.append (URLUtils.escapePathComponent (name));

	    if (isDir)
	    {
		sb.append ('/');
	    }

	    sb.append ("\">");
	    sb.append (HTMLUtils.literalToHtml (name));

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
}
