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

package com.milk.stu.iface;

import java.util.TreeMap;

/**
 * Instances of this class are more or less like interned strings, except
 * that they each get a unique sequence number that is used to make
 * variable-type lookups quicker. This actual <i>class</i> lives in the
 * <code><i>iface</i></code> package because it is small, it should never
 * need to be subclassed, and it is referred to by other classes (er,
 * interfaces) in this package.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class Identifier
    implements Comparable
{
    /** the map from names to instances */
    static private TreeMap theMap = new TreeMap ();

    /** the id for the next instance */
    static private int theNextId = 0;

    /** non-null; the name */
    private final String myName;

    /** non-null; the id */
    private final int myId;



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Get the unique instance associated with the given name.
     *
     * @param name non-null; the name
     * @return non-null; the associated instance
     */
    static public synchronized Identifier intern (String name)
    {
	name = name.intern ();

	Identifier ident = (Identifier) theMap.get (name);
	if (ident == null)
	{
	    ident = new Identifier (name, theNextId);
	    theNextId++;
	    theMap.put (name, ident);
	}

	return ident;
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is private. Use {@link #intern}.
     *
     * @param name non-null; the name of the identifier
     * @param id non-null; its id number
     */
    private Identifier (String name, int id)
    {
	myName = name;
	myId = id;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	// xxx--should unmangle mangled names
	return myName;
    }

    // superclass's comment suffices
    public int hashCode ()
    {
	return myId;
    }

    // superclass's comment suffices
    public boolean equals (Object other)
    {
	return this == other;
    }

    /**
     * Note: This implementation always returns one of <code>-1</code>,
     * <code>0</code>, or <code>1</code>.
     */
    public int compareTo (Object other)
    {
	if (this == other)
	{
	    return 0;
	}

	Identifier otheri = (Identifier) other;
	long diff = otheri.myId - myId;
	return (diff < 0) ? -1 : 1;
    }

    /**
     * Get the name. The result is always an interned string.
     *
     * @return non-null; the name
     */
    public String getName ()
    {
	return myName;
    }
}
