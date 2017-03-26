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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Function which knows how to call a method defined to take an
 * <code>Object[]</code> argument, doing the call by wrapping up all the
 * arguments it gets into the one array.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class VarArgsMethodCaller
    implements Function
{
    /** non-null; argument types for methods that can be used by this class */
    static private final Class[] ARGUMENT_TYPES = 
	new Class[] { Object[].class };

    /** null-ok; the target object */
    private Object myTarget;

    /** non-null; the method to call */
    private Method myMethod;



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Construct an instance that is applicable to the named static method
     * of the given class.
     *
     * @param clazz non-null; the class to be applicable to
     * @param name non-null; the name of the method
     * @return non-null; an instance which is applicable to all the named
     * static method of the given class
     */
    static public VarArgsMethodCaller makeStatic (Class clazz, String name)
    {
	if (clazz == null)
	{
	    throw new NullPointerException ("clazz == null");
	}
	
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	Method meth;

	try
	{
	    meth = clazz.getMethod (name, ARGUMENT_TYPES);
	}
	catch (NoSuchMethodException ex)
	{
	    throw new RuntimeException (ex);
	}

	if (! Modifier.isStatic (meth.getModifiers ()))
	{
	    throw new RuntimeException ("not static: " + clazz + "." + name);
	}

	return new VarArgsMethodCaller (null, meth);
    }

    /**
     * Construct an instance that is applicable to the named method
     * (static or instance) of the given object.
     *
     * @param object non-null; the object to be applicable to
     * @param name non-null; the name of the method
     * @return non-null; an instance which is applicable to the named
     * method of the given object
     */
    static public VarArgsMethodCaller makeInstance (Object object, String name)
    {
	if (object == null)
	{
	    throw new NullPointerException ("object == null");
	}
	
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	Class clazz = object.getClass ();

	Method meth;

	try
	{
	    meth = clazz.getMethod (name, ARGUMENT_TYPES);
	}
	catch (NoSuchMethodException ex)
	{
	    throw new RuntimeException (ex);
	}

	return new VarArgsMethodCaller (object, meth);
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is a private constructor; use the static
     * methods to construct instances of this class.
     *
     * @param target null-ok; the target object (<code>null</code> if only
     * to be applicable to static methods)
     * @param method non-null; method to apply
     */
    private VarArgsMethodCaller (Object target, Method method)
    {
	myTarget = target;
	myMethod = method;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // interface's comment suffices
    public Object apply (Object[] args)
    {
	try
	{
	    return myMethod.invoke (myTarget, new Object[] { args });
	}
	catch (IllegalAccessException ex)
	{
	    throw new RuntimeException (ex);
	}
	catch (InvocationTargetException ex)
	{
	    throw new RuntimeException (ex);
	}
    }
}
