// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.command;

import com.milk.util.ListenerList;
import java.util.EventListener;

/**
 * This is a handy class for holding a list of zero or more
 * <code>Command</code>s. It implements the <code>Commandable</code>
 * interface as a means of getting to the list, not for actually commanding
 * this object (which isn't user-commandable).
 *
 * <p>BUG--<code>myListeners</code> ought to be sharable. This means that
 * we actually need to have a reasonable <code>ListenerDelegate</code>-type
 * class which, e.g., knows about the set of initial events to send to
 * new listeners.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class CommandList
implements Commandable
{
    /** the array of commands */
    private Command[] myCommands;

    /** the listeners to this object */
    private ListenerList myListeners;

    /** the (intentionally-private) object to synchronize on */
    private Object mySynch;

    /** the count of commands */
    private int myCount;

    /**
     * Construct an initially-empty <code>CommandList</code>.
     */
    public CommandList ()
    {
	myCommands = null;
	myListeners = new ListenerList ();
	mySynch = myListeners; // good enough
	myCount = 0;
    }

    /**
     * Add a command to the list.
     *
     * @param command the command to add
     */
    public void add (Command command)
    {
	synchronized (mySynch)
	{
	    if (myCommands == null)
	    {
		// create the initial array
		myCommands = new Command[2];
	    }
	    else if (myCount == myCommands.length)
	    {
		// grow the array
		Command[] newArr = new Command[myCount * 3 / 2];
		System.arraycopy (myCommands, 0, newArr, 0, myCount);
		myCommands = newArr;
	    }

	    myCommands[myCount] = command;
	    myCount++;

	    myListeners.checkedBroadcast (
	        CommandableEvent.commandAdded (this, command));
	}
    }

    /**
     * Remove a command from the list.
     *
     * @param command the command to remove
     * @return true if the command was in the list; false if it wasn't.
     */
    public boolean remove (Command command)
    {
	synchronized (mySynch)
	{
	    if (myCount == 0)
	    {
		// easy out
		return false;
	    }

	    int at;
	    for (at = 0; at < myCount; at++)
	    {
		if (myCommands[at] == command)
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
		myCommands = null;
		myCount = 0;
	    }
	    else
	    {
		// decrease the count and move the ex-last-element to the
		// emptied slot; note that it's effectively a nop if the
		// element to remove is the last
		myCount--;
		myCommands[at] = myCommands[myCount];

		// be nice to the gc
		myCommands[myCount] = null;
		
		if ((myCount * 4) < myCommands.length)
		{
		    // list shrunk a lot; resize it to be memory-friendly
		    Command[] newArr = new Command[myCount];
		    System.arraycopy (myCommands, 0, newArr, 0, myCount);
		    myCommands = newArr;
		}
	    }

	    myListeners.checkedBroadcast (
	        CommandableEvent.commandRemoved (this, command));
	    return true;
	}
    }

    /**
     * Return true if this list has no elements.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty ()
    {
	synchronized (mySynch)
	{
	    return (myCount == 0);
	}
    }

    /**
     * Add an array of commands to the list. It is no different than
     * calling <code>add()</code> on each command individually.
     *
     * @param commands the commands to add
     */
    public void add (Command[] commands)
    {
	for (int i = 0; i < commands.length; i++)
	{
	    add (commands[i]);
	}
    }

    // ------------------------------------------------------------------------
    // Commandable interface methods

    /**
     * Add a listener for this object.
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener)
    {
	synchronized (mySynch)
	{
	    myListeners.add (listener);
	    if (listener instanceof CommandableListener)
	    {
		for (int i = 0; i < myCount; i++)
		{
		    CommandableEvent.commandAdded (this, myCommands[i]).
			sendTo (listener);
		}
	    }
	}
    }

    /**
     * Remove a listener from this command that was previously added
     * with <code>addListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeListener (EventListener listener)
    {
	synchronized (mySynch)
	{
	    myListeners.remove (listener);
	}
    }

    /**
     * Get the user commands that may be used with this object.
     *
     * @return the array of commands
     */
    public Command[] getCommands ()
    {
	synchronized (mySynch)
	{
	    Command[] result = new Command[myCount];
	    if (myCount != 0)
	    {
		System.arraycopy (myCommands, 0, result, 0, myCount);
	    }
	    return result;
	}
    }
}
