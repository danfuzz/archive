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
 * A <code>SendFilter</code> merely combines <code>SendSource</code> and
 * <code>Sender</code>. Conceptually, it is a filter layer between an
 * original sender and some eventual target, where sending to the
 * <code>SendFilter</code> may in turn cause the filter to send on to the
 * target. By default, <code>SendFilter</code> will just resend to its
 * target without actually changing the message. In order to perform
 * filtering, subclasses must override the <code>send()</code> method and
 * use <code>super.send()</code> to do the actual sending.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class SendFilter
implements Sender, SendSource
{
    /** the target to resend to */
    private Sender myTarget;

    /** whether we have stopped sending or not */
    private boolean myStopSending;

    /**
     * Construct a <code>SendFilter</code> to send to the given target.
     *
     * @param target the target to resend to
     */
    public SendFilter (Sender target)
    {
	if (target == null)
	{
	    throw new IllegalArgumentException ("bad target: " + target);
	}

	myTarget = target;
	myStopSending = false;
    }

    /**
     * Throw a <code>SenderCutException</code> if this
     * <code>SendFilter</code> has been stopped. This is mainly useful for
     * subclasses overriding <code>send()</code>. 
     */
    public void throwIfStopped ()
    {
	if (myStopSending)
	{
	    throw new SenderCutException (this);
	}
    }

    // ------------------------------------------------------------------------
    // SendSource instance methods

    /**
     * Tell this object to cease sending.
     */
    public void stopSending ()
    {
	myStopSending = true;
	myTarget = null;
    }

    // ------------------------------------------------------------------------
    // Sender instance methods
   
    /**
     * Send the given message.
     *
     * @param message the message to be sent
     */
    public void send (Object message)
    {
	if (myStopSending)
	{
	    throw new SenderCutException (this);
	}

	myTarget.send (message);
    }

    /**
     * Wait for this <code>Sender</code> to be empty.
     */
    public void waitUntilEmpty ()
    {
	myTarget.waitUntilEmpty ();
    }
}
