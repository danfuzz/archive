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
import com.milk.objed.Editable;
import com.milk.objed.Editor;
import com.milk.objed.FieldValueEditor;
import com.milk.objed.FixedFieldsEditor;
import com.milk.objed.StringTextEditor;
import com.milk.objed.ValueEditor;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.event.ChannelEvent;
import com.milk.uberchat.event.ChannelListener;
import com.milk.util.BadArgumentException;

/**
 * This class contains <code>Command</code>s for <code>ChatChannel</code>
 * objects as inner classes and knows how to manage them.
 *
 * @see com.milk.uberchat.iface.ChatChannel
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ChannelCommands
{
    /** the <code>ChatChannel</code> to use */
    private ChatChannel myChannel;

    /** the listener that listens to the channel */
    private MyChannelListener myChannelListener;

    /** the <code>join()</code> command object */
    private Join myJoin;

    /** the <code>leave()</code> command object */
    private Leave myLeave;

    /** the <code>setTopic()</code> command object */
    private SetTopic mySetTopic;

    /** the (intentionally private) object to synchronize on */
    private Object mySynch;

    /**
     * Construct a <code>ChannelCommands</code> object. Constructing
     * the object automatically causes it to add a listener to the
     * specified channel, but it doesn't actually add any commands
     * (since, as a separate entity, it has no capability to add commands
     * to it).
     *
     * @param channel non-null; the channel to use
     */
    public ChannelCommands (ChatChannel channel)
    {
	myChannel = channel;
	myChannelListener = new MyChannelListener ();
	myJoin = new Join ();
	myLeave = new Leave ();
	mySetTopic = new SetTopic ();
	mySynch = myChannelListener; // good enough
	myChannel.addListener (myChannelListener);
    }

    /**
     * Get the commands that this object manages.
     *
     * @return an array of commands
     */
    public Command[] getCommands ()
    {
	return new Command[] { myJoin, myLeave, mySetTopic };
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the listener that listens to the channel object and
     * enables/disables the commands as appropriate.
     */
    private class MyChannelListener
    implements ChannelListener
    {
	public void joinedChannel (ChannelEvent event)
	{
	    synchronized (mySynch)
	    {
		myJoin.mySetEnabled (false);
		myLeave.mySetEnabled (true);
	    }
	}

	public void joiningChannel (ChannelEvent event)
	{
	    synchronized (mySynch)
	    {
		myJoin.mySetEnabled (false);
		myLeave.mySetEnabled (false);
	    }
	}

	public void leftChannel (ChannelEvent event)
	{
	    synchronized (mySynch)
	    {
		myJoin.mySetEnabled (true);
		myLeave.mySetEnabled (false);
	    }
	}

	public void leavingChannel (ChannelEvent event)
	{
	    synchronized (mySynch)
	    {
		myJoin.mySetEnabled (false);
		myLeave.mySetEnabled (false);
	    }
	}

	public void topicChanged (ChannelEvent event)
	{
	    // ignore it
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
     * This is the command associated with the <code>join()</code>
     * operation. 
     */
    private class Join
    extends MyCommand
    {
	public Join ()
	{
	    setLabel ("Join");
	    setDescription ("Join this channel.");
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
                    "ChannelCommands.Join.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument must be null.");
	    }
	    
	    myChannel.join ();
	    return null;
	}
    }

    /** 
     * This is the command associated with the <code>leave()</code>
     * operation. 
     */
    private class Leave
    extends MyCommand
    {
	public Leave ()
	{
	    setLabel ("Leave");
	    setDescription ("Leave this channel.");
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
                    "SystemCommands.Leave.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument must be null.");
	    }
	    
	    myChannel.leave ();
	    return null;
	}
    }

    /** 
     * This is the command associated with the
     * <code>SetTopic()</code> operation. 
     */
    private class SetTopic
    extends MyCommand
    {
	public SetTopic ()
	{
	    setLabel ("Set Topic...");
	    setDescription ("Set the topic of this channel.");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new SetTopicArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof SetTopicArgument))
	    {
		throw new BadArgumentException (
                    "ChannelCommands.SetTopic.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    SetTopicArgument arg = (SetTopicArgument) argument;
	    myChannel.setTopic (arg.myTopic);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>SetTopic</code>.
     */
    public static class SetTopicArgument
    implements Editable
    {
	public String myTopic = "";
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[1];

		fields[0] = 
		    new FieldValueEditor (
                        "topic",
			"the new topic",
			true, false, this, "myTopic");
		fields[0] = new StringTextEditor (fields[0]);

		myEditor = 
		    new FixedFieldsEditor (
                        "Set Topic",
			"Enter a new topic.",
			fields);
	    }

	    return myEditor;
	}
    }
}
