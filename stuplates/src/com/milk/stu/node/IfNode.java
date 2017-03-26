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

package com.milk.stu.node;

import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;
import com.milk.stu.util.Conversions;

/**
 * Node representing a standard "if" conditional.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class IfNode
    extends BaseNode
{
    /** non-null; the test */
    private final StuNode myTest;

    /** non-null; node to evaluate in case the test is true */
    private final StuNode myThenPart;

    /** non-null; node to evaluate in case the test is false */
    private final StuNode myElsePart;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param test non-null; the test
     * @param thenPart non-null; node to evaluate in case the test is true
     * @param elsePart non-null; node to evaluate in case the test is false
     */
    public IfNode (StuNode test, StuNode thenPart, StuNode elsePart)
    {
	if (test == null)
	{
	    throw new NullPointerException ("test == null");
	}

	if (thenPart == null)
	{
	    throw new NullPointerException ("thenPart == null");
	}

	if (elsePart == null)
	{
	    throw new NullPointerException ("elsePart == null");
	}

	myTest = test;
	myThenPart = thenPart;
	myElsePart = elsePart;
    }

    /**
     * Construct an instance with an empty <code>else</code> part.
     *
     * @param test non-null; the test
     * @param thenPart non-null; node to evaluate in case the test is true
     */
    public IfNode (StuNode test, StuNode thenPart)
    {
	this (test, thenPart, LiteralNode.NULL);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	return "(if (" + myTest + ") " + myThenPart + 
	    " else " + myElsePart + ")";
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	// make a child environment for evaluation (so new variable
	// definitions don't leak out)
	environment = environment.makeChild ();

	Object test = myTest.eval (environment);
	boolean b = Conversions.booleanValue (test);

	if (b)
	{
	    return myThenPart.eval (environment);
	}
	else
	{
	    return myElsePart.eval (environment);
	}
    }
}
