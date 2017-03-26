// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.eval;

/**
 * A <code>NameExpression</code> merely holds a name to look up when
 * evaluated.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class NameExpression
extends Expression
{
    /** an object which we know will never be found in any environment,
     * since we never hand it out */
    private static final Object TheNotFoundObject = new Object ();

    /** the name to look up */
    private final Object myName;

    /**
     * Construct a <code>NameExpression</code>.
     *
     * @param name the name to look up
     */
    public NameExpression (Object name)
    {
	myName = name;
    }

    /**
     * Evaluate this expression in the given <code>Environment</code>.
     *
     * @param environment the environment to evaluate in
     * @return the result of evaluation
     */
    public Object eval (Environment environment)
    {
	Object result = environment.get (myName, TheNotFoundObject);

	if (result == TheNotFoundObject)
	{
	    throw new NameNotFoundException (myName, environment);
	}

	return result;
    }

    /**
     * Get the name.
     *
     * @return the name
     */
    public Object getName ()
    {
	return myName;
    }
}
