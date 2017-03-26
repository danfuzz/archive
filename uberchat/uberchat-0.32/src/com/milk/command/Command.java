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
 * A <code>Command</code> is a user-activatable action of some sort.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Command
{
    /**
     * Add a listener for this command. The listener will get any events
     * that it defines interfaces for. <code>Command</code> itself is
     * defined to only ever send <code>CommandEvent</code>s, but actual
     * implementations may send other event types. If the listener is in
     * fact a <code>CommandListener</code>, then it immediately gets sent
     * three events indicating the state of the command: a
     * <code>labelChanged</code> event, a <code>descriptionChanged</code>
     * event, and an <code>enabledChanged</code> event.
     *
     * @see CommandEvent
     * @see CommandListener
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
     * Get the label of this command. The label is a short string which
     * succinctly names this command. The return value should never be
     * null; <code>""</code> is acceptable, however.
     *
     * @return non-null; the label */
    public String getLabel ();

    /**
     * Get the full description of this command. It is suitable for
     * tool-tips or the like. The return value should never be
     * null; <code>""</code> is acceptable, however.
     *
     * @return non-null; the description
     */
    public String getDescription ();

    /**
     * Return true if this command is currently enabled. If it is
     * enabled, then <code>run()</code> should work. If it is not,
     * then <code>run()</code> should <i>not</i> work.
     *
     * @return true if this command is currently enabled
     */
    public boolean isEnabled ();

    /**
     * Create and return an argument template for this command. If the
     * template is null, then this command has no arguments. If it is
     * non-null, it may be any object, but, for the purposes of allowing
     * humans to interact with the command, it is generally best for it
     * to be an <code>Editable</code> of some sort.
     *
     * @see com.milk.objed.Editable
     *
     * @return non-null; a new argument template for this command, or
     * null if the command has no arguments
     */
    public Object makeArgument ();

    /**
     * Run the command with the given argument. The argument should be
     * an object that was previously returned from <code>makeArgument</code>.
     *
     * @param argument the argument of the command
     * @return null-ok; the return value of running the command
     * @exception DisabledCommandException thrown if the command is not
     * enabled at the time this method is called
     */
    public Object run (Object argument)
    throws DisabledCommandException;
}
