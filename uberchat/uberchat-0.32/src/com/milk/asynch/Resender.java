// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.asynch;

import java.util.Vector;

/**
 * A <code>Resender</code> is a terminus for sending which can be told to
 * resend to any number (0 included) of other <code>Senders</code>. It's
 * both a <code>Sender</code> and a <code>SendSource</code>, but it's not
 * actually a <code>SendFilter</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class Resender
implements Sender, SendSource
{
    /** the list of targets to resend to; null indicates that we were
     * told to stop sending */
    private Vector myTargets;

    /** the (intentionally-private) object to do all synchronization on */
    private Object mySynch;

    /**
     * Construct a <code>Resender</code>, which initially doesn't resend to
     * anything. 
     */
    public Resender ()
    {
	myTargets = new Vector ();
	mySynch = new Object ();
    }

    /**
     * Tell this <code>Resender</code> to automatically send anything it
     * receives to the given sender.
     *
     * @param sender the <code>Sender</code> to autosend to 
     */
    public void sendTo (Sender sender)
    {
	synchronized (mySynch)
	{
	    if (myTargets == null)
	    {
		throw new SenderCutException (this);
	    }

	    myTargets.addElement (sender);
	}
    }

    /**
     * Tell this <code>Resender</code> to no longer automatically send to
     * the given sender.
     *
     * @param sender the <code>Sender</code> to stop autosending to 
     */
    public void dontSendTo (Sender sender)
    {
	synchronized (mySynch)
	{
	    if (myTargets != null)
	    {
		myTargets.removeElement (sender);
	    }
	}
    }

    // ------------------------------------------------------------------------
    // SendSource interface methods

    /**
     * Tell this object to cease sending.
     */
    public void stopSending ()
    {
	synchronized (mySynch)
	{
	    myTargets = null;
	}
    }

    // ------------------------------------------------------------------------
    // Sender instance methods
   
    /**
     * Send a message, blocking if necessary.
     *
     * @param message the message to send
     */
    public void send (Object message)
    {
	synchronized (mySynch)
	{
	    if (myTargets == null)
	    {
		throw new SenderCutException (this);
	    }

	    int sz = myTargets.size ();
	    for (int i = 0; i < sz; i++)
	    {
		Sender s = (Sender) myTargets.elementAt (i);
		s.send (message);
	    }
	}
    }

    /**
     * Wait for this <code>Sender</code> to be empty.
     */
    public void waitUntilEmpty ()
    {
	synchronized (mySynch)
	{
	    if (myTargets == null)
	    {
		throw new SenderCutException (this);
	    }

	    int sz = myTargets.size ();
	    for (int i = 0; i < sz; i++)
	    {
		Sender s = (Sender) myTargets.elementAt (i);
		s.waitUntilEmpty ();
	    }
	}
    }
}
