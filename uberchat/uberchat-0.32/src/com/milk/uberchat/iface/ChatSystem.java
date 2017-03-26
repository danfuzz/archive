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

import com.milk.objed.Editable;
import java.util.EventListener;

/**
 * <p>This interface is what all chat systems must adhere to. It allows for
 * clients to request stuff like connecting, disconnecting, and arranging
 * to receive messages transmitted from the host.</p>
 *
 * <p>This interface is defined to extend <code>Editable</code> as both the
 * way for users to tweak the parameters of a system, and in order to
 * facilitate saving and loading.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatSystem
extends ChatEntity, Editable
{
    /** return value from <code>getConnectionState()</code> indicating that
     * the system is connected */
    public static final int CONNECTED = 0;

    /** return value from <code>getConnectionState()</code> indicating that
     * the system is in the process of connecting */
    public static final int CONNECTING = 1;

    /** return value from <code>getConnectionState()</code> indicating that
     * the system is disconnected */
    public static final int DISCONNECTED = 2;

    /** return value from <code>getConnectionState()</code> indicating that
     * the system is in the process of disconnecting */
    public static final int DISCONNECTING = 3;

    /**
     * <p>Add a listener for this system. If the listener is in fact a
     * <code>SystemListener</code>, then it immediately gets sent a
     * connect-type event if the system is anything but disconnected, and
     * it gets sent an <code>identityAdded</code> event for all
     * pre-existing identities. Note that <code>ChatIdentity</code>s only
     * come into existence when a connection is successfully made to a host
     * system.</p>
     *
     * @see com.milk.uberchat.event.SystemEvent
     * @see com.milk.uberchat.event.SystemListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Turn the given user name into its canonical form. For example,
     * if case is ignored for this system, this might return it as
     * all lower-case. The returned string should also be interned.
     *
     * @param orig the original name
     * @return the canonical form
     */
    public String canonicalUserName (String orig);

    /**
     * Turn the given channel name into its canonical form. For example,
     * if case is ignored for this system, this might return it as
     * all lower-case. The returned string should also be interned.
     *
     * @param orig the original name
     * @return the canonical form
     */
    public String canonicalChannelName (String orig);

    /**
     * (Attempt to) connect to this system. A <code>systemConnecting</code>
     * event will be sent if a connection actually get attempted, and a
     * <code>systemConnected</code> event gets sent once a connection
     * actually is successful. If the connection is unsuccessful, then a
     * <code>systemDisconnected</code> event gets sent. 
     */
    public void connect ();

    /**
     * Disconnect from this system. A <code>systemDisconnecting</code>
     * event will be sent immediately if it is valid to disconnect from the
     * system (i.e., the system is in fact connected). Once the system is
     * actually disconnected, a <code>systemDisconnected</code> event gets
     * sent. 
     */
    public void disconnect ();

    /**
     * Return the current state of the connection of this system to its
     * host. It is one of the constants <code>ChatSystem.CONNECTING</code>,
     * <code>ChatSystem.CONNECTED</code>
     * <code>ChatSystem.DISCONNECTING</code>, or
     * <code>ChatSystem.DISCONNECTED</code>.
     *
     * @return the current state of the connection 
     */
    public int getConnectionState ();

    /**
     * Get the list of <code>ChatIdentity</code>s for this system. Note
     * that if the system is not connected, this should return an empty
     * array.
     *
     * @return non-null; the list (array actually) of identities 
     */
    public ChatIdentity[] getIdentities ();

    /**
     * Get a template which is suitable for handing to a factory to
     * recreate a <code>ChatSystem</code> like this one. It is valid for a
     * system to not have an associated factory, but that will make it
     * impossible to reasonably edit the system. In that case, this method
     * returns null.
     *
     * @return null-ok; a suitable template, if any 
     */
    public ChatSystemTemplate getTemplate ();
}
