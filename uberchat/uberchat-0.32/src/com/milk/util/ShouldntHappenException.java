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
 * This is an exception to throw when a program detects unexpected
 * behavior. That is, throwing one of these is an indication that the
 * person that wrote the code that had the throw either has a bug in their
 * code, or there is a bug in the Java system that the code is running on.
 * It should <i>not</i> be thrown in cases of client error. For example,
 * code that is part of a class library shouldn't throw this exception
 * because of bad data passed to it by the clients of the library. Rather,
 * such problems should be detected before it reaches the point of throwing
 * this exception.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class ShouldntHappenException
extends RuntimeSubException
{
    /**
     * Construct a <code>ShouldntHappenException</code> with no detail
     * message and no sub-exception.
     */
    public ShouldntHappenException ()
    {
	this (null, null);
    }

    /**
     * Construct a <code>ShouldntHappenException</code> with the given detail
     * message and no sub-exception.
     * 
     * @param detail the detail message
     */
    public ShouldntHappenException (String detail)
    {
	this (detail, null);
    }

    /**
     * Construct a <code>ShouldntHappenException</code> with no given detail
     * message and the given sub-exception.
     * 
     * @param subException null-ok; the sub-exception
     */
    public ShouldntHappenException (Throwable subException)
    {
	this (null, subException);
    }

    /**
     * Construct a <code>ShouldntHappenException</code> with the given detail
     * message and sub-exception.
     * 
     * @param detail null-ok; the detail message
     * @param subException null-ok; the sub-exception
     */
    public ShouldntHappenException (String detail, Throwable subException)
    {
	super ("SHOULDN'T HAPPEN: " + 
	       ((detail == null) ? "Unexpected behavior detected." : detail),
	       subException);
    }
}
