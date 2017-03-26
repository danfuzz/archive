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
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;

/**
 * Node representing an arbitrary literal value.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class LiteralNode
    extends BaseNode
{
    /** non-null; map of interned literals */
    static private final HashMap theInterns = new HashMap ();

    /** non-null; standard instance with value <code>Infinity</code> */
    static public final LiteralNode INFINITY = 
	new LiteralNode (new Double (Double.POSITIVE_INFINITY));

    /** non-null; standard instance with value <code>NaN</code> */
    static public final LiteralNode NAN = 
	new LiteralNode (new Double (Double.NaN));

    /** non-null; standard instance with value <code>null</code> */
    static public final LiteralNode NULL = new LiteralNode (null);

    /** non-null; standard instance with value <code>Boolean.TRUE</code> */
    static public final LiteralNode TRUE = new LiteralNode (Boolean.TRUE);

    /** non-null; standard instance with value <code>Boolean.FALSE</code> */
    static public final LiteralNode FALSE = new LiteralNode (Boolean.FALSE);

    /** null-ok; wrapped value */
    private final Object myValue;



    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Return an instance wrapping a string with the given value.
     *
     * @param value non-null; the value to represent
     * @return non-null; an instance that wraps the value
     */
    static public LiteralNode stringLit (String value)
    {
	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	return intern (value.intern ());
    }

    /**
     * Return an instance wrapping a float whose value is
     * representable as the given string.
     *
     * @param value non-null; the unparsed value
     * @return non-null; an instance that wraps the value
     */
    static public LiteralNode floatLit (String value)
    {
	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	return intern (new Double (value));
    }

    /**
     * Return an instance wrapping an integer whose value is
     * representable as the given string.
     *
     * @param value non-null; the unparsed value
     * @return non-null; an instance that wraps the value
     */
    static public LiteralNode intLit (String value)
    {
	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	BigInteger bi;

	if (value.startsWith ("0"))
	{
	    if (value.startsWith ("0x"))
	    {
		bi = new BigInteger (value.substring (2), 16);
	    }
	    else
	    {
		bi = new BigInteger (value, 8);
	    }
	}
	else
	{
	    bi = new BigInteger (value);
	}

	return intern (bi);
    }

    /**
     * Return an instance wrapping a URI whose value is
     * representable as the given string.
     *
     * @param value non-null; the parsed URI value
     * @return non-null; an instance that wraps the value
     */
    static public LiteralNode uriLit (URI value)
    {
	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	return intern (value);
    }



    // ------------------------------------------------------------------------
    // private static methods

    /** 
     * Intern the given value as a literal.
     *
     * @param value non-null; the value to intern
     * @return non-null; the interned literal
     */
    static private LiteralNode intern (Object value)
    {
	LiteralNode ln = (LiteralNode) theInterns.get (value);

	if (ln == null)
	{
	    ln = new LiteralNode (value);
	    theInterns.put (value, ln);
	}

	return ln;
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is a private method; the right way to
     * construct instances is to use one of the provided public static
     * methods.
     *
     * @param value null-ok; the value to wrap
     */
    private LiteralNode (Object value)
    {
	myValue = value;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	if (myValue == null)
	{
	    return "null";
	}

	if (myValue instanceof String)
	{
	    return "\"" + myValue + "\"";
	}

	return myValue.toString ();
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	return myValue;
    }
}
