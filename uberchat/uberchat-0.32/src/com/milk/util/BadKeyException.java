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
 * This exception is thrown when a key is inappropriate in a particular
 * context.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class BadKeyException
extends RuntimeException
{
    /** the key that is bad */
    private Object myKey;

    /** the context it is bad in */
    private Object myContext;

    /**
     * Construct a <code>BadKeyException</code>.
     *
     * @param key the bad key
     * @param context the context it is bad in
     */
    public BadKeyException (Object key, Object context)
    {
	this (key, context, null);
    }

    /**
     * Construct a <code>BadKeyException</code>.
     *
     * @param key the bad key
     * @param context the context it is bad in
     * @param msg null-ok; extra detail message
     */
    public BadKeyException (Object key, Object context, String msg)
    {
	super ("Bad key (" + key + ") for context (" + context + ")"
	       + ((msg == null) ? "" : (": " + msg)));
	myKey = key;
	myContext = context;
    }

    /**
     * Get the key.
     *
     * @return the key
     */
    public Object getKey ()
    {
	return myKey;
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
