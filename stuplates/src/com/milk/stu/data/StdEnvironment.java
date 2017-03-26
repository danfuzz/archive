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

package com.milk.stu.data;

import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;
import com.milk.stu.iface.Slot;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Standard implementation of {@link Environment}.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public final class StdEnvironment
    implements Environment
{
    /** null-ok; the parent environment, if any */
    private final Environment myParent;

    /** non-null; array of bindings, sorted */
    private Binding[] myBindings;

    /** current number of bindings */
    private int myBindingCount;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct a new instance.
     *
     * @param parent null-ok; the parent environment, if any
     * @param initialCapacity the initial (expected) number of bindings
     * that this instance will hold
     */
    public StdEnvironment (Environment parent, int initialCapacity)
    {
	if (initialCapacity < 15)
	{
	    initialCapacity = 15;
	}

	myParent = parent;
	myBindings = new Binding[initialCapacity];
	myBindingCount = 0;

	defineAlways (Names.VAR_thisEnv, this);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ('[');

	boolean first = true;
	TreeMap tree = new TreeMap ();
	putBindingsIn (tree);
	Iterator i = tree.entrySet ().iterator ();
	while (i.hasNext ())
	{
	    Map.Entry one = (Map.Entry) i.next ();

	    if (first)
	    {
		first = false;
	    }
	    else
	    {
		sb.append (", ");
	    }

	    sb.append (one.getKey ());
	    sb.append (": ");
	    sb.append (one.getValue ());
	}

	if (first)
	{
	    sb.append (':');
	}

	sb.append (']');

	return sb.toString ();
    }

    // interface's comment suffices
    public Slot define (Identifier name)
    {
	Slot slot = getSlotIfDefined (name);
	if (slot != null)
	{
	    throw new RuntimeException ("variable already defined: " + name);
	}

	return defineAlways (name, null);
    }

    // interface's comment suffices
    public Slot defineAlways (Identifier name, Object value)
    {
	int index = getSlotIndex (name);
	Slot slot;

	if (index >= 0)
	{
	    slot = myBindings[index].getSlot ();
	}
	else
	{
	    slot = new DirectSlot ();
	    Binding b = new Binding (name, slot);
	
	    if (myBindingCount == myBindings.length)
	    {
		Binding[] newb = new Binding[myBindingCount * 3 / 2 + 2];
		System.arraycopy (myBindings, 0, newb, 0, myBindingCount);
		myBindings = newb;
	    }

	    index = ~index;
	    if (index < myBindingCount)
	    {
		System.arraycopy (myBindings, index, myBindings, index + 1,
				  myBindingCount - index);
	    }
	    myBindings[index] = b;
	    myBindingCount++;
	}

	slot.setValue (value);
	return slot;
    }

    // interface's comment suffices
    public Slot getSlotIfDefined (Identifier name)
    {
	int index = getSlotIndex (name);

	if (index >= 0)
	{
	    return myBindings[index].getSlot ();
	}

	if (myParent != null)
	{
	    return myParent.getSlotIfDefined (name);
	}

	return null;
    }

    // interface's comment suffices
    public Slot getSlot (Identifier name)
    {
	Slot slot = getSlotIfDefined (name);

	if (slot == null)
	{
	    throw new RuntimeException ("undefined variable: " + name);
	}

	return slot;
    }

    // interface's comment suffices
    public Object getValue (Identifier name)
    {
	return getSlot (name).getValue ();
    }

    // interface's comment suffices
    public Environment makeChild ()
    {
	return new StdEnvironment (this, 0);
    }

    // interface's comment suffices
    public Environment makeChild (int initialCapacity)
    {
	return new StdEnvironment (this, initialCapacity);
    }

    // interface's comment suffices
    public void putBindingsIn (Map map)
    {
	if (myParent != null)
	{
	    // do the parent first, so bindings that are shadowed will
	    // get overwritten
	    myParent.putBindingsIn (map);
	}

	for (int i = 0; i < myBindingCount; i++)
	{
	    Binding one = myBindings[i];
	    map.put (one.getName (), one.getSlot ());
	}
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Get the index of the named slot if it is directly defined by this
     * instance. If it is not defined, then get the one's complement of the
     * index that such a slot should be inserted at (one's complement so
     * that "insert at index zero" can be represented).
     *
     * @param name non-null; the name of the variable
     * @return null-ok; the variable's slot or <code>null</code> if there
     * is no such variable directly defined in this instance
     */
    private int getSlotIndex (Identifier name)
    {
	Binding[] barr = myBindings; // to avoid instance variable access

	int min = 0;
	int max = myBindingCount;
	for (;;)
	{
	    if (min >= max)
	    {
		// not found; return the one's comlement of the insert pos
		return ~max;
	    }

	    int guess = (max + min) >> 1;
	    int comp = barr[guess].compareTo (name);

	    switch (comp)
	    {
		case 0: return guess;
		case 1: max = guess; break;
		default: min = guess + 1; break;
	    }
	}

	// note: all returns happen in the for loop above
    }



    // ------------------------------------------------------------------------
    // inner classes

    /**
     * Variable binding from a name to a slot. Note that comparisons only
     * ever check the name, since that's all that's ever needed.
     */
    private final class Binding
    {
	/** non-null; the variable name */
	private final Identifier myName;

	/** non-null; the associated slot */
	private final Slot mySlot;

	/** the identity hash code of the name */
	private final int myNameHash;

	/**
	 * Construct an instance.
	 *
	 * @param name non-null; the variable name
	 * @param slot non-null; the associated slot
	 */
	public Binding (Identifier name, Slot slot)
	{
	    myName = name;
	    mySlot = slot;
	    myNameHash = name.hashCode ();
	}

	// superclass's comment suffices
	public boolean equals (Object other)
	{
	    return this == other;
	}

	/**
	 * Like the usual {@link #compareTo}, except compare with a
	 * variable name, and the return value is always one
	 * of <code>-1</code>, <code>0</code>, or <code>1</code>.
	 *
	 * @param name non-null; the name to compare to
	 * @return the usual comparison result
	 */
	public int compareTo (Identifier name)
	{
	    return myName.compareTo (name);
	}

	/**
	 * Get the name.
	 *
	 * @return non-null; the name
	 */
	public Identifier getName ()
	{
	    return myName;
	}

	/**
	 * Get the slot.
	 *
	 * @return non-null; the slot
	 */
	public Slot getSlot ()
	{
	    return mySlot;
	}
    }
}
