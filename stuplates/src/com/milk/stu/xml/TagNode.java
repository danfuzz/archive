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
import java.io.Writer;

/**
 * A node in a Stupid XML tree representing a tag and all its children.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class TagNode
    extends XmlNode
{
    /** non-null; the name of the tag */
    private final String myTagName;

    /** non-null; the set of attributes */
    private final AttributeSet myAttributes;

    /** non-null; the children */
    private final Fragment myChildren;

    

    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param startLine the start line, 1-based
     * @param startColumn the start line, 1-based
     * @param endLine the end line, inclusive, 1-based
     * @param endColumn the end column, exclusive, 1-based
     * @param tagName non-null; the name of the tag
     * @param attributes non-null; the set of attributes
     * @param children non-null; the children
     */
    public TagNode (int startLine, int startColumn,
		    int endLine, int endColumn,
		    String tagName, AttributeSet attributes, Fragment children)
    {
	super (startLine, startColumn, endLine, endColumn);

	if (tagName == null)
	{
	    throw new NullPointerException ("tagName == null");
	}

	if (attributes == null)
	{
	    throw new NullPointerException ("attributes == null");
	}

	if (children == null)
	{
	    throw new NullPointerException ("children == null");
	}

	myTagName = tagName;
	myAttributes = attributes;
	myChildren = children;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public void writeTo (Writer writer)
	throws IOException
    {
	writer.write ('<');
	writer.write (myTagName);
	myAttributes.writeTo (writer);

	if (myChildren.getCount () == 0)
	{
	    writer.write ("/>");
	}
	else
	{
	    writer.write ('>');
	    myChildren.writeTo (writer);
	    writer.write ("</");
	    writer.write (myTagName);
	    writer.write ('>');
	}
    }

    // superclass's comment suffices
    public String getTagName ()
    {
	return myTagName;
    }

    // superclass's comment suffices
    public AttributeSet getAttributes ()
    {
	return myAttributes;
    }

    // superclass's comment suffices
    public Fragment getChildren ()
    {
	return myChildren;
    }
}
