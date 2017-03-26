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
 * This class provides a way to sense that a sender has gone idle and send
 * out an idle message. Generally, one constructs an
 * <code>IdleFilter</code> handing it a target <code>Sender</code>. Then,
 * instead of sending to the target directly, one sends to the
 * <code>IdleFilter</code>, which will, in general, just resend to the
 * target. However, once messages stop arriving at the
 * <code>IdleFilter</code>, the <code>IdleFilter</code> itself will send an
 * idle message to the target.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class IdleFilter
extends SendFilter
{
    private Object myIdleMessage;
    private long myIdleTime;
    private TimedSend myNextIdle;

    /**
     * Construct an <code>IdleFilter</code> to send to the given sender,
     * with the given idle timeout.
     *
     * @param target the target to (re)send to
     * @param idleMessage the message to send when idle
     * @param idleTime the time (msec) to wait before sending an idle message
     */
    public IdleFilter (Sender target, Object idleMessage, long idleTime)
    {
	super (target);

	if (idleTime <= 0)
	{
	    throw new IllegalArgumentException ("bad idleTime: " + idleTime);
	}

	myIdleMessage = idleMessage;
	myIdleTime = idleTime;
	myNextIdle = new TimedSend (target, myIdleMessage);
	setupNextIdle ();
    }

    // ------------------------------------------------------------------------
    // helper methods

    /**
     * Set up the <code>myNextIdle</code> instance variable, scheduling
     * it appropriately.
     */
    private void setupNextIdle ()
    {
	myNextIdle.schedule (myIdleTime);
    }

    // ------------------------------------------------------------------------
    // SendSource interface methods

    /**
     * Tell this <code>SendSource</code> to cease sending.
     */
    public void stopSending ()
    {
	myNextIdle.cancel ();
	super.stopSending ();
    }

    // ------------------------------------------------------------------------
    // Sender interface methods

    /**
     * Send a message.
     *
     * @param message the message to send
     */
    public void send (Object message)
    {
	super.send (message);
	setupNextIdle ();
    }
}
