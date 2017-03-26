// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.base;

import com.milk.command.Command;
import com.milk.command.CommandList;
import com.milk.command.CommandableEvent;
import com.milk.command.CommandableListener;
import com.milk.uberchat.event.EntityEvent;
import com.milk.uberchat.event.EntityListener;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.iface.ChatEntity;
import com.milk.util.ListenerList;
import com.milk.util.BaseEvent;
import com.milk.util.BugInSubclassException;
import java.util.EventListener;

/**
 * This is an abstract base class that provides a lot of what
 * all <code>ChatEntity</code> implementors need.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class BaseEntity
implements ChatEntity
{
    /** the (stable) name of this object */
    private String myName;

    /** the (stable) canonical name of this object */
    private String myCanonicalName;

    /** the (mutable) description of this object */
    private String myDescription;

    /** the list of listeners for this object */
    private ListenerList myListeners;

    /** the list of uber-listeners for this object */
    private ListenerList myUberListeners;

    /** the list of commands for this object */
    private CommandList myCommands;

    /** whether to debug this system. It is <code>false</code> by default,
     * and merely setting it to true will cause much debugging output
     * to be spewed to <code>System.err</code>. */
    protected boolean myDebug;

    /**
     * Construct a <code>BaseEntity</code>.
     *
     * @param name the name for this object
     * @param canonicalName the canonical name for this object
     * @param description the initial description for this object
     */
    public BaseEntity (String name, String canonicalName, String description)
    {
	myName = name;
	myCanonicalName = canonicalName;
	myDescription = description;
	myListeners = new ListenerList ();
	myUberListeners = new ListenerList ();
	myCommands = new CommandList ();
	myDebug = false;
    }

    /**
     * This just returns the class name and current description, for
     * ease of debugging.
     *
     * @return the string form of this object
     */
    public String toString ()
    {
	String className = getClass ().getName ();
	int lastDot = className.lastIndexOf ('.');
	if (lastDot != -1)
	{
	    className = className.substring (lastDot + 1);
	}

	return "{" + className + ": " + myDescription + "}";
    }

    // ------------------------------------------------------------------------
    // ChatEntity interface methods

    /**
     * Add a listener to this entity.
     *
     * @param listener the listener to add
     */
    public final void addListener (EventListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".addEntityListener (" + 
			  listener + ")");
	}

	myListeners.add (listener);
	if (listener instanceof CommandableListener)
	{
	    sendInitialEvents ((CommandableListener) listener);
	}
	listenerAdded (listener);
    }

    /**
     * Remove a listener from this entity.
     *
     * @param listener the listener to remove
     */
    public final void removeListener (EventListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".removeEntityListener (" + 
			  listener + ")");
	}

	myListeners.remove (listener);
    }

    /**
     * Add an uber-listener to this entity.
     *
     * @param listener the listener to add
     */
    public final void addUberListener (EventListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".addUberListener (" + 
			  listener + ")");
	}

	myUberListeners.add (listener);
	uberListenerAdded (listener);
    }

    /**
     * Remove an uber-listener from this entity.
     *
     * @param listener the listener to remove
     */
    public final void removeUberListener (EventListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".removeUberListener (" + 
			  listener + ")");
	}

	myUberListeners.remove (listener);
    }

    /**
     * Get the short descriptive name of this object.
     *
     * @return the name of the object 
     */
    public final String getName ()
    {
	return myName;
    }

    /**
     * Get the canonical name of this object.
     *
     * @return the canonical name of the object 
     */
    public final String getCanonicalName ()
    {
	return myCanonicalName;
    }

    /**
     * Get a verbose string description of this object.
     *
     * @return the verbose string description
     */
    public final String getDescription ()
    {
	return myDescription;
    }

    // ------------------------------------------------------------------------
    // Commandable interface methods

    /**
     * Get the user commands that may be used with this entity.
     *
     * @return the array of commands
     */
    public Command[] getCommands ()
    {
	return myCommands.getCommands ();
    }

    // ------------------------------------------------------------------------
    // Protected methods for subclasses to override

    /**
     * <code>BaseEntity</code> calls this method when an
     * <code>EventListener</code> has been added to this object (but not an
     * uber-listener). By default it does nothing, but subclasses can
     * override it to do any special processing they need to do, such as
     * sending initial messages into the listener. Overriding methods must
     * call <code>super.listenerAdded()</code>.
     *
     * @param listener the listener that was added 
     */
    protected void listenerAdded (EventListener listener)
    {
	// this space intentionally left blank
    }

    /**
     * <code>BaseEntity</code> calls this method when an uber-listener has
     * been added. By default it does nothing, but subclasses can override
     * it to do any special processing they need to do, such as sending
     * initial messages into the listener and informing their children that
     * they have a new uber-listener. Overriding methods must call
     * <code>super.uberListenerAdded()</code>.
     *
     * @param listener the listener that was added 
     */
    protected void uberListenerAdded (EventListener listener)
    {
	// this space intentionally left blank
    }

    /**
     * <code>BaseEntity</code> calls this when the name and/or canonical
     * name of this object changes. It is called after the instance
     * variables are set but before any events get sent out. By default it
     * does nothing, but subclasses can override it to do any special
     * processing they need to do. Overriding methods must call
     * <code>super.nameChanged()</code>.
     */
    protected void nameChanged ()
    {
	// this space intentionally left blank
    }

    /**
     * <code>BaseEntity</code> calls this when the description of this
     * object changes. It is called after the instance variable is set but
     * before any events get sent out. By default it does nothing, but
     * subclasses can override it to do any special processing they need to
     * do. Overriding methods must call
     * <code>super.descriptionChanged()</code>. 
     */
    protected void descriptionChanged ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Add a command to this entity.
     *
     * @param command the command to add
     */
    protected final void addCommand (Command command)
    {
	myCommands.add (command);
	// BUG--remove this when CommandList properly shares listeners
	broadcast (CommandableEvent.commandAdded (this, command));
    }

    /**
     * Add an array of commands to this entity.
     *
     * @param commands null-ok; the commands to add
     */
    protected final void addCommands (Command[] commands)
    {
	if (commands == null)
	{
	    return;
	}

	myCommands.add (commands);
	// BUG--remove this when CommandList properly shares listeners
	for (int i = 0; i < commands.length; i++)
	{
	    broadcast (CommandableEvent.commandAdded (this, commands[i]));
	}
    }

    /**
     * Remove a command from this entity that was previously added with
     * <code>addCommand()</code>.
     *
     * @param command the command to remove
     */
    protected final void removeCommand (Command command)
    {
	boolean removed = myCommands.remove (command);
	// BUG--remove this when CommandList properly shares listeners
	if (removed)
	{
	    broadcast (CommandableEvent.commandRemoved (this, command));
	}
    }

    /**
     * Set the description of this entity. If it is different than the
     * current description, then listeners will get notified of the change.
     *
     * @param description the new description 
     */
    protected final void setDescription (String description)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".setDescription (" + 
			  description + ")");
	}

	if (description.equals (myDescription))
	{
	    return;
	}

	myDescription = description;
	descriptionChanged ();
	broadcast (
            EntityEvent.descriptionChanged (this, description));
    }

    /**
     * Set the name and canonical name of this entity. If either is
     * different than their current values, then listeners will get
     * notified of the change.
     *
     * @param name the new name
     * @param canonicalName the new canonical name 
     */
    protected final void setName (String name, String canonicalName)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".setName (" + 
			  name + ", " + canonicalName + ")");
	}

	if (   name.equals (myName)
	    && (canonicalName == myCanonicalName))
	{
	    // easy out; they didn't actually change
	    return;
	}

	myName = name;
	myCanonicalName = canonicalName;
	nameChanged ();
	broadcast (
            EntityEvent.nameChanged (this, name, canonicalName));
    }

    /**
     * Broadcast an event to the listeners of this entity. This first calls
     * <code>uberBroadcast()</code> to give subclasses a chance to do
     * something interesting and then sends to all the directly registered
     * listeners.
     *
     * @param event the event to send 
     */
    protected final void broadcast (BaseEvent event)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".broadcast (" + 
			  event + ")");
	}

	if (event.getSource () != this)
	{
	    broadcast (
                ErrorEvent.bugReport (
                    this,
		    new BugInSubclassException (
                        "BaseEntity.broadcast() called with a " +
			"different-sourced event:\n" + event)));
	}

	uberBroadcast (event);
	myListeners.checkedBroadcast (event);
    }

    /**
     * Broadcast an event to the appropriate uber-listeners of this entity.
     * Subclasses may override this to send to additional listeners or
     * perform needed actions, but overriding methods must call
     * <code>super.uberBroadcast()</code>. Java has no way to enforce this,
     * however. It just sucks that way sometimes.
     *
     * @param event the event to send 
     */
    protected void uberBroadcast (BaseEvent event)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".uberBroadcast (" + 
			  event + ")");
	}

	myUberListeners.checkedBroadcast (event);
    }

    /**
     * Print a debug message to the console. This does nothing if the
     * <code>myDebug</code> variable is set to <code>false</code>.
     *
     * @param msg the message to print
     */
    protected final void debugPrint (String msg)
    {
	if (myDebug)
	{
	    System.err.print (msg);
	}
    }

    /**
     * Print a debug message to the console, with a newline at the end.
     * This does nothing if the <code>myDebug</code> variable is set to
     * <code>false</code>.
     *
     * @param msg the message to print 
     */
    protected final void debugPrintln (String msg)
    {
	if (myDebug)
	{
	    System.err.println (msg);
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Send the initial set of events to a newly-added listener. BUG--this
     * method shouldn't be necessary; remove it when <code>CommandList</code>
     * can share its listeners with this object.
     *
     * @param listener the listener that was added
     */
    private void sendInitialEvents (CommandableListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".sendInitialEvents (" + 
			  listener + ")");
	}

	// send out commandAdded event for all the commands
	Command[] cmds = myCommands.getCommands ();
	for (int i = 0; i < cmds.length; i++)
	{
	    CommandableEvent.commandAdded (this, cmds[i]).sendTo (listener);
	}
    }
}
