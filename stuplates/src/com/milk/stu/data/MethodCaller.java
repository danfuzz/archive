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
import java.math.BigInteger;
import java.util.TreeSet;

/**
 * Function which knows how to select a method from a list of potentials
 * and apply it to an encapsulated target.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class MethodCaller
    implements Function
{
    /** null-ok; the target object */
    private Object myTarget;

    /** non-null, with non-null elements; possibly-applicable methods */
    private MethodBundle[] myMethods;



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Construct an instance that is applicable to the named static method(s)
     * of the given class.
     *
     * @param clazz non-null; the class to be applicable to
     * @param name non-null; the name of the method(s)
     * @return non-null; an instance which is applicable to all the named
     * static methods of the given class
     */
    static public MethodCaller makeStatic (Class clazz, String name)
    {
	return makeStatic (clazz, Identifier.intern (name));
    }

    /**
     * Construct an instance that is applicable to the named static method(s)
     * of the given class.
     *
     * @param clazz non-null; the class to be applicable to
     * @param name non-null; the name of the method(s)
     * @return non-null; an instance which is applicable to all the named
     * static methods of the given class
     */
    static public MethodCaller makeStatic (Class clazz, Identifier name)
    {
	if (clazz == null)
	{
	    throw new NullPointerException ("clazz == null");
	}
	
	if (name == null)
	{
	    throw new NullPointerException ("name == null");
	}

	ClassBundle cb = ClassBundle.get (clazz);
	MethodBundle[] meths = cb.getClassMethods (name);

	return new MethodCaller (null, meths);
    }

    /**
     * Construct an instance that is applicable to the named static and
     * instance method(s) of the given object.
     *
     * @param object non-null; the object to be applicable to
     * @param name non-null; the name of the method(s)
     * @return non-null; an instance which is applicable to all the named
     * instance methods of the given object
     */
    static public MethodCaller makeInstance (Object object, Identifier name)
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
	ClassBundle cb = ClassBundle.get (clazz);
	MethodBundle[] meths = cb.getMethods (name);

	return new MethodCaller (object, meths);
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance. This is private; use one of the public static
     * methods to get instances.
     *
     * @param target null-ok; the target object (<code>null</code> if only
     * to be applicable to static methods)
     * @param methods non-null, with non-null elements; possibly-applicable
     * methods
     */
    private MethodCaller (Object target, MethodBundle[] methods)
    {
	if (methods == null)
	{
	    throw new NullPointerException ("methods == null");
	}

	myTarget = target;
	myMethods = methods;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ("<");
	
	if (myTarget != null)
	{
	    sb.append (myTarget);
	    sb.append (".");
	}

	TreeSet nset = new TreeSet ();
	for (int i = 0; i < myMethods.length; i++)
	{
	    nset.add (myMethods[i].toString ());
	}
	sb.append (nset);

	sb.append (">");
	return sb.toString ();
    }

    /**
     * Get the count of possibly applicable methods.
     *
     * @return the count
     */
    public int getCount ()
    {
	return myMethods.length;
    }

    // interface's comment suffices
    public Object apply (Object[] args)
    {
	for (int i = 0; i < myMethods.length; i++)
	{
	    MethodBundle one = myMethods[i];
	    if (one.canApplyTo (args))
	    {
		return one.invoke (myTarget, args);
	    }
	}

	// xxx: need real rep printer
	StringBuffer sb = new StringBuffer ();
	sb.append ('(');
	for (int i = 0; i < args.length; i++)
	{
	    if (i != 0) sb.append (", ");
	    sb.append (args[i]);
	}
	sb.append (')');
	
	throw new RuntimeException ("not applicable: " + this + " to " +
				    sb);
    }
}
