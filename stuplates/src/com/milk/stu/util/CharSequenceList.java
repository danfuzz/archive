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

package com.milk.stu.util;

import java.io.IOException;
import java.io.Writer;

/**
 * List of <code>CharSequence</code> objects, which is itself a
 * <code>CharSequence</code>. This is used as a replacement for
 * building a <code>String</code> using a <code>StringBuffer</code>
 * intermediary.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class CharSequenceList
    implements CharSequence
{
    /** non-null; array of sequences */
    private CharSequence[] mySequences;

    /** >= 0; current count of sequences */
    private int myCount;

    /** length of this instance */
    private int myLength;

    /** whether or not this instance is still mutable */
    private boolean myIsMutable;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param initialCapacity >= 0; the initial expected number of elements
     */
    public CharSequenceList (int initialCapacity)
    {
	if (initialCapacity < 0)
	{
	    throw new IllegalArgumentException ("initialCapacity < 0");
	}

	if (initialCapacity < 15)
	{
	    initialCapacity = 15;
	}

	mySequences = new CharSequence[initialCapacity];
	myCount = 0;
	myLength = 0;
	myIsMutable = true;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    public int length ()
    {
	return myLength;
    }

    // interface's comment suffices
    public char charAt (int index)
    {
	CharSequence seq = null;

	for (int i = 0; i < myCount; i++)
	{
	    seq = mySequences[i];
	    if (index < seq.length ())
	    {
		break;
	    }
	}

	return seq.charAt (index);
    }

    // interface's comment suffices
    public CharSequence subSequence (int start, int end)
    {
	StringBuffer sb = new StringBuffer (end - start);

	for (int i = start; i < end; i++)
	{
	    sb.append (charAt (i));
	}

	return sb.toString ();
    }

    // interface's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer (length ());

	for (int i = 0; i < myCount; i++)
	{
	    sb.append (mySequences[i]);
	}

	return sb.toString ();
    }

    /**
     * Make this instance immutable. After this method returns, this instance
     * may not be modified.
     */
    public void makeImmutable ()
    {
	myIsMutable = false;
    }

    /**
     * Append the chars form of the given object to this instance. If
     * passed as <code>null</code>, nothing is appended (in particular, not
     * the string <code>"null"</code>). This will throw an exception if
     * this instance has been made immutable.
     *
     * @param obj null-ok; the object to append 
     */
    public void append (Object obj)
    {
	if (! myIsMutable)
	{
	    throw new RuntimeException ("attempt to modify immutable " +
					"CharSequenceList");
	}

	if (obj == null)
	{
	    return;
	}

	CharSequence seq;

	if (obj instanceof CharSequence)
	{
	    seq = (CharSequence) obj;
	}
	else
	{
	    seq = obj.toString ();
	}

	int len = seq.length ();
	if (len == 0)
	{
	    return;
	}

	if (myCount == mySequences.length)
	{
	    CharSequence[] news = new CharSequence[myCount * 3 / 2];
	    System.arraycopy (mySequences, 0, news, 0, myCount);
	    mySequences = news;
	}

	mySequences[myCount] = seq;
	myCount++;
	myLength += len;
    }

    /**
     * Append the chars form of the given object to this instance. If
     * passed as <code>null</code>, then the string <code>"null"</code> is
     * appended. This will throw an exception if this instance has been
     * made immutable.
     *
     * @param obj null-ok; the object to append 
     */
    public void appendShowingNull (Object obj)
    {
	if (obj == null)
	{
	    append ("null");
	}
	else
	{
	    append (obj);
	}
    }

    /**
     * Write the contents of this instance to the given writer.
     *
     * @param writer non-null; the writer to write to
     * @throws IOException pass-through from the writer
     */
    public void writeTo (Writer writer)
	throws IOException
    {
	for (int i = 0; i < myCount; i++)
	{
	    CharSequence one = mySequences[i];
	    if (one instanceof String)
	    {
		writer.write ((String) one);
	    }
	    else if (one instanceof CharSequenceList)
	    {
		((CharSequenceList) one).writeTo (writer);
	    }
	    else
	    {
		int len = one.length ();
		for (int j = 0; j < len; j++)
		{
		    writer.write (one.charAt (j));
		}
	    }
	}
    }
}

