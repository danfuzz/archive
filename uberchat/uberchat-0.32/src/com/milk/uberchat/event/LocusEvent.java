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

import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with (non-chat-message)
 * happenings at a <code>ChatLocus</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class LocusEvent
extends BaseEvent
{
    /** type code for <code>userAdded</code> event */
    public static final int USER_ADDED = 0;

    /** type code for <code>userRemoved</code> event */
    public static final int USER_REMOVED = 1;

    /**
     * Construct a <code>LocusEvent</code> of the particular type for the
     * particular locus.
     *
     * @param source the locus in question
     * @param type the event type
     * @param argument the argument
     */
    private LocusEvent (ChatLocus source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>userAdded</code> event.
     *
     * @param locus the locus in question
     * @param user the user that was added
     */
    public static LocusEvent userAdded (ChatLocus locus, ChatUser user)
    {
	return new LocusEvent (locus, USER_ADDED, user);
    }

    /**
     * Construct and return a <code>userRemoved</code> event.
     *
     * @param locus the locus in question
     * @param user the user that was removed
     */
    public static LocusEvent userRemoved (ChatLocus locus, ChatUser user)
    {
	return new LocusEvent (locus, USER_REMOVED, user);
    }

    /**
     * Get the source of this event as a <code>ChatLocus</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatLocus</code>
     */
    public ChatLocus getLocus ()
    {
	return (ChatLocus) source;
    }

    /**
     * Get the argument of this event as a <code>ChatUser</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a <code>ChatUser</code> 
     */
    public ChatUser getUser ()
    {
	return (ChatUser) myArgument;
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
	    case USER_ADDED:   return "user-added";
	    case USER_REMOVED: return "user-removed";
	    default:           return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	LocusListener l = (LocusListener) listener;

	switch (myType)
	{
	    case USER_ADDED:   l.userAdded (this); break;
	    case USER_REMOVED: l.userRemoved (this); break;
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
	return (listener instanceof LocusListener);
    }
}
