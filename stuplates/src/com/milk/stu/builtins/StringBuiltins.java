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
import com.milk.stu.util.CharSequenceList;
import com.milk.stu.util.VarNames;

/**
 * Built-in functions for the <code>:string:</code> qualifier.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class StringBuiltins
{
    /** non-null; operator qualifier for this class */
    static private final String QUAL = "string";



    // ------------------------------------------------------------------------
    // environment maker

    static private void putOp (Environment env, String name)
    {
	Object value = MethodCaller.makeStatic (StringBuiltins.class, name);
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
	putOp (env, Names.OP_IDENTITY);
    }



    // ------------------------------------------------------------------------
    // comparisons

    static public boolean eq (Object o1, Object o2)
    {
	return identity (o1).equals (identity (o2));
    }

    static public boolean ne (Object o1, Object o2)
    {
	return ! identity (o1).equals (identity (o2));
    }

    static public boolean lt (Object o1, Object o2)
    {
	return identity (o1).compareTo (identity (o2)) < 0;
    }

    static public boolean le (Object o1, Object o2)
    {
	return identity (o1).compareTo (identity (o2)) <= 0;
    }

    static public boolean gt (Object o1, Object o2)
    {
	return identity (o1).compareTo (identity (o2)) > 0;
    }

    static public boolean ge (Object o1, Object o2)
    {
	return identity (o1).compareTo (identity (o2)) >= 0;
    }



    // ------------------------------------------------------------------------
    // manipulation

    static public CharSequenceList add (Object o1, Object o2)
    {
	CharSequenceList result = new CharSequenceList (2);

	result.appendShowingNull (o1);
	result.appendShowingNull (o2);
	result.makeImmutable ();

	return result;
    }



    // ------------------------------------------------------------------------
    // identity

    static public String identity (Object o)
    {
	if (o == null)
	{
	    return "null";
	}

	return o.toString ();
    }
}
