// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.base;

import com.milk.uberchat.event.UserEvent;
import com.milk.uberchat.event.UserListener;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * <p>This is an abstract base class that provides a lot of what
 * all <code>ChatUser</code> implementors need.</p>
 *
 * <p>Note that <code>BaseUser</code> completely takes care of setting the
 * description for its <code>ChatEntity</code> nature, so subclasses should
 * <i>not</i> call <code>BaseEntity.setDescription()</code>. Unfortunately,
 * Java doesn't provide a way to specify this restriction programatically.
 * It just sucks that way sometimes.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseUser
extends BaseChannelHolder
implements ChatUser
{
    /** the current nickname of this user */
    private String myNickname;

    /**
     * Construct a <code>BaseUser</code>. It starts out with an empty
     * channels list.
     *
     * @param name the name for this user
     * @param targetIdentity the target identity for this user
     * @param nickname null-ok; the initial nickname for the user
     */
    public BaseUser (String name, ChatIdentity targetIdentity, String nickname)
    {
	// note duplicate method calls; can't be helped since Java forces
	// the call to super to be the first thing in a constructor body;
	// Java just sucks that way sometimes
	super (name, 
	       targetIdentity.getTargetSystem ().canonicalUserName (name),
	       makeDescription (name, targetIdentity, nickname, null),
	       targetIdentity.getTargetSystem (), 
	       targetIdentity);

	myNickname = nickname;

	// tell the BaseLocus about the users in the locus
	addUser (this);
	if (! getCanonicalName ().equals (targetIdentity.getCanonicalName ()))
	{
	    // the check is for the case where this BaseUser will in fact
	    // be the identity user for the identity we were handed
	    addUser (targetIdentity.getIdentityUser ());
	}

	if (myDebug)
	{
	    debugPrintln ("!!! BaseUser (" + name + ", " + targetIdentity +
			  ", " + nickname + ")");
	}
    }

    // ------------------------------------------------------------------------
    // ChatUser interface methods

    /**
     * Get the nickname/description of this user. Should return
     * <code>""</code> and not null if the chat system doesn't support
     * nicknames.
     *
     * @return non-null; the nickname/description of the user 
     */
    public final String getNickname ()
    {
	if (myNickname == null)
	{
	    myNickname = "";
	}

	return myNickname;
    }

    /**
     * Get the standard <code>"<i>name</i>/<i>nick</i>"</code> combo
     * string. This returns either the full combination, or merely
     * the name if the nickname is either empty or identical to the
     * name.
     *
     * @return the name/nick combo
     */
    public final String getNameNickCombo ()
    {
	// BUG--should cache this and notice name changes
	String result = getName ();
	if (   (myNickname.length () != 0)
	    && !myNickname.equals (result))
	{
	    result += '/' + myNickname;
	}

	return result;
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Update the list of users in this locus. In this case, it does
     * nothing, since the set of users for a <code>BaseUsers</code> is
     * constant.
     */
    public final void updateUsers ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // Protected methods that we override

    /**
     * <code>BaseEntity</code> calls this method when a listener has been
     * added.
     *
     * @param listener the listener that was added 
     */
    protected void listenerAdded (EventListener listener)
    {
	super.listenerAdded (listener);
	if (listener instanceof UserListener)
	{
	    sendInitialEvents ((UserListener) listener);
	}
    }

    /**
     * <code>BaseEntity</code> calls this method when an uber-listener has
     * been added.
     *
     * @param listener the listener that was added 
     */
    protected void uberListenerAdded (EventListener listener)
    {
	super.uberListenerAdded (listener);
	if (listener instanceof UserListener)
	{
	    sendInitialEvents ((UserListener) listener);
	}
    }

    /**
     * <p>Broadcast an event to the appropriate uber-listeners of this
     * entity. <code>BaseUser</code> uses this to send the event to its
     * <code>ChatIdentity</code> parent, but only if the parent is in fact
     * a <code>BaseEntity</code>.</p>
     * 
     * <p>To repeat the note from <code>BaseEntity</code>: Subclasses may
     * override this to send to additional listeners or perform needed
     * actions, but overriding methods must call
     * <code>super.uberBroadcast()</code>. Java has no way to enforce this,
     * however. It just sucks that way sometimes.</p>
     *
     * @param event the event to send 
     */
    protected void uberBroadcast (BaseEvent event)
    {
	super.uberBroadcast (event);

	ChatIdentity ident = getTargetIdentity ();
	if (ident instanceof BaseEntity)
	{
	    ((BaseEntity) ident).uberBroadcast (event);
	}
    }

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel has
     * been added. We use this to make sure that the channel is known to
     * the identity and that the channel knows about the user, keeping
     * everything in synch. These things only work if the target identity
     * of the user is in fact a <code>BaseChannelHolder</code>, and the
     * channel is in fact a <code>BaseLocus</code>.
     *
     * @param channel the channel that was added 
     */
    protected void channelAdded (ChatChannel channel)
    {
	super.channelAdded (channel);

	ChatIdentity ident = getTargetIdentity ();
	if (ident instanceof BaseChannelHolder)
	{
	    ((BaseChannelHolder) ident).addChannel (channel);
	}

	if (channel instanceof BaseLocus)
	{
	    ((BaseLocus) channel).addUser (this);
	}
    }

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel
     * has been removed. We use this to make sure the channel's user
     * list doesn't contain this object, but we can only do this if
     * the channel is in fact a <code>BaseLocus</code>.
     *
     * @param channel the channel that was removed
     */
    protected void channelRemoved (ChatChannel channel)
    {
	super.channelRemoved (channel);

	if (channel instanceof BaseLocus)
	{
	    ((BaseLocus) channel).removeUser (this);
	}
    }

    /**
     * <code>BaseEntity</code> calls this when the name and/or canonical
     * name of this object changes. We use this to re-make the description,
     * since it is in part based on the name.
     */
    protected void nameChanged ()
    {
	super.nameChanged ();
	setDescription ();
    }

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Tell this <code>BaseUser</code> that the nickname has changed.
     *
     * @param nickname null-ok; the new nickname for the channel
     */
    protected final void nicknameChanged (String nickname)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".nicknameChanged (" + 
			  nickname + ")");
	}

	if (nickname == null)
	{
	    nickname = "";
	}

	myNickname = nickname;
	broadcast (UserEvent.userChangedNickname (this, nickname));
	setDescription ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Set the description for this user, based on the current state
     * of the object.
     */
    private void setDescription ()
    {
	ChatChannel[] channels = getKnownChannels ();

	setDescription (
            makeDescription (getName (), getTargetIdentity (), myNickname,
			     channels));
    }

    /**
     * Create a description for this user.
     *
     * @param name the name for this user
     * @param identity the target identity for this user
     * @param nickname null-ok; the nickname
     * @param channels null-ok; the array of channels
     */
    private static String makeDescription (String name, ChatIdentity identity,
					   String nickname, 
					   ChatChannel[] channels)
    {
	if (   (nickname == null)
	    || (nickname.length () == 0)
	    || nickname.equals (name))
	{
	    nickname = "";
	}
	else
	{
	    nickname = "/" + nickname;
	}
       
	String result = "user " + name + nickname + 
	    ", to " + identity.getName () + " on " +
	    identity.getTargetSystem ().getName ();
	
	if ((channels != null) && (channels.length != 0))
	{
	    result += "; channels:";
	    for (int i = 0; i < channels.length; i++)
	    {
		result += " " + channels[i].getName ();
	    }
	}

	return result;
    }

    /**
     * Send the initial set of events to a newly-added listener.
     *
     * @param listener the listener that was added
     */
    private void sendInitialEvents (UserListener listener)
    {
	if (myNickname.length () != 0)
	{
	    UserEvent.userChangedNickname (this, myNickname).sendTo (listener);
	}
    }
}
