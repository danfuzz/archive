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
 * An <code>ApplyExpression</code> holds a function and arguments to
 * evaluate. The function must always evaluate to a function object, and
 * the environment is asked to apply that function to the (possibly empty)
 * array of arguments.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ApplyExpression
extends Expression
{
    /** a zero-length array of objects, used so we don't keep allocating
     * these */
    private static final Object[] TheZeroLengthObjectArray = new Object[0];

    /** the functor to evaluate */
    private final Expression myFunctor;

    /** the arguments to evaluate */
    private final Expression[] myArguments;

    /**
     * Construct an <code>ApplyExpression</code>.
     *
     * @param functor the functor to evaluate
     * @param arguments the arguments to evaluate
     */
    public ApplyExpression (Expression functor, Expression[] arguments)
    {
	myFunctor = functor;

	// we can't trust the client to not muck with the passed-in array
	// (unless its length is 0), so we have to copy it. Java needs to
	// have constant arrays, but it doesn't. It just sucks that way
	// sometimes.
	if (arguments.length == 0)
	{
	    myArguments = arguments;
	}
	else
	{
	    myArguments = new Expression[arguments.length];
	    System.arraycopy (arguments, 0, myArguments, 0, arguments.length);
	}
    }

    /**
     * Evaluate this expression in the given <code>Environment</code>.
     *
     * @param environment the environment to evaluate in
     * @return the result of evaluation
     */
    public Object eval (Environment environment)
    {
	Object[] args;
	int argLen = myArguments.length;
	Object function = myFunctor.eval (environment);

	if (! environment.isFunction (function))
	{
	    throw new NotAFunctionException (function, environment);
	}

	if (argLen == 0)
	{
	    args = TheZeroLengthObjectArray;
	}
	else
	{
	    args = new Object[argLen];
	    for (int i = 0; i < argLen; i++)
	    {
		args[i] = myArguments[i].eval (environment);
	    }
	}

	return environment.apply (function, args);
    }

    /**
     * Get the functor.
     *
     * @return the functor
     */
    public Expression getFunctor ()
    {
	return myFunctor;
    }

    /**
     * Get the count of arguments.
     *
     * @return the count of arguments
     */
    public int getArgumentCount ()
    {
	return myArguments.length;
    }

    /**
     * Return a particular argument.
     *
     * @param n which argument to get
     * @return the <code>n</code>th argument
     */
    public Expression getArgument (int n)
    {
	return myArguments[n];
    }
}
