// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.command;

import java.util.EventListener;

/**
 * This is the interface for objects that care about
 * <code>CommandableEvent</code>s.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface CommandableListener
extends EventListener
{
    /**
     * This is called when a command is added.
     *
     * @param event the event commemorating the moment
     */
    public void commandAdded (CommandableEvent event);

    /**
     * This is called when a command is removed.
     *
     * @param event the event commemorating the moment
     */
    public void commandRemoved (CommandableEvent event);
}
