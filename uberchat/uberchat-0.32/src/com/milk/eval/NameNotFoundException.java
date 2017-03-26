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
 * This exception is thrown when a name lookup fails during evaluation.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class NameNotFoundException
extends EvalException
{
    /** the name that wasn't found */
    private final Object myName;

    /** the environment it wasn't found in */
    private final Environment myEnvironment;

    /**
     * Construct a <code>NameNotFoundException</code>.
     *
     * @param name the name that wasn't found
     * @param environment the environment it wasn't found in
     */
    public NameNotFoundException (Object name, Environment environment)
    {
	super ("Name (" + name + ") not found in environment (" + 
	       environment + ").");

	myName = name;
	myEnvironment = environment;
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
