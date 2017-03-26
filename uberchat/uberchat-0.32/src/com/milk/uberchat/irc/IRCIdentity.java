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
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.uberchat.base.BaseIdentity;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This is the <code>ChatIdentity</code> class for an IRC identity.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class IRCIdentity
extends BaseIdentity
{
    /** the system */
    private IRCSystem mySystem;

    /** the table mapping current nicknames back to users */
    private Hashtable myNickUserMap;

    /**
     * Construct an <code>IRCIdentity</code> that uses the given
     * <code>IRCSystem</code>.
     *
     * @param system the system to use 
     */
    /*package*/ IRCIdentity (IRCSystem system)
    {
	super (system.getUserid (), system);
	mySystem = system;

	myNickUserMap = new Hashtable ();
    }

    // ------------------------------------------------------------------------
    // ChatIdentity interface methods

    /**
     * (Attempt to) change the nickname/description of this identity
     *
     * @param newNick the new nickname
     */
    public void setNickname (String newNick)
    {
	mySystem.sendCommand ("nick", new String[] { newNick });
    }

    /**
     * Update the list of channels from the server.
     */
    public void updateChannels ()
    {
	ReplyTap tap = new ReplyTap()
	{
	    private Vector chans = new Vector ();

	    synchronized public boolean handleReply (ServerReply rep)
	    {
		int code = (rep != null) 
		    ? rep.getReplyInt () 
		    : ServerReply.RPL_LISTEND;

		if (code == ServerReply.RPL_LISTEND)
		{
		    if (chans != null)
		    {
			setFromChans ();
		    }
		    return true;
		}
		else
		{
		    // args:
		    //   my-nickname
		    //   channel-name
		    //   user-count
		    //   topic
		    String[] args = rep.getArguments ();
		    
		    IRCChannel chan = (IRCChannel) nameToChannel (args[1]);
		    if (chan == null)
		    {
			// ignore it
		    }
		    else if (chans != null)
		    {
			chans.addElement (chan);
			if (chans.size () > 20)
			{
			    setFromChans ();
			}
		    }
		    else
		    {
			callAddChannel (chan);
		    }

		    return false;
		}
	    }

	    /** Helper to set the channels from the <code>chans</code>
	     * variable and then null it out. */
	    private void setFromChans ()
	    {
		ChatChannel[] carr = new ChatChannel[chans.size ()];
		chans.copyInto (carr);
		chans = null;
		callSetChannels (carr);
	    }
	};

	mySystem.commandTap ("list",
			     new String[0],
			     new int[] { ServerReply.RPL_LIST, 
					 ServerReply.RPL_LISTEND },
			     tap);
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Update the list of users fromt the host.
     */
    public void updateUsers ()
    {
	mySystem.sendCommand ("names", new String[0]);
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
	    mySystem.rawSend (text);
	    return;
	}

	broadcast (
            ErrorEvent.errorReport (
                this,
                "You cannot speak in an IRC system window.\n" +
		"IRC doesn't have broadcast messages."));
    }

    // ------------------------------------------------------------------------
    // Protected methods that we override

    /**
     * BaseIdentity calls this when a client has requested a channel
     * by name, but it doesn't exist in its list of known channels. 
     *
     * @param name the name of the channel to make
     * @return the channel made, or null if it wasn't made
     */
    protected final ChatChannel identityMakeChannel (String name)
    {
        return new IRCChannel (mySystem, name);
    }

    /**
     * BaseIdentity calls this when a client has requested a user
     * by name, but it doesn't exist in its list of known users. 
     *
     * @param name the name of the user to make
     * @return the user made, or null if it wasn't made
     */
    protected final ChatUser identityMakeUser (String name)
    {
	// first, check to see if this name is in the nickname map
	String cname = mySystem.canonicalUserName (name);
	ChatUser user = (ChatUser) myNickUserMap.get (cname);
	
	if (user == null)
	{
	    // it wasn't in the nick map, must actually make a new one
	    user = new IRCUser (mySystem, name);
	}

	return user;
    }

    /**
     * BaseIdentity calls this exactly once, the first time a client
     * asks for the identity user for this identity.
     *
     * @return the identity user for this identity
     */
    protected final ChatUser identityGetIdentityUser ()
    {
	return nameToUser (mySystem.getUserid ());
    }

    /**
     * <code>BaseLocus</code> calls this method when a user
     * has been added. We use this to deal with nickname mapping.
     *
     * @param user the user that was added
     */
    protected void userAdded (ChatUser user)
    {
	super.userAdded (user);
	myNickUserMap.put (user.getCanonicalName (), user);
    }

    /**
     * <code>BaseLocus</code> calls this method when a user
     * has been removed. We use this to deal with nickname mapping,
     * or rather, in this case, unmapping.
     *
     * @param user the user that was removed
     */
    protected void userRemoved (ChatUser user)
    {
	super.userRemoved (user);
	String uid = ((IRCUser) user).getIRCUserid ();
	myNickUserMap.remove (mySystem.canonicalUserName (uid));
    }

    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Indicate that the IRC userid of a a user has changed.
     *
     * @param user the user
     * @param oldUserid the old userid
     * @param newUserid the new userid
     */
    /*package*/ void useridRemap (IRCUser user, 
				  String oldUserid, String newUserid)
    {
	if (getKnownUser (user.getCanonicalName ()) == user)
	{
	    // only bother if the user is officially known to us
	    myNickUserMap.remove (mySystem.canonicalUserName (oldUserid));
	    myNickUserMap.put (mySystem.canonicalUserName (newUserid), user);
	}
	else
	{
	    System.err.println ("### got remap for unknown user: " + user + 
				" " + oldUserid + " " + newUserid);
	}
    }

    /**
     * Turn a nickname into a user, returning null if the name wasn't found.
     *
     * @param nick the nickname
     * @return the user that the nick refers to
     */
    /*package*/ IRCUser nickToExistingUser (String nick)
    {
	return (IRCUser) myNickUserMap.get (mySystem.canonicalUserName (nick));
    }

    /**
     * Turn a nickname into a user, creating it if necessary.
     *
     * @param nick the nickname
     * @return the user that the nick refers to
     */
    /*package*/ IRCUser nickToUser (String nick)
    {
	IRCUser user = nickToExistingUser (nick);

	if (user == null)
	{
	    // not in table, so it's the first we've heard of it
	    user = (IRCUser) getKnownUser (mySystem.canonicalUserName (nick));
	    if (user != null)
	    {
		// uh oh! collision--we unmangled a name into this
		// would-be user's userid; gotta do a presto-change-o
		user.makeNameBeUserid ();
	    }

	    user = (IRCUser) nameToUser (nick);
	}

	return user;
    }

    /**
     * Add a user to the identity.
     *
     * @param user the user to add
     */
    /*package*/ final void callAddUser (ChatUser user)
    {
	// BaseLocus.addUser() is protected, which is why we need
	// this method
	addUser (user);
    }

    /**
     * Remove a user from the identity.
     *
     * @param user the user to remove
     */
    /*package*/ final void callRemoveUser (ChatUser user)
    {
	// BaseLocus.addUser() is protected, which is why we need
	// this method
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

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Tell the identity about all the channels.
     *
     * @param chans the channels
     */
    private final void callSetChannels (ChatChannel[] chans)
    {
	// there's a bug in javac which prevents an inner class calling
	// this method--it spits out a bogus error; so we have to resort
	// to doing this. Java just sucks that way sometimes.
	setChannels (chans);
    }

    /**
     * Tell the identity to add a channel to add a user.
     *
     * @param chan the channel to add
     */
    private final void callAddChannel (ChatChannel chan)
    {
	// there's a bug in javac which prevents an inner class calling
	// this method--it spits out a bogus error; so we have to resort
	// to doing this. Java just sucks that way sometimes.
	addChannel (chan);
    }
}
