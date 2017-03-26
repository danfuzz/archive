// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed;

import com.milk.timer.TimedRun;
import java.util.Vector;

/**
 * An <code>AutoUpdater</code> is used to cause a set of editors to be told
 * to update themselves at a regular basis.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class AutoUpdater
{
    /** the list of editors to update */
    private Vector myEditors;
    
    /** the length of the editors list */
    private int myLength;

    /** the frequency (in msec) to update, or 0 to stop */
    private int myFrequency;

    /** the <code>TimedRun</code> used to fire the update events */
    private TimedRun myTimedRun;

    /** true if the <code>TimedRun</code> is currently scheduled */
    private boolean myIsScheduled;

    /**
     * Construct a new <code>AutoUpdater</code>, initially-empty and
     * stopped.
     */
    public AutoUpdater ()
    {
	myEditors = new Vector ();
	myLength = 0;
	myFrequency = 0;
	myTimedRun = new TimedRun (new UpdateRunnable ());
	myIsScheduled = false;
    }

    /**
     * Add an editor to update.
     *
     * @param editor the editor to add
     */
    public void add (Editor editor)
    {
	synchronized (myEditors)
	{
	    myEditors.addElement (editor);
	    myLength++;
	    schedule ();
	}
    }

    /**
     * Remove an editor that was previously added with
     * <code>addEditor()</code>.
     *
     * @param editor the editor to remove
     */
    public void remove (Editor editor)
    {
	synchronized (myEditors)
	{
	    myEditors.removeElement (editor);
	    myLength = myEditors.size ();
	    schedule ();
	}
    }

    /**
     * Set the frequency of updating. A value of 0 means to stop
     * updating.
     *
     * @param frequency >= 0; the frequency (in msec) to update at
     */
    public void setFrequency (int frequency)
    {
	if (frequency < 0)
	{
	    throw new IllegalArgumentException (
                "Bad value for frequency: " + frequency);
	}

	synchronized (myEditors)
	{
	    myFrequency = frequency;
	    schedule ();
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Schedule or cancel the runnable, as appropriate.
     */
    private void schedule ()
    {
	if ((myFrequency == 0) || (myLength == 0))
	{
	    if (myIsScheduled)
	    {
		myTimedRun.cancel ();
		myIsScheduled = false;
	    }
	}
	else
	{
	    if (! myIsScheduled)
	    {
		myTimedRun.schedule (myFrequency);
		myIsScheduled = true;
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This class is used as the <code>Runnable</code> which fires off
     * all the update requests.
     */
    private class UpdateRunnable
    implements Runnable
    {
	/**
	 * Just go through the list of editors, calling <code>update()</code>
	 * on each, then reschedule the run if needed.
	 */
	public void run ()
	{
	    synchronized (myEditors)
	    {
		for (int i = 0; i < myLength; i++)
		{
		    ((Editor) myEditors.elementAt (i)).update ();
		}

		myIsScheduled = false;
		schedule ();
	    }
	}
    }
}
