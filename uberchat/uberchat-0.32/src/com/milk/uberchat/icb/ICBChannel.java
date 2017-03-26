// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.icb;

import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.base.BaseChannel;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.util.Vector;

/**
 * This is the ChatChannel class for ICB channels.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class ICBChannel
extends BaseChannel
{
    /** the system to use */
    private ICBSystem mySystem;

    /**
     * Construct an ICBChannel for the given system and channel name.
     *
     * @param system the given system
     * @param name the given channel name
     */
    /*package*/ ICBChannel (ICBSystem system, String name)
    {
	super (name, system.getIdentity (), "");
	mySystem = system;
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
        if (getJoinedState () == JOINED)
        {
            mySystem.sendPacket ('h', new String[] { "topic", topic });
        }
        else
        {
	    broadcast (
                ErrorEvent.errorReport (
                    this,
		    "ICB only allows topic setting on channels you have " +
		    "joined."));
        }
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Update the list of users in this locus from the host.
     */
    public void updateUsers ()
    {
	ICBIdentity ident = mySystem.getIdentity ();
	String chanToSend = (getJoinedState () == JOINED) ? "." : getName ();
	Vector uvec = ident.doWhoCommand (chanToSend);
	ChatUser[] users = new ChatUser[uvec.size ()];
	uvec.copyInto (users);
	setUsers (users);
    }

    /**
     * Speak in this locus.
     *
     * @param kind the kind of speech (see <code>SpeechKinds</code for details)
     * @param text the message text
     */
    public void speak (String kind, String text)
    {
	if (   kind.equals (SpeechKinds.RAW) 
	    || kind.equals (SpeechKinds.RAW_NO_NL))
	{
	    mySystem.rawSend (text);
	    return;
	}

	if (getJoinedState () != JOINED)
	{
	    broadcast (
                ErrorEvent.errorReport (
                    this,
		    "ICB only allows speaking on channels you have joined."));
	    return;
	}

	while (text != null)
	{
	    int nlAt = text.indexOf ('\n');
	    String line;
	    if (nlAt == -1)
	    {
		line = text;
		text = null;
	    }
	    else
	    {
		line = text.substring (0, nlAt);
		text = text.substring (nlAt + 1);
	    }

	    line = SpeechKinds.mangleSpeech (kind, line);

	    mySystem.sendPacket ('b', new String[] { line });
	}

    }

    // ------------------------------------------------------------------------
    // Protected methods that we must override

    /**
     * BaseChannel calls this method when it actually wants to try to do a
     * channel join. It will only call this if the channel has not in fact
     * already been joined. 
     */
    protected final void channelJoin ()
    {
	if (mySystem.getNominallyLeftChannel ())
	{
	    ICBChannel cur = mySystem.getCurrentChannel ();

	    if (cur != this)
	    {
		mySystem.sendPacket ('h', new String[] { "g", getName ()});
	    }
	    else
	    {
		mySystem.setNominallyLeftChannel (false);
		joinedChannel ();
	    }
	}
	else
	{
	    leftChannel ();
	    broadcast (
                ErrorEvent.errorReport (
                    this, 
		    "ICB only allows you to be joined with one channel at " +
		    "a time."));
	}
    }

    /**
     * BaseChannel calls this method when it actually wants to try to do a
     * channel leave. It will only call this if the channel is in fact
     * currently joined. 
     */
    protected final void channelLeave ()
    {
	mySystem.setNominallyLeftChannel (true);
	leftChannel ();
    }

    // ------------------------------------------------------------------------
    // Package methods 

    /**
     * Set the last known topic for this channel.
     *
     * @param topic the last known topic
     */
    /*package*/ final void setLastKnownTopic (String topic)
    {
	topicChanged (topic);
    }

    /**
     * Tell the channel that it was joined.
     */
    /*package*/ final void reallyJoinedChannel ()
    {
	// BaseChannel.joinedChannel is protected, which is why we need
	// this method
	joinedChannel ();
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
}
