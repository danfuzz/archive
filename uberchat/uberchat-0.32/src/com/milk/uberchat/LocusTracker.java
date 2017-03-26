// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat;

import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.event.MessageListener;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.util.ListenerList;
import java.util.Vector;

/**
 * This class keeps useful state for a locus, like keeping a log of
 * messages. <code>LocusTracker</code> relies on its creator to hand it
 * messages; it doesn't actually hook itself up to anything.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class LocusTracker
{
    /** the locus that this object tracks */
    private ChatLocus myLocus;

    /** the maximum number of messages to keep in the log */
    private int myMaxMessages;

    /** the messages in the log */
    private Vector myMessages;

    /** the listeners to send messages to */
    private ListenerList myListeners;

    /** the (intentionally-private) object to use for synchronization */
    private Object mySynch;

    /**
     * Construct a <code>LocusTracker</code> object for a particular locus.
     *
     * @param locus the locus for this tracker
     * @param maxMessages the maximum number of messages to keep in the log
     */
    public LocusTracker (ChatLocus locus, int maxMessages)
    {
	myLocus = locus;
	myMaxMessages = maxMessages;
	myMessages = new Vector ();
	myListeners = new ListenerList ();
	mySynch = new Object ();
    }

    /**
     * Add a listener. The given listener will get told of any new messages
     * that get sent to this <code>LocusTracker</code>, but first it will
     * get sent all the logged messages.
     *
     * @param listener the listener to add
     */
    public void addListener (MessageListener listener)
    {
	synchronized (mySynch)
	{
	    myListeners.add (listener);

	    int sz = myMessages.size ();
	    for (int i = 0; i < sz; i++)
	    {
		((MessageEvent) myMessages.elementAt (i)).sendTo (listener);
	    }
	}
    }

    /**
     * Remove a listener that was previously added with
     * <code>addListener()</code>.
     *
     * @param listener the listener to remove
     */
    public void removeListener (MessageListener listener)
    {
	synchronized (mySynch)
	{
	    myListeners.remove (listener);
	}
    }

    /**
     * Add a message to the log. This will cause it to be sent to
     * any listeners as well.
     *
     * @param message the message to add
     */
    public void addMessage (MessageEvent message)
    {
	synchronized (mySynch)
	{
	    myMessages.addElement (message);

	    if (myMessages.size () > myMaxMessages)
	    {
		myMessages.removeElementAt (0);
	    }

	    myListeners.broadcast (message);
	}
    }

    /**
     * Return true if this tracker has any listeners.
     *
     * @return true if this tracker has any listeners
     */
    public boolean hasListeners ()
    {
	return (! myListeners.isEmpty ());
    }
}
