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
import java.lang.reflect.Field;

/**
 * A slot that defers to a particular field of a particular object or class.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FieldSlot
    implements Slot
{
    /** null-ok; the target object to get from, or <code>null</code> for a
     * static field */
    private Object myTarget;

    /** non-null; the field object to use */
    private Field myField;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param target null-ok; the target object to get from, or
     * <code>null</code> for a static field
     * @param field non-null; the field object to use
     */
    public FieldSlot (Object target, Field field)
    {
	if (field == null)
	{
	    throw new NullPointerException ("field == null");
	}

	myTarget = target;
	myField = field;
    }


    
    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    public Object getValue ()
    {
	try
	{
	    return myField.get (myTarget);
	}
	catch (IllegalAccessException ex)
	{
	    throw new RuntimeException (ex);
	}
    }

    // interface's comment suffices
    public void setValue (Object value)
    {
	try
	{
	    myField.set (myTarget, value);
	}
	catch (IllegalAccessException ex)
	{
	    throw new RuntimeException (ex);
	}
    }
}
