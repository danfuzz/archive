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
 * Base class for nodes.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
abstract public class BaseNode
    implements StuNode
{
    // ------------------------------------------------------------------------
    // constructor

    // implicit public no-arg constructor



    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    final public CharSequence evalToChars (Environment environment)
    {
	Object result = eval (environment);

	if (result == null)
	{
	    return "";
	}

	if (result instanceof CharSequence)
	{
	    return (CharSequence) result;
	}

	return result.toString ();
    }

    // interface's comment suffices
    final public CharSequence evalOutsideToChars (Environment environment)
    {
	Object result = evalOutside (environment);

	if (result == null)
	{
	    return "";
	}
	
	if (result instanceof CharSequence)
	{
	    return (CharSequence) result;
	}

	return result.toString ();
    }

    /**
     * This default implementation just calls {@link #eval}.
     */
    public Object evalOutside (Environment environment)
    {
	return eval (environment);
    }

    /**
     * This default implementation just throws an exception to indicate
     * that this instance cannot be taken to be a slot.
     */
    public Slot evalSlot (Environment environment)
    {
	throw new RuntimeException ("not a slot");
    }

    /**
     * This default implementation just calls {@link #evalSlot}.
     */
    public Slot evalOutsideSlot (Environment environment)
    {
	return evalSlot (environment);
    }
}
