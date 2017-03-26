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
 * Coercer class that coerces to <code>Short.TYPE</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class ShortCoercer
    extends Coercer
{
    /** non-null; unique instance of this class */
    static public final ShortCoercer theOne = new ShortCoercer ();



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is private; use {@link #theOne}.
     */
    private ShortCoercer ()
    {
	super (Short.TYPE);
    }


    
    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public boolean isMoreSpecificThan (Coercer other)
    {
	if ((this == other) || (other == ByteCoercer.theOne))
	{
	    return false;
	}

	// short is more specific than anything but byte, in its domain
	return true;
    }

    // superclass's comment suffices
    public boolean canCoerce (Object obj)
    {
	if (obj instanceof Short)
	{
	    return true;
	}

	if (obj instanceof BigInteger)
	{
	    BigInteger bint = (BigInteger) obj;
	    return (bint.bitLength () <= 15);
	}

	return false;
    }

    // superclass's comment suffices
    public Object coerce (Object obj)
    {
	if (obj instanceof Short)
	{
	    return obj;
	}

	return new Short (((BigInteger) obj).shortValue ());
    }
}
