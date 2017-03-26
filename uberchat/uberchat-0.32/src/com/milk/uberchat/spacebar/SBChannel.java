// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.spacebar;

import com.milk.uberchat.base.BaseChannel;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;

/**
 * This is the ChatChannel class for a SpaceBar channels.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public final class SBChannel
extends BaseChannel
{
    /** the SBSystem to use */
    private SBSystem mySystem;

    /**
     * Construct a SBChannel for the given system and channel number.
     *
     * @param system the given system
     * @param name the channel name
     */
    /*package*/ SBChannel (SBSystem system, String name)
    {
	super (system.canonicalChannelName (name),
	       system.getIdentity (),
	       null);

	mySystem = system;
    }

    /**
     * (Attempt to) set the topic of this channel.
     *
     * @param topic the new topic for the channel.
     */
    public void setTopic (String topic)
    {
	if (getJoinedState () == JOINED)
	{
	    if (topic.equals (""))
	    {
		mySystem.typeLine ("/U");
	    }
	    else
	    {
		mySystem.sendExpectSend ("/N", "new-name", topic + "\n");
	    }
	}
	else
	{
	    broadcast (
                ErrorEvent.errorReport (
                    this,
		    "SpaceBar only allows topic setting on channels you " +
		    "have joined."));
	}
    }

    /**
     * Update the user list from the server.
     */
    public void updateUsers ()
    {
	// BUG--should run asynchronously
	SBIdentity ident = mySystem.getIdentity ();
	String toSend = (getJoinedState () == JOINED) 
	    ? "\n" : getCanonicalName () + '\n';
	
	String[] lines =
	    mySystem.sendExpectSendGather ("/C", "channel", toSend);
	boolean gotTopic = false;

	if (lines.length == 0)
	{
	    // bad channel name
	    return;
	}

	if (parseWhoLine (lines[0], ident) instanceof SBChannel)
	{
	    gotTopic = true;
	}

	ChatUser[] users = new ChatUser[lines.length - (gotTopic ? 1 : 0)];

	for (int i = (gotTopic ? 1 : 0); i < lines.length; i++)
	{
	    SBUser user = (SBUser) parseWhoLine (lines[i], ident);
	    users[i - (gotTopic ? 1 : 0)] = user;
	    user.setLastKnownChannel (this);
	}

	setUsers (users);
    }

    /**
     * Speak in this locus.
     *
     * @param kind the kind of speech (see <code>SpeechKinds</code> for
     * details)
     * @param text the message text 
     */
    public void speak (String kind, String text)
    {
	if (   kind.equals (SpeechKinds.RAW) 
	    || kind.equals (SpeechKinds.RAW_NO_NL))
	{
	    mySystem.rawSend (kind, text);
	    return;
	}

	if (getJoinedState () != JOINED)
	{
	    broadcast (
                ErrorEvent.errorReport (
                    this,
		    "SpaceBar only allows speaking on channels you have " +
		    "joined."));
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
	    if (line.length () == 0)
	    {
		line = " ";
	    }
	    else if (line.charAt (0) == '/')
	    {
		line = " " + line;
	    }

	    mySystem.typeLine (line + '\n');
	}
    }

    // ------------------------------------------------------------------------
    // Protected methods we override

    /**
     * Ask to join this channel.
     */
    protected void channelJoin ()
    {
	if (mySystem.getNominallyLeftChannel ())
	{
	    String cname = getCanonicalName ();
	    SBChannel cur = mySystem.getCurrentChannel ();

	    if (cur != this)
	    {
		mySystem.sendExpectSend ("/c", "channel", cname + "\n");
	    }
	    else
	    {
		mySystem.setNominallyLeftChannel (false);
		joinedChannel ();
	    }
	}
	else
	{
	    if (getJoinedState () != JOINED)
	    {
		leftChannel ();
		broadcast (
                    ErrorEvent.errorReport (
                        this, 
			"SpaceBar only allows you to be joined with one " +
			"channel at a time."));
	    }
	}
    }

    /**
     * Ask to leave this channel.
     */
    protected void channelLeave ()
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
    /*package*/ void setLastKnownTopic (String topic)
    {
	topicChanged (topic);
    }

    /**
     * Tell the channel that it was joined.
     */
    /*package*/ void reallyJoinedChannel ()
    {
	// BaseChannel.joinedChannel is protected, which is why we need
	// this method
	joinedChannel ();
    }

    /**
     * Parse a line from a who listing, returning either a SBChannel, a
     * SBUser, an SBIdentity, or null. It also automatically updates user
     * and channel info (topic, nickname, etc.).
     *
     * @param line the line
     * @param ident the SBIdentity to use for lookups
     * @return what it represents 
     */
    static /*package*/ ChatLocus parseWhoLine (String line, SBIdentity ident)
    {
	if (line.length () == 0)
	{
	    return null;
	}
	else if (line.startsWith ("All other channels"))
	{
	    // this indicates that all further users are on private channels
	    return ident;
	}
	else if (line.startsWith ("Channel"))
	{
	    String name = line.substring (8, 10);
	    if (name.charAt (0) == '0')
	    {
		name = name.substring (1);
	    }

	    SBChannel channel = (SBChannel) ident.nameToChannel (name);
	    
	    int dashesAt = line.indexOf (" -- ");
	    if (dashesAt == -1)
	    {
		channel.setLastKnownTopic ("");
	    }
	    else
	    {
		channel.setLastKnownTopic (
                    line.substring (dashesAt + 4, line.length ()));
	    }

	    return channel;
	}
	else if (line.startsWith ("```"))
	{
	    int delim1 = line.indexOf ("~$`");
	    int delim2 = line.indexOf ("~$`", delim1 + 3);
	    int delim3 = line.indexOf ("~$`", delim2 + 3);
	    int delim4 = line.indexOf ("```", delim3 + 3);
	    String userid = line.substring (3, delim1);
	    String nick = line.substring (delim1 + 3, delim2);
	    SBUser user = (SBUser) ident.nameToUser (userid);
	    user.setLastKnownNickname (nick);
	    return user;
	}
	else
	{
	    SBSystem sys = (SBSystem) ident.getTargetSystem ();
	    sys.bugReport (
                new ShouldntHappenException (
                    "SpaceBar client got weird who line:\n" + line));
	    return null;
	}
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
