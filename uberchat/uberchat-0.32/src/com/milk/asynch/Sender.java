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
 * This interface is for sending messages to a logical process (threads and
 * whatnot). Implementors of this interface should be sufficiently paranoid
 * that the clients of the interface don't themselves have to synchronize
 * on <code>Sender</code> objects. Note that in general, implementations of
 * <code>Sender.send()</code> should not run for unbounded amounts of time,
 * in particular, shouldn't synchronize on any poorly-controlled object or
 * run arbitrary client-supplied code.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface Sender
{
    /**
     * Send a message to this <code>Sender</code>, blocking if necessary.
     * Note that it is considered bad behavior for this method to ever
     * throw any exception, including a <code>RuntimeException</code>.
     * Implementors should do all in their power to prevent exceptions
     * from escaping a call to this method. It is also, therefore, legitimate
     * for users of this interface to drop any exceptions they catch from
     * calling this method on the floor. (Although, for debugging purposes,
     * it may be nice to know when such an event occurs.)
     *
     * @param message the message to put in the mailbox
     */
    public void send (Object message);

    /**
     * Wait for the <code>Sender</code> to be empty.
     */
    public void waitUntilEmpty ();
}
