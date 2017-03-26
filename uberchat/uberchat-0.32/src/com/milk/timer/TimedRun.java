// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.timer;

import com.milk.util.Orderable;
import com.milk.util.PriorityQueue;
import com.milk.util.UnorderedException;

/**
 * <code>TimedRun</code> is the way to get something to run based on the
 * passage of time. A <code>TimedRun</code> will run its
 * <code>Runnable</code> at most once per scheduling. If it hasn't yet run
 * and it gets rescheduled, then the previous scheduling is implicitly
 * canceled. Note that, in general, the <code>TimedRun</code> system will
 * ignore all exceptions caused by running the <code>Runnable</code>s that
 * it is handed. The <code>Runnable</code>s are responsible for handling
 * their own exceptions. However, with debugging turned on, such ignored
 * exceptions <i>do</i> get spit to <code>System.err</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class TimedRun
implements Orderable
{
    /** the queue of <code>TimedRun</code>s, prioritized by time, most
     * recent first */
    static private final PriorityQueue TheQueue;

    /** the thread that does all the dirty work */
    static private final Thread TheThread;

    /** the system debugging flag */
    static private boolean TheDebug = false;

    /** the <code>Runnable</code> to run */
    private Runnable myRunnable;

    /** true if this is active, i.e., not cancelled */
    private boolean myIsActive;

    /** the scheduled time to run */
    private long myScheduledTime;

    /** the debugging flag for the individual runnable */
    private boolean myDebug;

    /**
     * Construct a <code>TimedRun</code> which runs the given
     * <code>Runnable</code>.
     *
     * @param runnable the thing to run
     */
    public TimedRun (Runnable runnable)
    {
	myRunnable = runnable;
	myIsActive = false;
	myScheduledTime = 0;
	myDebug = false;
    }

    /**
     * Set the debugging flag for the whole <code>TimedRun</code> system.
     * With debug set to true, the system will spit out interesting stuff
     * to <code>System.err</code>.
     *
     * @param debug the debug flag
     */
    public static void setSystemDebug (boolean debug)
    {
	TheDebug = debug;
    }

    // ------------------------------------------------------------------------
    // Public methods

    /**
     * Set the debugging flag for this object. With debug set to true, the
     * system will spit out interesting stuff to <code>System.err</code>
     * having to do with this object.
     *
     * @param debug the debug flag 
     */
    public void setDebug (boolean debug)
    {
	myDebug = debug;
    }

    /**
     * Change the <code>Runnable</code> that this <code>TimedRun</code>
     * runs.
     *
     * @param runnable the new thing to run 
     */
    public synchronized void setRunnable (Runnable runnable)
    {
	myRunnable = runnable;
    }

    /**
     * Cancel this <code>TimedRun</code>. This has no effect if it never
     * had a schedule in the first place, if it had already done its run,
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
     * Schedule or reschedule this <code>TimedRun</code> to happen the
     * given amount of time in the future.
     *
     * @param when when in the future (in msec) the run should happen 
     */
    public void schedule (long when)
    {
	if (when <= 0)
	{
	    throw new IllegalArgumentException ("Bad value for when: " + when);
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
	TimedRun tr;
	long time1;
	long time2;

	try
	{
	    tr = (TimedRun) obj;
	}
	catch (ClassCastException ex)
	{
	    throw new UnorderedException (this, obj);
	}

	synchronized (this)
	{
	    time1 = myScheduledTime;
	}

	synchronized (tr)
	{
	    time2 = tr.myScheduledTime;
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
    // Private helper methods

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
     * <code>TimedRun</code>s. 
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
			// shouldn't happen; ignore it
			if (TheDebug)
			{
			    System.err.println (
                                "Shouldn't happen: TimedRun thread got " +
				"interrupted:");
			    ex.printStackTrace ();
			}
		    }
		    continue;
		}

		TimedRun tr = (TimedRun) TheQueue.peekFirst ();
		long now = System.currentTimeMillis ();
		long waitFor = 0;

		synchronized (tr)
		{
		    if (! tr.myIsActive)
		    {
			// inactive--shouldn't do anything but remove it
			TheQueue.removeFirst ();
		    }
		    else if (tr.myScheduledTime <= now)
		    {
			// active and its time has come
			TheQueue.removeFirst ();

			try
			{
			    tr.myRunnable.run ();
			}
			catch (Exception ex)
			{
			    // exceptions are ignored
			    if (TheDebug || tr.myDebug)
			    {
				System.err.println (tr + " got an exception:");
				ex.printStackTrace ();
			    }
			}
		    }
		    else
		    {
			// active but we should wait
			waitFor = tr.myScheduledTime - now;
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
			// shouldn't happen; ignore it
			if (TheDebug)
			{
			    System.err.println (
                                "Shouldn't happen: TimedRun thread got " +
				"interrupted:");
			    ex.printStackTrace ();
			}
		    }
		}
	    }
	}
    }
}
