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
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;
import com.milk.stu.data.ClassBundle;
import com.milk.stu.data.MethodBundle;

/**
 * Node representing method application.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class ApplyMethodNode
    extends BaseNode
{
    /** non-null; the target object node */
    private final StuNode myTarget;

    /** non-null; the method name */
    private final Identifier myMethodName;

    /** non-null, with non-null elements; array of argument nodes */
    private final StuNode[] myArgs;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param target non-null; the target object node
     * @param methodName non-null; the method name
     * @param args non-null, with non-null elements; array of argument nodes
     */
    public ApplyMethodNode (StuNode target, String methodName, StuNode[] args)
    {
	if (target == null)
	{
	    throw new NullPointerException ("target == null");
	}

	if (methodName == null)
	{
	    throw new NullPointerException ("methodName == null");
	}

	if (args == null)
	{
	    throw new NullPointerException ("args == null");
	}

	for (int i = 0; i < args.length; i++)
	{
	    if (args[i] == null)
	    {
		throw new NullPointerException ("args[" + i + "] == null");
	    }
	}

	myTarget = target;
	myMethodName = Identifier.intern (methodName);
	myArgs = args;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append (myTarget);
	sb.append ('.');
	sb.append (myMethodName);
	sb.append (" (");

	for (int i = 0; i < myArgs.length; i++)
	{
	    if (i != 0)
	    {
		sb.append (", ");
	    }
	    sb.append (myArgs[i]);
	}

	sb.append (")");
	return sb.toString ();
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	Object result = rawEval (environment);

	if (result instanceof Slot)
	{
	    return ((Slot) result).getValue ();
	}

	return result;
    }

    // interface's comment suffices
    public Slot evalSlot (Environment environment)
    {
	Object result = rawEval (environment);

	if (result instanceof Slot)
	{
	    return (Slot) result;
	}

	// super will throw the right exception
	return super.evalSlot (environment);
    }



    // ------------------------------------------------------------------------
    // private instance methods

    /** 
     * Raw evaluate. This does the application and returns the value. However
     * the things that call this check it / manipulate it before returning
     * to outside this class.
     *
     * @param environment non-null; the environment to evaluate in
     * @return null-ok; arbitrary result of evaluation
     */
    private Object rawEval (Environment environment)
    {
	StuNode[] argNodes = myArgs; // to avoid instance variable access
	int len = argNodes.length;

	Object target = myTarget.eval (environment);
	MethodBundle[] mbs;

	if (target instanceof Class)
	{
	    ClassBundle cb = ClassBundle.get ((Class) target);
	    mbs = cb.getClassMethods (myMethodName);
	}
	else
	{
	    ClassBundle cb = ClassBundle.get (target.getClass ());
	    mbs = cb.getMethods (myMethodName);
	}

	Object[] args = new Object[len];

	for (int i = 0; i < len; i++)
	{
	    args[i] = argNodes[i].eval (environment);
	}

	for (int i = 0; i < mbs.length; i++)
	{
	    if (mbs[i].canApplyTo (args))
	    {
		return mbs[i].invoke (target, args);
	    }
	}

	throw new RuntimeException ("no applicable " + myMethodName + 
				    " method");
    }
}
