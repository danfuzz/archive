// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.event;

import java.util.EventListener;

/**
 * This is the interface for objects that care about <code>ErrorEvent</code>s.
 *
 * @see ErrorEvent
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ErrorListener
extends EventListener
{
    /**
     * This is called when an error is to be reported to the user.
     *
     * @param event the event commemorating the moment
     */
    public void errorReport (ErrorEvent event);

    /**
     * This is called when a bug is to be reported to the user.
     *
     * @param event the event commemorating the moment
     */
    public void bugReport (ErrorEvent event);
}
