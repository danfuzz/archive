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
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;

/**
 * Node representing a local variable definition.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class DefineNode
    extends BaseNode
{
    /** non-null; the name of the variable */
    private final Identifier myName;

    /** non-null; the value to assign to it */
    private final StuNode myValue;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name non-null; the name of the variable
     * @param value non-null; the value to assign to it
     */
    public DefineNode (Identifier name, StuNode value)
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

    // superclass's comment suffices
    public String toString ()
    {
	return "(def " + myName + " := " + myValue + ")";
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	// create the slot before evaluating, so that the value can
	// self-reference if it wants to
	Slot slot = environment.define (myName);

	Object value = myValue.eval (environment);
	slot.setValue (value);
	return value;
    }
}
