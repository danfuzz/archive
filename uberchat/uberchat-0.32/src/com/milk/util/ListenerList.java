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

/**
 * This is a handy class for holding a list of zero or more
 * <code>EventListener</code>s. It's fairly efficient for storage, and it
 * knows how to do broadcasting with the help of the <code>BaseEvent</code>
 * class, which it is designed to work with.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ListenerList
{
    /** the array of listeners */
    private EventListener[] myListeners;

    /** the count of listeners */
    private int myCount;

    /**
     * Construct an initially-empty <code>ListenerList</code>.
     */
    public ListenerList ()
    {
	myListeners = null;
	myCount = 0;
    }

    /**
     * Add a listener to the list.
     *
     * @param listener the listener to add
     */
    public void add (EventListener listener)
    {
	if (myListeners == null)
	{
	    // create the initial array
	    myListeners = new EventListener[2];
	}
	else if (myCount == myListeners.length)
	{
	    // grow the array
	    EventListener[] newArr = new EventListener[myCount * 3 / 2];
	    System.arraycopy (myListeners, 0, newArr, 0, myCount);
	    myListeners = newArr;
	}

	myListeners[myCount] = listener;
	myCount++;
    }

    /**
     * Remove a listener from the list.
     *
     * @param listener the listener to remove
     * @return true if the listener was in the list; false if it wasn't.
     */
    public boolean remove (EventListener listener)
    {
	if (myCount == 0)
	{
	    // easy out
	    return false;
	}

	int at;
	for (at = 0; at < myCount; at++)
	{
	    if (myListeners[at] == listener)
	    {
		break;
	    }
	}

	if (at == myCount)
	{
	    // not found
	    return false;
	}

	if (myCount == 1)
	{
	    // special case for removing the last element
	    myListeners = null;
	    myCount = 0;
	    return true;
	}

	// decrease the count and move the ex-last-element to the emptied
	// slot; note that it's effectively a nop if the element to remove
	// is the last
	myCount--;
	myListeners[at] = myListeners[myCount];

	// be nice to the gc
	myListeners[myCount] = null;

	if ((myCount * 4) < myListeners.length)
	{
	    // list shrunk a lot; resize it to be memory-friendly
	    EventListener[] newArr = new EventListener[myCount];
	    System.arraycopy (myListeners, 0, newArr, 0, myCount);
	    myListeners = newArr;
	}

	return true;
    }

    /**
     * Return true if this list has no elements.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty ()
    {
	return (myCount == 0);
    }

    /**
     * Broadcast a <code>BaseEvent</code> to all the listeners unconditionally.
     *
     * @param event the event to broadcast
     */
    public void broadcast (BaseEvent event)
    {
	for (int i = 0; i < myCount; i++)
	{
	    event.sendTo (myListeners[i]);
	}
    }

    /**
     * Broadcast a <code>BaseEvent</code> to all the listeners that the
     * event thinks it can be sent to (queried with
     * <code>canSendTo()</code>).
     *
     * @param event the event to broadcast 
     */
    public void checkedBroadcast (BaseEvent event)
    {
	for (int i = 0; i < myCount; i++)
	{
	    EventListener l = myListeners[i];
	    if (event.canSendTo (l))
	    {
		event.sendTo (l);
	    }
	}
    }
}
