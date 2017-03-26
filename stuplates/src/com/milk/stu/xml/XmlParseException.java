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

/**
 * An exception from parsing Stupid XML. Its special feature is knowing
 * line/column information to go with the exception.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class XmlParseException
    extends RuntimeException
{
    /** the source range */
    private SourceRange mySourceRange;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param message null-ok; associated message
     * @param startLine the start line, 1-based
     * @param startColumn the start line, 1-based
     * @param endLine the end line, inclusive, 1-based
     * @param endColumn the end column, exclusive, 1-based
     */
    public XmlParseException (String message, int startLine, int startColumn,
			      int endLine, int endColumn)
    {
	super (message);
	mySourceRange = new SourceRange (startLine, startColumn,
					 endLine, endColumn);
    }

    /**
     * Construct an instance.
     *
     * @param throwable null-ok; embedded throwable
     * @param startLine the start line, 1-based
     * @param startColumn the start line, 1-based
     * @param endLine the end line, inclusive, 1-based
     * @param endColumn the end column, exclusive, 1-based
     */
    public XmlParseException (Throwable throwable,
			      int startLine, int startColumn,
			      int endLine, int endColumn)
    {
	super (throwable);
	mySourceRange = new SourceRange (startLine, startColumn,
					 endLine, endColumn);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Get the source range of this instance.
     *
     * @return non-null; the source range
     */
    public SourceRange getSourceRange ()
    {
	return mySourceRange;
    }
}
