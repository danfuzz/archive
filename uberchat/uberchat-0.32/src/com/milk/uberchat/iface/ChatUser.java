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
 * This interface is what all chat users must adhere to.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatUser
extends ChatChannelHolder
{
    /**
     * Add a listener for this user. If it is in fact a
     * <code>UserListener</code>, then the listener immediately gets sent
     * events about the interesting state of the user, which merely
     * includes a <code>userChangedNickname</code> event if the nickname is
     * non-empty.
     *
     * @see com.milk.uberchat.event.UserEvent
     * @see com.milk.uberchat.event.UserListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Get the nickname/description of this user. Should return
     * <code>""</code> and not null if the chat system doesn't support
     * nicknames.
     *
     * @return the nickname/description of the user 
     */
    public String getNickname ();

    /**
     * Get the standard <code>"<i>name</i>/<i>nick</i>"</code> combo
     * string. This returns either the full combination, or merely
     * the name if the nickname is either empty or identical to the
     * name.
     *
     * @return the name/nick combo
     */
    public String getNameNickCombo ();
}
