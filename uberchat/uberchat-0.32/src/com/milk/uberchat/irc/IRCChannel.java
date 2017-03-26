// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.irc;

import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.base.BaseChannel;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.util.Vector;

/**
 * This is the ChatChannel class for IRC channels.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class IRCChannel
extends BaseChannel
{
    /** the system to use */
    private IRCSystem mySystem;

    /** the identity to use */
    private IRCIdentity myIdentity;

    /**
     * Construct an <code>IRCChannel</code> for the given system and
     * channel name.
     *
     * @param system the given system
     * @param name the given channel name 
     */
    /*package*/ IRCChannel (IRCSystem system, String name)
    {
	super (name, system.getIdentity (), "");
	mySystem = system;
	myIdentity = system.getIdentity ();
    }

    // ------------------------------------------------------------------------
    // ChatChannel interface methods

    /**
     * (Attempt to) set the topic of this channel.
     *
     * @param topic the new topic for the channel
     */
    public void setTopic (String topic)
    {
	mySystem.sendCommand ("topic", 
			      new String[] { getName (), topic });
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Update the list of users in this locus from the host.
     */
    public void updateUsers ()
    {
	ReplyTap tap = new ReplyTap()
	{
	    private Vector users = new Vector ();

	    synchronized public boolean handleReply (ServerReply rep)
	    {
		int code = (rep != null) 
		    ? rep.getReplyInt () 
		    : ServerReply.RPL_ENDOFWHO;

		if (code == ServerReply.RPL_ENDOFWHO)
		{
		    if (users != null)
		    {
			setFromUsers ();
		    }
		    return true;
		}
		else
		{
		    // args:
		    //   my-nickname
		    //   channel
		    //   userid
		    //   host
		    //   irc-server
		    //   nickname
		    //   status
		    //   hopcount real-name
		    String[] args = rep.getArguments ();
		    
		    IRCUser user = myIdentity.nickToUser (args[5]);
		    if (user == null)
		    {
			// ignore it
		    }
		    else if (users != null)
		    {
			users.addElement (user);
			if (users.size () > 20)
			{
			    setFromUsers ();
			}
		    }
		    else
		    {
			callAddUser (user);
		    }

		    return false;
		}
	    }

	    /** Helper to set the users from the <code>users</code>
	     * variable and then null it out. */
	    private void setFromUsers ()
	    {
		ChatUser[] uarr = new ChatUser[users.size ()];
		users.copyInto (uarr);
		users = null;
		callSetUsers (uarr);
	    }
	};

	mySystem.commandTap ("who",
			     new String[] { getName () },
			     new int[] { ServerReply.RPL_WHOREPLY,
					 ServerReply.RPL_ENDOFWHO },
			     tap);
    }

    /**
     * Speak in this locus.
     *
     * @param kind the kind of speech (see <code>SpeechKinds</code for details)
     * @param text the message text
     */
    public void speak (String kind, String text)
    {
	mySystem.speakTo (this, kind, text);
    }

    // ------------------------------------------------------------------------
    // Protected methods that we must override

    /**
     * <code>BaseChannel</code> calls this method when it actually wants to
     * try to do a channel join. It will only call this if the channel has
     * not in fact already been joined. 
     */
    protected final void channelJoin ()
    {
	mySystem.sendCommand ("join", new String[] { getName () });
    }

    /**
     * <code>BaseChannel</code> calls this method when it actually wants to
     * try to do a channel leave. It will only call this if the channel is
     * in fact currently joined. 
     */
    protected final void channelLeave ()
    {
	mySystem.sendCommand ("part", new String[] { getName () });
    }

    // ------------------------------------------------------------------------
    // Package methods 

    /**
     * Set the last known topic for this channel.
     *
     * @param topic the last known topic
     */
    /*package*/ final void callTopicChanged (String topic)
    {
	topicChanged (topic);
    }

    /**
     * Say that the identity is not in this channel.
     */
    /*package*/ final void callLeftChannel ()
    {
	// BaseChannel.leftChannel() is protected, which is why we need
	// this method
	leftChannel ();
    }

    /**
     * Tell the channel who is on it.
     *
     * @param users the users on the channel
     */
    /*package*/ final void callSetUsers (ChatUser[] users)
    {
	// BaseLocus.setUsers() is protected, which is why we need
	// this method
	setUsers (users);
    }

    /**
     * Call <code>BaseEntity.broadcast()</code> on a
     * <code>systemPrivate</code> event with the given text.
     *
     * @param text the message text
     */
    /*package*/ void systemPrivate (String text)
    {
	broadcast (MessageEvent.systemPrivate (this, text));
    }

    /**
     * Call <code>BaseEntity.broadcast()</code> on a
     * <code>systemBroadcast</code> event with the given text.
     *
     * @param text the message text
     */
    /*package*/ void systemBroadcast (String text)
    {
	broadcast (MessageEvent.systemBroadcast (this, text));
    }

    /**
     * Call <code>BaseEntity.broadcast()</code> on an event. This is
     * defined to give this package access to an otherwise-protected
     * method.
     *
     * @param ev the event to broadcast 
     */
    /*package*/ void callBroadcast (BaseEvent ev)
    {
	broadcast (ev);
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Tell the channel to add a user.
     *
     * @param user the user to add
     */
    private final void callAddUser (ChatUser user)
    {
	// there's a bug in javac which prevents an inner class calling
	// this method--it spits out a bogus error; so we have to resort
	// to doing this. Java just sucks that way sometimes.
	addUser (user);
    }
}
