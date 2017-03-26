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
 * A <code>Commandable</code> is an object that has an associated list
 * of user commands.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Commandable
{
    /**
     * Add a listener for this object. The listener will get any events
     * that it defines interfaces for. <code>Commandable</code> itself is
     * defined to only ever send <code>CommandableEvent</code>s, but actual
     * implementations may send other event types. If the listener is in
     * fact a <code>CommandableListener</code>, then it immediately gets
     * sent a <code>commandAdded</code> event per command of the
     * <code>Commandable</code>.
     *
     * @see CommandableEvent
     * @see CommandableListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Remove a listener from this command that was previously added
     * with <code>addListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeListener (EventListener listener);

    /**
     * Get the user commands that may be used with this object.
     *
     * @return the array of commands
     */
    public Command[] getCommands ();
}
