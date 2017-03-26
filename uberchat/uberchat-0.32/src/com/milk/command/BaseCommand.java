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

import com.milk.util.BaseEvent;
import com.milk.util.BugInSubclassException;
import com.milk.util.ListenerList;
import java.util.EventListener;

/**
 * This is a base class which implements most of the standard
 * <code>Command</code> functionality. It notably lacks public
 * <code>makeArgument()</code> and protected <code>commandRun()</code>,
 * which must be overridden to do reasonable things.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseCommand
implements Command
{
    /** the listener list to use */
    private ListenerList myListeners;

    /** the label */
    private String myLabel;

    /** the description */
    private String myDescription;

    /** the enabled state */
    private boolean myEnabled;

    /** the (intentionally-private) object to synch on */
    private Object mySynch;

    /**
     * Construct a <code>BaseCommand</code>. It initially has an empty
     * label and description, and is disabled.
     */
    public BaseCommand ()
    {
	myListeners = new ListenerList ();
	myLabel = "";
	myDescription = "";
	myEnabled = false;
	mySynch = myListeners; // good enough
    }

    // ------------------------------------------------------------------------
    // Command interface methods

    /**
     * Add a listener for this command.
     *
     * @param listener the listener to add 
     */
    public final void addListener (EventListener listener)
    {
	synchronized (mySynch)
	{
	    myListeners.add (listener);
	    CommandEvent.labelChanged (this, myLabel).sendTo (listener);
	    CommandEvent.descriptionChanged (this, myDescription).
		sendTo (listener);
	    CommandEvent.enabledChanged (this, myEnabled).sendTo (listener);
	}
    }

    /**
     * Remove a listener from this command that was previously added
     * with <code>addListener</code>.
     *
     * @param listener the listener to remove
     */
    public final void removeListener (EventListener listener)
    {
	synchronized (mySynch)
	{
	    myListeners.remove (listener);
	}
    }

    /**
     * Get the label of this command.
     *
     * @return non-null; the label
     */
    public final String getLabel ()
    {
	synchronized (mySynch)
	{
	    return myLabel;
	}
    }

    /**
     * Get the full description of this command.
     *
     * @return non-null; the description
     */
    public final String getDescription ()
    {
	synchronized (mySynch)
	{
	    return myDescription;
	}
    }

    /**
     * Return true if this command is currently enabled.
     *
     * @return true if this command is currently enabled
     */
    public final boolean isEnabled ()
    {
	synchronized (mySynch)
	{
	    return myEnabled;
	}
    }

    /**
     * Create and return an argument template for this command. Subclasses
     * must override this to return something appropriate.
     *
     * @return non-null; a new argument template for this command, or
     * null if the command has no arguments
     */
    public abstract Object makeArgument ();

    /**
     * Run the command with the given argument. The argument should be
     * an object that was previously returned from <code>makeArgument</code>.
     *
     * @param argument the argument of the command
     * @return null-ok; the return value of running the command
     * @exception DisabledCommandException thrown if the command is not
     * enabled at the time this method is called
     */
    public Object run (Object argument)
    throws DisabledCommandException
    {
	synchronized (mySynch)
	{
	    if (! myEnabled)
	    {
		throw new DisabledCommandException (this, argument);
	    }

	    return commandRun (argument);
	}
    }

    // ------------------------------------------------------------------------
    // Protected methods which must be overridden

    /**
     * Really run this command. <code>BaseCommand.run()</code> calls this,
     * after checking to make sure the command is enabled. That is, this
     * method is only ever called on an enabled command. Note that
     * synchronization in this class ensures that only one thread at a time
     * is in a particular object's <code>commandRun()</code> method.
     *
     * @param argument the argument of the command
     * @return null-ok; the return value of running the command 
     */
    protected abstract Object commandRun (Object argument);

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Set the label of this command. If it is in fact different from the
     * old label, then a <code>labelChanged</code> event is sent to all
     * appropriate listeners.
     *
     * @param label null-ok; the new label
     */
    protected final void setLabel (String label)
    {
	synchronized (mySynch)
	{
	    if (label == null)
	    {
		label = "";
	    }

	    if (label.equals (myLabel))
	    {
		// easy out--it didn't change
		return;
	    }
	    
	    myLabel = label;
	    broadcast (CommandEvent.labelChanged (this, label));
	}
    }

    /**
     * Set the description of this command. If it is in fact different from
     * the old description, then a <code>descriptionChanged</code> event is
     * sent to all appropriate listeners.
     *
     * @param description null-ok; the new description 
     */
    protected final void setDescription (String description)
    {
	synchronized (mySynch)
	{
	    if (description == null)
	    {
		description = "";
	    }
	    
	    if (description.equals (myDescription))
	    {
		// easy out--it didn't change
		return;
	    }
	    
	    myDescription = description;
	    broadcast (CommandEvent.descriptionChanged (this, description));
	}
    }

    /**
     * Set the enabled state of this command. If it is in fact different
     * from the old state, then an <code>enabledChanged</code> event is
     * sent to all appropriate listeners.
     *
     * @param enabled the new enabled state 
     */
    protected final void setEnabled (boolean enabled)
    {
	synchronized (mySynch)
	{
	    if (enabled == myEnabled)
	    {
		// easy out--it didn't change
		return;
	    }

	    myEnabled = enabled;
	    broadcast (CommandEvent.enabledChanged (this, enabled));
	}
    }

    /**
     * Broadcast an event to all the listeners of this object.
     *
     * @param event the event to broadcast
     */
    protected final void broadcast (BaseEvent event)
    {
	synchronized (mySynch)
	{
	    if (event.getSource () != this)
	    {
		throw new BugInSubclassException (
                    "BaseCommand.broadcast() called with a " +
		    "different-sourced event:\n" + event);
	    }

	    myListeners.checkedBroadcast (event);
	}
    }
}
