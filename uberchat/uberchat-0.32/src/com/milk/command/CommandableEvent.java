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

import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with happenings on
 * a <code>Commandable</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class CommandableEvent
extends BaseEvent
{
    /** type code for <code>commandAdded</code> event */
    public static final int COMMAND_ADDED = 0;

    /** type code for <code>commandRemoved</code> event */
    public static final int COMMAND_REMOVED = 1;

    /**
     * Construct a <code>CommandableEvent</code> of the particular type for the
     * particular command.
     *
     * @param source the commandable in question
     * @param type the event type
     * @param argument the argument 
     */
    private CommandableEvent (Commandable source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>commandAdded</code> event.
     *
     * @param source the commandable in question
     * @param command the command that was added
     */
    public static CommandableEvent commandAdded (Commandable source,
						 Command command)
    {
	return new CommandableEvent (source, COMMAND_ADDED, command);
    }

    /**
     * Construct and return a <code>commandRemoved</code> event.
     *
     * @param source the commandable in question
     * @param command the command that was removed
     */
    public static CommandableEvent commandRemoved (Commandable source,
						   Command command)
    {
	return new CommandableEvent (source, COMMAND_REMOVED, command);
    }

    /**
     * Get the source of this event as a <code>Commandable</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>Command</code> 
     */
    public Commandable getCommandable ()
    {
	return (Commandable) source;
    }

    /**
     * Get the argument of this event as a <code>Command</code>. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a <code>Command</code>
     */
    public Command getCommand ()
    {
	return (Command) myArgument;
    }

    // ------------------------------------------------------------------------
    // BaseEvent methods

    /**
     * Turn the given type code into a string.
     *
     * @param type the type code to translate
     * @return the type as a string
     */
    protected String typeToString (int type)
    {
	switch (type)
	{
	    case COMMAND_ADDED:   return "command-added";
	    case COMMAND_REMOVED: return "command-added";
	    default:              return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	CommandableListener l = (CommandableListener) listener;

	switch (myType)
	{
	    case COMMAND_ADDED:   l.commandAdded (this);   break;
	    case COMMAND_REMOVED: l.commandRemoved (this); break;
	    default:
	    {
		throw new RuntimeException (
                    "Attempt to send unknown event type " + myType + ".");
	    }
	}
    }

    /**
     * Return true if this event is appropiate for the given listener.
     *
     * @param listener the listener to check
     * @return true if the listener listens to this kind of event
     */
    public boolean canSendTo (EventListener listener)
    {
	return (listener instanceof CommandableListener);
    }
}
