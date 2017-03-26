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

import com.milk.stu.data.FieldSlot;
import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;
import java.lang.reflect.Field;

/**
 * Node representing an object member reference. This includes object
 * methods, which are represented as functions.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class RefNode
    extends BaseNode
{
    /** non-null; the object */
    private final StuNode myObject;

    /** non-null; the name of the member */
    private final Identifier myName;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param object non-null; the object
     * @param name non-null; the name of the slot
     */
    public RefNode (StuNode object, String name)
    {
	if (object == null)
	{
	    throw new NullPointerException ("object == null");
	}

	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	myObject = object;
	myName = Identifier.intern (name);
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	return myObject + "." + myName;
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	Slot s = evalSlot (environment);
	return s.getValue ();
    }

    // interface's comment suffices
    public Slot evalSlot (Environment environment)
    {
	Object obj = myObject.eval (environment);
	Class cls;

	if (obj instanceof Class)
	{
	    cls = (Class) obj;
	    obj = null;
	}
	else
	{
	    cls = obj.getClass ();
	}

	try
	{
	    Field f = cls.getField (myName.getName ());
	    return new FieldSlot (obj, f);
	}
	catch (NoSuchFieldException ex)
	{
	    throw new RuntimeException (ex);
	}
    }
}
