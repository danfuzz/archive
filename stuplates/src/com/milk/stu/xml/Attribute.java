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
 * An XML attribute.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class Attribute
{
    /** non-null; the attribute name */
    private final String myName;

    /** non-null; the attribute value */
    private final Fragment myValue;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name non-null; the attribute name
     * @param value non-null; the attribute value
     */
    public Attribute (String name, Fragment value)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	myName = name;
	myValue = value;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Write this instance to the given writer.
     *
     * @param writer non-null; the writer to write to
     */
    public void writeTo (Writer writer)
	throws IOException
    {
	writer.write (myName);
	writer.write ("=\"");
	myValue.writeTo (writer);
	writer.write ('\"');
    }

    /**
     * Get the name.
     *
     * @return non-null; the name
     */
    public String getName ()
    {
	return myName;
    }

    /**
     * Get the value.
     *
     * @return non-null; the value
     */
    public Fragment getValue ()
    {
	return myValue;
    }
}
