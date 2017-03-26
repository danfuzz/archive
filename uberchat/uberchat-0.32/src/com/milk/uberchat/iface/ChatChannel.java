// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.iface;

import java.util.EventListener;

/**
 * This interface is what all chat channels must adhere to.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatChannel
extends ChatLocus
{
    /** return value from <code>getJoinedState()</code> indicating that
     * the channel is joined */
    public static final int JOINED = 0;

    /** return value from <code>getJoinedState()</code> indicating that
     * the channel is in the process of being joined */
    public static final int JOINING = 1;

    /** return value from <code>getJoinedState()</code> indicating that
     * the channel is not joined */
    public static final int LEFT = 2;

    /** return value from <code>getJoinedState()</code> indicating that
     * the channel is in the process of being left */
    public static final int LEAVING = 3;

    /**
     * Add a listener for this channel. If it is in fact a
     * <code>ChannelListener</code>, then it immediately gets a joined-type
     * event if the channel is in any state other than <code>LEFT</code>,
     * and a <code>topicChanged</code> event if the topic isn't empty or
     * unknown.
     *
     * @see com.milk.uberchat.event.ChannelEvent
     * @see com.milk.uberchat.event.ChannelListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Get the topic of this channel. It should be <code>""</code> (not null)
     * if the topic is empty or unknown, or the chat system for this channel
     * doesn't support topics.
     *
     * @return non-null; the topic of this channel
     */
    public String getTopic ();

    /**
     * (Attempt to) set the topic of this channel. A null value is
     * interpreted as <code>""</code>.
     *
     * @param topic null-ok; the new topic for the channel
     */
    public void setTopic (String topic);

    /**
     * Get the joined state of this channel. It is one of
     * <code>ChatChannel.JOINED</code>, <code>ChatChannel.JOINING</code>,
     * <code>ChatChannel.LEFT</code>, or <code>ChatChannel.LEAVING</code>.
     *
     * @return the joined state of this channel 
     */
    public int getJoinedState ();

    /**
     * (Attempt to) join this channel. Assuming the channel is
     * <code>LEFT</code> at the time this is called, this should
     * immediately cause the channel to be in a <code>JOINING</code> state.
     * Once the channel is actually joined, it will be in the
     * <code>JOINED</code> state, but if the attempt to join fails, it will
     * be back in the <code>LEFT</code> state. Events for each state
     * transition are sent out to listeners as they happen.
     */
    public void join ();

    /**
     * (Attempt to) leave this channel. Assuming the channel is
     * <code>JOINED</code> at the time this is called, this should
     * immediately cause the channel to be in a <code>LEAVING</code> state.
     * Once the channel has actually been left, it will be in the
     * <code>LEFT</code> state. Events for each state transition are sent
     * out to listeners as they happen. 
     */
    public void leave ();

    /**
     * Join the channel if it isn't joined, leave it if it is, or do nothing
     * (possibly signalling an error) if it is in a transitional state.
     */
    public void joinOrLeave ();
}

