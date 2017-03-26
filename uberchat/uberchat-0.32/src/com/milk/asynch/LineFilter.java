// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.asynch;

/**
 * A <code>LineFilter</code> is a resending filter which expects to be fed
 * a bunch of <code>char[]</code>s, interspersed with other arbitrary
 * objects. The non-<code>char[]</code>s are taken to be "delimiters" to
 * <code>LineFilter</code>'s primary operation, which is splitting the
 * <code>char[]</code>s into lines of text. A line is considered ended by
 * either a CR, an LF, or a CRLF pair. When it resends the line, all
 * line-ends are canonicalized into just an LF. If a partial line is
 * received and then a non-<code>char[]</code> is received, then the
 * partial line is sent with no line end. This allows for use with, e.g.,
 * an <code>IdleFilter</code>, so that partial lines will get sent
 * promptly, but full lines will still get properly coalesced.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class LineFilter
extends SendFilter
{
    private StringBuffer myBuffer;
    private boolean mySwallowLF;
    public boolean myDebug = false;

    /**
     * Construct a <code>LineFilter</code> to send to the given target.
     *
     * @param target the target to resend to
     */
    public LineFilter (Sender target)
    {
	super (target);

	myBuffer = new StringBuffer ();
	mySwallowLF = false;
    }

    // ------------------------------------------------------------------------
    // SendSource interface methods

    /**
     * Send a message.
     *
     * @param message the message to send
     */
    public synchronized void send (Object message)
    {
	throwIfStopped ();

	if (message instanceof char[])
	{
	    char[] ca = (char[]) message;
	    boolean swallowLF = mySwallowLF;

	    for (int i = 0; i < ca.length; i++)
	    {
		char c = ca[i];
		switch (c)
		{
		    case '\r':
		    {
			myBuffer.append ('\n');
			sendBuffer ();
			swallowLF = true;
			break;
		    }
		    case '\n':
		    {
			if (swallowLF)
			{
			    swallowLF = false;
			}
			else
			{
			    myBuffer.append ('\n');
			    sendBuffer ();
			}
			break;
		    }
		    default:
		    {
			myBuffer.append (c);
			swallowLF = false;
			break;
		    }
		}
	    }

	    mySwallowLF = swallowLF;
	}
	else
	{
	    sendBuffer ();
	    try
	    {
		super.send (message);
	    }
	    catch (Exception ex)
	    {
		// it's legit to ignore the exception; see
		// Sender.send() for more details
		if (myDebug)
		{
		    System.err.println (this + " ignored exception:");
		    ex.printStackTrace ();
		}
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Helper methods

    /**
     * Send the contents of the buffer as a <code>char[]</code>, but only
     * if it's not empty. 
     */
    private void sendBuffer ()
    {
	int len = myBuffer.length ();

	if (len == 0)
	{
	    return;
	}

	char[] toSend = new char[len];
	myBuffer.getChars (0, len, toSend, 0);
	myBuffer.setLength (0);
	try
	{
	    super.send (toSend);
	}
	catch (Exception ex)
	{
	    // it's legit to ignore the exception; see
	    // Sender.send() for more details
	    if (myDebug)
	    {
		System.err.println (this + " ignored exception:");
		ex.printStackTrace ();
	    }
	}
    }
}
