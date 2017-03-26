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

import com.milk.stu.iface.Slot;
import java.util.List;

/**
 * A slot that refers to a particular list element by index.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class ListSlot
    implements Slot
{
    /** non-null; the target list */
    private List myTarget;

    /** the index into the target */
    private int myIndex;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param target non-null; the target list
     * @param index the index into the target
     */
    public ListSlot (List target, int index)
    {
	if (target == null)
	{
	    throw new NullPointerException ("target == null");
	}

	myTarget = target;
	myIndex = index;
    }


    
    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    public Object getValue ()
    {
	return myTarget.get (myIndex);
    }

    // interface's comment suffices
    public void setValue (Object value)
    {
	myTarget.set (myIndex, value);
    }
}
