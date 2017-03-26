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
 * This interface is for receiving messages from a logical process (threads
 * and whatnot). Implementors of this interface should be sufficiently
 * paranoid that the clients of the interface don't themselves have to
 * synchronize on Receiver objects.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface Receiver
{
    /**
     * Retrieve a message out of the <code>Receiver</code>, but if the
     * <code>Receiver</code> is empty, first wait for it to be filled.
     *
     * @return a message that was in the <code>Receiver</code>, or null if
     * there was none 
     */
    public Object receive ();

    /**
     * Wait for the <code>Receiver</code> to have a message in it, but
     * don't actually retrieve the message. 
     */
    public void waitUntilFull ();
}
