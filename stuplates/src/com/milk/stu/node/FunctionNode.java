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
import com.milk.stu.iface.Identifier;
import com.milk.stu.iface.Names;
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;
import com.milk.stu.util.VarNames;

/**
 * Node representing an anonymous function.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class FunctionNode
    extends BaseNode
{
    /** null-ok; the name for self-reference, if any */
    private final Identifier myName;

    /** non-null, with non-null elements; array of parameter names */
    private final Identifier[] myParamNames;

    /** non-null; body of the function */
    private final StuNode myBody;

    /** null-ok; the name for the named return, if any */
    private final Identifier myReturnName;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name null-ok; the name of this function for purposes of
     * self-reference, if any
     * @param paramNames non-null, with non-null elements; array of
     * parameter names
     * @param body non-null; body of the function 
     */
    public FunctionNode (String name, String[] paramNames, StuNode body)
    {
	if (paramNames == null)
	{
	    throw new NullPointerException ("paramNames == null");
	}

	myParamNames = new Identifier[paramNames.length];

	for (int i = 0; i < paramNames.length; i++)
	{
	    if (paramNames[i] == null)
	    {
		throw new NullPointerException ("paramNames[" + i + 
						"] == null");
	    }
	    myParamNames[i] = Identifier.intern (paramNames[i]);
	}

	if (body == null)
	{
	    throw new NullPointerException ("body == null");
	}

	if (name == null)
	{
	    myName = null;
	    myReturnName = null;
	}
	else
	{
	    myName = Identifier.intern (name);
	    myReturnName = VarNames.returnName (name);
	}

	myBody = body;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ("fn");

	if (myName != null)
	{
	    sb.append (' ');
	    sb.append (myName);
	}

	sb.append (" (");

	for (int i = 0; i < myParamNames.length; i++)
	{
	    if (i != 0)
	    {
		sb.append (", ");
	    }
	    sb.append (myParamNames[i]);
	}

	sb.append (") ");
	sb.append (myBody);

	return sb.toString ();
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	return new Closure (environment);
    }



    // ------------------------------------------------------------------------
    // private inner classes

    /**
     * A closure of this (outer) instance over a particular environment.
     */
    private class Closure
	implements Function
    {
	/** non-null; the environment that is closed over */
	private Environment myEnvironment;

	/**
	 * Construct an instance.
	 *
	 * @param environment non-null; the environment to close over
	 */
	public Closure (Environment environment)
	{
	    if (environment == null)
	    {
		throw new NullPointerException ("environment == null");
	    }

	    myEnvironment = environment;
	}

	// superclass's comment suffices
	public String toString ()
	{
	    if (myName == null)
	    {
		return "<function:anonymous>";
	    }
	    
	    return "<function:" + myName + ">";
	}

	// interface's comment suffices
	public Object apply (Object[] args)
	{
	    // check the argument count
	    if (args.length != myParamNames.length)
	    {
		throw new IllegalArgumentException ("wrong number of " +
						    "arguments to " + this);
	    }

	    // make a child environment and add new bindings for self (if
	    // appropriate), return, named return (if appropriate), and the
	    // arguments (if appropriate)

	    Environment env = myEnvironment.makeChild (args.length * 2 + 3);

	    for (int i = 0; i < args.length; i++)
	    {
		env.defineAlways (myParamNames[i], args[i]);
	    }

	    Function returnHere = new ControlFlow (env, true, myName);
	    env.defineAlways (Names.VAR_return, returnHere);

	    if (myName != null)
	    {
		env.defineAlways (myName, this);
		env.defineAlways (myReturnName, returnHere);
	    }

	    // eval the body

	    try
	    {
		return myBody.eval (env);
	    }
	    catch (ControlFlow.CFException ex)
	    {
		if (ex.isFor (env))
		{
		    return ex.getBreakArg ();
		}
		throw ex;
	    }
	}
    }
}
