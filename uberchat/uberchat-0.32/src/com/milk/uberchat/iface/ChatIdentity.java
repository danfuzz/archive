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
 * This interface encapsulates a user identity, generally a particular
 * userid on a particular chat system. This kept as a separate interface
 * from <code>ChatSystem</code> itself to allow for the possibility of
 * multiple active identities on the same system. As a
 * <code>ChatLocus</code>, a <code>ChatIdentity</code> is about sending
 * and/or receiving system-wide messages for the given identity.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatIdentity
extends ChatChannelHolder
{
    /**
     * Get the <code>ChatUser</code> that represents this identity.
     *
     * @return the identity user
     */
    public ChatUser getIdentityUser ();

    /**
     * Get the current nickname/description of this identity.
     *
     * @return the nickname
     */
    public String getNickname ();

    /**
     * (Attempt to) change the nickname/description of this identity.
     *
     * @param newNick the new nickname
     */
    public void setNickname (String newNick);

    /**
     * Turn a string name into a <code>ChatChannel</code>, if possible, or
     * return null if not successful.
     *
     * @param name the name of the channel
     * @return the corresponding <code>ChatChannel</code> 
     */
    public ChatChannel nameToChannel (String name);

    /**
     * Turn a string name into a <code>ChatUser</code>, if possible, or
     * return null if not successful.
     *
     * @param name the name of the user
     * @return the corresponding <code>ChatUser</code> 
     */
    public ChatUser nameToUser (String name);

    /**
     * Join or leave a named channel. This should behave more or less like
     * <code>nameToChannel (name).joinOrLeave ()</code>.
     *
     * @param name the name of the channel
     */
    public void joinOrLeaveChannel (String name);

    /**
     * Speak to a named channel. This should behave more or less like
     * <code>nameToChannel (name).speak (kind, text)</code>.
     *
     * @param name the name of the channel
     * @param kind the speech kind
     * @param text the text to speak
     */
    public void speakToChannel (String name, String kind, String text);

    /**
     * Speak to a named user. This should behave more or less like
     * <code>nameToUser (name).speak (kind, text)</code>.
     *
     * @param name the name of the user
     * @param kind the speech kind
     * @param text the text to speak
     */
    public void speakToUser (String name, String kind, String text);
}

