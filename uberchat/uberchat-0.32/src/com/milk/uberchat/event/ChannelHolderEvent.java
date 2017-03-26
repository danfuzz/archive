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
import com.milk.uberchat.iface.ChatChannelHolder;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with channels being
 * added and removed from a <code>ChatChannelHolder</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ChannelHolderEvent
extends BaseEvent
{
    /** type code for <code>channelAdded</code> event */
    public static final int CHANNEL_ADDED = 0;

    /** type code for <code>channelRemoved</code> event */
    public static final int CHANNEL_REMOVED = 1;

    /**
     * Construct a <code>ChannelHolderEvent</code> of the particular type
     * for the particular source.
     *
     * @param source the source
     * @param type the event type
     * @param argument the argument 
     */
    private ChannelHolderEvent (ChatChannelHolder source, int type, 
				Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>channelAdded</code> event.
     *
     * @param source the object that the channel was added to
     * @param channel the channel that was added
     */
    public static ChannelHolderEvent channelAdded (ChatChannelHolder source,
						   ChatChannel channel)
    {
	return new ChannelHolderEvent (source, CHANNEL_ADDED, channel);
    }

    /**
     * Construct and return a <code>channelRemoved</code> event.
     *
     * @param source the object that the channel was removed from
     * @param channel the channel that was removed
     */
    public static ChannelHolderEvent channelRemoved (
        ChatChannelHolder source, ChatChannel channel)
    {
	return new ChannelHolderEvent (source, CHANNEL_REMOVED, channel);
    }

    /**
     * Get the source of this event as a <code>ChatChannelHolder</code>. It
     * is merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatChannelHolder</code> 
     */
    public ChatChannelHolder getChannelHolder ()
    {
	return (ChatChannelHolder) source;
    }

    /**
     * Get the argument of this event as a <code>ChatChannel</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a <code>ChatChannel</code> 
     */
    public ChatChannel getChannel ()
    {
	return (ChatChannel) myArgument;
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
	    case CHANNEL_ADDED:   return "channel-added";
	    case CHANNEL_REMOVED: return "channel-removed";
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
	ChannelHolderListener il = (ChannelHolderListener) listener;

	switch (myType)
	{
	    case CHANNEL_ADDED:   il.channelAdded (this);   break;
	    case CHANNEL_REMOVED: il.channelRemoved (this); break;
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
	return (listener instanceof ChannelHolderListener);
    }
}
