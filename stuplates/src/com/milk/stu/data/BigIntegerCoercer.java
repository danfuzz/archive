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

import java.math.BigInteger;

/**
 * Coercer class that coerces to <code>BigInteger</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class BigIntegerCoercer
    extends GenericCoercer
{
    /** non-null; unique instance of this class */
    static public final BigIntegerCoercer theOne = new BigIntegerCoercer ();



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is private; use {@link #theOne}.
     */
    private BigIntegerCoercer ()
    {
	super (BigInteger.class);
    }


    
    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public boolean canCoerce (Object obj)
    {
	return (obj == null) ||
	    (obj instanceof BigInteger) ||
	    (obj instanceof Integer) ||
	    (obj instanceof Long) ||
	    (obj instanceof Byte) ||
	    (obj instanceof Short);
    }

    // superclass's comment suffices
    public Object coerce (Object obj)
    {
	if ((obj == null) || (obj instanceof BigInteger))
	{
	    return obj;
	}

	return BigInteger.valueOf (((Number) obj).longValue ());
    }
}
