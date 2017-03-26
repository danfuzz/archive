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
 * This is the exception thrown by <code>Orderable.compare()</code> and
 * <code>Orderer.compare()</code> when they are handed handed objects which
 * are unordered.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class UnorderedException
extends RuntimeException
{
    /**
     * Construct an <code>UnorderedException</code> with no detail message.
     */
    public UnorderedException ()
    {
	// this space intentionally left blank
    }

    /**
     * Construct an <code>UnorderedException</code> with the given detail
     * message.
     *
     * @param msg the detail message 
     */
    public UnorderedException (String msg)
    {
	super (msg);
    }

    /**
     * Construct an <code>UnorderedException</code> complaining that the
     * given two objects are unordered with respect to each other.
     *
     * @param obj1 one object
     * @param obj2 the other object
     */
    public UnorderedException (Object obj1, Object obj2)
    {
	super ("Objects are unordered with respect to each other: " +
	       obj1 + ", " + obj2);
    }
}
