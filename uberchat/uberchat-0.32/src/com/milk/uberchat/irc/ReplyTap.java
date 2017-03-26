// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.irc;

/**
 * This interface is to be implemented by the taps that are added to
 * snarf away <code>ServerReply</code> messages.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ interface ReplyTap
{
    /**
     * Handle the given reply message.
     *
     * @param reply the reply message
     * @return true if the tap is done with its tapping
     */
    public boolean handleReply (ServerReply reply);
}
