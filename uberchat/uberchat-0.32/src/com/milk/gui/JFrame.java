// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This is a replacement for (subclass of, actually)
 * <code>javax.swing.JFrame</code>, that knows about iconification.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class JFrame
extends javax.swing.JFrame
{
    /** true if the frame is iconified */
    private boolean myIsIconified;

    /** the window listener */
    private MyWindowListener myWindowListener;

    /** the (intentionally-private) object we synch on */
    private Object mySynch;

    /**
     * Construct a <code>JFrame</code>.
     */
    public JFrame ()
    {
	setup ();
    }

    /**
     * Construct a <code>JFrame</code>.
     *
     * @param title the title for the frame
     */
    public JFrame (String title)
    {
	super (title);
	setup ();
    }

    /**
     * Return true if the frame is iconified. Note that a frame must
     * be showing to be iconified.
     *
     * @return true if the frame is iconified
     */
    public boolean isIconified ()
    {
	synchronized (mySynch)
	{
	    return myIsIconified;
	}
    }

    /**
     * Override of <code>JFrame.show ()</code> to deiconify the frame
     * if it is iconified.
     *
     * @deprecated Use <code>setVisible (true)</code> instead.
     */
    public void show ()
    {
	if (isIconified ())
	{
	    // unfortunately, this is the only way I could figure to get it
	    // to work; it probably causes the window manager to look a
	    // little psycho. Oh well. Java just sucks that way sometimes.
	    setVisible (false);
	}

	super.show ();
    }
 
    /**
     * Override of <code>JFrame.setVisible ()</code> to deiconify the frame
     * if it is iconified and the parameter is true.
     *
     * @param visibility the visibility
     */
    public void setVisible (boolean visibility)
    {
	if ((visibility == true) && isIconified ())
	{
	    // unfortunately, this is the only way I could figure to get it
	    // to work; it probably causes the window manager to look a
	    // little psycho. Oh well. Java just sucks that way sometimes.
	    setVisible (false);
	}

	super.setVisible (visibility);
    }

    /**
     * Deiconify the frame, but only if the frame is already showing (in an
     * iconified state).
     */
    public void deiconify ()
    {
	if (isIconified ())
	{
	    setVisible (true);
	}
    }
 
    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Do the instance variable setup.
     */
    public void setup ()
    {
	myIsIconified = false;
	myWindowListener = new MyWindowListener ();
	mySynch = myWindowListener;
	addWindowListener (myWindowListener);
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the listener that notices (de)iconifications.
     */
    private class MyWindowListener
    extends WindowAdapter
    {
	public void windowClosed (WindowEvent e)
	{
	    synchronized (mySynch)
	    {
		myIsIconified = false;
	    }
	}

	public void windowIconified (WindowEvent e)
	{
	    synchronized (mySynch)
	    {
		myIsIconified = true;
	    }
	}

	public void windowDeiconified (WindowEvent e)
	{
	    synchronized (mySynch)
	    {
		myIsIconified = false;
	    }
	}
    }
}
