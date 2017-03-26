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
 * This is the exception which is thrown when something tries to cause a
 * send to happen but the target <code>Sender</code> has been told not to
 * send anymore.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class SenderCutException
extends RuntimeException
{
    /**
     * Construct a <code>SenderCutException</code>, referring to the given
     * <code>Sender</code>.
     *
     * @param sender the given <code>Sender</code> 
     */
    public SenderCutException (Sender sender)
    {
	super ("Sender has been cut: " + sender);
    }
}
