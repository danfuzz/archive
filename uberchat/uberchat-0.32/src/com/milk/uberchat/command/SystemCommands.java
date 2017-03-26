// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.command;

import com.milk.command.BaseCommand;
import com.milk.command.Command;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.event.SystemEvent;
import com.milk.uberchat.event.SystemListener;
import com.milk.util.BadArgumentException;

/**
 * This class contains <code>Command</code>s for <code>ChatSystem</code>
 * objects as inner classes and knows how to manage them.
 *
 * @see com.milk.uberchat.iface.ChatSystem
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class SystemCommands
{
    /** the <code>ChatSystem</code> to use */
    private ChatSystem mySystem;

    /** the listener that listens to the system */
    private MySystemListener mySystemListener;

    /** the <code>connect()</code> command object */
    private Connect myConnect;

    /** the <code>disconnect()</code> command object */
    private Disconnect myDisconnect;

    /** the (intentionally private) object to synchronize on */
    private Object mySynch;

    /**
     * Construct a <code>SystemCommands</code> object. Constructing
     * the object automatically causes it to add a listener to the
     * specified system, but it doesn't actually add any commands
     * (since, as a separate entity, it has no capability to add commands
     * to it).
     *
     * @param system non-null; the system to use
     */
    public SystemCommands (ChatSystem system)
    {
	mySystem = system;
	mySystemListener = new MySystemListener ();
	myConnect = new Connect ();
	myDisconnect = new Disconnect ();
	mySynch = mySystemListener; // good enough
	mySystem.addListener (mySystemListener);
    }

    /**
     * Get the commands that this object manages.
     *
     * @return an array of commands
     */
    public Command[] getCommands ()
    {
	return new Command[] { myConnect, myDisconnect };
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the listener that listens to the system object and
     * enables/disables the commands as appropriate.
     */
    private class MySystemListener
    implements SystemListener
    {
	public void identityAdded (SystemEvent event)
	{
	    // ignore it
	}

	public void identityRemoved (SystemEvent event)
	{
	    // ignore it
	}

	public void systemConnected (SystemEvent event)
	{
	    synchronized (mySynch)
	    {
		myConnect.mySetEnabled (false);
		myDisconnect.mySetEnabled (true);
	    }
	}

	public void systemConnecting (SystemEvent event)
	{
	    synchronized (mySynch)
	    {
		myConnect.mySetEnabled (false);
		myDisconnect.mySetEnabled (false);
	    }
	}

	public void systemDisconnected (SystemEvent event)
	{
	    synchronized (mySynch)
	    {
		myConnect.mySetEnabled (true);
		myDisconnect.mySetEnabled (false);
	    }
	}

	public void systemDisconnecting (SystemEvent event)
	{
	    synchronized (mySynch)
	    {
		myConnect.mySetEnabled (false);
		myDisconnect.mySetEnabled (false);
	    }
	}
    }

    /**
     * This is the superclass of the commands defined in this class.
     */
    private abstract class MyCommand
    extends BaseCommand
    {
	/**
	 * Set the enabled status of the command. This method exists
	 * merely to export an otherwise-protected method. We shouldn't
	 * have to do this--the method should be available without
	 * these shenanigans, but Java just sucks that way sometimes.
	 *
	 * @param enabled the enabled value
	 */
	/*package*/ void mySetEnabled (boolean enabled)
	{
	    setEnabled (enabled);
	}
    }

    /** 
     * This is the command associated with the <code>connect()</code>
     * operation. 
     */
    private class Connect
    extends MyCommand
    {
	public Connect ()
	{
	    setLabel ("Connect");
	    setDescription ("Connect to this system.");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return null;
	}

	protected Object commandRun (Object argument)
	{
	    if (argument != null)
	    {
		throw new BadArgumentException (
                    "SystemCommands.Connect.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument must be null.");
	    }
	    
	    mySystem.connect ();
	    return null;
	}
    }

    /** 
     * This is the command associated with the <code>disconnect()</code>
     * operation. 
     */
    private class Disconnect
    extends MyCommand
    {
	public Disconnect ()
	{
	    setLabel ("Disconnect");
	    setDescription ("Disconnect from this system.");
	    setEnabled (false);
	}

	public Object makeArgument ()
	{
	    return null;
	}

	protected Object commandRun (Object argument)
	{
	    if (argument != null)
	    {
		throw new BadArgumentException (
                    "SystemCommands.Disconnect.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument must be null.");
	    }
	    
	    mySystem.disconnect ();
	    return null;
	}
    }
}
