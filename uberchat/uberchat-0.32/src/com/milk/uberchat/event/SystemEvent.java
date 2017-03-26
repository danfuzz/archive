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

import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with <code>ChatSystem</code>s.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class SystemEvent
extends BaseEvent
{
    /** type code for <code>identityAdded</code> event */
    public static final int IDENTITY_ADDED = 0;

    /** type code for <code>identityRemoved</code> event */
    public static final int IDENTITY_REMOVED = 1;

    /** type code for <code>systemConnected</code> event */
    public static final int SYSTEM_CONNECTED = 2;

    /** type code for <code>systemConnecting</code> event */
    public static final int SYSTEM_CONNECTING = 3;

    /** type code for <code>systemDisconnected</code> event */
    public static final int SYSTEM_DISCONNECTED = 4;

    /** type code for <code>systemDisconnecting</code> event */
    public static final int SYSTEM_DISCONNECTING = 5;

    /**
     * Construct a <code>SystemEvent</code> of the particular type.
     *
     * @param source the system in question
     * @param type the event type
     * @param argument the argument
     */
    private SystemEvent (ChatSystem source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return an <code>identityAdded</code> event.
     *
     * @param ident the identity that was added
     */
    public static SystemEvent identityAdded (ChatIdentity ident)
    {
	return new SystemEvent (ident.getTargetSystem (), 
				IDENTITY_ADDED, ident);
    }

    /**
     * Construct and return an <code>identityRemoved</code> event.
     *
     * @param ident the identity that was removed
     */
    public static SystemEvent identityRemoved (ChatIdentity ident)
    {
	return new SystemEvent (ident.getTargetSystem (), 
				IDENTITY_REMOVED, ident);
    }

    /**
     * Construct and return a <code>systemConnected</code> event.
     *
     * @param source the system that was connected
     */
    public static SystemEvent systemConnected (ChatSystem source)
    {
	return new SystemEvent (source, SYSTEM_CONNECTED, null);
    }

    /**
     * Construct and return a <code>systemConnecting</code> event.
     *
     * @param source the system that is connecting
     */
    public static SystemEvent systemConnecting (ChatSystem source)
    {
	return new SystemEvent (source, SYSTEM_CONNECTING, null);
    }

    /**
     * Construct and return a <code>systemDisconnected</code> event.
     *
     * @param source the system that was disconnected
     */
    public static SystemEvent systemDisconnected (ChatSystem source)
    {
	return new SystemEvent (source, SYSTEM_DISCONNECTED, null);
    }

    /**
     * Construct and return a <code>systemDisonnecting</code> event.
     *
     * @param source the system that is disconnecting
     */
    public static SystemEvent systemDisconnecting (ChatSystem source)
    {
	return new SystemEvent (source, SYSTEM_CONNECTING, null);
    }

    /**
     * Get the source of this event as a <code>ChatSystem</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatSystem</code>. 
     */
    public ChatSystem getSystem ()
    {
	return (ChatSystem) source;
    }

    /**
     * Get the argument of this event as a <code>ChatIdentity</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a <code>ChatIdentity</code>. 
     */
    public ChatIdentity getIdentity ()
    {
	return (ChatIdentity) myArgument;
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
	    case IDENTITY_ADDED:       return "identity-added";
	    case IDENTITY_REMOVED:     return "identity-removed";
	    case SYSTEM_CONNECTED:     return "system-connected";
	    case SYSTEM_CONNECTING:    return "system-connecting";
	    case SYSTEM_DISCONNECTED:  return "system-disconnected";
	    case SYSTEM_DISCONNECTING: return "system-disconnecting";
	    default:                   return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	SystemListener l = (SystemListener) listener;

	switch (myType)
	{
	    case IDENTITY_ADDED:       l.identityAdded (this);       break;
	    case IDENTITY_REMOVED:     l.identityRemoved (this);     break;
	    case SYSTEM_CONNECTED:     l.systemConnected (this);     break;
	    case SYSTEM_CONNECTING:    l.systemConnecting (this);    break;
	    case SYSTEM_DISCONNECTED:  l.systemDisconnected (this);  break;
	    case SYSTEM_DISCONNECTING: l.systemDisconnecting (this); break;
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
	return (listener instanceof SystemListener);
    }
}
