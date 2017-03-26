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
 * This is an exception to throw when a program detects unexpected behavior
 * but that behavior is due to code not directly associated with the system
 * that is doing the throwing. That is, throwing one of these is an
 * indication that the code that <i>uses</i> the throwing code has a bug or
 * at least is not behaving properly.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. */
public class BugInClientException
extends RuntimeSubException
{
    /**
     * Construct a <code>BugInClientException</code> with no detail
     * message and no sub-exception.
     */
    public BugInClientException ()
    {
	this (null, null);
    }

    /**
     * Construct a <code>BugInClientException</code> with the given detail
     * message and no sub-exception.
     * 
     * @param detail the detail message
     */
    public BugInClientException (String detail)
    {
	this (detail, null);
    }

    /**
     * Construct a <code>BugInClientException</code> with no given detail
     * message and the given sub-exception.
     * 
     * @param subException null-ok; the sub-exception
     */
    public BugInClientException (Throwable subException)
    {
	this (null, subException);
    }

    /**
     * Construct a <code>BugInClientException</code> with the given detail
     * message and sub-exception.
     * 
     * @param detail null-ok; the detail message
     * @param subException null-ok; the sub-exception
     */
    public BugInClientException (String detail, Throwable subException)
    {
	super ("BUG IN CLIENT: " + 
	       ((detail == null) ? "Bug in client code." : detail),
	       subException);
    }
}
