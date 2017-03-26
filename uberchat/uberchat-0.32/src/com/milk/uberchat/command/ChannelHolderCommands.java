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
import com.milk.uberchat.iface.ChatChannelHolder;
import com.milk.util.BadArgumentException;

/**
 * This class contains <code>Command</code>s for
 * <code>ChatChannelHolder</code> objects as inner classes and knows how to
 * manage them.
 *
 * @see com.milk.uberchat.iface.ChatChannelHolder
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class ChannelHolderCommands
{
    /** the <code>ChatChannelHolder</code> to use */
    private ChatChannelHolder myChannelHolder;

    /** the <code>updateChannels()</code> command object */
    private UpdateChannels myUpdateChannels;

    /**
     * Construct a <code>ChannelHolderCommands</code> object. Constructing
     * the object doesn't actually add any commands (since, as a separate
     * entity, it has no capability to add commands to it).
     *
     * @param channelHolder non-null; the channel holder to use 
     */
    public ChannelHolderCommands (ChatChannelHolder channelHolder)
    {
	myChannelHolder = channelHolder;
	myUpdateChannels = new UpdateChannels ();
    }

    /**
     * Get the commands that this object manages.
     *
     * @return an array of commands
     */
    public Command[] getCommands ()
    {
	return new Command[] { myUpdateChannels };
    }

    // ------------------------------------------------------------------------
    // Private helper classes

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
     * This is the command associated with the <code>updateChannels()</code>
     * operation. 
     */
    private class UpdateChannels
    extends MyCommand
    {
	public UpdateChannels ()
	{
	    setLabel ("Update Channels");
	    setDescription ("Update the channels in this locus.");
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
                    "ChannelHolderCommands.UpdateChannels.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument must be null.");
	    }
	    
	    myChannelHolder.updateChannels ();
	    return null;
	}
    }
}
