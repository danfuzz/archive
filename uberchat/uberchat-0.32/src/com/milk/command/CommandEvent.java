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
 * a <code>Command</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class CommandEvent
extends BaseEvent
{
    /** type code for <code>descriptionChanged</code> event */
    public static final int DESCRIPTION_CHANGED = 0;

    /** type code for <code>labelChanged</code> event */
    public static final int LABEL_CHANGED = 1;

    /** type code for <code>enabledChanged</code> event */
    public static final int ENABLED_CHANGED = 2;

    /**
     * Construct a <code>CommandEvent</code> of the particular type for the
     * particular command.
     *
     * @param source the command in question
     * @param type the event type
     * @param argument the argument 
     */
    private CommandEvent (Command source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>descriptionChanged</code> event.
     *
     * @param source the command whose description changed
     * @param description its new description
     */
    public static CommandEvent descriptionChanged (Command source,
						   String description)
    {
	return new CommandEvent (source, DESCRIPTION_CHANGED, description);
    }

    /**
     * Construct and return a <code>labelChanged</code> event.
     *
     * @param source the command whose name changed
     * @param label its new label
     */
    public static CommandEvent labelChanged (Command source,
					     String label)
    {
	return new CommandEvent (source, LABEL_CHANGED, label);
    }

    /**
     * Construct and return an <code>enabledChanged</code> event.
     *
     * @param source the command whose enabled state changed
     * @param enabled its new enabled state
     */
    public static CommandEvent enabledChanged (Command source,
					       boolean enabled)
    {
	return new CommandEvent (source, 
				 ENABLED_CHANGED,
				 enabled ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Get the source of this event as a <code>Command</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>Command</code> 
     */
    public Command getCommand ()
    {
	return (Command) source;
    }

    /**
     * Get the argument of this event as a string description. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a string
     */
    public String getDescription ()
    {
	return (String) myArgument;
    }

    /**
     * Get the argument of this event as a string label. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a string
     */
    public String getLabel ()
    {
	return (String) myArgument;
    }

    /**
     * Get the argument of this event as a boolean enabled value. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a boolean
     */
    public boolean getEnabled ()
    {
	return ((Boolean) myArgument).booleanValue ();
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
	    case DESCRIPTION_CHANGED: return "description-changed";
	    case LABEL_CHANGED:       return "label-changed";
	    case ENABLED_CHANGED:     return "enabled-changed";
	    default:                  return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	CommandListener l = (CommandListener) listener;

	switch (myType)
	{
	    case DESCRIPTION_CHANGED: l.descriptionChanged (this); break;
	    case LABEL_CHANGED:       l.labelChanged (this);       break;
	    case ENABLED_CHANGED:     l.enabledChanged (this);     break;
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
	return (listener instanceof CommandListener);
    }
}
