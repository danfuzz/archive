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

import com.milk.uberchat.command.LocusCommands;
import com.milk.uberchat.event.LocusEvent;
import com.milk.uberchat.event.LocusListener;
import com.milk.uberchat.iface.ChatEntity;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.BaseEvent;
import java.util.EventListener;
import java.util.Vector;

/**
 * This is an abstract base class that provides a lot of what all
 * <code>ChatLocus</code> implementors need. It deals with the plumbing
 * with systems and identities, and stuff like that.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseLocus
extends BaseEntity
implements ChatLocus
{
    /** the target system for this locus */
    private ChatSystem myTargetSystem;

    /** the target identity for this locus */
    private ChatIdentity myTargetIdentity;

    /** the commands management object */
    private LocusCommands myCommands;

    /** the current list of users in this locus */
    private Vector myUsers;

    /**
     * Construct a <code>BaseLocus</code>.
     *
     * @param name the name for this locus
     * @param canonicalName the canonical name for this locus
     * @param description the initial description for this locus
     * @param targetSystem the target system for this locus
     * @param targetIdentity the target identity for this locus; if it is
     * passed as null, then the <code>BaseLocus</code> being constructed
     * must in fact be a ChatIdentity, and it will become its own target
     * identity 
     */
    public BaseLocus (String name, String canonicalName,
		      String description,
		      ChatSystem targetSystem, ChatIdentity targetIdentity)
    {
	super (name, canonicalName, description);
	myTargetSystem = targetSystem;
	myTargetIdentity = (targetIdentity == null) 
	    ? (ChatIdentity) this 
	    : targetIdentity;
	myUsers = new Vector ();
	myCommands = new LocusCommands (this);
	addCommands (myCommands.getCommands ());

	if (targetSystem instanceof BaseEntity)
	{
	    // inherit debug flag from system if possible
	    myDebug = ((BaseEntity) targetSystem).myDebug;

	    if (myDebug)
	    {
		debugPrintln ("!!! BaseLocus (" + name + 
			      ", " + canonicalName + ", " + description +
			      ", " + targetSystem + ", " + targetIdentity +
			      ")");
	    }
	}
    }

    // ------------------------------------------------------------------------
    // ChatLocus interface methods

    /**
     * Get the identity that this locus is directed at.
     *
     * @return the identity for this locus
     */
    public final ChatIdentity getTargetIdentity ()
    {
	return myTargetIdentity;
    }

    /**
     * Get the system that this locus is connected to.
     *
     * @return the system for this locus
     */
    public final ChatSystem getTargetSystem ()
    {
	return myTargetSystem;
    }

    /**
     * Given a canonical name, return the user in this locus with
     * that name, but only if the user is already known. That is,
     * asking for the name does not imply that it should be found,
     * and no querying of the host system should be done to satisfy
     * this request.
     *
     * @param name the canonical name to look up
     * @return the user found, or null if not
     */
    public final ChatUser getKnownUser (String name)
    {
	// BUG--linear search
	int sz = myUsers.size ();
	for (int i = 0; i < sz; i++)
	{
	    ChatUser u = (ChatUser) myUsers.elementAt (i);
	    if (u.getCanonicalName () == name)
	    {
		return u;
	    }
	}

	return null;
    }

    /**
     * Return the number of currently-known users in this locus.
     *
     * @return the count of known channels
     */
    public final int getKnownUserCount ()
    {
	return myUsers.size ();
    }

    /**
     * Return the list of currently-known users in this locus.
     * That is, no querying of the host should be done to satisfy this
     * request.
     *
     * @return the list (array, actually) of currently-known users
     */
    public final ChatUser[] getKnownUsers ()
    {
	ChatUser[] result = new ChatUser[myUsers.size ()];
	myUsers.copyInto (result);
	return result;
    }

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Add a new user to this locus. This will automatically send out the
     * right events to the right listeners. Also, if the target identity of
     * this locus is also a <code>BaseLocus</code> and this locus is not in
     * fact it's own target identity, then the identity will also be
     * informed of the existence of this user. If the argument is passed as
     * null, it is silently ignored.
     *
     * @param user null-ok; the user to add 
     */
    protected final void addUser (ChatUser user)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".addUser (" + user + ")");
	}

	if (user == null)
	{
	    return;
	}

	if (! myUsers.contains (user))
	{
	    ChatIdentity targIdent = getTargetIdentity ();
	    if (   (targIdent != this)
		&& (targIdent instanceof BaseLocus))
	    {
		((BaseLocus) targIdent).addUser (user);
	    }
	    myUsers.addElement (user);
	    userAdded (user);
	    broadcast (LocusEvent.userAdded (this, user));
	}
    }

    /**
     * Remove a user from this locus. This will automatically send out
     * the right events to the right listeners. If the argument is passed
     * as null, it is silently ignored.
     *
     * @param user null-ok; the user to remove
     */
    protected final void removeUser (ChatUser user)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".removeUser (" + user + ")");
	}

	if (user == null)
	{
	    return;
	}

	boolean removed = myUsers.removeElement (user);

	if (removed)
	{
	    userRemoved (user);
	    broadcast (LocusEvent.userRemoved (this, user));
	}
    }

    /**
     * Set the user list for this locus. This will automatically send out
     * the right events to the right listeners.
     *
     * @param users null-ok; the new user list
     */
    protected final void setUsers (ChatUser[] users)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".setUsers (" + users + ")");
	}

	Vector removeList;
	if ((users == null) || (users.length == 0))
	{
	    // easy case of clearing the users list
	    removeList = myUsers;
	}
	else
	{
	    removeList = (Vector) myUsers.clone ();
	    for (int i = 0; i < users.length; i++)
	    {
		ChatUser u = users[i];
		int at = myUsers.indexOf (u);
		if (at == -1)
		{
		    // wasn't there; add it
		    addUser (u);
		}
		else
		{
		    // was already there; just remove it from the remove list
		    removeList.removeElement (u);
		}
	    }
	}

	// whatever's left in the remove list are users that have gone away
	for (int i = removeList.size () - 1; i >= 0; i--)
	{
	    ChatUser u = (ChatUser) removeList.elementAt (i);
	    removeUser (u);
	}
    }

    /**
     * Tell all the users in this locus that an uber-listener was added to
     * them. <code>BaseLocus.uberListenerAdded()</code> doesn't do this
     * automatically since some subclasses won't want that to happen. For
     * example, <code>BaseChannel</code> and <code>BaseUser</code> do not
     * want to tell their users when an uber-listener is added (in the case
     * of <code>BaseUser</code>, it would cause an infinite recursion!).
     * Note that users only get informed if they are in fact subclasses of
     * <code>BaseEntity</code>.
     *
     * @param listener the listener that was added 
     */
    protected final void uberListenerAddedToUsers (EventListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".uberListenerAddedToUsers (" + 
			  listener + ")");
	}
	int sz = myUsers.size ();
	for (int i = 0; i < sz; i++)
	{
	    Object user = myUsers.elementAt (i);
	    if (user instanceof BaseEntity)
	    {
		((BaseEntity) user).uberListenerAdded (listener);
	    }
	}
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
	if (listener instanceof LocusListener)
	{
	    sendInitialEvents ((LocusListener) listener);
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
	if (listener instanceof LocusListener)
	{
	    sendInitialEvents ((LocusListener) listener);
	}
    }

    // ------------------------------------------------------------------------
    // Public methods that subclasses must override

    /**
     * Update the list of users in this locus. Subclasses should override
     * it to do whatever they need to do to determine the current list of
     * users and then call <code>addUser()</code>,
     * <code>removeUser()</code>, and/or <code>setUsers()</code> to
     * appropriately set things up. This should preferably happen
     * asynchronously to the call to this method.
     */
    public abstract void updateUsers ();

    /**
     * Speak in this locus. Subclasses must implement this to Do The Right
     * Thing for the actual locus in question.
     *
     * @param kind the kind of speech
     * @param text the message text 
     */
    public abstract void speak (String kind, String text);

    // ------------------------------------------------------------------------
    // Protected methods that subclasses may want to override

    /**
     * <code>BaseLocus</code> calls this method when a user
     * has been added. By default, it does nothing, but subclasses may
     * want to override this to perform appropriate housekeeping. If
     * this method is in fact overridden, subclasses should make sure
     * to call <code>super.userAdded()</code> to make sure all the
     * superclasses get a chance to do their respective things.
     *
     * @param user the user that was added
     */
    protected void userAdded (ChatUser user)
    {
	// this space intentionally left blank
    }

    /**
     * <code>BaseLocus</code> calls this method when a user
     * has been removed. By default, it does nothing, but subclasses may
     * want to override this to perform appropriate housekeeping. If
     * this method is in fact overridden, subclasses should make sure
     * to call <code>super.userRemoved()</code> to make sure all the
     * superclasses get a chance to do their respective things.
     *
     * @param user the user that was removed
     */
    protected void userRemoved (ChatUser user)
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Send the initial set of events to a newly-added listener.
     *
     * @param listener the listener that was added
     */
    private void sendInitialEvents (LocusListener listener)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".sendInitialEvents (" + 
			  listener + ")");
	}
	// send out enter events for all the users
	int sz = myUsers.size ();
	for (int i = 0; i < sz; i++)
	{
	    ChatUser u = (ChatUser) myUsers.elementAt (i);
	    LocusEvent.userAdded (this, u).sendTo (listener);
	}
    }
}
