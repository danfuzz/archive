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

import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with happenings on
 * a <code>ChatUser</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class UserEvent
extends BaseEvent
{
    /** type code for <code>userChanedNickname</code> event */
    public static final int USER_CHANGED_NICKNAME = 0;

    /**
     * Construct a <code>UserEvent</code> of the particular type for the
     * particular user.
     *
     * @param source the user in question
     * @param type the event type
     * @param argument the argument
     */
    private UserEvent (ChatUser source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>userChangedNickname</code> event.
     *
     * @param user the user in question
     * @param nickname the new nickname
     */
    public static UserEvent userChangedNickname (ChatUser user,
						 String nickname)
    {
	return new UserEvent (user, USER_CHANGED_NICKNAME, nickname);
    }

    /**
     * Get the source of this event as a <code>ChatUser</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatUser</code> 
     */
    public ChatUser getUser ()
    {
	return (ChatUser) source;
    }

    /**
     * Get the argument of this event as a nickname string. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a string
     */
    public String getNickname ()
    {
	return (String) myArgument;
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
	    case USER_CHANGED_NICKNAME: return "user-changed-nickname";
	    default:                    return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	UserListener l = (UserListener) listener;

	switch (myType)
	{
	    case USER_CHANGED_NICKNAME: l.userChangedNickname (this); break;
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
	return (listener instanceof UserListener);
    }
}
