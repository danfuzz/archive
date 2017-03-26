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
 * This exception is thrown when a value is inappropriate in a particular
 * context.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class BadValueException
extends RuntimeException
{
    /** the value that is bad */
    private Object myValue;

    /** the context it is bad in */
    private Object myContext;

    /**
     * Construct a <code>BadValueException</code>.
     *
     * @param value the bad value
     * @param context the context it is bad in
     */
    public BadValueException (Object value, Object context)
    {
	this (value, context, null);
    }

    /**
     * Construct a <code>BadValueException</code>.
     *
     * @param value the bad value
     * @param context the context it is bad in
     * @param msg null-ok; extra detail message
     */
    public BadValueException (Object value, Object context, String msg)
    {
	super ("Bad value (" + value + ") for context (" + context + ")"
	       + ((msg == null) ? "" : (":\n" + msg)));
	myValue = value;
	myContext = context;
    }

    /**
     * Get the value.
     *
     * @return the value
     */
    public Object getValue ()
    {
	return myValue;
    }

    /**
     * Get the context.
     *
     * @return the context
     */
    public Object getContext ()
    {
	return myContext;
    }
}
