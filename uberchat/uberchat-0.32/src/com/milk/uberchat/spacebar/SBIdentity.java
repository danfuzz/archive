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
import com.milk.uberchat.base.BaseIdentity;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import com.milk.util.ListenerList;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This is the ChatIdentity class for a SpaceBar identity.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public class SBIdentity
extends BaseIdentity
{
    /** the SBSystem to use */
    private SBSystem mySystem;

    /**
     * Construct a SBIdentity that uses the given SBSystem.
     *
     * @param system the system to use
     */
    /*package*/ SBIdentity (SBSystem system)
    {
	super (system.getUserid (), system);

	mySystem = system;
    }

    // ------------------------------------------------------------------------
    // ChatIdentity interface methods

    /**
     * Attempt to set the nickname for this identity.
     *
     * @param newNick the new nickname
     */
    public void setNickname (String newNick)
    {
	mySystem.sendExpectSend ("/n", "new-nickname", newNick + "\n");
    }

    /**
     * Update the list of channels from the server.
     */
    public void updateChannels ()
    {
	// spacebar doesn't have a separate way of knowing channels, so
	// just let updateUsers() do its thing, which does both users and
	// channels
	updateUsers ();
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Update the list of users in this locus from the host.
     */
    public void updateUsers ()
    {
	// a little weirdness so that the users on the current channel,
	// if private, get known correctly
	SBChannel currentChannel = mySystem.getCurrentChannel ();

	String[] lines = mySystem.sendGather ("/a\n");
	Vector uvec = new Vector ();
	Vector cvec = new Vector ();
	SBChannel chan = null;

	for (int i = 0; i < lines.length; i++)
	{
	    ChatLocus one = SBChannel.parseWhoLine (lines[i], this);

	    if (one == this)
	    {
		// signal that further users are on private channels
		chan = null;
	    }
	    if (one instanceof SBChannel)
	    {
		cvec.addElement (one);
		chan = (SBChannel) one;
	    }
	    else if (one instanceof SBUser)
	    {
		uvec.addElement (one);
		SBUser u = (SBUser) one;
		u.setLastKnownChannel (chan);
	    }
	}

	ChatUser[] users = new ChatUser[uvec.size ()];
	uvec.copyInto (users);
	setUsers (users);

	ChatChannel[] chans = new ChatChannel[cvec.size ()];
	cvec.copyInto (chans);
	setChannels (chans);

	// see comment above
	currentChannel.updateUsers ();
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
	    
	    mySystem.sendExpectSend ("/d", "message", line + "\n");
	}
    }

    // ------------------------------------------------------------------------
    // Protected methods we override

    /**
     * BaseIdentity calls this when a client has requested a channel
     * by name, but it doesn't exist in its list of known channels.
     *
     * @param name the name of the channel to make
     * @return the channel made, or null if it wasn't made
     */
    protected ChatChannel identityMakeChannel (String name)
    {
	return new SBChannel (mySystem, name);
    }

    /**
     * BaseIdentity calls this when a client has requested a user
     * by name, but it doesn't exist in its list of known users. 
     *
     * @param name the name of the user to make
     * @return the user made, or null if it wasn't made
     */
    protected ChatUser identityMakeUser (String name)
    {
	return new SBUser (mySystem, name);
    }

    /**
     * BaseIdentity calls this exactly once, the first time a client
     * asks for the identity user for this identity.
     *
     * @return the identity user for this identity
     */
    protected ChatUser identityGetIdentityUser ()
    {
	return nameToUser (mySystem.getUserid ());
    }

    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Indicate that a user has logged out.
     *
     * @param user the user that logged out
     */
    /*package*/ void userLoggedOut (SBUser user)
    {
	removeUser (user);
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
