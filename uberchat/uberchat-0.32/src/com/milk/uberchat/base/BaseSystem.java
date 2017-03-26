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

import com.milk.objed.Editor;
import com.milk.uberchat.command.SystemCommands;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.SystemEvent;
import com.milk.uberchat.event.SystemListener;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.uberchat.iface.ChatSystemTemplate;
import com.milk.util.BaseEvent;
import com.milk.util.BugInSubclassException;
import com.milk.util.ShouldntHappenException;
import java.util.EventListener;
import java.util.Vector;

/**
 * <p>This is an abstract base class that provides a lot of what all
 * <code>ChatSystem</code> implementors need. It deals with automatically
 * sending a lot of the needed events, such as for identity creation and
 * destruction, etc.</p>
 *
 * <p>Note that <code>BaseSystem</code> completely takes care of setting
 * the description for its <code>ChatEntity</code> nature, so subclasses
 * should <i>not</i> call <code>BaseEntity.setDescription()</code>.
 * Unfortunately, Java doesn't provide a way to specify this restriction
 * programatically. It just sucks that way sometimes.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseSystem
extends BaseEntity
implements ChatSystem
{
    /** the current connection state */
    private int myConnectionState;

    /** the current list of active identities */
    private Vector myIdentities;

    /** null-ok; the template object for this system, if any */
    private ChatSystemTemplate myTemplate;

    /** the commands management object */
    private SystemCommands myCommands;

    /** the editor object */
    private Editor myEditor;

    /**
     * Construct a <code>BaseSystem</code>.
     *
     * @param name the name for this system
     */
    public BaseSystem (String name)
    {
	super (name, name.intern (), "");
	myConnectionState = DISCONNECTED;
	myIdentities = new Vector ();
	myTemplate = null;
	myEditor = null;
	setDescription ();
	myCommands = new SystemCommands (this);
	addCommands (myCommands.getCommands ());
    }

    // ------------------------------------------------------------------------
    // ChatSystem interface methods

    /**
     * Get the list of <code>ChatIdentity</code> objects for this system.
     *
     * @return the list (array actually) of <code>ChatIdentity</code>
     * objects 
     */
    public final ChatIdentity[] getIdentities ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".getIdentities ()");
	}

	ChatIdentity[] result = new ChatIdentity[myIdentities.size ()];
	myIdentities.copyInto (result);
	return result;
    }

    /**
     * Attempt to connect to this system.
     */
    public final void connect ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".connect ()");
	}

	if (myConnectionState != DISCONNECTED)
	{
	    String msg = " system \"" + getName () + "\".";

	    switch (myConnectionState)
	    {
		case CONNECTED: 
		{
		    msg = "You are already connected to" + msg; 
		    break;
		}
		case CONNECTING: 
		{
		    msg = "You are already connecting to" + msg; 
		    break;
		}
		case DISCONNECTING: 
		{
		    msg = "You are in the process of disconnecting from" + 
			msg + "\nYou cannot re-connect until you have " +
			"finished disconnecting."; 
		    break;
		}
		default: 
		{
		    throw new ShouldntHappenException (
		        "Bogus connect error on" + msg);
		}
	    }

	    broadcast (ErrorEvent.errorReport (this, msg));
	    return;
	}

	myConnectionState = CONNECTING;
	setDescription ();
	broadcast (SystemEvent.systemConnecting (this));
	systemConnect ();
    }

    /**
     * Disconnect from this system.
     */
    public final void disconnect ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".disconnect ()");
	}

	if (myConnectionState != CONNECTED)
	{
	    String msg = " system \"" + getName () + "\".";

	    switch (myConnectionState)
	    {
		case DISCONNECTED: 
		{
		    msg = "You are not connected to" + msg; 
		    break;
		}
		case DISCONNECTING: 
		{
		    msg = "You are already disconnecting from" + msg; 
		    break;
		}
		case CONNECTING: 
		{
		    msg = "You are in the process of connecting to" + 
			msg + "\nYou cannot disconnect until you have " +
			"finished connecting."; 
		    break;
		}
		default: 
		{
		    throw new ShouldntHappenException (
		        "Bogus disconnect error on" + msg);
		}
	    }

	    broadcast (ErrorEvent.errorReport (this, msg));
	    return;
	}

	myConnectionState = DISCONNECTING;
	setDescription ();
	broadcast (SystemEvent.systemDisconnecting (this));
	systemDisconnect ();
    }

    /**
     * Return the current state of the connection of this system to its
     * host.
     *
     * @return the current state of the connection 
     */
    public final int getConnectionState ()
    {
	return myConnectionState;
    }

    /**
     * Get a template which is suitable for handing to a factory to
     * recreate a <code>ChatSystem</code> like this one.
     *
     * @return null-ok; a suitable template, if any 
     */
    public final ChatSystemTemplate getTemplate ()
    {
	return myTemplate.copy ();
    }

    // ------------------------------------------------------------------------
    // Editable interface methods

    /**
     * Get the editor for this system. The editor for a <code>BaseSystem</code>
     * is in fact the editor for the internal <code>ChatSystemTemplate</code>
     * object.
     *
     * @return non-null; the editor
     */
    public final Editor getEditor ()
    {
	if (myEditor == null)
	{
	    myEditor = myTemplate.getEditor ();
	}

	return myEditor;
    }

    // ------------------------------------------------------------------------
    // Protected helper methods

    /**
     * Inform the system that it has been connected. This will set the
     * state appropriately and will send out a <code>systemConnected</code>
     * event. 
     */
    protected final void systemConnected ()
    {
	if (myConnectionState != CONNECTING)
	{
	    throw new BugInSubclassException (
                "Subclass made a call to systemConnected() on a system in " +
		"an inappropriate state.");
	}

	myConnectionState = CONNECTED;
	setDescription ();
	broadcast (SystemEvent.systemConnected (this));
    }

    /**
     * Inform the system that it has been disconnected. This will set the
     * state appropriately and will send out a
     * <code>systemDisconnected</code> event. Additionally, if there are
     * any identities that haven't been removed, this will cause them to be
     * removed. 
     */
    protected final void systemDisconnected ()
    {
	if (myConnectionState == DISCONNECTED)
	{
	    throw new BugInSubclassException (
                "Subclass made a call to systemDisconnected() on a system " +
		"in an inappropriate state.");
	}

	removeAllIdentities ();
	myConnectionState = DISCONNECTED;
	setDescription ();
	broadcast (SystemEvent.systemDisconnected (this));
    }

    /**
     * Add a new identity to the system. This will automatically send an
     * <code>identityAdded</code> event to the listeners of this system.
     * Note that it is only valid to add an identity to a connected system.
     *
     * @param identity the identity to add 
     */
    protected final void addIdentity (ChatIdentity identity)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".addIdentity (" + identity + ")");
	}

	if (myConnectionState != CONNECTED)
	{
	    throw new BugInSubclassException (
                "Subclass attempted to add an identity to a non-connected " +
		"system.");
	}
	
	if (!myIdentities.contains (identity))
	{
	    myIdentities.addElement (identity);
	    if (identity instanceof BaseIdentity)
	    {
		((BaseIdentity) identity).identityAdded ();
	    }
	    broadcast (SystemEvent.identityAdded (identity));
	}
    }

    /**
     * Remove an identity from the system. This will automatically send an
     * <code>identityRemoved</code> event to the listeners of this
     * system.
     *
     * @param identity the identity to remove 
     */
    protected final void removeIdentity (ChatIdentity identity)
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".removeIdentity (" + 
			  identity + ")");
	}

	boolean removed = myIdentities.removeElement (identity);
	if (removed)
	{
	    if (identity instanceof BaseIdentity)
	    {
		((BaseIdentity) identity).identityRemoved ();
	    }
	    broadcast (SystemEvent.identityRemoved (identity));
	}
    }

    /**
     * Remove all the identities from the system. This means that the
     * system has been disconnected. This will automatically send an
     * <code>identityRemoved</code> event for each of the (former)
     * identities of the system.
     */
    protected final void removeAllIdentities ()
    {
	if (myDebug)
	{
	    debugPrintln ("!!! " + this + ".removeAllIdentities ()");
	}

	int sz = myIdentities.size ();
	for (int i = myIdentities.size () - 1; i >= 0; i--)
	{
	    removeIdentity ((ChatIdentity) myIdentities.elementAt (i));
	}
    }

    /**
     * Set the template to associate with this system. <code>BaseSystem</code>
     * will never hand this out directly, rather it always hands out a
     * copy, so subclasses should feel free to edit the template at will
     * and not worry about external hands messing with things.
     *
     * @param template the template to associate with this system
     */
    protected final void setTemplate (ChatSystemTemplate template)
    {
	myTemplate = template;
    }

    // ------------------------------------------------------------------------
    // Public methods that must be overridden

    /**
     * <code>BaseSystem</code> leaves it up to the subclass to decide how
     * to canonicalize user names.
     *
     * @param orig the original name
     * @return the canonical form 
     */
    public abstract String canonicalUserName (String orig);

    /**
     * <code>BaseSystem</code> leaves it up to the subclass to decide how
     * to canonicalize channel names.
     *
     * @param orig the original name
     * @return the canonical form 
     */
    public abstract String canonicalChannelName (String orig);

    // ------------------------------------------------------------------------
    // Protected methods for subclasses to override

    /**
     * <code>BaseSystem</code> calls this method when it actually wants a
     * connection to happen. It will only call this if the system is not
     * actually connected. By the time this method is called, a
     * <code>systemConnecting</code> event will have already been sent out.
     * Once the connection succeeds or fails, the subclass should call
     * <code>systemConnected()</code> or <code>systemDisconnected()</code>,
     * respectively. 
     */
    protected abstract void systemConnect ();

    /**
     * <code>BaseSystem</code> calls this method when it actually wants to
     * disconnect the system. It will only call this if the system is
     * actually connected. By the time this method is called, a
     * <code>systemDisconnecting</code> event will have already been sent
     * out. Once the connection is finally terminated, the subclass should
     * call <code>systemDisconnected()</code>. 
     */
    protected abstract void systemDisconnect ();

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
	if (listener instanceof SystemListener)
	{
	    sendInitialEvents ((SystemListener) listener);
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
	if (listener instanceof SystemListener)
	{
	    sendInitialEvents ((SystemListener) listener);
	}

	// tell all the identities
	int sz = myIdentities.size ();
	for (int i = 0; i < sz; i++)
	{
	    Object ident = myIdentities.elementAt (i);
	    if (ident instanceof BaseEntity)
	    {
		((BaseEntity) ident).uberListenerAdded (listener);
	    }
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
    // Private helper methods

    /**
     * Set the description of this system, based on the name and
     * connection status.
     */
    private void setDescription ()
    {
	StringBuffer s = new StringBuffer (100);
	s.append (getName ());
	s.append (", ");
	switch (myConnectionState)
	{
	    case CONNECTED: s.append ("connected");         break;
	    case CONNECTING: s.append ("connecting");       break;
	    case DISCONNECTED: s.append ("unconnected");    break;
	    case DISCONNECTING: s.append ("disconnecting"); break;
	}
	setDescription (s.toString ());
    }

    /**
     * Send the initial set of events to a newly-added listener.
     *
     * @param listener the listener that was added
     */
    private void sendInitialEvents (SystemListener listener)
    {
	// send out the appropriate event(s) for each possible state
	switch (myConnectionState)
	{
	    case CONNECTED:
	    {
		SystemEvent.systemConnected (this).sendTo (listener);

		// send out creation events for all the identities
		int sz = myIdentities.size ();
		for (int i = 0; i < sz; i++)
		{
		    ChatIdentity ident =
			(ChatIdentity) myIdentities.elementAt (i);
		    SystemEvent.identityAdded (ident).sendTo (listener);
		}
		break;
	    }
	    case CONNECTING:
	    {
		SystemEvent.systemConnecting (this).sendTo (listener);
		break;
	    }
	    case DISCONNECTED:
	    {
		// we don't do anything for this one
		break;
	    }
	    case DISCONNECTING:
	    {
		SystemEvent.systemDisconnecting (this).sendTo (listener);
		break;
	    }
	}
    }
}
