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
 * This class is a filter which merely takes <code>char[]</code>s sent to
 * it and resends them as <code>String</code>s. Everything else is passed
 * through unchanged.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class CharArrayToStringFilter
extends SendFilter
{
    /**
     * Construct a <code>CharArrayToStringFilter</code> to send to the
     * given target.
     *
     * @param target the target to resend to 
     */
    public CharArrayToStringFilter (Sender target)
    {
	super (target);
    }

    // ------------------------------------------------------------------------
    // SendSource interface methods

    /**
     * Send a message.
     *
     * @param message the message to send
     */
    public void send (Object message)
    {
	if (message instanceof char[])
	{
	    message = new String ((char[]) message);
	}

	super.send (message);
    }
}
