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
 * This interface is for <code>ChatLocus</code> objects that also have
 * an associated list of channels. The two normal cases of this are
 * <code>ChatIdentity</code> which knows about all the channels for
 * the identity and <code>ChatUser</code> which knows what channels the
 * user is on.
 *
 * @see ChatIdentity
 * @see ChatUser
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatChannelHolder
extends ChatLocus
{
    /**
     * Add a listener for this object. If the listener is in fact an
     * <code>ChannelHolderListener</code>, then it gets immediately sent a
     * <code>channelCreated</code> event for each channel the identity
     * knows about.
     *
     * @see com.milk.uberchat.event.ChannelHolderEvent
     * @see com.milk.uberchat.event.ChannelHolderListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Given a canonical name, return the known channel that corresponds to
     * that name, or return null if there is no known channel with that
     * name. No querying of the host should be done to satisfy this
     * request.
     *
     * @param name the canonical channel name to look up
     * @return null-ok; the corresponding channel, if any 
     */
    public ChatChannel getKnownChannel (String name);

    /**
     * Return the number of currently-known channels for this object.
     *
     * @return the count of known channels
     */
    public int getKnownChannelCount ();

    /**
     * Return the list of currently-known channels for this object.
     * That is, no querying of the host should be done to satisfy this
     * request.
     *
     * @return the list (array, actually) of currently-known channels
     */
    public ChatChannel[] getKnownChannels ();

    /**
     * Ask for the list of channels for this object to be updated from
     * the host. This may (and in fact should) return quickly, after which
     * a series of <code>channelAdded</code> and
     * <code>channelRemoved</code> events may be expected to be broadcast
     * to the listeners of this object, as appropriate. 
     */
    public void updateChannels ();
}

