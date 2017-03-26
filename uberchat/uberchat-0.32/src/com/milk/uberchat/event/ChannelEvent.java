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
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with activity that takes
 * place on a channel. In particular, it covers the user joining and
 * leaving and the topic changing.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ChannelEvent
extends BaseEvent
{
    /** type code for <code>joinedChannel</code> event */
    public static final int JOINED_CHANNEL = 0;

    /** type code for <code>joiningChannel</code> event */
    public static final int JOINING_CHANNEL = 1;

    /** type code for <code>leftChannel</code> event */
    public static final int LEFT_CHANNEL = 2;

    /** type code for <code>leavingChannel</code> event */
    public static final int LEAVING_CHANNEL = 3;

    /** type code for <code>topicChanged</code> event */
    public static final int TOPIC_CHANGED = 4;

    /**
     * Construct a <code>ChannelEvent</code> of the particular type for the
     * particular channel.
     *
     * @param source the channel in question
     * @param type the event type
     * @param argument the argument
     */
    private ChannelEvent (ChatChannel source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>joinedChannel</code> event.
     *
     * @param source the channel that was joined
     */
    public static ChannelEvent joinedChannel (ChatChannel source)
    {
	return new ChannelEvent (source, JOINED_CHANNEL, null);
    }

    /**
     * Construct and return a <code>joiningChannel</code> event.
     *
     * @param source the channel that is being joined
     */
    public static ChannelEvent joiningChannel (ChatChannel source)
    {
	return new ChannelEvent (source, JOINING_CHANNEL, null);
    }

    /**
     * Construct and return a <code>leftChannel</code> event.
     *
     * @param source the channel that was left
     */
    public static ChannelEvent leftChannel (ChatChannel source)
    {
	return new ChannelEvent (source, LEFT_CHANNEL, null);
    }

    /**
     * Construct and return a <code>leavingChannel</code> event.
     *
     * @param source the channel that is being left
     */
    public static ChannelEvent leavingChannel (ChatChannel source)
    {
	return new ChannelEvent (source, LEAVING_CHANNEL, null);
    }

    /**
     * Construct and return a <code>topicChanged</code> event.
     *
     * @param source the channel who's topic changed
     * @param topic null-ok; the new topic
     */
    public static ChannelEvent topicChanged (ChatChannel source,
					     String topic)
    {
	return new ChannelEvent (source, TOPIC_CHANGED, topic);
    }

    /**
     * Get the source of this event as a <code>ChatChannel</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatChannel</code>
     */
    public ChatChannel getChannel ()
    {
	return (ChatChannel) source;
    }

    /**
     * Get the argument of this event as a topic string. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a string
     */
    public String getTopic ()
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
	    case JOINED_CHANNEL:  return "joined-channel";
	    case JOINING_CHANNEL: return "joining-channel";
	    case LEFT_CHANNEL:    return "left-channel";
	    case LEAVING_CHANNEL: return "leaving-channel";
	    case TOPIC_CHANGED:   return "topic-changed";
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
	ChannelListener l = (ChannelListener) listener;

	switch (myType)
	{
	    case JOINED_CHANNEL:  l.joinedChannel (this);  break;
	    case JOINING_CHANNEL: l.joiningChannel (this); break;
	    case LEFT_CHANNEL:    l.leftChannel (this);    break;
	    case LEAVING_CHANNEL: l.leavingChannel (this); break;
	    case TOPIC_CHANGED:   l.topicChanged (this);   break;
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
	return (listener instanceof ChannelListener);
    }
}
