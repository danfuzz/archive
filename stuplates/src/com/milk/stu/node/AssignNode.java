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

/**
 * Node representing slot assignment.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class AssignNode
    extends BaseNode
{
    /** non-null; the location to assign to */
    private StuNode myLocation;

    /** non-null; the value to assign */
    private StuNode myValue;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param location non-null; the location to assign to
     * @param value non-null; the value to assign
     */
    public AssignNode (StuNode location, StuNode value)
    {
	if (location == null)
	{
	    throw new NullPointerException ("location == null");
	}

	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	myLocation = location;
	myValue = value;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	return "(" + myLocation + " := " + myValue + ")";
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	Slot slot = myLocation.evalSlot (environment);
	Object value = myValue.eval (environment);
	slot.setValue (value);
	return value;
    }
}
