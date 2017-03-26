// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.util;

import java.util.EventListener;
import java.util.EventObject;

/**
 * This is the milk-standard extension to <code>EventObject</code>. It adds
 * type and argument fields, a <code>sendTo()</code> method, and a couple
 * other goodies.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
abstract public class BaseEvent
extends EventObject
{
    /** the type tag */
    protected final int myType;

    /** the argument */
    protected final Object myArgument;

    /** the timestamp */
    private final long myTimeStamp;

    /**
     * Construct a <code>BaseEvent</code>.
     *
     * @param source the source of the event
     * @param type the type of the event
     * @param argument the argument of the event
     */
    public BaseEvent (Object source, int type, Object argument)
    {
	super (source);
	myType = type;
	myArgument = argument;
	myTimeStamp = System.currentTimeMillis ();
    }

    /**
     * Get the type of the event.
     *
     * @return the type
     */
    public final int getType ()
    {
	return myType;
    }

    /**
     * Get the type of the event as a string.
     *
     * @return the type string
     */
    public final String getTypeString ()
    {
	return typeToString (myType);
    }

    /**
     * Get the argment of the event.
     *
     * @return the argument
     */
    public final Object getArgument ()
    {
	return myArgument;
    }

    /**
     * Get the time stamp of the event. The time stamp is the moment
     * that the event was created, and is returned as a standard Java
     * time (milliseconds since whatever the hell that base date is...
     * 1-Jan-1970 midnight GMT or something like that).
     *
     * @return the time stamp of the event (msec since the base date)
     */
    public final long getTimeStamp ()
    {
	return myTimeStamp;
    }

    /**
     * Get the string form of this event.
     *
     * @return the string form
     */
    public final String toString ()
    {
	String result = "{" + getClass ().getName () + " " + source + " " +
	    typeToString (myType);

	if (myArgument != null)
	{
	    result += " " + myArgument;
	}

	result += "}";

	return result;
    }

    /**
     * Turn the given type code into a string. Subclasses need to override
     * this method. It <i>really</i> wants to be an abstract <i>class</i>
     * (not <i>instance</i>) method, but Java doesn't do that. It just
     * sucks that way sometimes.
     *
     * @param type the type code to translate
     * @return the type as a string 
     */
    protected abstract String typeToString (int type);

    /**
     * Send this event to the given listener. Subclasses need to override
     * this method, implementing it to cast the listener to an appropriate
     * class and calling the appropriate method on it.
     *
     * @param listener the listener to send to
     */
    public abstract void sendTo (EventListener listener);

    /**
     * Return true if this event is appropiate for the given listener.
     * Subclasses need to override this method, implementing it to do the
     * appropriate <code>instanceof</code> check, or whatever else it needs
     * to do to determine if the listener and event match.
     *
     * @param listener the listener to check
     * @return true if the listener listens to this kind of event 
     */
    public abstract boolean canSendTo (EventListener listener);
}
