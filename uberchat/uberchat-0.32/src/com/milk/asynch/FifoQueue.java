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
 * This class implements a FIFO queue for delivery of messages between
 * logical processes.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class FifoQueue
implements Rendezvous
{
    /** the <code>JustSender</code> for this object, if one exists at all */
    private JustSender mySender;

    /** the <code>JustReceiver</code> for this object, if one exists at all */
    private JustReceiver myReceiver;

    /** the current messages in the queue */
    private Vector myMessages;

    /**
     * Construct an initially-empty queue.
     */
    public FifoQueue ()
    {
	mySender = null;
	myReceiver = null;
	myMessages = new Vector ();
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
     * Send a message.
     *
     * @param message the message to put in the mailbox
     */
    public void send (Object message)
    {
	synchronized (myMessages)
	{
	    myMessages.addElement (message);
	    myMessages.notifyAll ();
	}
    }

    /**
     * Wait for this <code>Sender</code> to be empty.
     */
    public void waitUntilEmpty ()
    {
	synchronized (myMessages)
	{
	    for (;;)
	    {
		if (myMessages.size () == 0)
		{
		    break;
		}
		
		try
		{
		    myMessages.wait ();
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
     * However, wait no more than the given number of msec, and return
     * <code>null</code> if the timeout occurs before a message is
     * received. Specifying <code>0</code> means to wait forever.
     *
     * @param timeout maximum number of msec to wait for a message, or
     * <code>0</code> to never time out
     * @return null-ok; a message that was in the <code>Receiver</code>
     */
    public Object receive (long timeout)
    {
	Object result;
	boolean waited = false;

	synchronized (myMessages)
	{
	    for (;;)
	    {
		if (myMessages.size () == 0)
		{
		    if (waited && (timeout != 0))
		    {
			return null;
		    }

		    try
		    {
			myMessages.wait (timeout);
			waited = true;
		    }
		    catch (InterruptedException ex)
		    {
			// ignore it
		    }
		}
		else
		{
		    result = myMessages.firstElement ();
		    myMessages.removeElementAt (0);
		    break;
		}
	    }
	}

	return result;
    }

    /**
     * Retrieve a message out of this <code>Receiver</code>, but if the
     * <code>Receiver</code> is empty, first wait for it to be filled.
     *
     * @return non-null a message that was in the <code>Receiver</code>
     */
    public Object receive ()
    {
	return receive (0);
    }

    /**
     * Wait for this <code>Receiver</code> to have a message in it, but
     * don't actually retrieve the message. 
     */
    public void waitUntilFull ()
    {
	synchronized (myMessages)
	{
	    for (;;)
	    {
		if (myMessages.size () != 0)
		{
		    break;
		}
		
		try
		{
		    myMessages.wait ();
		}
		catch (InterruptedException ex)
		{
		    // ignore it
		}
	    }
	}
    }
}
