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
 * This is the ChatIdentity class for an ICB identity.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class ICBIdentity
extends BaseIdentity
{
    /** the system */
    private ICBSystem mySystem;

    /** the table mapping current nicknames back to users */
    private Hashtable myNickUserMap;

    /**
     * Construct an <code>ICBIdentity</code> that uses the given
     * <code>ICBSystem</code>.
     *
     * @param system the system to use 
     */
    /*package*/ ICBIdentity (ICBSystem system)
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
	if (newNick.length () > 12)
	{
	    mySystem.errorReport (
                "ICB only allows up to 12 character nicknames.");
	    return;
	}

	mySystem.sendPacket (
            'h', new String[] { "name", newNick });
    }

    /**
     * Update the list of channels from the server.
     */
    public void updateChannels ()
    {
	// ignore the return value; it'll be an empty vector anyway
	// BUG--should run asynchronously
	doWhoCommand ("-g");
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Update the list of users fromt the host.
     */
    public void updateUsers ()
    {
	// BUG--should run asynchronously
	Vector uvec = doWhoCommand ("");
	ChatUser[] users = new ChatUser[uvec.size ()];
	uvec.copyInto (users);
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
	    mySystem.rawSend (text);
	    return;
	}

	broadcast (
            ErrorEvent.errorReport (
                this,
                "You cannot speak in an ICB system window.\n" +
		"ICB doesn't have broadcast messages."));
    }

    // ------------------------------------------------------------------------
    // Protected methods that we must override

    /**
     * BaseIdentity calls this when a client has requested a channel
     * by name, but it doesn't exist in its list of known channels. 
     *
     * @param name the name of the channel to make
     * @return the channel made, or null if it wasn't made
     */
    protected final ChatChannel identityMakeChannel (String name)
    {
        return new ICBChannel (mySystem, name);
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
	String lowerName = name.toLowerCase ();
	ChatUser user = (ChatUser) myNickUserMap.get (lowerName);
	
	if (user == null)
	{
	    // it wasn't in the nick map, must actually make a new one
	    user = new ICBUser (mySystem, name);
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
	String uid = ((ICBUser) user).getICBUserid ();
	if (uid != null)
	{
	    myNickUserMap.put (uid, user);
	}
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
	myNickUserMap.remove (((ICBUser) user).getICBUserid ());
    }

    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Indicate that the ICB userid of a a user has changed.
     *
     * @param user the user
     * @param oldUserid the old userid
     * @param newUserid the new userid
     */
    /*package*/ void useridRemap (ICBUser user, 
				  String oldUserid, String newUserid)
    {
	if (getKnownUser (user.getCanonicalName ()) == user)
	{
	    // only bother if the user is officially known to us
	    myNickUserMap.remove (oldUserid);
	    myNickUserMap.put (newUserid, user);
	}
	else
	{
	    System.err.println ("### got remap for unknown user: " + user + " " + oldUserid + " " + newUserid);
	}
    }

    /**
     * Make a mapping of the given nick to the given user.
     *
     * @param nick the nickname
     * @param user the user it should map to
     */
    /*package*/ void nickUserMap (String nick, ICBUser user)
    {
	myNickUserMap.put (nick, user);
    }

    /**
     * Remove the mapping for the given nick.
     *
     * @param nick the nickname
     */
    /*package*/ void nickUserUnmap (String nick)
    {
	myNickUserMap.remove (nick);
    }

    /**
     * Turn a nickname into a user, returning null if the name wasn't found.
     *
     * @param nick the nickname
     * @return the user that the nick refers to
     */
    /*package*/ ICBUser nickToExistingUser (String nick)
    {
	return (ICBUser) myNickUserMap.get (nick.toLowerCase ());
    }

    /**
     * Turn a nickname into a user, creating it if necessary.
     *
     * @param nick the nickname
     * @return the user that the nick refers to
     */
    /*package*/ ICBUser nickToUser (String nick)
    {
	ICBUser user = (ICBUser) myNickUserMap.get (nick.toLowerCase ());

	if (user == null)
	{
	    // gotta actually make it
	    user = (ICBUser) nameToUser (nick);
	}

	return user;
    }

    /**
     * Do a "who" command on the given channel name. Return the list
     * of users, and do appropriate updates based on the channels
     * returned.
     * 
     * @param name the channel name to ask for 
     * @return the list of users
     */
    /*package*/ Vector doWhoCommand (String name)
    {
	MessageEvent[] response = 
	    mySystem.sendGather ('h', new String[] { "w", name });
	ICBChannel chan = null;

	Vector uvec = new Vector ();
	Vector cvec = new Vector ();
	for (int i = 0; i < response.length; i++)
	{
	    ChatLocus loc = parseWhoLine (response[i], this);
	    if (loc == this)
	    {
		// signals that the next users will have no known
		// channel
		chan = null;
	    }
	    else if (loc instanceof ICBUser)
	    {
		uvec.addElement (loc);
		((ICBUser) loc).setLastKnownChannel (chan);
	    }
	    else if (loc instanceof ICBChannel)
	    {
		cvec.addElement (loc);
		chan = (ICBChannel) loc;
	    }
	}

	if ((name.length () == 0) || (name.equals ("-g")))
	{
	    // totally update channels, since we got all the info for it
	    // anyway
	    ChatChannel[] chans = new ChatChannel[cvec.size ()];
	    cvec.copyInto (chans);
	    setChannels (chans);
	}

	return uvec;
    }

    /**
     * Parse a line from a who listing, returning either an ICBChannel,
     * an ICBUser, or null. It also automatically updates user and channel
     * info (topic, nickname, etc.).
     *
     * @param msg the message line
     * @param ident the ICBIdentity to use for lookups
     * @return what it represents
     */
    static /*package*/ ChatLocus parseWhoLine (MessageEvent msg, 
					       ICBIdentity ident)
    {
	int type = msg.getType ();
	Object arg = msg.getArgument ();

	if (type != MessageEvent.SYSTEM_PRIVATE)
	{
	    ICBSystem sys = (ICBSystem) ident.getTargetSystem ();
	    sys.bugReport (
	        new ShouldntHappenException (
		    "ICB client got weird who line:\n" + msg));
	    return null;
	}

	if (arg instanceof ICBWho)
	{
	    ICBWho who = (ICBWho) arg;
	    return ident.nickToUser (who.myUserid);
	}

	String text = msg.getText ();
	if (text.equals ("[Error] User not found."))
	{
	    // acceptable error message; ignore it
	    return null;
	}
	else if (   text.startsWith ("[Error] The group ") 
		 && text.endsWith (" doesn't exist."))
	{
	    // acceptable error message; ignore it
	    return null;
	}
	else if (   text.equals (" ")
		 || text.startsWith ("Total: "))
	{
	    // blank or "total" line--ignore it
	    return null;
	}
	else if (text.startsWith ("Group: "))
	{
	    int groupEnd = text.indexOf (' ', 7);
	    int statusAt = text.indexOf ('(', groupEnd + 1);
	    int statusEnd = text.indexOf (')', statusAt + 1);
	    int modAt = text.indexOf ("Mod: ", statusEnd + 1);
	    int modEnd = text.indexOf (' ', modAt + 5);
	    int topicAt = text.indexOf ("Topic: ", modEnd + 1);
	    String name = text.substring (7, groupEnd);
	    String status = text.substring (statusAt + 1, statusEnd);
	    String mod = text.substring (modAt + 5, modEnd);
	    String topic = text.substring (topicAt + 7);
	    if (topic.equals ("(None)"))
	    {
		topic = "";
	    }
	    char visChar = status.charAt (1);
	    if ((visChar == 's') || (visChar == 'i'))
	    {
		// indicate that we don't actually know the channel
		// for users in secret/invisible groups, except if it's the
		// group we're in
		if (name.charAt (0) == '*')
		{
		    // when secret, name comes out as "*name*"
		    // arrgh
		    name = name.substring (1, name.length () - 1);
		}
		else
		{
		    return ident;
		}
	    }
	    ICBChannel channel = (ICBChannel) ident.nameToChannel (name);
	    channel.setLastKnownTopic (topic);
	    return channel;
	}
	else
	{
	    ICBSystem sys = (ICBSystem) ident.getTargetSystem ();
	    sys.bugReport (
		new ShouldntHappenException (
                    "ICB client got weird who line:\n" + msg));
	    return null;
	}
    }

    /**
     * Send a <code>systemPrivate</code> event to whoever is listening
     * this object.
     *
     * @param msg the message text
     */
    /*package*/ void systemPrivate (String msg)
    {
	broadcast (MessageEvent.systemPrivate (this, msg));
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
     * Call <code>BaseLocus.removeUser()</code> on an event. This is
     * defined to give this package access to an otherwise-protected
     * method.
     *
     * @param user the user to remove
     */
    /*package*/ void callRemoveUser (ICBUser user)
    {
	removeUser (user);
    }
}
