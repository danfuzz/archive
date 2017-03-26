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

import com.milk.stu.data.MethodCaller;
import com.milk.stu.iface.Environment;
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;
import com.milk.stu.util.VarNames;

/**
 * Built-in functions for the <code>:double:</code> qualifier.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class DoubleBuiltins
{
    /** non-null; operator qualifier for this class */
    static private final String QUAL = "double";



    // ------------------------------------------------------------------------
    // environment maker

    static private void putOp (Environment env, String name)
    {
	Object value = MethodCaller.makeStatic (DoubleBuiltins.class, name);
	Identifier id = VarNames.operatorFunctionName (name, QUAL);
	env.defineAlways (id, value);
    }

    static public void putIn (Environment env)
    {
	putOp (env, Names.OP_EQ);
	putOp (env, Names.OP_NE);
	putOp (env, Names.OP_GE);
	putOp (env, Names.OP_GT);
	putOp (env, Names.OP_LE);
	putOp (env, Names.OP_LT);

	putOp (env, Names.OP_ADD);
	putOp (env, Names.OP_DIV);
	putOp (env, Names.OP_MOD);
	putOp (env, Names.OP_MUL);
	putOp (env, Names.OP_POW);
	putOp (env, Names.OP_REMAINDER);
	putOp (env, Names.OP_SUB);
	putOp (env, Names.OP_NEG);
	putOp (env, Names.OP_IDENTITY);
    }



    // ------------------------------------------------------------------------
    // comparisons

    static public boolean eq (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 == d2;
    }

    static public boolean ne (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 != d2;
    }

    static public boolean ge (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 >= d2;
    }

    static public boolean gt (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 > d2;
    }

    static public boolean le (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 <= d2;
    }

    static public boolean lt (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 < d2;
    }



    // ------------------------------------------------------------------------
    // arithmetic operations

    static public double neg (Object o)
    {
	return -identity (o);
    }

    static public double add (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 + d2;
    }

    static public double sub (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 - d2;
    }

    static public double mul (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 * d2;
    }

    static public double div (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 / d2;
    }

    static public double remainder (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);
	return d1 % d2;
    }

    static public double mod (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);

	if (d2 < 0)
	{
	    return Double.NaN;
	}

	double r = d1 % d2;

	if (r < 0)
	{
	    r += Math.abs (d2);
	}

	return r;
    }

    static public double pow (Object o1, Object o2)
    {
	double d1 = identity (o1);
	double d2 = identity (o2);

	return Math.pow (d1, d2);
    }



    // ------------------------------------------------------------------------
    // identity

    static public double identity (Object n)
    {
	if (n == null)
	{
	    return 0.0;
	}
	else if (n instanceof Number)
	{
	    return ((Number) n).doubleValue ();
	}
	else if (n instanceof Boolean)
	{
	    return ((Boolean) n).booleanValue () ? 1.0 : 0.0;
	}

	String s = n.toString ().trim ();
	if (s.startsWith ("+"))
	{
	    s = s.substring (1);
	}

	if (s.equalsIgnoreCase ("nan"))
	{
	    return Double.NaN;
	}
	else if (s.equalsIgnoreCase ("infinity"))
	{
	    return Double.POSITIVE_INFINITY;
	}
	if (s.equalsIgnoreCase ("-infinity"))
	{
	    return Double.NEGATIVE_INFINITY;
	}

	return Double.parseDouble (s);
    }
}
