package com.milk.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is a trivial extension of <code>RuntimeException</code>, which
 * adds an embedded exception, to use in exception cascades. Goodness
 * knows why <code>Throwable</code> just doesn't have this to begin
 * with.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 * 
 * @author Dan Bornstein, danfuzz@milk.com
 */
public class EmbeddedException
extends RuntimeException
{
    /** the embedded exception */
    private Throwable myEmbeddedException;

    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance with neither a message nor an embedded exception.
     */
    public EmbeddedException ()
    {
	this (null, null);
    }

    /**
     * Construct an instance with just a message but no embedded exception.
     *
     * @param message the message
     */
    public EmbeddedException (String message)
    {
	this (message, null);
    }

    /**
     * Construct an instance with just an embedded exception but no message.
     * The message ends up being derived from the embedded exception.
     *
     * @param embeddedException the exception
     */
    public EmbeddedException (Throwable embeddedException)
    {
	this (embeddedException.getMessage (), embeddedException);
    }

    /**
     * Construct an instance with both message and embedded exception.
     *
     * @param message the message
     * @param embeddedException the exception
     */
    public EmbeddedException (String message, Throwable embeddedException)
    {
	super (message);
	myEmbeddedException = embeddedException;
    }

    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Retrieve the embedded exception, if any.
     *
     * @return null-okay; the embedded exception
     */
    public Throwable getEmbeddedException ()
    {
	return myEmbeddedException;
    }

    // superclass's javadoc suffices
    public void printStackTrace ()
    {
	printStackTrace (System.err);
    }

    // superclass's javadoc suffices
    public void printStackTrace (PrintStream s)
    {
	super.printStackTrace (s);
	if (myEmbeddedException != null)
	{
	    s.println ("Embedded exception:");
	    myEmbeddedException.printStackTrace (s);
	}
    }

    // superclass's javadoc suffices
    public void printStackTrace (PrintWriter w)
    {
	super.printStackTrace (w);
	if (myEmbeddedException != null)
	{
	    w.println ("Embedded exception:");
	    myEmbeddedException.printStackTrace (w);
	}
    }
}
