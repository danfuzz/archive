// Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
// reserved, except as follows:
// 
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the condition that the above
// copyright notice and this permission notice shall be included in all copies
// or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.milk.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This is a trivial extension of <code>RuntimeException</code>, which
 * adds an embedded exception, to use in exception cascades. Goodness
 * knows why <code>Throwable</code> just doesn't have this to begin
 * with.
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
