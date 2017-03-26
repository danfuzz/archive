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

import com.milk.uberchat.command.ChannelHolderCommands;
import com.milk.uberchat.event.ChannelHolderEvent;
import com.milk.uberchat.event.ChannelHolderListener;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatChannelHolder;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatSystem;
import java.util.EventListener;
import java.util.Vector;

/**
 * This is an abstract base class that provides a lot of what
 * all <code>ChatChannelHolder</code> implementors need.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseChannelHolder
extends BaseLocus
implements ChatChannelHolder
{
    /** the list of known channels */
    private Vector myChannels;

    /** the commands management object */
    private ChannelHolderCommands myCommands;

    /**
     * Construct a <code>BaseChannelHolder</code>.
     *
     * @param name the name for this locus
     * @param canonicalName the canonical name for this locus
     * @param description the initial description for this locus
     * @param targetSystem the target system for this locus
     * @param targetIdentity the target identity for this locus; if it
     * is passed as null, then the object being constructed must in fact
     * be a <code>ChatIdentity</code>, and it will become its own target
     * identity
     */
    public BaseChannelHolder (String name, 
			      String canonicalName, String description,
			      ChatSystem targetSystem, 
			      ChatIdentity targetIdentity)
    {
	super (name, canonicalName, description,
	       targetSystem, targetIdentity);
	myChannels = new Vector ();
	myCommands = new ChannelHolderCommands (this);
	addCommands (myCommands.getCommands ());

	if (myDebug)
	{
	    debugPrintln ("!!! BaseChannelHolder (" + name + ", " + 
			  targetSystem + ")");
	}
    }

    // ------------------------------------------------------------------------
    // ChatChannelHolder interface methods

    /**
     * Given a canonical name, return the known channel that corresponds to
     * that name, or return null if there is no known channel with that
     * name. No querying of the host should be done to satisfy this
     * request.
     *
     * @param name the canonical channel name to look up
     * @return null-ok; the corresponding channel, if any 
     */
    public final ChatChannel getKnownChannel (String name)
    {
	// BUG--linear search
	int sz = myChannels.size ();
	for (int i = 0; i < sz; i++)
	{
	    ChatChannel c = (ChatChannel) myChannels.elementAt (i);
	    if (c.getCanonicalName () == name)
	    {
		return c;
	    }
	}

	return null;
    }

    /**
     * Return the number of currently-known channels for this object.
     *
     * @return the count of known channels
     */
    public final int getKnownChannelCount ()
    {
	return myChannels.size ();
    }

    /**
     * Return the list of currently-known channels for this object.
     *
     * @return the list (array, actually) of currently-known channels
     */
    public final ChatChannel[] getKnownChannels ()
    {
	ChatChannel[] result = new ChatChannel[myChannels.size ()];
	myChannels.copyInto (result);
	return result;
    }

    // ------------------------------------------------------------------------
    // Public methods that subclasses must override

    /**
     * Ask for the list of channels for this object to be updated from
     * the host. Subclasses must override this method to do something
     * appropriate. In particular, they should arrange for calls to
     * <code>addChannel()</code>, <code>removeChannel()</code>, and/or
     * <code>setChannels()</code> to happen, preferably asynchronously
     * to the actual call to this method.
     */
    public abstract void updateChannels ();

    // ------------------------------------------------------------------------
    // Protected methods that subclasses may want to override

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel
     * has been added. By default, it does nothing, but subclasses may
     * want to override this to perform appropriate housekeeping. If
     * this method is in fact overridden, subclasses should make sure
     * to call <code>super.channelAdded()</code> to make sure all the
     * superclasses get a chance to do their respective things.
     *
     * @param channel the channel that was added
     */
    protected void channelAdded (ChatChannel channel)
    {
	// this space intentionally left blank
    }

    /**
     * <code>BaseChannelHolder</code> calls this method when a channel
     * has been removed. By default, it does nothing, but subclasses may
     * want to override this to perform appropriate housekeeping. If
     * this method is in fact overridden, subclasses should make sure
     * to call <code>super.channelRemoved()</code> to make sure all the
     * superclasses get a chance to do their respective things.
     *
     * @param channel the channel that was removed
     */
    protected void channelRemoved (ChatChannel channel)
    {
	// this space intentionally left blank
    }

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
	if (listener instanceof ChannelHolderListener)
	{
	    sendInitialEvents ((ChannelHolderListener) listener);
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
	if (listener instanceof ChannelHolderListener)
	{
	    sendInitialEvents ((ChannelHolderListener) listener);
	}
    }

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Add a new channel to this object. This will automatically send out
     * the right events to the right listeners. Also, if the target
     * identity of this object is also a <code>BaseChannelHolder</code> and
     * this object isn't its own target identity, then the identity will be
     * informed of the given channel's existence. If null is passed, it is
     * silently ignored.
     *
     * @param channel null-ok; the channel to add 
     */
    protected final void addChannel (ChatChannel channel)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".addChannel (" + channel + ")");
	}

	if (channel == null)
	{
	    return;
	}

	if (! myChannels.contains (channel))
	{
	    ChatIdentity targIdent = getTargetIdentity ();
	    if (   (targIdent != this)
		&& (targIdent instanceof BaseChannelHolder))
	    {
		((BaseChannelHolder) targIdent).addChannel (channel);
	    }
	    myChannels.addElement (channel);
	    channelAdded (channel);
	    broadcast (ChannelHolderEvent.channelAdded (this, channel));
	}
    }

    /**
     * Remove a channel from this object. This will automatically send out
     * the right events to the right listeners. If null is passed, it is
     * silently ignored.
     *
     * @param channel null-ok; the channel to remove 
     */
    protected final void removeChannel (ChatChannel channel)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".removeChannel (" + channel + ")");
	}

	if (channel == null)
	{
	    return;
	}

	boolean removed = myChannels.removeElement (channel);

	if (removed)
	{
	    channelRemoved (channel);

	    broadcast (
                ChannelHolderEvent.channelRemoved (this, channel));
	}
    }

    /**
     * Set the channel list for this identity. This will automatically send
     * out the right events to the right listeners.
     *
     * @param channels null-ok; the new channel list 
     */
    protected final void setChannels (ChatChannel[] channels)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".setChannels (" + channels + ")");
	}

	Vector removeList;
	if ((channels == null) || (channels.length == 0))
	{
	    // easy case of clearing the channels list
	    removeList = myChannels;
	}
	else
	{
	    removeList = (Vector) myChannels.clone ();
	    for (int i = 0; i < channels.length; i++)
	    {
		ChatChannel c = channels[i];
		int at = myChannels.indexOf (c);
		if (at == -1)
		{
		    // wasn't there; add it
		    addChannel (c);
		}
		else
		{
		    // was already there; just remove it from the remove list
		    removeList.removeElement (c);
		}
	    }
	}

	// whatever's left in the remove list are channels that have gone
	// away
	for (int i = removeList.size () - 1; i >= 0; i--)
	{
	    ChatChannel c = (ChatChannel) removeList.elementAt (i);
	    removeChannel (c);
	}
    }

    /**
     * Tell all the channels in this object that an uber-listener was added
     * to them. <code>BaseChannelHolder.uberListenerAdded()</code> doesn't
     * do this automatically since some subclasses won't want that to
     * happen. In particular, <code>BaseUser</code> does not want to tell
     * its channels when an uber-listener is added. Note that channels only
     * get informed if they are in fact subclasses of
     * <code>BaseEntity</code>.
     *
     * @param listener the listener that was added 
     */
    protected final void uberListenerAddedToChannels (EventListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".uberListenerAddedToChannels (" + 
			  listener + ")");
	}
	int sz = myChannels.size ();
	for (int i = 0; i < sz; i++)
	{
	    Object chan = myChannels.elementAt (i);
	    if (chan instanceof BaseEntity)
	    {
		((BaseEntity) chan).uberListenerAdded (listener);
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Send the initial set of events to a newly-added listener.
     *
     * @param listener the listener that was added
     */
    private void sendInitialEvents (ChannelHolderListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".sendInitialEvents (" + 
			  listener + ")");
	}
	int sz = myChannels.size ();
	for (int i = 0; i < sz; i++)
	{
	    ChatChannel c = (ChatChannel) myChannels.elementAt (i);
	    ChannelHolderEvent.channelAdded (this, c).sendTo (listener);
	}
    }
}
