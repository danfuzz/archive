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
import java.util.ArrayList;

/**
 * Node representing match assignment. The way a match assignment works is
 * that, first, the function node is evaluated, then the argument nodes are
 * evaluated in order. If an argument node is literal, then the function
 * will be called with that literal argument; if not, then the funciton
 * will be called with a number representing that hole (first hole is
 * <code>0</code>, then <code>1</code>, and so on). The node is evaluated
 * as a slot and tucked away for later assignment. Then the value node is
 * evaluated. After that, the function is called; its arguments consist of
 * an array to hold the hole values (initialized to <code>null</code>s, the
 * value to match, and then all of the literals-or-hole-numbers. The result
 * of the function call becomes the result of the evaluation of this node.
 * However, before the evaluation completes, the hole values are assigned to
 * their respective slots.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class AssignMatchNode
    extends BaseNode
{
    /** non-null; the matcher function */
    private final StuNode myFunction;

    /** non-null, with non-null elements; array of argument nodes */
    private final StuNode[] myArgs;

    /** non-null; the value to assign */
    private final StuNode myValue;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param function non-null; the template-building function node
     * @param args non-null, with non-null elements; array of argument nodes
     * @param value non-null; the value to assign
     */
    public AssignMatchNode (StuNode function, StuNode[] args, StuNode value)
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

	if (value == null)
	{
	    throw new NullPointerException ("value == null");
	}

	myFunction = function;
	myArgs = args;
	myValue = value;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ('(');
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

	sb.append ("` =~ ");
	sb.append (myValue);
	sb.append (')');

	return sb.toString ();
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	Object func = myFunction.eval (environment);
	Object[] args = new Object[myArgs.length + 2];
	ArrayList slots = new ArrayList ();
	ArrayList assigns = new ArrayList ();

	// make a child environment for evaluation (so new variable
	// definitions don't leak out)
	Environment innerEnv = environment.makeChild ();

	for (int i = 0; i < myArgs.length; i++)
	{
	    StuNode one = myArgs[i];
	    if (one instanceof LiteralNode)
	    {
		args[i + 2] = one.eval (null);
	    }
	    else
	    {
		int sz = slots.size ();
		args[i + 2] = new Integer (sz);
		slots.add (one.evalOutsideSlot (innerEnv));
		assigns.add (null);
	    }
	}

	Object value = myValue.eval (environment);

	args[0] = assigns;
	args[1] = value;

	Object result = ((Function) func).apply (args);

	int sz = slots.size ();
	for (int i = 0; i < sz; i++)
	{
	    Slot slot = (Slot) slots.get (i);
	    Object assign = assigns.get (i);
	    slot.setValue (assign);
	}

	return result;
    }
}
