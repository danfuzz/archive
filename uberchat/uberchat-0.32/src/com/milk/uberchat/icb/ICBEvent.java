// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.icb;

import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This class is for representing some of the special packets that
 * an ICB server sends to the client. It's done as a <code>BaseEvent</code>
 * subclass because the form factor is just too similar to ignore.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class ICBEvent
extends BaseEvent
{
    /** type code for <code>disconnectPacket</code> event */
    public static final int DISCONNECT_PACKET = 0;

    /** type code for <code>errorPacket</code> event */
    public static final int ERROR_PACKET = 1;

    /** type code for <code>importantPacket</code> event */
    public static final int IMPORTANT_PACKET = 2;

    /** type code for <code>pingPacket</code> event */
    public static final int PING_PACKET = 3;

    /** type code for <code>pongPacket</code> event */
    public static final int PONG_PACKET = 4;

    /** type code for <code>registerPacket</code> event */
    public static final int REGISTER_PACKET = 5;

    /** type code for <code>whoPacket</code> event */
    public static final int WHO_PACKET = 6;

    /**
     * Construct an <code>ICBEvent<code> of the particular type for the
     * particular source.
     *
     * @param source the source system of the event
     * @param type the event type
     * @param argument the argument
     */
    private ICBEvent (ICBSystem source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>disconnectPacket</code> event.
     *
     * @param source the source of the event
     */
    public static ICBEvent disconnectPacket (ICBSystem source)
    {
	return new ICBEvent (source, DISCONNECT_PACKET, null);
    }

    /**
     * Construct and return an <code>errorPacket</code> event.
     *
     * @param source the source of the event
     * @param msg the error message
     */
    public static ICBEvent errorPacket (ICBSystem source, String msg)
    {
	return new ICBEvent (source, ERROR_PACKET, msg);
    }

    /**
     * Construct and return an <code>importantPacket</code> event.
     *
     * @param source the source of the event
     * @param msg the message
     */
    public static ICBEvent importantPacket (ICBSystem source, String msg)
    {
	return new ICBEvent (source, IMPORTANT_PACKET, msg);
    }

    /**
     * Construct and return a <code>pingPacket</code> event.
     *
     * @param source the source of the event
     */
    public static ICBEvent pingPacket (ICBSystem source)
    {
	return new ICBEvent (source, PING_PACKET, null);
    }

    /**
     * Construct and return a <code>pongPacket</code> event.
     *
     * @param source the source of the event
     */
    public static ICBEvent pongPacket (ICBSystem source)
    {
	return new ICBEvent (source, PONG_PACKET, null);
    }

    /**
     * Construct and return a <code>registerPacket</code> event.
     *
     * @param source the source of the event
     * @param msg the message
     */
    public static ICBEvent registerPacket (ICBSystem source, String msg)
    {
	return new ICBEvent (source, REGISTER_PACKET, msg);
    }

    /**
     * Construct and return a <code>whoPacket</code> event.
     *
     * @param source the source of the event
     * @param who the who info
     */
    public static ICBEvent registerPacket (ICBSystem source, ICBWho who)
    {
	return new ICBEvent (source, WHO_PACKET, who);
    }

    /**
     * Get the source of this event as an <code>ICBSystem</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ICBSystem</code> 
     */
    public ICBSystem getSystem ()
    {
	return (ICBSystem) source;
    }

    /**
     * Get the argument of this event as a message string. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a string
     */
    public String getMessage ()
    {
	return (String) myArgument;
    }

    /**
     * Get the argument of this event as an <code>ICBWho</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as an <code>ICBWho</code>
     */
    public ICBWho getWho ()
    {
	return (ICBWho) myArgument;
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
	    case DISCONNECT_PACKET: return "disconnect-packet";
	    case ERROR_PACKET:      return "error-packet";
	    case IMPORTANT_PACKET:  return "important-packet";
	    case PING_PACKET:       return "ping-packet";
	    case PONG_PACKET:       return "pong-packet";
	    case REGISTER_PACKET:   return "register-packet";
	    case WHO_PACKET:        return "who-packet";
	    default:                return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	ICBListener l = (ICBListener) listener;

	switch (myType)
	{
	    case DISCONNECT_PACKET: l.disconnectPacket (this); break;
	    case ERROR_PACKET:      l.errorPacket (this);      break;
	    case IMPORTANT_PACKET:  l.importantPacket (this);  break;
	    case PING_PACKET:       l.pingPacket (this);       break;
	    case PONG_PACKET:       l.pongPacket (this);       break;
	    case REGISTER_PACKET:   l.registerPacket (this);   break;
	    case WHO_PACKET:        l.whoPacket (this);        break;
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
	return (listener instanceof ICBListener);
    }
}
