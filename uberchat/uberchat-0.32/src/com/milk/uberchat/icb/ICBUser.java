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

import com.milk.uberchat.base.BaseUser;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.util.BaseEvent;
import java.util.Vector;

/**
 * This is the ChatUser class for an ICB user.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class ICBUser
extends BaseUser
{
    /** the ICBSystem to use */
    private ICBSystem mySystem;

    /** the ICBIdentity to use */
    private ICBIdentity myIdentity;

    /** the ICB userid to use for messages and to recognize as this user */
    private String myICBUserid;

    /** the last known channel for this user */
    private ICBChannel myLastKnownChannel;
    
    /**
     * Construct an ICBUser for the given system and userid.
     *
     * @param system the given system
     * @param userid the given userid
     */
    /*package*/ ICBUser (ICBSystem system, String userid)
    {
	super (userid, system.getIdentity (), "");

	mySystem = system;
	myIdentity = system.getIdentity ();
	myICBUserid = system.canonicalUserName (userid);
	myLastKnownChannel = null;
    }

    // ------------------------------------------------------------------------
    // ChatChannelHolder interface methods

    /**
     * Ask for the list of channels for this object to be updated from
     * the host. 
     */
    public void updateChannels ()
    {
	// close enough; we ask the host for the list of users
	// wherever this user is; it ought to update things appropriately
	// BUG--should run asynchronously
	ICBIdentity ident = mySystem.getIdentity ();
	Vector result = ident.doWhoCommand ('@' + getCanonicalName ());
	if (result.size () == 0)
	{
	    // means they've logged out
	    myIdentity.callRemoveUser (this);
	}
    }

    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Set the last known nickname for this user.
     *
     * @param nick the last known nickname
     */
    /*package*/ void setLastKnownNickname (String nick)
    {
	String lowerNick = nick.toLowerCase ().intern ();
	String cname = getCanonicalName ();

	if (cname.startsWith (lowerNick))
	{
	    // if the current name is longer than and starts with the given
	    // nickname, then in fact the name and nick ought to swap; this
	    // deals with "foo_away" changing their name to "foo"
	    setName (nick, lowerNick);
	    cname = lowerNick;
	    nick = "";
	    lowerNick = "";
	}

	if (lowerNick.startsWith (cname))
	{
	    // deal with the nickname starting with the user name, so you
	    // don't see things like "joe/joeHungover"
	    nick = nick.substring (cname.length ());
	}

	myIdentity.useridRemap (this, myICBUserid, lowerNick);
	myICBUserid = lowerNick;
	nicknameChanged (nick);
    }

    /**
     * Set the last known channel for this user.
     *
     * @param channel the last known channel
     */
    /*package*/ void setLastKnownChannel (ICBChannel channel)
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
     * Get the ICB userid for this user. This may be different from
     * getName() if the userid did a name change while we were watching.
     *
     * @return the ICB userid
     */
    /*package*/ String getICBUserid ()
    {
	return myICBUserid;
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
    // ChatLocus interface methods

    /**
     * Speak in this locus.
     *
     * @param kind the kind of speech (see SpeechKinds for details)
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

	if (kind.equals (SpeechKinds.BEEP))
	{
	    // actually send a beep if the kind is a beep
	    mySystem.sendPacket (
                'h', new String[] { "beep", myICBUserid });
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

	    mySystem.sendPacket (
                'h', new String[] { "m", myICBUserid + " " + line });
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
	    myLastKnownChannel = (ICBChannel) channel;

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
}
