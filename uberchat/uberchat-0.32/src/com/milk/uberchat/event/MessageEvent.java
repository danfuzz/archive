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
 * This is the class for events having to do with chat-messages coming in
 * from the host directed toward a particular <code>ChatLocus</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class MessageEvent
extends BaseEvent
{
    /** type code for <code>userBroadcast</code> event */
    public static final int USER_BROADCAST = 0;

    /** type code for <code>systemBroadcast</code> event */
    public static final int SYSTEM_BROADCAST = 1;

    /** type code for <code>systemPrivate</code> event */
    public static final int SYSTEM_PRIVATE = 2;

    /**
     * Construct a <code>MessageEvent</code> of the particular type for the
     * particular locus.
     *
     * @param source the locus in question
     * @param type the event type
     * @param argument the argument
     */
    private MessageEvent (ChatLocus source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>userBroadcast</code> event.
     *
     * @param locus the locus in question
     * @param user the user that said something
     * @param kind the kind of thing that was said (see
     * <code>SpeechKinds</code>)
     * @param text the text that was spoken
     */
    public static MessageEvent userBroadcast (ChatLocus locus, ChatUser user,
					      String kind, String text)
    {
	return new MessageEvent (locus, USER_BROADCAST, 
				 new UserBroadcastDetails (user, kind, text));
    }

    /**
     * Construct and return a <code>systemBroadcast</code> event.
     *
     * @param locus the locus in question
     * @param text the text that the system sent
     */
    public static MessageEvent systemBroadcast (ChatLocus locus, String text)
    {
	return new MessageEvent (locus, SYSTEM_BROADCAST, text);
    }

    /**
     * Construct and return a <code>systemPrivate</code> event.
     *
     * @param locus the locus in question
     * @param arg the argument that the system sent (often, but not
     * necessarily, a string) 
     */
    public static MessageEvent systemPrivate (ChatLocus locus, Object arg)
    {
	return new MessageEvent (locus, SYSTEM_PRIVATE, arg);
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
     * Get the argument of this event as a text string. It is merely a
     * convenience to avoid having to do the cast/methods yourself.
     *
     * @return the argument of this event as a string
     */
    public String getText ()
    {
	if (myArgument instanceof String)
	{
	    return (String) myArgument;
	}
	else if (myArgument instanceof UserBroadcastDetails)
	{
	    return ((UserBroadcastDetails) myArgument).getMessageString ();
	}
	else
	{
	    return myArgument.toString ();
	}
    }

    /**
     * Get the argument of this event as a
     * <code>UserBroadcastDetails</code>. It is merely a convenience to
     * avoid having to do the cast yourself.
     *
     * @return the argument of this event as a
     * <code>UserBroadcastDetails</code> 
     */
    public UserBroadcastDetails getDetails ()
    {
	return (UserBroadcastDetails) myArgument;
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
	    case USER_BROADCAST:   return "user-broadcast";
	    case SYSTEM_BROADCAST: return "system-broadcast";
	    case SYSTEM_PRIVATE:   return "system-private";
	    default:               return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	MessageListener l = (MessageListener) listener;

	switch (myType)
	{
	    case USER_BROADCAST:   l.userBroadcast (this);   break;
	    case SYSTEM_BROADCAST: l.systemBroadcast (this); break;
	    case SYSTEM_PRIVATE:   l.systemPrivate (this);   break;
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
	return (listener instanceof MessageListener);
    }
}
