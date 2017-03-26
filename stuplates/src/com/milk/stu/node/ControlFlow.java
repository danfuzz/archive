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

/**
 * Class used for control flow functions (that is, <code>break</code>,
 * <code>continue</code>, and <code>return</code>).
 */
public class ControlFlow
    implements Function
{
    /** non-null; the environment representing the scope to control */
    private final Environment myEnvironment;

    /** <code>true</code> if this is a <code>break</code> or
     * <code>return</code> function, <code>false</code> if this is a
     * <code>continue</code> function */
    private final boolean myIsBreak;

    /** null-ok; name of the block that this instance is in reference to,
     * if any */
    private final Identifier myName;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param environment non-null; the environment
     * @param isBreak whether this is a <code>break</code> or
     * <code>return</code>
     * @param name null-ok; the name of the block that this instance is
     * in reference to, if any 
     */
    public ControlFlow (Environment environment, boolean isBreak, 
			Identifier name)
    {
	if (environment == null)
	{
	    throw new NullPointerException ("environment == null");
	}

	myEnvironment = environment;
	myIsBreak = isBreak;
	myName = name;
    }

    // superclass's comment suffices
    public String toString ()
    {
	Identifier kind = myIsBreak ? Names.VAR_break : Names.VAR_continue;
	
	if (myName == null)
	{
	    return "<function:" + kind + ">";
	}
	
	return "<function:" + kind + ":" + myName + ">";
    }

    // interface's comment suffices
    public Object apply (Object[] args)
    {
	// check the argument count
	if (args.length > (myIsBreak ? 1 : 0))
	{
	    throw new IllegalArgumentException ("wrong number of " +
						"arguments to " + this);
	}

	// apply by throwing
	Object arg = (args.length != 0) ? args[0] : null;
	throw new CFException (arg);
    }

    /**
     * Exception class used for control flow.
     */
    public class CFException
	extends RuntimeException
    {
	/** null-ok; the argument to the <code>break</code> (ignored
	 * if this is a <code>continue</code>). */
	private Object myBreakArg;

	/**
	 * Construct an instance.
	 *
	 * @param arg null-ok; the argument to the <code>break</code>, if
	 * appropriate
	 */
	public CFException (Object breakArg)
	{
	    myBreakArg = breakArg;
	}

	/**
	 * Return whether or not this instance is in reference to the given
	 * environment.
	 *
	 * @param env non-null; the environment to check
	 * @return <code>true</code> iff this instance is in reference to
	 * the given environment
	 */
	public boolean isFor (Environment env)
	{
	    return env == myEnvironment;
	}

	/**
	 * Get whether or not this is a <code>break</code>.
	 *
	 * @return the <code>break</code> flag
	 */
	public boolean isBreak ()
	{
	    return myIsBreak;
	}

	/**
	 * Get the <code>break</code> argument.
	 *
	 * @return null-ok; the argument
	 */
	public Object getBreakArg ()
	{
	    return myBreakArg;
	}
    }
}
