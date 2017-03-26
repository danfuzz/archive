// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
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

package com.milk.stu.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A node in a Stupid XML tree. To make it a little easier to deal with
 * instances, this class defines all possibly applicable methods.
 * It is up to users to use {@link #getType} or <code>instanceof</code> or
 * other means to determine which methods are appropriate for a given actual
 * instance.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
abstract public class XmlNode
{
    /** the start line, 1-based */
    private int myStartLine;

    /** the start column, 1-based */
    private int myStartColumn;

    /** the end line, inclusive, 1-based */
    private int myEndLine;

    /** the end column, exclusive, 1-based */
    private int myEndColumn;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param startLine the start line, 1-based
     * @param startColumn the start line, 1-based
     * @param endLine the end line, inclusive, 1-based
     * @param endColumn the end column, exclusive, 1-based
     */
    public XmlNode (int startLine, int startColumn,
		    int endLine, int endColumn)
    {
	if (startLine <= 0)
	{
	    throw new IllegalArgumentException ("startLine <= 0");
	}

	if (startColumn <= 0)
	{
	    throw new IllegalArgumentException ("startColumn <= 0");
	}

	if (endLine < startLine)
	{
	    throw new IllegalArgumentException ("endLine < startLine");
	}
	else if (endLine == startLine)
	{
	    if (endColumn < startColumn)
	    {
		throw new IllegalArgumentException ("(endLine == startLine)" +
						    " && " +
						    "(endLine < startLine)");
	    }
	}
	else
	{
	    if (endColumn <= 0)
	    {
		throw new IllegalArgumentException ("endColumn <= 0");
	    }
	}

	myStartLine = startLine;
	myStartColumn = startColumn;
	myEndLine = endLine;
	myEndColumn = endColumn;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    final public String toString ()
    {
	StringWriter sw = new StringWriter ();
	try
	{
	    writeTo (sw);
	}
	catch (IOException ex)
	{
	    throw new RuntimeException (ex);
	}

	return sw.toString ();
    }

    /**
     * Get the start line of this node. The numbering is 1-based.
     *
     * @return >= 1; the start line
     */
    final public int getStartLine ()
    {
	return myStartLine;
    }

    /**
     * Get the start column of this node. The numbering is 1-based.
     *
     * @return >= 1; the start column
     */
    final public int getStartColumn ()
    {
	return myStartColumn;
    }

    /**
     * Get the end line of this node, inclusive. The numbering is 1-based.
     *
     * @return >= 1; the end line
     */
    final public int getEndLine ()
    {
	return myEndLine;
    }

    /**
     * Get the end column of this node, exclusive. The numbering is 1-based.
     *
     * @return >= 1; the end column
     */
    final public int getEndColumn ()
    {
	return myEndColumn;
    }

    /**
     * Write this node to the given writer.
     *
     * @param writer non-null; the writer to write to
     */
    abstract public void writeTo (Writer writer)
	throws IOException;

    /**
     * Get the tag name of this node. This will throw an exception if
     * this is not a {@link TagNode}.
     *
     * @return non-null; the tag name
     */
    public String getTagName ()
    {
	throw new IllegalArgumentException ("not a tag: " + this);	
    }

    /**
     * Get the attribute set of this node. This will throw
     * an exception if this is not a {@link TagNode}.
     *
     * @return non-null; the attribute set
     */
    public AttributeSet getAttributes ()
    {
	throw new IllegalArgumentException ("not a tag: " + this);	
    }

    /**
     * Get the fragment representing the children of this node. This will
     * throw an exception if this is not a {@link TagNode}.
     *
     * @return non-null; the children, as a fragment
     */
    public Fragment getChildren ()
    {
	throw new IllegalArgumentException ("not a tag: " + this);	
    }

    /**
     * Get the text of this instance. This will throw an
     * exception if this is not a {@link TextNode} or a {@link CommentNode}.
     *
     * @return non-null; the text
     */
    public String getText ()
    {
	throw new IllegalArgumentException ("not text: " + this);
    }

    /**
     * Get the entity name of this instance. This will throw an
     * exception if this is not an {@link EntityNode}.
     *
     * @return non-null; the entity name
     */
    public String getEntityName ()
    {
	throw new IllegalArgumentException ("not an entity: " + this);
    }
}
