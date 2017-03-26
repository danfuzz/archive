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
import java.math.BigInteger;

/**
 * Built-in functions for the <code>:int:</code> qualifier.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class IntBuiltins
{
    /** non-null; operator qualifier for this class */
    static private final String QUAL = "int";



    // ------------------------------------------------------------------------
    // environment maker

    static private void putOp (Environment env, String name)
    {
	Object value = MethodCaller.makeStatic (IntBuiltins.class, name);
	Identifier id = VarNames.operatorFunctionName (name, QUAL);
	env.defineAlways (id, value);
    }

    static public void putIn (Environment env)
    {
	putOp (env, Names.OP_ADD);
	putOp (env, Names.OP_DIV);
	putOp (env, Names.OP_MOD);
	putOp (env, Names.OP_MUL);
	putOp (env, Names.OP_POW);
	putOp (env, Names.OP_REMAINDER);
	putOp (env, Names.OP_SUB);
	putOp (env, Names.OP_NEG);
	putOp (env, Names.OP_IDENTITY);

	putOp (env, Names.OP_AND);
	putOp (env, Names.OP_OR);
	putOp (env, Names.OP_XOR);
	putOp (env, Names.OP_INVERT);
	putOp (env, Names.OP_LSHIFT);
	putOp (env, Names.OP_RSHIFT);

	putOp (env, Names.OP_EQ);
	putOp (env, Names.OP_NE);
	putOp (env, Names.OP_GE);
	putOp (env, Names.OP_GT);
	putOp (env, Names.OP_LE);
	putOp (env, Names.OP_LT);
    }



    // ------------------------------------------------------------------------
    // comparisons

    static public boolean eq (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.compareTo (n2) == 0;
    }

    static public boolean ne (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.compareTo (n2) != 0;
    }

    static public boolean ge (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.compareTo (n2) >= 0;
    }

    static public boolean gt (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.compareTo (n2) > 0;
    }

    static public boolean le (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.compareTo (n2) <= 0;
    }

    static public boolean lt (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.compareTo (n2) < 0;
    }



    // ------------------------------------------------------------------------
    // bitwise operations

    static public BigInteger invert (Object o)
    {
	BigInteger n = identity (o);
	return n.not ();
    }

    static public BigInteger and (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.and (n2);
    }

    static public BigInteger or (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.or (n2);
    }

    static public BigInteger xor (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.xor (n2);
    }

    static public BigInteger lshift (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);

	// xxx: stupid test; why doesn't BigInteger have shiftLeft(BigInteger)
	// anyway?
	int int2 = n2.intValue ();
	BigInteger n3 = BigInteger.valueOf (int2);
	if (! n3.equals (n2))
	{
	    throw new RuntimeException ("big integer overflow");
	}

	return n1.shiftLeft (int2);
    }

    static public BigInteger rshift (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);

	// xxx: stupid test; why doesn't BigInteger have shiftRight(BigInteger)
	// anyway?
	int int2 = n2.intValue ();
	BigInteger n3 = BigInteger.valueOf (int2);
	if (! n3.equals (n2))
	{
	    throw new RuntimeException ("big integer overflow");
	}

	return n1.shiftRight (int2);
    }



    // ------------------------------------------------------------------------
    // arithmetic operations

    static public BigInteger add (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.add (n2); 
    }

    static public BigInteger div (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.divide (n2);
    }

    static public BigInteger mod (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.mod (n2);
    }

    static public BigInteger mul (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.multiply (n2);
    }

    static public BigInteger neg (Object o)
    {
	BigInteger n = identity (o);
	return n.negate ();
    }

    static public BigInteger pow (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);

	// xxx: stupid test; why doesn't BigInteger have pow(BigInteger)
	// anyway?
	int int2 = n2.intValue ();
	BigInteger n3 = BigInteger.valueOf (int2);
	if (! n3.equals (n2))
	{
	    throw new RuntimeException ("big integer overflow");
	}

	return n1.pow (int2);
    }

    static public BigInteger remainder (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.remainder (n2);
    }

    static public BigInteger sub (Object o1, Object o2)
    {
	BigInteger n1 = identity (o1);
	BigInteger n2 = identity (o2);
	return n1.subtract (n2);
    }



    // ------------------------------------------------------------------------
    // identity

    static public BigInteger identity (Object n)
    {
	if (n == null)
	{
	    return BigInteger.ZERO;
	}
	else if (n instanceof BigInteger)
	{
	    return (BigInteger) n;
	}
	else if (n instanceof Number)
	{
	    return BigInteger.valueOf (((Number) n).longValue ());
	}
	else if (n instanceof Boolean)
	{
	    return ((Boolean) n).booleanValue () ? 
		BigInteger.ONE : BigInteger.ZERO;
	}
	
	String s = n.toString ().trim ();
	if (s.startsWith ("+"))
	{
	    s = s.substring (1);
	}

	return new BigInteger (s);
    }
}
