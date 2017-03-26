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
 * A <code>Rendezvous</code> is anything which combines both a
 * <code>Sender</code> and a <code>Receiver</code>. Additionally, it must
 * support splitting apart the two parts via the additionally-specified
 * interface.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface Rendezvous
extends Sender, Receiver
{
    /**
     * Get a <code>JustSender</code> for this <code>Rendezvous</code>.
     *
     * @return the <code>JustSender</code>
     */
    public JustSender getSender ();

    /**
     * Get a <code>JustReceiver</code> for this <code>Rendezvous</code>.
     *
     * @return the <code>JustReceiver</code>
     */
    public JustReceiver getReceiver ();
}
