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

import com.milk.stu.iface.Identifier;

/**
 * Bundle of a method object (of some sort) with other useful metadata.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
abstract public class MethodBundle
    implements Comparable
{
    /** non-null; the name of the method */
    private Identifier myName;

    /** whether or not this is a static method */
    private boolean myIsStatic;

    /** non-null, with non-null elements; the coercers corresponding to
     * each parameter type */
    private Coercer[] myCoercers;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name non-null; the name of the method
     * @param isStatic whether or not this represents a static method
     * @param coercers non-null, with non-null elements; coercers
     * representing the arguments of the method
     */
    public MethodBundle (Identifier name, boolean isStatic, Coercer[] coercers)
    {
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	if (coercers == null)
	{
	    throw new NullPointerException ("coercers == null");
	}

	for (int i = 0; i < coercers.length; i++)
	{
	    if (coercers[i] == null)
	    {
		throw new NullPointerException ("coercers[" + i + "] == null");
	    }
	}

	myName = name;
	myIsStatic = isStatic;
	myCoercers = coercers;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    final public boolean equals (Object other)
    {
	return compareTo (other) == 0;
    }

    // interface's comment suffices
    final public int compareTo (Object other)
    {
	if (other == this)
	{
	    // easy out
	    return 0;
	}

	MethodBundle otherMB = (MethodBundle) other;

	// static methods are "greater than" instance methods

	if (myIsStatic)
	{
	    if (! otherMB.myIsStatic)
	    {
		return 1;
	    }
	}
	else
	{
	    if (otherMB.myIsStatic)
	    {
		return -1;
	    }
	}

	// sort by names

	int nameCmp = myName.compareTo (otherMB.myName);
	if (nameCmp != 0)
	{
	    return nameCmp;
	}

	// sort by number of arguments

	int len = myCoercers.length;
	int otherLen = otherMB.myCoercers.length;

	if (len < otherLen)
	{
	    return -1;
	}
	else if (len > otherLen)
	{
	    return 1;
	}

	// lengths are the same; pick the one that has the earliest more
	// specific argument

	for (int i = 0; i < len; i++)
	{
	    Coercer coer1 = myCoercers[i];
	    Coercer coer2 = otherMB.myCoercers[i];

	    if (coer1.isMoreSpecificThan (coer2))
	    {
		return -1;
	    }
	    else if (coer2.isMoreSpecificThan (coer1))
	    {
		return 1;
	    }
	}

	// no arguments have more specificity; go by lexographic ordering
	// of the names of the parameter types

	for (int i = 0; i < len; i++)
	{
	    String name1 = myCoercers[i].getName ();
	    String name2 = otherMB.myCoercers[i].getName ();
	    int cmp = name1.compareTo (name2);

	    if (cmp != 0)
	    {
		return cmp;
	    }
	}
	
	return 0;
    }

    /**
     * Get the name of the method.
     *
     * @return non-null; the name
     */
    final public Identifier getName ()
    {
	return myName;
    }

    /**
     * Get whether or not this is a static method.
     *
     * @return whether or not this is a static method
     */
    final public boolean isStatic ()
    {
	return myIsStatic;
    }

    /**
     * Return whether or not this instance's method is applicable to the given
     * arguments.
     *
     * @param args non-null; the arguments to check
     * @return <code>true</code> if the method is applicable to the given
     * arguments or <code>false</code> if not
     */
    final public boolean canApplyTo (Object[] args)
    {
	if (args.length != myCoercers.length)
	{
	    // easy out
	    return false;
	}

	for (int i = 0; i < myCoercers.length; i++)
	{
	    if (! myCoercers[i].canCoerce (args[i]))
	    {
		return false;
	    }
	}

	return true;
    }

    /**
     * Invoke the method on the given object with the given arguments.
     * The arguments may be modified in order to coerce them appropriately.
     *
     * @param obj null-ok; the object to invoke on
     * @param args non-null; the arguments to pass
     * @return null-ok; arbitrary result of application
     */
    public Object invoke (Object obj, Object[] args)
    {
	for (int i = 0; i < myCoercers.length; i++)
	{
	    args[i] = myCoercers[i].coerce (args[i]);
	}

	return invoke0 (obj, args);
    }



    // ------------------------------------------------------------------------
    // protected instance methods

    /**
     * Invoke the method on the given object with the given arguments.
     * The arguments will have already been coerced to appropriate
     * values.
     *
     * @param obj null-ok; the object to invoke on
     * @param args non-null; the pre-coerced arguments to pass
     * @return null-ok; arbitrary result of application
     */
    abstract protected Object invoke0 (Object obj, Object[] args);
}
