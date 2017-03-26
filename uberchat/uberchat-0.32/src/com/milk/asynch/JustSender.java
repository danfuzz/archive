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
 * This is an implementation of <code>Sender</code> that wraps around
 * another <code>Sender</code>, and only re-exports the <code>Sender</code>
 * interface. This allows for better capability confinement (e.g., you
 * don't have to hand an unrestricted <code>MailBox</code> to something
 * that <i>should</i> only be sending to it).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class JustSender
implements Sender
{
    /** the sender we are wrapped around */
    private Sender mySender;

    /**
     * Construct a <code>JustSender</code> from the given <code>Sender</code>.
     *
     * @param sender the Sender to wrap up
     */
    public JustSender (Sender sender)
    {
	mySender = sender;
    }

    /**
     * Send a message.
     *
     * @param message the message to send
     */
    public void send (Object message)
    {
	mySender.send (message);
    }

    /**
     * Wait for the <code>Sender</code> to be empty.
     */
    public void waitUntilEmpty ()
    {
	mySender.waitUntilEmpty ();
    }
}
