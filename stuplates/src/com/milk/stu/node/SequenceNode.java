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
import com.milk.stu.iface.Slot;
import com.milk.stu.iface.StuNode;

/**
 * Node representing a sequence of expressions.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class SequenceNode
    extends BaseNode
{
    /** non-null, with non-null elements; the expressions in sequence */
    private final StuNode[] myExprs;



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param exprs non-null, with non-null elements; the expressions in
     * sequence
     */
    public SequenceNode (StuNode[] exprs)
    {
	if (exprs == null)
	{
	    throw new NullPointerException ("exprs == null");
	}

	for (int i = 0; i < exprs.length; i++)
	{
	    if (exprs[i] == null)
	    {
		throw new NullPointerException ("exprs[" + i + "] == null");
	    }
	}
	       
	myExprs = exprs;
    }



    // ------------------------------------------------------------------------
    // public instance methods

    // superclass's comment suffices
    public String toString ()
    {
	StringBuffer sb = new StringBuffer ();

	sb.append ("{");

	for (int i = 0; i < myExprs.length; i++)
	{
	    if (i != 0)
	    {
		sb.append (';');
	    }
	    sb.append (' ');
	    sb.append (myExprs[i]);
	}

	sb.append (" }");
	return sb.toString ();
    }

    // interface's comment suffices
    public Object evalOutside (Environment environment)
    {
	Object result = null;

	for (int i = 0; i < myExprs.length; i++)
	{
	    result = myExprs[i].eval (environment);
	}

	return result;
    }

    // interface's comment suffices
    public Object eval (Environment environment)
    {
	// make a child environment for evaluation (so new variable
	// definitions don't leak out)
	environment = environment.makeChild ();

	return evalOutside (environment);
    }

    // interface's comment suffices
    public Slot evalOutsideSlot (Environment environment)
    {
	int len = myExprs.length;

	if (len == 0)
	{
	    super.evalOutsideSlot (environment);
	}

	for (int i = 0; i < (len - 1); i++)
	{
	    myExprs[i].eval (environment);
	}

	return myExprs[len - 1].evalSlot (environment);
    }

    // interface's comment suffices
    public Slot evalSlot (Environment environment)
    {
	// make a child environment for evaluation (so new variable
	// definitions don't leak out)
	environment = environment.makeChild ();

	return evalOutsideSlot (environment);
    }

}
