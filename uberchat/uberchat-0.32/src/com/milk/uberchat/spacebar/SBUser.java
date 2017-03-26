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

import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.base.BaseUser;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.util.BaseEvent;

/**
 * This is the ChatUser class for a SpaceBar user.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public class SBUser
extends BaseUser
{
    /** the SBSystem to use */
    private SBSystem mySystem;

    /** the SBIdentity to use */
    private SBIdentity myIdentity;
    
    /** the last known channel for this userid */
    private SBChannel myLastKnownChannel;

    /**
     * Construct a SBUser for the given system and userid
     *
     * @param system the given system
     * @param userid the given userid
     */
    /*package*/ SBUser (SBSystem system, String userid)
    {
	super (userid, system.getIdentity (), "");

	mySystem = system;
	myLastKnownChannel = null;
	myIdentity = system.getIdentity ();
    }

    // ------------------------------------------------------------------------
    // ChatChannelHolder interface methods

    /**
     * Ask for the list of channels for this object to be updated from
     * the host. 
     */
    public void updateChannels ()
    {
	// BUG--ought to use the "/i" command, but for now, just update
	// the world
	mySystem.getIdentity ().updateUsers ();
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

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

	if (kind.equals (SpeechKinds.BEEP))
	{
	    // actually send a beep if the kind is a beep
	    mySystem.sendExpectSend ("/b", "to", getCanonicalName () + "\n");

	    broadcast (
	        MessageEvent.userBroadcast (this, 
					    myIdentity.getIdentityUser (),
					    SpeechKinds.BEEP, ""));
	    if (text.equals (""))
	    {
		// don't bother with message if it's empty
		return;
	    }
	    kind = SpeechKinds.SAYS;
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

	    mySystem.sendExpectSend2 ("/p", "to", getCanonicalName () + "\n", 
				      "message", line + "\n");

	    // this makes "manually" entered kinds come out right in the
	    // local event broadcast
	    String[] unmangled = SpeechKinds.unmangleSpeech (line);
	    broadcast (
	        MessageEvent.userBroadcast (
                    this, myIdentity.getIdentityUser (),
		    unmangled[0], unmangled[1]));
	}
    }


    // ------------------------------------------------------------------------
    // Protected methods that we override

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel
     * has been added. We use this to keep the <code>myLastKnownChannel</code>
     * instance variable in synch.
     *
     * @param channel the channel that was added
     */
    protected void channelAdded (ChatChannel channel)
    {
	super.channelAdded (channel);

	if (channel != myLastKnownChannel)
	{
	    removeChannel (myLastKnownChannel);
	    myLastKnownChannel = (SBChannel) channel;

	    if (this == myIdentity.getIdentityUser ())
	    {
		mySystem.setCurrentChannel (myLastKnownChannel);
		mySystem.setNominallyLeftChannel (false);
	    }
	}
    }

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel has
     * been removed. We use this to keep the
     * <code>myLastKnownChannel</code> instance variable in synch.
     *
     * @param channel the channel that was removed */
    protected void channelRemoved (ChatChannel channel)
    {
	super.channelRemoved (channel);

	if (channel == myLastKnownChannel)
	{
	    myLastKnownChannel = null;
	}
    }

    // ------------------------------------------------------------------------
    // Package helper methods

    /**
     * Set the last known nickname for this user.
     *
     * @param nick the last known nickname
     */
    /*package*/ void setLastKnownNickname (String nick)
    {
	nicknameChanged (nick);
    }

    /**
     * Set the last known channel for this user.
     *
     * @param channel the last known channel
     */
    /*package*/ void setLastKnownChannel (SBChannel channel)
    {
	if (myLastKnownChannel == channel)
	{
	    // easy out
	    return;
	}

	myLastKnownChannel = channel;

	if (channel == null)
	{
	    setChannels (null);
	}
	else
	{
	    setChannels (new ChatChannel[] { channel });

	    if (this == myIdentity.getIdentityUser ())
	    {
		mySystem.setCurrentChannel (channel);
		mySystem.setNominallyLeftChannel (false);
	    }
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
