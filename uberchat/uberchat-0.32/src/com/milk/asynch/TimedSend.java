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

import com.milk.util.Orderable;
import com.milk.util.PriorityQueue;
import com.milk.util.UnorderedException;

/**
 * <code>TimedSend</code> is the way to get sends to happen based on the
 * passage of time. A <code>TimedSend</code> represents exactly one send.
 * Scheduling it implicitly cancels previous schedulings.
 * BUG: redo this class to use <code>com.milk.timer.TimedRun</code>!
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class TimedSend
implements SendSource, Orderable
{
    /** the queue of timed sends, prioritized by time, most recent first */
    static private final PriorityQueue TheQueue;

    /** the thread that does all the dirty work */
    static private final Thread TheThread;

    /** the target for this send */
    private Sender myTarget;

    /** the message of this send */
    private Object myMessage;
    
    /** true if this is active, i.e., not cancelled */
    private boolean myIsActive;

    /** the scheduled time to send the message */
    private long myScheduledTime;

    /**
     * Construct a <code>TimedSend</code> which sends the given message to
     * the given target.
     *
     * @param target the target to send to
     * @param message the message to send 
     */
    public TimedSend (Sender target, Object message)
    {
	myTarget = target;
	myMessage = message;
	myIsActive = false;
	myScheduledTime = 0;
    }

    // ------------------------------------------------------------------------
    // Orderable interface methods

    /**
     * Return either -1, 0, or 1 to indicate that this object
     * is less than, equal to, or greater than, the given object, respectively.
     *
     * @param obj the object to compare to
     * @return the result of comparison
     * @exception UnorderedException thrown if this object isn't
     * ordered with respect to <code>obj</code>
     */
    public int compare (Object obj)
        throws UnorderedException
    {
	TimedSend ts;
	long time1;
	long time2;

	try
	{
	    ts = (TimedSend) obj;
	}
	catch (ClassCastException ex)
	{
	    throw new UnorderedException ("Unordered: " + this + ", " + obj);
	}

	synchronized (this)
	{
	    time1 = myScheduledTime;
	}

	synchronized (ts)
	{
	    time2 = ts.myScheduledTime;
	}

	if (time1 < time2)
	{
	    return -1;
	}

	if (time1 > time2)
	{
	    return 1;
	}

	return 0;
    }

    // ------------------------------------------------------------------------
    // SendSource interface methods

    /**
     * Tell this object to cease sending. In this case, this is the same
     * as <code>cancel()</code>.
     */
    public void stopSending ()
    {
	cancel ();
    }

    // ------------------------------------------------------------------------
    // TimedSend public methods

    /**
     * Change the target that this <code>TimedSend</code> sends to.
     *
     * @param target the new target
     */
    public synchronized void setTarget (Sender target)
    {
	myTarget = target;
    }

    /**
     * Change the message that this <code>TimedSend</code> sends.
     *
     * @param message the new message to send
     */
    public synchronized void setMessage (Object message)
    {
	myMessage = message;
    }

    /**
     * Cancel this <code>TimedSend</code>. This has no effect if it never
     * had a schedule in the first place, if it had already done its send,
     * or it had already been canceled. 
     */
    public void cancel ()
    {
	synchronized (this)
	{
	    if (! myIsActive)
	    {
		return;
	    }
	}

	synchronized (TheQueue)
	{
	    synchronized (this)
	    {
		TheQueue.remove (this);
		myIsActive = false;
	    }
	}
    }

    /**
     * Schedule or reschedule this <code>TimedSend</code> to happen the
     * given amount of time in the future.
     *
     * @param when when in the future (in msec) the send should happen 
     */
    public void schedule (long when)
    {
	if (when <= 0)
	{
	    throw new IllegalArgumentException ("bad when: " + when);
	}

	cancel ();

	synchronized (TheQueue)
	{
	    synchronized (this)
	    {
		myScheduledTime = System.currentTimeMillis () + when;
		myIsActive = true;
		TheQueue.insert (this);
		TheQueue.notifyAll ();
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Internal methods

    static
    {
	// set up our queue and thread
	TheQueue = new PriorityQueue ();
	TheThread = new Thread ()
	    {
		public void run ()
		{
		    threadRun ();
		}
	    };

	TheThread.setDaemon (true);
	TheThread.start ();
    }

    /**
     * This is the method that gets run in the thread to service
     * <code>TimedSend</code>s. 
     */
    private static void threadRun ()
    {
	for (;;)
	{
	    synchronized (TheQueue)
	    {
		if (TheQueue.size () == 0)
		{
		    try
		    {
			TheQueue.wait ();
		    }
		    catch (InterruptedException ex)
		    {
			// shouldn't happen
			System.err.println ("### shouldn't happen");
			ex.printStackTrace ();
		    }
		    continue;
		}

		TimedSend ts = (TimedSend) TheQueue.peekFirst ();
		long now = System.currentTimeMillis ();
		long waitFor = 0;

		synchronized (ts)
		{
		    if (! ts.myIsActive)
		    {
			// inactive--shouldn't do anything but remove it
			TheQueue.removeFirst ();
		    }
		    else if (ts.myScheduledTime <= now)
		    {
			// active and its time has come
			TheQueue.removeFirst ();

			try
			{
			    ts.myTarget.send (ts.myMessage);
			}
			catch (Exception ex)
			{
			    // exceptions are ignored
			    System.err.println ("### ignored");
			    ex.printStackTrace ();
			}
		    }
		    else
		    {
			// active but we should wait
			waitFor = ts.myScheduledTime - now;
		    }
		}

		if (waitFor != 0)
		{
		    // we were told to wait
		    try
		    {
			TheQueue.wait (waitFor);
		    }
		    catch (InterruptedException ex)
		    {
			// shouldn't happen
			System.err.println ("### shouldn't happen");
			ex.printStackTrace ();
		    }
		}
	    }
	}
    }
}
