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
import java.util.List;

/**
 * An XML fragment, consisting of a list of nodes.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class Fragment
{
    /** non-null; array of nodes */
    private final XmlNode[] myNodes;



    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance.
     *
     * @param nodes non-null, with non-null elements; the nodes of the fragment
     */
    public Fragment (XmlNode[] nodes)
    {
	if (nodes == null)
	{
	    throw new NullPointerException ("children == null");
	}

	for (int i = 0; i < nodes.length; i++)
	{
	    if (nodes[i] == null)
	    {
		throw new NullPointerException ("nodes[" + i + "] == null");
	    }
	}

	myNodes = nodes;
    }

    /**
     * Construct an instance.
     *
     * @param nodes non-null, with non-null elements; the nodes of the fragment
     */
    public Fragment (List nodes)
    {
	this ((XmlNode[]) nodes.toArray (new XmlNode[nodes.size ()]));
    }

    /**
     * Construct an empty instance.
     */
    public Fragment ()
    {
	this (new XmlNode[0]);
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
	for (int i = 0; i < myNodes.length; i++)
	{
	    myNodes[i].writeTo (writer);
	}
    }

    /**
     * Get the number of nodes in this fragment.
     *
     * @return >= 0; the number of nodes
     */
    public int getCount ()
    {
	return myNodes.length;
    }

    /**
     * Get the node of this instance with the given index.
     *
     * @param n >= 0 && < getCount(); which node to get
     * @return non-null; the <code>n</code>th node
     */
    public XmlNode get (int n)
    {
	return myNodes[n];
    }
}
