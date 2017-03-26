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

/**
 * This class implements a simple mailbox system for delivery of a single
 * message between logical processes (i.e., threads or whatnot).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public class MailBox
implements Rendezvous
{
    /** the <code>JustSender</code> for this object, if one exists at all */
    private JustSender mySender;

    /** the <code>JustReceiver</code> for this object, if one exists at all */
    private JustReceiver myReceiver;

    /** the current message in the mailbox */
    private Object myMessage;

    /** true if there is a message in the mailbox (needed so you can
     * send null if you want to) */
    private boolean myIsFull;

    /** the (intentionally-private) object to do all synchronization on */
    private Object mySynch;

    /**
     * Construct an initially-empty mailbox.
     */
    public MailBox ()
    {
	mySender = null;
	myReceiver = null;
	myMessage = null;
	myIsFull = false;
	mySynch = new Object ();
    }

    // ------------------------------------------------------------------------
    // Rendezvous interface methods

    /**
     * Get a <code>JustSender</code> for this <code>Rendezvous</code>.
     *
     * @return the JustSender
     */
    public JustSender getSender ()
    {
	if (mySender == null)
	{
	    mySender = new JustSender (this);
	}

	return mySender;
    }

    /**
     * Get a <code>JustReceiver</code> for this <code>Rendezvous</code>.
     *
     * @return the JustReceiver
     */
    public JustReceiver getReceiver ()
    {
	if (myReceiver == null)
	{
	    myReceiver = new JustReceiver (this);
	}

	return myReceiver;
    }

    // ------------------------------------------------------------------------
    // Sender interface methods

    /**
     * Place a message in the mailbox, but if the mailbox is already full,
     * first wait for it to be emptied.
     *
     * @param message the message to put in the mailbox
     */
    public void send (Object message)
    {
	synchronized (mySynch)
	{
	    for (;;)
	    {
		if (! myIsFull)
		{
		    myMessage = message;
		    myIsFull = true;
		    mySynch.notifyAll ();
		    break;
		}

		try
		{
		    mySynch.wait ();
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
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
	    for (;;)
	    {
		if (! myIsFull)
		{
		    break;
		}
		
		try
		{
		    mySynch.wait ();
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Receiver interface methods

    /**
     * Retrieve a message out of this <code>Receiver</code>, but if the
     * <code>Receiver</code> is empty, first wait for it to be filled.
     *
     * @return a message that was in the <code>Receiver</code>
     */
    public Object receive ()
    {
	Object result;

	synchronized (mySynch)
	{
	    for (;;)
	    {
		if (myIsFull)
		{
		    result = myMessage;
		    myMessage = null;
		    myIsFull = false;
		    mySynch.notifyAll ();
		    break;
		}
		
		try
		{
		    mySynch.wait ();
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }
	}

	return result;
    }

    /**
     * Wait for this <code>Receiver</code> to have a message in it, but
     * don't actually retrieve the message. 
     */
    public void waitUntilFull ()
    {
	synchronized (mySynch)
	{
	    for (;;)
	    {
		if (myIsFull)
		{
		    break;
		}
		
		try
		{
		    mySynch.wait ();
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }
	}
    }
}
