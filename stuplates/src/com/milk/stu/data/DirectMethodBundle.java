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

import com.milk.stu.iface.Identifier;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A {@link MethodBundle} that directly refers to an underlying Java
 * method object.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class DirectMethodBundle
    extends MethodBundle
{
    /** non-null; the method in question */
    private Method myMethod;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param method non-null; the method in question
     */
    public DirectMethodBundle (Method method)
    {
	super (Identifier.intern (method.getName ()),
	       Modifier.isStatic (method.getModifiers ()),
	       coercersFor (method));

	myMethod = method;
    }

    /**
     * Get the coercers for the given method's arguments.
     *
     * @param method non-null; the method in question
     * @return non-null; the coercers for the method's arguments
     */
    static private Coercer[] coercersFor (Method method)
    {
	if (method == null)
	{
	    throw new NullPointerException ("method == null");
	}

	Class[] ptypes = method.getParameterTypes ();
	Coercer[] result = new Coercer[ptypes.length];

	for (int i = 0; i < ptypes.length; i++)
	{
	    result[i] = Coercer.get (ptypes[i]);
	}

	return result;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ("<method ");
	sb.append (myMethod.getDeclaringClass ().getName ());
	sb.append ('.');
	sb.append (getName ());
	sb.append (">");
	
	return sb.toString ();
    }



    // ------------------------------------------------------------------------
    // protected instance methods

    // superclass's comment suffices
    protected Object invoke0 (Object obj, Object[] args)
    {
	try
	{
	    return myMethod.invoke (obj, args);
	}
	catch (IllegalAccessException ex)
	{
	    throw new RuntimeException (ex);
	}
	catch (InvocationTargetException ex)
	{
	    Throwable t = ex.getTargetException ();
	    if (t instanceof RuntimeException)
	    {
		throw (RuntimeException) t;
	    }
	    throw new RuntimeException (t);
	}
    }
}
