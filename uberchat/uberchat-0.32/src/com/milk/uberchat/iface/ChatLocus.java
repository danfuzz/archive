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
 * This interface represents any locus of users, including a chat identity, a
 * channel on a chat system, and a user on a chat system. Some methods may
 * make more or less sense then others in a particular context (e.g.,
 * <code>getKnownUsers()</code> applied to a <code>ChatUser</code> is
 * <i>mostly</i> pointless). Note that a locus generally encapsulates a
 * source identity, so, for example, if a chat system allows for a single
 * connection with multiple active identities, there could be multiple
 * <code>ChatLocus</code> objects for the same effective locus in the
 * system, one for each of the active identities.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatLocus
extends ChatEntity
{
    /**
     * Add a listener for this locus. If the listener is in fact
     * a <code>LocusListener</code>, then the listener gets initially sent
     * <code>userAdded</code> events for each user in the locus.
     *
     * <p><code>ChatLocus</code> objects are also the source for
     * <code>MessageEvent</code>s. Adding an <code>MessageListener</code>
     * to a <code>ChatLocus</code> should cause that listener to be told
     * when messages come in from the host system (speech or
     * otherwise).</p>
     *
     * @see com.milk.uberchat.event.LocusEvent
     * @see com.milk.uberchat.event.LocusListener
     * @see com.milk.uberchat.event.MessageEvent
     * @see com.milk.uberchat.event.MessageListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Get the identity that this locus is directed at.
     *
     * @return the identity for this locus
     */
    public ChatIdentity getTargetIdentity ();

    /**
     * Get the system that this locus is connected to.
     *
     * @return the system for this locus
     */
    public ChatSystem getTargetSystem ();

    /**
     * Given a canonical name, return the user in this locus with
     * that name, but only if the user is already known. That is,
     * asking for the name does not imply that it should be found,
     * and no querying of the host system should be done to satisfy
     * this request.
     *
     * @param name the canonical name to look up
     * @return the user found, or null if not
     */
    public ChatUser getKnownUser (String name);

    /**
     * Return the number of currently-known users in this locus.
     *
     * @return the count of known channels
     */
    public int getKnownUserCount ();

    /**
     * Return the list of currently-known users in this locus.
     * That is, no querying of the host should be done to satisfy this
     * request.
     *
     * @return the list (array, actually) of currently-known users
     */
    public ChatUser[] getKnownUsers ();

    /**
     * Ask for the list of users currently in this locus to be updated from
     * the host. This may (and in fact should) return quickly, after which
     * a series of <code>userAdded</code> and <code>userRemoved</code>
     * events may be expected to be broadcast to the listeners of this
     * object, as appropriate. 
     */
    public void updateUsers ();

    /**
     * Speak in this locus.
     *
     * @see com.milk.uberchat.SpeechKinds
     *
     * @param kind the kind of speech (see <code>SpeechKinds</code> for
     * details)
     * @param text the message text
     */
    public void speak (String kind, String text);
}
