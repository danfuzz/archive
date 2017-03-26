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

import com.milk.uberchat.command.ChannelCommands;
import com.milk.uberchat.event.ChannelEvent;
import com.milk.uberchat.event.ChannelListener;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.util.EventListener;

/**
 * This is an abstract base class that provides a lot of what
 * all <code>ChatChannel</code> implementors need.
 *
 * <p>Note that <code>BaseChannel</code> completely takes care of setting
 * the description for its <code>ChatEntity</code> nature, so subclasses
 * should <i>not</i> call <code>BaseEntity.setDescription()</code>.
 * Unfortunately, Java doesn't provide a way to specify this restriction
 * programatically. It just sucks that way sometimes.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseChannel
extends BaseLocus
implements ChatChannel
{
    /** the current topic of this channel */
    private String myTopic;

    /** the current joined state of the channel 
     * @see com.milk.uberchat.iface.ChatChannel#getJoinedState */
    private int myJoinedState;

    /** the commands management object */
    private ChannelCommands myCommands;

    /**
     * Construct a <code>BaseChannel</code>.
     *
     * @param name the name for this channel
     * @param targetIdentity the target identity for this channel
     * @param topic null-ok; the initial topic for the channel
     */
    public BaseChannel (String name, ChatIdentity targetIdentity, String topic)
    {
	// note duplicate method calls; can't be helped since Java forces
	// the call to super to be the first thing in a constructor body;
	// Java just sucks that way sometimes
	super (name, 
	       targetIdentity.getTargetSystem ().canonicalChannelName (name),
	       makeDescription (name, targetIdentity, topic, LEFT),
	       targetIdentity.getTargetSystem (), 
	       targetIdentity);

	myTopic = (topic == null) ? "" : topic;
	myJoinedState = LEFT;

	myCommands = new ChannelCommands (this);
	addCommands (myCommands.getCommands ());

	if (myDebug)
	{
	    debugPrintln ("!!! BaseChannel (" + name + ", " + targetIdentity +
			  ", " + topic + ")");
	}
    }

    // ------------------------------------------------------------------------
    // ChatChannel interface methods

    /**
     * Get the topic of this channel. It returns <code>""</code> if the
     * topic is empty or unknown.
     *
     * @return the topic of this channel 
     */
    public final String getTopic ()
    {
	return myTopic;
    }

    /**
     * Get the joined state of this channel. It is one of
     * <code>ChatChannel.JOINED</code>, <code>ChatChannel.JOINING</code>,
     * <code>ChatChannel.LEFT</code>, or <code>ChatChannel.LEAVING</code>.
     *
     * @return the joined state of this channel 
     */
    public int getJoinedState ()
    {
	return myJoinedState;
    }

    /**
     * (Attempt to) join this channel.
     */
    public final void join ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".join ()");
	}

	if (myJoinedState != LEFT)
	{
	    String msg = " channel \"" + getName () + "\".";

	    switch (myJoinedState)
	    {
		case JOINED: msg = "You are already on" + msg; break;
		case JOINING: msg = "You are already joining" + msg; break;
		case LEAVING: msg = "You are in the process of leaving" + msg +
				  "\nYou cannot re-join until you have " +
				  "finished leaving."; break;
		default: 
		{
		    throw new ShouldntHappenException (
		        "Bogus join error on" + msg);
		}
	    }

	    broadcast (ErrorEvent.errorReport (this, msg));
	    return;
	}

	myJoinedState = JOINING;
	setDescription ();
	broadcast (ChannelEvent.joiningChannel (this));
	channelJoin ();
    }

    /**
     * (Attempt to) leave this channel.
     */
    public final void leave ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".leave ()");
	}

	if (myJoinedState != JOINED)
	{
	    String msg = " channel \"" + getName () + "\".";

	    switch (myJoinedState)
	    {
		case LEFT: msg = "You are not on" + msg; break;
		case LEAVING: msg = "You are already leaving" + msg; break;
		case JOINING: msg = "You are in the process of joining" + msg +
				  "\nYou cannot leave until you have " +
				  "finished joining."; break;
		default: 
		{
		    throw new ShouldntHappenException (
		        "Bogus leave error on" + msg);
		}
	    }

	    broadcast (ErrorEvent.errorReport (this, msg));
	    return;
	}

	myJoinedState = LEAVING;
	setDescription ();
	broadcast (ChannelEvent.leavingChannel (this));
	channelLeave ();
    }

    /**
     * Join the channel if it isn't joined, leave it if it is, or do nothing
     * (possibly signalling an error), if it is in a transitional state.
     */
    public final void joinOrLeave ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".leave ()");
	}

	switch (myJoinedState)
	{
	    case LEAVING:
	    case LEFT: 
	    {
		join (); 
		break;
	    }
	    case JOINING:
	    case JOINED: 
	    {
		leave (); 
		break;
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Public methods that subclasses must override

    /**
     * (Attempt to) set the topic of this channel.
     *
     * @param topic the new topic for the channel.
     */
    public abstract void setTopic (String topic);

    // ------------------------------------------------------------------------
    // Protected methods for subclasses to override

    /**
     * <code>BaseChannel</code> calls this method when it actually wants to
     * try to do a channel join. It will only call this if the channel has
     * not in fact already been joined and is not in the process of being
     * joined. By the time this is called, the state of the channel is
     * already <code>JOINING</code>, and it is up to the subclass to call
     * <code>joinedChannel()</code> or <code>leftChannel()</code> depending
     * on the success or failure of the join attempt. In the case of success,
     * it is also sufficient merely to call <code>addUser()</code> with
     * the identity user as the argument.
     */
    protected abstract void channelJoin ();

    /**
     * <code>BaseChannel</code> calls this method when it actually wants to
     * try to do a channel leave. It will only call this if the channel is
     * in fact currently joined. By the time this is called, the state of
     * the channel is already <code>LEAVING</code>, and it is up to the
     * subclass to call <code>leftChannel()</code> or
     * <code>joinedChannel()</code> depending on the success or failure of
     * the leave attempt. In the case of success, it is also sufficient
     * merely to call <code>removeUser()</code> with the identity user as
     * the argument. 
     */
    protected abstract void channelLeave ();

    // ------------------------------------------------------------------------
    // Protected methods that we override

    /**
     * <code>BaseEntity</code> calls this method when an
     * <code>EventListener</code> has been added.
     *
     * @param listener the listener that was added 
     */
    protected void listenerAdded (EventListener listener)
    {
	super.listenerAdded (listener);
	if (listener instanceof ChannelListener)
	{
	    sendInitialEvents ((ChannelListener) listener);
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
	if (listener instanceof ChannelListener)
	{
	    sendInitialEvents ((ChannelListener) listener);
	}
    }

    /**
     * <p>Broadcast an event to the appropriate uber-listeners of this
     * entity. <code>BaseChannel</code> uses this to send the event to its
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
     * <code>BaseLocus</code> calls this method when a user
     * has been added. We use this to make sure that the user knows
     * that it is in this channel, but we can only do this if the
     * user in fact a <code>BaseChannelHolder</code>. Also, if the
     * user is the identity user, this makes sure that the channel is
     * considered joined.
     *
     * @param user the user that was added
     */
    protected void userAdded (ChatUser user)
    {
	super.userAdded (user);
	if (user instanceof BaseChannelHolder)
	{
	    ((BaseChannelHolder) user).addChannel (this);
	}

	if (user == getTargetIdentity ().getIdentityUser ())
	{
	    joinedChannel ();
	}
    }

    /**
     * <code>BaseLocus</code> calls this method when a user
     * has been removed. We use this to make sure that the user knows
     * that it is not in this channel, but we can only do this if the
     * user in fact a <code>BaseChannelHolder</code>. Also, if the
     * user is the identity user, this makes sure that the channel is
     * considered left.
     *
     * @param user the user that was removed
     */
    protected void userRemoved (ChatUser user)
    {
	super.userRemoved (user);
	if (user instanceof BaseChannelHolder)
	{
	    ((BaseChannelHolder) user).removeChannel (this);
	}

	if (user == getTargetIdentity ().getIdentityUser ())
	{
	    leftChannel ();
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
     * Tell the <code>BaseChannel</code> that the identity for this channel
     * has in fact joined the channel. It sends out appropriate events,
     * and makes sure that the identity user is in fact in the channel.
     */
    protected final void joinedChannel ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".joinedChannel ()");
	}

	if (myJoinedState == JOINED)
	{
	    // already joined
	    return;
	}

	myJoinedState = JOINED;
	addUser (getTargetIdentity ().getIdentityUser ());
	broadcast (ChannelEvent.joinedChannel (this));
	setDescription ();
    }

    /**
     * Tell the <code>BaseChannel</code> that the identity for this channel
     * has left the channel. It sends out appropriate events, and makes
     * sure that the identity user is not in fact in the channel. 
     */
    protected final void leftChannel ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".leftChannel ()");
	}

	if (myJoinedState == LEFT)
	{
	    // already left
	    return;
	}

	myJoinedState = LEFT;
	removeUser (getTargetIdentity ().getIdentityUser ());
	broadcast (ChannelEvent.leftChannel (this));
	setDescription ();
    }

    /**
     * Tell the <code>BaseChannel</code> that the topic has changed.
     *
     * @param topic null-ok; the new topic for the channel
     */
    protected final void topicChanged (String topic)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".topicChanged (" + topic + ")");
	}

	if (topic == null)
	{
	    topic = "";
	}

	if (topic.equals (myTopic))
	{
	    return;
	}

	myTopic = topic;
	broadcast (ChannelEvent.topicChanged (this, topic));
	setDescription ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Set the description for this channel based on the current state.
     */
    private void setDescription ()
    {
	setDescription (
            makeDescription (getName (), getTargetIdentity (), myTopic,
			     myJoinedState));
    }

    /**
     * Create a description for this channel.
     *
     * @param name the name for this channel
     * @param identity the target identity for this identity
     * @param topic null-ok; the topic
     * @param joined the joined state
     */
    private static String makeDescription (String name, ChatIdentity identity,
					   String topic, int joined)
    {
	String result = "chan " + name + ", " + identity.getName () + " on " +
	    identity.getTargetSystem ().getName ();
	
	if ((topic != null) && (topic.length () != 0))
	{
	    result += "; topic: " + topic;
	}

	switch (joined)
	{
	    case JOINED:  result += " [joined]";  break;
	    case JOINING: result += " [joining]"; break;
	    case LEAVING: result += " [leaving]"; break;
	}

	return result;
    }

    /**
     * Send the initial set of events to a newly-added listener.
     *
     * @param listener the listener that was added
     */
    private void sendInitialEvents (ChannelListener listener)
    {
	switch (myJoinedState)
	{
	    case JOINED:  ChannelEvent.joinedChannel (this).sendTo (listener);
		          break;
	    case JOINING: ChannelEvent.joiningChannel (this).sendTo (listener);
		          break;
	    case LEAVING: ChannelEvent.leavingChannel (this).sendTo (listener);
		          break;
	}

	if (myTopic.length () != 0)
	{
	    ChannelEvent.topicChanged (this, myTopic).sendTo (listener);
	}
    }
}
