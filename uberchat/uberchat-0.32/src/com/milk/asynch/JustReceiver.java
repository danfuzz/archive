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
 * This is an implementation of <code>Receiver</code> that wraps around
 * another <code>Receiver</code>, and only re-exports the
 * <code>Receiver</code> interface. This allows for better capability
 * confinement (e.g., you don't have to hand an unrestricted
 * <code>MailBox</code> to something that <i>should</i> only be receiving
 * from it).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class JustReceiver
implements Receiver
{
    /** the receiver we are wrapped around */
    private Receiver myReceiver;

    /**
     * Construct a <code>JustReceiver</code> from the given Receiver.
     *
     * @param receiver the <code>Receiver</code> to wrap up
     */
    public JustReceiver (Receiver receiver)
    {
	myReceiver = receiver;
    }

    /**
     * Retrieve a message out of the <code>Receiver</code>, but if the
     * <code>Receiver</code> is empty, first wait for it to be filled.
     *
     * @return a message that was in the <code>Receiver</code>
     */
    public Object receive ()
    {
	return myReceiver.receive ();
    }

    /**
     * Wait for the <code>Receiver</code> to have a message in it, but
     * don't actually retrieve the message. 
     */
    public void waitUntilFull ()
    {
	myReceiver.waitUntilFull ();
    }
}
