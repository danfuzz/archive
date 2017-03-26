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

import com.milk.uberchat.command.IdentityCommands;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.LocusEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * <p>This is an abstract base class that provides a lot of what
 * all <code>ChatIdentity</code> implementors need.</p>
 *
 * <p>Note that <code>BaseIdentity</code> completely takes care of setting
 * the description for its <code>ChatEntity</code> nature, so subclasses
 * should <i>not</i> call <code>BaseEntity.setDescription()</code>.
 * Unfortunately, Java doesn't provide a way to specify this restriction
 * programatically. It just sucks that way sometimes.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseIdentity
extends BaseChannelHolder
implements ChatIdentity
{
    /** the <code>ChatUser</code> that corresponds to this identity */
    private ChatUser myUser;

    /** the commands management object */
    private IdentityCommands myCommands;

    /**
     * Construct a <code>BaseIdentity</code>.
     *
     * @param name the name for this identity, generally a userid of some sort
     * @param targetSystem the target system for this identity
     */
    public BaseIdentity (String name, ChatSystem targetSystem)
    {
	super (name, 
	       targetSystem.canonicalUserName (name),
	       makeDescription (name, targetSystem),
	       targetSystem, 
	       null);
	myUser = null;

	myCommands = new IdentityCommands (this);
	addCommands (myCommands.getCommands ());

	if (myDebug)
	{
	    debugPrintln ("!!! BaseIdentity (" + name + ", " + targetSystem +
			  ")");
	}
    }

    // ------------------------------------------------------------------------
    // ChatIdentity interface methods

    /**
     * Get the <code>ChatUser</code> that represents this identity.
     *
     * @return the identity user
     */
    public final ChatUser getIdentityUser ()
    {
	if (myUser == null)
	{
	    myUser = identityGetIdentityUser ();
	    addUser (myUser);
	}

	return myUser;
    }

    /**
     * Get the current nickname/description of this identity.
     *
     * @return the nickname
     */
    public final String getNickname ()
    {
	return getIdentityUser ().getNickname ();
    }

    /**
     * Turn a string name into a <code>ChatChannel</code>, if possible, or
     * return null if not successful.
     *
     * @param name the name of the channel
     * @return the corresponding <code>ChatChannel</code> 
     */
    public final ChatChannel nameToChannel (String name)
    {
	String cname = getTargetSystem ().canonicalChannelName (name);

	if (cname == null)
	{
	    return null;
	}

	ChatChannel chan = getKnownChannel (cname);

	if (chan == null)
	{
	    // not found; ask subclass to create it
	    chan = identityMakeChannel (name);

	    if (chan != null) 
	    {
		addChannel (chan);
	    }
	}
	return chan;
    }

    /**
     * Turn a string name into a <code>ChatUser</code>, if possible, or
     * return null if not successful.
     *
     * @param name the name of the user
     * @return the corresponding <code>ChatUser</code>
     */
    public final ChatUser nameToUser (String name)
    {
	String cname = getTargetSystem ().canonicalUserName (name);

	if (cname == null)
	{
	    return null;
	}

	ChatUser user = getKnownUser (cname);

	if (user == null)
	{
	    // not found; ask subclass to create it
	    user = identityMakeUser (name);
	    if (user != null)
	    {
		addUser (user);
	    }
	}

	return user;
    }

    /**
     * Join or leave a named channel. This should behave more or less like
     * <code>nameToChannel (name).joinOrLeave ()</code>.
     *
     * @param name the name of the channel
     */
    public final void joinOrLeaveChannel (String name)
    {
	ChatChannel chan = nameToChannel (name);
	if (chan == null)
	{
	    broadcast (ErrorEvent.errorReport (this, 
					       "Bad channel name: " + name));
	    return;
	}

	chan.joinOrLeave ();
    }

    /**
     * Speak to a named channel. This should behave more or less like
     * <code>nameToChannel (name).speak (kind, text)</code>.
     *
     * @param name the name of the channel
     * @param kind the speech kind
     * @param text the text to speak
     */
    public final void speakToChannel (String name, String kind, String text)
    {
	ChatChannel chan = nameToChannel (name);
	if (chan == null)
	{
	    broadcast (ErrorEvent.errorReport (this, 
					       "Bad channel name: " + name));
	    return;
	}

	chan.speak (kind, text);
    }

    /**
     * Speak to a named user. This should behave more or less like
     * <code>nameToUser (name).speak (kind, text)</code>.
     *
     * @param name the name of the user
     * @param kind the speech kind
     * @param text the text to speak
     */
    public final void speakToUser (String name, String kind, String text)
    {
	ChatUser user = nameToUser (name);
	if (user == null)
	{
	    broadcast (ErrorEvent.errorReport (this, 
					       "Bad user name: " + name));
	    return;
	}

	user.speak (kind, text);
    }

    // ------------------------------------------------------------------------
    // Public methods that subclasses must override

    /**
     * (Attempt to) change the nickname/description of this identity.
     *
     * @param newNick the new nickname
     */
    public abstract void setNickname (String newNick);

    // ------------------------------------------------------------------------
    // Protected methods that we override

    /**
     * <code>BaseEntity</code> calls this method when an uber-listener has
     * been added.
     *
     * @param listener the listener that was added 
     */
    protected void uberListenerAdded (EventListener listener)
    {
	super.uberListenerAdded (listener);

	// tell all the channels
	uberListenerAddedToChannels (listener);

	// and tell all the users
	uberListenerAddedToUsers (listener);
    }

    /**
     * <p>Broadcast an event to the appropriate uber-listeners of this
     * entity. <code>BaseIdentity</code> uses this to send the event
     * to its parent system.
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

	ChatSystem sys = getTargetSystem ();
	if (sys instanceof BaseEntity)
	{
	    ((BaseEntity) sys).uberBroadcast (event);
	}
    }

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel has
     * been removed. We use this to make sure that the identity is not
     * joined with the channel, since it shouldn't end up joined with a
     * non-existent channel. We also use this to inform the channel that it
     * has no users in it, since if the channel doesn't exist on an
     * identity, then it had better not have any users lurking around! The
     * notification can only work, though, if the given channel is in fact
     * a <code>BaseLocus</code> of some sort.
     *
     * @param channel the channel that was removed 
     */
    protected void channelRemoved (ChatChannel channel)
    {
	super.channelRemoved (channel);

	if (channel instanceof BaseLocus)
	{
	    ((BaseLocus) channel).setUsers (null);
	    if (   (channel instanceof BaseChannel)
		&& (channel.getJoinedState () != ChatChannel.LEFT))
	    {
		((BaseChannel) channel).leftChannel ();
	    }
	}
    }

    /**
     * <code>BaseLocus</code> calls this method when a user
     * has been removed. We use this to make sure that the user isn't
     * in any channels, but we can only do this if the user is
     * in fact a <code>BaseChannelHolder</code>.
     *
     * @param user the user that was removed
     */
    protected void userRemoved (ChatUser user)
    {
	super.userRemoved (user);
	if (user instanceof BaseChannelHolder)
	{
	    ((BaseChannelHolder) user).setChannels (null);
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
	setDescription (makeDescription (getName (), getTargetSystem ()));
    }

    // ------------------------------------------------------------------------
    // Protected methods for subclasses to override

    /**
     * <code>BaseIdentity</code> calls this when a client has requested a
     * channel by name, but it doesn't exist in its list of known channels.
     * The subclass should either make the channel or return null. It
     * should not call <code>addChannel()</code>; <code>BaseIdentity</code>
     * will do that for you.
     *
     * @param name the name of the channel to make
     * @return null-ok; the channel made, or null if it wasn't made 
     */
    protected abstract ChatChannel identityMakeChannel (String name);

    /**
     * <code>BaseIdentity</code> calls this when a client has requested a
     * user by name, but it doesn't exist in its list of known users. The
     * subclass should either make the user or return null. It should not
     * call <code>addUser()</code>; <code>BaseIdentity</code> will do that
     * for you.
     *
     * @param name the name of the user to make
     * @return null-ok; the user made, or null if it wasn't made 
     */
    protected abstract ChatUser identityMakeUser (String name);

    /**
     * <code>BaseIdentity</code> calls this exactly once, the first time a
     * client asks for the identity user for this identity.
     *
     * @return the identity user for this identity 
     */
    protected abstract ChatUser identityGetIdentityUser ();

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Inform this <code>BaseIdentity</code> that it has been added to
     * the system it is associated with. If the system is in fact a
     * <code>BaseSystem</code>, then it will call this method itself, when
     * appropriate. If not, it is up to the subclass to call this when
     * appropriate. 
     */
    protected final void identityAdded ()
    {
	// just make sure the identity user is all happy
	getIdentityUser ();
    }


    /**
     * Inform this <code>BaseIdentity</code> that it has been removed from
     * the system it is associated with. If the system is in fact a
     * <code>BaseSystem</code>, then it will call this method itself, when
     * appropriate. If not, it is up to the subclass to call this when
     * appropriate. 
     */
    protected final void identityRemoved ()
    {
	// just cut all the users and channels loose, except for the
	// identity user, which gets to stay
	ChatUser[] iduarr = new ChatUser[] { getIdentityUser () };
	setUsers (iduarr);
	setChannels (null);
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Create a description for this identity.
     *
     * @param name the name for this identity
     * @param system the target system for this identity
     */
    private static String makeDescription (String name, ChatSystem system)
    {
	return name + " on " + system.getName () + ", system";
    }
}
