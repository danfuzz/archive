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

/**
 * Coercer class that coerces to a given arbitrary class. The
 * implementation in this class will only ever pass objects unaltered,
 * though {@link #canCoerce} and {@link #isMoreSpecificThan} behave
 * sensibly. Subclasses can (and do) override this behavior, though.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class GenericCoercer
    extends Coercer
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param targetClass non-null; the class that this instance converts
     * to
     */
    public GenericCoercer (Class targetClass)
    {
	super (targetClass);

	if (targetClass.isPrimitive ())
	{
	    throw new IllegalArgumentException ("targetClass.isPrimitive ()");
	}
    }


    
    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    final public boolean isMoreSpecificThan (Coercer other)
    {
	Class thisClass = getTargetClass ();
	Class otherClass = other.getTargetClass ();

	if (thisClass == otherClass)
	{
	    return false;
	}

	if (thisClass.isAssignableFrom (otherClass))
	{
	    // other class is a subtype of this class, hence this is
	    // less specific
	    return false;
	}

	if (otherClass.isAssignableFrom (thisClass))
	{
	    // this class is a subtype of other class, hence this is
	    // more specific
	    return true;
	}

	// neither is more specific than the other
	return false;
    }

    // superclass's comment suffices
    public boolean canCoerce (Object obj)
    {
	if (obj == null)
	{
	    return true;
	}

	return (getTargetClass ().isInstance (obj));
    }

    // superclass's comment suffices
    public Object coerce (Object obj)
    {
	return obj;
    }
}
