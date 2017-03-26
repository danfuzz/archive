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
 * A set of XML attributes.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class AttributeSet
{
    /** non-null; array of attributes name */
    private Attribute[] myAttributes;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. It is initially empty
     */
    public AttributeSet ()
    {
	myAttributes = new Attribute[0];
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
	for (int i = 0; i < myAttributes.length; i++)
	{
	    writer.write (' ');
	    myAttributes[i].writeTo (writer);
	}
    }

    /**
     * Get the count of attributes.
     *
     * @return the count
     */
    public int getCount ()
    {
	return myAttributes.length;
    }

    /**
     * Get the attribute at the given index.
     *
     * @param n >= 0 && < getCount(); the index
     * @return non-null; the attribute at the given index.
     */
    public Attribute get (int n)
    {
	return myAttributes[n];
    }

    /**
     * Put a new attribute in this instance. If there was already an attribute
     * with the same name (compared with <code>Object.equals()</code>, then
     * replace it and return the old value. If not, add the new attribute
     * and return <code>null</code>.
     *
     * @param attrib non-null; the attribute to add
     * @return null-ok; the old attribute with the same name, if any
     */
    public Attribute put (Attribute attrib)
    {
	if (attrib == null)
	{
	    throw new NullPointerException ("attrib == null");
	}

	String name = attrib.getName ();

	for (int i = 0; i < myAttributes.length; i++)
	{
	    Attribute one = myAttributes[i];
	    if (name.equals (one.getName ()))
	    {
		myAttributes[i] = attrib;
		return one;
	    }
	}

	Attribute[] newa = new Attribute[myAttributes.length + 1];
	System.arraycopy (myAttributes, 0, newa, 0, myAttributes.length);
	newa[myAttributes.length] = attrib;
	myAttributes = newa;

	return null;
    }
}
