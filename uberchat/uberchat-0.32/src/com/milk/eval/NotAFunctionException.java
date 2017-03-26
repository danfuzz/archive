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
 * This exception is thrown when an <code>ApplyExpression</code> is
 * asked to evaluate itself, and its functor position evaluates to
 * something other than a <code>Function</code>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class NotAFunctionException
extends EvalException
{
    /** the object that is not a function */
    private final Object myNonFunction;

    /** the environment it is not a function in */
    private final Environment myEnvironment;

    /**
     * Construct a <code>NotAFunctionException</code>.
     *
     * @param nonFunction the object that is not a function
     * @param environment the environment that it is not a function in
     */
    public NotAFunctionException (Object nonFunction, Environment environment)
    {
	super ("Object (" + nonFunction + 
	       ") is not a function in environment (" + environment + ").");

	myNonFunction = nonFunction;
	myEnvironment = environment;
    }

    /**
     * Get the non-function.
     *
     * @return the non-function
     */
    public Object getNonFunction ()
    {
	return myNonFunction;
    }

    /**
     * Get the environment.
     *
     * @return the environment
     */
    public Environment getEnvironment ()
    {
	return myEnvironment;
    }
}
