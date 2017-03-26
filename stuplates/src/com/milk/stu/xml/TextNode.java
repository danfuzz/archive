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
 * A node in a Stupid XML tree representing a run of text.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class TextNode
    extends XmlNode
{
    /** non-null; the text in question */
    private final String myText;

    

    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param startLine the start line, 1-based
     * @param startColumn the start line, 1-based
     * @param endLine the end line, inclusive, 1-based
     * @param endColumn the end column, exclusive, 1-based
     * @param text non-null; the text in question
     */
    public TextNode (int startLine, int startColumn,
		     int endLine, int endColumn, String text)
    {
	super (startLine, startColumn, endLine, endColumn);

	if (text == null)
	{
	    throw new NullPointerException ("text == null");
	}

	myText = text;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public void writeTo (Writer writer)
	throws IOException
    {
	writer.write (myText);
    }

    // superclass's comment suffices
    public String getText ()
    {
	return myText;
    }
}
