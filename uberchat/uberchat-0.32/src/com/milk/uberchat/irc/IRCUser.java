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

import com.milk.uberchat.base.BaseUser;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.util.BaseEvent;
import java.util.Vector;

/**
 * This is the <code>ChatUser</code> class for an IRC user.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class IRCUser
extends BaseUser
{
    /** the <code>IRCSystem</code> to use */
    private IRCSystem mySystem;

    /** the <code>IRCIdentity</code> to use */
    private IRCIdentity myIdentity;

    /** the IRC userid to use for messages and to recognize as this user */
    private String myIRCUserid;

    /**
     * Construct an <code>IRCUser</code> for the given system and userid.
     *
     * @param system the given system
     * @param userid the given userid
     */
    /*package*/ IRCUser (IRCSystem system, String userid)
    {
	super (userid, system.getIdentity (), "");

	mySystem = system;
	myIdentity = system.getIdentity ();
	myIRCUserid = userid;
    }

    // ------------------------------------------------------------------------
    // ChatChannelHolder interface methods

    /**
     * Ask for the list of channels for this object to be updated from
     * the host. 
     */
    public void updateChannels ()
    {
	ReplyTap tap = new ReplyTap()
	{
	    private Vector chans = new Vector ();
	    private IRCUser user = null;

	    synchronized public boolean handleReply (ServerReply rep)
	    {
		int code = (rep != null) 
		    ? rep.getReplyInt () 
		    : ServerReply.RPL_ENDOFWHOIS;

		if (code == ServerReply.RPL_ENDOFWHOIS)
		{
		    if (user != null)
		    {
			ChatChannel[] carr = new ChatChannel[chans.size ()];
			chans.copyInto (carr);
			user.callSetChannels (carr);
		    }
		    return true;
		}

		String[] args = rep.getArguments ();
		switch (code)
		{
		    case ServerReply.RPL_WHOISUSER:
		    {
			// args:
			//    target-nickname
			//    nickname
			//    user name
			//    host name
			//    "*"
			//    real name
			user = myIdentity.nickToUser (args[1]);
			// BUG--should set other info too
			break;
		    }
		    case ServerReply.RPL_WHOISSERVER:
		    {
			// args:
			//    target-nickname
			//    nickname
			//    server
			//    server info
			// BUG--should set interesting info
			break;
		    }
		    case ServerReply.RPL_WHOISOPERATOR:
		    {
			// args:
			//    target-nickname
			//    nickname
			//    "is an IRC operator"
			// BUG--should set interesting info
			break;
		    }
		    case ServerReply.RPL_WHOISIDLE:
		    {
			// args:
			//    target-nickname
			//    nickname
			//    seconds-idle
			//    "seconds idle"
			// BUG--should set interesting info
			break;
		    }
		    case ServerReply.RPL_WHOISCHANNELS:
		    {
			// args:
			//    target-nickname
			//    nickname
			//    {[@|+]<channel><space>}*
			String cstr = args[2];
			int len = cstr.length ();
			for (int at = 0; at < len; at++)
			{
			    char c = cstr.charAt (at);
			    if ((c == ' ') || (c == '@') || (c == '+'))
			    {
				continue;
			    }
			    int chanAt = at;
			    at++;
			    while ((at < len) && (cstr.charAt (at) != ' '))
			    {
				at++;
			    }
			    ChatChannel chan = 
			        myIdentity.nameToChannel (
			            cstr.substring (chanAt, at));
			    if (chan != null)
			    {
				chans.addElement (chan);
			    }
			}
			break;
		    }
		}
		
		return false;
	    }
	};

	mySystem.commandTap ("whois",
			     new String[] { myIRCUserid },
			     new int[] { ServerReply.RPL_WHOISUSER, 
					 ServerReply.RPL_WHOISSERVER,
					 ServerReply.RPL_WHOISOPERATOR,
					 ServerReply.RPL_WHOISIDLE,
					 ServerReply.RPL_WHOISCHANNELS,
					 ServerReply.RPL_ENDOFWHOIS },
			     tap);

	// since we may know some channels because we're in the channel
	// with them, we should check such channels explicitly after the
	// whois, since it might have erased salient info
	ChatChannel[] known = getKnownChannels ();
	for (int i = 0; i < known.length; i++)
	{
	    if (known[i].getJoinedState () == ChatChannel.JOINED)
	    {
		((IRCChannel) known[i]).updateUsers ();
	    }
	}
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
	mySystem.speakTo (this, kind, text);
    }

    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Indicate that this user joined a channel. This merely makes
     * what is otherwise a protected method into a package method.
     *
     * @param channel the channel the user joined
     */
    /*package*/ void callAddChannel (ChatChannel channel)
    {
	addChannel (channel);
    }

    /**
     * Indicate that this user left a channel. This merely makes
     * what is otherwise a protected method into a package method.
     *
     * @param channel the channel the user left
     */
    /*package*/ void callRemoveChannel (ChatChannel channel)
    {
	removeChannel (channel);
    }

    /**
     * Indicate the channels that this user is on. This merely makes
     * what is otherwise a protected method into a package method.
     *
     * @param channels the channels the user is on
     */
    /*package*/ void callSetChannels (ChatChannel[] channels)
    {
	setChannels (channels);
    }

    /**
     * Set the last known nickname for this user.
     *
     * @param nick the last known nickname
     */
    /*package*/ void setLastKnownNickname (String nick)
    {
	String userid = nick;
	String cnick = mySystem.canonicalUserName (nick);
	String cname = getCanonicalName ();

	if (cname.startsWith (cnick))
	{
	    // if the current name is longer than and starts with the given
	    // nickname, then in fact the name and nick ought to swap; this
	    // deals with "foo_away" changing their name to "foo"
	    setName (nick, cnick);
	    cname = cnick;
	    nick = "";
	    cnick = "";
	}

	if (cnick.startsWith (cname))
	{
	    // deal with the nickname starting with the user name, so you
	    // don't see things like "joe/joeHungover"
	    nick = nick.substring (cname.length ());
	}

	myIdentity.useridRemap (this, myIRCUserid, userid);
	myIRCUserid = userid;
	nicknameChanged (nick);
    }

    /**
     * Make the name of this user be the same as the userid. This is done
     * when (a) this user's name <i>isn't</i> in fact the userid (due to
     * nickname change tracking) and (b) a new user logs in with the name
     * that this user currently has.
     */
    /*package*/ void makeNameBeUserid ()
    {
	String cname = mySystem.canonicalUserName (myIRCUserid);
	setName (myIRCUserid, cname);
	nicknameChanged ("");
    }

    /**
     * Get the IRC userid for this user. This may be different from
     * <code>getName()</code> if the userid did a name change while we were
     * watching.
     *
     * @return the IRC userid 
     */
    /*package*/ String getIRCUserid ()
    {
	return myIRCUserid;
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
}
