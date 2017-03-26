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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is an exception to throw when a program needs to throw a
 * <code>RuntimeException</code> which <i>may</i> embed another exception.
 * It has a way to access the sub-exception and the
 * <code>printStackTrace()</code> methods do something reasonable too.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class RuntimeSubException
extends RuntimeException
{
    /** null-ok; the wrapped exception */
    private Throwable mySubException;

    /**
     * Construct a <code>RuntimeSubException</code> with no detail
     * message and no sub-exception.
     */
    public RuntimeSubException ()
    {
	this (null, null);
    }

    /**
     * Construct a <code>RuntimeSubException</code> with the given detail
     * message and no sub-exception.
     * 
     * @param detail the detail message
     */
    public RuntimeSubException (String detail)
    {
	this (detail, null);
    }

    /**
     * Construct a <code>RuntimeSubException</code> with no given detail
     * message and the given sub-exception.
     * 
     * @param subException null-ok; the sub-exception
     */
    public RuntimeSubException (Throwable subException)
    {
	this (null, subException);
    }

    /**
     * Construct a <code>RuntimeSubException</code> with the given detail
     * message and sub-exception.
     * 
     * @param detail null-ok; the detail message
     * @param subException null-ok; the sub-exception
     */
    public RuntimeSubException (String detail, Throwable subException)
    {
	super (detail);
	mySubException = subException;
    }

    /**
     * Get the sub-exception out of this object, if any.
     *
     * @return null-ok; the sub-exception
     */
    public Throwable getSubException ()
    {
	return mySubException;
    }

    /**
     * This version of this method prints out the stack trace of the
     * sub-exception as well as the stack trace of this exception.
     */
    public void printStackTrace ()
    {
	printStackTrace (System.err);
    }

    /**
     * This version of this method prints out the stack trace of the
     * sub-exception as well as the stack trace of this exception.
     *
     * @param s the stream to print to
     */
    public void printStackTrace (PrintStream s)
    {
	super.printStackTrace (s);
	if (mySubException != null)
	{
	    s.println ("----------");
	    mySubException.printStackTrace (s);
	}
    }

    /**
     * This version of this method prints out the stack trace of the
     * sub-exception as well as the stack trace of this exception.
     *
     * @param w the writer to print to
     */
    public void printStackTrace (PrintWriter w)
    {
	super.printStackTrace (w);
	if (mySubException != null)
	{
	    w.println ("----------");
	    mySubException.printStackTrace (w);
	}
    }
}
