// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.util;

/**
 * This exception is thrown when an argument is inappropriate for a call
 * of some sort. It's like <code>IllegalArgumentException</code> but with
 * more explicit structure.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class BadArgumentException
extends RuntimeException
{
    /**
     * Construct a <code>BadArgumentException</code>.
     *
     * @param function the function/method/whatever that was called
     * @param arguments the arguments it was called with
     * @param badArgument the argument (number) that is bad
     */
    public BadArgumentException (Object function, Object[] arguments,
				 int badArgument)
    {
	this (function, arguments, badArgument, null);
    }

    /**
     * Construct a <code>BadArgumentException</code>.
     *
     * @param function the function/method/whatever that was called
     * @param arguments the arguments it was called with
     * @param badArgument the argument (number) that is bad
     * @param detail further detail message
     */
    public BadArgumentException (Object function, Object[] arguments,
				 int badArgument, String detail)
    {
	super (makeMessage (function, arguments, badArgument, detail));
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Construct a detail message from the constructor args.
     *
     * @param function the function/method/whatever that was called
     * @param arguments null-ok; the arguments it was called with
     * @param badArgument the argument (number) that is bad
     * @param detail null-ok; further detail message
     */
    private static String makeMessage (Object function, Object[] arguments,
				       int badArgument, String detail)
    {
	StringBuffer sb = new StringBuffer ();
	sb.append ("Bad argument (number ");
	sb.append (badArgument);
	sb.append (") for call: ");
	sb.append (function.toString ());
	sb.append (" (");
	if (arguments != null)
	{
	    for (int i = 0; i < arguments.length; i++)
	    {
		if (i != 0)
		{
		    sb.append (", ");
		}
		sb.append (arguments[i].toString ());
	    }
	}
	sb.append (")");
	if ((detail != null) && (detail.length () != 0))
	{
	    sb.append ("\n");
	    sb.append (detail);
	}

	return sb.toString ();
    }
}
