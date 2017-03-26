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

package com.milk.stu.builtins;

import com.milk.stu.data.ListSlot;
import com.milk.stu.data.MapSlot;
import com.milk.stu.data.MethodCaller;
import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;
import com.milk.stu.util.CharSequenceList;
import com.milk.stu.util.VarNames;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Built-in functions for the <code>:default:</code> qualifier.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class DefaultBuiltins
{
    // ------------------------------------------------------------------------
    // environment maker

    static private void putOp (Environment env, String name, Object value)
    {
	Identifier id = VarNames.operatorFunctionName (name, null);
	env.defineAlways (id, value);
    }

    static private void putOp (Environment env, String name)
    {
	Object value = MethodCaller.makeStatic (DefaultBuiltins.class, name);
	putOp (env, name, value);
    }

    static private void putCopy (Environment env, String name, String qual)
    {
	Identifier fromName = VarNames.operatorFunctionName (name, qual);
	Object value = env.getValue (fromName);
	putOp (env, name, value);
    }

    static public void putIn (Environment env)
    {
	// directly defined in this class
	putOp (env, Names.OP_EQ);
	putOp (env, Names.OP_NE);
	putOp (env, Names.OP_GE);
	putOp (env, Names.OP_GT);
	putOp (env, Names.OP_LE);
	putOp (env, Names.OP_LT);

	putOp (env, Names.OP_GET);

	putOp (env, Names.OP_ADD);
	putOp (env, Names.OP_DIV);
	putOp (env, Names.OP_MOD);
	putOp (env, Names.OP_MUL);
	putOp (env, Names.OP_NEG);
	putOp (env, Names.OP_POW);
	putOp (env, Names.OP_REMAINDER);
	putOp (env, Names.OP_SUB);

	putOp (env, Names.OP_AND);
	putOp (env, Names.OP_OR);
	putOp (env, Names.OP_XOR);
	putOp (env, Names.OP_INVERT);

	putOp (env, Names.OP_IDENTITY);

	// stolen from :boolean:
	putCopy (env, Names.OP_NOT, "boolean");

	// stolen from :int:
	putCopy (env, Names.OP_LSHIFT, "int");
	putCopy (env, Names.OP_RSHIFT, "int");
    }



    // ------------------------------------------------------------------------
    // references

    static public Object get (List l, int index)
    {
	return new ListSlot (l, index);
    }

    static public Object get (Map m, Object key)
    {
	return new MapSlot (m, key);
    }



    // ------------------------------------------------------------------------
    // arithmetic / string operations

    static public Object add (Object o1, Object o2)
    {
	BigInteger bi1 = widenToBigInteger (o1);
	if (bi1 != null)
	{
	    BigInteger bi2 = widenToBigInteger (o2);
	    if (bi2 != null)
	    {
		return bi1.add (bi2);
	    }
	}

	Double d1 = widenToDouble (o1);
	if (d1 != null)
	{
	    Double d2 = widenToDouble (o2);
	    if (d2 != null)
	    {
		return new Double (d1.doubleValue () + d2.doubleValue ());
	    }
	}

	if (o1 == null)
	{
	    o1 = "null";
	}

	if (o2 == null)
	{
	    o2 = "null";
	}

	CharSequenceList result = new CharSequenceList (2);
	result.appendShowingNull (o1);
	result.appendShowingNull (o2);
	result.makeImmutable ();
	return result;
    }

    static public Object div (Object o1, Object o2)
    {
	Object arr = convertToCommonNumbers (o1, o2);

	if (arr instanceof double[])
	{
	    double[] darr = (double[]) arr;
	    return new Double (darr[0] / darr[1]);
	}

	BigInteger[] biarr = (BigInteger[]) arr;
	return biarr[0].divide (biarr[1]);
    }

    static public Object mod (Object o1, Object o2)
    {
	Object arr = convertToCommonNumbers (o1, o2);

	if (arr instanceof double[])
	{
	    double[] darr = (double[]) arr;
	    return new Double (DoubleBuiltins.mod (new Double (darr[0]), 
						   new Double (darr[1])));
	}

	BigInteger[] biarr = (BigInteger[]) arr;
	return biarr[0].mod (biarr[1]);
    }

    static public Object mul (Object o1, Object o2)
    {
	Object arr = convertToCommonNumbers (o1, o2);

	if (arr instanceof double[])
	{
	    double[] darr = (double[]) arr;
	    return new Double (darr[0] * darr[1]);
	}

	BigInteger[] biarr = (BigInteger[]) arr;
	return biarr[0].multiply (biarr[1]);
    }

    static public Object neg (Object o)
    {
	BigInteger bi = widenToBigInteger (o); 
	if (bi != null)
	{
	    return bi.negate ();
	}

	Double d = widenToDouble (o);
	if (d != null)
	{
	    return new Double (-d.doubleValue ());
	}

	throw new ClassCastException ("not a number: " + o);
    }

    static public Object pow (Object o1, Object o2)
    {
	Object arr = convertToCommonNumbers (o1, o2);

	if (arr instanceof double[])
	{
	    double[] darr = (double[]) arr;
	    return new Double (Math.pow (darr[0], darr[1]));
	}

	BigInteger[] biarr = (BigInteger[]) arr;
	return IntBuiltins.pow (biarr[0], biarr[1]);
    }

    static public Object remainder (Object o1, Object o2)
    {
	Object arr = convertToCommonNumbers (o1, o2);

	if (arr instanceof double[])
	{
	    double[] darr = (double[]) arr;
	    return new Double (darr[0] % darr[1]);
	}

	BigInteger[] biarr = (BigInteger[]) arr;
	return biarr[0].remainder (biarr[1]);
    }

    static public Object sub (Object o1, Object o2)
    {
	Object arr = convertToCommonNumbers (o1, o2);

	if (arr instanceof double[])
	{
	    double[] darr = (double[]) arr;
	    return new Double (darr[0] - darr[1]);
	}

	BigInteger[] biarr = (BigInteger[]) arr;
	return biarr[0].subtract (biarr[1]);
    }



    // ------------------------------------------------------------------------
    // bitwise / boolean operations

    static public Object and (Object o1, Object o2)
    {
	if ((o1 instanceof Number) && (o2 instanceof Number))
	{
	    return IntBuiltins.and (o1, o2);
	}
	
	return Boolean.valueOf (BooleanBuiltins.and (o1, o2));
    }

    static public Object or (Object o1, Object o2)
    {
	if ((o1 instanceof Number) && (o2 instanceof Number))
	{
	    return IntBuiltins.or (o1, o2);
	}
	
	return Boolean.valueOf (BooleanBuiltins.or (o1, o2));
    }

    static public Object xor (Object o1, Object o2)
    {
	if ((o1 instanceof Number) && (o2 instanceof Number))
	{
	    return IntBuiltins.xor (o1, o2);
	}
	
	return Boolean.valueOf (BooleanBuiltins.xor (o1, o2));
    }

    static public Object invert (Object o)
    {
	if (o instanceof Number)
	{
	    return IntBuiltins.invert (o);
	}
	
	return Boolean.valueOf (BooleanBuiltins.invert (o));
    }



    // ------------------------------------------------------------------------
    // object comparisons

    static public boolean eq (Object o1, Object o2)
    {
	String raw = compare (o1, o2);

	return (raw == Names.OP_EQ);
    }

    static public boolean ne (Object o1, Object o2)
    {
	String raw = compare (o1, o2);

	return (raw != Names.OP_EQ);
    }

    static public boolean lt (Object o1, Object o2)
    {
	return compare (o1, o2) == Names.OP_LT;
    }

    static public boolean le (Object o1, Object o2)
    {
	String raw = compare (o1, o2);

	return (raw == Names.OP_LT) || (raw == Names.OP_EQ);
    }

    static public boolean gt (Object o1, Object o2)
    {
	return compare (o1, o2) == Names.OP_GT;
    }

    static public boolean ge (Object o1, Object o2)
    {
	String raw = compare (o1, o2);

	return (raw == Names.OP_GT) || (raw == Names.OP_EQ);
    }



    // ------------------------------------------------------------------------
    // identity

    static public Object identity (Object o)
    {
	return o;
    }



    // ------------------------------------------------------------------------
    // static private methods

    static private Object convertToCommonNumbers (Object o1, Object o2)
    {
	BigInteger bi1 = widenToBigInteger (o1);
	if (bi1 != null)
	{
	    BigInteger bi2 = widenToBigInteger (o2);
	    if (bi2 != null)
	    {
		return new BigInteger[] { bi1, bi2 };
	    }
	}

	Double d1 = widenToDouble (o1);
	if (d1 != null)
	{
	    Double d2 = widenToDouble (o2);
	    if (d2 != null)
	    {
		return new double[] { d1.doubleValue (), d2.doubleValue() };
	    }
	}

	throw new ClassCastException ("not both numbers: " + o1 + 
				      " and " + o2);
    }

    static private String compare (Object o1, Object o2)
    {
	if (o1 == o2)
	{
	    return Names.OP_EQ;
	}

	if ((o1 == null) || (o2 == null))
	{
	    return Names.OP_NE;
	}

	try
	{
	    BigInteger bi1 = widenToBigInteger (o1);
	    if (bi1 != null)
	    {
		BigInteger bi2 = widenToBigInteger (o2);
		if (bi2 != null)
		{
		    return intToCompareResult (bi1.compareTo (bi2));
		}
	    }
	}
	catch (RuntimeException ex)
	{
	    // fall through, as per spec
	    // XXX--should probably log the error, though
	}

	try
	{
	    Double d1 = widenToDouble (o1);
	    if (d1 != null)
	    {
		Double d2 = widenToDouble (o2);
		if (d2 != null)
		{
		    return intToCompareResult (d1.compareTo (d2));
		}
	    }
	}
	catch (RuntimeException ex)
	{
	    // fall through, as per spec
	    // XXX--should probably log the error, though
	}

	compareTos:
	try
	{
	    int comp;

	    if (o1 instanceof String)
	    {
		comp = ((String) o1).compareTo (o2.toString ());
	    }
	    else if (o2 instanceof String)
	    {
		comp = o1.toString ().compareTo (o2);
	    }
	    else if (o1 instanceof Comparable)
	    {
		comp = ((Comparable) o1).compareTo (o2);
	    }
	    else
	    {
		break compareTos;
	    }

	    return intToCompareResult (comp);
	}
	catch (RuntimeException ex)
	{
	    // fall through, as per spec
	    // XXX--should probably log the error, though
	}

	try
	{
	    return o1.equals (o2) ? Names.OP_EQ : Names.OP_NE;
	}
	catch (RuntimeException ex)
	{
	    // return "ne", as per spec
	    return Names.OP_NE;
	    // XXX--should probably log the error, though
	}
    }

    static private String intToCompareResult (int i)
    {
	if (i < 0)
	{
	    return Names.OP_LT;
	}
	else if (i > 0)
	{
	    return Names.OP_GT;
	}

	return Names.OP_EQ;
    }

    static private BigInteger widenToBigInteger (Object o)
    {
	if (o instanceof BigInteger)
	{
	    return (BigInteger) o;
	}
	else if ((o instanceof Byte) ||
		 (o instanceof Short) ||
		 (o instanceof Integer) ||
		 (o instanceof Long))
	{
	    return BigInteger.valueOf (((Number) o).longValue ());
	}

	return null;
    }

    static private Double widenToDouble (Object o)
    {
	if (o instanceof Double)
	{
	    return (Double) o;
	}
	else if (o instanceof Number)
	{
	    return new Double (((Number) o).doubleValue ());
	}

	return null;
    }
}
