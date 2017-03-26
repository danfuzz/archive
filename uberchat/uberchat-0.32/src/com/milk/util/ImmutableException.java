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
 * This exception is thrown when attempts are made to set a value to
 * something that is immutable.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ImmutableException
extends RuntimeException
{
    /**
     * Construct an <code>ImmutableException</code> with no detail message.
     */
    public ImmutableException ()
    {
    }

    /**
     * Construct an <code>ImmutableException</code> with the given detail
     * message. 
     *
     * @param detail the detail message
     */
    public ImmutableException (String detail)
    {
	super (detail);
    }

    /**
     * Construct an <code>ImmutableException</code> with a detail message
     * claiming the given object is immutable.
     *
     * @param obj the object in question
     */
    public ImmutableException (Object obj)
    {
	this ("Invalid attempt to change immutable object: " + obj);
    }
}
