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
 * Node representing a standard loop construct.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class LoopNode
    extends BaseNode
{
    /** null-ok; the name for explicitly named <code>break</code>s and
     * <code>continue</code>s, if any */
    private final Identifier myName;

    /** non-null; the loop body */
    private final StuNode myBody;

    /** null-ok; the name for the named break, if any */
    private final Identifier myBreakName;

    /** null-ok; the name for the named continue, if any */
    private final Identifier myContinueName;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param name null-ok; the name of this loop for explicitly named
     * <code>break</code>s and <code>continue</code>s, if any
     * @param body non-null; body of the loop 
     */
    public LoopNode (String name, StuNode body)
    {
	if (body == null)
	{
	    throw new NullPointerException ("body == null");
	}

	if (name == null)
	{
	    myName = null;
	    myBreakName = null;
	    myContinueName = null;
	}
	else
	{
	    myName = Identifier.intern (name);
	    myBreakName = VarNames.breakName (name);
	    myContinueName = VarNames.continueName (name);
	}

	myBody = body;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ("loop");

	if (myName != null)
	{
	    sb.append (' ');
	    sb.append (myName);
	}

	sb.append (' ');
	sb.append (myBody);

	return sb.toString ();
    }

    // interface's comment suffices
    public Object eval (Environment env)
    {
	// make a child environment for evaluation (a place to
	// define break and continue functions)
	env = env.makeChild (4);

	// define and bind the break and continue functions
	Function breakHere = new ControlFlow (env, true, myName);
	Function continueHere = new ControlFlow (env, false, myName);
	env.defineAlways (Names.VAR_break, breakHere);
	env.defineAlways (Names.VAR_continue, continueHere);

	if (myName != null)
	{
	    env.defineAlways (myBreakName, breakHere);
	    env.defineAlways (myContinueName, continueHere);
	}

	// run the loop...

	for (;;)
	{
	    try
	    {
		myBody.eval (env);
	    }
	    catch (ControlFlow.CFException ex)
	    {
		if (ex.isFor (env))
		{
		    if (ex.isBreak ())
		    {
			return ex.getBreakArg ();
		    }
		}
		else
		{
		    throw ex;
		}
	    }
	}
    }
}
