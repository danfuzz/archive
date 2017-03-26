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

import com.milk.uberchat.iface.ChatEntity;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with errors that
 * need to be reported to the user.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ErrorEvent
extends BaseEvent
{
    /** type code for <code>errorReport</code> event */
    public static final int ERROR_REPORT = 0;

    /** type code for <code>bugReport</code> event */
    public static final int BUG_REPORT = 1;

    /**
     * Construct an <code>ErrorEvent</code> of the particular type for the
     * particular source.
     *
     * @param source the source entity of the error
     * @param type the event type
     * @param argument the argument
     */
    private ErrorEvent (ChatEntity source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return an <code>errorReport</code> event.
     *
     * @param source the source of the error
     * @param msg the error message
     */
    public static ErrorEvent errorReport (ChatEntity source, String msg)
    {
	return new ErrorEvent (source, ERROR_REPORT, msg);
    }

    /**
     * Construct and return an <code>bugReport</code> event.
     *
     * @param source the source of the error
     * @param ex an exception associated with the bug
     */
    public static ErrorEvent bugReport (ChatEntity source, Throwable ex)
    {
	return new ErrorEvent (source, BUG_REPORT, ex);
    }

    /**
     * Get the source of this event as a <code>ChatEntity</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatEntity</code> 
     */
    public ChatEntity getEntity ()
    {
	return (ChatEntity) source;
    }

    /**
     * Get the argument of this event as a <code>String</code> message. It
     * is merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a <code>String</code> 
     */
    public String getMessage ()
    {
	return (String) myArgument;
    }

    /**
     * Get the argument of this event as a <code>Throwable</code>. It
     * is merely a convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a <code>Throwable</code> 
     */
    public Throwable getException ()
    {
	return (Throwable) myArgument;
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
	    case ERROR_REPORT: return "error-report";
	    case BUG_REPORT:   return "bug-report";
	    default:           return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	ErrorListener l = (ErrorListener) listener;

	switch (myType)
	{
	    case ERROR_REPORT: l.errorReport (this); break;
	    case BUG_REPORT:   l.bugReport (this);   break;
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
	return (listener instanceof ErrorListener);
    }
}
