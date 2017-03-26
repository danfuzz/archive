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
import com.milk.stu.iface.Function;
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;

/**
 * Node representing a list of expressions, to append together as a
 * template and then apply as arguments to a particular function. This is
 * similar to an {@link ApplyNode}, except that a scope is created for the
 * arguments, allowing local variables to be shared amongst them, and
 * sequence arguments are evaluated "on the outside" so that their variable
 * declarations do in fact end up in the shared scope.
 *
 * @author Dan Bornstein, danfuzz@milk.com 
 */
final public class TemplateNode
    extends BaseNode
{
    /** non-null; the template-building function node */
    private final StuNode myFunction;

    /** non-null, with non-null elements; array of argument nodes */
    private final StuNode[] myArgs;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param function non-null; the template-building function node
     * @param args non-null, with non-null elements; array of argument nodes
     */
    public TemplateNode (StuNode function, StuNode[] args)
    {
	if (function == null)
	{
	    throw new NullPointerException ("function == null");
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

	myFunction = function;
	myArgs = args;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append (myFunction);
	sb.append ('`');

	for (int i = 0; i < myArgs.length; i++)
	{
	    StuNode one = myArgs[i];

	    if (one instanceof LiteralNode)
	    {
		sb.append (one.eval (null));
	    }
	    else if (one instanceof SequenceNode)
	    {
		sb.append ('$');
		sb.append (one);
	    }
	    else
	    {
		sb.append ("${");
		sb.append (one);
		sb.append ("}");
	    }
	}

	sb.append ('`');
	return sb.toString ();
    }

    /**
     * Get the array of argument nodes.
     *
     * @return non-null; the arguments array
     */
    public StuNode[] getArguments ()
    {
	return myArgs;
    }

    /**
     * Get the function node.
     *
     * @return non-null; the function node
     */
    public StuNode getFunction ()
    {
	return myFunction;
    }

    /**
     * Return a copy of this instance, except with a different function.
     *
     * @param function non-null; the new function
     * @return non-null; the newly-constructed instance
     */
    public TemplateNode withFunction (StuNode function)
    {
	return new TemplateNode (function, myArgs);
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	Object func = myFunction.eval (environment);
	Object[] args = new Object[myArgs.length];

	// make a child environment for evaluation (so new variable
	// definitions don't leak out)
	environment = environment.makeChild ();

	for (int i = 0; i < myArgs.length; i++)
	{
	    args[i] = myArgs[i].evalOutside (environment);
	}

	return ((Function) func).apply (args);
    }
}
