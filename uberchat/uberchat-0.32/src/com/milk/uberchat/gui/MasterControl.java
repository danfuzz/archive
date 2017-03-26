// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.gui;

import com.milk.objed.gui.EditorControl;
import com.milk.uberchat.LocusTracker;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.ErrorListener;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.event.MessageListener;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatSystem;
import com.milk.util.ListenerList;
import com.milk.util.ShouldntHappenException;
import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * This is the master control object for UberChat.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class MasterControl
implements ErrorListener, MessageListener
{
    /** max message count for logs. BUG--should be a user pref. */
    private static final int LOCUS_LOG_SIZE = 200;

    /** the list of active systems */
    private Vector mySystems;

    /** the table mapping all loci to trackers */
    private Hashtable myTrackers;

    /** the unique identities panel */
    private IdentitiesPanel myIdentitiesPanel;

    /** the StyleContext to use for all chat windows */
    private StyleContext myStyles;

    /** the <code>EditorControl</code> object to use */
    private EditorControl myEditorControl;

    /**
     * Construct a <code>MasterControl</code> object.
     */
    public MasterControl ()
    {
	mySystems = new Vector ();
	myTrackers = new Hashtable ();
	myIdentitiesPanel = null;
	makeStyles ();
	myEditorControl = new EditorControl ();
    }

    /**
     * Set the identities panel to use.
     *
     * @param panel the panel to use
     */
    public void setIdentitiesPanel (IdentitiesPanel panel)
    {
	myIdentitiesPanel = panel;
    }

    /**
     * Get the StyleContext to use for all chat windows.
     *
     * @return the StyleContext
     */
    public StyleContext getChatStyles ()
    {
	return myStyles;
    }

    /**
     * Get the <code>EditorControl</code> object to use.
     *
     * @return the <code>EditorControl</code> object
     */
    public EditorControl getEditorControl ()
    {
	return myEditorControl;
    }

    /**
     * Add a new system into the world.
     * 
     * @param sys the new system to add
     */
    public void newSystem (ChatSystem sys)
    {
	synchronized (this)
	{
	    mySystems.addElement (sys);
	    myIdentitiesPanel.systemCreated (sys);
	    sys.addUberListener (this);
	}
    }

    /**
     * Get the <code>LocusTracker</code> for the given locus.
     *
     * @param locus the locus to look up
     * @return the tracker for that locus
     */
    public LocusTracker getLocusTracker (ChatLocus locus)
    {
	LocusTracker t = (LocusTracker) myTrackers.get (locus);

	if (t == null)
	{
	    t = new LocusTracker (locus, LOCUS_LOG_SIZE);
	    myTrackers.put (locus, t);
	}

	return t;
    }

    // ------------------------------------------------------------------------
    // MessageListener interface methods

    /**
     * This is called when a user broadcasts a message in a locus.
     *
     * @param event the event commemorating the moment
     */
    public void userBroadcast (MessageEvent event)
    {
	handleMessageEvent (event);
    }

    /**
     * This is called when the host system broadcasts a message in a locus.
     *
     * @param event the event commemorating the moment 
     */
    public void systemBroadcast (MessageEvent event)
    {
	handleMessageEvent (event);
    }

    /**
     * This is called when the host system sends a private message
     * to this client in a locus.
     *
     * @param event the event commemorating the moment 
     */
    public void systemPrivate (MessageEvent event)
    {
	handleMessageEvent (event);
    }

    // ------------------------------------------------------------------------
    // ErrorListener interface methods

    /**
     * Show an error to the user.
     *
     * @param event the event to show
     */
    public void errorReport (ErrorEvent event)
    {
	JOptionPane.showMessageDialog (null,
				       event.getMessage (),
				       "UberChat Error",
				       JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a bug to the user.
     *
     * @param event the event to show
     */
    public void bugReport (ErrorEvent event)
    {
	System.err.println ("--------------------- BUG DETECTED");
	System.err.println ("Source: " + event.getSource ());
	event.getException ().printStackTrace ();
	System.err.println ("(At least I admit as much, eh?)");
	System.err.println ("--------------------- END BUG DETECTION REPORT");
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Handle a <code>MessageEvent</code> by doing the appropriate
     * dispatching.
     *
     * @param event the event in question
     */
    private void handleMessageEvent (MessageEvent event)
    {
	ChatLocus loc = event.getLocus ();

	if (loc == null)
	{
	    throw new ShouldntHappenException (
                "Got MessageEvent with null locus:\n" + event);
	}

	synchronized (myTrackers)
	{
	    LocusTracker t = (LocusTracker) myTrackers.get (loc);

	    if (t == null)
	    {
		t = new LocusTracker (loc, LOCUS_LOG_SIZE);
		myTrackers.put (loc, t);
	    }

	    if (! t.hasListeners ())
	    {
		LocusPanel.makeFrame (this, loc);
	    }

	    t.addMessage (event);
	}
    }

    /**
     * Helper method to create the <code>myStyles</code> object.
     */
    private void makeStyles ()
    {
	// BUG--this should be based on user prefs
	Style s;
	myStyles = new StyleContext ();

	Style normal = myStyles.addStyle ("normal", null);
	StyleConstants.setFontFamily (normal, "DialogInput");
	StyleConstants.setFontSize (normal, 10);
	StyleConstants.setSpaceAbove (normal, 0.0F);
	StyleConstants.setSpaceBelow (normal, 0.0F);

	s = myStyles.addStyle ("speech", normal);
	StyleConstants.setLeftIndent (s, 20.0F);
	StyleConstants.setFirstLineIndent (s, -20.0F);

	s = myStyles.addStyle ("speaker", s);
	StyleConstants.setBold (s, true);
	StyleConstants.setForeground (s, new Color (0x0000c0));

	s = myStyles.addStyle ("system-private", normal);
	StyleConstants.setLeftIndent (s, 20.0F);
	StyleConstants.setFirstLineIndent (s, -20.0F);
	StyleConstants.setForeground (s, new Color (0xc00000));

	s = myStyles.addStyle ("system-broadcast", s);
	StyleConstants.setBold (s, true);

	s = myStyles.addStyle ("other", normal);
	StyleConstants.setForeground (s, new Color (0x008000));
    }
}
