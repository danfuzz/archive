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

import com.milk.stu.iface.Function;
import com.milk.stu.iface.Identifier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A {@link MethodBundle} that refers to a {@link Function} object.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FunctionMethodBundle
    extends MethodBundle
{
    /** non-null; the function in question */
    private Function myFunction;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name non-null; the name of the method
     * @param isStatic whether or not this represents a static method
     * @param coercers non-null, with non-null elements; coercers
     * representing the arguments of the method
     * @param function non-null; the function to refer to
     */
    public FunctionMethodBundle (Identifier name, boolean isStatic,
				 Coercer[] coercers, Function function)
    {
	super (name, isStatic, coercers);

	if (function == null)
	{
	    throw new NullPointerException ("function == null");
	}

	myFunction = function;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ("<method ");
	sb.append (myFunction);
	sb.append (">");
	
	return sb.toString ();
    }



    // ------------------------------------------------------------------------
    // protected instance methods

    // superclass's comment suffices
    protected Object invoke0 (Object obj, Object[] args)
    {
	if (isStatic ())
	{
	    return myFunction.apply (args);
	}
	else
	{
	    Object[] args1 = new Object[args.length + 1];
	    args1[0] = obj;
	    System.arraycopy (args, 0, args1, 1, args.length);
	    return myFunction.apply (args1);
	}
    }
}
