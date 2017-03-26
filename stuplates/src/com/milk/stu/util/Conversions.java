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

package com.milk.stu.util;

/**
 * Utility methods for converting between standard types.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class Conversions
{
    // ------------------------------------------------------------------------
    // constructor

    /**
     * This class is uninstantiable.
     */
    private Conversions ()
    {
	// this space intentionally left blank
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Convert the given value to a <code>boolean</code>.
     * <code>Boolean</code> objects convert in the obvious way.
     * <code>null</code> converts to <code>false</code>. Zero numeric
     * values convert to <code>false</code>. Empty strings convert to
     * <code>false</code>. All other values convert to <code>true</code>.
     *
     * @param o null-ok; the value to convert
     * @return the <code>boolean</code> equivalent 
     */
    static public boolean booleanValue (Object o)
    {
	if (o == null)
	{
	    return false;
	}

	if (o instanceof Boolean)
	{
	    return ((Boolean) o).booleanValue ();
	}

	if (o instanceof String)
	{
	    return ((String) o).length () != 0;
	}

	if (o instanceof Number)
	{
	    return ((Number) o).doubleValue () != 0.0;
	}

	return true;
    }

}
